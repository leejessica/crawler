package mo.umac.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.online.YahooLocalQueryFileDB;
import mo.umac.geo.GeoOperator;
import mo.umac.parser.YahooResultSet;

/**
 * H2 dataset with spatial index
 * 
 * @author kate
 * 
 */
public class H2DBGeo extends DataSet {

    public final static String GEO_DB_NAME = "../yahoolocal-h2-geo/datasets";

    public static Connection conn;

    @Override
    public void record(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, YahooResultSet resultSet) {
	// TODO Auto-generated method stub

    }

    @Override
    public YahooResultSet query(AQuery qc) {
	// TODO Auto-generated method stub
	return null;
    }

    public static Connection getConnection() {
	try {
	    Class.forName("org.h2.Driver");
	    String connectString = "jdbc:h2:" + GEO_DB_NAME
		    + ";AUTO_SERVER=TRUE";
	    Connection conn = DriverManager.getConnection(connectString, null,
		    null);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return conn;
    }

    public static void createTable() {
	try {
	    String sql = "create table T1 (ID identity, NAME varchar (20), GEOM varbinary (200))";
	    conn.createStatement().executeUpdate(sql);
	    // ... populate with data, test etc
	    conn.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
