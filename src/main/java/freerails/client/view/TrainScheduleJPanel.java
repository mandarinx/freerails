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

package freerails.client.view;

import freerails.client.renderer.RendererRoot;
import freerails.controller.ModelRoot;
import freerails.move.ChangeTrainScheduleMove;
import freerails.move.Move;
import freerails.util.ImmutableList;
import freerails.util.Utils;
import freerails.world.*;
import freerails.world.cargo.CargoType;
import freerails.world.player.FreerailsPrincipal;
import freerails.world.train.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.NoSuchElementException;

/**
 * This JPanel displays a train's schedule and provides controls that let you
 * edit it.
 */

public class TrainScheduleJPanel extends javax.swing.JPanel implements View, WorldListListener {

    private static final long serialVersionUID = 3762248626113884214L;

    private static final Logger logger = Logger.getLogger(TrainScheduleJPanel.class.getName());
    javax.swing.JButton addStationJButton;
    javax.swing.JMenu addWagonJMenu;
    javax.swing.JMenuItem autoConsistJMenuItem;
    javax.swing.JMenu changeConsistJMenu;
    javax.swing.JMenuItem changeStation;
    javax.swing.JMenuItem dontWaitJMenuItem;
    javax.swing.JPopupMenu editOrderJPopupMenu;
    javax.swing.JMenuItem engineOnlyJMenuItem;
    javax.swing.JMenuItem gotoStationJMenuItem;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JSeparator jSeparator1;
    javax.swing.JSeparator jSeparator2;
    javax.swing.JMenuItem noChangeJMenuItem;
    javax.swing.JList orders;
    javax.swing.JButton priorityOrdersJButton;
    javax.swing.JMenuItem pullUpJMenuItem;
    javax.swing.JMenuItem pushDownJMenuItem;
    javax.swing.JMenuItem removeAllJMenuItem;
    javax.swing.JMenuItem removeLastJMenuItem;
    javax.swing.JMenuItem removeStationJMenuItem;
    javax.swing.JMenu removeWagonsJMenu;
    freerails.client.view.SelectStationJPanel selectStationJPanel1;
    javax.swing.JPopupMenu selectStationJPopupMenu;
    freerails.client.view.TrainOrderJPanel trainOrderJPanel1;
    javax.swing.JMenu waitJMenu;
    javax.swing.JMenuItem waitUntilFullJMenuItem;
    private int trainNumber = -1;
    private int scheduleID = -1;
    private TrainOrdersListModel listModel;
    private ModelRoot modelRoot;
    private RendererRoot vl;

    public TrainScheduleJPanel() {
        initComponents();
    }


    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        trainOrderJPanel1 = new freerails.client.view.TrainOrderJPanel();
        editOrderJPopupMenu = new javax.swing.JPopupMenu();
        gotoStationJMenuItem = new javax.swing.JMenuItem();
        changeStation = new javax.swing.JMenuItem();
        removeStationJMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        addWagonJMenu = new javax.swing.JMenu();
        removeWagonsJMenu = new javax.swing.JMenu();
        removeLastJMenuItem = new javax.swing.JMenuItem();
        removeAllJMenuItem = new javax.swing.JMenuItem();
        changeConsistJMenu = new javax.swing.JMenu();
        noChangeJMenuItem = new javax.swing.JMenuItem();
        engineOnlyJMenuItem = new javax.swing.JMenuItem();
        autoConsistJMenuItem = new javax.swing.JMenuItem();
        waitJMenu = new javax.swing.JMenu();
        dontWaitJMenuItem = new javax.swing.JMenuItem();
        waitUntilFullJMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        pullUpJMenuItem = new javax.swing.JMenuItem();
        pushDownJMenuItem = new javax.swing.JMenuItem();
        selectStationJPanel1 = new freerails.client.view.SelectStationJPanel();
        selectStationJPopupMenu = new javax.swing.JPopupMenu();
        selectStationJPopupMenu.add(selectStationJPanel1);
        addStationJButton = new javax.swing.JButton();
        priorityOrdersJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        orders = new javax.swing.JList();

        gotoStationJMenuItem.setText("Goto station");
        gotoStationJMenuItem.addActionListener(this::gotoStationJMenuItemActionPerformed);

        editOrderJPopupMenu.add(gotoStationJMenuItem);

        changeStation.setText("Change Station");
        changeStation.addActionListener(this::changeStationActionPerformed);

        editOrderJPopupMenu.add(changeStation);

        removeStationJMenuItem.setText("Remove station");
        removeStationJMenuItem.addActionListener(this::removeStationJMenuItemActionPerformed);

        editOrderJPopupMenu.add(removeStationJMenuItem);

        editOrderJPopupMenu.add(jSeparator1);

        addWagonJMenu.setText("Add Wagon");
        editOrderJPopupMenu.add(addWagonJMenu);

        removeWagonsJMenu.setText("Remove wagon(s)");
        removeLastJMenuItem.setText("Remove last");
        removeLastJMenuItem.addActionListener(this::removeLastJMenuItemActionPerformed);

        removeWagonsJMenu.add(removeLastJMenuItem);

        removeAllJMenuItem.setText("Remove all wagons");
        removeAllJMenuItem.addActionListener(this::removeAllJMenuItemActionPerformed);

        removeWagonsJMenu.add(removeAllJMenuItem);

        editOrderJPopupMenu.add(removeWagonsJMenu);

        changeConsistJMenu.setText("Change consist to..");
        noChangeJMenuItem.setText("'No change'");
        noChangeJMenuItem.addActionListener(this::noChangeJMenuItemActionPerformed);

        changeConsistJMenu.add(noChangeJMenuItem);

        engineOnlyJMenuItem.setText("Engine only");
        engineOnlyJMenuItem.addActionListener(this::engineOnlyJMenuItemActionPerformed);

        changeConsistJMenu.add(engineOnlyJMenuItem);

        autoConsistJMenuItem.setText("Choose wagons automatically");
        autoConsistJMenuItem.addActionListener(this::autoConsistJMenuItemActionPerformed);

        changeConsistJMenu.add(autoConsistJMenuItem);

        editOrderJPopupMenu.add(changeConsistJMenu);

        waitJMenu.setText("Wait at station");
        dontWaitJMenuItem.setText("Don't wait");
        dontWaitJMenuItem.addActionListener(this::dontWaitJMenuItemActionPerformed);

        waitJMenu.add(dontWaitJMenuItem);

        waitUntilFullJMenuItem.setText("Wait until full");
        waitUntilFullJMenuItem.addActionListener(this::waitUntilFullJMenuItemActionPerformed);

        waitJMenu.add(waitUntilFullJMenuItem);

        editOrderJPopupMenu.add(waitJMenu);

        editOrderJPopupMenu.add(jSeparator2);

        pullUpJMenuItem.setText("Pull up");
        pullUpJMenuItem.addActionListener(this::pullUpJMenuItemActionPerformed);

        editOrderJPopupMenu.add(pullUpJMenuItem);

        pushDownJMenuItem.setText("Push down");
        pushDownJMenuItem.addActionListener(this::pushDownJMenuItemActionPerformed);

        editOrderJPopupMenu.add(pushDownJMenuItem);

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.TitledBorder("Schedule"));
        addStationJButton.setText("Add Station");
        addStationJButton.addActionListener(this::addStationJButtonActionPerformed);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(addStationJButton, gridBagConstraints);

        priorityOrdersJButton.setText("Add Priority Orders");
        priorityOrdersJButton.addActionListener(this::priorityOrdersJButtonActionPerformed);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(priorityOrdersJButton, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(280, 160));
        orders.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        orders.setCellRenderer(trainOrderJPanel1);
        orders.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                ordersKeyPressed(e);
            }
        });
        orders.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                ordersMouseClicked(e);
            }
        });

        jScrollPane1.setViewportView(orders);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jScrollPane1, gridBagConstraints);

    }

    private void ordersKeyPressed(java.awt.event.KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_O: {
                // Add priority orders
                priorityOrdersJButtonActionPerformed(null);
                break;
            }
            case KeyEvent.VK_N: {
                // Add station
                addStationJButtonActionPerformed(null);
                break;
            }
            default: {
                // do nothing.
            }
        }

        int orderNumber = orders.getSelectedIndex();
        if (orderNumber == -1) {
            // No order is selected.
            return;
        }
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_G: {
                // Goto station.
                gotoStationJMenuItemActionPerformed(null);
                break;
            }
            case KeyEvent.VK_S: {
                // Change station
                showSelectStation(getSchedule(), orderNumber);
                break;
            }
            case KeyEvent.VK_A: {
                // Auto schedule
                setAutoConsist();
                break;
            }
            case KeyEvent.VK_C: {
                // Change add wagon

                break;
            }
            case KeyEvent.VK_DELETE: {
                // Remove station
                removeStationJMenuItemActionPerformed(null);
                break;
            }
            case KeyEvent.VK_BACK_SPACE: {
                // Remove last wagon
                removeLastWagon();
                break;
            }

            case KeyEvent.VK_W: {
                // toggle wait until full
                MutableSchedule s = getSchedule();
                TrainOrdersModel order = s.getOrder(orderNumber);
                setWaitUntilFull(!order.waitUntilFull);
                break;
            }
            default: {
                // do nothing.
            }
        }
        listModel.fireRefresh();

    }

    private void autoConsistJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        setAutoConsist();
    }

    private void changeStationActionPerformed(java.awt.event.ActionEvent evt) {
        int orderNumber = orders.getSelectedIndex();
        showSelectStation(getSchedule(), orderNumber);
    }

    private void removeAllJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        removeAllWagons();
    }

    private void removeLastJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        removeLastWagon();
    }

    private void waitUntilFullJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        setWaitUntilFull(true);
    }

    private void dontWaitJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        setWaitUntilFull(false);
    }

    private void engineOnlyJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        removeAllWagons();
    }

    private void noChangeJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        noChange();
    }

    private void priorityOrdersJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        MutableSchedule s = getSchedule();
        try {
            s.setPriorityOrders(new TrainOrdersModel(getFirstStationID(), null, false, false));// TODO fix bug
            showSelectStation(s, Schedule.PRIORITY_ORDERS);
        } catch (NoSuchElementException e) {
            logger.warn("No stations exist so can't add station to schedule!");
        }
    }

    private void addStationJButtonActionPerformed(java.awt.event.ActionEvent evt) {
        MutableSchedule s = getSchedule();
        try {
            int newOrderNumber = s.addOrder(new TrainOrdersModel(getFirstStationID(), null, false, false)); // TODO fix bug
            showSelectStation(s, newOrderNumber);
        } catch (NoSuchElementException e) {
            logger.warn("No stations exist so can't add station to schedule!");
        }
    }

    private void removeStationJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        MutableSchedule s = getSchedule();
        int i = orders.getSelectedIndex();
        s.removeOrder(i);
        sendUpdateMove(s);
    }

    private void gotoStationJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        MutableSchedule s = getSchedule();
        int i = orders.getSelectedIndex();
        s.setOrderToGoto(i);
        sendUpdateMove(s);
    }

    private void pushDownJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        MutableSchedule s = getSchedule();
        int i = orders.getSelectedIndex();
        s.pushDown(i);
        sendUpdateMove(s);
        orders.setSelectedIndex(i + 1);
    }

    private void ordersMouseClicked(java.awt.event.MouseEvent evt) {
        int i = orders.getSelectedIndex();
        MutableSchedule s = getSchedule();
        if (i >= s.getNumOrders()) {
            // The selected index does not exist!
            // For some reason, the JList hasn't updated yet.
            i = -1;
        }
        if (-1 != i && java.awt.event.MouseEvent.BUTTON3 == evt.getButton()) {
            // If an element is select and the right button is pressed.
            TrainOrdersModel order = s.getOrder(i);
            pullUpJMenuItem.setEnabled(s.canPullUp(i));
            pushDownJMenuItem.setEnabled(s.canPushDown(i));
            gotoStationJMenuItem.setEnabled(s.canSetGotoStation(i));
            removeWagonsJMenu.setEnabled(order.orderHasWagons());
            waitJMenu.setEnabled(order.orderHasWagons());
            addWagonJMenu.setEnabled(order.hasLessThanMaximumNumberOfWagons());
            setupWagonsPopup();
            editOrderJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    private void pullUpJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        MutableSchedule s = getSchedule();
        int i = orders.getSelectedIndex();
        s.pullUp(i);
        sendUpdateMove(s);
        orders.setSelectedIndex(i - 1);
    }

    public void setup(ModelRoot modelRoot, RendererRoot vl, Action closeAction) {
        trainOrderJPanel1.setup(modelRoot, vl, null);
        this.modelRoot = modelRoot;
        this.vl = vl;

        // This actionListener is fired by the select station popup when a
        // station is selected.
        Action action = new AbstractAction() {

            private static final long serialVersionUID = -2096909872676721636L;

            public void actionPerformed(ActionEvent e) {
                sendUpdateMove(selectStationJPanel1.generateNewSchedule());
                selectStationJPopupMenu.setVisible(false);
                listModel.fireRefresh();
                orders.requestFocus();

            }
        };
        selectStationJPanel1.setup(modelRoot, vl, action);
    }

    public void display(int newTrainNumber) {
        trainNumber = newTrainNumber;
        FreerailsPrincipal principal = modelRoot.getPrincipal();
        ReadOnlyWorld w = modelRoot.getWorld();
        TrainModel train = (TrainModel) w.get(principal, KEY.TRAINS, newTrainNumber);
        scheduleID = train.getScheduleID();
        listModel = new TrainOrdersListModel(w, newTrainNumber, principal);
        orders.setModel(listModel);
        orders.setFixedCellWidth(250);
        listModel.fireRefresh();
        enableButtons();
    }

    private void enableButtons() {
        MutableSchedule s = getSchedule();
        addStationJButton.setEnabled(s.canAddOrder());

        // Only one set of priority orders are allowed.
        priorityOrdersJButton.setEnabled(!s.hasPriorityOrders());
    }

    private MutableSchedule getSchedule() {
        FreerailsPrincipal principal = modelRoot.getPrincipal();
        ReadOnlyWorld w = modelRoot.getWorld();
        TrainModel train = (TrainModel) w.get(principal, KEY.TRAINS, trainNumber);
        ImmutableSchedule immutableSchedule = (ImmutableSchedule) w.get(principal, KEY.TRAIN_SCHEDULES, train.getScheduleID());
        return new MutableSchedule(immutableSchedule);
    }

    /**
     * Since stations can be removed, we should not assume that station 0
     * exists: this method returns the id of the first station that exists.
     */
    private int getFirstStationID() {
        WorldIterator stations = new NonNullElementWorldIterator(KEY.STATIONS, modelRoot.getWorld(), modelRoot.getPrincipal());
        if (stations.next()) {
            return stations.getIndex();
        }
        throw new NoSuchElementException();
    }

    private void setupWagonsPopup() {
        addWagonJMenu.removeAll(); // Remove existing menu items.
        NonNullElementWorldIterator cargoTypes = new NonNullElementWorldIterator(SKEY.CARGO_TYPES, modelRoot.getWorld());

        while (cargoTypes.next()) {
            final CargoType wagonType = (CargoType) cargoTypes.getElement();
            JMenuItem wagonMenuItem = new JMenuItem();
            final int wagonTypeNumber = cargoTypes.getIndex();
            wagonMenuItem.setText(wagonType.getDisplayName());
            Image image = vl.getWagonImages(wagonTypeNumber).getSideOnImage();
            int height = image.getHeight(null);
            int width = image.getWidth(null);
            int scale = height / 10;
            Icon icon = new ImageIcon(image.getScaledInstance(width / scale, height / scale, Image.SCALE_FAST));
            wagonMenuItem.setIcon(icon);
            wagonMenuItem.addActionListener(e -> addWagon(wagonTypeNumber));
            addWagonJMenu.add(wagonMenuItem);
        }
    }

    private void noChange() {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        newOrders = new TrainOrdersModel(oldOrders.getStationID(), null, false, false);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void setWaitUntilFull(boolean b) {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        // If auto-consist is set do nothing
        if (oldOrders.autoConsist) return;
        // If no-change is set do nothing
        if (oldOrders.consist == null) return;
        boolean autoConsist = false;
        newOrders = new TrainOrdersModel(oldOrders.getStationID(), oldOrders.consist, b, autoConsist);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void setAutoConsist() {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        newOrders = new TrainOrdersModel(oldOrders.getStationID(), null, false, true);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void addWagon(int wagonTypeNumber) {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        Integer[] newConsist;
        // The consist will be null if old orders were 'no change'.
        if (null != oldOrders.consist) {
            int oldLength = oldOrders.consist.size();
            newConsist = new Integer[oldLength + 1];
            // Copy existing wagons
            for (int i = 0; i < oldLength; i++) {
                newConsist[i] = oldOrders.consist.get(i);
            }
            // Then add specified wagon.
            newConsist[oldLength] = wagonTypeNumber;
        } else {
            newConsist = new Integer[]{wagonTypeNumber};
        }
        newOrders = new TrainOrdersModel(oldOrders.getStationID(), new ImmutableList<>(newConsist), oldOrders.getWaitUntilFull(), false);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void removeAllWagons() {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        newOrders = new TrainOrdersModel(oldOrders.getStationID(), new ImmutableList<>(), false, false);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void removeLastWagon() {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        if (oldOrders.consist == null) {
            return;
        }
        ImmutableList<Integer> oldConsist = oldOrders.consist;
        int newLength = oldConsist.size() - 1;
        if (newLength < 0) {
            // No wagons to remove!
            return;
        }
        ImmutableList<Integer> newConsist = Utils.removeLastOfImmutableList(oldConsist);

        newOrders = new TrainOrdersModel(oldOrders.getStationID(), newConsist, oldOrders.waitUntilFull, false);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void sendUpdateMove(MutableSchedule mutableSchedule) {
        FreerailsPrincipal principal = modelRoot.getPrincipal();
        ReadOnlyWorld w = modelRoot.getWorld();
        TrainModel train = (TrainModel) w.get(principal, KEY.TRAINS, trainNumber);
        // int scheduleID = train.getScheduleID();
        assert (scheduleID == train.getScheduleID());
        ImmutableSchedule before = (ImmutableSchedule) w.get(principal, KEY.TRAIN_SCHEDULES, scheduleID);
        ImmutableSchedule after = mutableSchedule.toImmutableSchedule();
        Move m = new ChangeTrainScheduleMove(scheduleID, before, after, principal);
        modelRoot.doMove(m);
    }

    public void listUpdated(KEY key, int index, FreerailsPrincipal principal) {
        if (KEY.TRAIN_SCHEDULES == key && scheduleID == index) {
            listModel.fireRefresh();
            enableButtons();
        }
    }

    public void itemAdded(KEY key, int index, FreerailsPrincipal principal) {
        // do nothing.
    }

    public void itemRemoved(KEY key, int index, FreerailsPrincipal principal) {
        // do nothing.
    }

    /**
     * Show the popup that lets the user select a station, called when a new
     * scheduled stop is added and when an existing scheduled stop is changed.
     */
    private void showSelectStation(MutableSchedule schedule, int orderNumber) {
        selectStationJPanel1.display(schedule, orderNumber);

        // Show the select station popup in the middle of the window.
        Container topLevelAncestor = getTopLevelAncestor();
        Dimension d = topLevelAncestor.getSize();
        Dimension d2 = selectStationJPopupMenu.getPreferredSize();
        int x = Math.max((d.width - d2.width) / 2, 0);
        int y = Math.max((d.height - d2.height) / 2, 0);
        selectStationJPopupMenu.show(topLevelAncestor, x, y);
        selectStationJPanel1.requestFocus();
    }

}
