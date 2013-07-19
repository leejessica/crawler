package mo.umac.crawler.offline;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.YahooLocalCrawlerStrategy;
import mo.umac.db.DataSet;
import mo.umac.db.Postgresql;
import mo.umac.parser.YahooResultSet;
import mo.umac.spatial.GeoOperator;
import mo.umac.utils.CommonUtils;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * The off line algorithm is for testing different algorithms in the
 * experiments.
 * 
 * @author Kate
 * 
 */
public abstract class OfflineYahooLocalCrawlerStrategy extends
	YahooLocalCrawlerStrategy {
    protected static Logger logger = Logger
	    .getLogger(OfflineYahooLocalCrawlerStrategy.class.getName());

    /**
     * This is the crawling algorithm
     */
    public abstract void crawl(String state, int category, String query,
	    Envelope envelopeState);

    /**
     * @param aQuery
     * @return
     */
    public static YahooResultSet query(AQuery aQuery) {
	// FIXME change to R tree, file dataset
	DataSet dataset = new Postgresql();
	return dataset.query(aQuery);
    }

    /*
     * (non-Javadoc)
     * 
     * @see mo.umac.crawler.YahooLocalCrawlerStrategy#prepareData()
     */
    @Override
    protected void prepareData() {
	// TODO connect to the local dataset

    }

    /*
     * (non-Javadoc)
     * 
     * @see mo.umac.crawler.YahooLocalCrawlerStrategy#endData()
     */
    protected void endData() {
	// TODO shut down the connection
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * mo.umac.crawler.YahooLocalCrawlerStrategy#crawlByCategoriesStates(java
     * .util.LinkedList, java.util.List, java.util.LinkedList,
     * java.util.HashMap)
     */
    protected void crawlByCategoriesStates(
	    LinkedList<Envelope> listEnvelopeStates,
	    List<String> listCategoryNames, LinkedList<String> nameStates,
	    HashMap<Integer, String> categoryIDMap) {
	for (int i = 0; i < nameStates.size(); i++) {
	    String state = nameStates.get(i);
	    logger.info("crawling in the state: " + state);
	    for (int j = 0; j < listCategoryNames.size(); j++) {
		String query = listCategoryNames.get(j);
		logger.info("crawling the category: " + query);
		// initial category
		int category = -1;
		Object searchingResult = CommonUtils.getKeyByValue(
			categoryIDMap, query);
		if (searchingResult != null) {
		    category = (Integer) searchingResult;

		    //
		    Envelope envelopeStateLLA = listEnvelopeStates.get(i);

		    logger.debug(envelopeStateLLA.toString());
		    Envelope envelopeStateECEF = GeoOperator
			    .lla2ecef(envelopeStateLLA);
		    logger.debug(envelopeStateECEF.toString());

		    crawl(state, category, query, envelopeStateECEF);
		    //
		} else {
		    logger.error("Cannot find category id for query: " + query
			    + " in categoryIDMap");
		}
	    }
	}
    }
}
