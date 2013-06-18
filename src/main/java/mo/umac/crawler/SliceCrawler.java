package mo.umac.crawler;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

public class SliceCrawler extends OfflineYahooLocalCrawlerStrategy {

	public static Logger logger = Logger
			.getLogger(SliceCrawler.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mo.umac.crawler.OfflineYahooLocalCrawlerStrategy#crawl(java.lang.String,
	 * int, java.lang.String, com.vividsolutions.jts.geom.Envelope)
	 * 
	 * This is the implementation of the upper bound algorithm. TODO
	 */
	@Override
	public void crawl(String state, int category, String query,
			Envelope envelopeState) {
		// first fine the middle line, and then use the 1 dimensional method to
		// issue queries on this line.
		double middleLine = middleLine(envelopeState);
		
		
		
		// For all returned points, find the left and the right nearest point to
		// the middle line.

	}
	
	private double middleLine(Envelope envelopeState) {
		double midLat = 0.0;
		// TODO 
		return midLat;
	}

}
