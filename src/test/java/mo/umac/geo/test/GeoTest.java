package mo.umac.geo.test;

import org.opengis.referencing.cs.CoordinateSystem;

import com.vividsolutions.jts.awt.PointShapeFactory;


public class GeoTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }
    
    private void testCircle(){
	PointShapeFactory.Circle(); 
    }
    
    private void testCoordinateSystem(){
	CoordinateSystem coordinateSystem = CoordinateSystem.getCoordinateSystem();
    }

    pirvate void test intersectionPoints(){
    	float a = d.Dot( d ) ;
    	float b = 2*f.Dot( d ) ;
    	float c = f.Dot( f ) - r*r ;

    	float discriminant = b*b-4*a*c;
    	if( discriminant &lt; 0 )
    	{
    	  // no intersection
    	}
    	else
    	{
    	  // ray didn't totally miss sphere,
    	  // so there is a solution to
    	  // the equation.

    	  discriminant = sqrt( discriminant );

    	  // either solution may be on or off the ray so need to test both
    	  // t1 is always the smaller value, because BOTH discriminant and
    	  // a are nonnegative.
    	  float t1 = (-b - discriminant)/(2*a);
    	  float t2 = (-b + discriminant)/(2*a);

    	  // 3x HIT cases:
    	  //          -o->             --|-->  |            |  --|->
    	  // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit), 

    	  // 3x MISS cases:
    	  //       ->  o                     o ->              | -> |
    	  // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

    	  if( t1 >= 0 && t1 <= 1 )
    	  {
    	    // t1 is an intersection, and if it hits,
    	    // it's closer than t2 would be
    	    // Impale, Poke
    	    return true ;
    	  }

    	  // here t1 didn't intersect so we are either started
    	  // inside the sphere or completely past it
    	  if( t2 >= 0 && t2 <= 1 )
    	  {
    	    // ExitWound
    	    return true ;
    	  }

    	  // no intn: FallShort, Past, CompletelyInside
    	  return false ;
    	}
    }
}
