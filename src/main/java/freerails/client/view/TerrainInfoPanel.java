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
 * TerrainInfoPanel.java
 *
 */

package freerails.client.view;

import freerails.client.renderer.RendererRoot;
import freerails.model.ModelConstants;
import freerails.model.cargo.Cargo;
import freerails.model.cargo.CargoConversion;
import freerails.model.cargo.CargoProductionOrConsumption;
import freerails.model.terrain.*;
import freerails.model.world.UnmodifiableWorld;

import javax.swing.*;
import java.awt.*;

/**
 * Shows information on a terrain type.
 */
class TerrainInfoPanel extends JPanel {

    private static final long serialVersionUID = 3258131375164045363L;
    private RendererRoot rendererRoot;
    private UnmodifiableWorld world;
    private JLabel terrainDescription;
    private JLabel terrainImage;
    private JLabel terrainName;

    public TerrainInfoPanel() {
        GridBagConstraints gridBagConstraints;

        terrainImage = new JLabel();
        terrainName = new JLabel();
        terrainDescription = new JLabel();

        setLayout(new GridBagLayout());

        terrainImage.setIcon(new ImageIcon(getClass().getResource("/freerails/client/graphics/terrain/City_0.png")));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(8, 8, 4, 4);
        add(terrainImage, gridBagConstraints);

        terrainName.setFont(new Font("Dialog", 1, 14));
        terrainName.setText("City");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 8);
        add(terrainName, gridBagConstraints);

        terrainDescription.setFont(new Font("Dialog", 0, 12));
        terrainDescription.setText("<html>\n<p>Right-of-Way costs X per mile. </p>\n<table width=\"75%\" >\n  <tr> \n    <td><strong>Supplies:</strong></td>\n    <td>&nbsp;</td>\n  </tr>\n  <tr> \n    <td>Mail </td>\n    <td>2</td>\n  </tr>\n  <tr> \n    <td>Passengers</td>\n    <td>2</td>\n  </tr>\n  <tr> \n    <td> <strong>Demands</strong></td>\n    <td>&nbsp;</td>\n  </tr>\n  <tr> \n    <td>Mail</td>\n    <td>&nbsp;</td>\n  </tr>\n  <tr> \n    <td>Passengers</td>\n    <td>&nbsp;</td>\n  </tr>\n  <tr> \n    <td><strong>Converts</strong></td>\n    <td>&nbsp;</td>\n  </tr>\n  <tr> \n    <td>Livestock to Food</td>\n    <td>&nbsp;</td>\n  </tr>\n  <tr>\n    <td>Steel to Goods</td>\n    <td>&nbsp;</td>\n  </tr>\n</table>\n</html>");
        terrainDescription.setVerticalAlignment(SwingConstants.TOP);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(4, 8, 4, 8);
        add(terrainDescription, gridBagConstraints);
    }

    public void setup(UnmodifiableWorld world, RendererRoot vl) {
        this.world = world;
        rendererRoot = vl;
    }

    public void setTerrainType(int terrainId) {

        Terrain type = world.getTerrain(terrainId);

        String row = "<p>Right-of-Way costs $" + type.getRightOfWayCost() + " per mile. </p>";
        StringBuilder tableString = new StringBuilder();
        int cargosProduced = type.getProductions().size();
        int cargosConsumed = type.getConsumptions().size();
        int cargosConverted = type.getConversions().size();
        if (cargosProduced + cargosConsumed + cargosConverted > 0) {
            // if the terrain type produces, consumes, or converts anything.
            tableString = new StringBuilder("<table width=\"75%\" >");
            if (cargosProduced != 0) {
                tableString.append("<tr> <td><strong>Supplies</strong></td> <td>&nbsp;</td> </tr>");
                for (int i = 0; i < cargosProduced; i++) {
                    CargoProductionOrConsumption p = type.getProductions().get(i);
                    Cargo c = world.getCargo(p.getCargoId());
                    String supply = String.valueOf(p.getRate() / ModelConstants.UNITS_OF_CARGO_PER_WAGON);
                    tableString.append("<tr> <td>").append(c.getName()).append(" </td><td>").append(supply).append("</td></tr>");
                }
            }
            if (cargosConsumed != 0) {
                tableString.append("<tr> <td><strong>Demands</strong></td> <td>&nbsp;</td> </tr>");
                for (int i = 0; i < cargosConsumed; i++) {
                    CargoProductionOrConsumption consumption = type.getConsumptions().get(i);
                    Cargo cargo = world.getCargo(consumption.getCargoId());
                    tableString.append("<tr> <td>").append(cargo.getName()).append(" </td><td>&nbsp;</td></tr>");
                }
            }
            if (cargosConverted != 0) {
                tableString.append("<tr> <td><strong>Converts</strong></td> <td>&nbsp;</td> </tr>");
                for (int i = 0; i < cargosConverted; i++) {
                    CargoConversion p = type.getConversions().get(i);
                    Cargo input = world.getCargo(p.getSourceCargoId());
                    Cargo output = world.getCargo(p.getProductCargoId());
                    tableString.append("<tr> <td colspan=\"2\">").append(input.getName()).append(" to ").append(output.getName()).append("</td></tr>");
                }
            }
            tableString.append("</table> ");
        }
        String labelString = "<html>" + row + tableString + "</html>";
        terrainDescription.setText(labelString);
        terrainName.setText(type.getName());

        Image tileIcon = rendererRoot.getTileRendererByIndex(terrainId).getDefaultIcon();
        terrainImage.setIcon(new ImageIcon(tileIcon));

        repaint();
    }

}
