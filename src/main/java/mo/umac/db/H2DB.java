package mo.umac.db;

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
	    prepItem = con
		    .prepareStatement("INSERT INTO ITEM (ITEMID, TITLE, CITY, STATE, LATITUDE, LONGITUDE, DISTANCE, AVERAGERATING, TOTALRATINGS, TOTALREVIEWS) VALUES (?,?,?,?,?,?,?,?,?,?)");
	    prepCategory = con
		    .prepareStatement("INSERT INTO CATEGORY (ITEMID, CATEGORYID, CATEGORYNAME) VALUES (?,?,?)");
	    prepQuery = con
		    .prepareStatement("INSERT INTO QUERY (QUERYID, LATITUDE, LONGITUDE, RADIUS, LEVEL, PARENTID) VALUES (?,?,?,?,?,?)");
	    prepRelationship = con
		    .prepareStatement("INSERT INTO RELATIONSHIP (ITEMID, QEURYID, POSITION), VALUES(?,?,?)");
	    List<Result> results = resultSet.getResults();
	    for (int i = 0; i < results.size(); i++) {
		Result result = results.get(i);
		// table 1
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
		prepItem.addBatch();
		// table 2
		List<Category> listCategory = result.getCategories();
		for (int j = 0; j < listCategory.size(); j++) {
		    Category category = listCategory.get(j);
		    prepCategory.setInt(1, result.getId());
		    prepCategory.setInt(2, category.getId());
		    prepCategory.setString(3, category.getName());
		    prepCategory.addBatch();
		}
		// table 4
		prepRelationship.setInt(1, result.getId());
		prepRelationship.setInt(2, queryID);
		prepRelationship.setInt(3, i+1);
	    }
	    // table 3
	    prepQuery.setInt(1, queryID);
	    prepQuery.setDouble(2, qc.getCircle().getCenter().y);
	    prepQuery.setDouble(3, qc.getCircle().getCenter().x);
	    prepQuery.setDouble(4, qc.getCircle().getRadius());
	    prepQuery.setInt(5, level);
	    prepQuery.setInt(6, parentID);
	    prepQuery.addBatch();

	    con.setAutoCommit(false);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private void createTables() {
	String sqlQueryTable = sqlQueryTable();
	String sqlItemTable = sqlItemTable();
	String sqlCategoryTable = sqlCategoryTable();
	String sqlQRR = sqlQueryResultsRelationship();

	try {
	    Class.forName("org.h2.Driver");
	    Connection conn = DriverManager.getConnection(
		    "jdbc:h2:file:../yahoolocal-h2/datasets;AUTO_SERVER=TRUE",
		    "sa", "");
	    Statement stat = conn.createStatement();
	    stat.execute(sqlQueryTable);
	    stat.execute(sqlItemTable);
	    stat.execute(sqlCategoryTable);
	    stat.execute(sqlQRR);
	    stat.close();
	    conn.close();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private String sqlItemTable() {
	String sqlItemTable = "CREATE TABLE ITEM (ITEMID INT PRIMARY KEY, TITLE VARCHAR(200), CITY VARCHAR(200), STATE VARCHAR(10), LATITUDE DOUBLE, LONGITUDE DOUBLE, DISTANCE DOUBLE, AVERAGERATING Svar, TOTALRATINGS DOUBLE, TOTALREVIEWS DOUBLE)";
	return sqlItemTable;
    }

    private String sqlCategoryTable() {
	String sqlCategoryTable = "CREATE TABLE CATEGORY (ITEMID INT, CATEGORYID INT, CATEGORYNAME VARCHAR(200))";
	return sqlCategoryTable;
    }

    /**
     * level: the divided level radius: the radius of the circle want to covered
     * 
     * @return
     */
    private String sqlQueryTable() {
	String sqlQueryTable = "CREATE TABLE QUERY (QUERYID INT PRIMARY KEY, LATITUDE DOUBLE, LONGITUDE DOUBLE, RADIUS DOUBLE, LEVEL INT, PARENTID INT";
	return sqlQueryTable;
    }

    /**
     * This table records that the item is returned by which query in which
     * position.
     * 
     * @return
     */
    private String sqlQueryResultsRelationship() {
	String sqlQRR = "CREATE TABLE RELATIONSHIP (ITEMID INT, QEURYID INT, POSITION INT)";
	return sqlQRR;
    }

    /**
     * Transfer the plain text dataset to the in-memory dataset: H2
     * 
     * @param file
     * @param h2Name
     */
    private void PlainTextToH2(String file, String h2Name) {

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }

}
