/**
 * 
 */
package mo.umac.crawler;

import java.io.BufferedWriter;
import java.util.ArrayList;

import mo.umac.geo.Coverage;

import org.apache.log4j.Logger;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author kate
 * 
 */
public class QuadTreeCrawler extends CrawlerStrategy {
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
	public IndicatorResult crawl(String appid, String state,
			int category, String subFolder,
			Envelope aEnvelope, BufferedWriter queryOutput, BufferedWriter resultsOutput) {
		// logger.debug("crawling [" + aEnvelope.getMinX() + ","
		// + aEnvelope.getMaxX() + "," + aEnvelope.getMinY() + ","
		// + aEnvelope.getMaxY() + "]");
		IndicatorResult indicatorResult = oneCrawlingProcedure(appid,
				aEnvelope, state, category, subFolder, queryOutput, resultsOutput);
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
				crawl(appid, state, category, subFolder,
						dividedEnvelope, queryOutput, resultsOutput);
			}
		}
		return indicatorResult;
	}

}
