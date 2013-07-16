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
	
	logger.info(center.toString());
	
	YahooResultSet resultSet = oneCrawlingProcedureForOneDimension(center,
		state, category, query);
	addResults(center, middleLine, finalResultSet, resultSet);

	// find the top and bottom boundary
	Coordinate farthestPoint = farthestPOI(resultSet);
	double radius = center.distance(farthestPoint);

	if (radius >= middleLine.getLength() / 2) {
	    // finished crawling
	    return finalResultSet;
	}

	// recursively crawl
	// TODO check
	Coordinate newRight = middleLine.pointAlongOffset(0.5, -radius);
	LineSegment leftLine = new LineSegment(left, newRight);
	OneDimensionalResultSet newLeftResultSet = extendOneDimensional(state,
		category, query, leftLine);
	addResults(finalResultSet, newLeftResultSet);

	Coordinate newLeft = middleLine.pointAlongOffset(0.5, radius);
	LineSegment rightLine = new LineSegment(newLeft, right);
	OneDimensionalResultSet newRightResultSet = extendOneDimensional(state,
		category, query, rightLine);
	addResults(finalResultSet, newRightResultSet);

	return finalResultSet;
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
	finalResultSet.addACircle(circle);

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

    /**
     * find the farthest POI in one query
     * 
     * @param resultSet
     * @return
     */
    private Coordinate farthestPOI(YahooResultSet resultSet) {
	int size = resultSet.getPOIs().size();
	if (size == 0) {
	    return null;
	} else {
	    POI farthestPOI = resultSet.getPOIs().get(size - 1);
	    return farthestPOI.getCoordinate();
	}
    }

    @Override
    public void crawl(String state, int category, String query,
	    Envelope envelopeState) {
	// TODO Auto-generated method stub

    }

    /**
     * Common steps in one crawling procedure, crawl in the center point
     * 
     * @param appid
     * @param aEnvelope
     * @param category
     * @param query
     * @param subFolder
     * @param queryFile
     * @param queryOutput
     * @param resultsFile
     * @param resultsOutput
     * @param resultSet
     *            return all POIs got in this query procedure
     * @param stateName
     * @return an indicator of the result of this query
     */
    protected YahooResultSet oneCrawlingProcedureForOneDimension(
	    Coordinate point, String state, int category, String query) {
	AQuery aQuery = new AQuery(query, MAX_TOTAL_RESULTS_RETURNED, state,
		category, point);
	YahooResultSet resultSet = query(aQuery);
	return resultSet;
    }

}
