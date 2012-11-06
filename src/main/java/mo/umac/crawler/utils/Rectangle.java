/**
 * 
 */
package mo.umac.crawler.utils;


/**
 * @author Kate YAN
 * 
 */
public class Rectangle {
	private Point upperLeft = new Point(0.0, 0.0);
	private Point upperRight = new Point(0.0, 0.0);
	private Point lowerLeft = new Point(0.0, 0.0);;
	private Point lowerRight = new Point(0.0, 0.0);;
	private Point center = new Point(0.0, 0.0);;
	private double length = 0.0;
	private double width = 0.0;
	private double diagonal = 0.0;
	
	public Rectangle(Point upperLeft, Point lowerRight) {
		super();
		this.upperLeft = upperLeft;
		this.lowerRight = lowerRight;
	}
	
	public Point computeCenter(){
		//TODO
		return null;
	}
	
	public double computeDiagonal(){
		//TODO
		return 0.0;
	}
	
	public Point upperLeft(){
		return upperLeft;
	}
	
	public Point center(){
		return center;
	}
	
	public double diagonal(){
		return diagonal;
	}
}
