package mo.umac.spatial.test;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * 
 * Class GeometryRelated.java
 * 
 * Description 二元比较集合。二元比较以两个几何对象作为参数，返回一个Boolean类型的值，
 * 来指明这两个几何对象是否具有指定的空间关系。支持的空间关系包括： equals、disjoint、intersects, touches,
 * crosses, within, contains, overlaps
 * 
 * Company mapbar
 * 
 * author Chenll E-mail: Chenll@mapbar.com
 * 
 * Version 1.0
 * 
 * Date 2012-2-17 下午06:17:01
 */
public class GeometryRelated {

    private GeometryFactory geometryFactory = JTSFactoryFinder
	    .getGeometryFactory(null);

    public Point createPoint(String lon, String lat) {
	Coordinate coord = new Coordinate(Double.parseDouble(lon),
		Double.parseDouble(lat));
	Point point = geometryFactory.createPoint(coord);
	return point;
    }

    /**
     * will return true as the two line strings define exactly the same shape.
     * 两个几何对象是否是重叠的
     * 
     * @return
     * @throws ParseException
     */
    public boolean equalsGeo() throws ParseException {
	WKTReader reader = new WKTReader(geometryFactory);
	LineString geometry1 = (LineString) reader
		.read("LINESTRING(0 0, 2 0, 5 0)");
	LineString geometry2 = (LineString) reader.read("LINESTRING(5 0, 0 0)");
	// return geometry1 ==geometry2; false
	// check if two geometries are exactly equal; right down to the
	// coordinate level.
	// return geometry1.equalsExact(geometry2); false
	return geometry1.equals(geometry2);// true
    }

    /**
     * The geometries have no points in common 几何对象没有交点(相邻)
     * 
     * @return
     * @throws ParseException
     */
    public boolean disjointGeo() throws ParseException {
	WKTReader reader = new WKTReader(geometryFactory);
	LineString geometry1 = (LineString) reader
		.read("LINESTRING(0 0, 2 0, 5 0)");
	LineString geometry2 = (LineString) reader.read("LINESTRING(0 1, 0 2)");
	return geometry1.disjoint(geometry2);
    }

    /**
     * The geometries have at least one point in common. 至少一个公共点(相交)
     * 
     * @return
     * @throws ParseException
     */
    public boolean intersectsGeo() throws ParseException {
	WKTReader reader = new WKTReader(geometryFactory);
	LineString geometry1 = (LineString) reader
		.read("LINESTRING(0 0, 2 0, 5 0)");
	LineString geometry2 = (LineString) reader.read("LINESTRING(0 0, 0 2)");
	Geometry interPoint = geometry1.intersection(geometry2);// 相交点
	System.out.println(interPoint.toText());// 输出 POINT (0 0)
	return geometry1.intersects(geometry2);
    }

    /**
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
	GeometryRelated gr = new GeometryRelated();
	System.out.println(gr.equalsGeo());
	System.out.println(gr.disjointGeo());
	System.out.println(gr.intersectsGeo());
    }

}
