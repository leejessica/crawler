package mo.umac.crawler.offline;

import java.util.ArrayList;
import java.util.List;

import mo.umac.parser.POI;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;

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
	LineSegment middleLine = middleLine(envelopeState);
	OneDimensionalCrawler oneDimensionalCrawler = new OneDimensionalCrawler();
	OneDimensionalResultSet oneDimensionalResultSet = oneDimensionalCrawler
		.extendOneDimensional(state, category, query, middleLine);

	// For all returned points, find the left and the right nearest point to
	// the middle line.
	List<POI> leftRightNearestPOIs = nearestPOIs(envelopeState, middleLine,
		oneDimensionalResultSet);
	// TODO compute that should be covered regions (left/right)
	List<Envelope> leftRightNearestEnvelope = nearestCoveredRegion(
		envelopeState, middleLine, leftRightNearestPOIs);

	// stop criteria
	boolean covered = false;
	covered = judgeCovered(envelopeState, middleLine,
		oneDimensionalResultSet);
	if (covered) {
	    return;
	}

	fillGaps(envelopeState, middleLine, leftRightNearestEnvelope.get(0),
		oneDimensionalResultSet);
	fillGaps(envelopeState, middleLine, leftRightNearestEnvelope.get(1),
		oneDimensionalResultSet);

	List<Envelope> leftRightRemainedEnvelope = remainedRegion(
		envelopeState, leftRightNearestEnvelope);
	Envelope envelopeLeft = leftRightRemainedEnvelope.get(0);
	crawl(state, category, query, envelopeLeft);

	Envelope envelopeRight = leftRightRemainedEnvelope.get(1);
	crawl(state, category, query, envelopeRight);
    }

    /**
     * find the gaps, and then cover the gaps
     * 
     * @param envelopeState
     * @param middleLine
     * @param envelope
     * @param oneDimensionalResultSet
     */
    private void fillGaps(Envelope envelopeState, LineSegment middleLine,
	    Envelope envelope, OneDimensionalResultSet oneDimensionalResultSet) {
	// FIXME 1. fillGaps

    }

    /**
     * Judge whether this envelope has been covered by this one dimensional
     * crawling method
     * 
     * @param envelopeState
     * @param middleLine
     * @param oneDimensionalResultSet
     * @return
     */
    private boolean judgeCovered(Envelope envelopeState,
	    LineSegment middleLine,
	    OneDimensionalResultSet oneDimensionalResultSet) {
	// FIXME 2 judgeCovered
	return false;
    }

    /**
     * Find the remained envelope need be crawled later
     * 
     * @param envelopeState
     * @param leftRightNearestEnvelope
     * @return
     */
    private List<Envelope> remainedRegion(Envelope envelopeState,
	    List<Envelope> leftRightNearestEnvelope) {
	// TODO
	return null;
    }

    /**
     * Compute the left region and the right region based on the left/right
     * nearest points.
     * 
     * @param envelopeState
     * @param middleLine
     * @param leftRightPOIs
     * @return
     */
    private List<Envelope> nearestCoveredRegion(Envelope envelopeState,
	    LineSegment middleLine, List<POI> leftRightNearestPOIs) {
	List<Envelope> leftRightNearestEnvelope = new ArrayList<Envelope>();
	// TODO
	// cannot exceed the boundary of the envelopeState
	return leftRightNearestEnvelope;
    }

    /**
     * Find the nearest left and right POIs to the middle line. But not in the
     * middle line
     * 
     * @param envelopeState
     * @param middleLine
     * @param oneDimensionalResultSet
     * @return the left & the right nearest point
     */
    private List<POI> nearestPOIs(Envelope envelopeState,
	    LineSegment middleLine,
	    OneDimensionalResultSet oneDimensionalResultSet) {
	List<POI> leftRight = new ArrayList<POI>();
	// TODO nearestPOIs
	return leftRight;
    }

    /**
     * fine the middle line
     * 
     * @param envelopeState
     * @return the longitude of the middle line
     */
    private LineSegment middleLine(Envelope envelopeState) {
	// Coordinate center = envelopeState.centre();
	// return center.x;
	// TODO
	return null;
    }

}
