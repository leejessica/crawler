/**
 * 
 */
package mo.umac.crawler.utils;

import com.vividsolutions.jts.geom.Coordinate;


/**
 * This Circle represents a query which is also an area covered.
 * 
 * @author Kate Yim
 * 
 */
public class Circle {
	private Coordinate center = null;
	/* The unit of radius is 'm' in the map. */
	private double radius = 0.0;

	public Circle(Coordinate center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Coordinate getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}


}
