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
 * SelectMapPanel.java
 *
 */

package freerails.client.launcher;

import freerails.client.launcher.LauncherInterface.MSG_TYPE;
import freerails.network.SaveGamesManager;
import freerails.server.SaveGameManagerImpl;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;

/**
 * The Launcher panel that lets you load a game or start a new game with a
 * choice of maps.
 */
public class SelectMapPanel extends JPanel implements LauncherPanel {

    private static final long serialVersionUID = 3763096353857024568L;
    private static final String SELECT_A_MAP = "Select a map.";
    private static final String INVALID_PORT = "A valid port value is between between 0 and 65535.";
    private final LauncherInterface owner;

    JPanel jPanel1;
    JPanel jPanel2;
    JPanel jPanel3;
    JPanel jPanel4;
    JScrollPane jScrollPane1;
    JScrollPane jScrollPane2;
    JList newmapsJList;
    JLabel portLabel;
    JList savedmapsJList;
    JTextField serverPort;

    SelectMapPanel(LauncherInterface owner) {
        this.owner = owner;

        initComponents();

        /* initialise the map list */
        SaveGamesManager sgm = new SaveGameManagerImpl();
        newmapsJList.setListData(sgm.getNewMapNames());
        savedmapsJList.setListData(sgm.getSaveGameNames());
        if (sgm.getSaveGameNames().length > 0) {
            savedmapsJList.setSelectedIndex(0);
        } else {
            newmapsJList.setSelectedIndex(0);
        }

        owner.setNextEnabled(true);

        // Listen for changes in the server port text box.
        serverPort.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            public void changedUpdate(DocumentEvent e) {
                validateInput();
            }

        });
    }

    MapSelection getSelection() {
        if (newmapsJList.getSelectedIndex() != -1) {
            savedmapsJList.setSelectedIndex(-1);
            return MapSelection.NEW_GAME;
        }

        if (savedmapsJList.getSelectedIndex() != -1) {
            return MapSelection.LOAD_GAME;
        }
        return MapSelection.NONE;
    }

    void setServerPortPanelVisible(boolean b) {
        jPanel3.setVisible(b);
    }

    /**
     * @return
     */
    public String getNewMapName() {
        return (String) newmapsJList.getSelectedValue();
    }

    /**
     * @return
     */
    public String getSaveGameName() {
        return (String) savedmapsJList.getSelectedValue();
    }

    int getServerPort() {
        String s = serverPort.getText();
        return Integer.parseInt(s);
    }

    /**
     * @return
     */
    public boolean validateInput() {
        /* Validate map selection. */
        if (getSelection() == MapSelection.NONE) {
            owner.setInfoText(SELECT_A_MAP, MSG_TYPE.ERROR);
            return false;
        }

        /* Validate port. */
        try {
            int port = getServerPort();
            if (port < 0 || port > 65535) {
                owner.setInfoText(INVALID_PORT, MSG_TYPE.ERROR);
                return false;
            }
        } catch (Exception e) {
            owner.setInfoText(INVALID_PORT, MSG_TYPE.ERROR);
            return false;
        }

        /* Everything is ok. */
        owner.hideErrorMessages();
        owner.setProperty(LauncherInterface.SERVER_PORT_PROPERTY, serverPort.getText());
        owner.saveProps();
        return true;
    }


    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        newmapsJList = new JList();
        jPanel4 = new JPanel();
        jScrollPane2 = new JScrollPane();
        savedmapsJList = new JList();
        jPanel3 = new JPanel();
        portLabel = new JLabel();
        serverPort = new JTextField();
        jPanel2 = new JPanel();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(new TitledBorder(new EtchedBorder(), "New Game"));
        jPanel1.setPreferredSize(null);
        jScrollPane1.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
        newmapsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newmapsJList.addListSelectionListener(this::newmapsJListValueChanged);

        jScrollPane1.setViewportView(newmapsJList);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(new TitledBorder(new EtchedBorder(), "Load game"));
        jPanel4.setPreferredSize(null);
        jPanel4.setRequestFocusEnabled(false);
        jScrollPane2.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
        savedmapsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        savedmapsJList.addListSelectionListener(this::savedmapsJListValueChanged);

        jScrollPane2.setViewportView(savedmapsJList);

        jPanel4.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel4, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(new TitledBorder(new EtchedBorder(), "Server port"));
        portLabel.setText("Port:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(portLabel, gridBagConstraints);

        serverPort.setColumns(6);
        serverPort.setText(owner.getProperty(LauncherInterface.SERVER_PORT_PROPERTY));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(serverPort, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel3, gridBagConstraints);

    }

    private void savedmapsJListValueChanged(ListSelectionEvent evt) {
        if (savedmapsJList.getSelectedIndex() != -1) newmapsJList.clearSelection();

        validateInput();
    }

    private void newmapsJListValueChanged(ListSelectionEvent evt) {
        if (newmapsJList.getSelectedIndex() != -1) savedmapsJList.clearSelection();
        validateInput();
    }
}
