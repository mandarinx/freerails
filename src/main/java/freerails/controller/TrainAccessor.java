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

import freerails.util.ImmutableList;
import freerails.util.Point2D;
import freerails.world.*;
import freerails.world.cargo.CargoBatchBundle;
import freerails.world.cargo.ImmutableCargoBatchBundle;
import freerails.world.player.FreerailsPrincipal;
import freerails.world.station.Station;
import freerails.world.terrain.TileTransition;
import freerails.world.track.TrackSection;
import freerails.world.train.*;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Provides convenience methods to access the properties of a train from the world object.
 */
public class TrainAccessor {

    private final ReadOnlyWorld world;
    private final FreerailsPrincipal p;
    private final int id;

    /**
     * @param world
     * @param p
     * @param id
     */
    public TrainAccessor(final ReadOnlyWorld world, final FreerailsPrincipal p, final int id) {
        this.world = world;
        this.p = p;
        this.id = id;
    }

    /**
     * @param row
     * @param onTrain
     * @param consist
     * @return
     */
    public static ImmutableList<Integer> spaceAvailable2(ReadOnlyWorld row, CargoBatchBundle onTrain, ImmutableList<Integer> consist) {
        // This array will store the amount of space available on the train for
        // each cargo type.
        final int NUM_CARGO_TYPES = row.size(SKEY.CARGO_TYPES);
        Integer[] spaceAvailable = new Integer[NUM_CARGO_TYPES];
        Arrays.fill(spaceAvailable, 0);

        // First calculate the train's total capacity.
        for (int j = 0; j < consist.size(); j++) {
            int cargoType = consist.get(j);
            spaceAvailable[cargoType] += WagonType.UNITS_OF_CARGO_PER_WAGON;
        }

        for (int cargoType = 0; cargoType < NUM_CARGO_TYPES; cargoType++) {
            spaceAvailable[cargoType] = spaceAvailable[cargoType] - onTrain.getAmountOfType(cargoType);
        }
        return new ImmutableList<>(spaceAvailable);

    }

    /**
     * @param time
     * @return
     */
    public TrainActivity getStatus(double time) {
        TrainMotion tm = findCurrentMotion(time);
        return tm.getActivity();
    }

    /**
     * @return the id of the station the train is currently at, or -1 if no
     * current station.
     */
    public int getStationId(double time) {

        TrainMotion tm = findCurrentMotion(time);
        PositionOnTrack pot = tm.getFinalPosition();
        Point2D pp = pot.getP();

        // loop through the station list to check if train is at the same Point2D as a station
        for (int i = 0; i < world.size(p, KEY.STATIONS); i++) {
            Station tempPoint = (Station) world.get(p, KEY.STATIONS, i);

            if (null != tempPoint && pp.equals(tempPoint.p)) {
                return i; // train is at the station at location tempPoint
            }
        }

        return -1;
    }

    /**
     * @param time
     * @param view
     * @return
     */
    public TrainPositionOnMap findPosition(double time, Rectangle view) {
        ActivityIterator ai = world.getActivities(p, id);

        // goto last
        ai.gotoLastActivity();
        // search backwards
        while (ai.getFinishTime() >= time && ai.hasPrevious()) {
            ai.previousActivity();
        }
        boolean afterFinish = ai.getFinishTime() < time;
        while (afterFinish && ai.hasNext()) {
            ai.nextActivity();
            afterFinish = ai.getFinishTime() < time;
        }
        double dt = time - ai.getStartTime();
        dt = Math.min(dt, ai.getDuration());
        TrainMotion tm = (TrainMotion) ai.getActivity();

        Point2D start = tm.getPath().getStart();
        int trainLength = tm.getTrainLength();
        Rectangle trainBox = new Rectangle(start.x * WorldConstants.TILE_SIZE - trainLength * 2, start.y * WorldConstants.TILE_SIZE - trainLength * 2, trainLength * 4, trainLength * 4);
        if (!view.intersects(trainBox)) {
            return null; // 666 doesn't work
        }
        return tm.getState(dt);
    }

    /**
     * @param time
     * @return
     */
    public TrainMotion findCurrentMotion(double time) {
        ActivityIterator ai = world.getActivities(p, id);
        boolean afterFinish = ai.getFinishTime() < time;
        if (afterFinish) {
            ai.gotoLastActivity();
        }
        return (TrainMotion) ai.getActivity();
    }

    /**
     * @return
     */
    public TrainModel getTrain() {
        return (TrainModel) world.get(p, KEY.TRAINS, id);
    }

    /**
     * @return
     */
    public ImmutableSchedule getSchedule() {
        TrainModel train = getTrain();
        return (ImmutableSchedule) world.get(p, KEY.TRAIN_SCHEDULES, train.getScheduleID());
    }

    /**
     * @return
     */
    public CargoBatchBundle getCargoBundle() {
        TrainModel train = getTrain();
        return (ImmutableCargoBatchBundle) world.get(p, KEY.CARGO_BUNDLES, train.getCargoBundleID());
    }

    /**
     * Returns true iff all the following hold.
     * <ol>
     * <li>The train is waiting for a full load at some station X.</li>
     * <li>The current train order tells the train to goto station X.</li>
     * <li>The current train order tells the train to wait for a full load.</li>
     * <li>The current train order specifies a consist that matches the train's
     * current consist.</li>
     * </ol>
     */
    public boolean keepWaiting() {
        double time = world.currentTime().getTicks();
        int stationId = getStationId(time);
        if (stationId == -1) return false;
        TrainActivity act = getStatus(time);
        if (act != TrainActivity.WAITING_FOR_FULL_LOAD) return false;
        ImmutableSchedule shedule = getSchedule();
        TrainOrdersModel order = shedule.getOrder(shedule.getOrderToGoto());
        if (order.stationId != stationId) return false;
        if (!order.waitUntilFull) return false;
        TrainModel train = getTrain();
        return order.getConsist().equals(train.getConsist());
    }

    /**
     * @return the location of the station the train is currently heading
     * towards.
     */
    public Point2D getTarget() {
        TrainModel train = (TrainModel) world.get(p, KEY.TRAINS, id);
        int scheduleID = train.getScheduleID();
        Schedule schedule = (ImmutableSchedule) world.get(p, KEY.TRAIN_SCHEDULES, scheduleID);
        int stationNumber = schedule.getStationToGoto();

        if (-1 == stationNumber) {
            // There are no stations on the schedule.
            return Point2D.ZERO;
        }

        Station station = (Station) world.get(p, KEY.STATIONS, stationNumber);
        return station.p;
    }

    /**
     * @param time
     * @return
     */
    public HashSet<TrackSection> occupiedTrackSection(double time) {
        TrainMotion tm = findCurrentMotion(time);
        PathOnTiles path = tm.getPath();
        HashSet<TrackSection> sections = new HashSet<>();
        Point2D start = path.getStart();
        int x = start.x;
        int y = start.y;
        for (int i = 0; i < path.steps(); i++) {
            TileTransition s = path.getStep(i);
            Point2D tile = new Point2D(x, y);
            x += s.deltaX;
            y += s.deltaY;
            sections.add(new TrackSection(s, tile));
        }
        return sections;
    }

    /**
     * @param time
     * @return
     */
    public boolean isMoving(double time) {
        TrainMotion tm = findCurrentMotion(time);
        double speed = tm.getSpeedAtEnd();
        return speed != 0;
    }

    /**
     * The space available on the train measured in cargo units.
     */
    public ImmutableList<Integer> spaceAvailable() {

        TrainModel train = (TrainModel) world.get(p, KEY.TRAINS, id);
        CargoBatchBundle bundleOnTrain = (ImmutableCargoBatchBundle) world.get(p, KEY.CARGO_BUNDLES, train.getCargoBundleID());
        return spaceAvailable2(world, bundleOnTrain, train.getConsist());

    }

}
