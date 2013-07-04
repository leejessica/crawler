package mo.umac.db.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

/**
 * @author kate
 * 
 */
public class TestH2Geo {

    /**
     * @param args
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws ParseException
     */
    public static void main(String[] args) throws ClassNotFoundException,
	    SQLException, ParseException {
	// createTable();
	// insert();
	query();

    }

    public static void createTable() {
	try {
	    Class.forName("org.h2.Driver");
	    Connection conn = DriverManager.getConnection(
		    "jdbc:h2:../example-folder/hatbox;AUTO_SERVER=TRUE", null,
		    null);
	    String sql = "create table T1 (ID identity, NAME varchar (20), GEOM varbinary (200))";
	    conn.createStatement().executeUpdate(sql);
	    // ... populate with data, test etc
	    conn.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

    }

    public static void insert() throws ClassNotFoundException, SQLException {
	Class.forName("org.h2.Driver");
	Connection conn = DriverManager
		.getConnection(
			"jdbc:h2:../example-folder/hatbox;AUTO_SERVER=TRUE",
			null, null);
	GeometryFactory geomFactory = new GeometryFactory();
	CoordinateArraySequenceFactory factory = CoordinateArraySequenceFactory
		.instance();
	Coordinate[] coord = new Coordinate[1];
	WKBWriter writer = new WKBWriter();
	PreparedStatement ps = conn
		.prepareStatement("insert into T1 (NAME, GEOM) values (?,?)");
	ps.setString(1, "Test Point");
	coord[0] = new Coordinate(145.0, -37.0);
	ps.setBytes(2,
		writer.write(new Point(factory.create(coord), geomFactory)));
	ps.execute();
	coord[0] = new Coordinate(145.1, -37.1);
	ps.setBytes(2,
		writer.write(new Point(factory.create(coord), geomFactory)));
	ps.execute();
	coord[0] = new Coordinate(145.2, -37.2);
	ps.setBytes(2,
		writer.write(new Point(factory.create(coord), geomFactory)));
	ps.execute();
	coord[0] = new Coordinate(145.3, -37.3);
	ps.setBytes(2,
		writer.write(new Point(factory.create(coord), geomFactory)));
	ps.execute();
	ps.close();
	conn.close();
    }

    public static void query() throws ClassNotFoundException, SQLException,
	    ParseException {
	Class.forName("org.h2.Driver");
	Connection conn = DriverManager
		.getConnection(
			"jdbc:h2:../example-folder/hatbox;AUTO_SERVER=TRUE",
			null, null);
	Statement stmt = conn.createStatement();
	WKBReader reader = new WKBReader();
	String sql = "select * from T1";
	ResultSet rs = stmt.executeQuery(sql);
	// "select ID, GEOM from T1 as t inner join "
	// +
	// "HATBOX_MBR_INTERSECTS_ENV('PUBLIC','T1',145.05,145.25,-37.25,-37.05) as i "
	// + "on t.ID = i.HATBOX_JOIN_ID");
	while (rs.next()) {
	    int id = rs.getInt(1);
	    String name = rs.getString(2);
	    byte[] bytes = rs.getBytes(3);
	    System.out.println(id);
	    System.out.println(name);
	    System.out.println(reader.read(bytes));

	}
	rs.close();
	stmt.close();
	conn.close();
    }

    private void creating() {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put("dbtype", "h2");
	params.put("database", "/abs/path/to/geotools");

	try {
	    DataStore datastore = DataStoreFinder.getDataStore(params);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
