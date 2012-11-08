/**
 * 
 */
package mo.umac.crawler.utils;

import com.vividsolutions.jts.geom.Point;

/**
 * @author Kate YAN
 * 
 */
public class Rectangle {
	private Point upperLeft = null;
	private Point upperRight = null;
	private Point lowerLeft = null;
	private Point lowerRight = null;
	private Point center = null;
	private double length = 0.0;
	private double width = 0.0;
	private double diagonal = 0.0;

	/**
	 * Construct a rectangle with two points
	 * 
	 * @param upperLeft
	 * @param lowerRight
	 */
	public Rectangle(Point upperLeft, Point lowerRight) {
		super();
		this.upperLeft = upperLeft;
		this.lowerRight = lowerRight;
	}

	/**
	 * Construct a rectangle with length and width.
	 * 
	 * @param length
	 * @param width
	 */
	public Rectangle(double length, double width) {
		this.length = length;
		this.width = width;
	}

	/**
	 * Compute parameters of the rectangle from the upperLeft and lowerRight
	 * points.
	 * 
	 * @param upperLeft
	 * @param lowerRight
	 * @return
	 */
	private void compute(Point upperLeft, Point lowerRight) {
		// TODO upperRight, lowerLeft, center, length, width, diagonal
	}

	/**
	 * Compute parameters of the rectangle from length and width
	 * 
	 * @param length
	 * @param width
	 */
	private void compute(double length, double width) {
		// TODO point * 4, center, diagonal
		// The default values of point are from the original point. 
	}

	public Point center() {
		if (center == null && upperLeft != null && lowerRight != null) {
			compute(upperLeft, lowerRight);
		} else if (center == null && length != 0.0 && width != 0.0) {
			compute(length, width);
		}
		return center;
	}

	public double diagonal() {
		if (center == null && upperLeft != null && lowerRight != null) {
			compute(upperLeft, lowerRight);
		} else if (center == null && length != 0.0 && width != 0.0) {
			compute(length, width);
		}
		return diagonal;
	}
}
