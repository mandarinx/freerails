/*
 * Created on 18-Feb-2005
 *
 */
package jfreerails.controller;

import static jfreerails.world.common.Step.EAST;
import static jfreerails.world.common.Step.NORTH;
import static jfreerails.world.common.Step.NORTH_EAST;
import static jfreerails.world.common.Step.NORTH_WEST;
import static jfreerails.world.common.Step.SOUTH;
import static jfreerails.world.common.Step.SOUTH_EAST;
import static jfreerails.world.common.Step.SOUTH_WEST;
import static jfreerails.world.common.Step.WEST;
import jfreerails.move.AbstractMoveTestCase;
import jfreerails.move.Move;
import jfreerails.move.MoveStatus;
import jfreerails.server.MapFixtureFactory2;
import jfreerails.world.common.ImInts;
import jfreerails.world.common.ImPoint;
import jfreerails.world.common.PositionOnTrack;
import jfreerails.world.common.Step;
import jfreerails.world.player.FreerailsPrincipal;
import jfreerails.world.player.Player;
import jfreerails.world.top.AKEY;
import jfreerails.world.top.ActivityIterator;
import jfreerails.world.train.ImmutableSchedule;
import jfreerails.world.train.MutableSchedule;
import jfreerails.world.train.PathOnTiles;
import jfreerails.world.train.TrainMotion;
import jfreerails.world.train.TrainOrdersModel;

/**
 * Junit test for AddTrainPreMove.
 * 
 * @author Luke
 * 
 */
public class AddTrainPreMoveTest extends AbstractMoveTestCase {

	TrackMoveProducer trackBuilder;

	StationBuilder stationBuilder;

	FreerailsPrincipal principal;

	private ImPoint stationA;

	private ImPoint stationB;

	ImmutableSchedule defaultSchedule;

	protected void setupWorld() {
		world = MapFixtureFactory2.getCopy();
		MoveExecutor me = new SimpleMoveExecutor(world, 0);
		principal = me.getPrincipal();
		trackBuilder = new TrackMoveProducer(me, world);
		stationBuilder = new StationBuilder(me);

		// Build track.
		stationBuilder
				.setStationType(stationBuilder.getTrackTypeID("terminal"));
		Step[] track = { EAST, EAST, EAST, EAST, EAST, EAST, EAST, EAST, EAST };
		stationA = new ImPoint(10, 10);
		MoveStatus ms0 = trackBuilder.buildTrack(stationA, track);
		assertTrue(ms0.ok);

		// Build 2 stations.
		MoveStatus ms1 = stationBuilder.buildStation(stationA);
		assertTrue(ms1.ok);
		stationB = new ImPoint(19, 10);
		MoveStatus ms2 = stationBuilder.buildStation(stationB);
		assertTrue(ms2.ok);

		TrainOrdersModel order0 = new TrainOrdersModel(0, null, false, false);
		TrainOrdersModel order1 = new TrainOrdersModel(1, null, false, false);
		MutableSchedule s = new MutableSchedule();
		s.addOrder(order0);
		s.addOrder(order1);
		defaultSchedule = s.toImmutableSchedule();

	}

	public void testMove() {
		AddTrainPreMove preMove = new AddTrainPreMove(0, new ImInts(0, 0),
				stationA, principal, defaultSchedule);
		Move m = preMove.generateMove(world);
		assertDoMoveIsOk(m);

		assertUndoMoveIsOk(m);

		assertSurvivesSerialisation(m);
	}

	/**
	 * Check that the path on tiles created for the new train is actually on the
	 * track.
	 */
	public void testPathOnTiles() {
		AddTrainPreMove preMove = new AddTrainPreMove(0, new ImInts(0, 0),
				stationA, principal, defaultSchedule);
		Move m = preMove.generateMove(world);
		MoveStatus ms = m.doMove(world, Player.AUTHORITATIVE);
		assertTrue(ms.ok);

		TrainAccessor ta = new TrainAccessor(world, principal, 0);
		TrainMotion motion = ta.findCurrentMotion(0);
		assertNotNull(motion);
		PathOnTiles path = motion.getTiles(motion.duration());
		assertTrackHere(path);

	}

	public void testMove2() {
		AddTrainPreMove preMove = new AddTrainPreMove(0, new ImInts(0, 0),
				stationA, principal, defaultSchedule);
		Move m = preMove.generateMove(world);
		MoveStatus ms = m.doMove(world, Player.AUTHORITATIVE);
		assertTrue(ms.ok);
		ActivityIterator ai = world.getActivities(AKEY.TRAIN_POSITIONS, 0,
				principal);
		TrainMotion tm = (TrainMotion) ai.getActivity();
		assertEquals(0d, tm.duration());
		assertEquals(0d, tm.getSpeedAtEnd());
		assertEquals(0d, tm.getDistance(100));
		assertEquals(0d, tm.getDistance(0));
		PositionOnTrack pot = tm.getFinalPosition();
		assertNotNull(pot);
		assertEquals(EAST, pot.facing());
		assertEquals(13, pot.getX());
		assertEquals(10, pot.getY());

	}

	public void testGetSchedule() {
		world = MapFixtureFactory2.getCopy();
		MoveExecutor me = new SimpleMoveExecutor(world, 0);
		principal = me.getPrincipal();
		TrackMoveProducer producer = new TrackMoveProducer(me, world);
		Step[] trackPath = { EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST,
				NORTH_WEST, NORTH, NORTH_EAST };
		ImPoint from = new ImPoint(5, 5);
		MoveStatus ms = producer.buildTrack(from, trackPath);
		if (!ms.ok)
			throw new IllegalStateException(ms.message);

		TrainOrdersModel[] orders = {};
		ImmutableSchedule is = new ImmutableSchedule(orders, -1, false);
		AddTrainPreMove addTrain = new AddTrainPreMove(0, new ImInts(), from,
				principal, is);
		Move m = addTrain.generateMove(world);
		ms = m.doMove(world, principal);
		if (!ms.ok)
			throw new IllegalStateException(ms.message);

		TrainAccessor ta = new TrainAccessor(world, principal, 0);
		assertNotNull(ta.getTarget());

	}

}
