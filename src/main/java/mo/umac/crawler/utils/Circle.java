/**
 * 
 */
package mo.umac.crawler.utils;

import com.vividsolutions.jts.geom.Point;

/**
 * This Circle represents a query which is also an area covered.
 * 
 * @author Kate YAN
 * 
 */
public class Circle {
	private Point center = null;
	/* The unit of radium is 'm' in the map. */
	private double radium = 0.0;

	public Circle(Point center, double radium) {
		this.center = center;
		this.radium = radium;
	}

	public Point getCenter() {
		return center;
	}

	public double getRadium() {
		return radium;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public void setRadium(double radium) {
		this.radium = radium;
	}

}
