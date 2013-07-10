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

}
