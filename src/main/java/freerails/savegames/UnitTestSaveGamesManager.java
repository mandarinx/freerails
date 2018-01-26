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

package freerails.savegames;

import freerails.server.gamemodel.ServerGameModel;
import freerails.util.Utils;
import freerails.world.FullWorld;
import freerails.world.World;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores saved games in memory rather than on disk.
 */
public class UnitTestSaveGamesManager implements SaveGamesManager {

    private final String[] mapsAvailable = {"map1", "map2"};
    private final Map<String, Serializable> savedGames = new HashMap<>();

    /**
     * @return
     */
    public String[] getSaveGameNames() {
        return savedGames.keySet().toArray(new String[0]);
    }

    /**
     * @return
     */
    public String[] getNewMapNames() {
        return mapsAvailable.clone();
    }

    /**
     * @param s
     * @param w
     * @throws IOException
     */
    public void saveGame(String s, Serializable w) throws IOException {
        // Make a copy so that the saved version's state cannot be changed.
        Serializable copy = Utils.cloneBySerialisation(w);
        savedGames.put(s, copy);
    }

    /**
     * @param name
     * @return
     * @throws IOException
     */
    public ServerGameModel loadGame(String name) throws IOException {
        Serializable o = savedGames.get(name);
        return (ServerGameModel) Utils.cloneBySerialisation(o);
    }

    /**
     * @param name
     * @return
     * @throws IOException
     */
    public World newMap(String name) throws IOException {
        return new FullWorld(10, 10);
    }
}