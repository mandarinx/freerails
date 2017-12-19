/*
 * Created on 28-Mar-2003
 *
 */
package freerails.world.station;

import freerails.world.FreerailsSerializable;
import freerails.world.common.ImInts;

/**
 * This class represents the blue print for what an engine shop is producing.
 *
 */
public class PlannedTrain implements FreerailsSerializable {
    private static final long serialVersionUID = 3545515106038592057L;

    private final int engineType;

    private final ImInts wagonTypes;

    /**
     *
     * @param e
     * @param wagons
     */
    public PlannedTrain(int e, int[] wagons) {
        engineType = e;
        wagonTypes = new ImInts(wagons);
    }

    @Override
    public int hashCode() {
        return engineType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PlannedTrain))
            return false;

        final PlannedTrain productionAtEngineShop = (PlannedTrain) o;

        if (engineType != productionAtEngineShop.engineType)
            return false;
        return wagonTypes.equals(productionAtEngineShop.wagonTypes);
    }

    /**
     *
     * @return
     */
    public int getEngineType() {
        return engineType;
    }

    /**
     *
     * @return
     */
    public ImInts getWagonTypes() {
        return wagonTypes;
    }

    @Override
    public String toString() {
        return "engine type: " + this.engineType + ", with "
                + wagonTypes.size() + "wagons";
    }
}