package freerails.controller;

import freerails.world.common.ImPoint;
import freerails.world.common.PositionOnTrack;
import freerails.world.common.Step;
import freerails.world.top.ReadOnlyWorld;
import freerails.world.track.FreerailsTile;
import freerails.world.track.NullTrackType;
import freerails.world.track.TrackConfiguration;
import freerails.world.track.TrackPiece;

import java.awt.*;
import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * GraphExplorer that explorers track, the ints it returns are encoded
 * PositionOnTrack objects.
 *
 */
public class FlatTrackExplorer implements GraphExplorer, Serializable {
    private static final long serialVersionUID = 3834311713465185081L;
    final PositionOnTrack currentBranch = PositionOnTrack.createComingFrom(0,
            0, Step.NORTH);
    private final ReadOnlyWorld w;
    private PositionOnTrack currentPosition = PositionOnTrack.createComingFrom(
            0, 0, Step.NORTH);
    private boolean beforeFirst = true;

    /**
     *
     * @param world
     * @param p
     * @throws NoTrackException
     */
    public FlatTrackExplorer(ReadOnlyWorld world, PositionOnTrack p)
            throws NoTrackException {
        w = world;
        FreerailsTile tile = (FreerailsTile) world.getTile(p.getX(), p.getY());
        if (tile.getTrackPiece().getTrackTypeID() == NullTrackType.NULL_TRACK_TYPE_RULE_NUMBER) {
            throw new NoTrackException(p.toString());
        }

        this.currentPosition = PositionOnTrack.createComingFrom(p.getX(), p
                .getY(), p.cameFrom());
    }

    /**
     * @param w
     * @param p location of track to consider.
     * @return an array of PositionOnTrack objects describing the set of
     * possible orientations at this position (heading towards the
     * center of the tile)
     */
    public static PositionOnTrack[] getPossiblePositions(ReadOnlyWorld w,
                                                         ImPoint p) {
        TrackPiece tp = ((FreerailsTile) w.getTile(p.x, p.y)).getTrackPiece();
        TrackConfiguration conf = tp.getTrackConfiguration();
        Step[] vectors = Step.getList();

        // Count the number of possible positions.
        int n = 0;

        for (Step vector1 : vectors) {
            if (conf.contains(vector1.get9bitTemplate())) {
                n++;
            }
        }

        PositionOnTrack[] possiblePositions = new PositionOnTrack[n];

        n = 0;

        for (Step vector : vectors) {
            if (conf.contains(vector.get9bitTemplate())) {
                possiblePositions[n] = PositionOnTrack.createComingFrom(p.x,
                        p.y, vector.getOpposite());
                n++;
            }
        }

        return possiblePositions;
    }

    /**
     *
     * @return
     */
    public ReadOnlyWorld getWorld() {
        return w;
    }

    public int getPosition() {
        return this.currentPosition.toInt();
    }

    /**
     *
     * @param i
     */
    public void setPosition(int i) {
        beforeFirst = true;
        currentPosition.setValuesFromInt(i);
    }

    public void moveForward() {
        if (beforeFirst) {
            throw new IllegalStateException();
        }
        this.setPosition(this.getVertexConnectedByEdge());
    }

    public void nextEdge() {
        if (!hasNextEdge()) {
            throw new NoSuchElementException();
        }
        Step v = this.getFirstVectorToTry();
        Point p = new Point(currentPosition.getX(), currentPosition.getY());
        FreerailsTile ft = (FreerailsTile) w.getTile(p.x, p.y);
        TrackPiece tp = ft.getTrackPiece();
        TrackConfiguration conf = tp.getTrackConfiguration();
        Step[] vectors = Step.getList();

        int i = v.getID();

        int loopCounter = 0;

        while (!conf.contains(vectors[i].get9bitTemplate())) {
            i++;
            i = i % 8;
            loopCounter++;

            if (8 < loopCounter) {
                throw new IllegalStateException();
                // This should never happen.. ..but it does happen when you
                // removed the track from under a train.
            }
        }

        Step branchDirection = Step.getInstance(i);
        this.currentBranch.setCameFrom(branchDirection);

        int x = this.currentPosition.getX() + branchDirection.deltaX;
        int y = this.currentPosition.getY() + branchDirection.deltaY;

        this.currentBranch.setX(x);
        this.currentBranch.setY(y);

        beforeFirst = false;
    }

    public int getVertexConnectedByEdge() {
        return currentBranch.toInt();
    }

    public int getEdgeCost() {
        return (int) Math.round(currentBranch.cameFrom().getLength());
    }

    /**
     *
     * @return
     */
    public boolean hasNextEdge() {
        if (beforeFirst) {
            // We can always go back the way we have come, so if we are before
            // the first
            // branch, there must be a branch: the one we used to get here.
            return true;
        }
        // Since we can always go back the way we have come, if the direction of
        // current branch is not equal to the opposite of the current direction,
        // there must be another branch.
        Step currentBranchDirection = this.currentBranch.cameFrom();
        Step oppositeToCurrentDirection = this.currentPosition.cameFrom()
                .getOpposite();

        return oppositeToCurrentDirection.getID() != currentBranchDirection
                .getID();
    }

    Step getFirstVectorToTry() {
        if (beforeFirst) {
            // Return the vector that is 45 degrees clockwise from the oppposite
            // of the current position.
            Step v = this.currentPosition.cameFrom();
            v = v.getOpposite();

            int i = v.getID();
            i++;
            i = i % 8;
            v = Step.getInstance(i);

            return v;
        }
        // Return the vector that is 45 degrees clockwise from the direction
        // of the current branch.
        Step v = this.currentBranch.cameFrom();
        int i = v.getID();
        i++;
        i = i % 8;
        v = Step.getInstance(i);

        return v;
    }

    /**
     *
     * @return
     */
    public int getH() {
        // TODO Auto-generated method stub
        return 0;
    }
}