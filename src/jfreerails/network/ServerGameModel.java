/*
 * Created on Sep 11, 2004
 *
 */
package jfreerails.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import jfreerails.util.GameModel;
import jfreerails.world.top.World;


/**
 * @author Luke
 *
 */
public interface ServerGameModel extends GameModel, Serializable {
    void setWorld(World w);

    World getWorld();

    void init(MoveReceiver moveExecuter);

    void write(ObjectOutputStream objectOut) throws IOException;
}