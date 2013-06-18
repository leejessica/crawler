package mo.umac.crawler;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

public class SliceCrawler extends OfflineYahooLocalCrawlerStrategy {

    public static Logger logger = Logger
	    .getLogger(SliceCrawler.class.getName());

    /* (non-Javadoc)
     * @see mo.umac.crawler.OfflineYahooLocalCrawlerStrategy#crawl(java.lang.String, int, java.lang.String, com.vividsolutions.jts.geom.Envelope)
     * 
     * This is the implementation of the upper bound algorithm.
     * TODO  
     * 
     */
    @Override
    public void crawl(String state, int category, String query,
	    Envelope envelopeState) {
	
	
	
    }


    
}
