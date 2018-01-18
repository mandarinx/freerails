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

package freerails.world.train;

import freerails.util.LineSegment;
import freerails.world.WorldConstants;
import freerails.world.track.PathIterator;

import java.util.Iterator;

/**
 * Exposes the path of a train. TODO needs better comment
 */

public class TrainPathIterator implements PathIterator {

    private static final long serialVersionUID = 3256999977816502584L;
    private final Iterator<Integer> intIterator;
    private final PositionOnTrack p1 = new PositionOnTrack();
    private final PositionOnTrack p2 = new PositionOnTrack();

    /**
     * @param i
     */
    public TrainPathIterator(Iterator<Integer> i) {
        intIterator = i;
        p2.setValuesFromInt(intIterator.next());
    }

    public boolean hasNext() {
        return intIterator.hasNext();
    }

    public void nextSegment(LineSegment line) {
        p1.setValuesFromInt(p2.toInt());
        line.setX1(p1.getX() * WorldConstants.TILE_SIZE + WorldConstants.TILE_SIZE / 2);
        line.setY1(p1.getY() * WorldConstants.TILE_SIZE + WorldConstants.TILE_SIZE / 2);
        p2.setValuesFromInt(intIterator.next());
        line.setX2(p2.getX() * WorldConstants.TILE_SIZE + WorldConstants.TILE_SIZE / 2);
        line.setY2(p2.getY() * WorldConstants.TILE_SIZE + WorldConstants.TILE_SIZE / 2);
    }
}