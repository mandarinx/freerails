
/*
* ChequeredTileView.java
*
* Created on 07 July 2001, 14:25
*/
package jfreerails.client.renderer;

import java.awt.Image;
import java.io.IOException;

import jfreerails.client.common.ImageManager;
import jfreerails.client.common.ImageSplitter;
import jfreerails.world.terrain.TerrainType;
import jfreerails.world.top.World;

/**
*
* @author  Luke Lindsay
*/

final public class ChequeredTileRenderer extends AbstractTileRenderer {

	public int selectTileIcon(int x, int y, World w) {
		return (x + y) % 2;
	}

	/** Creates new ChequeredTileView */

	public ChequeredTileRenderer(
		ImageSplitter imageSplitter,
		int[] rgbValues,
		TerrainType tileModel) {
		super(tileModel, rgbValues);
		imageSplitter.setTransparencyToOPAQUE();
		tileIcons = new java.awt.Image[2];
		for (int i = 0; i < tileIcons.length; i++) {
			tileIcons[i] = imageSplitter.getTileFromSubGrid(0 + i, 0);
		}
		
	}
	
	public ChequeredTileRenderer(ImageManager imageManager, int[] rgbValues,
	TerrainType tileModel) throws IOException {
		super(tileModel, rgbValues);
		this.tileIcons = new Image[2];
		this.tileIcons[0]= imageManager.getImage(generateRelativeFileName(0, 1));
		this.tileIcons[1]= imageManager.getImage(generateRelativeFileName(1, 1));
	}
	
	public void dumpImages(ImageManager imageManager) {
		for (int i = 0; i < this.tileIcons.length; i++) {
			String fileName = generateRelativeFileName(i, 1);
			imageManager.setImage(fileName, this.tileIcons[i]);
		}
	}
	
}
