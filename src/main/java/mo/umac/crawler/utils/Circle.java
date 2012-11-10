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
	/* The unit of radium is 'm' in the map. */
	private double radium = 0.0;

	public Circle(Coordinate center, double radium) {
		this.center = center;
		this.radium = radium;
	}

	public Coordinate getCenter() {
		return center;
	}

	public double getRadium() {
		return radium;
	}

	public void setCenter(Coordinate center) {
		this.center = center;
	}

	public void setRadium(double radium) {
		this.radium = radium;
	}

}
