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
package freerails.world.cargo;

import freerails.util.Point2D;
import freerails.util.Utils;
import junit.framework.TestCase;

import java.io.Serializable;

/**
 * Test for CargoBatchBundle
 */
public class CargoBatchBundleTest extends TestCase {

    /**
     *
     */
    public void testEquals() {

        MutableCargoBatchBundle bundle1 = new MutableCargoBatchBundle();
        MutableCargoBatchBundle bundle2 = new MutableCargoBatchBundle();
        CargoBatch batch1 = new CargoBatch(1, new Point2D(2, 3), 4, 5);
        CargoBatch batch2 = new CargoBatch(4, new Point2D(2, 3), 4, 5);
        int q1 = 10;
        int q2 = 20;
        bundle1.addCargo(batch1, q1);
        bundle1.addCargo(batch2, q2);
        assertBundlesNotEqual(bundle1, bundle2);
        bundle2.addCargo(batch2, q2);
        assertBundlesNotEqual(bundle1, bundle2);
        bundle2.addCargo(batch1, q1);
        assertBundlesEqual(bundle1, bundle2);
    }

    private void assertBundlesEqual(MutableCargoBatchBundle a, MutableCargoBatchBundle b) {
        assertEquals(a, b);
        assertEquals(a, a);
        assertEquals(b, a);
        ImmutableCargoBatchBundle immA = a.toImmutableCargoBundle();
        assertEquals(immA, immA);
        assertEquals(immA, b);

        ImmutableCargoBatchBundle immB = b.toImmutableCargoBundle();
        assertEquals(immA, immB);
        Serializable cloneA = Utils.cloneBySerialisation(immA);
        Serializable cloneB = Utils.cloneBySerialisation(immB);
        assertEquals(cloneA, cloneB);
        assertEquals(a, immB);
    }

    private void assertBundlesNotEqual(MutableCargoBatchBundle a,
                                       MutableCargoBatchBundle b) {
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertFalse(a.toImmutableCargoBundle().equals(b));
        assertFalse(a.toImmutableCargoBundle().equals(
                b.toImmutableCargoBundle()));
        assertFalse(a.equals(b.toImmutableCargoBundle()));
    }

}
