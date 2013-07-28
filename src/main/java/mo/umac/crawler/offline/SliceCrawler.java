package mo.umac.crawler.offline;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mo.umac.crawler.CrawlerStrategy;
import mo.umac.metadata.APOI;
import mo.umac.paint.PaintShapes;
import mo.umac.spatial.Circle;
import mo.umac.spatial.GeoOperator;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;

public class SliceCrawler extends OfflineStrategy {

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
	    Envelope envelopeStateECEF) {
	logger.debug("------------crawling---------");
	logger.debug(envelopeStateECEF.toString());

	// finished crawling
	if (envelopeStateECEF == null) {
	    return;
	}

	if (covered(envelopeStateECEF)) {
	    logger.debug("This region has been covered");
	    return;
	}

	// first find the middle line, and then use the 1 dimensional method to
	// issue queries on this line.
	LineSegment middleLine = middleLine(envelopeStateECEF);
	//
	logger.debug("middleLine = " + middleLine.toString());
	// PaintShapes.paint.addLine(middleLine);
	// PaintShapes.paint.myRepaint();
	//
	ResultSetOneDimensional oneDimensionalResultSet = OneDimensionalCrawler
		.oneDimCrawl(state, category, query, middleLine);
	oneDimensionalResultSet.setLine(middleLine);

	// The one dimensional crawler maybe already cover this region
	if (covered(oneDimensionalResultSet, envelopeStateECEF)) {
	    logger.debug("this envelope is covered by the one dimensional crawler");
	    logger.debug(envelopeStateECEF.toString());
	    CrawlerStrategy.rtreeRectangles.addRectangle(rectangleId++,
		    envelopeStateECEF);
	    //
	    PaintShapes.paint.color = PaintShapes.paint.blueTranslucence;
	    PaintShapes.paint.addRectangle(envelopeStateECEF);
	    PaintShapes.paint.myRepaint();
	    //
	    return;
	}

	// print
	logger.debug("before sorting the circles: ");
	for (int i = 0; i < oneDimensionalResultSet.getCircles().size(); i++) {
	    Circle circle = oneDimensionalResultSet.getCircles().get(i);
	    logger.debug(circle.getCenter().toString());
	}

	// sort all circles in the middle line
	Collections.sort(oneDimensionalResultSet.getCircles(),
		new CircleComparable());

	// print sorting results
	logger.debug("After sorting the circles: ");
	for (int i = 0; i < oneDimensionalResultSet.getCircles().size(); i++) {
	    Circle circle = oneDimensionalResultSet.getCircles().get(i);
	    logger.debug(circle.getCenter().toString());
	}

	// left
	Coordinate leftNearestCoordinates = nearestLeftCoordinates(
		envelopeStateECEF, middleLine, oneDimensionalResultSet);
	LineSegment leftBoarderLine = null;
	if (leftNearestCoordinates != null) {
	    logger.debug("leftNearestCoordinates = "
		    + leftNearestCoordinates.toString());
	    leftBoarderLine = GeoOperator.parallel(middleLine,
		    leftNearestCoordinates);
	    logger.debug("leftBoarderLine = " + leftBoarderLine.toString());
	    //
	    PaintShapes.paint.color = PaintShapes.paint.blackTranslucence;
	    PaintShapes.paint.addLine(leftBoarderLine);
	    PaintShapes.paint.myRepaint();
	    //
	    fillGaps(state, category, query, middleLine, leftBoarderLine,
		    oneDimensionalResultSet);

	    Envelope leftRemainedEnvelope = leftRemainedRegion(
		    envelopeStateECEF, leftBoarderLine);
	    if (leftRemainedEnvelope != null) {
		logger.debug("leftRemainedEnvelope = "
			+ leftRemainedEnvelope.toString());
		crawl(state, category, query, leftRemainedEnvelope);
	    }
	}
	// right
	Coordinate rightNearestCoordinates = nearestRightCoordinates(
		envelopeStateECEF, middleLine, oneDimensionalResultSet);
	LineSegment rightBoarderLine = null;
	if (rightNearestCoordinates != null) {
	    logger.debug("rightNearestCoordinates = "
		    + rightNearestCoordinates.toString());
	    rightBoarderLine = GeoOperator.parallel(middleLine,
		    rightNearestCoordinates);
	    logger.debug("rightBoarderLine = " + rightBoarderLine.toString());
	    fillGaps(state, category, query, middleLine, rightBoarderLine,
		    oneDimensionalResultSet);
	    //
	    PaintShapes.paint.color = PaintShapes.paint.blackTranslucence;
	    PaintShapes.paint.addLine(rightBoarderLine);
	    PaintShapes.paint.myRepaint();
	    //
	    Envelope rightRemainedEnvelope = rightRemainedRegion(
		    envelopeStateECEF, rightBoarderLine);
	    if (rightRemainedEnvelope != null) {
		logger.debug("rightRemainedEnvelope = "
			+ rightRemainedEnvelope.toString());
		crawl(state, category, query, rightRemainedEnvelope);
	    }
	}

    }

    /**
     * The one dimensional crawler maybe already cover this region
     * 
     * @param oneDimensionalResultSet
     * @param envelopeStateECEF
     * @return
     */
    private boolean covered(ResultSetOneDimensional oneDimensionalResultSet,
	    Envelope envelopeStateECEF) {
	// XXX Not exactly.
	List<Circle> circles = oneDimensionalResultSet.getCircles();
	// this envelope is covered by one query circle
	if (circles.size() == 1) {
	    Circle circle = circles.get(0);
	    double rCircle = circle.getRadius();
	    double rEnvelope = envelopeStateECEF.centre().distance(
		    new Coordinate(envelopeStateECEF.getMinX(),
			    envelopeStateECEF.getMinY()));
	    if (rCircle > rEnvelope) {
		return true;
	    }
	}
	return false;
    }

    private boolean covered(Envelope envelopeStateECEF) {
	// FIXME check
	// find from the big rectangle(minY, maxY = envelopeStateECEF's minY,
	// maxY )
	return CrawlerStrategy.rtreeRectangles.contains(envelopeStateECEF);
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
	    ResultSetOneDimensional oneDimensionalResultSet) {
	logger.debug("...............fillGaps................");
	// All of these circles are sorted in the line.
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
	double y1 = 0, y2 = 0;
	double yNewBegin = 0, yNewEnd = 0;
	double yLastEnd = yBegin;
	boolean firstCircle = true;
	List<Circle> circles = oneDimensionalResultSet.getCircles();
	for (int i = 0; i < circles.size(); i++) {
	    logger.debug("yLastEnd = " + yLastEnd);
	    Circle circle = circles.get(i);
	    List<Coordinate> list = GeoOperator.intersect(circle, boardLine);
	    logger.debug("intersection of circle: "
		    + circle.getCenter().toString() + ", " + circle.getRadius()
		    + " with line: " + boardLine.toString() + " is: ");
	    for (int j = 0; j < list.size(); j++) {
		logger.debug(list.get(j).toString());
	    }
	    if (list.size() == 0) {
		logger.debug("Didn't intersect with the circle");
		// already covered

	    }
	    if (list.size() == 1) {
		y1 = list.get(0).y;
		logger.debug("Only one intersect point");
		logger.debug("y1 = " + y1);
		// whether tangent?
		if (Math.abs(xBoardLine - y1) < CrawlerStrategy.EPSILON) {
		    logger.debug("tangent");
		    // yes
		    yNewBegin = y1;
		    yNewEnd = y1;
		} else {
		    // not tangent
		    logger.debug("not tangent");
		    if (y1 < yEnd && !firstCircle) {// the last circle
			yNewBegin = y1;
			yNewEnd = yEnd + 1;
		    }
		    if (y1 > yBegin && firstCircle) {// the first circle
			yNewBegin = yBegin - 1;
			yNewEnd = y1;
			firstCircle = false;
		    }
		}
	    }
	    //
	    if (list.size() == 2) {
		logger.debug("two intersect points");
		yNewBegin = list.get(0).y;
		yNewEnd = list.get(1).y;
		logger.debug("y1 = " + list.get(0).y);
		logger.debug("y2 = " + list.get(1).y);
	    }

	    logger.debug("yNewBegin = " + yNewBegin);
	    logger.debug("yNewEnd = " + yNewEnd);

	    if (yNewBegin > yLastEnd) {
		// not covered
		Envelope smallEnvelope = new Envelope(xMiddleLine, xBoardLine,
			yLastEnd, yNewBegin);
		logger.debug("smallEnvelope = " + smallEnvelope.toString());
		PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
		PaintShapes.paint.addRectangle(smallEnvelope);
		PaintShapes.paint.myRepaint();
		crawl(state, category, query, smallEnvelope);
	    }
	    yLastEnd = yNewEnd;
	}
	// the last one
	if (yLastEnd < yEnd) {
	    Envelope smallEnvelope = new Envelope(xMiddleLine, xBoardLine,
		    yLastEnd, yEnd);
	    logger.debug("smallEnvelope = " + smallEnvelope.toString());
	    PaintShapes.paint.color = PaintShapes.paint.redTranslucence;
	    PaintShapes.paint.addRectangle(smallEnvelope);
	    PaintShapes.paint.myRepaint();
	    crawl(state, category, query, smallEnvelope);
	}
    }

    /**
     * Find the remained envelope need be crawled later
     * 
     * @param envelopeState
     * @param leftRightNearestEnvelope
     * @return
     */
    private Envelope leftRemainedRegion(Envelope envelopeState,
	    LineSegment leftBoraderLine) {
	double minX = envelopeState.getMinX();
	double y1 = envelopeState.getMinY();
	double y2 = envelopeState.getMaxY();
	//
	logger.debug("--------------leftRemainedRegion");
	logger.debug("envelopeState = " + envelopeState);
	if (leftBoraderLine == null) {
	    logger.debug("leftBoraderLine = null");
	}
	//
	Envelope leftRemainedEnvelope;
	double maxXLeft = leftBoraderLine.p0.x;
	if (maxXLeft > minX) {
	    leftRemainedEnvelope = new Envelope(minX, maxXLeft, y1, y2);
	} else {
	    leftRemainedEnvelope = null;
	}

	return leftRemainedEnvelope;
    }

    private Envelope rightRemainedRegion(Envelope envelopeState,
	    LineSegment rightBoraderLine) {
	double maxX = envelopeState.getMaxX();
	double y1 = envelopeState.getMinY();
	double y2 = envelopeState.getMaxY();

	logger.debug("--------------rightRemainedRegion");
	logger.debug("envelopeState = " + envelopeState);
	if (rightBoraderLine == null) {
	    logger.debug("rightBoraderLine = null");
	}
	Envelope rightRemainedEnvelope;
	double minXRight = rightBoraderLine.p0.x;
	if (minXRight < maxX) {
	    rightRemainedEnvelope = new Envelope(minXRight, maxX, y1, y2);
	} else {
	    rightRemainedEnvelope = null;
	}

	return rightRemainedEnvelope;
    }

    /**
     * Find the nearest left and right Coordinates to the middle line. But not
     * in the middle line
     * 
     * @param envelopeState
     * @param middleLine
     * @param oneDimensionalResultSet
     * @return the left & the right nearest point
     */
    private Coordinate nearestLeftCoordinates(Envelope envelopeState,
	    LineSegment middleLine,
	    ResultSetOneDimensional oneDimensionalResultSet) {
	double minXBoundary = envelopeState.getMinX();
	Coordinate leftNearest = null;
	List<APOI> leftPOIs = oneDimensionalResultSet.getLeftPOIs();
	if (leftPOIs.size() > 0) {
	    double bigX = minXBoundary;// leftPOIs.get(0).getCoordinate().x - 1;
	    for (int i = 0; i < leftPOIs.size(); i++) {
		APOI point = leftPOIs.get(i);
		double x = point.getCoordinate().x;
		if (x > bigX) {
		    leftNearest = point.getCoordinate();
		    bigX = x;
		}
	    }
	}

	return leftNearest;
    }

    private Coordinate nearestRightCoordinates(Envelope envelopeState,
	    LineSegment middleLine,
	    ResultSetOneDimensional oneDimensionalResultSet) {
	double maxXBoundary = envelopeState.getMaxX();
	List<APOI> rightPOIs = oneDimensionalResultSet.getRightPOIs();
	Coordinate rightNearest = null;
	if (rightPOIs.size() > 0) {
	    double smallX = maxXBoundary;// rightPOIs.get(0).getCoordinate().x +
					 // 1;
	    for (int i = 0; i < rightPOIs.size(); i++) {
		APOI point = rightPOIs.get(i);
		double x = point.getCoordinate().x;
		if (x < smallX) {
		    rightNearest = point.getCoordinate();
		    smallX = x;
		}
	    }
	}
	return rightNearest;
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
	double x = (x0 + x1) / 2;
	double y0 = envelopeState.getMinY();
	double y1 = envelopeState.getMaxY();

	LineSegment middleLine = new LineSegment(x, y0, x, y1);
	return middleLine;
    }
}
