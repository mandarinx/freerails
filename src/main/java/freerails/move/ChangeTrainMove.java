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

package freerails.move;

import freerails.model.player.Player;
import freerails.model.train.Train;
import freerails.model.world.World;
import freerails.nove.Status;
import org.jetbrains.annotations.NotNull;

// TODO maybe divide this into more cases like (change cargobatch, change schedule, ...) we already have next activity...
// TODO no undo, equals, hashcode
/**
 *
 */
public class ChangeTrainMove implements Move {

    private final Player player;
    private final Train train;

    public ChangeTrainMove(@NotNull Player player, @NotNull Train train) {
        this.player = player;
        this.train = train;
    }

    @Override
    public Status tryDoMove(World world, Player player) {
        return Status.OK;
    }

    @Override
    public Status tryUndoMove(World world, Player player) {
        return Status.OK;
    }

    @Override
    public Status doMove(World world, Player player) {
        Train t = world.getTrain(this.player, train.getId());
        // update consist
        t.setConsist(train.getConsist());
        // update engine
        t.setEngine(train.getEngine());
        // update cargobatchbundle
        t.setCargoBatchBundle(train.getCargoBatchBundle());
        // update schedule
        t.setSchedule(train.getSchedule());
        // update activities
        t.setActivities(train.getActivities());

        return Status.OK;
    }

    @Override
    public Status undoMove(World world, Player player) {
        return Status.OK;
    }
}
