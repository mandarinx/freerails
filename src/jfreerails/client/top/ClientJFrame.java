/*
 * ClientJFrame.java
 *
 * Created on 01 June 2003, 15:56
 */

package jfreerails.client.top;

import java.awt.event.KeyEvent;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JSplitPane;

/**
 *
 * @author  Luke
 */
public class ClientJFrame extends javax.swing.JFrame {
    
    private GUIComponentFactory gUIComponentFactory;
    
    public ClientJFrame(){
    	
    }
    
    /** Creates new form ClientJFrame */
    public ClientJFrame(GUIComponentFactory gcf) {
       	setup(gcf);
    }

	public void setup(GUIComponentFactory gcf) {
		 this.gUIComponentFactory=gcf;
		    initComponents();
		    //jSplitPane1.resetToPreferredSizes();
		    gUIComponentFactory.createDateJLabel();
	}
    
    public void setup() {
	jSplitPane1.revalidate();
        jSplitPane1.resetToPreferredSizes();

    /* Hack to stop F8 grabbing the focus of the SplitPane (see javadoc for
     * JSplitPane Key assignments */
    InputMap im = jSplitPane1.getInputMap(JSplitPane.WHEN_IN_FOCUSED_WINDOW);
    im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
    jSplitPane1.setInputMap(JSplitPane.WHEN_IN_FOCUSED_WINDOW, im);
    im = jSplitPane1.getInputMap(JSplitPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);   
    jSplitPane1.setInputMap(JSplitPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
	jSplitPane1.revalidate();
	jSplitPane1.resetToPreferredSizes();
	
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        rhsjPanel = new javax.swing.JPanel();
        mapOverview = gUIComponentFactory.createOverviewMap();
        trainsJTabPane1 = gUIComponentFactory.createTrainsJTabPane();

        lhsjPanel = new javax.swing.JPanel();
        mainMapView = gUIComponentFactory.createMainMap();
        statusjPanel = new javax.swing.JPanel();
        datejLabel = gUIComponentFactory.createDateJLabel();
        cashjLabel = gUIComponentFactory.createCashJLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        gameMenu = gUIComponentFactory.createGameMenu();
        buildMenu = gUIComponentFactory.createBuildMenu();
        displayMenu = gUIComponentFactory.createDisplayMenu();
        helpMenu = gUIComponentFactory.createHelpMenu();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jSplitPane1.setResizeWeight(0.8);
        rhsjPanel.setLayout(new java.awt.GridBagLayout());

        rhsjPanel.add(mapOverview, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rhsjPanel.add(trainsJTabPane1, gridBagConstraints);

        jSplitPane1.setRightComponent(rhsjPanel);

        lhsjPanel.setLayout(new java.awt.GridBagLayout());

        mainMapView.setAlignmentX(0.0F);
        mainMapView.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        lhsjPanel.add(mainMapView, gridBagConstraints);

        statusjPanel.add(datejLabel);

        statusjPanel.add(cashjLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        lhsjPanel.add(statusjPanel, gridBagConstraints);

        jSplitPane1.setLeftComponent(lhsjPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jSplitPane1, gridBagConstraints);

        gameMenu.setText("Game");
        jMenuBar1.add(gameMenu);

        buildMenu.setText("Build");
        jMenuBar1.add(buildMenu);

        displayMenu.setText("Display");
        jMenuBar1.add(displayMenu);

        helpMenu.setText("Help");
        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new ClientJFrame(new GUIComponentFactoryTestImpl()).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu buildMenu;
    private javax.swing.JLabel cashjLabel;
    private javax.swing.JLabel datejLabel;
    private javax.swing.JMenu displayMenu;
    private javax.swing.JMenu gameMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel lhsjPanel;
    private javax.swing.JScrollPane mainMapView;
    private javax.swing.JPanel mapOverview;
    private javax.swing.JPanel rhsjPanel;
    private javax.swing.JPanel statusjPanel;
    private javax.swing.JTabbedPane trainsJTabPane1;
    // End of variables declaration//GEN-END:variables
    
}