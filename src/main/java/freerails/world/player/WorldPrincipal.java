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

package freerails.world.player;

/**
 * A FreerailsPrincipal that is not a player.
 */
class WorldPrincipal extends FreerailsPrincipal {

    private static final long serialVersionUID = -5498947120662423937L;
    private final String principalName;

    public WorldPrincipal(String name) {
        super(-1);
        this.principalName = name;
    }

    public String getName() {
        return principalName;
    }

    @Override
    public String toString() {
        return principalName;
    }

    @Override
    public int hashCode() {
        return principalName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WorldPrincipal && (principalName.equals(((WorldPrincipal) o).principalName));

    }
}