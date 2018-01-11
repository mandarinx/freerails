/*
 * FreeRails
 * Copyright (C) 2000-2018 The FreeRails Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * ConnectedPlayersJPanel.java
 *
 */

package freerails.client.launcher;

import freerails.network.FreerailsGameServer;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A JPanel that shows the players currently logged in to the server.
 */
public class ConnectedPlayersJPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 4049080453489111344L;

    FreerailsGameServer server = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JList jList1;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JLabel title;

    /**
     * Creates new form ConnectedPlayersJPanel
     */
    public ConnectedPlayersJPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        title = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        title.setText("Connected Players");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(title, gridBagConstraints);

        jList1.setModel(new MyAbstractListModel());
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

    }

    void updateListOfPlayers() {
        if (null != server) {
            String[] playerNames = server.getPlayerNames();
            playerNames = playerNames.length == 0 ? new String[]{"No players are logged on!"} : playerNames;
            setListOfPlayers(playerNames);
        }
    }

    void setListOfPlayers(String[] players) {
        jList1.setListData(players);
    }

    /**
     * Called by the server when a player is added or removed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(FreerailsGameServer.CONNECTED_PLAYERS)) {
            if (EventQueue.isDispatchThread()) {
                updateListOfPlayers();
            } else {
                EventQueue.invokeLater(this::updateListOfPlayers);
            }
        }
    }

    private static class MyAbstractListModel extends javax.swing.AbstractListModel {

        private static final long serialVersionUID = -7077093078891444168L;

        final String[] strings = {"No players are logged on!"};

        public int getSize() {
            return strings.length;
        }

        public Object getElementAt(int index) {
            return strings[index];
        }
    }
    // End of variables declaration//GEN-END:variables

}
