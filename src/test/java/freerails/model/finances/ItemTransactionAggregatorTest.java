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
package freerails.model.finances;

import freerails.model.track.TrackType;
import freerails.model.world.World;
import freerails.model.MapFixtureFactory;
import freerails.util.Vec2D;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.SortedSet;

/**
 * Test for ItemTransactionAggregator.
 */
public class ItemTransactionAggregatorTest extends TestCase {

    private World world;

    /**
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // generate track types
        SortedSet<TrackType> trackTypes = MapFixtureFactory.generateTrackRuleList();

        world = new World.Builder().setMapSize(new Vec2D(10, 10)).setTrackTypes(trackTypes).build();
        world.addPlayer(MapFixtureFactory.TEST_PLAYER);
    }

    /**
     *
     */
    public void testCalulateNumberOfEachTrackType() {
        int[] actual;
        int[] expected;
        actual = calNumOfEachTrackType();

        /*
         * actual = ItemsTransactionAggregator.calculateNumberOfEachTrackType(world,
         * MapFixtureFactory.TEST_PRINCIPAL, 0);
         */
        expected = new int[]{0, 0, 0}; // No track has been built yet.
        assertTrue(Arrays.equals(expected, actual));

        addTrack(0, 10);

        actual = calNumOfEachTrackType();
        expected = new int[]{10, 0, 0};
        assertTrue(Arrays.equals(expected, actual));

        addTrack(2, 20);

        actual = calNumOfEachTrackType();
        expected = new int[]{10, 0, 20};
        assertTrue(Arrays.equals(expected, actual));
    }

    /**
     *
     * @return
     */
    private int[] calNumOfEachTrackType() {
        int[] actual;
        ItemsTransactionAggregator aggregator = new ItemsTransactionAggregator(world, MapFixtureFactory.TEST_PRINCIPAL);
        actual = new int[3];
        aggregator.setType(0);
        actual[0] = aggregator.calculateQuantity();
        aggregator.setType(1);
        actual[1] = aggregator.calculateQuantity();
        aggregator.setType(2);
        actual[2] = aggregator.calculateQuantity();

        return actual;
    }

    /**
     * Utility method to add the specified number of units of the specified track
     * type.
     */
    private void addTrack(int trackType, int quantity) {
        ItemTransaction transaction = new ItemTransaction(TransactionCategory.TRACK, trackType, quantity,
                new Money(trackType));
        world.addTransaction(MapFixtureFactory.TEST_PRINCIPAL, transaction);
    }
}