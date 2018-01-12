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
 * StationInfoJPanel.java
 *
 */

package freerails.client.view;

import freerails.client.renderer.RendererRoot;
import freerails.controller.ModelRoot;
import freerails.util.Point2D;
import freerails.world.*;
import freerails.world.cargo.CargoBatchBundle;
import freerails.world.cargo.CargoType;
import freerails.world.cargo.ImmutableCargoBatchBundle;
import freerails.world.player.FreerailsPrincipal;
import freerails.world.station.Station;
import freerails.world.terrain.FullTerrainTile;
import freerails.world.train.WagonType;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * This JPanel displays the supply and demand at a station.
 */

public class StationInfoJPanel extends JPanel implements View, WorldListListener {

    private static final Logger logger = Logger.getLogger(StationInfoJPanel.class.getName());

    private static final long serialVersionUID = 4050759377680150585L;

    private ReadOnlyWorld w;

    private ModelRoot modelRoot;

    private WorldIterator wi;

    /**
     * The index of the cargoBundle associated with this station.
     */
    private int cargoBundleIndex;
    private Serializable lastCargoBundle = null;
    private javax.swing.JButton close;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton nextStation;
    private javax.swing.JButton previousStation;
    private final ComponentListener componentListener = new ComponentAdapter() {
        @Override
        public void componentHidden(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

            int i = wi.getIndex();
            wi.reset();
            if (i != WorldIterator.BEFORE_FIRST) {
                try {
                    wi.gotoIndex(i);
                } catch (NoSuchElementException ex) {
                    logger.info("Exception ignored in StationInfoJPanel (NoSuchElement).");
                    return; // ignore silently
                }
            }
            display();
        }
    };

    public StationInfoJPanel() {
        initComponents();
    }


    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        nextStation = new javax.swing.JButton();
        previousStation = new javax.swing.JButton();
        close = new javax.swing.JButton();

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
        nextStation.addActionListener(this::nextStationActionPerformed);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(nextStation, gridBagConstraints);

        previousStation.setText("<- previous");
        previousStation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        previousStation.addActionListener(this::previousStationActionPerformed);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(previousStation, gridBagConstraints);

        close.setText("close");
        close.setMargin(new java.awt.Insets(0, 0, 0, 0));
        close.setMaximumSize(new java.awt.Dimension(65, 22));
        close.setMinimumSize(new java.awt.Dimension(65, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(close, gridBagConstraints);

    }

    private void previousStationActionPerformed(java.awt.event.ActionEvent evt) {

        // Add your handling code here:
        if (wi.previous()) {
            Point2D p = new Point2D(((Station) wi.getElement()).getStationX(), ((Station) wi.getElement()).getStationY());
            modelRoot.setProperty(ModelRoot.Property.CURSOR_POSITION, p);

            display();
        } else {
            throw new IllegalStateException();
        }
    }

    private void nextStationActionPerformed(java.awt.event.ActionEvent evt) {

        // Add your handling code here:
        if (wi.next()) {
            Point2D p = new Point2D(((Station) wi.getElement()).getStationX(), ((Station) wi.getElement()).getStationY());
            modelRoot.setProperty(ModelRoot.Property.CURSOR_POSITION, p);
            display();
        } else {
            throw new IllegalStateException();
        }

    }

    public void setup(ModelRoot modelRoot, RendererRoot vl, Action closeAction) {
        wi = new NonNullElementWorldIterator(KEY.STATIONS, modelRoot.getWorld(), modelRoot.getPrincipal());
        addComponentListener(componentListener);
        w = modelRoot.getWorld();
        this.modelRoot = modelRoot;
        close.addActionListener(closeAction);
    }

    public void setStation(int stationNumber) {
        wi.gotoIndex(stationNumber);
        display();
    }

    private void display() {

        if (wi.getRowID() > 0) {
            previousStation.setEnabled(true);
        } else {
            previousStation.setEnabled(false);
        }

        if (wi.getRowID() < (wi.size() - 1)) {
            nextStation.setEnabled(true);
        } else {
            nextStation.setEnabled(false);
        }

        int stationNumber = wi.getIndex();
        String label;
        if (stationNumber != WorldIterator.BEFORE_FIRST) {
            Station station = (Station) w.get(modelRoot.getPrincipal(), KEY.STATIONS, stationNumber);
            FullTerrainTile tile = (FullTerrainTile) w.getTile(station.x, station.y);
            String stationTypeName = tile.getTrackPiece().getTrackRule().getTypeName();
            cargoBundleIndex = station.getCargoBundleID();
            CargoBatchBundle cargoWaiting = (ImmutableCargoBatchBundle) w.get(modelRoot.getPrincipal(), KEY.CARGO_BUNDLES, station.getCargoBundleID());

            StringBuilder table1 = new StringBuilder();

            table1.append("<html>");

            table1.append("<h2 align=\"center\">");
            table1.append(station.getStationName());
            table1.append(" (");
            table1.append(stationTypeName);
            table1.append(")</h2>");

            table1.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\"><tr><td>&nbsp;</td>\n    <td>Demand</td>\n    <td>Supplies<br/>(cars/year)</td><td>Ready<br />(loads)</td>  </tr>");

            for (int i = 0; i < w.size(SKEY.CARGO_TYPES); i++) {

                // get the values
                CargoType cargoType = (CargoType) w.get(SKEY.CARGO_TYPES, i);
                String demanded = (station.getDemandForCargo().isCargoDemanded(i) ? "Yes" : "No");

                int amountSupplied = station.getSupply().getSupply(i);
                boolean isSupplied = (amountSupplied > 0);
                String supply = isSupplied ? String.valueOf(amountSupplied / WagonType.UNITS_OF_CARGO_PER_WAGON) : "&nbsp;";

                int amountWaiting = cargoWaiting.getAmountOfType(i);
                String waiting = (amountWaiting > 0) ? String.valueOf(amountWaiting / WagonType.UNITS_OF_CARGO_PER_WAGON) : "&nbsp;";

                // build the html
                if (station.getDemandForCargo().isCargoDemanded(i) || isSupplied) {
                    table1.append("<tr>");
                    table1.append("<td>").append(cargoType.getDisplayName()).append("</td>");
                    table1.append("<td align=center>").append(demanded).append("</td>");
                    table1.append("<td align=center>").append(supply).append("</td>");
                    table1.append("<td align=center>").append(waiting).append("</td>");
                    table1.append("</tr>");
                }

            }
            table1.append("</table>");
            table1.append("</html>");
            label = table1.toString();
        } else {
            cargoBundleIndex = WorldIterator.BEFORE_FIRST;
            label = "<html><h2 align=\"center\">No Station " + "Selected</h2></html>";
        }
        jLabel1.setText(label);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        /* We need to update if the cargo bundle has changed. */
        FreerailsPrincipal playerPrincipal = modelRoot.getPrincipal();

        /*
         * Avoid a array out of bounds exception when there are no stations and
         * the stations tab is visible.
         */
        if (w.boundsContain(playerPrincipal, KEY.CARGO_BUNDLES, cargoBundleIndex)) {
            Serializable currentCargoBundle = w.get(playerPrincipal, KEY.CARGO_BUNDLES, cargoBundleIndex);
            if (lastCargoBundle != currentCargoBundle) {
                display();
                lastCargoBundle = currentCargoBundle;
            }
        }
        super.paintComponent(g);
    }

    private void reactToUpdate(KEY key, int changedIndex, boolean isAddition) {
        if (!isVisible()) {
            return;
        }

        int currentIndex = wi.getIndex();
        if (key == KEY.CARGO_BUNDLES) {
            if (changedIndex == cargoBundleIndex) {
                /* update our cargo bundle */
                display();
            }
        } else if (key == KEY.STATIONS) {
            wi.reset();
            if (currentIndex != WorldIterator.BEFORE_FIRST) {
                if (currentIndex < wi.size()) {
                    wi.gotoIndex(currentIndex);
                } else {
                    currentIndex = WorldIterator.BEFORE_FIRST;
                }
            }
            if (isAddition && wi.getIndex() == WorldIterator.BEFORE_FIRST) {
                if (wi.next()) {
                    display();
                }
            }

            if (currentIndex == changedIndex || currentIndex == WorldIterator.BEFORE_FIRST) {
                display();
            }
        }

    }

    public void listUpdated(KEY key, int index, FreerailsPrincipal principal) {
        if (modelRoot.getPrincipal().equals(principal)) reactToUpdate(key, index, false);
    }

    public void itemAdded(KEY key, int index, FreerailsPrincipal principal) {
        if (modelRoot.getPrincipal().equals(principal)) reactToUpdate(key, index, true);
    }

    public void itemRemoved(KEY key, int index, FreerailsPrincipal principal) {
        if (modelRoot.getPrincipal().equals(principal)) reactToUpdate(key, index, false);
    }

    void removeCloseButton() {
        remove(close);
    }

}
