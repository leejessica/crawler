package mo.umac.crawler.offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mo.umac.crawler.YahooLocalCrawlerStrategy;
import mo.umac.parser.POI;
import mo.umac.spatial.Circle;
import mo.umac.spatial.GeoOperator;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;

public class SliceCrawler extends OfflineYahooLocalCrawlerStrategy {

    public static Logger logger = Logger
	    .getLogger(SliceCrawler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see
     * mo.umac.crawler.OfflineYahooLocalCrawlerStrategy#crawl(java.lang.String,
     * int, java.lang.String, com.vividsolutions.jts.geom.Envelope)
     * 
     * This is the implementation of the upper bound algorithm.
     */
    @Override
    public void crawl(String state, int category, String query,
	    Envelope envelopeState) {
	logger.debug(envelopeState.toString());
	
	// first find the middle line, and then use the 1 dimensional method to
	// issue queries on this line.
	LineSegment middleLine = middleLine(envelopeState);
	logger.debug(middleLine.toString());
	
	OneDimensionalCrawler oneDimensionalCrawler = new OneDimensionalCrawler();
	OneDimensionalResultSet oneDimensionalResultSet = oneDimensionalCrawler
		.extendOneDimensional(state, category, query, middleLine);
	oneDimensionalResultSet.setLine(middleLine);

	// For all returned points, find the left and the right nearest point to
	// the middle line.
	List<POI> leftRightNearestPOIs = nearestPOIs(envelopeState, middleLine,
		oneDimensionalResultSet);
	List<LineSegment> leftRightBoarderLine = borarderLine(envelopeState,
		middleLine, leftRightNearestPOIs);
	List<Envelope> leftRightNearestEnvelope = nearestCoveredRegion(
		envelopeState, middleLine, leftRightNearestPOIs);

	// sort all circles in the middle line
	Collections.sort(oneDimensionalResultSet.getCircles(),
		new CircleComparable());

	fillGaps(state, category, query, middleLine,
		leftRightBoarderLine.get(0), oneDimensionalResultSet);
	fillGaps(state, category, query, middleLine,
		leftRightBoarderLine.get(1), oneDimensionalResultSet);

	List<Envelope> leftRightRemainedEnvelope = remainedRegion(
		envelopeState, leftRightNearestEnvelope);
	Envelope envelopeLeft = leftRightRemainedEnvelope.get(0);
	crawl(state, category, query, envelopeLeft);

	Envelope envelopeRight = leftRightRemainedEnvelope.get(1);
	crawl(state, category, query, envelopeRight);
    }

    /**
     * The board line of the should be covered smaller envelope
     * 
     * @param envelopeState
     * @param middleLine
     * @param leftRightNearestPOIs
     * @return
     */
    private List<LineSegment> borarderLine(Envelope envelopeState,
	    LineSegment middleLine, List<POI> leftRightNearestPOIs) {
	POI leftPoint = leftRightNearestPOIs.get(0);
	POI rightPoint = leftRightNearestPOIs.get(1);
	LineSegment leftLine = leftLine = GeoOperator.parallel(middleLine,
		leftPoint);
	LineSegment rightLine = rightLine = GeoOperator.parallel(middleLine,
		rightPoint);
	List<LineSegment> list = new ArrayList<LineSegment>();
	list.add(leftLine);
	list.add(rightLine);
	return list;
    }

    public class CircleComparable implements Comparator<Circle> {
	@Override
	public int compare(Circle circle1, Circle circle2) {
	    Coordinate center1 = circle1.getCenter();
	    Coordinate center2 = circle2.getCenter();
	    return center1.compareTo(center2);
	}
    }

    /**
     * find the gaps, and then cover the gaps
     * 
     * @param state
     *            TODO
     * @param category
     *            TODO
     * @param query
     *            TODO
     * @param middleLine
     * @param oneDimensionalResultSet
     * @param envelopeState
     * @param envelope
     */
    private void fillGaps(String state, int category, String query,
	    LineSegment middleLine, LineSegment boardLine,
	    OneDimensionalResultSet oneDimensionalResultSet) {
	// All of these circles are sorted in the line.
	if (middleLine.p0.x < middleLine.p1.x) {
	    double xMiddleLine = middleLine.p0.x;
	    double xBoardLine = boardLine.p0.x;
	    double yBegin;
	    double yEnd;
	    if (boardLine.p0.y < boardLine.p1.y) {
		yBegin = boardLine.p0.y;
		yEnd = boardLine.p1.y;
	    } else {
		yBegin = boardLine.p1.y;
		yEnd = boardLine.p0.y;
	    }
	    double yFirst = yBegin;
	    double ySecond = yBegin;
	    List<Circle> circles = oneDimensionalResultSet.getCircles();
	    for (int i = 0; i < circles.size(); i++) {
		Circle circle = circles.get(i);
		List<Coordinate> list = GeoOperator.intersectOnEarth(circle,
			boardLine);
		if (list == null) {
		    logger.error("Didn't intersect with the circle");
		}
		double y1 = list.get(0).y;
		if (y1 > yFirst) {
		    // not covered
		    Envelope smallEnvelope = new Envelope(xMiddleLine,
			    xBoardLine, yFirst, y1);
		    crawl(state, category, query, smallEnvelope);
		}
		//
		if (list.size() == 2) {
		    double y2 = list.get(1).y;
		    yFirst = y2;
		} else {
		    yFirst = y1;
		}
	    }
	}
    }

    /**
     * Find the remained envelope need be crawled later
     * 
     * @param envelopeState
     * @param leftRightNearestEnvelope
     * @return
     */
    private List<Envelope> remainedRegion(Envelope envelopeState,
	    List<Envelope> leftRightNearestEnvelope) {
	double minX = envelopeState.getMinX();
	double maxX = envelopeState.getMaxX();
	double y1 = envelopeState.getMinY();
	double y2 = envelopeState.getMaxY();

	List<Envelope> leftRightRemainedEnvelope = new ArrayList<Envelope>();
	Envelope leftNearestEnvelope = leftRightNearestEnvelope.get(0);
	double maxXLeft = leftNearestEnvelope.getMinX();
	Envelope leftRemainedEnvelope = new Envelope(minX, maxXLeft, y1, y2);

	Envelope rightNearestEnvelope = leftRightNearestEnvelope.get(1);
	double minXRight = rightNearestEnvelope.getMaxX();
	Envelope rightRemainedEnvelope = new Envelope(minXRight, maxX, y1, y2);

	leftRightRemainedEnvelope.add(leftRemainedEnvelope);
	leftRightRemainedEnvelope.add(rightRemainedEnvelope);

	return leftRightRemainedEnvelope;
    }

    /**
     * Compute the left region and the right region based on the left/right
     * nearest points.
     * 
     * @param envelopeState
     * @param middleLine
     * @param leftRightPOIs
     * @return
     */
    private List<Envelope> nearestCoveredRegion(Envelope envelopeState,
	    LineSegment middleLine, List<POI> leftRightNearestPOIs) {
	List<Envelope> leftRightNearestEnvelope = new ArrayList<Envelope>();
	double y1 = middleLine.p0.y;
	double y2 = middleLine.p1.y;
	// left
	POI left = leftRightNearestPOIs.get(0);
	double minX = left.getCoordinate().x;
	Envelope leftEnvelope;
	if (minX > envelopeState.getMinX()) {
	    leftEnvelope = new Envelope(minX, middleLine.p0.x, y1, y2);
	} else {
	    // no points in this left region
	    leftEnvelope = null;
	}
	// right
	POI right = leftRightNearestPOIs.get(1);
	double maxX = right.getCoordinate().x;
	Envelope rightEnvelope;
	if (maxX < envelopeState.getMaxX()) {
	    rightEnvelope = new Envelope(middleLine.p0.x, maxX, y1, y2);
	} else {
	    // no points in this left region
	    rightEnvelope = null;
	}

	leftRightNearestEnvelope.add(leftEnvelope);
	leftRightNearestEnvelope.add(rightEnvelope);
	return leftRightNearestEnvelope;
    }

    /**
     * Find the nearest left and right POIs to the middle line. But not in the
     * middle line
     * 
     * @param envelopeState
     * @param middleLine
     * @param oneDimensionalResultSet
     * @return the left & the right nearest point
     */
    private List<POI> nearestPOIs(Envelope envelopeState,
	    LineSegment middleLine,
	    OneDimensionalResultSet oneDimensionalResultSet) {
	List<POI> leftRight = new ArrayList<POI>();
	List<POI> leftPOIs = oneDimensionalResultSet.getLeftPOIs();
	// TODO can be optimized later
	double bigX = leftPOIs.get(0).getCoordinate().x;
	POI leftNearest = null;
	for (int i = 0; i < leftPOIs.size(); i++) {
	    POI point = leftPOIs.get(i);
	    double x = point.getCoordinate().x;
	    if (x > bigX && x < middleLine.p0.x) {
		leftNearest = point;
		bigX = x;
	    }
	}
	// right
	List<POI> rightPOIs = oneDimensionalResultSet.getRightPOIs();
	double smallX = rightPOIs.get(0).getCoordinate().x;
	POI rightNearest = null;
	for (int i = 0; i < rightPOIs.size(); i++) {
	    POI point = rightPOIs.get(i);
	    double x = point.getCoordinate().x;
	    if (x < smallX && x > middleLine.p0.x) {
		rightNearest = point;
		smallX = x;
	    }
	}
	//
	leftRight.add(leftNearest);
	leftRight.add(rightNearest);

	return leftRight;
    }

    /**
     * fine the middle line
     * 
     * @param envelopeState
     * @return the longitude of the middle line
     */
    private LineSegment middleLine(Envelope envelopeState) {
	double x0 = envelopeState.getMinX();
	double x1 = envelopeState.getMaxX();
	if (Math.abs(x0 - x1) > YahooLocalCrawlerStrategy.EPSILON) {
	    logger.error("envelopeState is not perpendicular: " + x0 + ", "
		    + x1);
	}
	double x = (x0 + x1) / 2;
	double y0 = envelopeState.getMinY();
	double y1 = envelopeState.getMaxY();

	LineSegment middleLine = new LineSegment(x, y0, x, y1);
	return middleLine;
    }
}
