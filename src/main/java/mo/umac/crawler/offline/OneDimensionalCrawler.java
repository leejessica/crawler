/**
 * 
 */
package mo.umac.crawler.offline;

import java.util.List;

import org.apache.log4j.Logger;

import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.ResultSetYahooOnline;
import mo.umac.spatial.Circle;
import mo.umac.spatial.GeoOperator;

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

    public static Logger logger = Logger
	    .getLogger(OneDimensionalCrawler.class.getName());
    
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
	Coordinate left = middleLine.p0;
	Coordinate right = middleLine.p1;
	Coordinate center = middleLine.midPoint();

	logger.debug("left = " + left.toString());
	logger.debug("right = " + right.toString());
	logger.debug("center = " + center.toString());

	AQuery aQuery = new AQuery(center, state, category, query,
		MAX_TOTAL_RESULTS_RETURNED);
	ResultSet resultSet = query(aQuery);

	Coordinate farthestCoordinate = farthest(resultSet);
	double radius = center.distance(farthestCoordinate);

	addResults(center, middleLine, finalResultSet, resultSet);

	if (radius >= middleLine.getLength() / 2) {
	    // finished crawling
	    return finalResultSet;
	}

	// recursively crawl
	Coordinate newRight = middleLine.pointAlongOffset(0.5, -radius);
	logger.debug("newRight: " + newRight.toString());
	//
	LineSegment leftLine = new LineSegment(left, newRight);
	logger.debug("leftLine: " + leftLine.toString());

	ResultSetOneDimensional newLeftResultSet = oneDimCrawl(state, category,
		query, leftLine);
	addResults(finalResultSet, newLeftResultSet);

	Coordinate newLeft = middleLine.pointAlongOffset(0.5, radius);
	logger.debug("newLeft: " + newLeft.toString());
	LineSegment rightLine = new LineSegment(newLeft, right);
	logger.debug("rightLine: " + rightLine.toString());

	ResultSetOneDimensional newRightResultSet = oneDimCrawl(state,
		category, query, rightLine);
	addResults(finalResultSet, newRightResultSet);

	return finalResultSet;
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
	double radius = resultSet.getRadius();
	Circle circle = new Circle(center, radius);
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
