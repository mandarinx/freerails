/*
 * FreeRails
 * Copyright (C) 2000-2018 The FreeRails Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 *
 */
package freerails.controller;

import freerails.client.ModelRoot;
import freerails.client.ModelRootImpl;
import freerails.model.track.OccupiedTracks;
import freerails.model.train.motion.TrainMotion;
import freerails.model.train.schedule.UnmodifiableSchedule;
import freerails.model.train.schedule.TrainOrder;
import freerails.move.*;
import freerails.move.generator.AddTrainMoveGenerator;
import freerails.move.generator.MoveTrainMoveGenerator;
import freerails.model.MapFixtureFactory2;

import freerails.util.Vec2D;
import freerails.model.terrain.TileTransition;
import freerails.model.player.Player;
import freerails.model.train.*;
import freerails.model.train.motion.Motion;
import freerails.model.train.schedule.Schedule;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test for MoveTrainPreMove, tests moving round a loop of track.
 */
public class MoveTrainMoveGenerator1StTest extends AbstractMoveTestCase {

    private Player player;
    private int validEngineId;

    /**
     *
     */
    @Override
    protected void setupWorld() {
        world = MapFixtureFactory2.getCopy();
        validEngineId = world.getEngines().iterator().next().getId();
        MoveExecutor moveExecutor = new SimpleMoveExecutor(world, world.getPlayer(0));
        player = moveExecutor.getPlayer();
        ModelRoot modelRoot = new ModelRootImpl();
        TrackMoveProducer trackBuilder = new TrackMoveProducer(moveExecutor, world, modelRoot);
        StationBuilder stationBuilder = new StationBuilder(moveExecutor);

        // Build track.
        stationBuilder.setStationType(stationBuilder.getTrackTypeID("terminal"));
        TileTransition[] track = {TileTransition.EAST, TileTransition.EAST, TileTransition.EAST, TileTransition.EAST, TileTransition.EAST, TileTransition.EAST, TileTransition.EAST, TileTransition.EAST, TileTransition.EAST};
        Vec2D stationA = new Vec2D(10, 10);
        Status ms0 = trackBuilder.buildTrack(stationA, track);
        assertTrue(ms0.succeeds());

        // Build 2 stations.
        Status ms1 = stationBuilder.buildStation(stationA);
        assertTrue(ms1.succeeds());
        Vec2D stationB = new Vec2D(19, 10);
        Status ms2 = stationBuilder.buildStation(stationB);
        assertTrue(ms2.succeeds());

        TrainOrder order0 = new TrainOrder(1, null, false, false);
        TrainOrder order1 = new TrainOrder(0, null, false, false);
        Schedule schedule = new Schedule();
        schedule.addOrder(order0);
        schedule.addOrder(order1);

        Vec2D start = new Vec2D(10, 10);
        AddTrainMoveGenerator preMove = new AddTrainMoveGenerator(validEngineId, Arrays.asList(0, 0), start, player, schedule);
        Move move = preMove.generate(world);
        Status status = move.doMove(world, player);
        assertTrue(status.succeeds());
    }

    /**
     *
     */
    public void testNextVector() {
        MoveTrainMoveGenerator preMove = new MoveTrainMoveGenerator(0, player, new OccupiedTracks(player, world));
        TileTransition actual = preMove.nextStep(world);
        assertNotNull(actual);
        // The train is at station A, so should head east to station B.
        assertEquals(TileTransition.EAST, actual);
    }

    /**
     *
     */
    public void testNextSpeeds() {
        MoveTrainMoveGenerator preMove = new MoveTrainMoveGenerator(0, player, new OccupiedTracks(player, world));
        Motion speeds = preMove.nextSpeeds(world, TileTransition.EAST);
        assertNotNull(speeds);
        assertEquals(speeds.calculateSpeedAtTime(0), 0.0d);
        assertTrue(speeds.getTotalDistance() >= TileTransition.EAST.getLength());
        double t = speeds.getTotalTime();
        assertTrue(t > 0);
        assertTrue(speeds.calculateSpeedAtTime(t) > 0);
    }

    /**
     *
     */
    public void testMove() {
        MoveTrainMoveGenerator preMove = new MoveTrainMoveGenerator(0, player, new OccupiedTracks(player, world));
        Move move = preMove.generate(world);
        assertNotNull(move);
        assertSurvivesSerialisation(move);
    }

    /**
     *
     */
    public void testMove2() {
        Status status;
        Move move;
        setupLoopOfTrack();

        TrainAccessor ta = new TrainAccessor(world, player, 0);
        TrainMotion trainMotion = ta.findCurrentMotion(3);

        assertEquals(0.0d, trainMotion.duration());

        PathOnTiles expected = new PathOnTiles(new Vec2D(5, 5), TileTransition.SOUTH_WEST);
        assertEquals(expected, trainMotion.getPath());
        PositionOnTrack positionOnTrack = trainMotion.getFinalPosition();
        int x = positionOnTrack.getLocation().x;
        assertEquals(4, x);
        int y = positionOnTrack.getLocation().y;
        assertEquals(6, y);
        assertEquals(TileTransition.SOUTH_WEST, positionOnTrack.facing());

        MoveTrainMoveGenerator moveTrain = new MoveTrainMoveGenerator(0, player,
                new OccupiedTracks(player, world));

        assertEquals(TileTransition.NORTH_EAST, moveTrain.nextStep(world));

        move = moveTrain.generate(world);
        status = move.doMove(world, player);
        assertTrue(status.succeeds());

        TrainMotion tm2 = ta.findCurrentMotion(3);
        assertFalse(trainMotion.equals(tm2));

        expected = new PathOnTiles(new Vec2D(5, 5), TileTransition.SOUTH_WEST, TileTransition.NORTH_EAST);
        assertEquals(expected, tm2.getPath());

        assertTrue(tm2.duration() > 3.0d);
        // The expected value is 3.481641930846211, found from
        // stepping thu code in debugger.
        assertTrackHere(tm2.getTiles(tm2.duration()));

        positionOnTrack = tm2.getFinalPosition();
        assertEquals(4, x);
        assertEquals(6, y);
        // assertEquals(SOUTH, pot.facing());

        assertTrackHere(x, y);

        assertEquals(TileTransition.EAST, moveTrain.nextStep(world));

        MoveTrainMoveGenerator2NdTest.incrTime(world, player);
        move = moveTrain.generate(world);
        status = move.doMove(world, player);
        assertTrue(status.succeeds());

        TrainMotion tm3 = ta.findCurrentMotion(100);
        assertFalse(tm3.equals(tm2));
        expected = new PathOnTiles(new Vec2D(4, 6), TileTransition.NORTH_EAST, TileTransition.EAST);
        assertEquals(expected, tm3.getPath());

        assertTrackHere(tm3.getTiles(tm3.duration()));
        assertTrackHere(tm3.getTiles(tm3.duration() / 2));
        assertTrackHere(tm3.getTiles(0));
        assertTrackHere(tm3.getPath());

        assertEquals(TileTransition.SOUTH_EAST, moveTrain.nextStep(world));

        MoveTrainMoveGenerator2NdTest.incrTime(world, player);
        move = moveTrain.generate(world);

        status = move.doMove(world, player);
        assertTrue(status.succeeds());
    }

    private void setupLoopOfTrack() {
        world = MapFixtureFactory2.getCopy();
        MoveExecutor moveExecutor = new SimpleMoveExecutor(world, world.getPlayer(0));
        player = moveExecutor.getPlayer();
        ModelRoot modelRoot = new ModelRootImpl();
        TrackMoveProducer producer = new TrackMoveProducer(moveExecutor, world, modelRoot);
        TileTransition[] trackPath = {TileTransition.EAST, TileTransition.SOUTH_EAST, TileTransition.SOUTH, TileTransition.SOUTH_WEST, TileTransition.WEST,
                TileTransition.NORTH_WEST, TileTransition.NORTH, TileTransition.NORTH_EAST};
        Vec2D from = new Vec2D(5, 5);
        Status status = producer.buildTrack(from, trackPath);
        assertTrue(status.succeeds());

        TrainOrder[] orders = {};
        UnmodifiableSchedule schedule = new Schedule(orders, -1, false);
        AddTrainMoveGenerator addTrain = new AddTrainMoveGenerator(validEngineId, new ArrayList<>(), from, player, schedule);

        Move move = addTrain.generate(world);
        status = move.doMove(world, player);
        assertTrue(status.succeeds());
        TrainAccessor ta = new TrainAccessor(world, player, 0);
        TrainMotion motion = ta.findCurrentMotion(0);
        assertNotNull(motion);

        PathOnTiles expected = new PathOnTiles(from, TileTransition.SOUTH_WEST);
        PathOnTiles actual = motion.getTiles(motion.duration());
        assertEquals(expected, actual);
    }

    /**
     *
     */
    public void testMovingRoundLoop() {
        setupLoopOfTrack();

        MoveTrainMoveGenerator moveTrain = new MoveTrainMoveGenerator(0, player, new OccupiedTracks(player, world));
        Move move = moveTrain.generate(world);
        assertTrue(move.doMove(world, player).succeeds());
    }

    /**
     *
     */
    public void testGetTiles() {
        setupLoopOfTrack();

        MoveTrainMoveGenerator moveTrain = new MoveTrainMoveGenerator(0, player, new OccupiedTracks(player, world));
        Move move = moveTrain.generate(world);
        assertTrue(move.doMove(world, player).succeeds());

        TrainAccessor trainAccessor = new TrainAccessor(world, player, 0);
        TrainMotion motion = trainAccessor.findCurrentMotion(1);
        double duration = motion.duration();
        assertTrue(duration > 1);
        int trainLength = motion.getTrainLength();
        for (int i = 0; i < 10; i++) {
            double t = i == 0 ? 0 : duration * i / 10;
            PathOnTiles tiles = motion.getTiles(t);
            assertTrue("t=" + t, tiles.steps() > 0);

            assertTrue("t=" + t, tiles.getTotalDistance() >= trainLength);
        }
    }

    /**
     *
     */
    public void testFindNextVector() {
        setupLoopOfTrack();
        PositionOnTrack positionOnTrack = PositionOnTrack.createFacing(new Vec2D(4, 6), TileTransition.SOUTH_WEST);

        Vec2D target = new Vec2D();
        TileTransition expected = TileTransition.NORTH_EAST;
        assertEquals(expected, MoveTrainMoveGenerator.findNextStep(world, positionOnTrack, target));
        positionOnTrack.move(expected);
        expected = TileTransition.EAST;
        assertEquals(expected, MoveTrainMoveGenerator.findNextStep(world, positionOnTrack, target));
        positionOnTrack.move(expected);

        expected = TileTransition.SOUTH_EAST;
        assertEquals(expected, MoveTrainMoveGenerator.findNextStep(world, positionOnTrack, target));
        positionOnTrack.move(expected);

        expected = TileTransition.SOUTH;
        assertEquals(expected, MoveTrainMoveGenerator.findNextStep(world, positionOnTrack, target));
        positionOnTrack.move(expected);
    }

}