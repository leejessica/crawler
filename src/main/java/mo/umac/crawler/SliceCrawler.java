package mo.umac.crawler;

import java.io.BufferedWriter;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

public class SliceCrawler extends OnlineYahooLocalCrawlerStrategy {

    public static Logger logger = Logger
	    .getLogger(SliceCrawler.class.getName());

    @Override
    protected IndicatorResult crawl(String appid, String state, int category,
	    String query, String subFolder, Envelope envelopeState,
	    String queryFile, BufferedWriter queryOutput, String resultsFile,
	    BufferedWriter resultsOutput) {
	// 
	return null;
    }

}
