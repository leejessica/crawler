/**
 * 
 */
package mo.umac.crawler;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import mo.umac.utils.FileOperator;
import mo.umac.geo.Circle;
import mo.umac.geo.Coverage;
import mo.umac.geo.UScensusData;
import mo.umac.parser.ResultSet;
import org.apache.log4j.Logger;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Conduct a search using the Yahoo! Local Search Service.
 * 
 * @author Kate Yim
 * 
 */
public class BruteForceCrawler extends CrawlerStrategy {
	//
	public static Logger logger = Logger.getLogger(BruteForceCrawler.class
			.getName());

	/**
	 * Crawl points in US by states. All crawled .xml files will be classified
	 * into the subFolders corresponding to their states' name.
	 * 
	 * @param mapOutput
	 *            store the crawled file's name , the corresponding query
	 *            criteria, and .gz file's name.
	 * @param envelopeState
	 *            a MBR of a state
	 * @param overflow
	 * @param folder
	 *            stores the crawled .xml files, consist by a folder's name +
	 *            the state's name
	 * 
	 * @return true: End; false: Not End
	 */
	public boolean crawlOne(String appid, String subFolder,
			Envelope preEnvelope, Envelope region, Envelope unit,
			boolean overflow) {
	/*	Envelope aEnvelope;
		// If it is the last region, then end the crawling process.
		if (finishedCrawling(preEnvelope, region)) {
			return true;
		}
		// This loop represents traversing every sub-region
		aEnvelope = nextEnvelopeInRegion(region, preEnvelope, unit, overflow);
		crawl(appid, subFolder, preEnvelope, region, unit, overflow);
		Circle circle = Coverage.computeCircle(aEnvelope);

		// the first page for any query
		int start = 1;
		YahooLocalQuery qc = new YahooLocalQuery(subFolder, dbOutput, region,
				appid, start, circle, numQueries, query, zip, MAX_RESULTS_NUM);
		ResultSet resultSet = query(qc);
		numQueries++;
		// This loop represents turning over the page.
		int maxStartForThisQuery = maxStartForThisQuery(resultSet);
		for (start += MAX_RESULTS_NUM; start < maxStartForThisQuery; start += MAX_RESULTS_NUM) {
			qc = new YahooLocalQuery(subFolder, dbOutput, region, appid, start,
					circle, numQueries, query, zip, MAX_RESULTS_NUM);
			query(qc);
			numQueries++;
		}*/
		return false;
	}

	public boolean crawl(String appid, String subFolder, Envelope preEnvelope,
			Envelope region, Envelope unit, boolean overflow) {
/*		Envelope aEnvelope;
		// If it is the last region, then end the crawling process.
		if (finishedCrawling(preEnvelope, region)) {
			return true;
		}
		// This region can be crawled at once
		if (inOneCrawling(region, unit)) {
			// the first page for any query
			int start = 1;
			Circle circle = Coverage.computeCircle(aEnvelope);
			QueryCondition qc = new QueryCondition(subFolder, mapOutput,
					region, appid, start, circle, numQueries, overflow, query,
					zip, results);
			ResultSet resultSet = query(qc);
			numQueries++;
			// This loop represents turning over the page.
			int maxStartForThisQuery = maxStartForThisQuery(resultSet);
			for (start += maxResults; start < maxStartForThisQuery; start += maxResults) {
				qc = new QueryCondition(subFolder, mapOutput, region, appid,
						start, circle, numQueries, overflow, query, zip,
						results);
				query(qc);
				numQueries++;
			}
		} else {
			// divide this region for further crawling
		}
		// This loop represents traversing every sub-region
		// aEnvelope = nextEnvelopeInRegion(region, preEnvelope, unit,
		// overflow);
*/
		return false;
	}

	@Override
	protected boolean crawl(String appid, String subFolder,
			Envelope envelopeState, BufferedWriter queryOutput, BufferedWriter resultsOutput) {
		return false;
	}
}
