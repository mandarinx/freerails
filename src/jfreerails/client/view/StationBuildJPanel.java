/*
 * Copyright (C) 2003 Robert Tuck
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/*
 * JPanel.java
 *
 * Created on 19 August 2003, 22:34
 */

package jfreerails.client.view;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JButton;

import jfreerails.client.model.ModelRoot;
import jfreerails.client.model.StationBuildModel;
import jfreerails.client.renderer.ViewLists;

/**
 *
 * @author  bob
 */
class StationBuildJPanel extends javax.swing.JPanel {
    
    private int numberOfButtons = 0;

    /** Creates new form JPanel */
    public StationBuildJPanel() {
        initComponents();
    }
    
    void setup(ViewLists vl, ModelRoot modelRoot) {
	StationBuildModel stationBuildModel = modelRoot.getStationBuildModel();
	Action[] actions = stationBuildModel.getStationChooseActions();
	for (int i = 0; i < actions.length; i++) {
	    StationButton button = new StationButton(actions[i]);
	    Dimension d = button.getSize();
	    Dimension s = stationTypesjPanel1.getSize();
	    int columns = (int) (s.getWidth() / d.getWidth());
	    stationTypesjPanel1.add(button);
	    numberOfButtons++;
	}
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane1 = new javax.swing.JScrollPane();
        stationTypesjPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(stationTypesjPanel1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    /**
     * represents a track rule - contains a small icon representing the track
     * and a text label
     */
    private class StationButton extends JButton {
	public StationButton(Action a) {
	    super(a);
	    setMargin(new Insets(0,0,0,0));
	    setText(null);
	}
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel stationTypesjPanel1;
    // End of variables declaration//GEN-END:variables
    
}
