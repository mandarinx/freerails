/*
 * TrainDetailsJPanel.java
 *
 * Created on 16 June 2030, 20:03
 */

package jfreerails.client.view;
import jfreerails.client.common.ModelRoot;
import jfreerails.client.renderer.ViewLists;
import jfreerails.world.cargo.CargoType;
import jfreerails.world.cargo.ImmutableCargoBundle;
import jfreerails.world.player.FreerailsPrincipal;
import jfreerails.world.top.KEY;
import jfreerails.world.top.ReadOnlyWorld;
import jfreerails.world.top.SKEY;
import jfreerails.world.top.WorldListListener;
import jfreerails.world.train.TrainModel;
/**
 *	This JPanel displays a side-on view of a train and a summary of the cargo that it is carrying.
 *
 * @author  Luke Lindsay
 */
public class TrainDetailsJPanel extends javax.swing.JPanel implements View, WorldListListener {
    
    
    private ReadOnlyWorld w;
    
    private FreerailsPrincipal principal;
    
    private int trainNumber = -1;
    
    /** The id of the bundle of cargo that the train is carrying - we need to update
     * the view when the bundle is updated.
     */
    private int bundleID = -1;
    
    
    public TrainDetailsJPanel() {
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
        trainViewJPanel1 = new jfreerails.client.view.TrainViewJPanel();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.TitledBorder("Current Details"));
        setPreferredSize(new java.awt.Dimension(250, 97));
        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("<html><head></head><body>Trains X: 20 passengers, 15 tons of mfg goods, 12 sacks of mail, and 7 tons of livestock.</body></html>");
        jLabel1.setMinimumSize(new java.awt.Dimension(250, 17));
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(trainViewJPanel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    public void setup(ModelRoot mr,  ViewLists vl, java.awt.event.ActionListener submitButtonCallBack) {
        
        this.trainViewJPanel1.setup(mr, vl, submitButtonCallBack);
        trainViewJPanel1.setHeight(30);
        trainViewJPanel1.setCenterTrain(true);
        this.w = mr.getWorld();
        principal = mr.getPrincipal();
    }
    
    public void displayTrain(int trainNumber){
        
        this.trainNumber = trainNumber;
        
        trainViewJPanel1.display(trainNumber);
        TrainModel train = (TrainModel)w.get(KEY.TRAINS, trainNumber, principal);
        
        this.bundleID = train.getCargoBundleID();
        
        
        for(int i = 0 ; i < train.getNumberOfWagons() ; i++ ){
            //this.sideOnTrainViewJPanel1.addWagon(train.getWagon(i));
        }
        ImmutableCargoBundle cb = (ImmutableCargoBundle)w.get(KEY.CARGO_BUNDLES, train.getCargoBundleID(), principal);
        String s="Train #"+trainNumber+": ";
        int numberOfTypesInBundle = 0;
        for (int i = 0 ; i < w.size(SKEY.CARGO_TYPES) ; i ++){
            int amount = cb.getAmount(i);
            if(0 != amount){
                CargoType ct = (CargoType)w.get(SKEY.CARGO_TYPES, i);
                String cargoTypeName = ct.getDisplayName();
                if(0!=numberOfTypesInBundle){
                    s+="; ";
                }
                numberOfTypesInBundle++;
                
                s+= cargoTypeName+" ("+amount+")";
            }
        }
        if(0 == numberOfTypesInBundle){
            s+="no cargo";
        }
        s+=".";
        this.jLabel1.setText(s);
    }
    
    public void listUpdated(KEY key, int index, FreerailsPrincipal p) {
        
        if(KEY.TRAINS == key && index == trainNumber && principal.equals(p)){
            //The train has been updated.
            this.displayTrain(this.trainNumber);
        }else if(KEY.CARGO_BUNDLES == key && index == bundleID && principal.equals(p)){
            //The train's cargo has changed.
            this.displayTrain(this.trainNumber);
        }
        trainViewJPanel1.listUpdated(key, index,p);
    }
    
    public void itemAdded(KEY key, int index, FreerailsPrincipal p) {
        trainViewJPanel1.itemAdded(key, index,p);
    }
    
    public void itemRemoved(KEY key, int index, FreerailsPrincipal p) {
        trainViewJPanel1.itemRemoved(key, index,p);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private jfreerails.client.view.TrainViewJPanel trainViewJPanel1;
    // End of variables declaration//GEN-END:variables
    
}
