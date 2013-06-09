package mo.umac.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mo.umac.crawler.YahooLocalQuery;
import mo.umac.parser.Category;
import mo.umac.parser.Rating;
import mo.umac.parser.Result;
import mo.umac.parser.ResultSet;

/**
 * Operators of the database
 * 
 * @author Kate
 * 
 */
public class H2DB extends DataSet {

    // sqls for creating table
    private String sqlCreateItemTable = "CREATE TABLE ITEM (ITEMID INT PRIMARY KEY, TITLE VARCHAR(200), CITY VARCHAR(200), STATE VARCHAR(10), LATITUDE DOUBLE, LONGITUDE DOUBLE, DISTANCE DOUBLE, AVERAGERATING Svar, TOTALRATINGS DOUBLE, TOTALREVIEWS DOUBLE)";
    private String sqlCreateCategoryTable = "CREATE TABLE CATEGORY (ITEMID INT, CATEGORYID INT, CATEGORYNAME VARCHAR(200))";
    /**
     * level: the divided level radius: the radius of the circle want to covered
     */
    private String sqlCreateQueryTable = "CREATE TABLE QUERY (QUERYID INT PRIMARY KEY, QUERY VARCHAR(100), ZIP INT, RESULTS INT, START INT, LATITUDE DOUBLE, LONGITUDE DOUBLE, RADIUS DOUBLE, LEVEL INT, PARENTID INT";

    /**
     * This table records that the item is returned by which query in which
     * position.
     */
    private String sqlCreateQRR = "CREATE TABLE RELATIONSHIP (ITEMID INT, QEURYID INT, POSITION INT)";

    // sqls preparation for insertion
    private String sqlPrepItem = "INSERT INTO ITEM (ITEMID, TITLE, CITY, STATE, LATITUDE, LONGITUDE, DISTANCE, AVERAGERATING, TOTALRATINGS, TOTALREVIEWS) VALUES (?,?,?,?,?,?,?,?,?,?)";

    private String sqlPrepCategory = "INSERT INTO CATEGORY (ITEMID, CATEGORYID, CATEGORYNAME) VALUES (?,?,?)";

    private String sqlPrepQuery = "INSERT INTO QUERY (QUERYID, QUERY, ZIP, RESULTS, START, LATITUDE, LONGITUDE, RADIUS, LEVEL, PARENTID) VALUES (?,?,?,?,?,??,?,?,?)";

    private String sqlPrepRelationship = "INSERT INTO RELATIONSHIP (ITEMID, QEURYID, POSITION), VALUES(?,?,?)";

    @Override
    public void record(int queryID, int level, int parentID,
	    YahooLocalQuery qc, ResultSet resultSet) {
	Connection con = qc.getCon();
	// prepared statement
	PreparedStatement prepItem;
	PreparedStatement prepCategory;
	PreparedStatement prepQuery;
	PreparedStatement prepRelationship;
	try {
	    prepItem = con.prepareStatement(sqlPrepItem);
	    prepCategory = con.prepareStatement(sqlPrepCategory);
	    prepQuery = con.prepareStatement(sqlPrepQuery);
	    prepRelationship = con.prepareStatement(sqlPrepRelationship);
	    List<Result> results = resultSet.getResults();
	    for (int i = 0; i < results.size(); i++) {
		Result result = results.get(i);
		// table 1
		setPrepItem(result, prepItem);
		prepItem.addBatch();
		// table 2
		List<Category> listCategory = result.getCategories();
		for (int j = 0; j < listCategory.size(); j++) {
		    Category category = listCategory.get(j);
		    setPrepCategory(result, category, prepCategory);
		    prepCategory.addBatch();
		}
		// table 4
		setPrepRelationship(result, queryID, i, prepRelationship);
		prepRelationship.addBatch();
	    }
	    // table 3
	    setPrepQuery(queryID, qc, level, parentID, prepQuery);
	    prepQuery.addBatch();

	    con.setAutoCommit(false);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private PreparedStatement setPrepItem(Result result,
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
	    prepItem.setString(8, rating.getAverageRating());
	    prepItem.setDouble(9, rating.getTotalRatings());
	    prepItem.setDouble(10, rating.getTotalReviews());
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepItem;
    }

    private PreparedStatement setPrepCategory(Result result, Category category,
	    PreparedStatement prepCategory) {
	try {
	    prepCategory.setInt(1, result.getId());
	    prepCategory.setInt(2, category.getId());
	    prepCategory.setString(3, category.getName());
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepCategory;
    }

    private PreparedStatement setPrepQuery(int queryID, YahooLocalQuery qc,
	    int level, int parentID, PreparedStatement prepQuery) {
	setPrepQuery(queryID, qc.getQuery(), qc.getZip(), qc.getResults(),
		qc.getStart(), qc.getCircle().getCenter().y, qc.getCircle()
			.getCenter().x, qc.getCircle().getRadius(), level,
		parentID, prepQuery);
	return prepQuery;
    }

    private PreparedStatement setPrepQuery(int queryID, String query, int zip,
	    int results, int start, double latitude, double longitude,
	    double radius, int level, int parentID, PreparedStatement prepQuery) {
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
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepQuery;
    }

    private PreparedStatement setPrepRelationship(Result result, int queryID,
	    int position, PreparedStatement prepRelationship) {
	try {
	    prepRelationship.setInt(1, result.getId());
	    prepRelationship.setInt(2, queryID);
	    prepRelationship.setInt(3, position + 1);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepRelationship;
    }

    private void createTables() {
	try {
	    Class.forName("org.h2.Driver");
	    Connection conn = DriverManager.getConnection(
		    "jdbc:h2:file:../yahoolocal-h2/datasets;AUTO_SERVER=TRUE",
		    "sa", "");
	    Statement stat = conn.createStatement();
	    stat.execute(sqlCreateQueryTable);
	    stat.execute(sqlCreateItemTable);
	    stat.execute(sqlCreateCategoryTable);
	    stat.execute(sqlCreateQRR);
	    stat.close();
	    conn.close();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Transfer the plain text dataset to the in-memory dataset: H2
     * 
     * @param folderPath
     * @param h2Name
     */
    private void convertFileDBToH2DB(String folderPath, String h2Name) {
	// looking for files: results and query
	String queryFile = folderPath + "query";
	String resultsFile = folderPath + "results";

	// converting query file
	BufferedReader br = null;
	try {
	    Class.forName("org.h2.Driver");
	    Connection conn = DriverManager.getConnection(
		    "jdbc:h2:file:../yahoolocal-h2/datasets;AUTO_SERVER=TRUE",
		    "sa", "");
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(
		    queryFile)));
	    String data = null;
	    String[] split;
	    int queryID;
	    int indexHyphenm;
	    int indexDot;
	    while ((data = br.readLine()) != null) {
		data = data.trim();
		split = data.split(";");
		// query id
		String queryIDString = split[0];
		indexHyphenm = queryIDString.indexOf("-");
		if (indexHyphenm != -1) {
		    queryIDString = queryIDString.substring(0, indexHyphenm);
		    queryID = Integer.parseInt(queryIDString);
		} else {
		    indexDot = queryIDString.indexOf(".xml");
		    queryIDString = queryIDString.substring(0, indexDot);
		    queryID = Integer.parseInt(queryIDString);
		}
		// query Info
		String query = split[1];
		int zip = Integer.parseInt(split[2]);
		int results = Integer.parseInt(split[3]);
		int start = Integer.parseInt(split[4]);
		double latitude = Double.parseDouble(split[5]);
		double longitude = Double.parseDouble(split[6]);
		double radius = Double.parseDouble(split[7]);
		int level = -1;
		int parentID = -1;

		// begin to prepare the statements
		try {
		    PreparedStatement prepQuery = conn
			    .prepareStatement(sqlPrepQuery);
		    setPrepQuery(queryID, query, zip, results, start, latitude,
			    longitude, radius, level, parentID, prepQuery);
		    prepQuery.addBatch();
		    //
		    PreparedStatement prepItem = conn.prepareStatement(sqlPrepItem);
		    setPrepItem(result, prepItem);
		    
		    
		    PreparedStatement prepCategory;
		    PreparedStatement prepRelationship;
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	    }
	    br.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e1) {
	    e1.printStackTrace();
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}
    }
}
