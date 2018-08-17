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
package freerails.network;

import freerails.model.player.Player;
import freerails.move.*;
import freerails.move.generator.MoveGenerator;
import freerails.model.game.Time;
import freerails.model.world.World;
import freerails.move.generator.TimeTickMoveGenerator;
import freerails.util.Vec2D;
import freerails.util.WorldGenerator;
import junit.framework.TestCase;

/**
 */
public class MovePrecommitterTest extends TestCase {

    private World world;
    private MovePrecommitter committer;

    /**
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        world = WorldGenerator.minimalWorld();
        committer = new MovePrecommitter(world);
    }

    /**
     * Test simple case of precommitting then fully committing moves.
     */
    public void test1() {
        Time oldtime = getTime();
        Time newTime = oldtime.advancedTime();
        assertFalse(oldtime.equals(newTime));

        Move move = new TimeTickMove(oldtime, newTime);
        Status status = move.tryDoMove(world, Player.AUTHORITATIVE);
        assertTrue(status.succeeds());

        committer.toServer(move);

        // The move m should now have been precommitted.
        assertEquals(newTime, getTime());
        assertFalse(committer.blocked);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(1, committer.precomitted.size());

        committer.fromServer(status);

        // The move m should now be full committed.
        assertFalse(committer.blocked);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());
        assertEquals(newTime, getTime());
    }

    /**
     * Test test clash.
     */
    public void test2() {
        Time oldtime = getTime();
        Time newTime = oldtime.advancedTime();
        assertFalse(oldtime.equals(newTime));

        Move move = new TimeTickMove(oldtime, newTime);
        Status status = move.tryDoMove(world, Player.AUTHORITATIVE);
        assertTrue(status.succeeds());

        committer.toServer(move);

        // The move should now have been precommitted.
        assertEquals(newTime, getTime());
        assertFalse(committer.blocked);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(1, committer.precomitted.size());

        committer.fromServer(move);
        assertFalse(move.tryDoMove(world, Player.AUTHORITATIVE).succeeds());

        // The move m should now be full committed.
        assertEquals(1, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());

        assertEquals(newTime, getTime());

        committer.precommitMoves();

        /*
         * The committer should be block since the move on the uncommitted list
         * fails to go through.
         */
        assertTrue(committer.blocked);
        assertEquals(1, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());

        Move m2 = new TimeTickMove(newTime, oldtime);
        committer.fromServer(m2);
        assertEquals(oldtime, getTime());
        committer.fromServer(status);
        assertEquals(newTime, getTime());
        assertFalse(committer.blocked);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());
    }

    /**
     * Test test rejection 1.
     */
    public void test3() {
        Time oldtime = getTime();
        Time newTime = oldtime.advancedTime();
        assertFalse(oldtime.equals(newTime));

        Move move = new TimeTickMove(oldtime, newTime);
        Status status = move.tryDoMove(world, Player.AUTHORITATIVE);
        assertTrue(status.succeeds());

        committer.toServer(move);

        // The move m should now have been precommitted.
        assertEquals(newTime, getTime());
        assertFalse(committer.blocked);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(1, committer.precomitted.size());

        // Now, suppose the server rejected the move..
        Status rejection = Status.moveFailed("Rejected!");
        committer.fromServer(rejection);
        assertEquals(oldtime, getTime());
        assertFalse(committer.blocked);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());
    }

    /**
     * Test test rejection 2.
     */
    public void test4() {
        Time oldtime = getTime();
        Time newTime = oldtime.advancedTime();
        assertFalse(oldtime.equals(newTime));

        // the following move should fail!
        Move move = new TimeTickMove(newTime, oldtime);
        Status status = move.tryDoMove(world, Player.AUTHORITATIVE);
        assertFalse(status.succeeds());

        committer.toServer(move);
        assertTrue(committer.blocked);
        assertEquals(1, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());

        committer.fromServer(status);
        assertFalse(committer.blocked);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());
    }

    /**
     *
     */
    public void testPreMoves1() {
        MoveGenerator moveGenerator = TimeTickMoveGenerator.INSTANCE;
        Time oldtime = getTime();
        Time newTime = oldtime.advancedTime();
        committer.fromServer(moveGenerator);
        assertEquals(newTime, getTime());
    }

    /**
     *
     */
    public void testPreMoves2() {
        MoveGenerator moveGenerator = TimeTickMoveGenerator.INSTANCE;
        Time oldtime = getTime();
        Time newTime = oldtime.advancedTime();

        // Send a premove to the server.
        committer.toServer(moveGenerator);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(1, committer.precomitted.size());
        assertEquals(newTime, getTime());

        // The server accepts it..
        committer.fromServer(TryMoveStatus.TRY_MOVE_OK);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());
        assertEquals(newTime, getTime());
    }

    /**
     *
     */
    public void testPreMoves3() {
        MoveGenerator moveGenerator = TimeTickMoveGenerator.INSTANCE;
        Time oldtime = getTime();
        Time newTime = oldtime.advancedTime();

        // Send a premove to the server.
        committer.toServer(moveGenerator);
        assertEquals(0, committer.uncomitted.size());
        assertEquals(1, committer.precomitted.size());
        assertEquals(newTime, getTime());

        // The server rejects it.
        committer.fromServer(TryMoveStatus.failed("failed"));
        assertEquals(0, committer.uncomitted.size());
        assertEquals(0, committer.precomitted.size());
        assertEquals(oldtime, getTime());
    }

    private Time getTime() {
        return world.currentTime();
    }
}