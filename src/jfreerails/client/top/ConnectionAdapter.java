package jfreerails.client.top;

import java.io.IOException;
import jfreerails.client.view.ModelRoot;
import jfreerails.controller.ConnectionToServer;
import jfreerails.controller.LocalConnection;
import jfreerails.controller.MoveReceiver;
import jfreerails.controller.UncommittedMoveReceiver;
import jfreerails.controller.UntriedMoveReceiver;
import jfreerails.move.Move;
import jfreerails.move.MoveStatus;
import jfreerails.move.TimeTickMove;
import jfreerails.move.WorldChangedEvent;
import jfreerails.util.GameModel;
import jfreerails.world.top.World;


/**
 * This class receives moves from the client. This class tries out moves on the
 * world if necessary, and passes them to the connection.
 */
public class ConnectionAdapter implements UntriedMoveReceiver {
    private NonAuthoritativeMoveExecuter moveExecuter;
    private ModelRoot modelRoot;
    ConnectionToServer connection;

    /**
     * we forward outbound moves from the client to this.
     */
    UncommittedMoveReceiver uncommittedReceiver;
    MoveReceiver moveReceiver;
    World world;

    public ConnectionAdapter(ModelRoot mr) {
        modelRoot = mr;
    }

    /**
     * This class receives moves from the connection and passes them on to a MoveReceiver.
     */
    public class WorldUpdater implements MoveReceiver {
        private MoveReceiver moveReceiver;

        /**
         * Processes inbound moves from the server
         */
        public void processMove(Move move) {
            if (move instanceof WorldChangedEvent) {
                try {
                    setConnection(connection);
                } catch (IOException e) {
                    modelRoot.getUserMessageLogger().println("Unable to open" +
                        " remote connection");
                    closeConnection();
                }
            } else if (move instanceof TimeTickMove) {
                /*
                 * flush our outgoing moves prior to receiving next tick
                 * TODO improve our buffering strategy
                 */
                connection.flush();
            }

            moveReceiver.processMove(move);
        }

        public void setMoveReceiver(MoveReceiver moveReceiver) {
            this.moveReceiver = moveReceiver;
        }
    }

    private WorldUpdater worldUpdater = new WorldUpdater();

    /**
     * Processes outbound moves to the server
     */
    public void processMove(Move move) {
        if (uncommittedReceiver != null) {
            uncommittedReceiver.processMove(move);
        }
    }

    public void undoLastMove() {
        if (uncommittedReceiver != null) {
            uncommittedReceiver.undoLastMove();
        }
    }

    public MoveStatus tryDoMove(Move move) {
        return move.tryDoMove(world);
    }

    public MoveStatus tryUndoMove(Move move) {
        return move.tryUndoMove(world);
    }

    private void closeConnection() {
        connection.close();
        connection.removeMoveReceiver(worldUpdater);
        modelRoot.getUserMessageLogger().println("Connection to server closed");
    }

    public synchronized void setConnection(ConnectionToServer c)
        throws IOException {
        if (connection != null) {
            closeConnection();
            connection.removeMoveReceiver(worldUpdater);
        }

        connection = c;
        connection.open();

        if (connection instanceof LocalConnection) {
            worldUpdater.setMoveReceiver(moveReceiver);
        }

        connection.addMoveReceiver(worldUpdater);
        world = connection.loadWorldFromServer();
        modelRoot.setWorld(world);

        //  if (!(connection instanceof LocalConnection)) {
        moveExecuter = new NonAuthoritativeMoveExecuter(world, moveReceiver,
                modelRoot);
        worldUpdater.setMoveReceiver(moveExecuter);
        uncommittedReceiver = moveExecuter.getUncommittedMoveReceiver();
        ((NonAuthoritativeMoveExecuter.PendingQueue)uncommittedReceiver).addMoveReceiver(connection);
        //  } else {
        //      uncommittedReceiver = connection;
        //  }
        // }
    }

    public void setMoveReceiver(MoveReceiver m) {
        //moveReceiver = new CompositeMoveSplitter(m);
        //I don't want moves split at this stage since I want to be able
        //to listen for composite moves.
        moveReceiver = m;
    }

    public GameModel getModel() {
        return this.moveExecuter;
    }
}