/**
 * 
 */
package mo.umac.crawler.offline;

import java.io.BufferedWriter;

import mo.umac.crawler.online.IndicatorResult;
import mo.umac.geo.Circle;
import mo.umac.geo.Coverage;
import mo.umac.parser.POI;
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
			int category, String query, Envelope aEnvelope, double middleLine) {

		// FIXME

		Circle circle = Coverage.computeCircle(aEnvelope);
		String queryFile = null;
		String subFolder = null;
		BufferedWriter resultsOutput = null;
		BufferedWriter queryOutput = null;
		int start = 0;
		String resultsFile = null;
		int zip = 0;
		int results = 0;
		int numQueries = 0;

		YahooResultSet resultSet = new YahooResultSet();
		IndicatorResult indicatorResult = oneCrawlingProcedure(APPID,
				aEnvelope, state, category, query, subFolder, queryFile,
				queryOutput, resultsFile, resultsOutput, resultSet);
		// looking for the boundary of this query procedure
		int numPOIs = resultSet.getPOIs().size();
		POI farestPOI = resultSet.getPOIs().get(numPOIs - 1);
		//FIXME construct the point!! or it should changed to the poi construction!
		farestPOI.getLatitude();

		return null;
	}

	@Override
	public void crawl(String state, int category, String query,
			Envelope envelopeState) {
		// TODO Auto-generated method stub

	}

}
