/**
 * 
 */
package mo.umac.crawler.offline;

import mo.umac.crawler.online.YahooLocalQuery;
import mo.umac.geo.Circle;
import mo.umac.geo.Coverage;
import mo.umac.parser.YahooResultSet;

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
	    int category, String query, Envelope envelopeState,
	    double middleLine) {

	// FIXME

	// Circle circle = Coverage.computeCircle(aEnvelope);
	//
	// YahooLocalQuery aQuery = new YahooLocalQuery(subFolder, queryFile,
	// queryOutput, resultsFile, resultsOutput, envelopeState, appid,
	// state, category, start, circle, numQueries, query, zip, results);

	// YahooResultSet resultSet = query(aQuery);

	return null;
    }

    @Override
    public void crawl(String state, int category, String query,
	    Envelope envelopeState) {
	// TODO Auto-generated method stub

    }

}
