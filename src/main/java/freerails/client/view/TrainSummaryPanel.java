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
 * TrainSummaryPanel.java
 *
 */

package freerails.client.view;

import freerails.client.model.TrainSummeryModel;
import freerails.client.renderer.RendererRoot;
import freerails.client.renderer.TrainListCellRenderer;
import freerails.client.ModelRoot;
import freerails.model.game.Clock;
import freerails.model.world.UnmodifiableWorld;
import freerails.model.finance.Money;
import freerails.model.player.Player;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class TrainSummaryPanel extends JPanel implements ListCellRenderer, View {

    private static final long serialVersionUID = 4121133628006020919L;
    private final TrainSummeryModel model;
    private final Color backgoundColor = (Color) UIManager.getDefaults().get("List.background");
    private final Color selectedColor = (Color) UIManager.getDefaults().get("List.selectionBackground");
    private final Color selectedColorNotFocused = Color.LIGHT_GRAY;
    private UnmodifiableWorld world;
    private Player player;
    private TrainListCellRenderer trainListCellRenderer1;
    private JLabel headingLabel;
    private JLabel trainIncomeLabel;
    private JLabel trainMaintenanceCostLabel;
    private JLabel trainNumLabel;

    /**
     * Creates new form TrainSummaryPanel
     */
    public TrainSummaryPanel() {
        model = new TrainSummeryModel();
        GridBagConstraints gridBagConstraints;

        trainNumLabel = new JLabel();
        headingLabel = new JLabel();
        trainMaintenanceCostLabel = new JLabel();
        trainIncomeLabel = new JLabel();

        setLayout(new GridBagLayout());

        setPreferredSize(new Dimension(500, 50));
        trainNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trainNumLabel.setText("label1");
        trainNumLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        trainNumLabel.setPreferredSize(new Dimension(100, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        add(trainNumLabel, gridBagConstraints);

        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headingLabel.setText("label2");
        headingLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        headingLabel.setPreferredSize(new Dimension(100, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        add(headingLabel, gridBagConstraints);

        trainMaintenanceCostLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trainMaintenanceCostLabel.setText("label3");
        trainMaintenanceCostLabel.setMaximumSize(getMaximumSize());
        trainMaintenanceCostLabel.setPreferredSize(new Dimension(100, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        add(trainMaintenanceCostLabel, gridBagConstraints);

        trainIncomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trainIncomeLabel.setText("label1");
        trainIncomeLabel.setPreferredSize(new Dimension(100, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        add(trainIncomeLabel, gridBagConstraints);
    }

    @Override
    public void setup(ModelRoot modelRoot, RendererRoot rendererRoot, Action closeAction) {
        player = modelRoot.getPlayer();
        world = modelRoot.getWorld();
        trainListCellRenderer1 = new TrainListCellRenderer(modelRoot, rendererRoot);
        trainListCellRenderer1.setHeight(15);
        model.setWorld(world, player);
    }

    private String findStationName(int trainNum) {
        return model.getStationName(trainNum);
    }

    private String findTrainIncome(int trainNum) {
        Money m = model.findTrainIncome(trainNum);
        return '$' + m.toString();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // TODO find a different way of relating row number to train id
        // int trainID = NonNullElementWorldIterator.rowToIndex(world, PlayerKey.Trains, player, index);
        int trainID = 0;
        String trainNumText = "#" + (trainID + 1);

        trainNumLabel.setText(trainNumText);
        headingLabel.setText(findStationName(trainID));
        trainMaintenanceCostLabel.setText(findMaintenanceCost());
        trainIncomeLabel.setText(findTrainIncome(trainID));

        GridBagConstraints gridBagConstraints;

        trainListCellRenderer1.setOpaque(true);
        trainListCellRenderer1.setCenterTrain(false);
        trainListCellRenderer1.display(trainID);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(3, 0, 3, 0);
        add(trainListCellRenderer1, gridBagConstraints);

        if (isSelected) {
            if (list.isFocusOwner()) {
                setBackground(selectedColor);
                trainListCellRenderer1.setBackground(selectedColor);
            } else {
                setBackground(selectedColorNotFocused);
                trainListCellRenderer1.setBackground(selectedColorNotFocused);
            }
        } else {
            setBackground(backgoundColor);
            trainListCellRenderer1.setBackground(backgoundColor);
        }
        // Set selected
        return this;
    }

    private String findMaintenanceCost() {

        Clock clock = world.getClock();
        double month = clock.getCurrentMonth();
        long cost = (long) (month / 12 * 5000);

        Money m = new Money(cost);
        return '$' + m.toString();
    }
}
