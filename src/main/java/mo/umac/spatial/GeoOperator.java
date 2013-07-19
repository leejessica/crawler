package mo.umac.spatial;

import java.util.ArrayList;
import java.util.List;

import mo.umac.crawler.POI;
import mo.umac.crawler.YahooLocalCrawlerStrategy;

import org.apache.log4j.Logger;
import org.apache.xerces.dom3.DOMConfiguration;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geomgraph.Position;

/**
 * //FIXME all operations should be compared to postgresql operations in order
 * to check the correctness of CRS
 * 
 * 
 * @author kate
 * 
 */
public class GeoOperator {

    protected static Logger logger = Logger.getLogger(GeoOperator.class
	    .getName());

    public final static double RADIUS = 6371007.2;// authalic earth radius of
						  // 6371007.2 meters

    public static Envelope lla2ecef(Envelope envelope) {
	// converting the envelope
	double minX = envelope.getMinX();
	double maxX = envelope.getMaxX();
	double minY = envelope.getMinY();
	double maxY = envelope.getMaxY();

	double[] p1Lla = { minX, minY, 0 };
	double[] p1Ecef = ECEFLLA.lla2ecef(p1Lla);
	double[] p2Lla = { maxX, maxY, 0 };
	double[] p2Ecef = ECEFLLA.lla2ecef(p2Lla);
	Envelope envelopeEcef = new Envelope(p1Ecef[0], p1Ecef[1], p2Ecef[0],
		p2Ecef[1]);
	return envelopeEcef;

    }

    public void initCRS() {
	CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
	try {
	    CoordinateReferenceSystem crs = factory
		    .createCoordinateReferenceSystem("EPSG:4326");
	} catch (NoSuchAuthorityCodeException e) {
	    e.printStackTrace();
	} catch (FactoryException e) {
	    e.printStackTrace();
	}
	System.setProperty("org.geotools.referencing.forceXY", "true");
    }

    /**
     * Determine the the position of the point to the line
     * 
     * @param line
     * @param point
     * @return
     */
    public static int findPosition(LineSegment line, Coordinate point) {
	Coordinate p0 = line.p0;
	if (point.x < p0.x) {
	    return Position.LEFT;
	} else if (point.x > p0.x) {
	    return Position.RIGHT;
	} else {
	    return Position.ON;
	}
    }

    /**
     * {@link http://paulbourke.net/geometry/circlesphere/}
     * 
     * Calculate the intersection of a ray and a sphere. The line segment is
     * defined from p1 to p2 The sphere is of radius r and centered at sc There
     * are potentially two points of intersection given by p = p1 + mu1 (p2 -
     * p1) p = p1 + mu2 (p2 - p1) Return FALSE if the ray doesn't intersect the
     * sphere. *
     * 
     * @param circle
     * @param lineSeg
     * @return
     * @deprecated
     */
    public List intersect(Circle circle, LineSegment lineSeg) {
	List<Coordinate> list = new ArrayList<Coordinate>();
	double a, b, c;
	double bb4ac;
	Coordinate dp = new Coordinate();
	Coordinate p1 = lineSeg.p0;
	Coordinate p2 = lineSeg.p1;
	Coordinate sc = circle.getCenter();
	double r = circle.getRadius();
	dp.x = p2.x - p1.x;
	dp.y = p2.y - p1.y;
	a = dp.x * dp.x + dp.y * dp.y;
	b = 2 * (dp.x * (p1.x - sc.x) + dp.y * (p1.y - sc.y));
	c = sc.x * sc.x + sc.y * sc.y;
	c += p1.x * p1.x + p1.y * p1.y;
	c -= 2 * (sc.x * p1.x + sc.y * p1.y);
	c -= r * r;
	bb4ac = b * b - 4 * a * c;
	if (Math.abs(a) < 0.001 || bb4ac < 0) {
	    return null;
	}

	double t1 = (-b + Math.sqrt(bb4ac)) / (2 * a);
	double t2 = (-b - Math.sqrt(bb4ac)) / (2 * a);
	// Point p = p1+t*(p2-p1)
	return list;
    }

    /**
     * {@link http
     * ://gis.stackexchange.com/questions/36841/line-intersection-with
     * -circle-on-a-sphere-globe-or-earth}
     * 
     * @param circle
     * @param lineSeg
     * @return
     */
    public static List intersectOnEarth(Circle circle, LineSegment lineSeg) {
	List<Coordinate> list = new ArrayList<Coordinate>();
	double pi = Math.PI;// 3.141593;
	double degree = 2 * pi / 360;
	double radian = 1 / degree;
	double radius = RADIUS;
	// input
	Coordinate a0 = new Coordinate(lineSeg.p0.x * degree, lineSeg.p0.y
		* degree);
	Coordinate b0 = new Coordinate(lineSeg.p1.x * degree, lineSeg.p1.y
		* degree);
	Coordinate c0 = new Coordinate(circle.getCenter().x * degree,
		circle.getCenter().y * degree);
	double r = circle.getRadius();
	// projection project (lon, lat) to (R*cos(lat0) * lon, R*lat)
	Coordinate a = new Coordinate(a0.x * Math.cos(c0.x) * radius, a0.y * 1
		* radius);
	Coordinate b = new Coordinate(b0.x * Math.cos(c0.x) * radius, b0.y * 1
		* radius);
	Coordinate c = new Coordinate(c0.x * Math.cos(c0.x) * radius, c0.y * 1
		* radius);
	// Compute coefficients of the quadratic equation
	Coordinate v = new Coordinate(a.x - c.x, a.y - c.y);
	Coordinate u = new Coordinate(b.x - a.x, b.y - a.y);
	double alpha = u.x * u.x + u.y * u.y;
	double beta = u.x * v.x + u.y * v.y;
	double gamma = v.x * v.x + v.y * v.y - r * r;
	// judge the number of intersected points
	// solve the equation
	double delta = beta * beta - alpha * gamma;
	if (delta < 0) {
	    return null;
	} else if (Math.abs(delta - 0) < 0.001) {
	    // only one result
	    double t = -beta / alpha;
	    Coordinate x = new Coordinate(a0.x + (b0.x - a0.x) * radian, a0.y
		    + (b0.y - a0.y) * t * radian);
	    list.add(x);
	} else {
	    double t1 = (-beta - Math.sqrt(delta)) / alpha;
	    double t2 = (-beta + Math.sqrt(delta)) / alpha;
	    Coordinate x1 = new Coordinate(a0.x + (b0.x - a0.x) * radian, a0.y
		    + (b0.y - a0.y) * t1 * radian);
	    Coordinate x2 = new Coordinate(a0.x + (b0.x - a0.x) * radian, a0.y
		    + (b0.y - a0.y) * t2 * radian);
	    list.add(x1);
	    list.add(x2);
	}

	return list;
    }

    public void geoPoint() {

    }

    public double distance() {
	return 0.0;
    }

    /**
     * Compute the line segment which is parallel to the {@value middleLine} and
     * get through to the {@value outsidePoint}
     * 
     * @param middleLine
     * @param outsidePoint
     * @return
     */
    public static LineSegment parallel(LineSegment middleLine,
	    Coordinate outsideCoordinate) {
	Coordinate p0 = middleLine.p0;
	Coordinate p1 = middleLine.p1;
	double y0 = p0.y;
	double y1 = p1.y;
	double outsidePointX = outsideCoordinate.x;
	LineSegment lineSeg = new LineSegment(outsidePointX, y0, outsidePointX,
		y1);
	return lineSeg;
    }

    public static void logCoordinate(Coordinate coordinate) {
	logger.info("coordinate: " + coordinate.x + ", " + coordinate.y);
    }

    public static void logLineSegment(LineSegment lineSegment) {
	// logger.info("lineSegment: " + coordinate.x + ", " + coordinate.y);
    }

}
