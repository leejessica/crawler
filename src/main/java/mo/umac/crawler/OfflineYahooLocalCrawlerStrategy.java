package mo.umac.crawler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mo.umac.geo.UScensusData;
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
    public abstract void crawl(String state, int category, String query, Envelope envelopeState);

    /**
     * Entrance of the crawler
     * 
     * @param listNameStates
     * @param listCategoryNames
     */
    public void callCrawling(LinkedList<String> listNameStates,
	    List<String> listCategoryNames) {
	// TODO temporary, should be merged with online algorithm

	// State's information provided by UScensus
	LinkedList<Envelope> allEnvelopeStates = (LinkedList<Envelope>) UScensusData
		.MBR(UScensusData.STATE_SHP_FILE_NAME);
	LinkedList<String> allNameStates = (LinkedList<String>) UScensusData
		.stateName(UScensusData.STATE_DBF_FILE_NAME);

	LinkedList<Envelope> listEnvelopeStates = new LinkedList<Envelope>();

	// select the specified states according to the listNameStates
	for (int i = 0; i < listNameStates.size(); i++) {
	    String specifiedName = listNameStates.get(i);
	    for (int j = 0; j < allNameStates.size(); j++) {
		String name = allNameStates.get(j);
		if (name.equals(specifiedName)) {
		    listEnvelopeStates.add(allEnvelopeStates.get(j));
		}
	    }
	}

	HashMap<Integer, String> categoryIDMap = FileOperator
		.readCategoryID(CATEGORY_ID_PATH);

	crawlByCategoriesStates(listEnvelopeStates, listCategoryNames,
		listNameStates, categoryIDMap);

	httpClient.getConnectionManager().shutdown();

    }

    /**
     * 
     * @param listEnvelopeStates
     * @param listCategoryName
     * @param nameStates
     * @param categoryIDMap
     */
    private void crawlByCategoriesStates(
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
