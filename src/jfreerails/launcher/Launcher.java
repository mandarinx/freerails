/*
 * Launcher.java
 *
 * Created on 20 December 2003, 16:05
 */

package jfreerails.launcher;

import java.awt.CardLayout;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import jfreerails.client.common.FileUtils;
import jfreerails.client.common.ScreenHandler;
import jfreerails.client.top.GameLoop;
import jfreerails.network.FreerailsGameServer;
import jfreerails.network.InetConnectionAccepter;
import jfreerails.network.SavedGamesManager;
import jfreerails.network.ServerControlInterface;
import jfreerails.server.SavedGamesManagerImpl;
import jfreerails.server.ServerGameModelImpl;
import jfreerails.util.FreerailsProgressMonitor;
import jfreerails.util.GameModel;
import jfreerails.world.player.Player;

/**
 * Launcher GUI for both the server and/or client.
 *
 * TODO The code in the switch statements needs reviewing.
 *
 * @author rtuck99@users.sourceforge.net
 * @author Luke
 */
public class Launcher extends javax.swing.JFrame implements
FreerailsProgressMonitor, LauncherInterface {
    private static final Logger logger = Logger.getLogger(Launcher.class.getName());
    private static String QUICKSTART = "-quickstart";
    private static final int GAME_SPEED_SLOW = 10;
    private final Component[] wizardPages = new Component[4];
    private int currentPage = 0;
    private FreerailsGameServer server;
    private GUIClient client;
    
    
    public void setMessage(String s) {
        setInfoText(s);
    }
    
    public void setValue(int i) {
        jProgressBar1.setValue(i);
        
    }
    
    public void setMax(int max) {
        jProgressBar1.setMaximum(max);
        
    }
    
    public void setInfoText(String text) {
        infoLabel.setText(text);
        
    }
    
    public void setNextEnabled(boolean enabled) {
        nextButton.setEnabled(enabled);
        if (nextIsStart) {
            nextButton.setText("Start");
        } else {
            nextButton.setText("Next...");
        }
    }
    
    private boolean nextIsStart = false;
    
    
    
    private void startGame() {
        jProgressBar1.setVisible(true);
        setNextEnabled(false);
        LauncherPanel1 lp = (LauncherPanel1) wizardPages[0];
        MapSelectionPanel msp = (MapSelectionPanel) wizardPages[1];
        ClientOptionsJPanel cop = (ClientOptionsJPanel) wizardPages[2];
        ConnectedPlayersJPanel cp = (ConnectedPlayersJPanel) wizardPages[3];
        
        boolean recover = false;
        int mode;
               
        
        Player p;
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        
        
        switch (lp.getMode()) {
            case LauncherPanel1.MODE_SINGLE_PLAYER:
                try {
                    
                    mode = cop.getScreenMode();
                                        
                    client = new GUIClient(cop.getPlayerName(), this, mode, cop.getDisplayMode());
                    server = initServer();
                    client.connect(server, cop.getPlayerName(), "password");
                    
                    setServerGameModel();                                                            
                } catch (IOException e) {
                    setInfoText(e.getMessage());
                    recover = true;
                } finally {
                    if (recover) {                       
                        cop.setControlsEnabled(true);
                        prevButton.setEnabled(true);
                        setNextEnabled(true);
                        return;
                    }
                }
                startThread(server, client);
                break;
            case LauncherPanel1.MODE_START_NETWORK_GAME:              
                setServerGameModel();  
                currentPage = 3;
                String[] playerNames = server.getPlayerNames();
                playerNames = playerNames.length == 0 ? new String[]{"No players are connected."}:playerNames;
                cp.setListOfPlayers(playerNames);
                cl.show(jPanel1, "3");
                setNextEnabled(false);
               
                break;
            case LauncherPanel1.MODE_JOIN_NETWORK_GAME:
                mode = cop.getScreenMode();
                try {
               
                    InetSocketAddress serverInetAddress = cop.getRemoteServerAddress();
                    if(null == serverInetAddress){
                        throw new NullPointerException("Couldn't resolve hostname.");
                    }
                    String playerName = cop.getPlayerName();
                    client = new GUIClient(playerName, this, mode, cop.getDisplayMode());
                    
                    String hostname = serverInetAddress.getHostName();
                    int port = serverInetAddress.getPort();
                    client.connect(hostname, port, playerName, "password");
                    startThread(client);
                } catch (IOException e) {
                    setInfoText(e.getMessage());
                    recover = true;
                } catch (NullPointerException e) {
                    setInfoText(e.getMessage());
                    recover = true;
                }
                finally {
                    if (recover) {
                        cop.setControlsEnabled(true);
                        prevButton.setEnabled(true);
                        setNextEnabled(true);
                        return;
                    }
                }
               
                break;
            case LauncherPanel1.MODE_SERVER_ONLY:
                if(msp.validatePort()){
                    server = initServer();
                    try {                        
                        setServerGameModel();  
                        
                        prepare2HostNetworkGame(msp.getServerPort());
                        setNextEnabled(true);                        
                    } catch (NullPointerException e) {
                        setInfoText(e.getMessage());
                        recover = true;
                    }catch (IOException e) {
                        setInfoText(e.getMessage());
                        recover = true;
                    }
                    finally {
                        if (recover) {
                            cop.setControlsEnabled(true);
                            prevButton.setEnabled(true);
                            setNextEnabled(true);
                            return;
                        }
                    }
               
                }
        }//End of switch statement                              
    }
    
    private void setServerGameModel() {
        MapSelectionPanel msp2 = (MapSelectionPanel) wizardPages[1];
        if (msp2.getMapAction() == MapSelectionPanel.START_NEW_MAP) {                       
            server.newGame(msp2.getMapName());
        } else {
            server.loadgame(ServerControlInterface.FREERAILS_SAV);
            
        }
    }

    /** Starts the client and server in the same thread.*/
    private static void startThread(final FreerailsGameServer server, final GUIClient client) {
        Runnable run = new Runnable(){
            
            public void run() {
                while(null == client.getWorld()){
                    client.update();
                    server.update();
                }
                
                GameModel[] models= new GameModel[] {client, server};                
                ScreenHandler screenHandler = client.getScreenHandler();                
                GameLoop gameLoop = new GameLoop(screenHandler, models);
                screenHandler.apply();
                
                gameLoop.run();
            }
            
        };
        Thread t = new Thread(run, "Client + server main loop");
        t.start();
    }
    
    /** Starts the client in a new thread.*/
    private static void startThread(final GUIClient client) {
        Runnable run = new Runnable(){
            
            public void run() {
                while(null == client.getWorld()){
                    client.update();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        //do nothing
                    }
                }
                
                GameModel[] models= new GameModel[] {client};                
                ScreenHandler screenHandler = client.getScreenHandler();                
                GameLoop gameLoop = new GameLoop(screenHandler, models);
                screenHandler.apply();
                
                gameLoop.run();
            }
            
        };
        Thread t = new Thread(run, "Client main loop");
        t.start();
    }
    
    /** Starts the server in a new thread.*/
    private static void startThread(final FreerailsGameServer server) {
        
        Runnable r = new Runnable(){

            public void run() {
                
               while(true){
                   long startTime = System.currentTimeMillis();
                   server.update();
                   long deltatime = System.currentTimeMillis() - startTime;
                   if(deltatime < 20){ 
                       try {
                        Thread.sleep(20 - deltatime);
                    } catch (InterruptedException e) {
                       //do nothing.
                    }
                   }
               }
                
            }
            
        };
        
        Thread t = new Thread(r, "FreerailsGameServer");
        t.start();
    }
    
    private FreerailsGameServer initServer() {
        SavedGamesManager gamesManager = new SavedGamesManagerImpl();
        FreerailsGameServer server = new FreerailsGameServer(gamesManager);
        ServerGameModelImpl serverGameModel = new ServerGameModelImpl();
        server.setServerGameModel(serverGameModel);
        
        /* Set the server field on the connected players panel so that
         * it can keep track of who is connected.
         */        
        ConnectedPlayersJPanel cp = (ConnectedPlayersJPanel) wizardPages[3];
        cp.server = server;
        
        return server;
    }
    
    private Player getPlayer(String name) throws IOException {
        Player p;
        
        try {
            FileInputStream fis =
            FileUtils.openForReading(FileUtils.DATA_TYPE_PLAYER_SPECIFIC,
            name, "keyPair");
            setInfoText("Loading saved player keys");
            ObjectInputStream ois = new ObjectInputStream(fis);
            p = (Player) ois.readObject();
            p.loadSession(ois);
        } catch (FileNotFoundException e) {
            p = new Player(name);
            // save both public and private key for future use
            FileOutputStream fos =
            FileUtils.openForWriting(FileUtils.DATA_TYPE_PLAYER_SPECIFIC,
            name, "keyPair");
            setInfoText("Saving player keys");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(p);
            p.saveSession(oos);
        } catch (ClassNotFoundException e) {
            setInfoText("Player KeyPair was corrupted!");
            throw new IOException(e.getMessage());
        } catch (IOException e) {
            setInfoText("Player KeyPair was corrupted!");
            throw e;
        }
        return p;
    }
    
    /**
     * Runs the game.
     */
    public static void main(String args[]) {
        
        //Let the user know if we are using a custom logging config.
        String loggingProperties = System.getProperty("java.util.logging.config.file");
        if(null != loggingProperties){
            logger.info("Logging properties file: " +loggingProperties);
        }
        
        logger.fine("Started launcher.");
        boolean quickstart = false;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (QUICKSTART.equals(args[i]))
                    quickstart = true;
            }
            
        }
        Launcher launcher = new Launcher(quickstart);
        launcher.start(quickstart);
    }
    
    /**
     * Shows GUI. If <code>quickstart</code> is <code>true</code> runs the game.
     * @param quickstart boolean
     */
    public void start(boolean quickstart) {
    	setVisible(true);
        if (quickstart) {
            startGame();           
        }
    }
    
    /** Starts a thread listening for new connections.*/
    private void prepare2HostNetworkGame(int port) throws IOException{
        server = initServer();
        InetConnectionAccepter accepter = new InetConnectionAccepter(port, server);
        /* Note, the thread's name gets set in the run method so there is no point setting it here.*/
        Thread t = new Thread(accepter);
        t.start();
        
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "3");
        currentPage = 3;
    }
    
    public Launcher(boolean quickstart) {
        initComponents();
        
        wizardPages[0] = new LauncherPanel1(this);
        wizardPages[1] = new MapSelectionPanel(this);
        wizardPages[2] = new ClientOptionsJPanel(this);
        wizardPages[3] = new ConnectedPlayersJPanel();
        
        if (!quickstart) {
            jPanel1.add(wizardPages[0], "0");
            jPanel1.add(wizardPages[1], "1");
            jPanel1.add(wizardPages[2], "2");
            jPanel1.add(wizardPages[3], "3");
            pack();
            /* hide the progress bar until needed */
            jProgressBar1.setVisible(false);
        } else {
            prevButton.setVisible(false);
            nextButton.setVisible(false);
            pack();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        nextButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        prevButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("JFreerails Launcher");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setLayout(new java.awt.CardLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(420, 300));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jSeparator1, gridBagConstraints);

        nextButton.setText("Next...");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(nextButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jProgressBar1, gridBagConstraints);

        prevButton.setText("Back...");
        prevButton.setEnabled(false);
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(prevButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(infoLabel, gridBagConstraints);

        pack();
    }//GEN-END:initComponents
    
    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        nextIsStart = false;
        switch (currentPage) {
            case 1:
                cl.previous(jPanel1);
                currentPage--;
                prevButton.setEnabled(false);
                break;
            case 2:
                LauncherPanel1 panel = (LauncherPanel1) wizardPages[0];
                if (panel.getMode() == LauncherPanel1.MODE_JOIN_NETWORK_GAME) {
                    currentPage = 0;
                    cl.show(jPanel1, "0");
                    prevButton.setEnabled(false);
                } else {
                    currentPage--;
                    cl.previous(jPanel1);
                }
        }
    }//GEN-LAST:event_prevButtonActionPerformed
    
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        try{
            CardLayout cl = (CardLayout) jPanel1.getLayout();
            LauncherPanel1 panel = (LauncherPanel1) wizardPages[0];
            MapSelectionPanel msp = (MapSelectionPanel) wizardPages[1];
            ClientOptionsJPanel cop = (ClientOptionsJPanel) wizardPages[2];

            
            
            switch (currentPage) {
                case 0:
                    /* Initial game selection page */
                    switch (panel.getMode()) {
                        case LauncherPanel1.MODE_SERVER_ONLY:
                            /* go to map selection screen */
                            cl.next(jPanel1);
                            msp.setServerPortPanelVisible(true);
                            
                            currentPage++;
                            break;
                        case LauncherPanel1.MODE_SINGLE_PLAYER:
                            /* go to map selection screen */
                            cl.next(jPanel1);
                            msp.setServerPortPanelVisible(false);
                            cop.setRemoteServerPanelVisible(false);
                            currentPage++;
                            break;
                        case LauncherPanel1.MODE_START_NETWORK_GAME:
                            /* go to map selection screen */
                            msp.setServerPortPanelVisible(true);
                            cop.setRemoteServerPanelVisible(false);
                            cl.next(jPanel1);
                            currentPage++;
                            break;
                        case LauncherPanel1.MODE_JOIN_NETWORK_GAME:
                            /* client display options */
                            nextIsStart = true;
                            cl.show(jPanel1, "2");
                            currentPage = 2;
                            msp.setServerPortPanelVisible(false);
                            cop.setRemoteServerPanelVisible(true);
                            break;
                    }
                    prevButton.setEnabled(true);
                    break;
                case 1:
                    /* map selection page */
                    if (panel.getMode() == LauncherPanel1.MODE_SERVER_ONLY) {
                        if(msp.validatePort()){
                            prevButton.setEnabled(false);
                            prepare2HostNetworkGame(msp.getServerPort());  
                        }
                    } else {
                        nextIsStart = true;
                        prevButton.setEnabled(true);
                        setNextEnabled(true);
                        currentPage++;
                        cl.next(jPanel1);
                    }
                    break;
                case 2:
                    /* display mode selection */
                    if (panel.getMode() == LauncherPanel1.MODE_START_NETWORK_GAME) {
                        if(msp.validatePort()){
                            prevButton.setEnabled(false);
                            int mode = cop.getScreenMode();                                        
                                                                                   
                            prepare2HostNetworkGame(msp.getServerPort());
                            client = new GUIClient(cop.getPlayerName(), this, mode, cop.getDisplayMode()); 
                            client.connect(server, cop.getPlayerName(), "password");
                        }
                    }else{
                    
                        prevButton.setEnabled(false);
                        cop.setControlsEnabled(false);
                        startGame();
                    }
                    break;
                case 3:
                    /* Connection status screen */
                    prevButton.setEnabled(false);
                    setServerGameModel();  
                    if (panel.getMode() == LauncherPanel1.MODE_START_NETWORK_GAME) {
                    
                        startThread(server, client);
                    }else{
                        /*Start a stand alone server.*/
                        startThread(server);
                        setVisible(false);                        
                    }
                    setNextEnabled(false);
                    break;
                default:
                    throw new IllegalArgumentException(String.valueOf(currentPage));
            }
        }catch (Exception e){
            logger.severe("Unexpected exception, can't recover");
            e.printStackTrace();
            System.exit(1);
        }
    }//GEN-LAST:event_nextButtonActionPerformed
    
    /** Exit the Application. */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel infoLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    // End of variables declaration//GEN-END:variables
    
    public void finished() {
    	setVisible(false);
        
    }
    
}
