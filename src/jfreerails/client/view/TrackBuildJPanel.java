/*
 * JPanel.java
 *
 * Created on 19 August 2003, 22:19
 */

package jfreerails.client.view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Enumeration;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JToggleButton;

import jfreerails.client.renderer.ViewLists;
import jfreerails.world.track.TrackConfiguration;

/**
 *
 * @author  bob
 */
class TrackBuildJPanel extends javax.swing.JPanel {
    private ViewLists viewLists;
    private int numberOfButtons = 0;
    private int widthOfButton = 30;
    private TrackRuleButton currentButton = null;
    private ButtonGroup buttonGroup;
    private TrackBuildModel buildModel;

    public void validate() {
    super.validate();
    System.out.println("TrackBuildJPanel size " + getHeight());
    }

    /**
     * Workaround for completely broken FlowLayout behaviour.
     */
    private ComponentAdapter sizeListener = new ComponentAdapter () {
	public void componentResized(ComponentEvent e) {
	    System.out.println("resetting size");
	    System.out.println("current size is " +
	    trackBuildModesPanel.getSize());
	    /* determine max number of cols */
	    Dimension d = trackBuildModesSP.getViewport().getSize();
	    int numCols = (int) (d.getWidth() / (widthOfButton + 5));
	    int numRows = numberOfButtons / numCols + 1;
	    d.setSize(d.getWidth(), numRows * (widthOfButton + 5));
	    trackBuildModesPanel.setPreferredSize(d);
	    System.out.println("numcols = " + numCols + " numRows = " +
	    numRows);
	    System.out.println("new preferred size is " + d);
	    System.out.println("reading back.." + 
	    trackBuildModesPanel.getPreferredSize());
	    trackBuildModesPanel.revalidate();
	}
    };
    
    /*
     * 100 010 001 = 0x111
     */
    private static final int trackTemplate =
	TrackConfiguration.getFlatInstance(0x111).getTemplate();

    private void setTrackRule(TrackRuleButton b) {
	if (currentButton != null) {
	    currentButton.setSelected(false);
	    currentButton = b;
	}
    }

    void setup(ViewLists vl, ModelRoot modelRoot) {
	 // GridBagConstraints gbc = new GridBagConstraints();
	viewLists = vl;
	buttonGroup = new ButtonGroup();
	buildModel = modelRoot.getTrackBuildModel();
	trackModeCB.setModel(buildModel.getBuildModeActionAdapter());
	Enumeration e = buildModel.getTrackRuleAdapter().getActions();
	Enumeration enumButtonModels = buildModel.getTrackRuleAdapter().getButtonModels();
	while (e.hasMoreElements()) {
	    // Stations get built in the station pane
	    TrackRuleButton button = new TrackRuleButton((Action) e.nextElement());
	    button.setModel((ButtonModel) enumButtonModels.nextElement());
	    Dimension d = button.getSize();
	    Dimension s = trackBuildModesPanel.getSize();
	    int columns = (int) (s.getWidth() / d.getWidth());
	    buttonGroup.add(button);
	    trackBuildModesPanel.add(button);
	    numberOfButtons++;
	    /* this is OK since all buttons are same width */
	    widthOfButton = (int) button.getPreferredSize().getWidth();
	}
	trackBuildModesPanel.revalidate();
    }

    private void setupTrackComponents() {
	/*
	 * setup the "mode" combo box
	 */

	trackBuildModesSP.setViewportView(trackBuildModesPanel);
	trackBuildModesSP.addComponentListener(sizeListener);
    }

    /** Creates new form JPanel */
    public TrackBuildJPanel() {
        initComponents();
	setupTrackComponents();
    }
    
    /**
     * represents a track rule - contains a small icon representing the track
     * and a text label
     */
    private class TrackRuleButton extends JToggleButton {
	public TrackRuleButton(Action a) {
	    super(a);
	    setMargin(new Insets(0,0,0,0));
	    setText(null);
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
        jLabel2 = new javax.swing.JLabel();
        trackModeCB = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        trackBuildModesSP = new javax.swing.JScrollPane();
        trackBuildModesPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Mode:");
        jPanel1.add(jLabel2);

        jPanel1.add(trackModeCB);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jSeparator1, gridBagConstraints);

        trackBuildModesSP.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        trackBuildModesSP.setViewportView(trackBuildModesPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(trackBuildModesSP, gridBagConstraints);

    }//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel trackBuildModesPanel;
    private javax.swing.JScrollPane trackBuildModesSP;
    private javax.swing.JComboBox trackModeCB;
    // End of variables declaration//GEN-END:variables
    
}
