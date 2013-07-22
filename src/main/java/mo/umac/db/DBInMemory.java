package mo.umac.db;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import mo.umac.crawler.CrawlerStrategy;
import mo.umac.crawler.offline.OfflineStrategy;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.DefaultValues;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.ResultSetYahooOnline;
import mo.umac.metadata.YahooLocalQueryFileDB;
import mo.umac.rtree.MyRTree;
import mo.umac.spatial.ECEFLLA;

import com.vividsolutions.jts.geom.Coordinate;

public class DBInMemory {

    protected static Logger logger = Logger.getLogger(DBInMemory.class
	    .getName());
    
    /**
     * All tuples; Integer is the item's id
     */
    public static HashMap<Integer, APOI> pois;

    public static MyRTree rtree;

    /**
     * @param externalDataSet
     */
    public void readFromExtenalDB(String category, String state) {
	pois = CrawlerStrategy.dbExternal.readFromExtenalDB(category, state);
    }

    public void writeToExternalDB() {
	CrawlerStrategy.dbExternal.writeToExternalDB();
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

    private ResultSet queryByID(List<Integer> resultsID) {
	ResultSet resultSet = CrawlerStrategy.dbExternal.queryByID(resultsID);
	return resultSet;
    }

    /**
     * Indexing all pois
     */
    public void index() {
	rtree = new MyRTree(pois);
    }

    public ResultSet query(AQuery qc) {
	Coordinate ecef = qc.getPoint();
	logger.debug("query ecef = " + ecef.toString());
	Coordinate lla = ECEFLLA.ecef2lla(ecef);
	logger.debug("query lla = " + lla.toString());
	List<Integer> resultsID = rtree.searchNN(lla, qc.getTopK());
	// FIXME add re-transfering from the break point.
	int queryID = CrawlerStrategy.countNumQueries;
	// FIXME wrong counting...
	logger.debug("countNumQueries" + CrawlerStrategy.countNumQueries);
	CrawlerStrategy.countNumQueries++;

	ResultSet resultSet = queryByID(resultsID);
	List<APOI> pois = resultSet.getPOIs();
	int totalResultsReturned = resultsID.size();
	resultSet.setPOIs(pois);
	resultSet.setTotalResultsReturned(totalResultsReturned);
	//
	writeToExternalDB(queryID, qc, resultSet);
	return resultSet;
    }

}
