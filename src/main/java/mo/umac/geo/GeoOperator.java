package mo.umac.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geomgraph.Position;

public class GeoOperator {

    public static int findPosition(LineSegment line, Coordinate point){
	Coordinate p0 = line.p0;
	Coordinate p1 = line.p1;
	// http://stackoverflow.com/questions/1560492/how-to-tell-whether-a-point-is-to-the-right-or-left-of-a-line
	// http://stackoverflow.com/questions/3461453/determine-which-side-of-a-line-a-point-lies
	// FIXME findPosition
	return Position.LEFT;
    }
    
}
