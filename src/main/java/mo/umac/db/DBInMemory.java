package mo.umac.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    /**
     * The index for all points in the database
     */
    public static MyRTree rtreePoints;

    // TODO treeset is for debugging. change to hashset when running the program
    public static Set<Integer> poisIDs = new TreeSet<Integer>();

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
	rtreePoints = new MyRTree(coordinate);
    }

    /**
     * Indexing all pois
     */
    public void index() {
	rtreePoints = new MyRTree(pois);
    }

    public ResultSet query(AQuery qc) {
	Coordinate queryPoint = qc.getPoint();
	if (logger.isDebugEnabled()) {
	    logger.debug("query point = " + queryPoint.toString());
	}
	List<Integer> resultsID = rtreePoints
		.searchNN(queryPoint, qc.getTopK());
	//
	poisIDs.addAll(resultsID);

	// FIXME add re-transfer from the break point.
	int queryID = CrawlerStrategy.countNumQueries;

	if (logger.isDebugEnabled()) {
	    logger.debug("countNumQueries = " + CrawlerStrategy.countNumQueries);
	}

	ResultSet resultSet = queryByID(resultsID);
	resultSet.setTotalResultsReturned(resultsID.size());

	if (logger.isDebugEnabled()) {
	    int size1 = resultsID.size();
	    int size2 = resultSet.getPOIs().size();
	    if (size1 != size2) {
		logger.error("size1 != size2");
	    }
	}
	writeToExternalDB(queryID, qc, resultSet);

	//
	if (logger.isDebugEnabled()) {
	    logger.debug("countNumQueries = " + CrawlerStrategy.countNumQueries);
	    logger.debug("number of points crawled = " + numCrawlerPoints());

	    int size1 = numCrawlerPoints();
	    Set set = new TreeSet();
	    int size2 = numOfTuplesInExternalDB(set);
	    logger.debug("numCrawlerPoints in memory = " + size1);
	    logger.debug("numCrawlerPoints in db = " + size2);
	    // ...
	    // Iterator it = set.iterator();
	    // while (it.hasNext()) {
	    // int id = (Integer) it.next();
	    // logger.debug(id);
	    // }

	    if (size1 != size2) {
		logger.error("size1 != size2");
		logger.error("countNumQueries = "
			+ CrawlerStrategy.countNumQueries);
		logger.error("numCrawlerPoints in memory = " + size1);
		logger.error("numCrawlerPoints in db = " + size2);
	    }

	}

	if (queryID % 100 == 0) {
	    logger.info("countNumQueries = " + CrawlerStrategy.countNumQueries);
	    logger.info("number of points crawled = " + numCrawlerPoints());
	}
	CrawlerStrategy.countNumQueries++;
	return resultSet;
    }

    /**
     * Only for debugging
     * 
     * @return
     */
    public int numOfTuplesInExternalDB(Set set) {
	H2DB h2db = new H2DB();
	String dbName = H2DB.DB_NAME_TARGET;
	Connection conn = h2db.getConnection(dbName);
	try {
	    Statement stat = conn.createStatement();
	    String sql = "select distinct itemid from item";
	    java.sql.ResultSet rs = stat.executeQuery(sql);
	    while (rs.next()) {
		int id = rs.getInt(1);
		set.add(id);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return set.size();
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
	return DBInMemory.poisIDs.size();
    }

}
