/*
 * Copyright (C) Luke Lindsay
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
* StandardTileIconSelecter.java
*
* Created on 07 July 2001, 12:11
*/
package jfreerails.client.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import jfreerails.client.common.ImageManager;
import jfreerails.world.terrain.TerrainType;


/**
*
* @author  Luke Lindsay
*/
final public class StandardTileRenderer
    extends jfreerails.client.renderer.AbstractTileRenderer {
    public StandardTileRenderer(ImageManager imageManager, int[] rgbValues,
        TerrainType tileModel) throws IOException {
        super(tileModel, rgbValues);
        this.setTileIcons(new BufferedImage[1]);
        this.getTileIcons()[0] = imageManager.getImage(generateFilename());
    }

    public void dumpImages(ImageManager imageManager) {
        imageManager.setImage(generateFilename(), this.getTileIcons()[0]);
    }

    private String generateFilename() {
        return generateFilename(this.getTerrainType());
    }

    public static String generateFilename(String typeName) {
        return "terrain" + File.separator + typeName + ".png";
    }

    protected String generateFileNameNumber(int i) {
        throw new UnsupportedOperationException();
    }
}
