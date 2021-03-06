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

package freerails.model.station;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the supply at a station.
 */
public class StationSupply implements Serializable {

    private static final long serialVersionUID = 4049918272826847286L;
    private final List<Integer> supply;

    // TODO what is the meaning of cargoWaiting and do we need it?

    /**
     * @param cargoWaiting
     */
    public StationSupply(Integer[] cargoWaiting) {
        supply = new ArrayList<>(Arrays.asList(cargoWaiting));
    }

    // TODO why is cargType an int, not the class from world.cargo

    /**
     * Returns the number of car loads of the specified cargo that the station
     * supplies per year.
     */
    public int getSupply(int cargoType) {
        return supply.get(cargoType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StationSupply)) return false;

        final StationSupply stationSupply = (StationSupply) obj;

        return supply.equals(stationSupply.supply);
    }

    @Override
    public int hashCode() {
        return supply.hashCode();
    }

}