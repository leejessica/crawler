package mo.umac.crawler;

import java.io.BufferedWriter;

import mo.umac.crawler.online.IndicatorResult;
import mo.umac.crawler.online.YahooLocalQueryFileDB;
import mo.umac.geo.Circle;
import mo.umac.geo.Coverage;
import mo.umac.parser.YahooResultSet;

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
     * Check whether it still follows the query limitation
     * 
     * @param countNumQueries
     * @param beginTime
     */
    protected void sleepForIPRestriction(long beginTime) {
	// sleep to satisfy the ip restriction: 5000 accesses per day
	long now = System.currentTimeMillis();
	long diff = (now - beginTime);
	if (diff < DAY_TIME) {
	    try {
		long interval = (DAY_TIME - diff) / 1000 / 60;
		logger.info("Sleeping " + interval + "minutes.");
		Thread.currentThread().sleep(DAY_TIME - diff);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	// How can I made this mistake!!!
	// beginTime = System.currentTimeMillis();
    }

    /**
     * Force sleep if there are access limitations.
     * 
     * @param limitedPageCount
     */
    protected void sleeping(int limitedPageCount) {
	// Set 5 minutes as the unit
	long unit = 5 * 60 * 1000;
	long interval = 0;
	long base = 1;
	for (int i = 0; i < limitedPageCount - 1; i++) {
	    base *= 2;
	}
	interval = unit * base;
	long aDay = 24 * 60 * 60 * 1000;
	try {
	    if (interval > aDay) {
		logger.info("Sleeping a day");
		Thread.currentThread().sleep(aDay);
	    } else {
		logger.info("Sleeping " + interval / 1000 / 60 + " minutes.");
		Thread.currentThread().sleep(interval);
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Calculate the maximum start number of a query
     * 
     * @param resultSet
     * @return the max start value in constructing a query.
     */
    protected int maxStartForThisQuery(YahooResultSet resultSet) {
	int totalResultAvailable = resultSet.getTotalResultsAvailable();
	if (totalResultAvailable > MAX_START + MAX_RESULTS_NUM) {
	    return MAX_START;
	}
	int idealNum = (int) (Math.floor(1.0 * totalResultAvailable
		/ MAX_RESULTS_NUM) * 20);
	if (idealNum > MAX_START) {
	    return MAX_START;
	} else {
	    return idealNum;
	}
    }

    protected HttpClient createHttpClient() {
	PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
	HttpParams params = new BasicHttpParams();
	int timeout = 1000 * 24 * 60 * 60;
	HttpConnectionParams.setConnectionTimeout(params, timeout);
	HttpConnectionParams.setSoTimeout(params, timeout);
	HttpClient httpClient = new DefaultHttpClient(manager, params);
	return httpClient;
    }

    /**
     * Common steps in one crawling procedure, crawl in the center point
     * 
     * @param appid
     * @param aEnvelope
     * @param category
     * @param query
     * @param subFolder
     * @param queryFile
     * @param queryOutput
     * @param resultsFile
     * @param resultsOutput
     * @param resultSet
     *            return all POIs got in this query procedure
     * @param stateName
     * @return an indicator of the result of this query
     */
    protected IndicatorResult oneCrawlingProcedure(String appid,
	    Envelope aEnvelope, String state, int category, String query,
	    String subFolder, String queryFile, BufferedWriter queryOutput,
	    String resultsFile, BufferedWriter resultsOutput,
	    YahooResultSet resultSet) {
	YahooResultSet tempResultSet;
	// the first page for any query
	int start = 1;
	Circle circle = Coverage.computeCircle(aEnvelope);
	YahooLocalQueryFileDB qc = new YahooLocalQueryFileDB(subFolder,
		queryFile, queryOutput, resultsFile, resultsOutput, aEnvelope,
		appid, state, category, start, circle, countNumQueries, query,
		zip, MAX_RESULTS_NUM);
	resultSet = query(qc);
	//
	// This loop represents turning over the page.
	int maxStartForThisQuery = maxStartForThisQuery(resultSet);
	// logger.debug("totalResultsAvailable=" +
	// resultSet.getTotalResultsAvailable());
	// logger.debug("maxStartForThisQuery=" + maxStartForThisQuery);
	// TODO check the last page
	for (start += MAX_RESULTS_NUM; start <= maxStartForThisQuery; start += MAX_RESULTS_NUM) {
	    qc = new YahooLocalQueryFileDB(subFolder, queryFile, queryOutput,
		    resultsFile, resultsOutput, aEnvelope, appid, state,
		    category, start, circle, countNumQueries, query, zip,
		    MAX_RESULTS_NUM);
	    tempResultSet = query(qc);
	    // TODO check add at 5-7-2013
	    resultSet.getPOIs().addAll(tempResultSet.getPOIs());
	}
	// the last query
	if (maxStartForThisQuery == MAX_START) {
	    start = maxStartForThisQuery;
	    qc = new YahooLocalQueryFileDB(subFolder, queryFile, queryOutput,
		    resultsFile, resultsOutput, aEnvelope, appid, state,
		    category, start, circle, countNumQueries, query, zip,
		    MAX_RESULTS_NUM);
	    tempResultSet = query(qc);
	    // TODO check add at 5-7-2013
	    resultSet.getPOIs().addAll(tempResultSet.getPOIs());
	}
	if (resultSet.getTotalResultsAvailable() > MAX_TOTAL_RESULTS_RETURNED) {
	    return IndicatorResult.OVERFLOW;
	}
	return IndicatorResult.NONOVERFLOW;
    }
}
