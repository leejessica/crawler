package minitest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import mo.umac.crawler.offline.SortedBydistance;
import mo.umac.db.DBInMemory;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.IntersectPoint;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.VQP;
import mo.umac.metadata.VQP1;
import mo.umac.paint.PaintShapes;
import mo.umac.spatial.Circle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

public class Line_circle_test {
	public static int i = 0;
	public static double inRadius = 1;
	public static Coordinate startPoint1 = new Coordinate();

	public Line_circle_test() {
		super();
		System.out.println("this is a test program!!!!!!\n");
	}

	/* return the the intersection point between a linesegment and a circle */
	public static Coordinate getIntersectPoint(Coordinate startPoint,
			Coordinate p, double radius) {
		Coordinate[] intsectArray = line_circle_intersect(startPoint, radius, p);
		// record the intersection point
		Coordinate intsectPoint = new Coordinate();
		// obtain the point which is on the linesegment l
		LineSegment l = new LineSegment(startPoint, p);
		if (isOnLinesegment(intsectArray[0], l))
			intsectPoint = intsectArray[0];
		else
			intsectPoint = intsectArray[1];
		return intsectPoint;
	}

	public static boolean isOnLinesegment(Coordinate p, LineSegment l) {
		if (Math.abs(l.distance(p) - 0) < 1e-6)
			return true;
		else
			return false;
	}

	public static Coordinate[] line_circle_intersect(Coordinate startPoint,
			double radius, Coordinate p) {
		Coordinate[] a = new Coordinate[2];
		a[0] = new Coordinate();
		a[1] = new Coordinate();
		// the slope of the line:k=infinite
		if (p.x == startPoint.x) {
			a[0].x = startPoint.x;
			a[0].y = startPoint.y + radius;
			a[1].x = startPoint.x;
			a[1].y = startPoint.y - radius;
		}
		// k=0
		else if (p.y == startPoint.y) {
			a[0].x = startPoint.x + radius;
			a[0].y = startPoint.y;
			a[1].x = startPoint.x - radius;
			a[1].y = startPoint.y;
		} else {
			double k = (p.y - startPoint.y) / (p.x - startPoint.x);
			double A = Math.sqrt((radius * radius) / (1 + k * k));
			a[0].x = startPoint.x + A;
			a[0].y = startPoint.y + k * A;
			a[1].x = startPoint.x - A;
			a[1].y = startPoint.y - k * A;
		}
		return a;
	}

	public static boolean isinCircle1(Coordinate p, VQP vqp) {
		if (vqp.getCoordinate().distance(p) < vqp.getRadius()
				|| Math.abs(vqp.getCoordinate().distance(p) - vqp.getRadius()) < 1e-6)
			return true;
		return false;
	}

	public static void binaryQuery(Coordinate startPoint,
			Coordinate refCoordinate,
			/* String state, int category, String query, */
			LinkedList<VQP> visitedInfoQ, TreeSet<VQP> visitedOnlineQ,
			LinkedList<Coordinate> visitedQ) {

		// the maximum inscribed circle centered at startPoint
		VQP inscribedCircle = new VQP(startPoint, inRadius);
		/*
		 * obtain the intersection point between the linesegment(startPoint,
		 * refCoordinate) and the circle(startPoint, inRadius)
		 */
		Coordinate intsectPoint = getIntersectPoint(startPoint, refCoordinate,
				inRadius);
		System.out.println("intsectPoint=" + intsectPoint);
		// initial the startPoint1 in a fresh binary search procedure
		startPoint1 = intsectPoint;
		/*
		 * calculate the intersection point between c1 and
		 * linesegment(c1.getCoordinate(), startPoint)
		 */
		VQP firstCircle = visitedOnlineQ.first();
		Coordinate intsectPoint1 = getIntersectPoint(
				firstCircle.getCoordinate(), startPoint,
				firstCircle.getRadius());
		System.out.println("intsectPoint1=" + intsectPoint1);
		// keep track of the position for binary search
		Coordinate biCoordinate = new Coordinate();
		biCoordinate.x = (intsectPoint1.x + intsectPoint.x) / 2;
		biCoordinate.y = (intsectPoint1.y + intsectPoint.y) / 2;
		//LinkedList<VQP>temvisitedInfo=new LinkedList<VQP>();
		//LinkedList<Coordinate>temvisitedQ=new LinkedList<Coordinate>();
		//TreeSet<VQP>temvisitedOnlineQ=new TreeSet<VQP>(new SortedBydistance(startPoint));

		// exist vacancy between the biCoordinate position and the inscribed
		// circle
		while (!isinCircle1(biCoordinate, inscribedCircle)) {
			i++;
			// issue a query at the biCoordinate
			// record the circle(biCoordinate, biRadius)
			double biRadius = 1 + i * 0.1;
			Coordinate biCoordinate2 = (Coordinate)biCoordinate.clone();
			VQP q = new VQP(biCoordinate2, biRadius);
		    visitedInfoQ.add(q);
			visitedOnlineQ.add(q);
			visitedQ.addLast(biCoordinate2);
			intsectPoint1 = getIntersectPoint(biCoordinate, startPoint,
					biRadius);
			System.out.println("________________biCoordinate="+biCoordinate+"  biRadius="+biRadius);
			// calculate new biCoordinate
			biCoordinate.x = (intsectPoint1.x + intsectPoint.x) / 2;
			biCoordinate.y = (intsectPoint1.y + intsectPoint.y) / 2;

		}// END find the right position for ringCover
		//visitedInfoQ.addAll(temvisitedInfo);
		//visitedOnlineQ.addAll(temvisitedOnlineQ); 
		//visitedQ.addAll(temvisitedQ);
		Iterator<VQP> it = visitedOnlineQ.iterator();
		while (it.hasNext()) {
			VQP p = it.next();
			System.out.println("p=" + p.getCoordinate() + "  " + p.getRadius());
		}
		
		Iterator<Coordinate> it2 = visitedQ.iterator();
		while (it2.hasNext()) {
			Coordinate p2 = it2.next();
			System.out.println("p2=" + p2);
		}
	}

	public static void main(String[] args) {
		Coordinate s = new Coordinate(0, 0);
		LinkedList<VQP> visitedInfoQ = new LinkedList<VQP>();
		LinkedList<Coordinate> visitedQ = new LinkedList<Coordinate>();
		TreeSet<VQP> visitedOnlineQ = new TreeSet<VQP>(new SortedBydistance(s));

		Coordinate r = new Coordinate(20, 20);

		visitedOnlineQ.add(new VQP(r, 0));
		// Coordinate []a=line_circle_intersect(s, inRadius, r);
		// System.out.println("line_circle_intersect="+a[0]+"   "+a[1]);
		binaryQuery(s, r, visitedInfoQ, visitedOnlineQ, visitedQ);
		
	}

}
