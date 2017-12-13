/*
 * Copyright (C) 2003 Luke Lindsay
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/*
 * TrainOrders.java
 *
 * Created on 31 March 2003, 23:17
 */
package jfreerails.world.train;

import java.util.Arrays;

import jfreerails.world.common.FreerailsSerializable;
import jfreerails.world.top.ObjectKey;

/**
 * This class encapsulates the orders for a train.
 * @author  Luke
 */
public class TrainOrdersModel implements FreerailsSerializable {
    public static final int MAXIMUM_NUMBER_OF_WAGONS = 6;
    public final boolean waitUntilFull;
    public final int[] consist; //The wagon types to add; if null, then no change.
    public final ObjectKey station; //The station to goto.

    /**
     *  Creates a new instance of TrainOrders
     */
    public TrainOrdersModel(ObjectKey station, int[] newConsist, boolean wait) {
        //If there are no wagons, set wait = false.
        wait = (null == newConsist || 0 == newConsist.length) ? false : wait;

        waitUntilFull = wait;
        consist = newConsist;
        this.station = station;
    }

    public int[] getConsist() {
        return this.consist;
    }

    public ObjectKey getStationNumber() {
        return station;
    }

    public boolean isNoConsistChange() {
        return null == consist;
    }

    public boolean getWaitUntilFull() {
        return waitUntilFull;
    }

    public boolean orderHasWagons() {
        return null != consist && 0 != consist.length;
    }

    public boolean hasLessThanMaxiumNumberOfWagons() {
        return null == consist || consist.length < MAXIMUM_NUMBER_OF_WAGONS;
    }

    public boolean equals(Object obj) {
        if (obj instanceof TrainOrdersModel) {
            TrainOrdersModel test = (TrainOrdersModel)obj;

            return this.waitUntilFull == test.waitUntilFull &&
            this.station.equals(test.station) &&
            Arrays.equals(this.consist, test.consist);
        } else {
            return false;
        }
    }
}
