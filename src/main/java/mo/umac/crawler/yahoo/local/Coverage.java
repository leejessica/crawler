/**
 * 
 */
package mo.umac.crawler.yahoo.local;

import java.util.ArrayList;
import java.util.List;

import mo.umac.crawler.utils.Circle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * It deals with the general coverage problems.
 * 
 * @author Kate Yim
 * 
 */
public class Coverage {

	/**
	 * Compute the unit rectangle from the region which is represented by a big
	 * rectangle. The simplest implementation is compute the inscribed square in
	 * the circle. The complex implementation is to consider the inscribed
	 * rectangle.
	 * 
	 * @param rectangle
	 *            this is the given region
	 * @param maxR
	 *            the maximum radium of the circle
	 * @return A unit rectangle
	 * 
	 */
	private Coordinate computeUnit(Envelope envelope, double maxR) {
		double x = maxR / Math.sqrt(2);
		Coordinate unit = new Coordinate(x, x);
		return unit;
	}

	/**
	 * Cover a rectangle with a number of circles which are circumcircles of
	 * unit rectangles.
	 * 
	 * @param region
	 *            A big rectangle which covers the city/country
	 * @param unit
	 *            A small unit rectangle
	 * 
	 */
	private List coverEnvelope(Envelope region, Coordinate unit) {
		// TODO carefully design the returned list. Saving space! Maybe we can
		// consider about iterator!
		/*
		 * return only the first center point, and the interval, on the
		 * direction of longitude and latitude
		 */
		List list = new ArrayList<Envelope>();
		double interval = unit.x;
		double minX = region.getMinX();
		double maxX = region.getMaxX();
		double minY = region.getMinY();
		double maxY = region.getMaxY();
		double x2;
		double y2;
		for (double x1 = minX; x1 < maxX; x1 = x1 + interval) {
			x2 = x1 + interval;
			for (double y1 = minY; y1 < maxY; y1 = y1 + interval) {
				y2 = y1 + interval;
				Envelope small = new Envelope(x1, x2, y1, y2);
				list.add(small);
			}
		}
		return list;
	}

	/**
	 * Divide a rectangle into four small rectangles.
	 * 
	 * @param region
	 *            the rectangle
	 * @return A list contains 4 small rectangles
	 */
	private List divideRectagleWith(Envelope region) {
		List list = new ArrayList<Envelope>();
		double minX = region.getMinX();
		double maxX = region.getMaxX();
		double minY = region.getMinY();
		double maxY = region.getMaxY();
		double halfX = ( maxX - minX ) / 2;
		double halfY = ( maxY - minY ) / 2;
		double x2;
		double y2;
		for (double x1 = minX; x1 < maxX; x1 = x1 + halfX) {
			x2 = x1 + halfX;
			for (double y1 = minY; y1 < maxY; y1 = y1 + halfY) {
				y2 = y1 + halfY;
				Envelope small = new Envelope(x1, x2, y1, y2);
				list.add(small);
			}
		}
		return list;
	}

	/**
	 * Compute the circumcircle of a rectangle
	 * 
	 * @param rectangle
	 * @return
	 * 
	 * @deprecated
	 */
	private Circle computeCircle(Envelope rectangle) {
		// TODO
		return null;
	}

	private void divideRectangle(Envelope envelope) {

	}

}
