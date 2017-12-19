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
 * Created on Sep 3, 2004
 *
 */
package freerails.controller;

import freerails.move.ChangeTrackPieceCompositeMove;
import freerails.move.MoveStatus;
import freerails.world.common.ImPoint;
import freerails.world.common.PositionOnTrack;
import freerails.world.common.Step;
import freerails.world.player.FreerailsPrincipal;
import freerails.world.player.Player;
import freerails.world.terrain.TerrainType;
import freerails.world.terrain.TileTypeImpl;
import freerails.world.top.*;
import freerails.world.track.FreerailsTile;
import freerails.world.track.TrackRule;
import junit.framework.TestCase;

/**
 * JUnit test for BuildTrackExplorer.
 *
 */
public class BuildTrackExplorerTest extends TestCase {
    private final Player testPlayer = new Player("test", 0);
    private WorldImpl world;
    private FreerailsPrincipal principle;

    /**
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        world = new WorldImpl(20, 20);
        world.addPlayer(testPlayer);
        world.set(ITEM.GAME_RULES, GameRules.NO_RESTRICTIONS);
        principle = testPlayer.getPrincipal();
        MapFixtureFactory.generateTrackRuleList(world);
    }

    /**
     * On a blank map, we should be able to build track in any direction as long
     * as it does not go off the map.
     */
    public void test1() {
        PositionOnTrack start;

        // Test starting in the middle of the map.
        start = PositionOnTrack.createComingFrom(10, 10, Step.NORTH);

        BuildTrackExplorer explorer = new BuildTrackExplorer(world, principle);
        explorer.setPosition(start.toInt());
        assertNextVertexIs(Step.NORTH, 10, 9, explorer);
        assertNextVertexIs(Step.NORTH_EAST, 11, 9, explorer);
        assertNextVertexIs(Step.EAST, 11, 10, explorer);
        // We miss out SW, S, and SE since we don't want to double back on
        // ourselves.
        assertNextVertexIs(Step.WEST, 9, 10, explorer);
        assertNextVertexIs(Step.NORTH_WEST, 9, 9, explorer);
        assertFalse(explorer.hasNextEdge());

        // Test starting in the top left of the map.
        start = PositionOnTrack.createComingFrom(0, 0, Step.SOUTH_EAST);
        explorer.setPosition(start.toInt());
        assertNextVertexIs(Step.EAST, 1, 0, explorer);
        assertNextVertexIs(Step.SOUTH_EAST, 1, 1, explorer);
        assertNextVertexIs(Step.SOUTH, 0, 1, explorer);
        assertFalse(explorer.hasNextEdge());

        // Test starting in the bottom right of the map.
        start = PositionOnTrack.createComingFrom(19, 19, Step.NORTH_WEST);
        explorer.setPosition(start.toInt());
        assertNextVertexIs(Step.NORTH, 19, 18, explorer);
        assertNextVertexIs(Step.WEST, 18, 19, explorer);
        assertNextVertexIs(Step.NORTH_WEST, 18, 18, explorer);
        assertFalse(explorer.hasNextEdge());
    }

    /**
     * Test when we cannot build on some terrain types.
     */
    public void test2() {
        // Check the the Ocean type is where we think it is.
        int occeanTypeNumber = 4;
        TileTypeImpl ocean = (TileTypeImpl) world.get(SKEY.TERRAIN_TYPES,
                occeanTypeNumber);
        assertEquals(TerrainType.Category.Ocean, ocean.getCategory());

        // Check that track cannot be built on ocean.
        for (int i = 0; i < world.size(SKEY.TRACK_RULES); i++) {
            TrackRule rule = (TrackRule) world.get(SKEY.TRACK_RULES, i);
            assertFalse(rule.canBuildOnThisTerrainType(ocean.getCategory()));
        }

        // Place some ocean.
        FreerailsTile tile = FreerailsTile.getInstance(occeanTypeNumber);
        world.setTile(10, 9, tile);
        world.setTile(11, 10, tile);

        PositionOnTrack start;

        // Test starting in the middle of the map.
        start = PositionOnTrack.createComingFrom(10, 10, Step.NORTH);

        BuildTrackExplorer explorer = new BuildTrackExplorer(world, principle);
        explorer.setPosition(start.toInt());
        assertNextVertexIs(Step.NORTH_EAST, 11, 9, explorer);
        // We miss out SW, S, and SE since we don't want to double back on
        // ourselves.
        assertNextVertexIs(Step.WEST, 9, 10, explorer);
        assertNextVertexIs(Step.NORTH_WEST, 9, 9, explorer);
        assertFalse(explorer.hasNextEdge());
    }

    /**
     * Test for illegal track configurations.
     */
    public void test3() {
        // Build some track, from 10, 10 diagonally SE.
        int y = 10;
        int x = 10;

        for (int i = 0; i < 4; i++) {
            Step v = Step.SOUTH_EAST;
            buildTrack(x, y, v);
            x += v.deltaX;
            y += v.deltaY;
        }

        // If we enter 10, 10 from the south, we should be able to build track S
        // & SW.
        PositionOnTrack start = PositionOnTrack.createComingFrom(10, 10,
                Step.SOUTH);
        BuildTrackExplorer explorer = new BuildTrackExplorer(world, principle);
        explorer.setPosition(start.toInt());
        // SE is going along existing track
        assertNextVertexIs(Step.SOUTH_EAST, 11, 11, explorer);
        // S is building new track.
        assertNextVertexIs(Step.SOUTH, 10, 11, explorer);
        assertFalse(explorer.hasNextEdge());

        // If we enter 10, 11 from the north, we should be able to build track
        // N, E, W, & NW.
        start = PositionOnTrack.createComingFrom(10, 11, Step.NORTH);
        explorer.setPosition(start.toInt());
        assertNextVertexIs(Step.NORTH, 10, 10, explorer);
        assertNextVertexIs(Step.EAST, 11, 11, explorer);
        assertNextVertexIs(Step.WEST, 9, 11, explorer);
        assertNextVertexIs(Step.NORTH_WEST, 9, 10, explorer);
        assertFalse(explorer.hasNextEdge());

        // If we enter 10, 12 from the north, we also should be able to build
        // track N, E, W, & NW.
        start = PositionOnTrack.createComingFrom(10, 12, Step.NORTH);
        explorer.setPosition(start.toInt());
        assertNextVertexIs(Step.NORTH, 10, 11, explorer);
        assertNextVertexIs(Step.EAST, 11, 12, explorer);
        assertNextVertexIs(Step.WEST, 9, 12, explorer);
        assertNextVertexIs(Step.NORTH_WEST, 9, 11, explorer);
        assertFalse(explorer.hasNextEdge());
    }

    private void assertNextVertexIs(Step oneTileMoveVector, int x, int y,
                                    BuildTrackExplorer explorer) {
        assertTrue(explorer.hasNextEdge());
        explorer.nextEdge();

        PositionOnTrack pos = new PositionOnTrack(explorer
                .getVertexConnectedByEdge());
        assertEquals(PositionOnTrack.createComingFrom(x, y, oneTileMoveVector),
                pos);
    }

    private void buildTrack(int x, int y, Step direction) {
        TrackRule rule = (TrackRule) world.get(SKEY.TRACK_RULES, 0);
        ChangeTrackPieceCompositeMove move = ChangeTrackPieceCompositeMove
                .generateBuildTrackMove(new ImPoint(x, y), direction, rule,
                        rule, world, MapFixtureFactory.TEST_PRINCIPAL);
        MoveStatus ms = move.doMove(world, Player.AUTHORITATIVE);
        assertTrue(ms.ok);
    }
}