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

package freerails.world.track;

import freerails.world.FreerailsSerializable;
import freerails.world.common.Money;
import freerails.world.common.Step;
import freerails.world.terrain.TerrainType;

import java.util.Iterator;

/**
 * Defines methods to access the properties of a track type.
 *
 */
public interface TrackRule extends FreerailsSerializable, Comparable<TrackRule> {

    /**
     *
     * @return
     */
    TrackCategories getCategory();

    /**
     *
     * @param TerrainType
     * @return
     */
    boolean canBuildOnThisTerrainType(TerrainType.Category TerrainType);

    /**
     *
     * @return
     */
    boolean isStation();

    /**
     *
     * @return
     */
    boolean isDouble();

    /**
     *
     * @return
     */
    Money getPrice();

    /**
     *
     * @return
     */
    Money getFixedCost();

    /**
     *
     * @return
     */
    Money getMaintenanceCost();

    /**
     *
     * @return
     */
    int getStationRadius();

    /**
     *
     * @return
     */
    String getTypeName();

    /**
     *
     * @param a9bitTemplate
     * @return
     */
    boolean testTrackPieceLegality(int a9bitTemplate);

    /**
     *
     * @param config
     * @return
     */
    boolean trackPieceIsLegal(TrackConfiguration config);

    /**
     *
     * @return
     */
    int getMaximumConsecutivePieces();

    /**
     *
     * @param directionComingFrom
     * @return
     */
    Step[] getLegalRoutes(Step directionComingFrom);

    /**
     *
     * @return
     */
    Iterator<TrackConfiguration> getLegalConfigurationsIterator();

    /**
     *
     */
    enum TrackCategories {

        /**
         *
         */
        track,

        /**
         *
         */
        bridge,

        /**
         *
         */
        tunnel,

        /**
         *
         */
        station,

        /**
         *
         */
        non
    }
}