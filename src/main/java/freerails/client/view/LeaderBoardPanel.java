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
 *
 */
package freerails.client.view;

import freerails.client.renderer.RendererRoot;
import freerails.client.ModelRoot;
import freerails.model.finance.transaction.aggregator.NetWorthAggregator;
import freerails.model.finance.Money;
import freerails.model.finance.PlayerDetails;
import freerails.model.finance.transaction.aggregator.TransactionAggregator;
import freerails.model.game.Time;
import freerails.model.player.Player;
import freerails.model.world.UnmodifiableWorld;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Displays the details of the players ordered by net worth.
 */
public class LeaderBoardPanel extends JPanel implements View {

    private static final long serialVersionUID = 3258131375298066229L;
    private final List<PlayerDetails> values;
    private JList playersList = null;
    private ActionListener submitButtonCallBack = null;

    /**
     * This method initializes
     */
    public LeaderBoardPanel() {
        super();

        values = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            PlayerDetails p = new PlayerDetails();
            p.setNetworth(new Money(rand.nextInt(100)));
            values.add(p);
        }
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        add(getPlayersList(), null);
        MouseListener mouseAdapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (null == submitButtonCallBack) {
                    System.err.println("mouseClicked");
                } else {
                    submitButtonCallBack.actionPerformed(new ActionEvent(this, 0, null));
                }
            }
        };
        addMouseListener(mouseAdapter);
        playersList.addMouseListener(mouseAdapter);
        setSize(getPreferredSize());
    }

    /**
     * This method initializes list
     *
     * @return javax.swing.JList
     */
    private JList getPlayersList() {
        if (playersList == null) {
            playersList = new JList();
            playersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            playersList.setRequestFocusEnabled(false);
            playersList.setEnabled(true);

            Collections.sort(values);
            playersList.setListData(values.toArray());
        }
        return playersList;
    }

    /**
     * @param modelRoot
     * @param rendererRoot
     * @param closeAction
     */
    @Override
    public void setup(ModelRoot modelRoot, RendererRoot rendererRoot, Action closeAction) {
        UnmodifiableWorld world = modelRoot.getWorld();
        values.clear();
        submitButtonCallBack = closeAction;
        Time[] times = {Time.ZERO, world.getClock().getCurrentTime()};
        for (Player player: world.getPlayers()) {
            PlayerDetails details = new PlayerDetails();
            details.setName(player.getName());
            details.setStations(world.getStations(player).size());
            TransactionAggregator networth = new NetWorthAggregator(world, player, times);
            networth.aggregate();
            details.setNetworth(networth.getValues()[0]);
            values.add(details);
        }
        Collections.sort(values);
        playersList.setListData(values.toArray());
        setSize(getPreferredSize());
    }

} // @jve:decl-index=0:visual-constraint="67,32"
