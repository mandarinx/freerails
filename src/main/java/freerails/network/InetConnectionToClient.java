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

package freerails.network;

import freerails.world.FreerailsSerializable;

import java.io.IOException;
import java.net.Socket;

/**
 * Lets the server send messages to a client over the Internet.
 *
 */
public class InetConnectionToClient extends AbstractInetConnection implements
        ConnectionToClient {

    /**
     *
     * @param s
     * @throws IOException
     */
    public InetConnectionToClient(Socket s) throws IOException {
        super(s);
    }

    public FreerailsSerializable[] readFromClient() throws IOException {
        return read();
    }

    public FreerailsSerializable waitForObjectFromClient() throws IOException,
            InterruptedException {
        return waitForObject();
    }

    public void writeToClient(FreerailsSerializable object) throws IOException {
        send(object);
    }

    @Override
    String getThreadName() {
        return "InetConnectionToClient";
    }
}