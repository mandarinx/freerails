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

import freerails.Options;
import freerails.scenario.MapCreator;
import freerails.scenario.SaveGamesManager;
import freerails.scenario.FullSaveGameManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

/**
 * The Launcher panel that lets you load a game or start a new game with a
 * choice of maps.
 */
class SelectMapPanel extends JPanel {

    private static final long serialVersionUID = 3763096353857024568L;
    private static final String SELECT_A_MAP = "Select a map.";
    private static final String INVALID_PORT = "A valid port value is between between 0 and 65535.";
    private final LauncherInterface owner;

    private JPanel jPanel3;
    private JList newmapsJList;
    private JList savedmapsJList;
    private JTextField serverPort;

    SelectMapPanel(LauncherInterface owner) {
        this.owner = owner;

        GridBagConstraints gridBagConstraints;

        JPanel jPanel1 = new JPanel();
        JScrollPane jScrollPane1 = new JScrollPane();
        newmapsJList = new JList();
        JPanel jPanel4 = new JPanel();
        JScrollPane jScrollPane2 = new JScrollPane();
        savedmapsJList = new JList();
        jPanel3 = new JPanel();
        JLabel portLabel = new JLabel();
        serverPort = new JTextField();
        JPanel jPanel2 = new JPanel();

        setLayout(new GridBagLayout());

        jPanel1.setLayout(new BorderLayout());

        jPanel1.setBorder(new TitledBorder(new EtchedBorder(), "New Game"));
        jPanel1.setPreferredSize(null);
        jScrollPane1.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
        newmapsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newmapsJList.addListSelectionListener(this::newmapsJListValueChanged);

        jScrollPane1.setViewportView(newmapsJList);

        jPanel1.add(jScrollPane1, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        jPanel4.setLayout(new BorderLayout());

        jPanel4.setBorder(new TitledBorder(new EtchedBorder(), "Load game"));
        jPanel4.setPreferredSize(null);
        jPanel4.setRequestFocusEnabled(false);
        jScrollPane2.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
        savedmapsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        savedmapsJList.addListSelectionListener(this::savedmapsJListValueChanged);

        jScrollPane2.setViewportView(savedmapsJList);

        jPanel4.add(jScrollPane2, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel4, gridBagConstraints);

        jPanel3.setLayout(new GridBagLayout());

        jPanel3.setBorder(new TitledBorder(new EtchedBorder(), "Server port"));
        portLabel.setText("Port:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        jPanel3.add(portLabel, gridBagConstraints);

        serverPort.setColumns(6);
        serverPort.setText(String.valueOf(Options.PORT));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        jPanel3.add(serverPort, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(jPanel3, gridBagConstraints);

        // initialise the map list
        newmapsJList.setListData(MapCreator.getAvailableMapNames());
        SaveGamesManager sgm = new FullSaveGameManager();
        savedmapsJList.setListData(sgm.getSaveGameNames());
        if (sgm.getSaveGameNames().length > 0) {
            savedmapsJList.setSelectedIndex(0);
        } else {
            newmapsJList.setSelectedIndex(0);
        }

        owner.setNextEnabled(true);

        // Listen for changes in the server port text box.
        serverPort.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
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
        // Validate map selection.
        if (getSelection() == MapSelection.NONE) {
            owner.setInfoText(SELECT_A_MAP, InfoMessageType.ERROR);
            return false;
        }

        // Validate port.
        try {
            int port = getServerPort();
            if (port < 0 || port > 65535) {
                owner.setInfoText(INVALID_PORT, InfoMessageType.ERROR);
                return false;
            }
        } catch (Exception e) {
            owner.setInfoText(INVALID_PORT, InfoMessageType.ERROR);
            return false;
        }

        // Everything is success.
        owner.hideErrorMessages();
        Options.PORT = Integer.valueOf(serverPort.getText());
        return true;
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
