/**
 * 
 */
package mo.umac.crawler.offline;

import java.util.List;

import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSet;
import mo.umac.paint.PaintShapes;
import mo.umac.spatial.Circle;
import mo.umac.spatial.GeoOperator;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geomgraph.Position;

/**
 * The one dimensional crawler.
 * 
 * @author Kate
 * 
 */
public class OneDimensionalCrawler extends OfflineStrategy {

    public static Logger logger = Logger.getLogger(OneDimensionalCrawler.class
	    .getName());

    /**
     * Begin at the center of the line
     * 
     * 
     * @param state
     * @param category
     * @param query
     * @param envelopeState
     * @param middleLine
     * @return
     */
    public static ResultSetOneDimensional oneDimCrawl(String state,
	    int category, String query, LineSegment middleLine) {
	ResultSetOneDimensional finalResultSet = new ResultSetOneDimensional();
	Coordinate up = middleLine.p0;
	Coordinate down = middleLine.p1;
	Coordinate center = middleLine.midPoint();

	logger.debug("up = " + up.toString());
	logger.debug("down = " + down.toString());
	logger.debug("center = " + center.toString());

	AQuery aQuery = new AQuery(center, state, category, query,
		MAX_TOTAL_RESULTS_RETURNED);
	ResultSet resultSet = query(aQuery);
	logger.debug("resultSet.getPOIs().size() = "
		+ resultSet.getPOIs().size());
	List<APOI> resultPoints = resultSet.getPOIs();
	for (int i = 0; i < resultPoints.size(); i++) {
	    APOI aPoint = resultPoints.get(i);
	    logger.debug("APoint: " + aPoint.getId() + ", [" + aPoint.getCoordinate().toString() + "]");
		    
	}
	
	Coordinate farthestCoordinate = farthest(resultSet);
	logger.debug("farthestCoordinate = " + farthestCoordinate.toString());
	double radius = center.distance(farthestCoordinate);
	logger.debug("radius = " + radius);

	Circle aCircle = new Circle(center, radius);
	resultSet.addACircle(aCircle);
	//
	PaintShapes.paint.addCircle(aCircle);
	PaintShapes.paint.myRepaint();

	addResults(center, middleLine, finalResultSet, resultSet);

	logger.debug("middleLine.getLength() / 2 = " + middleLine.getLength()
		/ 2);
	if (radius >= middleLine.getLength() / 2) {
	    // finished crawling
	    logger.debug("finished crawling");
	    return finalResultSet;
	}

	// recursively crawl
	// upper
	// Coordinate newRight = middleLine.pointAlongOffset(0.5, -radius);
	Coordinate newDown = newDown(center, radius);
	logger.debug("newDown: " + newDown.toString());
	//
	LineSegment upperLine = new LineSegment(up, newDown);
	logger.debug("upperLine: " + upperLine.toString());
	ResultSetOneDimensional newLeftResultSet = oneDimCrawl(state, category,
		query, upperLine);
	addResults(finalResultSet, newLeftResultSet);
	// lower
	// Coordinate newLeft = middleLine.pointAlongOffset(0.5, radius);
	Coordinate newUp = newUp(center, radius);
	logger.debug("newUp: " + newUp.toString());
	LineSegment lowerLine = new LineSegment(newUp, down);
	logger.debug("lowerLine: " + lowerLine.toString());
	ResultSetOneDimensional newRightResultSet = oneDimCrawl(state,
		category, query, lowerLine);
	addResults(finalResultSet, newRightResultSet);

	return finalResultSet;
    }

    /**
     * This line is perpendicular, so it has the same x as center.x
     * 
     * @param center
     * @param radius
     * @return
     */
    private static Coordinate newDown(Coordinate center, double radius) {
	Coordinate newDown = new Coordinate(center.x, center.y - radius);
	return newDown;
    }

    private static Coordinate newUp(Coordinate center, double radius) {
	Coordinate newDown = new Coordinate(center.x, center.y + radius);
	return newDown;
    }

    private static Coordinate farthest(ResultSet resultSet) {
	Coordinate farthestCoordinate;
	int size = resultSet.getPOIs().size();
	// farthest
	if (size == 0) {
	    return null;
	} else {
	    APOI farthestPOI = resultSet.getPOIs().get(size - 1);
	    farthestCoordinate = farthestPOI.getCoordinate();
	}
	return farthestCoordinate;
    }

    private static void addResults(Coordinate center, LineSegment line,
	    ResultSetOneDimensional finalResultSet, ResultSet resultSet) {
	List<APOI> pois = resultSet.getPOIs();
	for (int i = 0; i < pois.size(); i++) {
	    APOI poi = pois.get(i);
	    int position = GeoOperator.findPosition(line, poi.getCoordinate());
	    switch (position) {
	    case Position.LEFT:
		finalResultSet.getLeftPOIs().add(poi);
		break;
	    case Position.RIGHT:
		finalResultSet.getRightPOIs().add(poi);
		break;
	    case Position.ON:
		finalResultSet.getOnPOIs().add(poi);
		break;
	    }
	}
	// TODO check
	finalResultSet.addAll(finalResultSet.getCircles(), resultSet.getCircles());
    }

    private static void addResults(ResultSetOneDimensional finalResultSet,
	    ResultSetOneDimensional newResultSet) {
	finalResultSet.addAll(finalResultSet.getLeftPOIs(),
		newResultSet.getLeftPOIs());
	finalResultSet.addAll(finalResultSet.getRightPOIs(),
		newResultSet.getRightPOIs());
	finalResultSet.addAll(finalResultSet.getCircles(),
		newResultSet.getCircles());
    }

    @Override
    public void crawl(String state, int category, String query,
	    Envelope envelopeState) {
	// TODO Auto-generated method stub

    }

}
