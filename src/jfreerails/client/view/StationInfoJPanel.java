/*
 * StationInfoJPanel.java
 *
 * Created on 04 May 2003, 18:56
 */

package jfreerails.client.view;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import jfreerails.client.renderer.ViewLists;
import jfreerails.controller.MoveChainFork;
import jfreerails.controller.MoveReceiver;
import jfreerails.move.AddItemToListMove;
import jfreerails.move.ListMove;
import jfreerails.move.Move;
import jfreerails.move.MoveStatus;
import jfreerails.world.cargo.CargoBundle;
import jfreerails.world.cargo.CargoType;
import jfreerails.world.station.StationModel;
import jfreerails.world.top.KEY;
import jfreerails.world.top.NonNullElements;
import jfreerails.world.top.World;
import jfreerails.world.top.WorldIterator;
import jfreerails.world.track.FreerailsTile;

/** This JPanel displays the supply and demand at a station.
 *
 * @author  Luke
 */
public class StationInfoJPanel extends javax.swing.JPanel implements
    MoveReceiver {
    
    private ViewLists vl;
    private World w;
    private WorldIterator wi;
    
    /**
     * The index of the cargoBundle associated with this station
     */
    private int cargoBundleIndex;
    
    /** Creates new form StationInfoJPanel */
    public StationInfoJPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        nextStation = new javax.swing.JButton();
        previousStation = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setMinimumSize(new java.awt.Dimension(250, 177));
        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10));
        jLabel1.setText("<html>\n<h4 align=\"center\">Supply and Demand at stationName</h4>\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\">\n  <tr>\n    <td>&nbsp;</td>\n    <td>Will pay<br>for</td>\n    <td>Supplies<br>(cars per year)</td>\n    <td>Waiting for pickup<br>(car loads)</td>\n  </tr>\n   <tr>\n    <td>Mail</td>\n    <td>Yes</td>\n    <td>&nbsp;</td>\n    <td>&nbsp;</td>\n  </tr>\n  <tr>\n    <td>Passengers</td>\n    <td>No</td>\n    <td>3</td>\n    <td>2.5</td>\n  </tr>\n \n</table>\n\n</html>");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setAlignmentY(0.0F);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        add(jLabel1, gridBagConstraints);

        nextStation.setText("next ->");
        nextStation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        nextStation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextStationActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        add(nextStation, gridBagConstraints);

        previousStation.setText("<- previous");
        previousStation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        previousStation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousStationActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 4);
        add(previousStation, gridBagConstraints);

    }//GEN-END:initComponents

    private void previousStationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousStationActionPerformed
        // Add your handling code here:
         if(wi.previous() ){
	     Point p = new Point(((StationModel) wi.getElement()).getStationX(),
		     ((StationModel) wi.getElement()).getStationY());
	     FreerailsCursor.getCursor().TryMoveCursor(p);
		     
            display();
        }else{
            throw new IllegalStateException();
        }
    }//GEN-LAST:event_previousStationActionPerformed

    private void nextStationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextStationActionPerformed
        // Add your handling code here:
          if(wi.next()){
	     Point p = new Point(((StationModel) wi.getElement()).getStationX(),
		     ((StationModel) wi.getElement()).getStationY());
	     FreerailsCursor.getCursor().TryMoveCursor(p);
            display();
        }else{
            throw new IllegalStateException();
        }
        
    }//GEN-LAST:event_nextStationActionPerformed

    public void setup(World w, ViewLists vl) {
        this.vl = vl;
        this.w = w;
        this.wi = new NonNullElements(KEY.STATIONS, w);
	addComponentListener(componentListener);
    }    
    
    public void setStation(int stationNumber){
        this.wi.gotoIndex(stationNumber);
        display();
    }
    
    public void display(){
        
        if(wi.getRowNumber()>0){
            this.previousStation.setEnabled(true);
        }else{
            this.previousStation.setEnabled(false);
        }
        
        if(wi.getRowNumber()<(wi.size()-1)){
            this.nextStation.setEnabled(true);
        }else{
            this.nextStation.setEnabled(false);
        }
        
        int stationNumber = wi.getIndex();
	String label;
	if (stationNumber != WorldIterator.BEFORE_FIRST) {
        StationModel station = (StationModel)w.get(KEY.STATIONS, stationNumber);
        FreerailsTile tile = w.getTile(station.x, station.y); 
        String stationTypeName = tile.getTrackRule().getTypeName();  
	    cargoBundleIndex = station.getCargoBundleNumber();
        CargoBundle cargoWaiting = (CargoBundle)w.get(KEY.CARGO_BUNDLES, station.getCargoBundleNumber());     
        String title = "<h2 align=\"center\">"+station.getStationName()+" ("+stationTypeName+")</h2>";
        String table ="<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\"><tr><td>&nbsp;</td>\n    <td>Will pay for</td>\n    <td>Supplies / cars per year</td><td>Waiting for pickup / car loads</td>  </tr>";
        for(int i = 0 ; i < w.size(KEY.CARGO_TYPES) ; i++){         
        	
        	//get the values
            CargoType cargoType = (CargoType)w.get(KEY.CARGO_TYPES, i);                       
            String demanded = (station.getDemand().isCargoDemanded(i) ? "Yes" : "No");                                 			
            int amountSupplied = station.getSupply().getSupply(i);
            String supply = (amountSupplied > 0) ? String.valueOf(amountSupplied) : "&nbsp;";            
			int amountWaiting = cargoWaiting.getAmount(i);
			String waiting = (amountWaiting > 0) ? String.valueOf(amountWaiting) : "&nbsp;";
			
			//build the html
			table +="<tr>";
			table +="<td>"+cargoType.getDisplayName()+"</td>";
			table +="<td>"+demanded+ "</td>";  
			table +="<td>"+supply+"</td>";
			table +="<td>"+waiting+"</td>";
			table +="</tr>";
                       
        }
        table +="</table>";
	    label = "<html>" + title + table + "</html>";
	} else {
	    cargoBundleIndex = WorldIterator.BEFORE_FIRST;
	    label = "<html><h2 align=\"center\">No Station " +
	    "Selected</h2></html>";
	}
        jLabel1.setText(label);
    }
    
    ComponentAdapter componentListener = new ComponentAdapter() {
	public void componentHidden(ComponentEvent e) {
	    MoveChainFork.getMoveChainFork().remove(StationInfoJPanel.this);
	}

	public void componentShown(ComponentEvent e) {
	    MoveChainFork.getMoveChainFork().add(StationInfoJPanel.this);
	    int i = wi.getIndex();
	    wi.reset();
	    if (i != WorldIterator.BEFORE_FIRST) {
		wi.gotoIndex(i);
	    }
	    display();
	}
    };
    
    public MoveStatus processMove(Move move) {
	if (! (move instanceof ListMove)) {
	    return MoveStatus.MOVE_RECEIVED;
	}

	ListMove lm = (ListMove) move;
	int currentIndex = wi.getIndex();
	int changedIndex = lm.getIndex();
	KEY key = lm.getKey();
	if (key == KEY.CARGO_BUNDLES) {
	    if (changedIndex == cargoBundleIndex) {
		/* update our cargo bundle */
		display();
		return MoveStatus.MOVE_OK;
	    }
	} else if (key == KEY.STATIONS) {
	    wi.reset();
	    if (currentIndex != WorldIterator.BEFORE_FIRST) {
		wi.gotoIndex(currentIndex);
	    }
	    if (lm instanceof AddItemToListMove &&
		    wi.getIndex() == WorldIterator.BEFORE_FIRST) {
		if (wi.next()) {
		    display();
		}
	    }
	    if (changedIndex < currentIndex) {
		previousStation.setEnabled(lm.getBefore() != null);
	    } else if (changedIndex > currentIndex) {
		nextStation.setEnabled(lm.getAfter() != null);
	    } else {
		display();
	    }
	} else {
	    return MoveStatus.MOVE_RECEIVED;
	}
	return MoveStatus.MOVE_OK;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton nextStation;
    private javax.swing.JButton previousStation;
    // End of variables declaration//GEN-END:variables
    
}
