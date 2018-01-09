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
import freerails.util.ImInts;
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
@SuppressWarnings("unused")
public class TrainScheduleJPanel extends javax.swing.JPanel implements View,
        WorldListListener {

    private static final long serialVersionUID = 3762248626113884214L;

    private static final Logger logger = Logger
            .getLogger(TrainScheduleJPanel.class.getName());
    // Variables declaration - do not modify//GEN-BEGIN:variables
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {// GEN-BEGIN:initComponents
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
        this.selectStationJPopupMenu.add(selectStationJPanel1);
        addStationJButton = new javax.swing.JButton();
        priorityOrdersJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        orders = new javax.swing.JList();

        gotoStationJMenuItem.setText("Goto station");
        gotoStationJMenuItem
                .addActionListener(this::gotoStationJMenuItemActionPerformed);

        editOrderJPopupMenu.add(gotoStationJMenuItem);

        changeStation.setText("Change Station");
        changeStation.addActionListener(this::changeStationActionPerformed);

        editOrderJPopupMenu.add(changeStation);

        removeStationJMenuItem.setText("Remove station");
        removeStationJMenuItem
                .addActionListener(this::removeStationJMenuItemActionPerformed);

        editOrderJPopupMenu.add(removeStationJMenuItem);

        editOrderJPopupMenu.add(jSeparator1);

        addWagonJMenu.setText("Add Wagon");
        editOrderJPopupMenu.add(addWagonJMenu);

        removeWagonsJMenu.setText("Remove wagon(s)");
        removeLastJMenuItem.setText("Remove last");
        removeLastJMenuItem
                .addActionListener(this::removeLastJMenuItemActionPerformed);

        removeWagonsJMenu.add(removeLastJMenuItem);

        removeAllJMenuItem.setText("Remove all wagons");
        removeAllJMenuItem
                .addActionListener(this::removeAllJMenuItemActionPerformed);

        removeWagonsJMenu.add(removeAllJMenuItem);

        editOrderJPopupMenu.add(removeWagonsJMenu);

        changeConsistJMenu.setText("Change consist to..");
        noChangeJMenuItem.setText("'No change'");
        noChangeJMenuItem
                .addActionListener(this::noChangeJMenuItemActionPerformed);

        changeConsistJMenu.add(noChangeJMenuItem);

        engineOnlyJMenuItem.setText("Engine only");
        engineOnlyJMenuItem
                .addActionListener(this::engineOnlyJMenuItemActionPerformed);

        changeConsistJMenu.add(engineOnlyJMenuItem);

        autoConsistJMenuItem.setText("Choose wagons automatically");
        autoConsistJMenuItem
                .addActionListener(this::autoConsistJMenuItemActionPerformed);

        changeConsistJMenu.add(autoConsistJMenuItem);

        editOrderJPopupMenu.add(changeConsistJMenu);

        waitJMenu.setText("Wait at station");
        dontWaitJMenuItem.setText("Don't wait");
        dontWaitJMenuItem
                .addActionListener(this::dontWaitJMenuItemActionPerformed);

        waitJMenu.add(dontWaitJMenuItem);

        waitUntilFullJMenuItem.setText("Wait until full");
        waitUntilFullJMenuItem
                .addActionListener(this::waitUntilFullJMenuItemActionPerformed);

        waitJMenu.add(waitUntilFullJMenuItem);

        editOrderJPopupMenu.add(waitJMenu);

        editOrderJPopupMenu.add(jSeparator2);

        pullUpJMenuItem.setText("Pull up");
        pullUpJMenuItem.addActionListener(this::pullUpJMenuItemActionPerformed);

        editOrderJPopupMenu.add(pullUpJMenuItem);

        pushDownJMenuItem.setText("Push down");
        pushDownJMenuItem
                .addActionListener(this::pushDownJMenuItemActionPerformed);

        editOrderJPopupMenu.add(pushDownJMenuItem);

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.TitledBorder("Schedule"));
        addStationJButton.setText("Add Station");
        addStationJButton
                .addActionListener(this::addStationJButtonActionPerformed);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(addStationJButton, gridBagConstraints);

        priorityOrdersJButton.setText("Add Priority Orders");
        priorityOrdersJButton
                .addActionListener(this::priorityOrdersJButtonActionPerformed);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(priorityOrdersJButton, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(280, 160));
        orders
                .setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        orders.setCellRenderer(trainOrderJPanel1);
        orders.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ordersKeyPressed(evt);
            }
        });
        orders.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ordersMouseClicked(evt);
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

    }// GEN-END:initComponents

    private void ordersKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_ordersKeyPressed
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

        int orderNumber = this.orders.getSelectedIndex();
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
                showSelectStation(this.getSchedule(), orderNumber);
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

    }// GEN-LAST:event_ordersKeyPressed

    private void autoConsistJMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_autoConsistJMenuItemActionPerformed
        setAutoConsist();
    }// GEN-LAST:event_autoConsistJMenuItemActionPerformed

    private void changeStationActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_changeStationActionPerformed
        int orderNumber = this.orders.getSelectedIndex();
        showSelectStation(this.getSchedule(), orderNumber);
    }// GEN-LAST:event_changeStationActionPerformed

    private void removeAllJMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeAllJMenuItemActionPerformed
        removeAllWagons();
    }// GEN-LAST:event_removeAllJMenuItemActionPerformed

    private void removeLastJMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeLastJMenuItemActionPerformed
        removeLastWagon();
    }// GEN-LAST:event_removeLastJMenuItemActionPerformed

    private void waitUntilFullJMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_waitUntilFullJMenuItemActionPerformed
        setWaitUntilFull(true);
    }// GEN-LAST:event_waitUntilFullJMenuItemActionPerformed

    private void dontWaitJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_dontWaitJMenuItemActionPerformed
        setWaitUntilFull(false);
    }// GEN-LAST:event_dontWaitJMenuItemActionPerformed

    private void engineOnlyJMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_engineOnlyJMenuItemActionPerformed
        removeAllWagons();
    }// GEN-LAST:event_engineOnlyJMenuItemActionPerformed

    private void noChangeJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_noChangeJMenuItemActionPerformed
        noChange();
    }// GEN-LAST:event_noChangeJMenuItemActionPerformed

    private void priorityOrdersJButtonActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_priorityOrdersJButtonActionPerformed
        MutableSchedule s = getSchedule();
        try {
            s.setPriorityOrders(new TrainOrdersModel(getFirstStationID(), null,
                    false, false));// TODO fix bug
            showSelectStation(s, Schedule.PRIORITY_ORDERS);
        } catch (NoSuchElementException e) {
            logger.warn("No stations exist so can't add station to schedule!");
        }
    }// GEN-LAST:event_priorityOrdersJButtonActionPerformed

    private void addStationJButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addStationJButtonActionPerformed
        MutableSchedule s = getSchedule();
        try {
            int newOrderNumber = s.addOrder(new TrainOrdersModel(
                    getFirstStationID(), null, false, false)); // TODO fix bug
            showSelectStation(s, newOrderNumber);
        } catch (NoSuchElementException e) {
            logger.warn("No stations exist so can't add station to schedule!");
        }
    }// GEN-LAST:event_addStationJButtonActionPerformed

    private void removeStationJMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeStationJMenuItemActionPerformed
        MutableSchedule s = getSchedule();
        int i = orders.getSelectedIndex();
        s.removeOrder(i);
        sendUpdateMove(s);
    }// GEN-LAST:event_removeStationJMenuItemActionPerformed

    private void gotoStationJMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gotoStationJMenuItemActionPerformed
        MutableSchedule s = getSchedule();
        int i = orders.getSelectedIndex();
        s.setOrderToGoto(i);
        sendUpdateMove(s);
    }// GEN-LAST:event_gotoStationJMenuItemActionPerformed

    private void pushDownJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pushDownJMenuItemActionPerformed
        MutableSchedule s = getSchedule();
        int i = orders.getSelectedIndex();
        s.pushDown(i);
        sendUpdateMove(s);
        orders.setSelectedIndex(i + 1);
    }// GEN-LAST:event_pushDownJMenuItemActionPerformed

    private void ordersMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_ordersMouseClicked
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
            addWagonJMenu.setEnabled(order.hasLessThanMaxiumNumberOfWagons());
            setupWagonsPopup();
            this.editOrderJPopupMenu.show(evt.getComponent(), evt.getX(), evt
                    .getY());
        }
    }// GEN-LAST:event_ordersMouseClicked

    private void pullUpJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pullUpJMenuItemActionPerformed
        MutableSchedule s = getSchedule();
        int i = orders.getSelectedIndex();
        s.pullUp(i);
        sendUpdateMove(s);
        orders.setSelectedIndex(i - 1);
    }// GEN-LAST:event_pullUpJMenuItemActionPerformed

    public void setup(ModelRoot mr, RendererRoot vl, Action al) {
        trainOrderJPanel1.setup(mr, vl, null);
        this.modelRoot = mr;
        this.vl = vl;

        // This actionListener is fired by the select station popup when a
        // station is selected.
        Action action = new AbstractAction() {

            public void actionPerformed(ActionEvent evt) {
                sendUpdateMove(selectStationJPanel1.generateNewSchedule());
                selectStationJPopupMenu.setVisible(false);
                listModel.fireRefresh();
                orders.requestFocus();

            }
        };
        this.selectStationJPanel1.setup(mr, vl, action);
    }

    public void display(int newTrainNumber) {
        this.trainNumber = newTrainNumber;
        FreerailsPrincipal principal = modelRoot.getPrincipal();
        ReadOnlyWorld w = modelRoot.getWorld();
        TrainModel train = (TrainModel) w.get(principal, KEY.TRAINS,
                newTrainNumber);
        this.scheduleID = train.getScheduleID();
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
        TrainModel train = (TrainModel) w.get(principal, KEY.TRAINS,
                trainNumber);
        ImmutableSchedule immutableSchedule = (ImmutableSchedule) w.get(
                principal, KEY.TRAIN_SCHEDULES, train.getScheduleID());
        return new MutableSchedule(immutableSchedule);
    }

    /**
     * Since stations can be removed, we should not assume that station 0
     * exists: this method returns the id of the first station that exists.
     */
    private int getFirstStationID() {
        NonNullElementWorldIterator stations = new NonNullElementWorldIterator(KEY.STATIONS, modelRoot
                .getWorld(), modelRoot.getPrincipal());
        if (stations.next()) {
            return stations.getIndex();
        }
        throw new NoSuchElementException();
    }

    private void setupWagonsPopup() {
        addWagonJMenu.removeAll(); // Remove existing menu items.
        NonNullElementWorldIterator cargoTypes = new NonNullElementWorldIterator(SKEY.CARGO_TYPES,
                modelRoot.getWorld());

        while (cargoTypes.next()) {
            final CargoType wagonType = (CargoType) cargoTypes.getElement();
            JMenuItem wagonMenuItem = new JMenuItem();
            final int wagonTypeNumber = cargoTypes.getIndex();
            wagonMenuItem.setText(wagonType.getDisplayName());
            Image image = vl.getWagonImages(wagonTypeNumber).getSideOnImage();
            int height = image.getHeight(null);
            int width = image.getWidth(null);
            int scale = height / 10;
            ImageIcon icon = new ImageIcon(image.getScaledInstance(width
                    / scale, height / scale, Image.SCALE_FAST));
            wagonMenuItem.setIcon(icon);
            wagonMenuItem
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(
                                java.awt.event.ActionEvent evt) {

                            addWagon(wagonTypeNumber);
                        }
                    });
            addWagonJMenu.add(wagonMenuItem);
        }
    }

    private void noChange() {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = this.orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        newOrders = new TrainOrdersModel(oldOrders.getStationID(), null, false,
                false);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void setWaitUntilFull(boolean b) {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = this.orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        // If auto-consist is set do nothing
        if (oldOrders.autoConsist)
            return;
        // If no-change is set do nothing
        if (oldOrders.consist == null)
            return;
        boolean autoConsist = false;
        newOrders = new TrainOrdersModel(oldOrders.getStationID(),
                oldOrders.consist, b, autoConsist);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void setAutoConsist() {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = this.orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        newOrders = new TrainOrdersModel(oldOrders.getStationID(), null, false,
                true);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void addWagon(int wagonTypeNumber) {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = this.orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        int[] newConsist;
        // The consist will be null if old orders were 'no change'.
        if (null != oldOrders.consist) {
            int oldLength = oldOrders.consist.size();
            newConsist = new int[oldLength + 1];
            // Copy existing wagons
            for (int i = 0; i < oldLength; i++) {
                newConsist[i] = oldOrders.consist.get(i);
            }
            // Then add specified wagon.
            newConsist[oldLength] = wagonTypeNumber;
        } else {
            newConsist = new int[]{wagonTypeNumber};
        }
        newOrders = new TrainOrdersModel(oldOrders.getStationID(), new ImInts(
                newConsist), oldOrders.getWaitUntilFull(), false);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void removeAllWagons() {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = this.orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        newOrders = new TrainOrdersModel(oldOrders.getStationID(),
                new ImInts(), false, false);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void removeLastWagon() {
        TrainOrdersModel oldOrders, newOrders;
        MutableSchedule s = getSchedule();
        int orderNumber = this.orders.getSelectedIndex();
        oldOrders = s.getOrder(orderNumber);
        if (oldOrders.consist == null) {
            return;
        }
        ImInts oldConsist = oldOrders.consist;
        int newLength = oldConsist.size() - 1;
        if (newLength < 0) {
            // No wagons to remove!
            return;
        }
        ImInts newConsist = oldConsist.removeLast();

        newOrders = new TrainOrdersModel(oldOrders.getStationID(), newConsist,
                oldOrders.waitUntilFull, false);
        s.setOrder(orderNumber, newOrders);
        sendUpdateMove(s);
    }

    private void sendUpdateMove(MutableSchedule mutableSchedule) {
        FreerailsPrincipal principal = modelRoot.getPrincipal();
        ReadOnlyWorld w = modelRoot.getWorld();
        TrainModel train = (TrainModel) w.get(principal, KEY.TRAINS,
                this.trainNumber);
        // int scheduleID = train.getScheduleID();
        assert (scheduleID == train.getScheduleID());
        ImmutableSchedule before = (ImmutableSchedule) w.get(principal,
                KEY.TRAIN_SCHEDULES, scheduleID);
        ImmutableSchedule after = mutableSchedule.toImmutableSchedule();
        Move m = new ChangeTrainScheduleMove(scheduleID, before, after,
                principal);
        this.modelRoot.doMove(m);
    }

    public void listUpdated(KEY key, int index, FreerailsPrincipal p) {
        if (KEY.TRAIN_SCHEDULES == key && this.scheduleID == index) {
            listModel.fireRefresh();
            enableButtons();
        }
    }

    public void itemAdded(KEY key, int index, FreerailsPrincipal p) {
        // do nothing.
    }

    public void itemRemoved(KEY key, int index, FreerailsPrincipal p) {
        // do nothing.
    }

    /**
     * Show the popup that lets the user select a station, called when a new
     * scheduled stop is added and when an existing scheduled stop is changed.
     */
    private void showSelectStation(MutableSchedule schedule, int orderNumber) {
        selectStationJPanel1.display(schedule, orderNumber);

        // Show the select station popup in the middle of the window.
        Container topLevelAncestor = this.getTopLevelAncestor();
        Dimension d = topLevelAncestor.getSize();
        Dimension d2 = selectStationJPopupMenu.getPreferredSize();
        int x = Math.max((d.width - d2.width) / 2, 0);
        int y = Math.max((d.height - d2.height) / 2, 0);
        selectStationJPopupMenu.show(topLevelAncestor, x, y);
        selectStationJPanel1.requestFocus();
    }
    // End of variables declaration//GEN-END:variables

}
