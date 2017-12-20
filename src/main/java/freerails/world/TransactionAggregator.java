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

package freerails.world;

import freerails.world.finances.Money;
import freerails.world.finances.Transaction;
import freerails.world.player.FreerailsPrincipal;

/**
 * Adds up the value of transactions. Implements GoF Template Method pattern.
 * Subclasses that aggregate a monetary sum should only override the method
 * {@code condition(int)}; subclasses that aggregate a non-monetary sum
 * should override all 4 protected methods.
 */
public abstract class TransactionAggregator {

    /**
     *
     */
    protected final ReadOnlyWorld w;

    /**
     *
     */
    protected final FreerailsPrincipal principal;
    private final GameTime[] DEFAULT_INTERVAL = new GameTime[]{
            GameTime.BIG_BANG, GameTime.DOOMSDAY};

    /**
     *
     */
    protected Money[] monetaryTotals;

    /**
     *
     */
    protected int runningTotal = 0;
    private GameTime[] timeValues = DEFAULT_INTERVAL;

    /**
     * @param w
     * @param principal
     */
    public TransactionAggregator(ReadOnlyWorld w, FreerailsPrincipal principal) {
        this.w = w;
        this.principal = principal;
    }

    /**
     * @return
     */
    public GameTime[] getTimes() {
        // return defensive copy.
        return timeValues.clone();
    }

    /**
     * @param times
     */
    public void setTimes(GameTime[] times) {
        if (1 > times.length) {
            throw new IllegalArgumentException(
                    "There must be at least two values.");
        }

        timeValues = new GameTime[times.length];

        timeValues[0] = times[0]; // since we start counting at 1.

        for (int i = 1; i < times.length; i++) {
            if (times[i].getTicks() < times[i - 1].getTicks()) {
                throw new IllegalArgumentException("Time at index " + (i - 1)
                        + " > time at index " + i + ".");
            }

            timeValues[i] = times[i];
        }
    }

    /**
     * Returns the sum of the appropriate transactions. Do not override.
     *
     * @return
     */
    final public Money calculateValue() {
        Money[] values = calculateValues();

        return values[0];
    }

    /**
     * Returns the sum of the appropriate transactions up to (inclusive) each of
     * the specified times. Do not override.
     *
     * @return
     */
    final public Money[] calculateValues() {
        setTotalsArrayLength(timeValues.length - 1);

        int timeIndex = 0;
        int numberOfTransactions = w.getNumberOfTransactions(this.principal);
        setTotalsArrayLength(timeValues.length - 1);

        for (int i = 0; i < numberOfTransactions; i++) {
            GameTime time = w.getTransactionTimeStamp(principal, i);
            int transactionTime = time.getTicks();

            while (timeValues[timeIndex].getTicks() <= transactionTime) {
                storeTotalIfAppropriate(timeIndex);
                timeIndex++;

                if (timeIndex >= timeValues.length) {
                    /*
                     * The current transaction occurred after the last of the
                     * specified times.
                     */
                    return monetaryTotals;
                }
            }

            if (timeIndex > 0 && condition(i)) {
                incrementRunningTotal(i);
            }
        }

        /*
         * There are no more transactions and the last transaction occurred
         * before one or more of the specified times.
         */
        while (timeIndex < timeValues.length) {
            storeTotalIfAppropriate(timeIndex);
            timeIndex++;
        }

        return monetaryTotals;
    }

    private void storeTotalIfAppropriate(int timeIndex) {
        if (timeIndex > 0) {
            storeRunningTotal(timeIndex - 1);
        }
    }

    /**
     * Creates a new array with the specified length to store monetary totals
     * and sets the running total to zero. Subclasses that aggregate other
     * quantities should override this method and create the appropriate arrays.
     *
     * @param length
     */
    protected void setTotalsArrayLength(int length) {
        monetaryTotals = new Money[length];
        runningTotal = 0;
    }

    /**
     * @param transactionID
     */
    protected void incrementRunningTotal(int transactionID) {
        Transaction t = w.getTransaction(principal, transactionID);
        runningTotal += t.deltaCash().getAmount();
    }

    /**
     * Stores the current running total in the totals array at the specified
     * position.
     *
     * @param timeIndex
     */
    protected void storeRunningTotal(int timeIndex) {
        monetaryTotals[timeIndex] = new Money(runningTotal);
    }

    /**
     * Returns true if we should count the specified transactions.
     *
     * @param transactionID
     * @return
     */
    abstract protected boolean condition(int transactionID);
}