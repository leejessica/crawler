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
	public boolean crawl(String appid, String subFolder, Envelope aEnvelope,
			BufferedWriter queryOutput, BufferedWriter resultsOutput) {
		boolean overflow = oneCrawlingProcedure(appid, aEnvelope, subFolder,
				queryOutput, resultsOutput);
		if (overflow) {
			ArrayList<Envelope> envelopeList = Coverage
					.divideEnvelope(aEnvelope);
			for (int i = 0; i <= envelopeList.size(); i++) {
				crawl(appid, subFolder, envelopeList.get(i), queryOutput,
						resultsOutput);
			}
		}
		return true;
	}

}
