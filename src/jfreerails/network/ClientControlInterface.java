/*
 * Created on Apr 14, 2004
 */
package jfreerails.network;

import java.io.Serializable;
import jfreerails.world.common.FreerailsMutableSerializable;


/**
 *  Defines the methods that the server can call on a client using a Message2Client.
 * @see Message2Client
 *  @author Luke
 *
 */
public interface ClientControlInterface {
    public static final String CONNECTED_CLIENTS = "CONNECTED_CLIENTS";
    public static final String MAPS_AVAILABLE = "MAPS_AVAILABLE";
    public static final String SAVED_GAMES = "SAVED_GAMES";

    /** Called when a new game is started or a game is loaded.*/
    void setGameModel(FreerailsMutableSerializable world);

    /** Sets a property, for example, the list of saved games.*/
    void setProperty(String propertyName, Serializable value);
}