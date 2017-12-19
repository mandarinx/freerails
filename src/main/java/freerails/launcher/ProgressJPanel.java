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
 * ProgressJPanel.java
 *
 * Created on 31 August 2005
 */

package freerails.launcher;

import freerails.util.FreerailsProgressMonitor;

/**
 * A JPanel that displays a splash screen and a progress bar.
 *
 */
public class ProgressJPanel extends javax.swing.JPanel implements
        FreerailsProgressMonitor {

    private static final long serialVersionUID = 3256445798203273776L;
    final int numSteps = 5;
    final LauncherInterface owner;
    int step, stepSize;
    // Variables declaration - do not modify                     
    javax.swing.JProgressBar progressBar;
    javax.swing.JLabel splashImage;

    /**
     * Creates new form ProgressJPanel
     * @param owner
     */
    public ProgressJPanel(LauncherInterface owner) {
        this.owner = owner;
        initComponents();
        progressBar.setMaximum(numSteps * 100);
    }

    /**
     *
     * @param i
     */
    public void setValue(int i) {
        int value = i * 100 / stepSize;
        value += 100 * step;
        progressBar.setValue(value);
    }

    /**
     *
     * @param max
     */
    public void nextStep(int max) {

        // So that the waiting for game to start message
        // goes away.
        owner.hideAllMessages();

        step++;
        stepSize = max;
        if (numSteps < step)
            throw new IllegalStateException();
    }

    // </editor-fold>                        

    /**
     *
     */

    public void finished() {
        if (numSteps - 1 != step)
            throw new IllegalStateException(numSteps + "!=" + step);

        getTopLevelAncestor().setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code
    // ">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        progressBar = new javax.swing.JProgressBar();
        splashImage = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 3, 7);
        add(progressBar, gridBagConstraints);

        splashImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        splashImage.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/freerails/client/graphics/splash_screen.jpg")));
        add(splashImage, new java.awt.GridBagConstraints());

    }
    // End of variables declaration                   

}
