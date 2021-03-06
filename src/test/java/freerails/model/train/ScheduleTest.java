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
package freerails.model.train;

import freerails.model.train.schedule.Schedule;
import freerails.model.train.schedule.TrainOrder;
import junit.framework.TestCase;

/**
 *
 */
public class ScheduleTest extends TestCase {

    /**
     *
     */
    public void test1() {
        TrainOrder trainOrder1 = new TrainOrder(0, null, false, false);
        TrainOrder trainOrder2 = new TrainOrder(1, null, false, false);

        Schedule schedule = new Schedule();
        schedule.addOrder(trainOrder1);
        schedule.addOrder(trainOrder2);

        int stationToGoto = schedule.getNextStationId();
        assertEquals(0, stationToGoto);

        assertEquals(0, schedule.getNextStationId());
    }

}
