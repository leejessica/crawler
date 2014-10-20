package mo.umac.crawler.offline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;

import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.VQP;
import mo.umac.metadata.NeighborPoint;
import mo.umac.metadata.IntersectPoint;
import mo.umac.paint.PaintShapes;
import mo.umac.spatial.Circle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class HexagonCrawler2_extension extends OfflineStrategy {

	public static int count = 0;
	public static double key = Math.sqrt(3);
	public static int countquery = 0;
	public static int level = 0;

	public HexagonCrawler2_extension() {
		super();
		logger.info("------------HexagonCrawler2------------");
	}

	@Override
	public void crawl(String state, int category, String query,
			Envelope evenlopeState) {

		if (logger.isDebugEnabled()) {
			logger.info("------------crawling-----------");
			logger.info(evenlopeState.toString());
		}
		// finished crawling
		if (evenlopeState == null) {
			return;
		}
		Coordinate startPoint = new Coordinate();
		startPoint.x = (evenlopeState.getMinX() + evenlopeState.getMaxX()) / 2;
		startPoint.y = (evenlopeState.getMinY() + evenlopeState.getMaxY()) / 2;
		// record all visited query points
		LinkedList<Coordinate> visited_Queue = new LinkedList<Coordinate>();
		LinkedList<Coordinate> unvisited_Queue = new LinkedList<Coordinate>();
		// record all known points'information
		LinkedList<VQP> visited_Queue_Info = new LinkedList<VQP>();

		// record the neighbor points of all the points had been processed
		ArrayList<NeighborPoint> neighbourArray = new ArrayList<NeighborPoint>();
		// issue the first query
		firstQuery(startPoint, state, category, query, visited_Queue,
				unvisited_Queue, visited_Queue_Info, neighbourArray);

	}

	public void calculatePoint(LinkedList<Coordinate> visited_Queue,
			LinkedList<Coordinate> unvisited_Queue, VQP[] neighbourQ,
			double radius, Coordinate startPoint, int level) {
		Coordinate[] d = new Coordinate[6];
		for (int i = 0; i < d.length; i++) {
			d[i] = new Coordinate();
		}
		d[0].x = startPoint.x;
		d[0].y = startPoint.y + key * radius;
		d[1].x = startPoint.x + Math.sqrt(3) * key * radius / 2;
		d[1].y = startPoint.y + key * radius / 2;
		d[2].x = startPoint.x + Math.sqrt(3) * key * radius / 2;
		d[2].y = startPoint.y - key * radius / 2;
		d[3].x = startPoint.x;
		d[3].y = startPoint.y - key * radius;
		d[4].x = startPoint.x - Math.sqrt(3) * key * radius / 2;
		d[4].y = startPoint.y - key * radius / 2;
		d[5].x = startPoint.x - Math.sqrt(3) * key * radius / 2;
		d[5].y = startPoint.y + key * radius / 2;
		for (int j = 0; j < 6; j++) {
			if (!myContain(visited_Queue, d[j])
					&& !myContain(unvisited_Queue, d[j])) {
				unvisited_Queue.addLast(d[j]);
			}
		}
		// record the neighbor points of the point to be processed
		for (int k = 0; k < 6; k++) {

			neighbourQ[k] = new VQP(d[k], 0, level + 1);
		}

	}

	private boolean myContain(LinkedList<Coordinate> q, Coordinate c) {
		for (int i = 0; i < q.size(); i++) {
			Coordinate one = q.get(i);
			if (Math.abs(one.x - c.x) < 0.000001
					&& Math.abs(one.y - c.y) < 0.000001) {
				return true;
			}
		}
		return false;
	}

	public void firstQuery(Coordinate startPoint, String state, int category,
			String query, LinkedList<Coordinate> visited_Queue,
			LinkedList<Coordinate> unvisited_Queue,
			LinkedList<VQP> visited_Queue_Info,
			ArrayList<NeighborPoint> neighbourArray) {
		/* issue first query */
		AQuery Firstquery = new AQuery(startPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED);
		ResultSet resultSetStart = query(Firstquery);
		countquery++;
		/* put the visited point to the queue */
		visited_Queue.addLast(startPoint);
		/* record all point queried */
		Set<APOI> queryset = new HashSet<APOI>();
		System.out.println("queryset.size=" + queryset.size());
		/* record all eligible point */
		Set<APOI> eligibleSet = new HashSet<APOI>();
		/* put all points gotten from querying into a set */
		queryset.addAll(resultSetStart.getPOIs());
		count = queryset.size(); // count the returned points
		System.out.println("first query " + count + " points");
		/* calculate the crawl radius */
		int size = resultSetStart.getPOIs().size();
		APOI farthest = resultSetStart.getPOIs().get(size - 1);
		Coordinate farthestCoordinate = farthest.getCoordinate();
		double distance = startPoint.distance(farthestCoordinate);
		/* paint the query circle */
		Circle circle = new Circle(startPoint, distance);
		if (logger.isDebugEnabled() && PaintShapes.painting) {
			PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(circle);
			PaintShapes.paint.myRepaint();
		}

		double radius = distance; // record the first crawl radius
		// System.out.println("radius=" + radius);

		// record the visited point's information
		visited_Queue_Info.addLast(new VQP(startPoint, radius, level));

		/* use neighbourQ to record the neighbor of point under processing */
		VQP[] neighbourQ = new VQP[6];

		/* all the new points at level 1 */
		calculatePoint(visited_Queue, unvisited_Queue, neighbourQ, radius,
				startPoint, level);

		/* add the startPoint and its neighbor to the neighbourArray */
		neighbourArray.add(new NeighborPoint(startPoint, neighbourQ));
		level++;

		// if the number of nodes got less than needed nodes, continue query
		while (count < NEED_POINTS) {
			System.out.println("level=\n" + level);
			extendQuery(state, category, query, visited_Queue, unvisited_Queue,
					queryset, visited_Queue_Info, neighbourArray, radius);
			// TODO update the information of the neighbor points
		}
	}

	/* The query point is the center of the hexagon */
	public void extendQuery(String state, int category, String query,
			LinkedList<Coordinate> visited_Queue,
			LinkedList<Coordinate> unvisited_Queue, Set<APOI> queryset,
			LinkedList<VQP> visited_Queue_Info,
			ArrayList<NeighborPoint> neighbourArray, double radius) {

		/* record the point whose query radius less than the hexagon's edge */
		LinkedList<VQP> lessradius = new LinkedList<VQP>();

		// start querying
		for (int i = 0; i < level * 6; i++) {
			if (!unvisited_Queue.isEmpty()) {
				Coordinate point = unvisited_Queue.removeFirst();
				/*
				 * get the point for the query of next round and all the new
				 * points gotten will at the next level of the current point
				 * which is processing; add point under processing and its
				 * neighbor to the neighbourArray
				 */
				VQP[] neighbourQ = new VQP[6];
				calculatePoint(visited_Queue, unvisited_Queue, neighbourQ,
						radius, point, level);
				neighbourArray.add(new NeighborPoint(point, neighbourQ));

				// issue query
				AQuery aquery = new AQuery(point, state, category, query,
						MAX_TOTAL_RESULTS_RETURNED);
				ResultSet resultSet = query(aquery);
				countquery++;

				// record points queried
				queryset.addAll(resultSet.getPOIs());
				int size1 = resultSet.getPOIs().size();
				APOI farthest1 = resultSet.getPOIs().get(size1 - 1);

				// record query radius
				double radius1 = point.distance(farthest1.getCoordinate());

				// paint circle
				Circle circle1 = new Circle(point, radius1);
				if (logger.isDebugEnabled() && PaintShapes.painting) {
					PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
					PaintShapes.paint.addCircle(circle1);
					PaintShapes.paint.myRepaint();
				}
				if (radius1 < radius) {
					lessradius.addLast(new VQP(point, radius1, level));
				}
				// record the information of the visited points
				visited_Queue_Info.addLast(new VQP(point, radius1, level));
			}
		}

		// update the neighbourArray
		updateNeighbor(neighbourArray, visited_Queue_Info);

		// query in the hexagon
		Iterator<VQP> iterator = lessradius.iterator();
		while (iterator.hasNext()) {
			VQP point1 = iterator.next();
			Iterator<NeighborPoint> iterator1 = neighbourArray.iterator();
			while (iterator1.hasNext()) {
				NeighborPoint point2 = iterator1.next();
				if (point2.getCenterPoint() == point1.getCoordinate()) {
					// TODO call the inhexagonQuery
					inhexagonQuery(state, category, query, point2,
							point1.getRadius(), queryset);
				}
			}
		}

		// while lessradius is not empty{
		// TODO inhexagon search
		// double tempradius=inhexgonQuery()
		// TODO update the information of the visited_Queue_Info}
		// TODO if count<need points, then continue query
		// TODO update the neighbourArray
		level++;
	}

	/*
	 * @param point: the processing point and its neighbor
	 * 
	 * @param radius1: the processing point's radius
	 */
	public double inhexagonQuery(String state, int category, String query,
			NeighborPoint point, double radius1, Set<APOI> queryset) {
		int level1 = 0;
		Coordinate centerPoint = point.getCenterPoint();
		/*
		 * visited_Queue1: record the visited points in the hexagon
		 * 
		 * unvisited_Queue1: record the unvisited_Queue1 points in the hexagon
		 * 
		 * neighborQ1:record the neighbor points of the given point in the
		 * hexagon
		 * 
		 * visited_Info_Queue1: record the information of the points which
		 * related to the in hexagon query
		 */

		// add the given point into the visited_Queue1
		LinkedList<Coordinate> visited_Queue1 = new LinkedList<Coordinate>();
		visited_Queue1.addLast(point.getCenterPoint());

		LinkedList<Coordinate> unvisited_Queue1 = new LinkedList<Coordinate>();

		LinkedList<VQP> visited_Info_Queue1 = new LinkedList<VQP>();
		visited_Info_Queue1.addLast(new VQP(centerPoint, radius1, level1));

		// calculate the neighbor points in hexagon
		VQP[] neighborQ1 = new VQP[6];
		calculatePoint(visited_Queue1, unvisited_Queue1, neighborQ1, radius1,
				point.getCenterPoint(), level1);
		// record all the intersect information for inhexagonquery
		LinkedList<IntersectPoint> intersectQ1 = new LinkedList<IntersectPoint>();
		/*
		 * for (int i = 0; i < 6; i++) { Coordinate p1 =
		 * neighborQ1[i].getCoordinate(); for (int j = 0; j < 6; j++) {
		 * Coordinate p2 = point.getNeighborQ()[j].getCoordinate(); double
		 * radius2 = point.getNeighborQ()[j].getRadius(); /* if in this
		 * direction has visited and no intersection, then update the local
		 * neighbor information
		 */
		/*
		 * if (isCollinear(p1, p2, centerPoint) && isIntersection(centerPoint,
		 * radius2, p2, radius2)) { // first update the local neighbor
		 * information neighborQ1[i].setCoordinate(p2);
		 * neighborQ1[i].setRadius(radius2); /* calculate and record the
		 * intersect points between centerPoint and neighbor point respectively
		 */
		/*
		 * IntersectPoint intersect1 = calculateIntersectPoint( centerPoint,
		 * radius1, p2, radius2); intersectQ1.addLast(intersect1); } } }
		 */

		// start issuing query in hexagon
		for (int i = 0; i < 6; i++) {

			if (neighborQ1[i].getRadius() == 0) {// the neighbor hasn't been
													// visited
				// issue query
				Coordinate p3 = neighborQ1[i].getCoordinate();
				AQuery inhexagonquery = new AQuery(p3, state, category, query,
						MAX_TOTAL_RESULTS_RETURNED);
				ResultSet inhexagonResult = query(inhexagonquery);
				countquery++;
				queryset.addAll(inhexagonResult.getPOIs());
				// update its radius
				int size2 = inhexagonResult.getPOIs().size();
				double radius3 = p3.distance(inhexagonResult.getPOIs()
						.get(size2 - 1).getCoordinate());
				neighborQ1[i].setRadius(radius3);
				// add it to the visited_Queue1
				visited_Queue1.addLast(p3);
			}
		}

		// TODO find gap and fill it up

		/*
		 * determine whether the center circle is intersect with the neighbor
		 * circle
		 */

		//
		double temp_radius = 0;
		return temp_radius;

	}

	/* update the information of the neighborArray using visited_Queue_Info */
	public void updateNeighbor(ArrayList<NeighborPoint> neighbourArray,
			LinkedList<VQP> visited_Queue_Info) {

		Iterator<NeighborPoint> iterator1 = neighbourArray.iterator();
		while (iterator1.hasNext()) {
			NeighborPoint p1 = iterator1.next();

			/*
			 * search the visited_Queue_Info to check if the processing point is
			 * in the visited_Queue_Info, if yes, then update the neighbourArray
			 */

			Iterator<VQP> iterator2 = visited_Queue_Info.iterator();
			while (iterator2.hasNext()) {
				Coordinate p2 = iterator2.next().getCoordinate();
				if (p1.getCenterPoint().x == p2.x
						&& p1.getCenterPoint().y == p2.y) {
					VQP[] neighbor1 = p1.getNeighborQ();
					for (int i = 0; i < 6; i++) {
						Iterator<VQP> iterator3 = visited_Queue_Info.iterator();
						while (iterator3.hasNext()) {
							VQP p3 = iterator3.next();
							if (neighbor1[i].getLatitude() == p3.getLatitude()
									&& neighbor1[i].getLongtitude() == p3
											.getLongtitude()) {
								neighbor1[i].setRadius(p3.getRadius());
							}
						}
					}
				}
			}
		}
	}

	// TODO judge whether the query need or not
	public Boolean needQuery1(Coordinate point, double radius,
			LinkedList<VQP> visited_Queue_Info1) {
		LinkedList<VQP> neighborList = new LinkedList<VQP>();
		/*
		 * set1:record all the points which come from the circle intersect with
		 * each other in the neighborList as well as contained by the given
		 * circle;
		 * 
		 * set2: record all the points which come from the neighbor circle
		 * intersect with the given circle
		 */
		Set<Coordinate> set1 = new HashSet<Coordinate>();
		Set<Coordinate> set2 = new HashSet<Coordinate>();

		// get the neighbor information of the given point
		Iterator<VQP> it = visited_Queue_Info1.iterator();
		while (it.hasNext()) {
			VQP neighbor = it.next();
			Coordinate p1 = neighbor.getCoordinate();
			double r1 = neighbor.getRadius();
			// the given circle is contained by the neighbor
			if (isinclude(p1, r1, point, radius)
					|| isinscribe(p1, r1, point, radius))
				return false;

			else // intersect or inscribe or include the neighbor
			if (isIntersection(point, radius, p1, r1))
				neighborList.addFirst(neighbor);

			else if (isinscribe(point, radius, p1, r1)
					|| isinclude(point, radius, p1, r1))
				neighborList.addLast(neighbor);
		}
		// if has no neighbor, then need to be queried
		if (neighborList.size() == 0)
			return true;
		else {
			// update the set1
			while (neighborList.size() > 1) {
				VQP p3 = neighborList.removeFirst();
				Iterator<VQP> it1 = neighborList.iterator();
				while (it1.hasNext()) {
					VQP p4 = it1.next();
					if (isIntersection(p3.getCoordinate(), p3.getRadius(),
							p4.getCoordinate(), p4.getRadius())) {
						IntersectPoint intersectP1 = calculateIntersectPoint(
								p3.getCoordinate(), p3.getRadius(),
								p4.getCoordinate(), p4.getRadius());
						if (intersectP1.getIntersectPoint1().distance(point) < radius) {
							set1.add(intersectP1.getCirclePoint1());
						}
						if (intersectP1.getCirclePoint2().distance(point) < radius) {
							set1.add(intersectP1.getIntersectPoint2());
						}
					}
				}
			}

			// case 1
			if (set1.size() == 0 && set2.size() == 0) {

			}
			// case 2
			else if (set1.size() == 0 && set2.size() != 0) {

			}
			// case 3
			else if (set1.size() != 0 && set2.size() != 0) {

			}
		}

	}

	/* To judge whether the 3 points are in the same straight line */
	public boolean isCollinear(Coordinate p1, Coordinate p2,
			Coordinate centerPoint) {
		double result = (p1.y - centerPoint.y) * (p2.x - centerPoint.x)
				- (p2.y - centerPoint.y) * (p1.x - centerPoint.x);
		if (Math.abs(result - 0) < 1e-6)
			return true;
		else
			return false;
	}

	/* judge the 2 circle whether is intersect or not */
	public boolean isIntersection(Coordinate p1, double r1, Coordinate p2,
			double r2) {
		double centerDistance = p1.distance(p2);
		double sumRadius = r1 + r2;
		double diffRadius = Math.abs(r1 - r2);

		// intersect
		if (centerDistance > diffRadius && centerDistance < sumRadius)
			return true;
		else
			return false;
	}

	// innitial:r1>r2
	public boolean isinclude(Coordinate p1, double r1, Coordinate p2, double r2) {
		double distance = p1.distance(p2);
		double diffRadius = r1 - r2;
		if (distance < diffRadius) {
			return true;
		} else
			return false;
	}

	// innitial:r1>r2
	public boolean isinscribe(Coordinate p1, double r1, Coordinate p2, double r2) {
		double distance = p1.distance(p2);
		double diffRadius = r1 - r2;
		if (distance - diffRadius < 1e-6) {
			return true;
		} else
			return false;
	}

	// TODO calculate the intersect points
	// TODO judge the number of the intersect point
	public IntersectPoint calculateIntersectPoint(Coordinate p1, double r1,
			Coordinate p2, double r2) {

		double L = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y));
		double k1 = (p1.y - p2.y) / (p1.x - p2.x);
		double k2 = -1 / k1;
		double AE = (r1 * r1 - r2 * r2 + L * L) / 2 * L;
		double x0 = p1.x + ((p2.x - p1.x) * AE) / L;
		double y0 = p1.y + k1 * (x0 - p1.x);
		double R2 = r1 * r1 - (x0 - p1.x) * (x0 - p1.x) - (y0 - p1.y)
				* (y0 - p1.y);
		double EF = Math.sqrt(R2 / (1 + k2 * k2));
		double Xc = x0 - EF;
		double Yc = y0 + k2 * (Xc - x0);
		double Xd = x0 + EF;
		double Yd = y0 + k2 * (Xd - x0);
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
		return intersect;
	}

	/* calculate the incircle of the query area */
	public double calculateIncircle(Coordinate startPoint, Coordinate point1,
			double radius1, Coordinate point2, double radius2) {
		double AB = startPoint.distance(point1);
		double AD = startPoint.distance(point2);
		double BD = point1.distance(point2);
		double cosBCD = (Math.pow(radius1, 2) + Math.pow(radius2, 2) - Math
				.pow(BD, 2)) / (2 * radius1 * radius2);
		double angleBCD = Math.acos(cosBCD);
		double cosBAD = (Math.pow(AB, 2) + Math.pow(AD, 2) - Math.pow(BD, 2))
				/ (2 * AB * AD);
		double angleBAD = Math.acos(cosBAD);
		/*
		 * using cosine rule to calculate AC
		 */
		double a1 = Math.pow(AB, 2) + Math.pow(radius1, 2) - Math.pow(AD, 2)
				- Math.pow(radius2, 2);
		double a2 = 2 * AB * radius1;
		double a3 = 2 * AD * radius2
				* Math.cos(2 * Math.PI - angleBCD - angleBAD);
		double a4 = 2 * AD * radius2
				* Math.sin(2 * Math.PI - angleBCD - angleBAD);
		double b1 = Math.pow(a4, 2) - Math.pow(a1, 2);
		double b2 = Math.pow((a2 - a3), 2) + Math.pow(a4, 2);
		double b3 = 2 * a1 * (a2 - a3);

		double X1 = (b3 + Math.sqrt(Math.pow(b3, 2) + 4 * b2 * b1)) / (2 * b2);
		double X2 = (b3 - Math.sqrt(Math.pow(b3, 2) + 4 * b2 * b1)) / (2 * b2);
		double AC1 = Math.sqrt(Math.pow(AB, 2) + Math.pow(radius1, 2) - 2 * AB
				* radius1 * X1);
		double AC2 = Math.sqrt(Math.pow(AB, 2) + Math.pow(radius1, 2) - 2 * AB
				* radius1 * X2);
		double AC = Math.max(AC1, AC2);
		return AC;
	}
}
