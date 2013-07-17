/**
 * 
 */
package mo.umac.crawler.offline;

import java.util.List;

import mo.umac.crawler.AQuery;
import mo.umac.parser.POI;
import mo.umac.parser.YahooResultSet;
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
public class OneDimensionalCrawler extends OfflineYahooLocalCrawlerStrategy {

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
    public OneDimensionalResultSet extendOneDimensional(String state,
	    int category, String query, LineSegment middleLine) {
	OneDimensionalResultSet finalResultSet = new OneDimensionalResultSet();
	Coordinate left = middleLine.p0;
	Coordinate right = middleLine.p1;
	Coordinate center = middleLine.midPoint();

	logger.debug(left.toString());
	logger.debug(right.toString());
	logger.debug(center.toString());

	Coordinate nearestPointOneQuery = new Coordinate();
	Coordinate farthestPoint = new Coordinate();

	AQuery aQuery = new AQuery(center, state, category, query,
		MAX_TOTAL_RESULTS_RETURNED);
	YahooResultSet resultSet = query(aQuery);

	nearestFarthest(middleLine, resultSet, nearestPointOneQuery,
		farthestPoint);
	addResults(center, middleLine, finalResultSet, resultSet);

	// find the top and bottom boundary
	double radius = center.distance(farthestPoint);

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

	OneDimensionalResultSet newLeftResultSet = extendOneDimensional(state,
		category, query, leftLine);
	addResults(finalResultSet, newLeftResultSet);

	Coordinate newLeft = middleLine.pointAlongOffset(0.5, radius);
	logger.debug("newLeft: " + newLeft.toString());
	LineSegment rightLine = new LineSegment(newLeft, right);
	logger.debug("rightLine: " + rightLine.toString());

	OneDimensionalResultSet newRightResultSet = extendOneDimensional(state,
		category, query, rightLine);
	addResults(finalResultSet, newRightResultSet);

	return finalResultSet;
    }

    private void nearestFarthest(LineSegment middleLine,
	    YahooResultSet resultSet, Coordinate nearestPointOneQuery,
	    Coordinate farthestPoint) {
	int size = resultSet.getPOIs().size();
	// farthest
	if (size == 0) {
	    return;
	} else {
	    POI farthestPOI = resultSet.getPOIs().get(size - 1);
	    farthestPoint = farthestPOI.getCoordinate();
	}
	// nearest
	for (int i = 0; i < size; i++) {
	    POI point = resultSet.getPOIs().get(i);
	    Coordinate coordinate = point.getCoordinate();
	    // not on the middleLine
	    double distance = middleLine.distance(coordinate);
	    if (distance > 0) {
		nearestPointOneQuery = coordinate;
		return;
	    }
	}

    }

    private void addResults(Coordinate center, LineSegment line,
	    OneDimensionalResultSet finalResultSet, YahooResultSet resultSet) {
	List<POI> pois = resultSet.getPOIs();
	for (int i = 0; i < pois.size(); i++) {
	    POI poi = pois.get(i);
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

    private void addResults(OneDimensionalResultSet finalResultSet,
	    OneDimensionalResultSet newResultSet) {
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
