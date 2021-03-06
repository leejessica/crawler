package mo.umac.crawler.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import mo.umac.crawler.CrawlerStrategy;
import mo.umac.crawler.offline.BinaryCrawler;
import mo.umac.crawler.offline.BinaryCrawler_Modify;
import mo.umac.crawler.offline.HexagonCrawler2;
import mo.umac.crawler.offline.HexagonCrawler2_Modify;
import mo.umac.crawler.offline.OfflineStrategy;
import mo.umac.crawler.offline.PeripheryQuery;
import mo.umac.crawler.offline.PerpheryQuery_Optimize;
import mo.umac.crawler.offline.SliceCrawler;
import mo.umac.db.DBInMemory;
import mo.umac.db.H2DB;
import mo.umac.metadata.APOI;
import mo.umac.paint.PaintShapes;
import mo.umac.paint.test.WindowUtilities;
import mo.umac.parser.Rating;
import mo.umac.utils.FileOperator;

import org.apache.log4j.xml.DOMConfigurator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class CrawlerTest extends CrawlerStrategy {

	public static String LOG_PROPERTY_PATH = "./src/main/resources/log4j.xml";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DOMConfigurator.configure(CrawlerTest.LOG_PROPERTY_PATH);
		CrawlerTest test = new CrawlerTest();
		PaintShapes.painting = true;
		WindowUtilities.openInJFrame(PaintShapes.paint, 1000, 1000);
		test.calling();

	}

	public void calling() {
		/************************* Change these lines *************************/
		CrawlerStrategy.CATEGORY_ID_PATH = "./src/main/resources/cat_id.txt";
		// YahooLocalCrawlerStrategy crawlerStrategy = new QuadTreeCrawler();
		// SliceCrawler crawler = new SliceCrawler();
		//HexagonCrawler2 crawler = new HexagonCrawler2();
		//PeripheryQuery crawler=new PeripheryQuery();
		//PerpheryQuery_Optimize crawler=new PerpheryQuery_Optimize();
		HexagonCrawler2_Modify crawler=new HexagonCrawler2_Modify();
		//BinaryCrawler crawler=new BinaryCrawler();
		//BinaryCrawler_Modify crawler=new BinaryCrawler_Modify();
		String state = "NY";
		int categoryID = 96926236;
		String category = "Restaurants";
		Envelope envelopeECEF = new Envelope(0, 1000, 0, 1000);
		//
		String testSource = "../crawler-data/yahoolocal-h2/test/1000";
		String testTarget = "../crawler-data/yahoolocal-h2/test/1000target";
		//
		int numItems = 1000;
		int topK = 10;
		CrawlerStrategy.MAX_TOTAL_RESULTS_RETURNED = topK;
		//
		CrawlerStrategy.categoryIDMap = FileOperator
				.readCategoryID(CATEGORY_ID_PATH);
		CrawlerStrategy.dbInMemory.poisCrawledTimes = new HashMap<Integer, Integer>();
		// source database
		CrawlerStrategy.dbExternal = new H2DB(testSource, testTarget);
		// generate dataset
		// List<Coordinate> points = generateSimpleCase(testSource, category,
		// state, numItems);
		// exportToH2(points, testSource, category, state);
		//
		CrawlerStrategy.dbInMemory = new DBInMemory();
		DBInMemory.pois = readFromGeneratedDB(testSource);
		//
		Iterator it2 = DBInMemory.pois.entrySet().iterator();
		while (it2.hasNext()) {
			Entry entry = (Entry) it2.next();
			APOI aPoint = (APOI) entry.getValue();
			Coordinate coordinate = aPoint.getCoordinate();
			PaintShapes.paint.addPoint(coordinate);
		}
		PaintShapes.paint.myRepaint();
		//
		CrawlerStrategy.dbInMemory.index();
		// target database
		CrawlerStrategy.dbExternal.createTables(testTarget);

		crawler.crawl(state, categoryID, category, envelopeECEF);

		logger.info("before updating");
		printExternalDB();
		CrawlerStrategy.dbInMemory.updataExternalDB();
		// testing updataExternalDB
		logger.info("after updating");
		printExternalDB();

		// close the connections
		OfflineStrategy.endData();

		logger.debug("Finished ! Oh ! Yeah! ");
		//logger.debug("number of queries issued = "
			//	+ CrawlerStrategy.countNumQueries);
		logger.debug("number of queries issued ="+HexagonCrawler2.countquery);
		logger.debug("number of points crawled = "
				+ CrawlerStrategy.dbInMemory.poisIDs.size());
		Set set = CrawlerStrategy.dbInMemory.poisIDs;
		Iterator<Integer> it = set.iterator();
		while (it.hasNext()) {
			int id = it.next();
			logger.debug(id);
		}

		logger.info("poisCrawledTimes:");
		Iterator it1 = CrawlerStrategy.dbInMemory.poisCrawledTimes.entrySet()
				.iterator();
		while (it1.hasNext()) {
			Entry entry = (Entry) it1.next();
			int poiID = (Integer) entry.getKey();
			int times = (Integer) entry.getValue();
			APOI aPOI = CrawlerStrategy.dbInMemory.pois.get(poiID);
			double longitude = aPOI.getCoordinate().x;
			double latitude = aPOI.getCoordinate().y;
			logger.info(poiID + ": " + times + ", " + "[" + longitude + ", "
					+ latitude + "]");
		}
	}

	/**
	 * Generate simple case, write them to the testSource database
	 */
	private List<Coordinate> generateSimpleCase(String testSource,
			String category, String state, int numItems) {
		double x = 1.0;
		double y = 1.0;
		List list = new ArrayList<Coordinate>();
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < numItems; i++) {
			x = random.nextDouble() * 1000;
			y = random.nextDouble() * 1000;
			Coordinate coordinate = new Coordinate(x, y);
			list.add(coordinate);
		}
		return list;
	}

	private HashMap<Integer, APOI> readFromGeneratedDB(String dbNameSource) {
		HashMap<Integer, APOI> map = new HashMap<Integer, APOI>();
		H2DB h2 = (H2DB) CrawlerStrategy.dbExternal;
		String dbName = CrawlerStrategy.dbExternal.dbNameSource;
		// TODO check sql
		try {
			Connection conn = h2.getConnection(dbName);
			Statement stat = conn.createStatement();

			String sql = "SELECT * FROM item";
			try {
				java.sql.ResultSet rs = stat.executeQuery(sql);
				while (rs.next()) {

					int itemID = rs.getInt(1);
					String title = rs.getString(2);
					String city = rs.getString(3);
					String state = rs.getString(4);

					double latitude = rs.getDouble(5);
					double longitude = rs.getDouble(6);
					double distance = rs.getDouble(7);

					double averageRating = rs.getDouble(8);
					double totalRating = rs.getDouble(9);
					double totalReviews = rs.getDouble(10);

					Rating rating = new Rating();
					rating.setAverageRating(averageRating);
					rating.setTotalRatings((int) totalRating);
					rating.setTotalReviews((int) totalReviews);

					int numCrawled = rs.getInt(11);
					//
					// print query result to console
					logger.debug("itemID: " + itemID);
					logger.debug("latitude: " + latitude);
					logger.debug("longitude: " + longitude);
					logger.debug("--------------------------");
					APOI poi = new APOI(itemID, title, city, state, longitude,
							latitude, rating, distance, null, numCrawled);
					map.put(itemID, poi);
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	public void printExternalDB() {
		H2DB h2 = (H2DB) CrawlerStrategy.dbExternal;
		String dbName = CrawlerStrategy.dbExternal.dbNameTarget;
		// TODO check sql
		try {
			Connection conn = h2.getConnection(dbName);
			Statement stat = conn.createStatement();

			String sql = "SELECT * FROM item";
			try {
				java.sql.ResultSet rs = stat.executeQuery(sql);
				while (rs.next()) {

					int itemID = rs.getInt(1);
					String title = rs.getString(2);
					String city = rs.getString(3);
					String state = rs.getString(4);

					double latitude = rs.getDouble(5);
					double longitude = rs.getDouble(6);
					double distance = rs.getDouble(7);

					double averageRating = rs.getDouble(8);
					double totalRating = rs.getDouble(9);
					double totalReviews = rs.getDouble(10);

					Rating rating = new Rating();
					rating.setAverageRating(averageRating);
					rating.setTotalRatings((int) totalRating);
					rating.setTotalReviews((int) totalReviews);

					int numCrawled = rs.getInt(11);
					//
					// print query result to console
					logger.debug("itemID: " + itemID);
					logger.debug("latitude: " + latitude);
					logger.debug("longitude: " + longitude);
					logger.debug("--------------------------");
					APOI poi = new APOI(itemID, title, city, state, longitude,
							latitude, rating, distance, null, numCrawled);
					logger.info(poi.toString());
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void exportToH2(List<Coordinate> points, String testSource,
			String category, String state) {
		HashMap<Integer, APOI> map = new HashMap<Integer, APOI>();

		H2DB h2 = (H2DB) CrawlerStrategy.dbExternal;
		String dbName = CrawlerStrategy.dbExternal.dbNameSource;
		try {
			Connection conn = h2.getConnection(dbName);
			Statement stat = conn.createStatement();
			// create table
			String sqlCreate = "CREATE TABLE IF NOT EXISTS ITEM "
					+ "(ITEMID INT PRIMARY KEY, TITLE VARCHAR(200), CITY VARCHAR(200), STATE VARCHAR(10), "
					+ "LATITUDE DOUBLE, LONGITUDE DOUBLE, DISTANCE DOUBLE, AVERAGERATING DOUBLE, TOTALRATINGS DOUBLE, TOTALREVIEWS DOUBLE, NUMCRAWLED INT)";
			;
			stat.execute(sqlCreate);
			stat.close();
			// import data
			String sqlInsert = "INSERT INTO ITEM (ITEMID, TITLE, CITY, STATE, "
					+ "LATITUDE, LONGITUDE, DISTANCE, AVERAGERATING, TOTALRATINGS, TOTALREVIEWS, NUMCRAWLED) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement prepItem = conn.prepareStatement(sqlInsert);
			for (int i = 0; i < points.size(); i++) {
				Coordinate coordinate = points.get(i);

				double longitude = coordinate.x;
				double latitude = coordinate.y;
				// table 1

				prepItem.setInt(1, i);
				prepItem.setString(2, "title");
				prepItem.setString(3, "city");
				prepItem.setString(4, state);
				prepItem.setDouble(5, latitude);
				prepItem.setDouble(6, longitude);
				prepItem.setDouble(7, 0);
				prepItem.setDouble(8, Rating.noAverageRatingValue);
				prepItem.setDouble(9, Rating.noAverageRatingValue);
				prepItem.setDouble(10, Rating.noAverageRatingValue);
				prepItem.setInt(11, 0);

				prepItem.addBatch();

			}
			prepItem.executeBatch();
			conn.commit();
			prepItem.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void crawlByCategoriesStates(
			LinkedList<Envelope> listEnvelopeStates,
			List<String> listCategoryNames, LinkedList<String> listNameStates,
			HashMap<Integer, String> categoryIDMap) {
		// TODO Auto-generated method stub

	}

}
