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
package freerails.client.model;

import freerails.client.model.TrainOrdersListModel.TrainOrdersListElement;
import freerails.model.finance.IncomeStatementGenerator;
import freerails.model.world.UnmodifiableWorld;
import freerails.model.finance.Money;
import freerails.model.player.Player;
import freerails.model.station.Station;
import freerails.model.train.schedule.TrainOrder;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TrainSummeryModel {

    private static final long MINIMUM_WAIT_TIME = 250;
    private final Map<Integer, Money> lastTrainIncome;
    private final Map<Integer, String> lastStations;
    private UnmodifiableWorld world = null;
    private int lastNrOfTransactions = 0;
    private Player player = null;
    private int maxTrainNum = 0;
    private long lastUpdate;
    private long lastStationUpdate;

    /**
     *
     */
    public TrainSummeryModel() {
        lastTrainIncome = new HashMap<>(64);
        lastUpdate = lastStationUpdate = System.currentTimeMillis();
        lastStations = new HashMap<>(64);
    }

    /**
     * @param world
     * @param player
     */
    public void setWorld(UnmodifiableWorld world, Player player) {
        if (this.world != world) {
            this.world = world;
        }
        if (this.player != player) {
            this.player = player;
        }
    }

    /**
     * @param trainNum
     * @return
     */
    public Money findTrainIncome(int trainNum) {
        int numberOfTransactions = world.getTransactions(player).size();
        long currentTime = System.currentTimeMillis();
        if (lastUpdate + MINIMUM_WAIT_TIME > currentTime || numberOfTransactions == lastNrOfTransactions) {
            // not necessary ...
            Money m = lastTrainIncome.get(trainNum);
            if (m != null) {
                return m;
            }
            // but we don't have it
        } else {
            lastNrOfTransactions = numberOfTransactions;
            lastUpdate = currentTime;
        }
        IncomeStatementGenerator income = new IncomeStatementGenerator(world, player);
        maxTrainNum = Math.max(maxTrainNum, trainNum);
        Money[] m = new Money[maxTrainNum + 1];
        income.calTrainRevenue(m);
        for (int i = 0; i < maxTrainNum; i++) {
            lastTrainIncome.put(i, m[i]);
        }
        return m[trainNum];
    }

    /**
     * @param trainNum
     * @return
     */
    public String getStationName(int trainNum) {
        long currentTime = System.currentTimeMillis();
        if (lastStationUpdate + MINIMUM_WAIT_TIME > currentTime) {
            String lastStation = lastStations.get(trainNum);
            if (lastStation != null) {
                return lastStation;
            }
        }
        lastStationUpdate = currentTime;
        TrainOrder orders = null;
        TrainOrdersListModel ordersList = new TrainOrdersListModel(world, trainNum, player);
        int size = ordersList.getSize();
        for (int i = 0; i < size; ++i) {
            TrainOrdersListElement element = (TrainOrdersListElement) ordersList.getElementAt(i);
            if (element.gotoStatus == TrainOrdersListModel.GOTO_NOW) {
                orders = element.order;
                break;
            }
        }
        Station station = world.getStation(player, orders.getStationID());
        String stationName = station.getStationName();
        lastStations.put(trainNum, stationName);
        return stationName;
    }
}
