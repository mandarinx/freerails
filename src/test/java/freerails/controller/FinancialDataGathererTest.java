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

import freerails.move.AddPlayerMove;
import freerails.move.Move;
import freerails.move.Status;
import freerails.model.finances.*;
import freerails.model.ModelConstants;
import freerails.model.world.World;
import freerails.model.player.Player;
import junit.framework.TestCase;

/**
 * Test for FinancialDataGatherer.
 */
public class FinancialDataGathererTest extends TestCase {

    private World world;
    private Player player;

    /**
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        player = new Player(0, "Player X");
        world = new World.Builder().build();

        Move addPlayer = AddPlayerMove.generateMove(world, player);
        Status status = addPlayer.doMove(world, Player.AUTHORITATIVE);
        assertTrue(status.succeeds());
    }

    /**
     *
     */
    public void testCanIssueBond() {
        FinancialDataGatherer fdg = new FinancialDataGatherer(world, player);
        assertTrue(fdg.canIssueBond()); // 5%

        assertTrue(addBond()); // 6%
        assertTrue(addBond()); // 7%
        assertFalse(addBond()); // 8% so can't
        fdg = new FinancialDataGatherer(world, player);
        assertEquals(8.0, fdg.nextBondInterestRate());
    }

    /**
     * Adds a bond and returns true if another bond can be added. Written to
     * avoid copy & paste in testCanIssueBond().
     */
    private boolean addBond() {
        world.addTransaction(player, BondItemTransaction.issueBond(5));
        FinancialDataGatherer financialDataGatherer = new FinancialDataGatherer(world, player);
        return financialDataGatherer.canIssueBond();
    }

    /**
     *
     */
    public void testNextBondInterestRate() {
        FinancialDataGatherer financialDataGatherer = new FinancialDataGatherer(world, player);
        assertEquals(5.0, financialDataGatherer.nextBondInterestRate());
        world.addTransaction(player, BondItemTransaction.issueBond(5));
        financialDataGatherer = new FinancialDataGatherer(world, player);
        assertEquals(6.0, financialDataGatherer.nextBondInterestRate());
    }

    /**
     *
     */
    public void testTreasuryStock() {
        FinancialDataGatherer fdg = new FinancialDataGatherer(world, player);
        assertEquals(0, fdg.treasuryStock());

        int treasuryStock = 10000;
        int totalStock = ModelConstants.IPO_SIZE;
        int publicStock = totalStock - treasuryStock;
        Transaction transaction = StockItemTransaction.buyOrSellStock(0, treasuryStock, new Money(5));
        world.addTransaction(player, transaction);
        fdg = new FinancialDataGatherer(world, player);
        assertEquals(treasuryStock, fdg.treasuryStock());
        assertEquals(totalStock, fdg.totalShares());
        assertEquals(publicStock, fdg.sharesHeldByPublic());
    }

    /**
     *
     */
    public void testBuyingStakesInOtherRRs() {
        world = new World.Builder().build();
        Player[] players = new Player[2];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i, "Player " + i);
            Move addPlayer = AddPlayerMove.generateMove(world, players[i]);
            Status status = addPlayer.doMove(world, Player.AUTHORITATIVE);
            assertTrue(status.succeeds());
        }

        // Make player #0 buy stock in player #1
        int quantity = 10000;
        Transaction transaction = StockItemTransaction.buyOrSellStock(1, quantity, new Money(5));
        world.addTransaction(players[0], transaction);
        FinancialDataGatherer fdg = new FinancialDataGatherer(world, players[0]);
        assertEquals(0, fdg.treasuryStock());
        int acutal = fdg.getStockInRRs()[1];
        assertEquals(quantity, acutal);
    }

    /**
     *
     */
    public void testTotalShares() {
        FinancialDataGatherer fdg = new FinancialDataGatherer(world, player);
        int expected = ModelConstants.IPO_SIZE;
        assertEquals(expected, fdg.totalShares());
    }
}