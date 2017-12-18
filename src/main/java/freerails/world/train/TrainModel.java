package freerails.world.train;

import freerails.world.common.FreerailsSerializable;
import freerails.world.common.ImInts;

/**
 * Represents a train.
 *
 * @author Luke
 */
public class TrainModel implements FreerailsSerializable {

    /**
     *
     */
    public static final int WAGON_LENGTH = 24;

    /**
     *
     */
    public static final int MAX_NUMBER_OF_WAGONS = 6;

    /**
     *
     */
    public static final int MAX_TRAIN_LENGTH = (1 + MAX_NUMBER_OF_WAGONS)
            * WAGON_LENGTH;
    private static final long serialVersionUID = 3545235825756812339L;
    private final int scheduleId;

    private final int engineTypeId;

    private final ImInts wagonTypes;

    private final int cargoBundleId;

    /**
     *
     * @param engine
     * @param wagons
     * @param scheduleID
     * @param BundleId
     */
    public TrainModel(int engine, ImInts wagons, int scheduleID, int BundleId) {
        engineTypeId = engine;
        wagonTypes = wagons;
        scheduleId = scheduleID;
        cargoBundleId = BundleId;
    }

    /**
     *
     * @param wagons
     * @param BundleId
     */
    public TrainModel(ImInts wagons, int BundleId) {
        wagonTypes = wagons;
        cargoBundleId = BundleId;
        engineTypeId = 0;
        scheduleId = 0;
    }

    /**
     *
     * @param engine
     * @param wagons
     * @param scheduleID
     */
    public TrainModel(int engine, ImInts wagons, int scheduleID) {
        engineTypeId = engine;
        wagonTypes = wagons;
        scheduleId = scheduleID;
        cargoBundleId = 0;
    }

    /**
     *
     * @param engine
     */
    public TrainModel(int engine) {
        engineTypeId = engine;
        wagonTypes = new ImInts(0, 1, 2);
        scheduleId = 0;
        cargoBundleId = 0;
    }

    @Override
    public int hashCode() {
        int result;
        result = scheduleId;
        result = 29 * result + engineTypeId;
        result = 29 * result + cargoBundleId;

        return result;
    }

    /**
     *
     * @param newEngine
     * @param newWagons
     * @return
     */
    public TrainModel getNewInstance(int newEngine, ImInts newWagons) {
        return new TrainModel(newEngine, newWagons, this.getScheduleID(), this
                .getCargoBundleID());
    }

    /**
     *
     * @return
     */
    public int getLength() {
        return (1 + wagonTypes.size()) * WAGON_LENGTH; // Engine + wagons.
    }

    /**
     *
     * @return
     */
    public boolean canAddWagon() {
        return wagonTypes.size() < MAX_NUMBER_OF_WAGONS;
    }

    /**
     *
     * @return
     */
    public int getNumberOfWagons() {
        return wagonTypes.size();
    }

    /**
     *
     * @param i
     * @return
     */
    public int getWagon(int i) {
        return wagonTypes.get(i);
    }

    /**
     *
     * @return
     */
    public int getEngineType() {
        return engineTypeId;
    }

    /**
     *
     * @return
     */
    public int getCargoBundleID() {
        return cargoBundleId;
    }

    /**
     *
     * @return
     */
    public int getScheduleID() {
        return scheduleId;
    }

    /**
     *
     * @return
     */
    public ImInts getConsist() {
        return wagonTypes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrainModel) {
            TrainModel test = (TrainModel) obj;

            return this.cargoBundleId == test.cargoBundleId
                    && this.engineTypeId == test.engineTypeId
                    && this.wagonTypes.equals(test.wagonTypes)
                    && this.scheduleId == test.scheduleId;
        }
        return false;
    }
}