package mo.umac.crawler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mo.umac.parser.YahooResultSet;
import mo.umac.spatial.UScensusData;
import mo.umac.utils.FileOperator;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

public abstract class YahooLocalCrawlerStrategy {

    protected static Logger logger = Logger
	    .getLogger(YahooLocalCrawlerStrategy.class.getName());

    /**
     * The path of the category ids of Yahoo! Local
     */
    public static String CATEGORY_ID_PATH = "./src/main/resources/cat_id.txt";

    /**
     * The maximum number of returned results by a query.
     */
    protected final int MAX_RESULTS_NUM = 20;
    /**
     * The maximum starting result position to return.
     */
    protected final int MAX_START = 250;

    /**
     * The maximum number of results on can get through this query by only
     * changing the start value.
     */
    protected final int MAX_TOTAL_RESULTS_RETURNED = MAX_START
	    + MAX_RESULTS_NUM; // =270;

    protected int countNumQueries = 1;

    protected int zip = 0;

    public static final double EPSILON = 0.0001;

    /**
     * Entrance of the crawler
     * 
     * @param listNameStates
     * @param listCategoryNames
     */
    public void callCrawling(LinkedList<String> listNameStates,
	    List<String> listCategoryNames) {
	LinkedList<Envelope> listEnvelopeStates = selectEnvelopes(
		listNameStates, listCategoryNames);
	HashMap<Integer, String> categoryIDMap = FileOperator
		.readCategoryID(CATEGORY_ID_PATH);

	prepareData();

	crawlByCategoriesStates(listEnvelopeStates, listCategoryNames,
		listNameStates, categoryIDMap);

	endData();

    }

    /**
     * Initializations for storing the data
     */
    protected abstract void prepareData();

    protected abstract void endData();

    protected abstract void crawlByCategoriesStates(
	    LinkedList<Envelope> listEnvelopeStates,
	    List<String> listCategoryNames, LinkedList<String> listNameStates,
	    HashMap<Integer, String> categoryIDMap);

    /**
     * Select the envelope information from UScensus data
     * 
     * @param listNameStates
     * @param listCategoryNames
     * @return
     */
    private LinkedList<Envelope> selectEnvelopes(
	    LinkedList<String> listNameStates, List<String> listCategoryNames) {
	// State's information provided by UScensus
	// FIXME check envelope
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
	return listEnvelopeStates;
    }

}
