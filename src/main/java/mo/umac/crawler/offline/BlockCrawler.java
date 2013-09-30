/**
 * 
 */
package mo.umac.crawler.offline;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

/**
 * split the region by the external knowledge
 * 
 * @author kate
 * 
 */
public class BlockCrawler extends OfflineStrategy {

	public static Logger logger = Logger.getLogger(BlockCrawler.class.getName());

	public BlockCrawler() {
		super();
		logger.info("------------BlockCrawler------------");
	}

	@Override
	public void crawl(String state, int category, String query, Envelope envelopeState) {
		// TODO Auto-generated method stub

	}

}
