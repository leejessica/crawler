package mo.umac.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
	// H2DB h2 = new H2DB();
	// String folderPath = "../yahoolocal/96926236+Restaurants/NY/";
	// String h2Name = "";
	// String folderPath2 = "../yahoolocal/96926236+Restaurants/NY/";
	// String h2Name = ;
	// h2.convertFileDBToH2DB(folderPath, h2Name);
	// h2.examData();
	Main main = new Main();
	// main.convertFromH2ToPostgresql();
    }

    private void convertFromH2ToPostgresql() {
	String sqlSelectItem = H2DB.sqlSelectStar + H2DB.ITEM;
	try {
	    Class.forName("org.h2.Driver");
	    Connection connH2 = DriverManager.getConnection(
		    "jdbc:h2:file:../yahoolocal-h2/datasets;AUTO_SERVER=TRUE",
		    "sa", "");
	    Postgresql post = new Postgresql();
	    Statement stat = connH2.createStatement();
	    //
	    Connection connPostgresql = post.connect(Postgresql.DB_NAME);
	    PreparedStatement prepItem = null;
	    connPostgresql.setAutoCommit(false);
	    prepItem = connPostgresql
		    .prepareStatement(Postgresql.sqlPrepInsertItem);
	    int i = 0;
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

		    i++;
		    System.out.println(i);
		    //
		    post.setPrepItemSimple(itemID, title, city, state,
			    longitude, latitude, distance, averageRating,
			    totalRating, totalReviews, prepItem);
		    prepItem.addBatch();
		    if (i % 1000 == 0) {
			prepItem.executeBatch();
		    }
		    //
		}
		prepItem.executeBatch();
		connPostgresql.commit();

		rs.close();
		prepItem.close();
		connPostgresql.close();
		stat.close();
		connH2.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
}
