package mo.umac.crawler.offline;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.YahooLocalCrawlerStrategy;
import mo.umac.db.DataSet;
import mo.umac.db.H2DBGeo;
import mo.umac.geo.UScensusData;
import mo.umac.parser.YahooResultSet;
import mo.umac.utils.CommonUtils;
import mo.umac.utils.FileOperator;

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
    // TODO OfflineYahooLocalCrawlerStrategy

    /**
     * This is the crawling algorithm
     */
    public abstract void crawl(String state, int category, String query,
	    Envelope envelopeState);

    public YahooResultSet query(AQuery aQuery) {
	DataSet dataset = new H2DBGeo();
	return dataset.query(aQuery);
    }

    @Override
    protected void initData() {
	// TODO connect to the local dataset

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
		    Envelope envelopeState = listEnvelopeStates.get(i);
		    crawl(state, category, query, envelopeState);
		    //
		} else {
		    logger.error("Cannot find category id for query: " + query
			    + " in categoryIDMap");
		}
	    }
	}
    }
}
