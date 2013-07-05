/**
 * 
 */
package mo.umac.crawler.offline;

import java.io.BufferedWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mo.umac.parser.POI;
import mo.umac.parser.YahooResultSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

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
	    int category, String query, Envelope aEnvelope, double middleLine) {

	String queryFile = null;
	String subFolder = null;
	BufferedWriter resultsOutput = null;
	BufferedWriter queryOutput = null;
	String resultsFile = null;

	Coordinate center = aEnvelope.centre();
	
	List resultSetList = new ArrayList<YahooResultSet>();
	// indicate whether this line has been fully covered
	boolean stop = false;
	while (!stop) {
	    YahooResultSet resultSet = new YahooResultSet();
	    oneCrawlingProcedure(APPID, aEnvelope, state, category, query,
		    subFolder, queryFile, queryOutput, resultsFile,
		    resultsOutput, resultSet);
	    resultSetList.add(resultSet);
	    // TODO looking for the boundary of this query procedure
	    
	}
	POI nearestPOI = nearestPOI(resultSetList, middleLine);
	Coordinate poi = nearestPOI.getCoordinate();
	return null;
    }

    /**
     * Find the nearest poi to the middle line among all queries.
     * 
     * @param resultSet
     * @param middleLine
     * @return
     */
    private POI nearestPOI(List resultSetList, double middleLine) {
	POI nearest = null;

	return nearest;
    }

    /**
     * find the farthest POI in one query
     * 
     * @param resultSet
     * @return
     */
    private POI farthestPOI(YahooResultSet resultSet) {
	return null;
    }

    @Override
    public void crawl(String state, int category, String query,
	    Envelope envelopeState) {
	// TODO Auto-generated method stub

    }

}
