package jfreerails.controller.net;

import java.io.IOException;
import java.net.Socket;
import jfreerails.world.common.FreerailsSerializable;


/**
 *
 *  @author Luke
 *
 */
public class InetConnection2Client extends AbstractInetConnection
    implements Connection2Client {
    public InetConnection2Client(Socket s) throws IOException {
        super(s);
    }

    public FreerailsSerializable[] readFromClient() throws IOException {
        return read();
    }

    public FreerailsSerializable waitForObjectFromClient()
        throws IOException, InterruptedException {
        return waitForObject();
    }

    public void writeToClient(FreerailsSerializable object)
        throws IOException {
        send(object);
    }

    String getThreadName() {
        return "InetConnection2Client";
    }
}