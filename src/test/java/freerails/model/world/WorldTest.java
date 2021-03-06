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
package freerails.model.world;

import freerails.model.finance.transaction.Transaction;
import freerails.model.finance.transaction.TransactionCategory;
import freerails.util.Vec2D;
import freerails.util.Utils;
import freerails.model.finance.*;
import freerails.model.player.Player;
import freerails.util.WorldGenerator;
import junit.framework.TestCase;

import java.io.Serializable;

/**
 * Test for World.
 */
public class WorldTest extends TestCase {

    private static final Serializable fs = new TestState(1);

    /**
     *
     */
    public void testConstructor() {
        World world = WorldGenerator.minimalWorld();
        assertEquals(world.getMapSize(), new Vec2D(20, 20));
    }

    /**
     * Tests that changing the object returned by defensiveCopy() does not alter
     * the world object that was copied.
     */
    public void testDefensiveCopy() {
        World original;
        World copy;
        original = WorldGenerator.minimalWorld();
        /**
         * Returns a copy of this world object - making changes to this copy will
         * not change this object.
         */
        copy = (World) Utils.cloneBySerialisation(original);
        assertNotSame("The copies should be different objects.", original, copy);
        assertEquals("The copies should be logically equal.", original, copy);
    }

    /**
     *
     */
    public void testEquals() {
        World original;
        World copy;
        original = WorldGenerator.minimalWorld();
        /**
         * Returns a copy of this world object - making changes to this copy will
         * not change this object.
         */
        copy = (World) Utils.cloneBySerialisation(original);

        Player player = new Player(0, "Name");
        int index = copy.addPlayer(player);
        assertEquals(index, 0);
        assertFalse(copy.equals(original));
        original.addPlayer(player);
        assertEquals("The copies should be logically equal.", original, copy);

        assertTrue(Utils.equalsBySerialization(original, copy));

        Transaction transaction = new Transaction(TransactionCategory.MISC_INCOME, new Money(100), copy.getClock().getCurrentTime());
        copy.addTransaction(player, transaction);
        assertEquals(new Money(100), copy.getCurrentBalance(player));
        assertFalse(copy.equals(original));
    }

    /**
     *
     */
    public void testEquals2() {
        World original;
        World copy, copy2;
        original = WorldGenerator.minimalWorld();
        /**
         * Returns a copy of this world object - making changes to this copy will
         * not change this object.
         */
        copy = (World) Utils.cloneBySerialisation(original);
        /**
         * Returns a copy of this world object - making changes to this copy will
         * not change this object.
         */
        copy2 = (World) Utils.cloneBySerialisation(original);
        // Test adding players.
        Player a = new Player(0, "Fred");
        Player b = new Player(1, "John");
        original.addPlayer(a);
        copy.addPlayer(b);
        assertFalse(copy.equals(original));
    }

    /**
     *
     */
    public void testBoundsContain() {
        World world = WorldGenerator.minimalWorld();
        assertTrue(world.boundsContain(new Vec2D(1, 1)));
        assertTrue(world.boundsContain(Vec2D.ZERO));
        assertFalse(world.boundsContain(new Vec2D(-1, -1)));
        assertFalse(world.boundsContain(new Vec2D(20, 20)));
    }

    /**
     *
     */
    public void testBankAccount() {
        World world = WorldGenerator.minimalWorld();
        Player player = new Player(0, "Test");
        int playerID = world.addPlayer(player);
        assertEquals(0, playerID);
        player = world.getPlayer(playerID);
        Transaction transaction = new Transaction(TransactionCategory.BOND, new Money(100), world.getClock().getCurrentTime());
        assertEquals(new Money(0), world.getCurrentBalance(player));
        world.addTransaction(player, transaction);
        assertEquals(1, world.getTransactions(player).size());
        assertEquals(new Money(100), world.getCurrentBalance(player));
        Transaction t2 = world.getTransaction(player, 0);
        assertEquals(transaction, t2);
        Transaction t3 = world.removeLastTransaction(player);
        assertEquals(transaction, t3);
        assertEquals(new Money(0), world.getCurrentBalance(player));
    }

}
