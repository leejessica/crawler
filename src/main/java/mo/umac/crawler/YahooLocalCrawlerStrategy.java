package mo.umac.crawler;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mo.umac.crawler.online.IndicatorResult;
import mo.umac.crawler.online.YahooLocalQueryFileDB;
import mo.umac.db.DataSet;
import mo.umac.geo.Circle;
import mo.umac.geo.Coverage;
import mo.umac.geo.UScensusData;
import mo.umac.parser.YahooResultSet;
import mo.umac.utils.FileOperator;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

public abstract class YahooLocalCrawlerStrategy {

    protected static Logger logger = Logger
	    .getLogger(YahooLocalCrawlerStrategy.class.getName());

    public static final String APPID = "appid";

    public static String PROPERTY_PATH = "./src/main/resources/crawler.properties";

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

    protected final long DAY_TIME = 24 * 60 * 60 * 1000;

    /**
     * Record the time, because of the restriction of 5000 queries per day
     */
    protected long beginTime = 0;

    protected HttpClient httpClient;

    protected int countNumQueries = 1;

    /**
     * Count the number of continuous limited pages
     */
    protected int limitedPageCount = 0;

    protected int zip = 0;

    public YahooResultSet query(AQuery aQuery) {
	// TODO ?
	return null;
    }

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

	initData();

	crawlByCategoriesStates(listEnvelopeStates, listCategoryNames,
		listNameStates, categoryIDMap);

	httpClient.getConnectionManager().shutdown();

    }

    /**
     * Initializations for storing the data
     */
    protected abstract void initData();

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
