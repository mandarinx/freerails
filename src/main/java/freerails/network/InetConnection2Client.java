package freerails.network;

import freerails.world.common.FreerailsSerializable;

import java.io.IOException;
import java.net.Socket;

/**
 * Lets the server send messages to a client over the Internet.
 *
 * @author Luke
 */
public class InetConnection2Client extends AbstractInetConnection implements
        Connection2Client {

    /**
     *
     * @param s
     * @throws IOException
     */
    public InetConnection2Client(Socket s) throws IOException {
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
        return "InetConnection2Client";
    }
}