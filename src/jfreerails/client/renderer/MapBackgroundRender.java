
/*
 *  BackgroundMapView.java
 *
 *  Created on 06 August 2001, 17:21
 */
package jfreerails.client.renderer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import jfreerails.world.terrain.TerrainTile;
import jfreerails.world.top.ReadOnlyWorld;
import jfreerails.world.track.TrackPiece;


/** This class encapsulates the objects that make-up and paint the background
 * of the map view. At present it is composed of two layers: the terrain layer
 * and the track layer.
 *
 * @author Luke Lindsay
 *   21 September 2001
 * @version 1
 */

final public class MapBackgroundRender implements MapLayerRenderer {

	/** The terrain layer.
	 */
	protected TerrainLayer terrainLayer;

	/** The track layer.
	 */
	protected TrackLayer trackLayer;

	private Dimension tileSize = new Dimension(30, 30);

	private Dimension mapSize;

	/*Used to avoid having to create a new rectangle for each call to
	 *the paint methods.
	 */
	private Rectangle clipRectangle = new Rectangle();

	/**
	 *  This innner class represents a view of the track on the map.
	 *
	 *@author     Luke Lindsay
	 *     21 September 2001
	 */

	final public class TrackLayer implements MapLayerRenderer {

		private ReadOnlyWorld w;

		private TrackPieceRendererList trackPieceViewList;




		/** Paints a rectangle of tiles onto the supplied
		 * graphics context.
		 * @param g The graphics context on which the tiles
		 * get painted.
		 * @param tilesToPaint The rectangle, measured in tiles, to
		 * paint.
		 */
		public void paintRectangleOfTiles(Graphics g, Rectangle tilesToPaint) {

			/*  Track can overlap the adjacent terrain tiles by half a tile.  This means
			 *that we need to paint the track from the tiles bordering the specified rectangle
			 *of tiles (tilesToPaint).  To prevent unnecessay painting, we set the clip to expose only the
			 *rectangle of tilesToPaint.
			 */

			Graphics tempG = g;
			Point tile = new Point();

			for (tile.x = tilesToPaint.x - 1;
				tile.x < (tilesToPaint.x + tilesToPaint.width + 1);
				tile.x++) {
				for (tile.y = tilesToPaint.y - 1;
					tile.y < (tilesToPaint.y + tilesToPaint.height + 1);
					tile.y++) {

					if ((tile.x >= 0)
						&& (tile.x < mapSize.width)
						&& (tile.y >= 0)
						&& (tile.y < mapSize.height)) {
						
							
						TrackPiece tp = (TrackPiece)w.getTile(tile.x, tile.y);	
						
						int graphicsNumber = tp.getTrackGraphicNumber();
													
						int ruleNumber = tp.getTrackRule().getRuleNumber();
						jfreerails.client.renderer
							.TrackPieceRenderer trackPieceView =
							trackPieceViewList.getTrackPieceView(ruleNumber);
						trackPieceView.drawTrackPieceIcon(
							graphicsNumber,
							tempG,
							tile.x,
							tile.y,
							tileSize);
					}
				}
			}

		}

		public void paintTile(Graphics g, int tileX, int tileY) {
			/*
			 *  Since track tiles overlap the adjacent terrain tiles, we create a temporary Graphics
			 *  object that only lets us draw on the selected tile.
			 */

			paintRectangleOfTiles(g, new Rectangle(tileX, tileY, 1, 1));

		}

		public void paintRectangleOfTiles(
			Graphics g,
			int x,
			int y,
			int width,
			int height) {
			paintRectangleOfTiles(g, new Rectangle(x, y, width, height));
		}

		public void refreshTile(int x, int y) {
		}

		public void refreshRectangleOfTiles(
			int x,
			int y,
			int width,
			int height) {
		}

		public void paintRect(Graphics g, Rectangle visibleRect) {
			throw new UnsupportedOperationException("Method not yet implemented.");
		}



		/** Creates a new tracklayer.
		 * @param trackMap Description of the Parameter
		 * @param trackPieceViewList Description of the Parameter
		 */

		public TrackLayer(
			ReadOnlyWorld world,
			TrackPieceRendererList trackPieceViewList) {
			this.trackPieceViewList = trackPieceViewList;
			this.w = world;
		}
	}

	/**
	 *  This inner class represents the terrain of the map.
	 *
	 *@author     Luke Lindsay
	 *     21 September 2001
	 */

	final public class TerrainLayer implements MapLayerRenderer {

		private TileRendererList tiles;

		private ReadOnlyWorld w;



		/** Paints the specified tile onto the supplied
		 * graphics context.
		 * @param x x coordinate
		 * @param y y coordinate.
		 * @param g The graphics context
		 */
		public void paintTile(Graphics g, Point tile) {
			
			int screenX = tileSize.width * tile.x;
			int screenY = tileSize.height * tile.y;
			if ((tile.x >= 0)
				&& (tile.x < mapSize.width)
				&& (tile.y >= 0)
				&& (tile.y < mapSize.height)) {
					
				TerrainTile tt = (TerrainTile)w.getTile(tile.x, tile.y);	

				int typeNumber = tt.getTerrainTypeNumber();
				TileRenderer tr = tiles.getTileViewWithNumber(typeNumber);
				if(null == tr){					
					System.out.println("No tile renderer for "+typeNumber);
				}else{
				
				tr.renderTile(
					g,
					screenX,
					screenY,
					tile.x,
					tile.y,
					w);
				}
			}
		}

		/** Paints a rectangle of tiles on the supplied graphics
		 * context.
		 * @param g The grahics context.
		 * @param tilesToPaint The rectangle, measued in tiles, to paint.
		 */
		public void paintRectangleOfTiles(Graphics g, Rectangle tilesToPaint) {
			Point tile = new Point();
			for (tile.x = tilesToPaint.x;
				tile.x < (tilesToPaint.x + tilesToPaint.width);
				tile.x++) {
				for (tile.y = tilesToPaint.y;
					tile.y < (tilesToPaint.y + tilesToPaint.height);
					tile.y++) {
					terrainLayer.paintTile(g, tile);
				}
			}

		}
		public void paintRect(Graphics g, Rectangle visibleRect) {
			throw new UnsupportedOperationException("Method not yet implemented.");
		}

		public void paintTile(Graphics g, int tileX, int tileY) {
			paintTile(g, new Point(tileX, tileY));
		}

		public void paintRectangleOfTiles(
			Graphics g,
			int x,
			int y,
			int width,
			int height) {
				paintRectangleOfTiles(g, new Rectangle(x,y,width, height));
		}

		public void refreshTile(int x, int y) {
		}

		public void refreshRectangleOfTiles(
			int x,
			int y,
			int width,
			int height) {
		}





		public TerrainLayer(ReadOnlyWorld world, TileRendererList tiles) {
			this.w = world;
			this.tiles = tiles;
		}
	}

	/** Creates new BackgroundMapView
	 *
	 * @param map                 The non-visual map object.
	 * @param tiles               An object holding a list of tileView objects,
	 *     which represent the visual properites of terrain tiles.
	 * @param trackPieceViewList The object holding the set of objects
	 *     representing the visual properties of track-pieces.
	 */

	public MapBackgroundRender(
		ReadOnlyWorld w,
		TileRendererList tiles,
		TrackPieceRendererList trackPieceViewList) {
		trackLayer = new TrackLayer(w, trackPieceViewList);
		terrainLayer = new TerrainLayer(w, tiles);
		mapSize = new Dimension(w.getMapWidth(), w.getMapHeight());
	}

	/** Paints a rectangle of the map on the specified
	 * graphics context.
	 * @param g Description of the Parameter
	 * @param tilesToPaint Description of the Parameter
	 */
	/*
	public void paintRectangleOfTiles(Graphics g, Rectangle tilesToPaint) {
		terrainLayer.paintRectangleOfTiles(g, tilesToPaint);
		trackLayer.paintRectangleOfTiles(g, tilesToPaint);
	}
	*/

	/*
	public void refreshRectangleOfTiles(
		Graphics g,
		int x,
		int y,
		int width,
		int height) {
		terrainLayer.refreshRectangleOfTiles(g, x, y, width, height);
		trackLayer.refreshRectangleOfTiles(g, x, y, width, height);
	}
	*/


	public void paintTile(Graphics g, int x, int y) {
		terrainLayer.paintTile(g, x, y);
		trackLayer.paintTile(g, x, y);
	}

	public void paintRect(Graphics g, Rectangle visibleRect) {
		//Dimension tileSize = getTileSize();
		//Dimension mapSize = getMapSizeInTiles();
		int tileWidth = 30;
		int tileHeight = 30;

		clipRectangle = g.getClipBounds(clipRectangle);

		int x=		clipRectangle.x / tileWidth;
		int y=		clipRectangle.y / tileHeight;
		int width=		(clipRectangle.width / tileWidth) + 2;
		int height=		(clipRectangle.height) / tileHeight + 2;

		paintRectangleOfTiles(g, x,y,width,height);
	}


	public void paintRectangleOfTiles(
		Graphics g,
		int x,
		int y,
		int width,
		int height) {
		terrainLayer.paintRectangleOfTiles(g, x,y,width,height);
		trackLayer.paintRectangleOfTiles(g, x,y,width,height);
	}

	public void refreshTile(int x, int y) {

	}

	public void refreshRectangleOfTiles(int x, int y, int width, int height) {

	}


}