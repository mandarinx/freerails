package jfreerails.world.station;

import jfreerails.world.common.FreerailsSerializable;


/** This class represents the demand for cargo at a station.
 * @author Luke
 */
public class DemandAtStation implements FreerailsSerializable {
    private final boolean[] demand;

    public DemandAtStation(boolean[] demand) {
        this.demand = (boolean[])demand.clone(); //defensive copy.
    }

    public boolean isCargoDemanded(int cargoNumber) {
        return demand[cargoNumber];
    }

    public int hashCode() {
        int result = 0;

        for (int i = 0; i < demand.length; i++) {
            result = 29 * result + (demand[i] ? 1 : 0);
        }

        return result;
    }

    public boolean equals(Object o) {
        if (o instanceof DemandAtStation) {
            DemandAtStation test = (DemandAtStation)o;

            if (this.demand.length != test.demand.length) {
                return false;
            }

            for (int i = 0; i < demand.length; i++) {
                if (demand[i] != test.demand[i]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}