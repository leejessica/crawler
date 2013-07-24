package mo.umac.db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import mo.umac.crawler.CrawlerStrategy;
import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.DefaultValues;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.ResultSetYahooOnline;
import mo.umac.metadata.YahooLocalQueryFileDB;
import mo.umac.parser.Category;
import mo.umac.parser.Rating;
import mo.umac.spatial.ECEFLLA;
import mo.umac.utils.CommonUtils;

/**
 * Operators of the database
 * 
 * @author Kate
 * 
 */
public class H2DB extends DBExternal {

    public final static String DB_NAME_SOURCE = "../yahoolocal-h2/source/datasets";

    public final static String DB_NAME_TARGET = "../yahoolocal-h2/target/datasets";

    // table names
    private final String QUERY = "QUERY";
    public final static String ITEM = "ITEM";
    private final String CATEGORY = "CATEGORY";
    private final String RELATIONSHIP = "RELATIONSHIP";

    public H2DB() {
	super();
	super.dbNameSource = DB_NAME_SOURCE;
	super.dbNameTarget = DB_NAME_TARGET;
    }

    public H2DB(String dbNameSource, String dbNameTarget) {
	super();
	super.dbNameSource = dbNameSource;
	super.dbNameTarget = dbNameTarget;
    }

    /****************************** sqls for creating table ******************************/
    /**
     * level: the divided level radius: the radius of the circle want to covered
     */
    private String sqlCreateQueryTable = "CREATE TABLE IF NOT EXISTS QUERY "
	    + "(QUERYID INT PRIMARY KEY, QUERY VARCHAR(100), ZIP INT, RESULTS INT, START INT, "
	    + "LATITUDE DOUBLE, LONGITUDE DOUBLE, RADIUS DOUBLE, LEVEL INT, PARENTID INT, "
	    + "TOTALRESULTSAVAILABLE INT, TOTALRESULTSRETURNED INT, FIRSTRESULTPOSITION INT)";
    private String sqlCreateItemTable = "CREATE TABLE IF NOT EXISTS ITEM "
	    + "(ITEMID INT PRIMARY KEY, TITLE VARCHAR(200), CITY VARCHAR(200), STATE VARCHAR(10), "
	    + "LATITUDE DOUBLE, LONGITUDE DOUBLE, DISTANCE DOUBLE, "
	    + "AVERAGERATING DOUBLE, TOTALRATINGS DOUBLE, TOTALREVIEWS DOUBLE)";
    private String sqlCreateCategoryTable = "CREATE TABLE IF NOT EXISTS CATEGORY (ITEMID INT, "
	    + "CATEGORYID INT, CATEGORYNAME VARCHAR(200), CONSTRAINT pk_itemCategory PRIMARY KEY (ITEMID,CATEGORYID))";

    /**
     * This table records that the item is returned by which query in which
     * position.
     */
    private String sqlCreateRelationshipTable = "CREATE TABLE IF NOT EXISTS RELATIONSHIP "
	    + "(ITEMID INT, QEURYID INT, POSITION INT, CONSTRAINT pk_itemquery PRIMARY KEY (ITEMID,QEURYID))";

    /****************************** sqls preparation for insertion ******************************/
    private String sqlPrepInsertItem = "INSERT INTO ITEM (ITEMID, TITLE, CITY, STATE, "
	    + "LATITUDE, LONGITUDE, DISTANCE, AVERAGERATING, TOTALRATINGS, TOTALREVIEWS) VALUES (?,?,?,?,?,?,?,?,?,?)";

    private String sqlPrepInsertCategory = "INSERT INTO CATEGORY (ITEMID, CATEGORYID, CATEGORYNAME) VALUES (?,?,?)";

    private String sqlPrepInsertQuery = "INSERT INTO QUERY (QUERYID, QUERY, ZIP, RESULTS, START, "
	    + "LATITUDE, LONGITUDE, RADIUS, LEVEL, PARENTID, "
	    + "TOTALRESULTSAVAILABLE ,TOTALRESULTSRETURNED, FIRSTRESULTPOSITION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private String sqlPrepInsertRelationship = "INSERT INTO RELATIONSHIP (ITEMID, QEURYID, POSITION) VALUES(?,?,?)";

    /**
     * sql for select all data from a table. Need concatenate the table's names.
     */
    public static String sqlSelectStar = "SELECT * FROM ";

    private String sqlSelectCountStar = "SELECT COUNT(*) FROM ";

    @Override
    public void writeToExternalDB(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, ResultSetYahooOnline resultSet) {
	Connection con = qc.getCon();
	// prepared statement
	PreparedStatement prepItem;
	PreparedStatement prepCategory;
	PreparedStatement prepQuery;
	PreparedStatement prepRelationship;
	try {
	    prepItem = con.prepareStatement(sqlPrepInsertItem);
	    prepCategory = con.prepareStatement(sqlPrepInsertCategory);
	    prepQuery = con.prepareStatement(sqlPrepInsertQuery);
	    prepRelationship = con.prepareStatement(sqlPrepInsertRelationship);
	    List<APOI> results = resultSet.getPOIs();
	    for (int i = 0; i < results.size(); i++) {
		APOI result = results.get(i);
		// table 1
		setPrepItem(result, prepItem);
		prepItem.addBatch();
		// table 2
		List<Category> listCategory = result.getCategories();
		for (int j = 0; j < listCategory.size(); j++) {
		    Category category = listCategory.get(j);
		    setPrepCategory(result.getId(), category, prepCategory);
		    prepCategory.addBatch();
		}
		// table 4
		setPrepRelationship(result.getId(), queryID, i + 1,
			prepRelationship);
		prepRelationship.addBatch();
	    }
	    // table 3
	    setPrepQuery(queryID, qc, level, parentID,
		    resultSet.getTotalResultsAvailable(),
		    resultSet.getTotalResultsReturned(),
		    resultSet.getFirstResultPosition(), prepQuery);
	    prepQuery.addBatch();

	    con.setAutoCommit(false);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void writeToExternalDB(int queryID, AQuery aQuery,
	    ResultSet resultSet) {
	String dbName = dbNameTarget;
	Connection con = connect(dbName);
	//
	// prepared statement
	PreparedStatement prepItem;
	PreparedStatement prepCategory;
	PreparedStatement prepQuery;
	PreparedStatement prepRelationship;
	try {
	    con.setAutoCommit(false);

	    prepItem = con.prepareStatement(sqlPrepInsertItem);
	    prepCategory = con.prepareStatement(sqlPrepInsertCategory);
	    prepQuery = con.prepareStatement(sqlPrepInsertQuery);
	    prepRelationship = con.prepareStatement(sqlPrepInsertRelationship);

	    List<APOI> results = resultSet.getPOIs();
	    double longitude = aQuery.getPoint().x;
	    double latitude = aQuery.getPoint().y;
	    String query = aQuery.getQuery();
	    int totalResultsAvailable = resultSet.getTotalResultsAvailable();
	    int totalResultsReturned = resultSet.getTotalResultsReturned();
	    int firstResultPosition = DefaultValues.INIT_INT;
	    double radius = DefaultValues.INIT_DOUBLE;

	    for (int i = 0; i < results.size(); i++) {
		APOI point = results.get(i);
		// table 1
		setPrepItem(point, prepItem);
		prepItem.addBatch();
		// table 2
		List<Category> listCategory = point.getCategories();
		if (listCategory != null) {
		    for (int j = 0; j < listCategory.size(); j++) {
			Category category = listCategory.get(j);
			setPrepCategory(point.getId(), category, prepCategory);
			prepCategory.addBatch();
		    }
		}
		// table 4
		setPrepRelationship(point.getId(), queryID, i + 1,
			prepRelationship);
		prepRelationship.addBatch();
	    }
	    // table 3
	    setPrepQuery(queryID, query, DefaultValues.INIT_INT,
		    DefaultValues.INIT_INT, DefaultValues.INIT_INT, latitude,
		    longitude, radius, DefaultValues.INIT_INT,
		    DefaultValues.INIT_INT, totalResultsAvailable,
		    totalResultsReturned, firstResultPosition, prepQuery);
	    prepQuery.addBatch();

	    prepItem.executeBatch();
	    prepCategory.executeBatch();
	    prepRelationship.executeBatch();
	    prepQuery.executeBatch();

	    con.commit();
	    prepItem.close();
	    prepCategory.close();
	    prepQuery.close();
	    prepRelationship.close();
	    con.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    /**
     * Exam whether data has been successfully inserted to the database
     */
    public void examData(String dbName) {
	// print
	printQueryTable(dbName);
	printItemTable(dbName);
	printCategoryTable(dbName);
	printRelationshipTable(dbName);
	// count
	int c1 = count(dbName, QUERY);
	System.out.println("count QUERY = " + c1);
	int c2 = count(dbName, ITEM);
	System.out.println("count ITEM = " + c2);
	int c3 = count(dbName, CATEGORY);
	System.out.println("count CATEGORY = " + c3);
	int c4 = count(dbName, RELATIONSHIP);
	System.out.println("count RELATIONSHIP = " + c4);
    }

    private void printQueryTable(String dbName) {
	String sqlSelectQuery = sqlSelectStar + QUERY;
	try {
	    Connection conn = connect(dbName);
	    Statement stat = conn.createStatement();
	    try {
		java.sql.ResultSet rs = stat.executeQuery(sqlSelectQuery);
		while (rs.next()) {

		    int queryID = rs.getInt(1);
		    String query = rs.getString(2);
		    int zip = rs.getInt(3);
		    int results = rs.getInt(4);
		    int start = rs.getInt(5);
		    double latitude = rs.getDouble(6);
		    double longitude = rs.getDouble(7);
		    double radius = rs.getDouble(8);
		    int level = rs.getInt(9);
		    int parentID = rs.getInt(10);
		    int totalResultsAvailable = rs.getInt(11);
		    int totalResultsReturned = rs.getInt(12);
		    int firstResultPosition = rs.getInt(13);

		    // print query result to console
		    System.out.println("queryID: " + queryID);
		    System.out.println("query: " + query);
		    System.out.println("zip: " + zip);
		    System.out.println("results: " + results);
		    System.out.println("start: " + start);
		    System.out.println("latitude: " + latitude);
		    System.out.println("longitude: " + longitude);
		    System.out.println("radius: " + radius);
		    System.out.println("level: " + level);
		    System.out.println("parentID: " + parentID);
		    System.out.println("totalResultsAvailable: "
			    + totalResultsAvailable);
		    System.out.println("totalResultsReturned: "
			    + totalResultsReturned);
		    System.out.println("firstResultPosition: "
			    + firstResultPosition);
		    System.out.println("--------------------------");
		}
		rs.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    stat.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private void printItemTable(String dbName) {
	String sqlSelectItem = sqlSelectStar + ITEM;
	try {
	    Connection conn = connect(dbName);
	    Statement stat = conn.createStatement();
	    try {
		java.sql.ResultSet rs = stat.executeQuery(sqlSelectItem);
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

		    // print query result to console
		    System.out.println("itemID: " + itemID);
		    System.out.println("title: " + title);
		    System.out.println("city: " + city);
		    System.out.println("state: " + state);
		    System.out.println("latitude: " + latitude);
		    System.out.println("longitude: " + longitude);
		    System.out.println("distance: " + distance);
		    System.out.println("averageRating: " + averageRating);
		    System.out.println("totalRating: " + totalRating);
		    System.out.println("totalReviews: " + totalReviews);
		    System.out.println("--------------------------");
		}
		rs.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    stat.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private void printCategoryTable(String dbName) {
	String sqlSelectCategory = sqlSelectStar + CATEGORY;
	try {
	    Connection conn = connect(dbName);
	    Statement stat = conn.createStatement();
	    try {
		java.sql.ResultSet rs = stat.executeQuery(sqlSelectCategory);
		while (rs.next()) {

		    int itemID = rs.getInt(1);
		    int categoryID = rs.getInt(2);
		    String categoryName = rs.getString(3);

		    // print query result to console
		    System.out.println("itemID: " + itemID);
		    System.out.println("categoryID: " + categoryID);
		    System.out.println("categoryName: " + categoryName);
		    System.out.println("--------------------------");
		}
		rs.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    stat.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private void printRelationshipTable(String dbName) {
	String sqlSelectRelationship = sqlSelectStar + RELATIONSHIP;
	try {
	    Connection conn = connect(dbName);
	    Statement stat = conn.createStatement();
	    try {
		java.sql.ResultSet rs = stat
			.executeQuery(sqlSelectRelationship);
		while (rs.next()) {

		    int itemID = rs.getInt(1);
		    int queryID = rs.getInt(2);
		    int position = rs.getInt(3);

		    // print query result to console
		    System.out.println("itemID: " + itemID);
		    System.out.println("queryID: " + queryID);
		    System.out.println("position: " + position);
		    System.out.println("--------------------------");
		}
		rs.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    stat.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private int count(String dbName, String tableName) {
	int count = 0;
	String sql = sqlSelectCountStar + tableName;
	try {
	    Connection conn = connect(dbName);
	    Statement stat = conn.createStatement();
	    try {
		java.sql.ResultSet rs = stat.executeQuery(sql);
		while (rs.next()) {

		    count = rs.getInt(1);
		}
		rs.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    stat.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return count;
    }

    private PreparedStatement setPrepItem(APOI result,
	    PreparedStatement prepItem) {
	try {
	    prepItem.setInt(1, result.getId());
	    prepItem.setString(2, result.getTitle());
	    prepItem.setString(3, result.getCity());
	    prepItem.setString(4, result.getState());
	    prepItem.setDouble(5, result.getLatitude());
	    prepItem.setDouble(6, result.getLongitude());
	    prepItem.setDouble(7, result.getDistance());
	    Rating rating = result.getRating();
	    if (rating != null) {
		prepItem.setDouble(8, rating.getAverageRating());
		prepItem.setDouble(9, rating.getTotalRatings());
		prepItem.setDouble(10, rating.getTotalReviews());
	    } else {
		prepItem.setDouble(8, Rating.noAverageRatingValue);
		prepItem.setDouble(9, Rating.noAverageRatingValue);
		prepItem.setDouble(10, Rating.noAverageRatingValue);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepItem;
    }

    private PreparedStatement setPrepCategory(int resultID, Category category,
	    PreparedStatement prepCategory) {
	try {
	    prepCategory.setInt(1, resultID);
	    prepCategory.setInt(2, category.getId());
	    prepCategory.setString(3, category.getName());
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepCategory;
    }

    private PreparedStatement setPrepQuery(int queryID,
	    YahooLocalQueryFileDB qc, int level, int parentID,
	    int totalResultsAvailable, int totalResultsReturned,
	    int firstResultPosition, PreparedStatement prepQuery) {
	setPrepQuery(queryID, qc.getQuery(), qc.getZip(), qc.getResults(),
		qc.getStart(), qc.getCircle().getCenter().y, qc.getCircle()
			.getCenter().x, qc.getCircle().getRadius(), level,
		parentID, totalResultsAvailable, totalResultsReturned,
		firstResultPosition, prepQuery);
	return prepQuery;
    }

    private PreparedStatement setPrepQuery(int queryID, String query, int zip,
	    int results, int start, double latitude, double longitude,
	    double radius, int level, int parentID, int totalResultsAvailable,
	    int totalResultsReturned, int firstResultPosition,
	    PreparedStatement prepQuery) {
	try {
	    prepQuery.setInt(1, queryID);
	    prepQuery.setString(2, query);
	    prepQuery.setInt(3, zip);
	    prepQuery.setInt(4, results);
	    prepQuery.setInt(5, start);
	    prepQuery.setDouble(6, latitude);
	    prepQuery.setDouble(7, longitude);
	    prepQuery.setDouble(8, radius);
	    prepQuery.setInt(9, level);
	    prepQuery.setInt(10, parentID);
	    prepQuery.setInt(11, totalResultsAvailable);
	    prepQuery.setInt(12, totalResultsReturned);
	    prepQuery.setInt(13, firstResultPosition);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepQuery;
    }

    private PreparedStatement setPrepRelationship(int resultID, int queryID,
	    int position, PreparedStatement prepRelationship) {
	try {
	    prepRelationship.setInt(1, resultID);
	    prepRelationship.setInt(2, queryID);
	    prepRelationship.setInt(3, position);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepRelationship;
    }

    @Override
    public void createTables(String dbName) {
	try {
	    Connection conn = connect(dbName);
	    Statement stat = conn.createStatement();
	    stat.execute(sqlCreateQueryTable);
	    stat.execute(sqlCreateItemTable);
	    stat.execute(sqlCreateCategoryTable);
	    stat.execute(sqlCreateRelationshipTable);
	    stat.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public Connection connect(String dbname) {
	Connection conn = null;
	try {
	    Class.forName("org.h2.Driver");
	    conn = DriverManager.getConnection("jdbc:h2:file:" + dbname
		    + ";MVCC=true;AUTO_SERVER=TRUE", "sa", "");
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return conn;
    }

    /**
     * Transfer the plain text dataset to the h2 dataset
     * 
     * @param folderPath
     * @param h2Name
     *            : not in use
     */
    public void convertFileDBToH2DB(String folderPath, String h2Name) {
	createTables(dbNameSource);
	convertQueryFile(folderPath, h2Name);
	convertResultsFile(folderPath, h2Name);
    }

    private void convertQueryFile(String folderPath, String h2Name) {
	String queryFile = folderPath + "query";
	BufferedReader brQuery = null;
	try {
	    Connection conn = connect(dbNameSource);
	    brQuery = new BufferedReader(new InputStreamReader(
		    new FileInputStream(queryFile)));
	    String data = null;
	    String[] split;
	    PreparedStatement prepQuery = conn
		    .prepareStatement(sqlPrepInsertQuery);
	    while ((data = brQuery.readLine()) != null) {
		data = data.trim();
		split = data.split(";");
		// query id
		String queryIDString = split[0];
		int queryID = parseID(queryIDString);

		// query Info
		String query = split[1];
		int zip = Integer.parseInt(split[2]);
		int results = Integer.parseInt(split[3]);
		int start = Integer.parseInt(split[4]);
		double latitude = Double.parseDouble(split[5]);
		double longitude = Double.parseDouble(split[6]);
		double radius = Double.parseDouble(split[7]);
		int totalResultsAvailable = Integer.parseInt(split[8]);
		int totalResultsReturned = Integer.parseInt(split[9]);
		int firstResultPosition = Integer.parseInt(split[10]);
		// additional information not download by previous data
		int level = -1;
		int parentID = -1;

		setPrepQuery(queryID, query, zip, results, start, latitude,
			longitude, radius, level, parentID,
			totalResultsAvailable, totalResultsReturned,
			firstResultPosition, prepQuery);
		prepQuery.addBatch();

	    }
	    prepQuery.executeBatch();
	    brQuery.close();
	    conn.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}
    }

    private void convertResultsFile(String folderPath, String h2Name) {
	String resultsFile = folderPath + "results";
	BufferedReader brResult = null;
	try {
	    Connection conn = connect(dbNameSource);
	    brResult = new BufferedReader(new InputStreamReader(
		    new FileInputStream(resultsFile)));
	    String data = null;
	    String[] split;
	    int position = 1;
	    PreparedStatement prepCategory = conn
		    .prepareStatement(sqlPrepInsertCategory);
	    PreparedStatement prepItem = conn
		    .prepareStatement(sqlPrepInsertItem);
	    PreparedStatement prepRelationship = conn
		    .prepareStatement(sqlPrepInsertRelationship);
	    while ((data = brResult.readLine()) != null) {
		try {
		    data = data.trim();
		    split = data.split(";");
		    int queryID = parseID(split[0]);
		    int itemID = Integer.parseInt(split[1]);
		    String title = split[2];
		    // for Dominos#39;s
		    if (title.equals("Dominos#39")) {
			title = "Dominos#39;s";
			String city = split[4];
			String state = split[5];
			double latitude = Double.parseDouble(split[6]);
			double longitude = Double.parseDouble(split[7]);
			double distance = Double.parseDouble(split[8]);
			List<Category> categories = new ArrayList<Category>();
			for (int i = 9; i < split.length; i = i + 2) {
			    // prepare category
			    Category category = new Category(
				    Integer.parseInt(split[i]), split[i + 1]);
			    categories.add(category);

			    setPrepCategory(itemID, category, prepCategory);
			    prepCategory.addBatch();
			}
			APOI result = new APOI(itemID, title, city, state,
				longitude, latitude, null, distance, categories);

			setPrepItem(result, prepItem);
			prepItem.addBatch();
		    } else {
			String city = split[3];
			String state = split[4];
			double latitude = Double.parseDouble(split[5]);
			double longitude = Double.parseDouble(split[6]);
			double distance = Double.parseDouble(split[7]);
			List<Category> categories = new ArrayList<Category>();
			for (int i = 8; i < split.length; i = i + 2) {
			    // prepare category
			    Category category = new Category(
				    Integer.parseInt(split[i]), split[i + 1]);
			    categories.add(category);

			    setPrepCategory(itemID, category, prepCategory);
			    prepCategory.addBatch();
			}
			APOI result = new APOI(itemID, title, city, state,
				longitude, latitude, null, distance, categories);
			setPrepItem(result, prepItem);
			prepItem.addBatch();
		    }
		    //
		    setPrepRelationship(itemID, queryID, position++,
			    prepRelationship);
		    prepRelationship.addBatch();
		} catch (Exception e) {
		    System.out.println(data);
		}
	    }
	    // execute prepare statements...
	    prepCategory.executeBatch();
	    prepItem.executeBatch();
	    prepRelationship.executeBatch();

	    brResult.close();
	    conn.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}
    }

    private int parseID(String queryIDString) {
	int queryID;
	int indexHyphenm;
	int indexDot;
	indexHyphenm = queryIDString.indexOf("-");
	if (indexHyphenm != -1) {
	    queryIDString = queryIDString.substring(0, indexHyphenm);
	    queryID = Integer.parseInt(queryIDString);
	} else {
	    indexDot = queryIDString.indexOf(".xml");
	    queryIDString = queryIDString.substring(0, indexDot);
	    queryID = Integer.parseInt(queryIDString);
	}
	return queryID;
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub

    }

    @Override
    public HashMap<Integer, APOI> readFromExtenalDB(String categoryQ,
	    String stateQ) {
	HashMap<Integer, APOI> map = new HashMap<Integer, APOI>();
	// TODO check sql
	try {
	    Connection conn = connect(dbNameSource);
	    Statement stat = conn.createStatement();

	    String sql = "SELECT * FROM item where state = '"
		    + stateQ
		    + "' and itemid in (select ITEMID from CATEGORY where CATEGORYNAME = '"
		    + categoryQ + "')";
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
		    //
		    List<Category> categories = new ArrayList<Category>();
		    Object searchingResult = CommonUtils.getKeyByValue(
			    CrawlerStrategy.categoryIDMap, categoryQ);
		    if (searchingResult != null) {
			int categoryID = (Integer) searchingResult;
			new ArrayList<Category>();
			Category category = new Category(categoryID, categoryQ);
			categories.add(category);
		    }
		    // TODO need check
		    // transfer from lla to ecef
		    Coordinate lla = new Coordinate(longitude, latitude);
		    Coordinate ecef = ECEFLLA.lla2ecef(lla);
		    longitude = ecef.x;
		    latitude = ecef.y;
		    // transfer from miles to meters
		    distance = 1609.34 * distance;

		    APOI poi = new APOI(itemID, title, city, state, longitude,
			    latitude, rating, distance, categories);
		    map.put(itemID, poi);
		}
		rs.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    stat.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return map;
    }

    @Override
    public int numCrawlerPoints() {
	int c = count(dbNameTarget, ITEM);
	return c;
    }

    // @Override
    // public ResultSet queryByID(List<Integer> resultsID) {
    // String dbname = dbNameSource;
    // Connection conn = connect(dbname);
    // ResultSet resultSet = new ResultSet();
    // List<APOI> pois = new ArrayList<APOI>();
    // String sql = sqlSelectStar + ITEM + " where itemid = ";
    //
    // try {
    //
    // Statement stat = conn.createStatement();
    // for (int i = 0; i < resultsID.size(); i++) {
    // int id = resultsID.get(i);
    // String sql2 = sql + id;
    // java.sql.ResultSet rs = stat.executeQuery(sql2);
    // while (rs.next()) {
    //
    // int itemID = rs.getInt(1);
    // String title = rs.getString(2);
    // String city = rs.getString(3);
    // String state = rs.getString(4);
    //
    // double latitude = rs.getDouble(5);
    // double longitude = rs.getDouble(6);
    // double distance = rs.getDouble(7);
    //
    // double averageRating = rs.getDouble(8);
    // double totalRating = rs.getDouble(9);
    // double totalReviews = rs.getDouble(10);
    //
    // Rating rating = new Rating();
    // rating.setAverageRating(averageRating);
    // rating.setTotalRatings((int) totalRating);
    // rating.setTotalReviews((int) totalReviews);
    //
    //
    // //
    // APOI point = new APOI(itemID, title, city, state,
    // longitude, latitude, rating, distance, null);
    // pois.add(point);
    // // print query result to console
    // // System.out.println("itemID: " + itemID);
    // // System.out.println("title: " + title);
    // // System.out.println("city: " + city);
    // // System.out.println("state: " + state);
    // // System.out.println("latitude: " + latitude);
    // // System.out.println("longitude: " + longitude);
    // // System.out.println("distance: " + distance);
    // // System.out.println("averageRating: " + averageRating);
    // // System.out.println("totalRating: " + totalRating);
    // // System.out.println("totalReviews: " + totalReviews);
    // // System.out.println("--------------------------");
    // }
    // rs.close();
    // }
    // resultSet.setPOIs(pois);
    // stat.close();
    // conn.close();
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // return resultSet;
    //
    // }

}
