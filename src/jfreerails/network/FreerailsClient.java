/*
 * Created on Apr 17, 2004
 */
package jfreerails.network;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Logger;
import jfreerails.move.Move;
import jfreerails.move.MoveStatus;
import jfreerails.util.GameModel;
import jfreerails.world.common.FreerailsMutableSerializable;
import jfreerails.world.common.FreerailsSerializable;
import jfreerails.world.player.Player;
import jfreerails.world.top.World;


/**
 *  A client for FreerailsGameServer.
 *  @author Luke
 *
 */
public class FreerailsClient implements ClientControlInterface, GameModel,
    UntriedMoveReceiver, ServerCommandReceiver {
    private static final Logger logger = Logger.getLogger(FreerailsClient.class.getName());
    protected Connection2Server connection2Server;
    private final HashMap properties = new HashMap();
    private final MoveChainFork moveFork;
    private World world;
    private MovePrecommitter committer;

    public FreerailsClient() {
        moveFork = new MoveChainFork();
    }

    public final MoveChainFork getMoveFork() {
        return moveFork;
    }

    /**
     * Connects this client to a remote server.
     */
    public final LogOnResponse connect(String address, int port,
        String username, String password) {
        logger.fine("Connect to remote server.  " + address + ":" + port);

        try {
            connection2Server = new InetConnection2Server(address, port);

            LogOnRequest request = new LogOnRequest(username, password);
            connection2Server.writeToServer(request);
            connection2Server.flush();

            LogOnResponse response = (LogOnResponse)connection2Server.waitForObjectFromServer();

            return response;
        } catch (Exception e) {
            try {
                connection2Server.disconnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return LogOnResponse.rejected(e.getMessage());
        }
    }

    /**
     * Connects this client to a local server.
     */
    public final LogOnResponse connect(NewGameServer server, String username,
        String password) {
        try {
            LogOnRequest request = new LogOnRequest(username, password);
            connection2Server = new NewLocalConnection();
            connection2Server.writeToServer(request);
            server.addConnection((NewLocalConnection)connection2Server);

            LogOnResponse response = (LogOnResponse)connection2Server.waitForObjectFromServer();

            return response;
        } catch (Exception e) {
            try {
                connection2Server.disconnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return LogOnResponse.rejected(e.getMessage());
        }
    }

    /**
     * Disconnect the client from the server.
     */
    public final void disconnect() {
        try {
            connection2Server.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void setGameModel(FreerailsMutableSerializable o) {
        world = (World)o;
        committer = new MovePrecommitter(world);
        newWorld(world);
    }

    /** Subclasses should override this method if they need to respond the the world
     * being changed.
     */
    protected void newWorld(World w) {
    }

    public final void setProperty(String propertyName, Serializable value) {
        properties.put(propertyName, value);
    }

    public final Serializable getProperty(String propertyName) {
        return (Serializable)properties.get(propertyName);
    }

    public final void resetProperties(HashMap newProperties) {
        // TODO Auto-generated method stub
    }

    public final void showMenu() {
        // TODO Auto-generated method stub
    }

    final FreerailsSerializable read() {
        try {
            return this.connection2Server.waitForObjectFromServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        throw new IllegalStateException();
    }

    final void write(FreerailsSerializable fs) {
        try {
            connection2Server.writeToServer(fs);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    /** Reads and deals with all outstanding messages from the server.*/
    final public void update() {
        try {
            FreerailsSerializable[] messages = connection2Server.readFromServer();

            for (int i = 0; i < messages.length; i++) {
                FreerailsSerializable message = messages[i];
                processMessage(message);
            }

            connection2Server.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** Processes a message received from the server.*/
    final void processMessage(FreerailsSerializable message)
        throws IOException {
        if (message instanceof ClientCommand) {
            ClientCommand command = (ClientCommand)message;
            CommandStatus status = command.execute(this);
            logger.fine(command.toString());
            connection2Server.writeToServer(status);
        } else if (message instanceof Move) {
            Move m = (Move)message;
            committer.fromServer(m);
            moveFork.processMove(m);
        } else if (message instanceof MoveStatus) {
            MoveStatus ms = (MoveStatus)message;
            committer.fromServer(ms);
        } else {
            logger.fine(message.toString());
        }
    }

    final public World getWorld() {
        return world;
    }

    /** Sends move to the server.*/
    final public void processMove(Move move) {
        committer.toServer(move);
        moveFork.processMove(move);
        write(move);
    }

    /** Tests a move before sending it to the server.*/
    final public MoveStatus tryDoMove(Move move) {
        return move.tryDoMove(world, Player.AUTHORITATIVE);
    }

    public void sendCommand(ServerCommand c) {
        write(c);
    }
}