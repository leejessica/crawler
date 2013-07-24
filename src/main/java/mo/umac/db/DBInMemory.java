package mo.umac.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mo.umac.crawler.CrawlerStrategy;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.ResultSetYahooOnline;
import mo.umac.metadata.YahooLocalQueryFileDB;
import mo.umac.rtree.MyRTree;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;

public class DBInMemory {

    protected static Logger logger = Logger.getLogger(DBInMemory.class
	    .getName());

    /**
     * All tuples; Integer is the item's id
     */
    public static HashMap<Integer, APOI> pois;

    public static MyRTree rtree;

    public static Set<Integer> poisIDs = new HashSet<Integer>();

    /**
     * @param externalDataSet
     */
    public void readFromExtenalDB(String category, String state) {
	pois = CrawlerStrategy.dbExternal.readFromExtenalDB(category, state);
    }

    public void writeToExternalDB(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, ResultSetYahooOnline resultSet) {
	CrawlerStrategy.dbExternal.writeToExternalDB(queryID, level, parentID,
		qc, resultSet);
    }

    /**
     * For recording the query results
     * 
     * @param dbName
     * @param queryID
     * @param query
     * @param resultSet
     */
    private void writeToExternalDB(int queryID, AQuery query,
	    ResultSet resultSet) {
	CrawlerStrategy.dbExternal.writeToExternalDB(queryID, query, resultSet);
    }

    public void index(List<Coordinate> coordinate) {
	rtree = new MyRTree(coordinate);
    }

    /**
     * Indexing all pois
     */
    public void index() {
	rtree = new MyRTree(pois);
    }

    public ResultSet query(AQuery qc) {
	Coordinate queryPoint = qc.getPoint();
	logger.debug("query point = " + queryPoint.toString());

	List<Integer> resultsID = rtree.searchNN(queryPoint, qc.getTopK());
	//
	poisIDs.addAll(resultsID);

	// FIXME add re-transfering from the break point.
	int queryID = CrawlerStrategy.countNumQueries;

	logger.debug("countNumQueries = " + CrawlerStrategy.countNumQueries);

	ResultSet resultSet = queryByID(resultsID);
	int totalResultsReturned = resultsID.size();
	resultSet.setTotalResultsReturned(totalResultsReturned);
	//
	long before = System.currentTimeMillis();
	System.out.println("Before writing: " + before);
	writeToExternalDB(queryID, qc, resultSet);
	long after = System.currentTimeMillis();
	System.out.println("After writing: " + after);
	System.out.println("time for writeToExternalDB = " + (after - before) / 1000);
	logger.debug("number of points crawled = " + numCrawlerPoints());

	if (queryID % 500 == 0) {
	    logger.info("countNumQueries = " + CrawlerStrategy.countNumQueries);
	    logger.info("number of points crawled = " + numCrawlerPoints());
	}
	CrawlerStrategy.countNumQueries++;
	return resultSet;
    }

    /**
     * @param resultsID
     * @return
     */
    public ResultSet queryByID(List<Integer> resultsID) {
	List<APOI> points = new ArrayList<APOI>();
	for (int i = 0; i < resultsID.size(); i++) {
	    int id = resultsID.get(i);
	    APOI point = pois.get(id);
	    points.add(point);
	}
	ResultSet resultSet = new ResultSet();
	resultSet.setPOIs(points);
	return resultSet;
    }

    public int numCrawlerPoints() {
	// return CrawlerStrategy.dbExternal.numCrawlerPoints();
	return DBInMemory.poisIDs.size();
    }

}
