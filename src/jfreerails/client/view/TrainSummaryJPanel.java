/*
 * TrainSummaryJPanel.java
 *
 * Created on January 24, 2005, 3:00 PM
 */

package jfreerails.client.view;

import java.awt.Color;

import javax.swing.ListCellRenderer;

import jfreerails.client.common.ModelRoot;
import jfreerails.client.renderer.ViewLists;
import jfreerails.client.view.TrainOrdersListModel.TrainOrdersListElement;
import jfreerails.world.common.GameCalendar;
import jfreerails.world.common.GameTime;
import jfreerails.world.common.Money;
import jfreerails.world.player.FreerailsPrincipal;
import jfreerails.world.station.StationModel;
import jfreerails.world.top.ITEM;
import jfreerails.world.top.KEY;
import jfreerails.world.top.NonNullElements;
import jfreerails.world.train.TrainOrdersModel;

/**
 *
 * @author  cphillips
 */
public class TrainSummaryJPanel extends javax.swing.JPanel implements ListCellRenderer, View {
    
    private static final long serialVersionUID = 4121133628006020919L;
	private jfreerails.world.top.ReadOnlyWorld w;
    private FreerailsPrincipal principal;
    private final Color backgoundColor = (java.awt.Color) javax.swing.UIManager.getDefaults().get("List.background");
   
    private final Color selectedColor = (java.awt.Color) javax.swing.UIManager.getDefaults().get("List.selectionBackground");
    
    private final Color selectedColorNotFocused = Color.LIGHT_GRAY;
    private TrainListCellRenderer trainListCellRenderer1;
    /** Creates new form TrainSummaryJPanel */
    public TrainSummaryJPanel() {
        initComponents();
    }

    public void setup(ModelRoot modelRoot, ViewLists vl, 
            java.awt.event.ActionListener submitButtonCallBack) {
            this.principal = modelRoot.getPrincipal();
            this.w = modelRoot.getWorld();
            trainListCellRenderer1 = new TrainListCellRenderer(modelRoot, vl);
            trainListCellRenderer1.setHeight(15);
    }
    
  
    private String findStationName(int trainNum) {
        TrainOrdersModel orders = null;
        TrainOrdersListModel ordersList = new TrainOrdersListModel(w, trainNum, principal);
        for(int i = 0; i < ordersList.getSize(); ++i) {
            TrainOrdersListElement element = (TrainOrdersListElement)ordersList.getElementAt(i);
            if(element.gotoStatus == TrainOrdersListModel.GOTO_NOW) {
                orders = element.order;
                break;
            }
        }
        StationModel station = (StationModel)w.get(KEY.STATIONS, orders.getStationID(), principal);
        return station.getStationName();
    }
    
    private String findTrainIncome(int trainNum) {
        IncomeStatementGenerator income = new IncomeStatementGenerator(w, principal);
        Money m = income.calTrainRevenue(trainNum);
        return "$" + m.toString();
    }
    public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        
        int trainID  = NonNullElements.row2index(w, KEY.TRAINS, principal, index);
        
       
           
       
        trainNumLabel.setText("#" + new Integer(trainID + 1).toString());
        headingLabel.setText(findStationName(trainID));
        trainMaintenanceCostLabel.setText(findMaintenanceCost());
        trainIncomeLabel.setText(findTrainIncome(trainID));
        
        java.awt.GridBagConstraints gridBagConstraints;
        
        trainListCellRenderer1.setOpaque(true);
        trainListCellRenderer1.setCenterTrain(false);
        trainListCellRenderer1.display(trainID); 
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        add(trainListCellRenderer1, gridBagConstraints);
        
        
        
        if (isSelected) {
            if(list.isFocusOwner()){
                setBackground(selectedColor);
                trainListCellRenderer1.setBackground(selectedColor);
            }else{
                setBackground(selectedColorNotFocused);
                trainListCellRenderer1.setBackground(selectedColorNotFocused);
            }
        } else {
            setBackground(backgoundColor);
            trainListCellRenderer1.setBackground(backgoundColor);
        }
        //Set selected
     
        return this;
    }
    private String findMaintenanceCost() {
        
        GameTime time = w.currentTime();
        GameCalendar gameCalendar = (GameCalendar)w.get(ITEM.CALENDAR);
        double month = gameCalendar.getMonth(time.getTicks());
        long cost = (long)(month / 12 * 5000);
        
        Money m = new Money(cost);
        return "$" + m.toString();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        trainNumLabel = new javax.swing.JLabel();
        headingLabel = new javax.swing.JLabel();
        trainMaintenanceCostLabel = new javax.swing.JLabel();
        trainIncomeLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 50));
        trainNumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        trainNumLabel.setText("jLabel1");
        trainNumLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        trainNumLabel.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(trainNumLabel, gridBagConstraints);

        headingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headingLabel.setText("jLabel2");
        headingLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        headingLabel.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(headingLabel, gridBagConstraints);

        trainMaintenanceCostLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        trainMaintenanceCostLabel.setText("jLabel3");
        trainMaintenanceCostLabel.setMaximumSize(getMaximumSize());
        trainMaintenanceCostLabel.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(trainMaintenanceCostLabel, gridBagConstraints);

        trainIncomeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        trainIncomeLabel.setText("jLabel1");
        trainIncomeLabel.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(trainIncomeLabel, gridBagConstraints);

    }//GEN-END:initComponents
   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel headingLabel;
    private javax.swing.JLabel trainIncomeLabel;
    private javax.swing.JLabel trainMaintenanceCostLabel;
    private javax.swing.JLabel trainNumLabel;
    // End of variables declaration//GEN-END:variables
    
}
