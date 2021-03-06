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
package freerails.util;

import java.awt.*;
import java.io.Serializable;

// TODO double version of it?
/**
 * Immutable vector with two integer coordinates and some arithmetic convenience functions.
 *
 * Mostly used for locations (points) or differences of locations (vectors).
 */
public final class Vec2D implements Serializable, Comparable<Vec2D> {

    private static final long serialVersionUID = -3053020239886388576L;
    public static final Vec2D ZERO = new Vec2D(0, 0);
    public final int x;
    public final int y;


    /**
     * @param x
     * @param y
     */
    public Vec2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     */
    public Vec2D() {
        this(0,0);
    }

    /**
     * Also creates a deep copy of the given object.
     *
     * @param o
     */
    public Vec2D(Vec2D o) {
        this(o.x, o.y);
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vec2D)) return false;

        final Vec2D other = (Vec2D) obj;

        return x == other.x && y == other.y;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return x * 29 + y;
    }

    /**
     *
     * @return
     */
    public double norm() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static Vec2D add(Vec2D a, Vec2D b) {
        return new Vec2D(a.x + b.x, a.y + b.y);
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static Vec2D subtract(Vec2D a, Vec2D b) {
        return new Vec2D(a.x - b.x, a.y - b.y);
    }

    public static Vec2D subtract(Vec2D a, int b) {
        return new Vec2D(a.x - b, a.y - b);
    }

    /**
     * Pointwise multiplication.
     * @param a
     * @param b
     * @return
     */
    public static Vec2D multiply(Vec2D a, Vec2D b) {
        return new Vec2D(a.x * b.x, a.y * b.y);
    }

    /**
     * Pointwise multiplication.
     * @param a
     * @param b
     * @return
     */
    public static Vec2D multiply(Vec2D a, int b) {
        return new Vec2D(a.x * b, a.y * b);
    }

    /**
     * Pointwise division by an int.
     * @param a
     * @param b
     * @return
     */
    public static Vec2D divide(Vec2D a, int b) {
        return new Vec2D(a.x / b, a.y / b);
    }

    /**
     * Pointwise maximum.
     *
     * @param a
     * @param b
     * @return
     */
    public static Vec2D max(Vec2D a, Vec2D b) {
        return new Vec2D(Math.max(a.x, b.x), Math.max(a.y, b.y));
    }

    /**
     * Pointwise minimum.
     *
     * @param a
     * @param b
     * @return
     */
    public static Vec2D min(Vec2D a, Vec2D b) {
        return new Vec2D(Math.min(a.x, b.x), Math.min(a.y, b.y));
    }

    // TODO Vec2D is not comparable because there is no larger/smaller notion, only equals
    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Vec2D o) {
        if (o.y != y) return y - o.y;
        else return x - o.x;
    }

    /**
     * Both members below the members of another vector.
     * @param o
     * @return
     */
    public boolean below(Vec2D o) {
        return x < o.x && y < o.y;
    }

    /**
     * Both members below or equal of the members of another vector.
     * @param o
     * @return
     */
    public boolean belowOrEqual(Vec2D o) {
        return x <= o.x && y <= o.y;
    }

    /**
     * Both members above the members of another vector.
     * @param o
     * @return
     */
    public boolean above(Vec2D o) {
        return x > o.x && y > o.y;
    }

    /**
     * Both members above or equal of the members of another vector.
     * @param o
     * @return
     */
    public boolean aboveOrEqual(Vec2D o) {
        return x >= o.x && y >= o.y;
    }

    /**
     *
     * @param p
     * @return
     */
    public static Point toPoint(Vec2D p) {
        return new Point(p.x, p.y);
    }

    /**
     *
     * @param p
     * @return
     */
    public static Dimension toDimension(Vec2D p) {
        return new Dimension(p.x, p.y);
    }
}
