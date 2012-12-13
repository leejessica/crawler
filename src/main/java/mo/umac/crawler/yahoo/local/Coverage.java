/**
 * 
 */
package mo.umac.crawler.yahoo.local;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import mo.umac.crawler.utils.Circle;

import org.geotools.referencing.GeodeticCalculator;

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
	 * Compute the distance (in miles) between two points.
	 * 
	 * @param p1
	 *            A start coordinate
	 * @param p2
	 *            An end coordinate
	 * 
	 */
	public double coordinateToMiles(Coordinate p1, Coordinate p2) {
		// TODO check
		double mile = 0.0;
		GeodeticCalculator calculator = new GeodeticCalculator();
		calculator.setStartingGeographicPoint(p1.x, p1.y);
		calculator.setDestinationGeographicPoint(p2.x, p2.y);
		double meter = calculator.getOrthodromicDistance();
		mile = meter * 0.000621371190;
		return mile;
	}

	/**
	 * Compute the coordinates represent a distance of a mile.
	 * 
	 * @param p1
	 *            A starting coordinate
	 * @param distance
	 *            the distance (in miles) between two coordinates.
	 */
	public Coordinate mileToCoordinates(Coordinate p1, double miles,
			double azimuth) {
		double meter = miles * 1609.34;
		GeodeticCalculator calculator = new GeodeticCalculator();
		// TODO not sure!
		calculator.setDirection(azimuth, meter);
		Point2D point = calculator.getDestinationGeographicPoint();
		Coordinate p2 = new Coordinate();
		p2.x = point.getX();
		p2.y = point.getY();
		return p2;
	}

	/**
	 * Divide a MBR into four small rectangles.
	 * 
	 * @param region
	 *            the rectangle
	 * @return A list of envelopes containing 4 small rectangles
	 */
	public List divideEnvelope(Envelope region) {
		ArrayList<Envelope> list = new ArrayList<Envelope>();
		double minX = region.getMinX();
		double maxX = region.getMaxX();
		double minY = region.getMinY();
		double maxY = region.getMaxY();
		double halfX = (maxX - minX) / 2;
		double halfY = (maxY - minY) / 2;
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
	 * Compute the unit rectangle from the region which is represented by a big
	 * rectangle. The simplest implementation is to compute the inscribed square in
	 * the circle. The complex implementation is to consider the inscribed
	 * rectangle.
	 * 
	 * @param rectangle
	 *            this is the given region.
	 * @param maxR
	 *            the maximum radius of the circle
	 * @return A unit rectangle
	 * 
	 */
	public static Envelope computeUnit(Envelope envelope, double maxR) {
		double x = maxR / Math.sqrt(2);
		Envelope unit = new Envelope(0, x, 0, x);
		return unit;
	}

	/**
	 * Compute the radius of the circumcircle of the envelope
	 * 
	 * @param envelope
	 * @return the radius
	 */
	public static Circle computeCircle(Envelope envelope) {
		double height = envelope.getHeight();
		double width = envelope.getWidth();
		double radius = Math.sqrt(height * height + width * width) / 2;
		Coordinate center = new Coordinate(envelope.centre().x,
				envelope.centre().y, 0);
		Circle circle = new Circle(center, radius);
		return circle;
	}

	/**
	 * Cover a rectangle with a number of circles which are circumcircles of
	 * unit rectangles.
	 * 
	 * @param region
	 *            A big rectangle which covers the city/country
	 * @param unit
	 *            A small unit rectangle
	 * @deprecated Compute at {@link Crawler.java}
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
	 * Get the first envelope in this region.
	 * @param region
	 * @param unit
	 * @param overflow TODO
	 * @return
	 */
	public static Envelope firstEnvelopeInRegion(Envelope region, Envelope unit, boolean overflow) {
		Envelope first = new Envelope();
		// TODO
		return first;
	}

	/**
	 * The number of sub-regions by dividing the <code>region</code> by
	 * <code>unit</code>
	 * 
	 * @param region
	 * @param unit
	 * @return
	 */
	public static int numsSubRegions(Envelope region, Envelope unit) {
		// TODO
		int number = 0;
		return number;
	}

	/**
	 * Get next region according to the previous envelope
	 * 
	 * @param envelopeState
	 *            The MBR of all regions
	 * @param aEnvelope
	 *            previous region
	 * @param unit
	 *            the unit region
	 * @param overflow TODO
	 * @return
	 */
	public static Envelope nextEnvelopeInRegion(Envelope envelopeState,
			Envelope aEnvelope, Envelope unit, boolean overflow) {
		// TODO
		return null;
	}
}
