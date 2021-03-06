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
 * SelectStationPanel.java
 *
 */

package freerails.client.view;

import freerails.client.KeyCodeToOneTileMoveVector;
import freerails.client.renderer.RendererRoot;
import freerails.client.ModelRoot;
import freerails.model.station.StationUtils;
import freerails.model.train.schedule.TrainOrder;
import freerails.util.Vec2D;
import freerails.model.world.UnmodifiableWorld;
import freerails.model.player.Player;
import freerails.model.station.Station;
import freerails.model.terrain.TerrainTile;
import freerails.model.terrain.TileTransition;
import freerails.model.train.schedule.Schedule;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.NoSuchElementException;

/**
 * Lets the user select a station from a map and add it to a train
 * schedule.
 */
public class SelectStationPanel extends JPanel implements View {

    private static final long serialVersionUID = 3258411750662877488L;
    private UnmodifiableWorld world;
    private ActionListener submitButtonCallBack;
    private int selectedStationID = 0;
    private int selectedOrderNumber = 1;
    private Schedule schedule;
    private Rectangle visableMapTiles = new Rectangle();
    private double scale = 1;
    private boolean needsUpdating = true;
    private Player player;
    private CargoWaitingAndDemandedPanel cargoWaitingAndDemandedPanel1;
    private JLabel label1;
    
    public SelectStationPanel() {
        GridBagConstraints gridBagConstraints;

        cargoWaitingAndDemandedPanel1 = new CargoWaitingAndDemandedPanel();
        label1 = new JLabel();

        setLayout(new GridBagLayout());

        setPreferredSize(new Dimension(500, 350));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                formComponentResized(e);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                formComponentShown(e);
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                formKeyPressed(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                formMouseClicked(e);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                formMouseMoved(e);
            }
        });

        cargoWaitingAndDemandedPanel1.setBorder(new LineBorder(new Color(0, 0, 0)));
        cargoWaitingAndDemandedPanel1.setPreferredSize(new Dimension(165, 300));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(7, 7, 7, 7);
        add(cargoWaitingAndDemandedPanel1, gridBagConstraints);

        label1.setText("Train #1 Stop 1");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(label1, gridBagConstraints);
    }

    private void formComponentShown(ComponentEvent evt) {
        setZoom();
    }

    private void formMouseClicked(MouseEvent evt) {
        formMouseMoved(evt);
        needsUpdating = true;
        submitButtonCallBack.actionPerformed(null);
    }

    private void formKeyPressed(KeyEvent evt) {
        try {
            TileTransition tileTransition = KeyCodeToOneTileMoveVector.getInstanceMappedToKey(evt.getKeyCode());
            // now find nearest station in direction of the vector.
            int stationId = StationUtils.findNearestStationInDirection(world, player, selectedStationID, tileTransition);

            if (selectedStationID != stationId && stationId != StationUtils.NOT_FOUND) {
                selectedStationID = stationId;
                cargoWaitingAndDemandedPanel1.display(selectedStationID);
                validate();
                repaint();
            }
        } catch (NoSuchElementException e) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                needsUpdating = true;
                submitButtonCallBack.actionPerformed(null);
            }
            // The key pressed isn't mapped to a OneTileMoveVector so do
            // nothing.
        }
    }

    private void formComponentResized(ComponentEvent evt) {
        setZoom();
    }

    private void formMouseMoved(MouseEvent evt) {
        // Add your handling code here:
        double x = evt.getX();
        x = x / scale + visableMapTiles.x;
        double y = evt.getY();
        y = y / scale + visableMapTiles.y;

        int station = StationUtils.findNearestStation(world, player, new Vec2D((int)x, (int)y));

        if (selectedStationID != station && station != StationUtils.NOT_FOUND) {
            selectedStationID = station;
            cargoWaitingAndDemandedPanel1.display(selectedStationID);
            validate();
            repaint();
        }
    }

    public void display(Schedule newSchedule, int orderNumber) {
        schedule = newSchedule;
        selectedOrderNumber = orderNumber;
        TrainOrder order = newSchedule.getOrder(selectedOrderNumber);
        selectedStationID = order.getStationID();

        // Set the text on the title JLabel.
        label1.setText("Stop " + (selectedOrderNumber + 1));

        // Set the station info panel to show the current selected station.
        cargoWaitingAndDemandedPanel1.display(selectedStationID);
    }

    /**
     * Sets the zoom based on the size of the component and the positions of the
     * stations.
     */
    private void setZoom() {
        Rectangle mapRect = getBounds();
        Rectangle r = cargoWaitingAndDemandedPanel1.getBounds();
        mapRect.width -= r.width;

        Vec2D topLeft = new Vec2D(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vec2D bottomRight = new Vec2D(Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (Station station: world.getStations(player)) {
            topLeft = Vec2D.min(topLeft, station.getLocation());
            bottomRight = Vec2D.max(bottomRight, station.getLocation());
        }
        Vec2D margin = new Vec2D(10, 10);
        topLeft = Vec2D.subtract(topLeft, margin);
        bottomRight = Vec2D.add(bottomRight, margin);

        visableMapTiles = new Rectangle(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
        boolean heightConstraintBinds = visableMapTiles.getHeight() / visableMapTiles.getWidth() > mapRect.getHeight() / mapRect.getWidth();
        if (heightConstraintBinds) {
            scale = mapRect.getHeight() / visableMapTiles.getHeight();
        } else {
            scale = mapRect.getWidth() / visableMapTiles.getWidth();
        }
        needsUpdating = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (needsUpdating) {
            setZoom();
        }

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Draw track
        g2.setColor(Color.BLACK);
        Vec2D mapSize = world.getMapSize();
        for (int x = Math.max(0, visableMapTiles.x); x < Math.min(visableMapTiles.width + visableMapTiles.x, mapSize.x); x++) {
            for (int y = Math.max(0, visableMapTiles.y); y < Math.min(visableMapTiles.height + visableMapTiles.y, mapSize.y); y++) {
                TerrainTile terrainTile = world.getTile(new Vec2D(x, y));
                if (terrainTile.getTrackPiece() != null) {
                    double xDouble = x - visableMapTiles.x;
                    xDouble = xDouble * scale;
                    double yDouble = y - visableMapTiles.y;
                    yDouble = yDouble * scale;
                    g.drawRect((int) xDouble, (int) yDouble, 1, 1);
                }
            }
        }
        // Draw stations
        for (Station station: world.getStations(player)) {

            /*
             * (1) The selected station is drawn green. (2) Non-selected
             * stations which are on the schedule are drawn blue. (3) Other
             * stations are drawn white. (4) If, for instance, station X is the
             * first stop on the schedule, "1" is drawn above the station. (5)
             * If, for instance, station X is the first and third stop on the
             * schedule, "1, 3" is drawn above the station. (6) The stop numbers
             * drawn above the stations are drawn using the same colour as used
             * to draw the station.
             */
            StringBuilder stopNumbersString = new StringBuilder();
            boolean stationIsOnSchedule = false;
            for (int orderNumber = 0; orderNumber < schedule.getNumberOfOrders(); orderNumber++) {
                int stationID = orderNumber == selectedOrderNumber ? selectedStationID : schedule.getOrder(orderNumber).getStationID();
                if (station.getId() == stationID) {
                    if (stationIsOnSchedule) {
                        stopNumbersString.append(", ").append((orderNumber + 1));
                    } else {
                        stopNumbersString = new StringBuilder(String.valueOf(orderNumber + 1));
                    }
                    stationIsOnSchedule = true;
                }
            }

            // TODO Vector arithmetic
            double x = station.getLocation().x - visableMapTiles.x;
            x = x * scale;
            double y = station.getLocation().y - visableMapTiles.y;
            y = y * scale;
            int xInt = (int) x;
            int yInt = (int) y;

            if (stationIsOnSchedule) {
                // TODO is the selectedStationID the right one, or is it a running number in a list?
                if (station.getId() == selectedStationID) {
                    g2.setColor(Color.GREEN);
                } else {
                    g2.setColor(Color.BLUE);
                }
                g2.drawString(stopNumbersString.toString(), xInt, yInt - 4);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.fillRect(xInt, yInt, 10, 10);
        }
    }

    @Override
    public void setup(ModelRoot modelRoot, RendererRoot rendererRoot, Action closeAction) {
        cargoWaitingAndDemandedPanel1.setup(modelRoot, rendererRoot, null);
        world = modelRoot.getWorld();
        submitButtonCallBack = closeAction;
        player = modelRoot.getPlayer();
    }

    public Schedule generateNewSchedule() {
        TrainOrder oldOrders, newOrders;
        oldOrders = schedule.getOrder(selectedOrderNumber);
        newOrders = new TrainOrder(selectedStationID, oldOrders.getConsist(), oldOrders.getWaitUntilFull(), oldOrders.isAutoConsist());
        schedule.setOrder(selectedOrderNumber, newOrders);
        return schedule;
    }

}
