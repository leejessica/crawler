package mo.umac.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mo.umac.crawler.online.YahooLocalQueryFileDB;
import mo.umac.parser.POI;
import mo.umac.parser.Rating;
import mo.umac.parser.YahooResultSet;

import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.geometric.PGcircle;
import org.postgresql.geometric.PGlseg;
import org.postgresql.geometric.PGpoint;

public class PostGIS {

    public Connection conn;

    // public final String DB_NAME = "../yahoolocal-h2/datasets";

    // sqls for creating table
    /**
     * level: the divided level radius: the radius of the circle want to covered
     */
    private String sqlCreateItemTable0 = "create table if not exists item (itemid int primary key, title varchar(200), city varchar(200), state varchar(10), longitude float, latitude float, distance float, averagerating real, totalratings real, totalreviews real)";
    /**
     * A geographic spatial reference system, from data downloaded from USCensus
     */
    private final int SRID_GEOGRAPHY_US = 4296;

    private final int SRID_GEOMETRY = 4326;
    private String sqlAddGeom = "SELECT AddGeometryColumn ('item','geom',4296,'POINT',2)";
    private String sqlPoint = "ST_SetSRID(ST_MakePoint(";

    // sqls preparation for insertion
    // private String sqlPrepInsertItem =
    // "insert into item (itemid, title, city, state, longitude, latitude, distance, averagerating, totalratings, totalreviews, geom) values (?,?,?,?,?,?,?,?,?,?,?)";
    private String sqlPrepInsertItem = "insert into item (itemid, title, city, state, longitude, latitude, distance, averagerating, totalratings, totalreviews, geom) values (?,?,?,?,?,?,?,?,?,?,ST_SetSRID(ST_MakePoint(?, ?), 4326))";

    /**
     * sql for select all data from a table. Need concatenate the table's names.
     */
    private String sqlSelectStar = "SELECT * FROM ";

    private String sqlSelectCountStar = "SELECT COUNT(*) FROM ";

    private int topK = 20;

    private String sqlKnn = "SELECT itemid FROM item ORDER BY geom <-> st_setsrid(st_makepoint(?,?),4326) LIMIT "
	    + topK;

    public static void main(String[] args) {
	PostGIS postGIS = new PostGIS();
	// postGIS.createTable();
	// postGIS.insert();
	postGIS.select();
    }

    public Connection connect() {
	/*
	 * Load the JDBC driver and establish a connection.
	 */
	try {
	    Class.forName("org.postgresql.Driver");
	    String url = "jdbc:postgresql://localhost:5432/yahoo";
	    conn = DriverManager.getConnection(url, "postgres", "postgres");
	    /*
	     * Add the geometry types to the connection. Note that you must cast
	     * the connection to the pgsql-specific connection implementation
	     * before calling the addDataType() method.
	     */
	    // ((org.postgresql.Connection) conn).addDataType("geometry",
	    // "org.postgis.PGgeometry");
	    // ((org.postgresql.Connection) conn).addDataType("box3d",
	    // "org.postgis.PGbox3d");
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return conn;
    }

    public void createTable() {
	Connection conn = connect();
	Statement stat;
	try {
	    stat = conn.createStatement();
	    stat.execute(sqlCreateItemTable0);
	    stat.execute(sqlAddGeom);
	    stat.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void insert() {
	Connection conn = connect();
	PreparedStatement prepItem;
	try {
	    prepItem = conn.prepareStatement(sqlPrepInsertItem);
	    setPrepItemSimple(2, "sampletitle", "samplecity", "ss", -71.060316,
		    48.432044, 0.3, 4.5, 3, 5, prepItem);
	    // prepItem.addBatch();
	    prepItem.executeUpdate();
	    prepItem.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void importData(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, YahooResultSet resultSet) {
	Connection con = connect();
	// prepared statement
	PreparedStatement prepItem;
	try {
	    prepItem = con.prepareStatement(sqlPrepInsertItem);
	    List<POI> results = resultSet.getPOIs();
	    for (int i = 0; i < results.size(); i++) {
		POI result = results.get(i);
		// table 1
		setPrepItem(result, prepItem);
		prepItem.addBatch();
	    }
	    con.setAutoCommit(false);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private PreparedStatement setPrepItemSimple(int id, String title,
	    String city, String state, double longitude, double latitude,
	    double distance, double averagerating, double totalratings,
	    double totalreviews, PreparedStatement prepItem) {
	try {
	    prepItem.setInt(1, id);
	    prepItem.setString(2, title);
	    prepItem.setString(3, city);
	    prepItem.setString(4, state);
	    prepItem.setFloat(5, (float) longitude);
	    prepItem.setFloat(6, (float) latitude);
	    prepItem.setFloat(7, (float) distance);

	    prepItem.setDouble(8, averagerating);
	    prepItem.setDouble(9, totalratings);
	    prepItem.setDouble(10, totalratings);
	    prepItem.setFloat(11, (float) longitude);
	    prepItem.setFloat(12, (float) latitude);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepItem;
    }

    private PreparedStatement setPrepItem(POI result, PreparedStatement prepItem) {
	try {
	    prepItem.setInt(1, result.getId());
	    prepItem.setString(2, result.getTitle());
	    prepItem.setString(3, result.getCity());
	    prepItem.setString(4, result.getState());
	    prepItem.setFloat(5, (float) result.getLongitude());
	    prepItem.setFloat(6, (float) result.getLatitude());
	    prepItem.setFloat(7, (float) result.getDistance());
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
	    PGpoint point = new PGpoint(result.getLongitude(),
		    result.getLatitude());
	    prepItem.setObject(11, point);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return prepItem;
    }

    public void select() {
	Connection conn = connect();

	Statement s;
	try {
	    s = conn.createStatement();
	    ResultSet r = s.executeQuery("select itemid, geom from item");
	    while (r.next()) {
		int id = r.getInt(1);
		System.out.println("Row " + id + ":");
		PGgeometry geom = (PGgeometry) r.getObject(2);
		System.out.println(geom.toString());
		if (geom.getGeoType() == Geometry.POINT) {
		    Point p = (Point) geom.getGeometry();
		    double x = p.x;
		    double y = p.y;
		    System.out.println("x, y = " + x + ", " + y);
		}
	    }
	    s.close();
	    conn.close();
	} catch (SQLException e) {

	    e.printStackTrace();
	}
    }

    /**
     * http://gis.stackexchange.com/questions/36841/line-intersection-with-circle-on-a-sphere-globe-or-earth
     * 
     * @return
     */
    public List intersection() {
	PGpoint center = new PGpoint(0, 0);
	PGcircle circle  = new PGcircle(center, 1);
	PGpoint p1 = new PGpoint(0, 1);
	PGpoint p2 = new PGpoint(1, 0);
	PGlseg lseg = new PGlseg(p1, p2);
	PGgeometry geometry = new PGgeometry();
	
	return null;
    }

    /**
     * {@link http://blog.opengeo.org/tag/knn/}
     * 
     * @return
     */
    public List knnQuery() {
	Connection conn = connect();
	// sqlKnn
	return null;
    }

    public void geoPoint() {

    }

    public double distance() {
	return 0.0;
    }

    /**
     * {@link http://postgis.net/docs/manual-1.3/ch04.html#id437402}
     */
    private void example() {
	try {
	    /*
	     * Load the JDBC driver and establish a connection.
	     */
	    Class.forName("org.postgresql.Driver");
	    String url = "jdbc:postgresql://localhost:5432/postgres";
	    conn = DriverManager.getConnection(url, "postgres", "postgres");
	    /*
	     * Add the geometry types to the connection. Note that you must cast
	     * the connection to the pgsql-specific connection implementation
	     * before calling the addDataType() method.
	     */
	    ((org.postgresql.Connection) conn).addDataType("geometry",
		    "org.postgis.PGgeometry");
	    ((org.postgresql.Connection) conn).addDataType("box3d",
		    "org.postgis.PGbox3d");
	    /*
	     * Create a statement and execute a select query.
	     */
	    Statement s = conn.createStatement();
	    ResultSet r = s
		    .executeQuery("select AsText(geom) as geom,id from geomtable");
	    while (r.next()) {
		/*
		 * Retrieve the geometry as an object then cast it to the
		 * geometry type. Print things out.
		 */
		PGgeometry geom = (PGgeometry) r.getObject(1);
		int id = r.getInt(2);
		System.out.println("Row " + id + ":");
		System.out.println(geom.toString());
	    }
	    s.close();
	    conn.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
