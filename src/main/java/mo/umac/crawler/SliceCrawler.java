package mo.umac.crawler;

import java.util.List;
import org.apache.log4j.Logger;
import com.vividsolutions.jts.geom.Coordinate;
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
     * This is the implementation of the upper bound algorithm.
     */
    @Override
    public void crawl(String state, int category, String query,
	    Envelope envelopeState) {

	// first fine the middle line, and then use the 1 dimensional method to
	// issue queries on this line.
	double middleLine = middleLine(envelopeState);
	OneDimensionalResultSet resultSet = OneDimensionalCrawler
		.extendOneDimensional(state, category, query, envelopeState,
			middleLine);

	// For all returned points, find the left and the right nearest point to
	// the middle line.
	List<Envelope> leftRight = nearestBoundary(envelopeState, middleLine,
		resultSet);
	// stop criteria
	boolean[] coveredLeftRight = { false, false };
	coveredLeftRight = judgeCovered(envelopeState, middleLine, leftRight);

	if (coveredLeftRight[0] == false) {
	    Envelope envelopeLeft = leftRight.get(0);
	    crawl(state, category, query, envelopeLeft);
	}
	if (coveredLeftRight[1] == false) {
	    Envelope envelopeRight = leftRight.get(1);
	    crawl(state, category, query, envelopeRight);
	}
    }

    private boolean[] judgeCovered(Envelope envelopeState, double middleLine,
	    List<Envelope> leftRight) {
	// TODO Auto-generated method stub
	return null;
    }

    private List<Envelope> nearestBoundary(Envelope envelopeState,
	    double middleLine, OneDimensionalResultSet resultSet) {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * fine the middle line
     * 
     * @param envelopeState
     * @return the longitude of the middle line
     */
    private double middleLine(Envelope envelopeState) {
	// TODO check 
	Coordinate center = envelopeState.centre();
	return center.x;
    }

}
