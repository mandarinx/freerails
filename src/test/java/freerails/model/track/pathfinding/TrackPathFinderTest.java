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
package freerails.model.track.pathfinding;

import freerails.model.track.BuildTrackStrategy;
import freerails.util.Vec2D;
import freerails.model.world.World;
import freerails.model.game.GameRules;
import freerails.model.world.WorldItem;
import freerails.model.player.Player;
import freerails.model.MapFixtureFactory;
import junit.framework.TestCase;

import java.util.List;

/**
 * Test for TrackPathFinder.
 */
public class TrackPathFinderTest extends TestCase {

    private final Player testPlayer = new Player("test", 0);
    private World world;

    /**
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        world = new World(new Vec2D(20, 20));
        world.addPlayer(testPlayer);
        world.set(WorldItem.GameRules, GameRules.NO_RESTRICTIONS);
        MapFixtureFactory.generateTrackRuleList(world);
    }

    /**
     *
     */
    public void testGeneratePath() {
        try {
            BuildTrackStrategy buildTrackStrategy = BuildTrackStrategy.getSingleRuleInstance(0, world);

            TrackPathFinder pathFinder = new TrackPathFinder(world, testPlayer.getPrincipal());
            List l = pathFinder.generatePath(Vec2D.ZERO, new Vec2D(0,5), buildTrackStrategy);
            assertEquals(5, l.size());

            List list2 = pathFinder.generatePath(new Vec2D(5, 5),new Vec2D(5, 10), buildTrackStrategy);
            assertEquals(5, list2.size());

            list2 = pathFinder.generatePath(new Vec2D(5, 10), new Vec2D(5,5), buildTrackStrategy);
            assertEquals(5, list2.size());
        } catch (PathNotFoundException e) {
            fail();
        }
    }
}