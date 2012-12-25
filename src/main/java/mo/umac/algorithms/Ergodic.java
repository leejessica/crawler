/**
 * 
 */
package mo.umac.algorithms;

import mo.umac.crawler.Coverage;
import mo.umac.crawler.QueryCondition;
import mo.umac.parser.ResultSet;
import mo.umac.utils.Circle;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author kate
 * 
 */
public class Ergodic extends CrawlerStrategy {

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
	 *            TODO
	 * @param folder
	 *            stores the crawled .xml files, consist by a folder's name +
	 *            the state's name
	 * 
	 * @return end ?
	 */
	private boolean crawl(String appid, String subFolder, Envelope preEnvelope,
			Envelope region, Envelope unit, boolean overflow) {
		Envelope aEnvelope;
		// If it is the last region, then end the crawling process.
		if (finishedCrawling(preEnvelope, region)) {
			return true;
		}
		// This loop represents traversing every sub-region
		aEnvelope = Coverage.nextEnvelopeInRegion(region, preEnvelope, unit,
				overflow);
		Circle circle = Coverage.computeCircle(aEnvelope);
		// First Query
		int start = 1;
		QueryCondition qc = new QueryCondition(subFolder, mapOutput, region,
				appid, start, circle, numQueries, overflow, query, zip, results);
		ResultSet resultSet = query(qc);
		numQueries++;

		// TODO

		// This loop represents turning over the page.
		int maxStartForThisQuery = maxStartForThisQuery(resultSet);
		for (start += maxResults; start < maxStartForThisQuery; start += maxResults) {
			qc = new QueryCondition(subFolder, mapOutput, region, appid, start,
					circle, numQueries, overflow, query, zip, results);
			query(qc);
			numQueries++;
		}
		return false;
	}

}
