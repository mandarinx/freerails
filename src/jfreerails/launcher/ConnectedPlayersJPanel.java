/*
 * ConnectedPlayersJPanel.java
 *
 * Created on 12 September 2004, 04:51
 */

package jfreerails.launcher;

import jfreerails.network.FreerailsGameServer;

/**
 * A JPanel that shows the players currently logged in to the server.
 * @author  Luke
 */
public class ConnectedPlayersJPanel extends javax.swing.JPanel {
    
    FreerailsGameServer server = null;
    
    /** Creates new form ConnectedPlayersJPanel */
    public ConnectedPlayersJPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jList1 = new javax.swing.JList();
        refreshJButton = new javax.swing.JButton();
        title = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "No players are logged on!" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jList1, gridBagConstraints);

        refreshJButton.setText("Refresh List");
        refreshJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshJButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(refreshJButton, gridBagConstraints);

        title.setText("Connected Players");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(title, gridBagConstraints);

    }//GEN-END:initComponents

    private void refreshJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshJButtonActionPerformed
       if(null != server){
           setListOfPlayers(server.getPlayerNames());
       }
    }//GEN-LAST:event_refreshJButtonActionPerformed
    
    void setListOfPlayers(String[] players){
        jList1.setListData(players);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JButton refreshJButton;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
    
}