/*
 * SelectStationJPanel.java
 *
 * Created on 06 February 2004, 16:34
 */

package jfreerails.client.view;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.NoSuchElementException;

import jfreerails.world.common.OneTileMoveVector;
import jfreerails.world.player.FreerailsPrincipal;
import jfreerails.world.station.StationModel;
import jfreerails.world.top.KEY;
import jfreerails.world.top.NonNullElements;
import jfreerails.world.top.ReadOnlyWorld;
import jfreerails.world.track.FreerailsTile;
import jfreerails.world.track.NullTrackPiece;
import jfreerails.world.train.Schedule;
import jfreerails.world.train.TrainModel;
import jfreerails.world.train.TrainOrdersModel;
/**
 * This JPanel lets the user select a stations from a map and add it to a train
 *  schedule.
 *
 * @author  Luke
 */
public class SelectStationJPanel extends javax.swing.JPanel implements View {
    
    private ReadOnlyWorld world;

    private ActionListener submitButtonCallBack;
    
    private int selectedStationID = 0;
    
    private int selectedOrderNumber = 1;
    
    private int trainID = 0;
    
    private Rectangle mapRect = new Rectangle();
    
    private Rectangle visableMapTiles = new Rectangle();
    
    private double scale = 1;
    
    private boolean needsUpdating = true;
    
    private FreerailsPrincipal principal;
    
    /** Creates new form SelectStationJPanel */
    public SelectStationJPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        cargoWaitingAndDemandedJPanel1 = new jfreerails.client.view.CargoWaitingAndDemandedJPanel();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 350));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });

        cargoWaitingAndDemandedJPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        cargoWaitingAndDemandedJPanel1.setPreferredSize(new java.awt.Dimension(150, 300));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(cargoWaitingAndDemandedJPanel1, gridBagConstraints);

        jLabel1.setText("Train #1 Stop 1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(jLabel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        setZoom();
    }//GEN-LAST:event_formComponentShown
    
    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        formMouseMoved(evt);
        needsUpdating = true;
        this.submitButtonCallBack.actionPerformed(null);
    }//GEN-LAST:event_formMouseClicked
    
    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        try{
            int lastSelectedStationId = this.selectedStationID;
            OneTileMoveVector v = OneTileMoveVector.getInstanceMappedToKey(evt.getKeyCode());
            //now find nearest station in direction of the vector.
            NearestStationFinder stationFinder = new NearestStationFinder(this.world, this.principal);
            int station = stationFinder.findNearestStationInDirection(this.selectedStationID, v);
            
            if(selectedStationID != station && station != NearestStationFinder.NOT_FOUND){
                selectedStationID = station;
                cargoWaitingAndDemandedJPanel1.display(selectedStationID);
                this.validate();
                this.repaint();
            }
        }catch (NoSuchElementException e){
            if(evt.getKeyCode() == KeyEvent.VK_ENTER){
                needsUpdating = true;
                submitButtonCallBack.actionPerformed(null);
            }
            //The key pressed isn't mapped to a OneTileMoveVector so do nothing.
        }
    }//GEN-LAST:event_formKeyPressed
    
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        setZoom();
    }//GEN-LAST:event_formComponentResized
    
    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // Add your handling code here:
        double x = evt.getX();
        x = x / scale + visableMapTiles.x;
        double y = evt.getY();
        y = y /scale +  visableMapTiles.y;
        
        NearestStationFinder stationFinder = new NearestStationFinder(this.world, this.principal);
        int station = stationFinder.findNearestStation((int)x, (int)y);
        
        if(selectedStationID != station && station != NearestStationFinder.NOT_FOUND){
            selectedStationID = station;
            cargoWaitingAndDemandedJPanel1.display(selectedStationID);
            this.validate();
            this.repaint();
            
        }
    }//GEN-LAST:event_formMouseMoved
    
    public void display(int newTrainID, int orderNumber){
        this.trainID = newTrainID;
        this.selectedOrderNumber = orderNumber;
        
        //Set the selected station to the current station for the specified order.
        TrainModel train = (TrainModel)world.get(KEY.TRAINS, this.trainID, this.principal);
        Schedule schedule = (Schedule)world.get(KEY.TRAIN_SCHEDULES, train.getScheduleID(), this.principal);
        TrainOrdersModel order = schedule.getOrder(selectedOrderNumber);
        this.selectedStationID = order.getStationNumber();
        
        //Set the text on the title JLabel.
        this.jLabel1.setText("Train #"+String.valueOf(trainID+1)+" Stop "+String.valueOf(selectedOrderNumber+1));
        
        //Set the station info panel to show the current selected station.
        cargoWaitingAndDemandedJPanel1.display(selectedStationID);
    }
    
    /** Sets the zoom based on the size of the component and the positions of the stations. */
    private void setZoom(){
        mapRect = this.getBounds();
        Rectangle r = cargoWaitingAndDemandedJPanel1.getBounds();
        mapRect.width -= r.width;
        
        int topLeftX = Integer.MAX_VALUE;
        int topLeftY = Integer.MAX_VALUE;
        int bottomRightX = Integer.MIN_VALUE;
        int bottomRightY = Integer.MIN_VALUE;
        
        
        NonNullElements it = new NonNullElements(KEY.STATIONS, world, this.principal);
        while(it.next()){
            StationModel station = (StationModel)it.getElement();
            if(station.x < topLeftX) topLeftX = station.x;
            if(station.y < topLeftY) topLeftY = station.y;
            if(station.x > bottomRightX) bottomRightX = station.x;
            if(station.y > bottomRightY) bottomRightY = station.y;
        }
        //Add some padding.
        topLeftX -= 10;
        topLeftY -= 10;
        bottomRightX += 10;
        bottomRightY += 10;
        
        int width = bottomRightX - topLeftX;
        int height = bottomRightY - topLeftY;
        visableMapTiles = new Rectangle(topLeftX, topLeftY, width, height);
        boolean heightConstraintBinds = (visableMapTiles.getHeight() / visableMapTiles.getWidth() ) > (mapRect.getHeight() / mapRect.getWidth() );
        if(heightConstraintBinds){
            scale = mapRect.getHeight() / visableMapTiles.getHeight();
        }else{
            scale = mapRect.getWidth() / visableMapTiles.getWidth();
        }
        needsUpdating = false;
    }
    
    protected void paintComponent(Graphics g){
        if(needsUpdating ){
            this.setZoom();
        }
        
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        NonNullElements it = new NonNullElements(KEY.STATIONS, world, this.principal);
        
        //Draw track
        g2.setColor(Color.BLACK);
        for(int x = Math.max(0, visableMapTiles.x); x < Math.min(visableMapTiles.width + visableMapTiles.x, world.getMapWidth()); x++){
            for(int y = Math.max(0, visableMapTiles.y); y < Math.min(visableMapTiles.height + visableMapTiles.y, world.getMapHeight()); y++){
                FreerailsTile tt = world.getTile(x, y);
                if (!tt.getTrackPiece().equals(NullTrackPiece.getInstance())) {
                    double xDouble = x - visableMapTiles.x;
                    xDouble = xDouble * scale;
                    double yDouble = y - visableMapTiles.y;
                    yDouble = yDouble * scale;
                    g.drawRect((int)xDouble, (int)yDouble, 1, 1);
                }
            }
        }
        
        TrainModel train = (TrainModel)world.get(KEY.TRAINS, this.trainID, this.principal);
        Schedule schedule = (Schedule)world.get(KEY.TRAIN_SCHEDULES, train.getScheduleID(), this.principal);
        
        //Draw stations
        while(it.next()){
            
                /*
                 * (1)	The selected station is drawn green.
                 * (2)	Non-selected stations which are on the schedule are drawn blue.
                 * (3)	Other stations are drawn white.
                 * (4)	If, for instance,  station X is the first stop on the schedule, "1" is drawn above the station.
                 * (5)	If, for instance,  station X is the first and third stop on the schedule, "1, 3" is drawn above the station.
                 * (6)	The stop numbers drawn above the stations are drawn using the same colour as used to draw the station.
                 */
            StationModel station = (StationModel)it.getElement();
            double x = station.x - visableMapTiles.x;
            x = x * scale;
            double y = station.y - visableMapTiles.y;
            y = y * scale;
            int xInt = (int)x;
            int yInt = (int)y;
            
            String stopNumbersString ="";
            boolean stationIsOnSchedule = false;
            for(int orderNumber = 0; orderNumber < schedule.getNumOrders(); orderNumber++){
                int stationID = orderNumber == this.selectedOrderNumber ? this.selectedStationID : schedule.getOrder(orderNumber).getStationNumber();
                if(it.getIndex() == stationID){
                    if(stationIsOnSchedule){
                        stopNumbersString = stopNumbersString+", "+String.valueOf(orderNumber+1);
                    }else{
                        stopNumbersString = String.valueOf(orderNumber+1);
                    }
                    stationIsOnSchedule = true;
                }
            }
            if(stationIsOnSchedule){
                if(it.getIndex() == selectedStationID){
                    g2.setColor(Color.GREEN);
                }else{
                    g2.setColor(Color.BLUE);
                }
                g2.drawString(stopNumbersString, xInt, yInt-4);
            }else{
                g2.setColor(Color.WHITE);
            }
            g2.fillRect(xInt, yInt, 10, 10);
        }        
    }
    
    public void setup(ModelRoot mr, ActionListener submitButtonCallBack) {
        cargoWaitingAndDemandedJPanel1.setup(mr,  null);
        this.world = mr.getWorld();
        this.submitButtonCallBack = submitButtonCallBack;
		principal = mr.getPlayerPrincipal();
    }
    
    public int getSelectedStationID(){
        return this.selectedStationID;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jfreerails.client.view.CargoWaitingAndDemandedJPanel cargoWaitingAndDemandedJPanel1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    
}
