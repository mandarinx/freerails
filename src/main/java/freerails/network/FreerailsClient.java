/*
 * Created on Apr 17, 2004
 */
package freerails.network;

import freerails.controller.*;
import freerails.move.Move;
import freerails.move.MoveStatus;
import freerails.util.GameModel;
import freerails.world.common.FreerailsMutableSerializable;
import freerails.world.common.FreerailsSerializable;
import freerails.world.player.Player;
import freerails.world.top.World;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * A client for FreerailsGameServer.
 *
 * @author Luke
 */
public class FreerailsClient implements ClientControlInterface, GameModel,
        UntriedMoveReceiver, ServerCommandReceiver {
    private static final Logger logger = Logger.getLogger(FreerailsClient.class
            .getName());
    private final HashMap<String, Serializable> properties = new HashMap<>();
    private final MoveChainFork moveFork;

    /**
     *
     */
    protected Connection2Server connection2Server;
    private World world;

    private MovePrecommitter committer;

    /**
     *
     */
    public FreerailsClient() {
        moveFork = new MoveChainFork();
    }

    /**
     *
     * @return
     */
    public final MoveChainFork getMoveFork() {
        return moveFork;
    }

    /**
     * Connects this client to a remote server.
     * @param address
     * @param port
     * @param password
     * @param username
     * @return 
     */
    public final LogOnResponse connect(String address, int port,
                                       String username, String password) {
        if (logger.isDebugEnabled()) {
            logger.debug("Connect to remote server.  " + address + ":" + port);
        }

        try {
            connection2Server = new InetConnection2Server(address, port);
        } catch (IOException e) {
            return LogOnResponse.rejected(e.getMessage());
        }

        try {
            LogOnRequest request = new LogOnRequest(username, password);
            connection2Server.writeToServer(request);
            connection2Server.flush();

            return (LogOnResponse) connection2Server
                    .waitForObjectFromServer();
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
     * @param server
     * @param username
     * @param password
     * @return 
     */
    public final LogOnResponse connect(GameServer server, String username,
                                       String password) {
        try {
            LogOnRequest request = new LogOnRequest(username, password);
            connection2Server = new LocalConnection();
            connection2Server.writeToServer(request);
            server.addConnection((LocalConnection) connection2Server);

            return (LogOnResponse) connection2Server
                    .waitForObjectFromServer();
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
        world = (World) o;
        committer = new MovePrecommitter(world);
        newWorld(world);
    }

    /**
     * Subclasses should override this method if they need to respond the the
     * world being changed.
     * @param w
     */
    protected void newWorld(World w) {
    }

    public void setProperty(ClientProperty propertyName, Serializable value) {
        properties.put(propertyName.name(), value);
    }

    /**
     *
     * @param propertyName
     * @return
     */
    public final Serializable getProperty(ClientProperty propertyName) {
        return properties.get(propertyName.name());
    }

    /**
     *
     * @param newProperties
     */
    public final void resetProperties(HashMap newProperties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    final FreerailsSerializable read() {
        try {
            return this.connection2Server.waitForObjectFromServer();
        } catch (IOException | InterruptedException e) {
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

    /**
     * Reads and deals with all outstanding messages from the server.
     */
    final public void update() {
        try {
            FreerailsSerializable[] messages = connection2Server
                    .readFromServer();

            for (FreerailsSerializable message : messages) {
                processMessage(message);
            }

            connection2Server.flush();
            clientUpdates();
        } catch (IOException e) {
            ReportBugTextGenerator.unexpectedException(e);
        }
    }

    /**
     * Empty method called by update(), subclasses should override this method
     * instead of overriding update().
     */
    protected void clientUpdates() {

    }

    /**
     * Processes a message received from the server.
     */
    final void processMessage(FreerailsSerializable message) throws IOException {
        if (message instanceof Message2Client) {
            Message2Client request = (Message2Client) message;
            MessageStatus status = request.execute(this);
            if (logger.isDebugEnabled()) {
                logger.debug(request.toString());
            }
            connection2Server.writeToServer(status);
        } else if (message instanceof Move) {
            Move m = (Move) message;
            committer.fromServer(m);
            moveFork.processMove(m);
        } else if (message instanceof MoveStatus) {
            MoveStatus ms = (MoveStatus) message;
            committer.fromServer(ms);
        } else if (message instanceof PreMove) {
            PreMove pm = (PreMove) message;
            Move m = committer.fromServer(pm);
            moveFork.processMove(m);
        } else if (message instanceof PreMoveStatus) {
            PreMoveStatus pms = (PreMoveStatus) message;
            committer.fromServer(pms);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(message.toString());
            }
        }
    }

    /**
     *
     * @return
     */
    final public World getWorld() {
        return world;
    }

    /**
     * Sends move to the server.
     * @param move
     */
    final public void processMove(Move move) {
        committer.toServer(move);
        moveFork.processMove(move);
        write(move);
    }

    /**
     * Tests a move before sending it to the server.
     * @param move
     * @return 
     */
    final public MoveStatus tryDoMove(Move move) {
        return move.tryDoMove(world, Player.AUTHORITATIVE);
    }

    /**
     *
     * @param c
     */
    public void sendCommand(Message2Server c) {
        write(c);
    }

    /**
     *
     * @param pm
     */
    public void processPreMove(PreMove pm) {
        Move m = committer.toServer(pm);
        moveFork.processMove(m);
        write(pm);
    }

    /**
     *
     * @return
     */
    protected long getLastTickTime() {
        return moveFork.getLastTickTime();
    }
}