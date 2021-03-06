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

package freerails.util.ui;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Since there is no setMinimum size method on JFrame, we use an instance of
 * this class to do the job.
 */
public class JFrameMinimumSizeEnforcer implements ComponentListener {

    private final int minWidth;
    private final int minHeight;

    /**
     * @param w
     * @param h
     */
    public JFrameMinimumSizeEnforcer(int w, int h) {
        minHeight = h;
        minWidth = w;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Component c = e.getComponent();

        int width = c.getWidth();
        int height = c.getHeight();

        // we check if either the width
        // or the height are below minimum
        boolean resize = false;

        if (width < minWidth) {
            resize = true;
            width = minWidth;
        }

        if (height < minHeight) {
            resize = true;
            height = minHeight;
        }

        if (resize) {
            c.setSize(width, height);
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}