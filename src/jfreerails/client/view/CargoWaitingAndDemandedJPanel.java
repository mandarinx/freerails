/*
 * CargoWaitingAndDemandedJPanel.java
 *
 * Created on 07 February 2004, 12:24
 */

package jfreerails.client.view;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import jfreerails.world.cargo.CargoBundle;
import jfreerails.world.cargo.CargoType;
import jfreerails.world.player.FreerailsPrincipal;
import jfreerails.world.station.StationModel;
import jfreerails.world.top.KEY;
import jfreerails.world.top.ReadOnlyWorld;
import jfreerails.world.top.SKEY;
import jfreerails.world.train.WagonType;
/**
 *
 * @author  Luke
 */
public class CargoWaitingAndDemandedJPanel extends javax.swing.JPanel implements View {
    
    private ReadOnlyWorld world;
    private FreerailsPrincipal principal;  

    /** Creates new form CargoWaitingAndDemandedJPanel */
    public CargoWaitingAndDemandedJPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        stationName = new javax.swing.JLabel();
        waiting = new javax.swing.JLabel();
        waitingJTable = new javax.swing.JTable();
        demands = new javax.swing.JLabel();
        demandsJList = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(100, 200));
        stationName.setText("Station Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(stationName, gridBagConstraints);

        waiting.setText("Waiting");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(waiting, gridBagConstraints);

        waitingJTable.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Button.background"));
        waitingJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Mail", "4"},
                {"Passengers", null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        waitingJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        waitingJTable.setFocusable(false);
        waitingJTable.setMinimumSize(new java.awt.Dimension(30, 32));
        waitingJTable.setRequestFocusEnabled(false);
        waitingJTable.setRowSelectionAllowed(false);
        waitingJTable.setShowHorizontalLines(false);
        waitingJTable.setShowVerticalLines(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(waitingJTable, gridBagConstraints);

        demands.setText("Demands");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(demands, gridBagConstraints);

        demandsJList.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Button.background"));
        demandsJList.setFont(new java.awt.Font("Dialog", 0, 12));
        demandsJList.setFocusable(false);
        demandsJList.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(demandsJList, gridBagConstraints);

    }//GEN-END:initComponents
    
    public void setup(ModelRoot model, ActionListener submitButtonCallBack) {
        this.world = model.getWorld();
        this.principal = model.getPlayerPrincipal();
    }
    
    public void display(int newStationID){
        StationModel station = (StationModel)world.get(KEY.STATIONS, newStationID, principal);
        this.stationName.setText(station.getStationName());
        int cargoBundleIndex = station.getCargoBundleNumber();
        final CargoBundle cargoWaiting =
        (CargoBundle) world.get(
        KEY.CARGO_BUNDLES,
        station.getCargoBundleNumber(), principal);
        
        //count the number of cargo types waiting and demanded.
        final ArrayList typeWaiting = new ArrayList();
        final ArrayList quantityWaiting = new ArrayList();
        final Vector typeDemanded = new Vector();
        for (int i = 0; i < world.size(SKEY.CARGO_TYPES); i++) {
              CargoType cargoType = (CargoType)world.get(SKEY.CARGO_TYPES, i);
              int amountWaiting = cargoWaiting.getAmount(i);
              
              if(0 !=amountWaiting){
                   typeWaiting.add(cargoType.getDisplayName());
                   int carloads = amountWaiting / WagonType.UNITS_OF_CARGO_PER_WAGON;
                   quantityWaiting.add(new Integer(carloads));
              }
              if(station.getDemand().isCargoDemanded(i)){
                  typeDemanded.add(cargoType.getDisplayName());
              }
        }
       
        /* The table shows the cargo waiting at the station.  First column is cargo type; second 
         column is quantity in carloads.*/
        TableModel tableModel = new AbstractTableModel() {                        
            
            public int getRowCount(){
                return typeWaiting.size();
            }
            public int getColumnCount(){
                return 2;
            }
            public Object getValueAt(int row, int column){
                if(0 == column){
                    return typeWaiting.get(row);
                }else{
                    return quantityWaiting.get(row);
                }
            }
        };
        this.waitingJTable.setModel(tableModel);
        
        /* The list shows the cargo demanded by the station.*/
        this.demandsJList.setListData(typeDemanded);    
        
        this.invalidate();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel demands;
    private javax.swing.JList demandsJList;
    private javax.swing.JLabel stationName;
    private javax.swing.JLabel waiting;
    private javax.swing.JTable waitingJTable;
    // End of variables declaration//GEN-END:variables
    
}
