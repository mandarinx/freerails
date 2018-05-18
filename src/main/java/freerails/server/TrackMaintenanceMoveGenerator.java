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
package freerails.server;

import freerails.model.track.TrackType;
import freerails.move.AddTransactionMove;
import freerails.move.Move;
import freerails.move.receiver.MoveReceiver;
import freerails.model.finances.ItemsTransactionAggregator;
import freerails.model.world.World;
import freerails.model.ModelConstants;
import freerails.model.finances.Money;
import freerails.model.finances.MoneyTransaction;
import freerails.model.finances.Transaction;
import freerails.model.finances.TransactionCategory;
import freerails.model.player.Player;

// TODO does not really follow the MoveGenerator interface, should it maybe?
/**
 * Iterates over the entries in the BankAccount and counts the number
 * of units of each track type, then calculates the cost of maintenance.
 */
public class TrackMaintenanceMoveGenerator {

    private final MoveReceiver moveReceiver;

    /**
     * @param moveReceiver
     */
    public TrackMaintenanceMoveGenerator(MoveReceiver moveReceiver) {
        this.moveReceiver = moveReceiver;
    }

    /**
     * @param world
     * @param principal
     * @param category
     * @return
     */
    private static Move generateMove(World world, Player principal, TransactionCategory category) {
        if (TransactionCategory.TRACK_MAINTENANCE != category && TransactionCategory.STATION_MAINTENANCE != category) {
            throw new IllegalArgumentException(String.valueOf(category));
        }

        ItemsTransactionAggregator aggregator = new ItemsTransactionAggregator(world, principal);
        aggregator.setCategory(TransactionCategory.TRACK);

        long amount = 0;

        for (TrackType trackType: world.getTrackTypes()) {
            // TODO Money arithmetic
            long maintenanceCost = trackType.getYearlyMaintenance().amount;

            // Is the track type the category we are interested in?
            boolean rightType = TransactionCategory.TRACK_MAINTENANCE == category ? !trackType.isStation() : trackType.isStation();

            if (rightType) {
                aggregator.setType(trackType.getId());
                amount += maintenanceCost * aggregator.calculateQuantity() / ModelConstants.LENGTH_OF_STRAIGHT_TRACK_PIECE;
            }
        }

        Transaction transaction = new MoneyTransaction(new Money(-amount), category);

        return new AddTransactionMove(principal, transaction);
    }

    /**
     * @param world
     */
    public void update(World world) {
        for (int i = 0; i < world.getNumberOfPlayers(); i++) {
            Player principal = world.getPlayer(i);
            Move move = generateMove(world, principal, TransactionCategory.TRACK_MAINTENANCE);
            moveReceiver.process(move);

            move = generateMove(world, principal, TransactionCategory.STATION_MAINTENANCE);
            moveReceiver.process(move);
        }
    }
}