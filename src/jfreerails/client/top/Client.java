package jfreerails.client.top;

import jfreerails.controller.MoveChainFork;
import jfreerails.util.GameModel;
import jfreerails.world.top.ReadOnlyWorld;


/**
 * Represents an instance of a jfreerails client. It provides access to common
 * services which implementations make use of. Objects within the client
 * keep a reference to an instance of this object to access per-client objects.
 *
 * TODO currently the world held by the client is not updated by incoming moves
 * over the connection, so it only works with a local connection when we have a
 * reference to the servers copy.
 *
 */
public abstract class Client {
    private MoveChainFork moveChainFork;
    private ConnectionAdapter receiver;

    /**
     * @return A receiver with which moves may be tried out and submitted
     */

    //    public UntriedMoveReceiver getReceiver() {
    //        return receiver;
    //    }

    /**
     * @return A read-only copy of the world
     */
    public ReadOnlyWorld getWorld() {
        return getReceiver().world;
    }

    public GameModel getModel() {
        return this.getReceiver().getModel();
    }

    protected void setMoveChainFork(MoveChainFork moveChainFork) {
        this.moveChainFork = moveChainFork;
    }

    protected MoveChainFork getMoveChainFork() {
        return moveChainFork;
    }

    protected void setReceiver(ConnectionAdapter receiver) {
        this.receiver = receiver;
    }

    protected ConnectionAdapter getReceiver() {
        return receiver;
    }
}