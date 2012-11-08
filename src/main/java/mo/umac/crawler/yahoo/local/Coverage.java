/**
 * 
 */
package mo.umac.crawler.yahoo.local;

import java.util.List;

import mo.umac.crawler.utils.Circle;
import mo.umac.crawler.utils.Rectangle;

/**
 * It deals with the general coverage problems.
 * 
 * @author Kate YAN
 * 
 */
public class Coverage {

	
	
	/**
	 * Cover a rectangle with a number of circles which are circumcircles of unit rectangles. 
	 * 
	 * @param region
	 *            A big rectangle which covers the city/country
	 * @param unit
	 *            A small unit rectangle
	 *
	 */
	private List coverRectangleWithRectanglesCircumcicle(Rectangle region, Rectangle unit) {
		// TODO carefully design the returned list.  Saving space! Maybe we can consider about iterator!
		/* return only the first center point, 
		 * and the interval, on the direction of longitude and latitude*/		
		return null;
	}
	
	/**
	 * Compute the unit rectangle from the region which is represented by a big rectangle.  
	 * 
	 * @param rectangle this is the given region
	 * @param maxR the maximum radium of the circle
	 * @return A unit rectangle
	 */
	private Rectangle computeUnit(Rectangle rectangle, double maxR){
		// TODO
		double length = 0.0; 
		double width = 0.0;
		Rectangle unit = new Rectangle(length, width);
		return unit;
	}
	
	/**
	 * Compute the circumcircle of a rectangle
	 * 
	 * @param rectangle
	 * @return
	 * 
	 * @deprecated
	 */
	private Circle computeCircle(Rectangle rectangle){
		// TODO
		return null;
	}
	
}
