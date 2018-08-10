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

package freerails.model.game;

import java.io.Serializable;

// TODO serialize to json and load/define default, no restriction rules through files, better naming
/**
 * Stores rules governing what players are allowed to do, for example whether
 * they can connect their track to the track of other players.
 */
public class GameRules implements Serializable {

    private static final long serialVersionUID = 3258125847557978416L;
    public static final GameRules DEFAULT_RULES = new GameRules(true, false);
    public static final GameRules NO_RESTRICTIONS = new GameRules(false, true);
    private final boolean canConnectToOtherRRTrack;
    private final boolean mustConnectToExistingTrack;

    private GameRules(boolean mustConnect, boolean canConnectToOthers) {
        canConnectToOtherRRTrack = canConnectToOthers;
        mustConnectToExistingTrack = mustConnect;
    }

    @Override
    public int hashCode() {
        int result;
        result = (canConnectToOtherRRTrack ? 1 : 0);
        result = 29 * result + (mustConnectToExistingTrack ? 1 : 0);

        return result;
    }

    /**
     * @return
     */
    public synchronized boolean canConnectToOtherRRTrack() {
        return canConnectToOtherRRTrack;
    }

    /**
     * @return
     */
    public synchronized boolean mustConnectToExistingTrack() {
        return mustConnectToExistingTrack;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameRules)) {
            return false;
        }

        GameRules test = (GameRules) obj;

        return canConnectToOtherRRTrack == test.canConnectToOtherRRTrack && mustConnectToExistingTrack == test.mustConnectToExistingTrack;
    }
}