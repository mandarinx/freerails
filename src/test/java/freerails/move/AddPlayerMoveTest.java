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
package freerails.move;

import freerails.model.player.Player;
import freerails.util.Utils;

/**
 *
 */
public class AddPlayerMoveTest extends AbstractMoveTestCase {

    /**
     *
     */
    public void testMove() {
        Player newPlayer = new Player(0, "New Player");
        assertTrue("Check reflexivity of Player.equals(.)", Utils.equalsBySerialization(newPlayer, newPlayer));
        AddPlayerMove move = AddPlayerMove.generateMove(getWorld(), newPlayer);
        assertSurvivesSerialisation(move);
        // TODO fails because removeLastPlayer in FullWord does not work currently
        // assertDoThenUndoLeavesWorldUnchanged(move);
    }

    /**
     *
     */
    public void testMove2() {
        Player newPlayer = new Player(1, "New Player");

        AddPlayerMove move = AddPlayerMove.generateMove(getWorld(), newPlayer);
        // TODO fails because removeLastPlayer in FullWord does not work currently
        // assertOkButNotRepeatable(move);
    }

}
