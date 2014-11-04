package mo.umac.metadata;

import com.vividsolutions.jts.geom.Coordinate;
/*
 * @param intersection: a intersection point of the query circle and the centeral circle 
 * @param neighborP, radius: the center and radius of the neighbor circle corresponding the intersection point 
 */
public class PandC {
	private Coordinate intersection=new Coordinate();
	private Coordinate neighborP=new Coordinate();
	private double radius;
	
	public PandC(){
	}
	
	public PandC(Coordinate a, Coordinate b, double r){
		this.intersection=a;
		this.neighborP=b;
		this.radius=r;
	}
	
	public void setintersection(Coordinate a){
		this.intersection=a;
	}
	
	public Coordinate getintersection(){
		return this.intersection;
	}
	
	public void setneighborcenter(Coordinate b){
		this.neighborP=b;
	}
	
	public Coordinate getneighborcenter(){
		return this.neighborP;
	}
	
	public void setRadius(double r){
		this.radius=r;
	}
	 
	public double getRadius(){
		return this.radius;
	}

}
