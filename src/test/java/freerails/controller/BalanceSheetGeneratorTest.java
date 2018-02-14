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
import freerails.move.MoveStatus;
import freerails.world.*;
import freerails.world.finances.Money;
import freerails.world.game.GameCalendar;
import freerails.world.game.GameTime;
import freerails.world.player.Player;
import freerails.world.world.FullWorld;
import freerails.world.world.World;
import junit.framework.TestCase;

/**
 * Test for BalanceSheetGenerator.
 */
public class BalanceSheetGeneratorTest extends TestCase {

    private Player player;
    private World world;

    /**
     *
     */
    public void testBondsFigure() {
        BalanceSheetGenerator generator = new BalanceSheetGenerator(world, player.getPrincipal());
        Money expectedBondValue = WorldConstants.BOND_VALUE_ISSUE;
        assertEquals(Money.opposite(expectedBondValue), generator.total.loans);
        assertEquals(Money.opposite(expectedBondValue), generator.ytd.loans);
    }

    /**
     *
     */
    public void testStockHolderEquityFigure() {
        BalanceSheetGenerator generator = new BalanceSheetGenerator(world, player.getPrincipal());
        Money expectStockHolderEquity = new Money(-500000);
        assertEquals(expectStockHolderEquity, generator.total.equity);
    }

    /**
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        world = new FullWorld(10, 10);
        player = new Player("Player X", world.getNumberOfPlayers());
        world.set(ITEM.CALENDAR, new GameCalendar(1200, 1840));
        world.setTime(new GameTime(0));

        Move addPlayerMove = AddPlayerMove.generateMove(world, player);
        MoveStatus moveStatus = addPlayerMove.doMove(world, player.getPrincipal());
        assertTrue(moveStatus.getMessage(), moveStatus.succeeds());

        world.setTime(new GameTime(100));
    }

}
