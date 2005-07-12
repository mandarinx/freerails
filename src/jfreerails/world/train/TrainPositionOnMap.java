package jfreerails.world.train;

import jfreerails.util.IntArray;
import jfreerails.world.common.FreerailsPathIterator;
import jfreerails.world.common.FreerailsSerializable;
import jfreerails.world.common.ImInts;
import jfreerails.world.common.IntLine;

/**
 * This <b>immutable</b> class represents the position of a train as a String
 * of points. There must be at least two points. The first point is the position
 * of the front of the train; the last point is the position of the end of the
 * train. Any intermediate points are positions of 'kinks' in the track.
 * 
 * Coordinates are expressed in display coordinates relative to the map origin
 * (as opposed to map squares).
 * 
 * 
 * <p>
 * Train positions can be combined and divided as illustrated below (notice what
 * happens to the head and tail that are combined)
 * </p>
 * <table width="100%" border="0">
 * <tr>
 * <td>if</td>
 * <td><code> a</code></td>
 * <td><code>=</code></td>
 * <td><code>{<strong>(10, 10)</strong>, (20,20), (30,30), (40,40) }</code></td>
 * </tr>
 * <tr>
 * <td>and</td>
 * <td><code> b</code></td>
 * <td><code>=</code></td>
 * <td><code>{(1,1), (4,4), (5,5), <strong>(10, 10)</strong>}</code></td>
 * </tr>
 * <tr>
 * <td>then</td>
 * <td><code>a.addToHead(b)</code></td>
 * <td><code>=</code></td>
 * <td><code>{(1,1), (4,4), (5,5), (20,20), (30,30), (40,40) }</code></td>
 * </tr>
 * <tr>
 * <td>and</td>
 * <td><code>b.addToTail(a)</code></td>
 * <td><code>=</code></td>
 * <td><code>{(1,1), (4,4), (5,5), (20,20), (30,30), (40,40) }</code></td>
 * </tr>
 * <tr>
 * <td>and if</td>
 * <td><code> c</code></td>
 * <td><code>=</code></td>
 * <td><code>{(1,1), (4,4), (5,5), (20,20), (30,30), (40,40) }</code></td>
 * </tr>
 * <tr>
 * <td>then</td>
 * <td><code>c.removeFromTail(a)</code></td>
 * <td><code>=</code></td>
 * <td><code>{(1,1), (4,4), (5,5), (10, 10)}</code></td>
 * </tr>
 * <tr>
 * <td>and</td>
 * <td><code>c.removeFromHead(b)</code></td>
 * <td><code>=</code></td>
 * <td><code>{(10, 10), (20,20), (30,30), (40,40) }</code></td>
 * </tr>
 * </table>
 * 
 * 
 * @author Luke Lindsay 26-Oct-2002
 * 
 */
public class TrainPositionOnMap implements FreerailsSerializable {
	public static final int CRASH_FRAMES_COUNT = 15;

	private static final long serialVersionUID = 3979269144611010865L;

	private final ImInts m_xpoints;

	private final ImInts m_ypoints;

	private final double m_speed, m_acceleration;

	private final SpeedTimeAndStatus.Activity m_activity;

	private boolean crashSite = false;

	public boolean isCrashSite() {
		return crashSite;
	}

	public void setCrashSite(boolean isCrash) {
		crashSite = isCrash;
	}

	private int frameCt = 1;

	private int frame = 0;

	public int getFrameCt() {
		return frameCt;
	}

	public void incrementFramCt() {
		if (frame > 0) {
			incrementFrame();
			frame = 0;
		} else {
			frame++;
		}
	}

	public void incrementFrame() {
		frameCt++;
	}

	public int hashCode() {
		int result = 0;

		// TODO is there are danger of overflow here?
		for (int i = 0; i < m_xpoints.size(); i++) {
			result = 29 * result + m_xpoints.get(i);
		}

		for (int i = 0; i < m_ypoints.size(); i++) {
			result = 29 * result + m_ypoints.get(i);
		}

		return result;
	}

	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}

		if (o == this) {
			return true;
		}

		if (o instanceof TrainPositionOnMap) {
			TrainPositionOnMap other = (TrainPositionOnMap) o;
			int thisLength = this.getLength();
			int otherLength = other.getLength();

			if (thisLength == otherLength) {
				FreerailsPathIterator path1;
				FreerailsPathIterator path2;
				IntLine line1 = new IntLine();
				IntLine line2 = new IntLine();

				path1 = other.path();
				path2 = this.path();

				while (path1.hasNext() && path2.hasNext()) {
					path1.nextSegment(line1);
					path2.nextSegment(line2);

					if (line1.x1 != line2.x1 || line1.y1 != line2.y1
							|| line1.x2 != line2.x2 || line1.y2 != line2.y2) {
						return false;
					}
				}

				if (path1.hasNext() || path2.hasNext()) {
					return false;
				}
				return true;
			}
			return false;
		}
		return false;
	}

	public double calulateDistance() {
		double distance = 0;
		IntLine line = new IntLine();
		FreerailsPathIterator path = this.path();

		while (path.hasNext()) {
			path.nextSegment(line);

			int sumOfSquares = (line.x1 - line.x2) * (line.x1 - line.x2)
					+ (line.y1 - line.y2) * (line.y1 - line.y2);
			distance += Math.sqrt(sumOfSquares);
		}

		return distance;
	}

	public int getLength() {
		return m_xpoints.size();
	}

	public ImInts getXPoints() {
		return m_xpoints;
	}

	public ImInts getYPoints() {
		return m_ypoints;
	}

	public int getX(int position) {
		return m_xpoints.get(position);
	}

	public int getY(int position) {
		return m_ypoints.get(position);
	}

	public FreerailsPathIterator path() {
		return new SimplePathIteratorImpl(this.m_xpoints, this.m_ypoints);
	}

	public FreerailsPathIterator reversePath() {
		int length = m_xpoints.size();
		int[] reversed_xpoints = new int[length];
		int[] reversed_ypoints = new int[length];

		for (int i = 0; i < length; i++) {
			reversed_xpoints[i] = m_xpoints.get(length - i - 1);
			reversed_ypoints[i] = m_ypoints.get(length - i - 1);
		}

		return new SimplePathIteratorImpl(reversed_xpoints, reversed_ypoints);
	}

	public TrainPositionOnMap reverse() {
		int length = m_xpoints.size();
		int[] reversed_xpoints = new int[length];
		int[] reversed_ypoints = new int[length];

		for (int i = 0; i < length; i++) {
			reversed_xpoints[i] = m_xpoints.get(length - i - 1);
			reversed_ypoints[i] = m_ypoints.get(length - i - 1);
		}

		return new TrainPositionOnMap(reversed_xpoints, reversed_ypoints,
				m_speed, m_acceleration, m_activity);
	}

	public TrainPositionOnMap(ImInts xs, ImInts ys) {
		m_xpoints = xs;
		m_ypoints = ys;
		this.m_acceleration = 0d;
		this.m_speed = 0d;
		this.m_activity = SpeedTimeAndStatus.Activity.READY;

	}

	private TrainPositionOnMap(int[] xpoints, int[] ypoints, double speed,
			double acceleration, SpeedTimeAndStatus.Activity activity) {
		if (xpoints.length != ypoints.length) {
			throw new IllegalArgumentException();
		}

		m_xpoints = new ImInts(xpoints);
		m_ypoints = new ImInts(ypoints);
		this.m_acceleration = acceleration;
		this.m_speed = speed;
		this.m_activity = activity;
	}

	public static TrainPositionOnMap createInstance(int[] xpoints, int[] ypoints) {
		return new TrainPositionOnMap(xpoints, ypoints, 0d, 0d,
				SpeedTimeAndStatus.Activity.READY);
	}

	public TrainPositionOnMap addToHead(TrainPositionOnMap b) {
		TrainPositionOnMap a = this;

		return addBtoHeadOfA(b, a);
	}

	private TrainPositionOnMap addBtoHeadOfA(TrainPositionOnMap b,
			TrainPositionOnMap a) {
		if (aHeadEqualsBTail(a, b)) {
			int newLength = a.getLength() + b.getLength() - 2;

			int[] newXpoints = new int[newLength];
			int[] newYpoints = new int[newLength];

			int aLength = a.getLength();
			int bLength = b.getLength();

			// First copy the points from B
			for (int i = 0; i < bLength - 1; i++) {
				newXpoints[i] = b.getX(i);
				newYpoints[i] = b.getY(i);
			}

			// Second copy the points from A.
			for (int i = 1; i < aLength; i++) {
				newXpoints[i + bLength - 2] = a.getX(i);
				newYpoints[i + bLength - 2] = a.getY(i);
			}

			return new TrainPositionOnMap(newXpoints, newYpoints,
					b.m_acceleration, b.m_speed, b.m_activity);
		}
		throw new IllegalArgumentException("Tried to add " + b.toString()
				+ " to the head of " + a.toString());
	}

	public boolean canAddToHead(TrainPositionOnMap b) {
		return aHeadEqualsBTail(this, b);
	}

	public TrainPositionOnMap addToTail(TrainPositionOnMap a) {
		TrainPositionOnMap b = this;

		return addBtoHeadOfA(b, a);
	}

	public boolean canAddToTail(TrainPositionOnMap b) {
		return aHeadEqualsBTail(b, this);
	}

	public TrainPositionOnMap removeFromHead(TrainPositionOnMap b) {
		if (headsAreEqual(this, b)) {
			int newLength = this.getLength() - b.getLength() + 2;

			int[] newXpoints = new int[newLength];
			int[] newYpoints = new int[newLength];

			int bLength = b.getLength();

			// copy head from b
			int bHeadPosition = b.getLength() - 1;
			newXpoints[0] = b.getX(bHeadPosition);
			newYpoints[0] = b.getY(bHeadPosition);

			// Copy rest from this
			for (int i = 1; i < newLength; i++) {
				int position = bLength + i - 2;

				newXpoints[i] = this.getX(position);
				newYpoints[i] = this.getY(position);
			}

			return new TrainPositionOnMap(newXpoints, newYpoints, m_speed,
					m_acceleration, m_activity);
		}
		throw new IllegalArgumentException();
	}

	public boolean canRemoveFromHead(TrainPositionOnMap b) {
		if (headsAreEqual(this, b)) {
			FreerailsPathIterator path = b.path();
			int i = 0;
			IntLine line = new IntLine();

			while (path.hasNext()) {
				path.nextSegment(line);

				if (this.getX(i) != line.x1 || this.getY(i) != line.y1) {
					return false;
				}

				i++;
			}

			return true;
		}
		return false;
	}

	public TrainPositionOnMap removeFromTail(TrainPositionOnMap b) {
		if (tailsAreEqual(this, b)) {
			int newLength = this.getLength() - b.getLength() + 2;

			int[] newXpoints = new int[newLength];
			int[] newYpoints = new int[newLength];

			// Copy from this
			for (int i = 0; i < newLength - 1; i++) {
				newXpoints[i] = this.getX(i);
				newYpoints[i] = this.getY(i);
			}

			// Copy tail from b
			newXpoints[newLength - 1] = b.getX(0);
			newYpoints[newLength - 1] = b.getY(0);

			return new TrainPositionOnMap(newXpoints, newYpoints, m_speed,
					m_acceleration, m_activity);
		}
		throw new IllegalArgumentException();
	}

	public boolean canRemoveFromTail(TrainPositionOnMap b) {
		if (tailsAreEqual(this, b)) {
			FreerailsPathIterator path = b.reversePath();
			int i = this.getLength() - 1;
			IntLine line = new IntLine();

			while (path.hasNext()) {
				path.nextSegment(line);

				if (this.getX(i) != line.x1 || this.getY(i) != line.y1) {
					return false;
				}

				i--;
			}

			return true;
		}
		return false;
	}

	public static TrainPositionOnMap createInSameDirectionAsPath(
			FreerailsPathIterator path) {
		return createInSameDirectionAsPath(path, 0d, 0d,
				SpeedTimeAndStatus.Activity.READY);
	}

	public static TrainPositionOnMap createInSameDirectionAsPath(
			FreerailsPathIterator path, double speed, double acceleration,
			SpeedTimeAndStatus.Activity activity) {
		IntArray xPointsIntArray = new IntArray();
		IntArray yPointsIntArray = new IntArray();
		IntLine line = new IntLine();
		int i = 0;

		while (path.hasNext()) {
			path.nextSegment(line);
			xPointsIntArray.add(i, line.x1);
			yPointsIntArray.add(i, line.y1);
			i++;

			if (i > 10000) {
				throw new IllegalStateException(
						"The TrainPosition has more than 10,000 points, which suggests that something is wrong.");
			}
		}

		xPointsIntArray.add(i, line.x2);
		yPointsIntArray.add(i, line.y2);

		int[] xPoints;
		int[] yPoints;

		xPoints = xPointsIntArray.toArray();
		yPoints = yPointsIntArray.toArray();

		return new TrainPositionOnMap(xPoints, yPoints, speed, acceleration,
				activity);
	}

	public static boolean headsAreEqual(TrainPositionOnMap a,
			TrainPositionOnMap b) {
		int aHeadX = a.getX(0);
		int aHeadY = a.getY(0);
		int bHeadX = b.getX(0);
		int bHeadY = b.getY(0);

		if (aHeadX == bHeadX && aHeadY == bHeadY) {
			return true;
		}
		return false;
	}

	public static boolean tailsAreEqual(TrainPositionOnMap a,
			TrainPositionOnMap b) {
		int aTailX = a.getX(a.getLength() - 1);
		int aTailY = a.getY(a.getLength() - 1);
		int bTailX = b.getX(b.getLength() - 1);
		int bTailY = b.getY(b.getLength() - 1);

		if (aTailX == bTailX && aTailY == bTailY) {
			return true;
		}
		return false;
	}

	public static boolean aHeadEqualsBTail(TrainPositionOnMap a,
			TrainPositionOnMap b) {
		int aHeadX = a.getX(0);
		int aHeadY = a.getY(0);

		int bTailX = b.getX(b.getLength() - 1);
		int bTailY = b.getY(b.getLength() - 1);

		if (aHeadX == bTailX && aHeadY == bTailY) {
			return true;
		}
		return false;
	}

	public static boolean bHeadEqualsATail(TrainPositionOnMap a,
			TrainPositionOnMap b) {
		return aHeadEqualsBTail(b, a);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TrainPosition {");

		for (int i = 0; i < m_xpoints.size(); i++) {
			sb.append("(");
			sb.append(m_xpoints.get(i));
			sb.append(", ");
			sb.append(m_ypoints.get(i));
			sb.append("), ");
		}

		sb.append("}");

		return sb.toString();
	}
}