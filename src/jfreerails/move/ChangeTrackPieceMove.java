package jfreerails.move;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import jfreerails.world.World;
import jfreerails.world.track.LegalTrackConfigurations;
import jfreerails.world.track.TrackPiece;
import jfreerails.world.track.TrackMap;

final public class ChangeTrackPieceMove
	implements NewTrackMove, MapUpdateMove {

	private final TrackPiece oldTrackPiece;

	private final TrackPiece newTrackPiece;

	private final Point location;

	public Point getLocation() {
		return location;
	}

	public TrackPiece getOldTrackPiece() {
		return oldTrackPiece;
	}

	public TrackPiece getNewTrackPiece() {
		return newTrackPiece;
	}

	public ChangeTrackPieceMove(TrackPiece before, TrackPiece after, Point p) {
		oldTrackPiece = before;
		newTrackPiece = after;
		location = new Point(p);
	}

	public MoveStatus tryDoMove(World w) {

		TrackMap trackTileMap = w.getTrackMap();

		//Check that location is on the map.
		if (!trackTileMap.boundsContain(location)) {
			return new MoveStatus(
				false,
				"Tried to build track outside the map.");
		}

		//Check that the current track piece at this.location is
		//the same as this.oldTrackPiece.
		TrackPiece currentTrackPieceAtLocation =
			trackTileMap.getTrackPiece(location);

		if ((currentTrackPieceAtLocation.getTrackConfiguration()
			!= oldTrackPiece.getTrackConfiguration())
			|| (currentTrackPieceAtLocation.getTrackRule()
				!= oldTrackPiece.getTrackRule())) {
			return new MoveStatus(
				false,
				"Unexpected track piece found at location: "
					+ location.x
					+ " ,"
					+ location.y);
		}

		//Check that oldTrackPiece is not hte same as newTrackPiece
		if ((oldTrackPiece.getTrackConfiguration()
			== newTrackPiece.getTrackConfiguration())
			&& (oldTrackPiece.getTrackRule() == newTrackPiece.getTrackRule())) {
			return new MoveStatus(
				false,
				"Tried to replace a one track piece with another identical one.");
		}

		//Check for illegal track configurations.
		if (!(oldTrackPiece
			.getTrackRule()
			.trackPieceIsLegal(oldTrackPiece.getTrackConfiguration())
			&& newTrackPiece.getTrackRule().trackPieceIsLegal(
				newTrackPiece.getTrackConfiguration()))) {
			return new MoveStatus(false, "Illegal track configuration.");
		}
		//Check for diagonal conflicts.
		if (!(noDiagonalTrackConflicts(location,
			oldTrackPiece.getTrackGraphicNumber(),
			trackTileMap)
			&& noDiagonalTrackConflicts(
				location,
				newTrackPiece.getTrackGraphicNumber(),
				trackTileMap))) {
			return new MoveStatus(
				false,
				"Illegal track configuration - diagonal conflict");
		}

		return MoveStatus.MOVE_ACCEPTED;
	}

	public MoveStatus tryUndoMove(World w) {

		TrackMap trackTileMap = w.getTrackMap();
		return MoveStatus.MOVE_RECEIVED;

	}

	public MoveStatus doMove(World w) {

		TrackMap trackTileMap = w.getTrackMap();
		MoveStatus moveStatus = tryDoMove(w);
		if (!moveStatus.isOk()) {
			return moveStatus;
		} else {
			trackTileMap.setTrackPiece(location, newTrackPiece);
			return moveStatus;
		}

	}

	public MoveStatus undoMove(World w) {

		TrackMap trackTileMap = w.getTrackMap();
		return MoveStatus.MOVE_RECEIVED;

	}
	private boolean noDiagonalTrackConflicts(
		Point point,
		int trackTemplate,
		TrackMap trackTileMap) {
		/*This method is needs replacing.  It only deals with flat track pieces, and
		 *is rather hard to make sense of.  LL
		 */
		//int trackTemplate = (1 << (3 * (1 + tv.getY()) + (1 + tv.getX())));
		int trackTemplateAbove;
		int trackTemplateBelow;
		int cornersTemplate =
			LegalTrackConfigurations.stringTemplate2Int("101000101");
		trackTemplate = trackTemplate & cornersTemplate;
		Dimension mapSize = trackTileMap.getMapSize();
		//Avoid array-out-of-bounds exceptions.
		if (point.y > 0) {
			trackTemplateAbove =
				trackTileMap.getTrackGraphicNumber(
					new Point(point.x, point.y - 1));
		} else {
			trackTemplateAbove = 0;
		}
		if ((point.y + 1) < mapSize.height) {
			trackTemplateBelow =
				trackTileMap.getTrackGraphicNumber(
					new Point(point.x, point.y + 1));
		} else {
			trackTemplateBelow = 0;
		}
		trackTemplateAbove = trackTemplateAbove >> 6;
		trackTemplateBelow = trackTemplateBelow << 6;
		trackTemplate =
			trackTemplate & (trackTemplateAbove | trackTemplateBelow);
		if (trackTemplate != 0) {
			return false;
			//There is a clash.
		} else {
			return true;
			//Things are ok.
		}
	}
	public Rectangle getUpdatedTiles() {
		int x, y, width, height;

		x = location.x - 1;
		y = location.y - 1;
		width = 3;
		height = 3;
		return new Rectangle(x, y, width, height);

	}

}