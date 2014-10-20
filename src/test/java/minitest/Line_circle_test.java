package minitest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import mo.umac.metadata.IntersectPoint;
import mo.umac.metadata.VQP;
import mo.umac.metadata.VQP1;

import com.vividsolutions.jts.geom.Coordinate;

public class Line_circle_test {

	public Line_circle_test() {
		super();
		System.out.println("this is a test program!!!!!!\n");
	}

	public static boolean needQuery(VQP circle, Set<VQP> Neighbor_set) {
		boolean needquery = false;
		if (!isCircumferenceCoverage(circle, Neighbor_set))
			needquery = true;
		else {
			System.out
					.println("that means the circle is circumferenceCoverage!!!");
			Iterator<VQP> it1 = Neighbor_set.iterator();
			while (it1.hasNext() && !needquery) {
				VQP circle1 = it1.next();
				if (!isPerimeterCoverage(circle, circle1, Neighbor_set))
					needquery = true;
			}
		}
		return needquery;
	}

	public static boolean isPerimeterCoverage(VQP circle, VQP circle1,
			Set<VQP> Neighbor_set) {
		boolean coverage = true;
		Iterator<VQP> it1 = Neighbor_set.iterator();
		// @param: record all the neighbors to circle1 except circle
		LinkedList<VQP> intercircleQ = new LinkedList<VQP>();
		// update the intercircleQ
		while (it1.hasNext()) {
			VQP circle2 = it1.next();
			if (!pointsequal(circle1.getCoordinate(), circle2.getCoordinate())
					&& circles_Insecter(circle1, circle2))
				intercircleQ.addLast(circle2);
		}
		Iterator<VQP> it2 = intercircleQ.iterator();
		while (it2.hasNext() && coverage) {
			VQP circletemp1 = it2.next();
			IntersectPoint inter1 = calculateIntersectPoint(circle1,
					circletemp1);
			boolean stopleft = false;
			boolean stopright = false;
			if (isinCircle(inter1.getIntersectPoint_left(), circle)
					|| isAtCircumference(inter1.getIntersectPoint_left(),
							circle)) {
				Iterator<VQP> it3 = intercircleQ.iterator();
				while (it3.hasNext() && !stopleft) {
					VQP circletemp2 = it3.next();
					if (!pointsequal(circletemp1.getCoordinate(),
							circletemp2.getCoordinate())) {
						if (isinCircle(inter1.getIntersectPoint_left(),
								circletemp2)
								|| isAtCircumference(
										inter1.getIntersectPoint_left(),
										circletemp2)) {
							stopleft = true;
						}
					}
				}
			} else {
				stopleft = true;
			}
			if (isinCircle(inter1.getIntersectPoint_right(), circle)
					|| isAtCircumference(inter1.getIntersectPoint_right(),
							circle)) {
				Iterator<VQP> it4 = intercircleQ.iterator();
				while (it4.hasNext() && !stopright) {
					VQP circletemp3 = it4.next();
					if (!pointsequal(circletemp1.getCoordinate(),
							circletemp3.getCoordinate())) {
						if (isinCircle(inter1.getIntersectPoint_right(),
								circletemp3)
								|| isAtCircumference(
										inter1.getIntersectPoint_right(),
										circletemp3)) {
							stopright = true;
						}
					}
				}
			} else {
				stopright = true;
			}
			if (!stopleft || !stopright)
				coverage = false;
		}
		System.out.println("isPerimeterCoverage=" + coverage);
		return coverage;
	}

	// to determine whether 2 circles intersect or not
	public static boolean circles_Insecter(VQP circle1, VQP circle2) {
		boolean intersect = false;
		double l1 = circle1.getCoordinate().distance(circle2.getCoordinate());
		double l2 = Math.abs(circle1.getRadius() - circle2.getRadius());
		double l3 = circle1.getRadius() + circle2.getRadius();
		if (l2 < l1 && l1 < l3) {
			intersect = true;
			// System.out.println("intersect="+intersect);
		}
		return intersect;
	}

	public static boolean isCircumferenceCoverage(VQP circle,
			Set<VQP> Neighbor_set) {
		boolean coverage = true;
		Iterator<VQP> it1 = Neighbor_set.iterator();
		// if there is a intersecting point is not in any other circles, the
		// circle is not perimeter covered
		while (it1.hasNext() && coverage) {
			VQP circle1 = it1.next();
			if (circles_Insecter(circle, circle1)) {
				IntersectPoint inter1 = calculateIntersectPoint(circle, circle1);
				// System.out.println("inter1"+inter1.getIntersectPoint_left()+", "
				// +inter1.getIntersectPoint_right()+", "+inter1.getCirclePoint2());
				// "stop=true" means a intersecting point is inside a circle
				boolean stop1 = false;
				boolean stop2 = false;
				Iterator<VQP> it2 = Neighbor_set.iterator();
				// if the two intersecting points are in any other neighbor
				// circles,
				// then stop
				while (it2.hasNext()) {
					VQP circle2 = it2.next();
					// System.out.println("circle2="+circle2.getCoordinate());
					if (!pointsequal(circle1.getCoordinate(),
							circle2.getCoordinate())) {

						if (!stop1
								&& (isinCircle(inter1.getIntersectPoint_left(),
										circle2) || isAtCircumference(
										inter1.getIntersectPoint_left(),
										circle2))) {
							stop1 = true;
						}
						if (!stop2&& (isinCircle(inter1.getIntersectPoint_right(),circle2) 
								|| isAtCircumference(inter1.getIntersectPoint_right(),circle2))) {
							stop2 = true;
						}
					}
					// System.out.println("stop1="+stop1+"  stop2="+stop2);
				}
				// There is at least a point not inside any circle
				if (!stop1 || !stop2)
					coverage = false;
			}
		}
		System.out.println("isCircumferenceCoverage=" + coverage);
		return coverage;
	}

	// determine whether a point is in a circle or not
	public static boolean isinCircle(Coordinate p, VQP vqp) {
		boolean flag = false;
		if (vqp.getCoordinate().distance(p) < vqp.getRadius()) {
			flag = true;
			// System.out.println("isinCircle="+flag);
		}
		return flag;
	}

	// To determine whether point p1 equals to point p2
	public static boolean pointsequal(Coordinate p1, Coordinate p2) {
		boolean equal = false;
		if (Math.abs(p1.x - p2.x) < 1e-6 && Math.abs(p1.y - p2.y) < 1e-6)
			equal = true;
		return equal;
	}

	// determine whether a point is at the circumference of a circle
	public static boolean isAtCircumference(Coordinate p, VQP circle) {
		boolean atCircumference = false;
		if (Math.abs(circle.getCoordinate().distance(p) - circle.getRadius()) < 1e-6) {
			atCircumference = true;
			// System.out.println("atCircumference=" + atCircumference);
		}
		return atCircumference;
	}

	/* calculate the intersecting points of two circle */
	public static IntersectPoint calculateIntersectPoint(VQP circle1,
			VQP circle2) {

		Coordinate p1 = circle1.getCoordinate();
		double r1 = circle1.getRadius();
		Coordinate p2 = circle2.getCoordinate();
		double r2 = circle2.getRadius();
		double L = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y));
		double AE = (r1 * r1 - r2 * r2 + L * L) / (2 * L);
		double CE = Math.sqrt(r1 * r1 - AE * AE);
		double Xc = 0;
		double Yc = 0;
		double Xd = 0;
		double Yd = 0;
		if (p1.y == p2.y) {
			double x0 = p1.x + ((p2.x - p1.x) * AE) / L;
			double y0 = p1.y;
			Xc = x0;
			Xd = x0;
			Yc = y0 + CE;
			Yd = y0 - CE;
		} else if (p1.x == p2.x) {
			double x0 = p1.x;
			double y0 = p1.y + ((p2.y - p1.y) * AE) / L;
			Yc = y0;
			Yd = y0;
			Xc = x0 + CE;
			Xd = x0 - CE;
		} else {
			double k1 = (p1.y - p2.y) / (p1.x - p2.x);
			double k2 = -1 / k1;
			double x0 = p1.x + ((p2.x - p1.x) * AE) / L;
			double y0 = p1.y + k1 * (x0 - p1.x);
			double R2 = r1 * r1 - (x0 - p1.x) * (x0 - p1.x) - (y0 - p1.y)
					* (y0 - p1.y);
			double EF = Math.sqrt(R2 / (1 + k2 * k2));
			Xc = x0 - EF;
			Yc = y0 + k2 * (Xc - x0);
			Xd = x0 + EF;
			Yd = y0 + k2 * (Xd - x0);
		}
		IntersectPoint intersect = new IntersectPoint();
		if (Math.abs(Xc - Xd) < 1e-6 && Math.abs(Yc - Yd) < 1e-6) {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1, null);
		} else {
			Coordinate intersectP1 = new Coordinate(Xc, Yc);
			Coordinate intersectP2 = new Coordinate(Xd, Yd);
			intersect = new IntersectPoint(p1, r1, p2, r2, intersectP1,
					intersectP2);
		}
		// System.out.println("distancce="+circle1.getCoordinate().distance(intersect.getIntersectPoint_left()));
		return intersect;

	}

	public static void main(String[] args) {
		Coordinate p = new Coordinate();
		p.x = 0;
		p.y = 0;
		double r = 1;
		VQP circle = new VQP(p, r);
		Coordinate p1 = new Coordinate();
		p1.x = 1;
		p1.y = 1;
		double r1 = 1.0;
		VQP circle1 = new VQP(p1, r1);
		Coordinate p2 = new Coordinate();
		p2.x = 1;
		p2.y = -1;
		double r2 = 1.5;
		VQP circle2 = new VQP(p2, r2);
		Coordinate p3 = new Coordinate();
		p3.x = -1;
		p3.y = 1;
		double r3 = 1.5;
		VQP circle3 = new VQP(p3, r3);
		Coordinate p4 = new Coordinate();
		p4.x = -1;
		p4.y = -1;
		double r4 = 0.8;
		VQP circle4 = new VQP(p4, r4);
		Coordinate p5 = new Coordinate();
		p5.x = 0.1;
		p5.y = 0.1;
		double r5 = 0.8;
		VQP circle5 = new VQP(p5, r5);
		Set<VQP> Neighborset = new HashSet<VQP>();
		Neighborset.add(circle1);
		Neighborset.add(circle2);
		Neighborset.add(circle3);
		Neighborset.add(circle4);
		Neighborset.add(circle5);
		// boolean isAtCircumference = isCircumferenceCoverage(circle,
		// Neighborset);
		// System.out.println("isCircumferenceCoverage=" + isCircumference);
		// boolean flag=isPerimeterCoverage(circle, circle1, Neighborset);
		// System.out.println("flag="+flag);
		Iterator<VQP> it = Neighborset.iterator();
		while (it.hasNext()) {
			VQP circlet = it.next();
			System.out.println("====" + circlet.getCoordinate());
		}

		boolean flag = needQuery(circle, Neighborset);
		System.out.println("flag=" + flag);

	}
}
