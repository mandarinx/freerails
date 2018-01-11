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
package freerails.world.train;

import freerails.util.ImmutableList;
import freerails.util.LineSegment;
import freerails.util.Pair;
import freerails.util.Point2D;
import freerails.world.terrain.TileTransition;
import freerails.world.track.PathIterator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An immutable class that stores a path made up of OneTileMoveVectors.
 */
public strictfp class PathOnTiles implements Serializable {

    private static final long serialVersionUID = 3544386994122536753L;
    private final Point2D start;
    private final ImmutableList<TileTransition> vectors;

    /**
     * @throws NullPointerException if null == start
     * @throws NullPointerException if null == vectorsList
     * @throws NullPointerException if null == vectorsList.get(i) for any i;
     */
    public PathOnTiles(Point2D start, List<TileTransition> vectorsList) {
        if (null == start) throw new NullPointerException();
        vectors = new ImmutableList<>(vectorsList);
        vectors.containsNulls();
        this.start = start;
    }

    /**
     * @throws NullPointerException if null == start
     * @throws NullPointerException if null == vectors
     * @throws NullPointerException if null == vectors[i] for any i;
     */
    public PathOnTiles(Point2D start, TileTransition... vectors) {
        if (null == start) throw new NullPointerException();
        this.vectors = new ImmutableList<>(vectors);
        this.vectors.containsNulls();
        this.start = start;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PathOnTiles)) return false;

        final PathOnTiles pathOnTiles = (PathOnTiles) obj;

        if (!start.equals(pathOnTiles.start)) return false;
        return vectors.equals(pathOnTiles.vectors);
    }

    /**
     * Returns the distance you would travel if you walked the all the way along
     * the path.
     */
    public double getTotalDistance() {
        return getDistance(vectors.size());
    }

    /**
     * @param steps
     * @return
     */
    public double getDistance(int steps) {
        double distanceSoFar = 0;
        for (int i = 0; i < steps; i++) {
            TileTransition v = vectors.get(i);
            distanceSoFar += v.getLength();
        }
        return distanceSoFar;
    }

    /**
     * Returns the coordinates of the point you would be standing at if you
     * walked the specified distance along the path from the start point.
     *
     * @throws IllegalArgumentException if distance &lt; 0
     * @throws IllegalArgumentException if distance &gt; getLength()
     */
    public Point2D getPoint(double distance) {
        if (0 > distance) throw new IllegalArgumentException("distance:" + distance + " < 0");

        int x = start.x;
        int y = start.y;
        double distanceSoFar = 0;
        for (int i = 0; i < vectors.size(); i++) {
            TileTransition v = vectors.get(i);
            distanceSoFar += v.getLength();
            x += v.deltaX;
            y += v.deltaY;
            if (distanceSoFar == distance) {
                return new Point2D(x * TileTransition.TILE_DIAMETER + TileTransition.TILE_DIAMETER / 2, y * TileTransition.TILE_DIAMETER + TileTransition.TILE_DIAMETER / 2);
            }
            if (distanceSoFar > distance) {
                int excess = (int) (TileTransition.TILE_DIAMETER * (distanceSoFar - distance) / v.getLength());
                x = x * TileTransition.TILE_DIAMETER - v.deltaX * excess;
                y = y * TileTransition.TILE_DIAMETER - v.deltaY * excess;
                return new Point2D(x + TileTransition.TILE_DIAMETER / 2, y + TileTransition.TILE_DIAMETER / 2);
            }
        }
        throw new IllegalArgumentException("distance:" + distance + " > getLength():" + vectors.size() + " distanceSoFar:" + distanceSoFar);
    }

    /**
     * Returns the coordinates of the point you would be standing at if you
     * walked the specified distance along the path from the start point.
     *
     * @throws IllegalArgumentException if distance &lt; 0
     * @throws IllegalArgumentException if distance &gt; getLength()
     */
    public Pair<Point2D, Point2D> getPoint(double firstdistance, double lastdistance) {
        if (0 > firstdistance) {
            throw new IllegalArgumentException("firstdistance:" + firstdistance + " < 0");
        }
        if (0 > lastdistance) {
            throw new IllegalArgumentException("lastdistance:" + lastdistance + " < 0");
        }
        if (firstdistance > lastdistance) {
            throw new IllegalArgumentException("firstdistance:" + firstdistance + " > lastdistance:" + lastdistance);
        }
        int x = start.x;
        int y = start.y;
        double distanceSoFar = 0;
        Point2D firstPoint = null;
        int i;
        TileTransition v = null;
        final int vectorsSize = vectors.size();
        for (i = 0; i < vectorsSize; i++) {
            v = vectors.get(i);
            distanceSoFar += v.getLength();
            x += v.deltaX;
            y += v.deltaY;
            if (distanceSoFar == firstdistance) {
                firstPoint = new Point2D(x * TileTransition.TILE_DIAMETER + TileTransition.TILE_DIAMETER / 2, y * TileTransition.TILE_DIAMETER + TileTransition.TILE_DIAMETER / 2);
                break;
            }
            if (distanceSoFar > firstdistance) {
                int excess = (int) (TileTransition.TILE_DIAMETER * (distanceSoFar - firstdistance) / v.getLength());
                int nx = x * TileTransition.TILE_DIAMETER - v.deltaX * excess + TileTransition.TILE_DIAMETER / 2;
                int ny = y * TileTransition.TILE_DIAMETER - v.deltaY * excess + TileTransition.TILE_DIAMETER / 2;
                firstPoint = new Point2D(nx, ny);
                break;
            }
        }
        if (firstPoint == null) {
            throw new IllegalArgumentException("firstdistance:" + firstdistance + " > getLength():" + vectorsSize + " distanceSoFar:" + distanceSoFar);
        }
        if (firstdistance == lastdistance) {
            return new Pair<>(firstPoint, firstPoint);
        }
        Point2D secondPoint = null;

        do {

            if (distanceSoFar == lastdistance) {
                secondPoint = new Point2D(x * TileTransition.TILE_DIAMETER + TileTransition.TILE_DIAMETER / 2, y * TileTransition.TILE_DIAMETER + TileTransition.TILE_DIAMETER / 2);
                break;
            }
            if (distanceSoFar > lastdistance) {
                int excess = (int) (TileTransition.TILE_DIAMETER * (distanceSoFar - lastdistance) / v.getLength());
                int nx = x * TileTransition.TILE_DIAMETER - v.deltaX * excess + TileTransition.TILE_DIAMETER / 2;
                int ny = y * TileTransition.TILE_DIAMETER - v.deltaY * excess + TileTransition.TILE_DIAMETER / 2;
                secondPoint = new Point2D(nx, ny);
                break;
            }
            i++;
            if (i >= vectorsSize) {
                break;
            }
            v = vectors.get(i);
            distanceSoFar += v.getLength();
            x += v.deltaX;
            y += v.deltaY;
        } while (true);

        if (secondPoint == null) {
            throw new IllegalArgumentException("lastdistance:" + lastdistance + " > getLength():" + vectorsSize + " distanceSoFar:" + distanceSoFar);
        }

        return new Pair<>(firstPoint, secondPoint);
    }

    /**
     * @return
     */
    public Point2D getStart() {
        return start;
    }

    /**
     * @param i
     * @return
     */
    public TileTransition getStep(int i) {
        return vectors.get(i);
    }

    /**
     * @return
     */
    public PositionOnTrack getFinalPosition() {
        int x = start.x;
        int y = start.y;
        for (int i = 0; i < vectors.size(); i++) {
            TileTransition v = vectors.get(i);
            x += v.deltaX;
            y += v.deltaY;
        }
        int i = vectors.size() - 1;
        TileTransition finalTileTransition = vectors.get(i);
        return PositionOnTrack.createFacing(x, y, finalTileTransition);
    }

    /**
     * Returns the index of the step that takes the distance travelled over the
     * specified distance.
     *
     * @throws IllegalArgumentException if distance &lt; 0
     * @throws IllegalArgumentException if distance &gt; getLength()
     */
    public int getStepIndex(int distance) {
        if (0 > distance) throw new IllegalArgumentException("distance < 0");
        int distanceSoFar = 0;
        for (int i = 0; i < vectors.size(); i++) {
            TileTransition v = vectors.get(i);
            distanceSoFar += v.getLength();
            if (distanceSoFar >= distance) return i;
        }
        throw new IllegalArgumentException("distance > getLength()");
    }

    @Override
    public int hashCode() {
        return start.hashCode();
    }

    /**
     * @return
     */
    public int steps() {
        return vectors.size();
    }

    /**
     * @param newTileTransitions
     * @return
     */
    public PathOnTiles addSteps(TileTransition... newTileTransitions) {
        int oldLength = vectors.size();
        TileTransition[] newPath = new TileTransition[oldLength + newTileTransitions.length];
        for (int i = 0; i < oldLength; i++) {
            newPath[i] = vectors.get(i);
        }
        System.arraycopy(newTileTransitions, 0, newPath, oldLength, newTileTransitions.length);
        return new PathOnTiles(start, newPath);
    }

    /**
     * Returns a PathIterator that exposes a sub section of the path
     * this object represents.
     *
     * @throws IllegalArgumentException if offset &lt; 0
     * @throws IllegalArgumentException if length &le; 0
     * @throws IllegalArgumentException if offset + length &gt; getLength()
     */
    public Pair<PathIterator, Integer> subPath(double offset, double length) {
        if (offset < 0) throw new IllegalArgumentException();
        if (length <= 0) throw new IllegalArgumentException();
        if ((offset + length) > getTotalDistance())
            throw new IllegalArgumentException(offset + " + " + length + " > " + getTotalDistance());

        final LinkedList<Point2D> points = new LinkedList<>();
        Point2D tile = start;
        int tileX = tile.x;
        int tileY = tile.y;
        int distanceSoFar = 0;
        for (int i = 0; i < vectors.size(); i++) {

            if (distanceSoFar > offset + length) {
                break;
            }
            if (distanceSoFar >= offset) {
                int x = TileTransition.TILE_DIAMETER / 2 + TileTransition.TILE_DIAMETER * tileX;
                int y = TileTransition.TILE_DIAMETER / 2 + TileTransition.TILE_DIAMETER * tileY;
                points.add(new Point2D(x, y));
            }

            TileTransition v = vectors.get(i);
            tileX += v.deltaX;
            tileY += v.deltaY;
            distanceSoFar += v.getLength();

        }

        Pair<Point2D, Point2D> point = getPoint(offset, offset + length);

        Point2D first = point.getA();

        if (points.isEmpty()) {
            points.addFirst(first);
        } else if (!points.getFirst().equals(first)) {
            points.addFirst(first);
        }

        Point2D last = point.getB();

        if (!points.getLast().equals(last)) {
            points.addLast(last);
        }

        return new Pair<>(new MyPathIterator(points), points.size());
    }

    /**
     * @return
     */
    public Iterator<Point2D> tiles() {
        return new Iterator<Point2D>() {
            int index = 0;

            Point2D next = start;

            public boolean hasNext() {
                return next != null;
            }

            public Point2D next() {
                if (next == null) throw new NoSuchElementException();

                Point2D returnValue = next;
                int x = next.x;
                int y = next.y;
                if (index < vectors.size()) {
                    TileTransition s = vectors.get(index);
                    x += s.deltaX;
                    y += s.deltaY;
                    next = new Point2D(x, y);
                } else {
                    next = null;
                }
                index++;

                return returnValue;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append('{');
        sb.append(start.x);
        sb.append(", ");
        sb.append(start.y);
        for (int i = 0; i < vectors.size(); i++) {
            sb.append(", ");
            sb.append(vectors.get(i));
        }
        sb.append('}');
        return sb.toString();
    }

    private static class MyPathIterator implements PathIterator {

        private static final long serialVersionUID = -4128415959622019625L;
        private final LinkedList<Point2D> points;
        int index;

        public MyPathIterator(LinkedList<Point2D> points) {
            this.points = points;
            index = 0;
        }

        public boolean hasNext() {
            return (index + 1) < points.size();
        }

        public void nextSegment(LineSegment line) {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Point2D a = points.get(index);
            line.setX1(a.x);
            line.setY1(a.y);

            Point2D b = points.get(index + 1);
            line.setX2(b.x);
            line.setY2(b.y);

            index++;
        }

    }
}
