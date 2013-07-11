/**
 * 
 */
package mo.umac.crawler.online;

import java.io.BufferedWriter;
import java.util.ArrayList;

import mo.umac.spatial.Coverage;

import org.apache.log4j.Logger;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author kate
 * 
 */
public class QuadTreeCrawler extends OnlineYahooLocalCrawlerStrategy {
	//
	public static Logger logger = Logger.getLogger(QuadTreeCrawler.class
			.getName());

	/**
	 * Crawling all points in this region
	 * 
	 * @param appid
	 * @param subFolder
	 * @param region
	 * @return whether it is finished
	 */
	public IndicatorResult crawl(String appid, String state, int category,
			String query, String subFolder, Envelope aEnvelope,
			String queryFile, BufferedWriter queryOutput, String resultsFile,
			BufferedWriter resultsOutput) {
		// logger.debug("crawling [" + aEnvelope.getMinX() + ","
		// + aEnvelope.getMaxX() + "," + aEnvelope.getMinY() + ","
		// + aEnvelope.getMaxY() + "]");
		// TODO find the last valid crawled page, and start crawling again from
		// that page. (Avoid re-crawling from beginning)
		IndicatorResult indicatorResult = oneCrawlingProcedure(appid,
				aEnvelope, state, category, query, subFolder, queryFile,
				queryOutput, resultsFile, resultsOutput, null);
		if (indicatorResult == IndicatorResult.OVERFLOW) {
			ArrayList<Envelope> envelopeList = Coverage
					.divideEnvelope(aEnvelope);
			// logger.debug("divided aEnvelope into ");
			// for (int i = 0; i < envelopeList.size(); i++) {
			// logger.debug("[" + envelopeList.get(i).getMinX() + ","
			// + envelopeList.get(i).getMaxX() + ","
			// + envelopeList.get(i).getMinY() + ","
			// + envelopeList.get(i).getMaxY() + "]");
			// }
			for (int i = 0; i < envelopeList.size(); i++) {
				Envelope dividedEnvelope = envelopeList.get(i);
				crawl(appid, state, category, query, subFolder,
						dividedEnvelope, queryFile, queryOutput, resultsFile,
						resultsOutput);
			}
		}
		return indicatorResult;
	}

	
}
