package jfreerails.client.top;

import java.awt.DisplayMode;
import java.io.IOException;
import java.net.InetAddress;
import javax.swing.JFrame;
import jfreerails.client.common.ScreenHandler;
import jfreerails.client.common.SynchronizedEventQueue;
import jfreerails.client.renderer.ViewLists;
import jfreerails.client.view.ModelRoot;
import jfreerails.controller.ConnectionToServer;
import jfreerails.controller.InetConnection;
import jfreerails.controller.LocalConnection;
import jfreerails.controller.MoveChainFork;
import jfreerails.controller.ServerControlInterface;
import jfreerails.util.FreerailsProgressMonitor;


/**
 * This class implements a GUI-driven client to be used by human players.
 *
 * XXX How should the server be controlled from the client? (loading, saving of
 * maps etc?). Currently we will do this over the local connection only, by
 * the client having access to a ServerControlInterface object
 */
public class GUIClient extends Client {
    private GUIComponentFactoryImpl gUIComponentFactory;
    private ModelRoot modelRoot;

    private GUIClient(ConnectionToServer server, int mode, DisplayMode dm,
        String title, FreerailsProgressMonitor pm) throws IOException {
        SynchronizedEventQueue.use();

        modelRoot = new ModelRoot();
        super.setReceiver(new ConnectionAdapter(modelRoot));
        super.setMoveChainFork(new MoveChainFork());
        getReceiver().setMoveReceiver(getMoveChainFork());
        getReceiver().setConnection(server);

        modelRoot.setMoveReceiver(getReceiver());
        modelRoot.setMoveFork(super.getMoveChainFork());

        GUIComponentFactoryImpl gUIComponentFactory = new GUIComponentFactoryImpl(modelRoot);

        ViewLists viewLists = new ViewListsImpl(getReceiver().world, pm);

        if (!viewLists.validate(getReceiver().world)) {
            throw new IllegalArgumentException();
        }

        gUIComponentFactory.setup(viewLists, getReceiver().world);

        JFrame client = gUIComponentFactory.createClientJFrame(title);

        //We want to setup the screen handler before creating the view lists since the 
        //ViewListsImpl creates images that are compatible with the current display settings 
        //and the screen handler may change the display settings.
        ScreenHandler screenHandler = new ScreenHandler(client, mode, dm);

        getMoveChainFork().add(gUIComponentFactory);

        GameLoop gameLoop = new GameLoop(screenHandler, this.getModel());
        String threadName = "JFreerails client: " + title;
        Thread t = new Thread(gameLoop, threadName);
        t.start();
    }

    /**
     * Start a client with an internet connection to a server
     */
    public GUIClient(InetAddress server, int mode, DisplayMode dm,
        String title, FreerailsProgressMonitor pm) throws IOException {
        this(new InetConnection(server), mode, dm, title, pm);
    }

    /**
     * sets up a connnection with a local server. Currently this is the only
     * form of connection supported.
     * @throws java.io.IOException if the connection could not be opened
     */
    public GUIClient(ServerControlInterface controls, LocalConnection server,
        int mode, DisplayMode dm, String title, FreerailsProgressMonitor pm)
        throws IOException {
        this((ConnectionToServer)new LocalConnection(server), mode, dm, title,
            pm);
        this.modelRoot.setServerControls(controls);
    }

    public ModelRoot getModelRoot() {
        return modelRoot;
    }
}