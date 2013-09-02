/**
 * 
 */
package mo.umac.crawler.offline;

import java.util.ArrayList;
import java.util.List;

import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSet;
import mo.umac.spatial.Circle;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * The heuristic algorithm in the paper
 * 
 * @author kate
 * 
 */
public class HexagonCrawler extends OfflineStrategy {

    public static Logger logger = Logger.getLogger(HexagonCrawler.class
	    .getName());

    public HexagonCrawler() {
	super();
	logger.info("------------HexagonCrawler------------");
    }

    /**
     * The parameter of shrinking the starting circle
     */
    public static double alpha = 0.8;

    /*
     * (non-Javadoc)
     * 
     * @see mo.umac.crawler.offline.OfflineStrategy#crawl(java.lang.String, int,
     * java.lang.String, com.vividsolutions.jts.geom.Envelope)
     */
    @Override
    public void crawl(String state, int category, String query,
	    Envelope envelopeStateECEF) {
	if (logger.isDebugEnabled()) {
	    logger.info("------------crawling---------");
	    logger.info(envelopeStateECEF.toString());
	}
	// finished crawling
	if (envelopeStateECEF == null) {
	    return;
	}

	// issue a query randomly at the envelope
	Coordinate start = random(envelopeStateECEF);

    }

    /**
     * Randomly choose a point at the envelope
     * 
     * @param envelope
     * @return
     */
    private Coordinate random(Envelope envelope) {
	// FIXME random
	Coordinate p = null;
	double height = envelope.getHeight();

	return p;
    }

    /**
     * Build the clique from a start point
     * 
     * @param start
     */
    private void clique(String state, int category, String query,
	    Coordinate start) {
	AQuery aQuery = new AQuery(start, state, category, query,
		MAX_TOTAL_RESULTS_RETURNED);
	ResultSet resultSet = query(aQuery);
	Coordinate farthestCoordinate = OneDimensionalCrawler
		.farthest(resultSet);
	if (farthestCoordinate == null) {
	    logger.error("farestest point is null");
	}
	double radius = start.distance(farthestCoordinate);
	if (logger.isDebugEnabled()) {
	    logger.debug("farthestCoordinate = "
		    + farthestCoordinate.toString());
	    logger.debug("radius = " + radius);
	}
	Circle aCircle = new Circle(start, radius);
	resultSet.addACircle(aCircle);
	//
	double radiusAlpha = radius * alpha;
	List nextCenters = aroundPoints(start, radiusAlpha);
	// issuing these queries
	for (int i = 0; i < nextCenters.size(); i++) {
	    Coordinate onePoint = (Coordinate) nextCenters.get(i);
	    // FIXME
	}
	
	
    }

    /**
     * Get the next 6 issuing points from the center.
     * 
     * @param center
     * @return
     */
    private List aroundPoints(Coordinate center, double radius) {
	List nextCenters = new ArrayList<Coordinate>();

	return nextCenters;
    }
    

}
