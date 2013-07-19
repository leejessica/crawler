package mo.umac.rtree.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.geometric.PGpoint;

import mo.umac.db.Postgresql;
import mo.umac.rtree.MyRTree;

import com.vividsolutions.jts.geom.Coordinate;

public class MyRTreeTest {

    public String dbName = "test";

    /**
     * @param args
     */
    public static void main(String[] args) {
	MyRTreeTest test = new MyRTreeTest();
	// List<Coordinate> coors = test.generatePoints();
	List<Coordinate> coors = test.importPoints();

	// System.out.println(coors.size());
	// for (int i = 0; i < coors.size(); i++) {
	// Coordinate coor = coors.get(i);
	// System.out.println("[" + coor.x + ", " + coor.y + "]");
	// }

	// test.storeInPostgresql(coors);

	MyRTree rtree = new MyRTree(coors);
	Coordinate searchPoint = new Coordinate(30.6, 45.0);
	List<Integer> results = rtree.searchNN(searchPoint, 10);
	for (int i = 0; i < results.size(); i++) {
	    System.out.println(results.get(i).toString());
	}

    }

    private List<Coordinate> importPoints() {
	Postgresql post = new Postgresql();
	List<Coordinate> coords = new ArrayList<Coordinate>();
	Connection conn = post.connect(dbName);
	Statement s;
	try {
	    s = conn.createStatement();
	    ResultSet r = s.executeQuery("select * from points");
	    while (r.next()) {
		int id = r.getInt(1);
		float x = r.getFloat(2);
		float y = r.getFloat(3);
		Coordinate coor = new Coordinate(x, y);
		coords.add(coor);
	    }
	    s.close();
	    conn.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return coords;
    }

    public List<Coordinate> generatePoints() {
	int numPoint = 100;
	double x = 1.0;
	double y = 1.0;
	List list = new ArrayList<Coordinate>();
	Random random = new Random(System.currentTimeMillis());
	for (int i = 0; i < numPoint; i++) {
	    x = random.nextDouble() * 100;
	    y = random.nextDouble() * 100;
	    Coordinate coordinate = new Coordinate(x, y);
	    list.add(coordinate);
	}
	return list;
    }

    public void storeInPostgresql(List<Coordinate> coors) {
	Postgresql postgresql = new Postgresql();
	postgresql.DB_NAME = "test";

	String sql = "create table if not exists points (itemid int primary key, "
		+ "longitude float, latitude float)";
	String sqlAddGeom = "SELECT AddGeometryColumn ('points','geom'," + 4326
		+ ",'POINT',2)";
	postgresql.createTable(sql, sqlAddGeom);
	String sqlPrep = "insert into points (itemid, longitude, latitude, geom) values (?,?,?,ST_SetSRID(ST_MakePoint(?, ?), 4326))";
	exportData(sqlPrep, coors);

    }

    public void exportData(String sqlPrep, List<Coordinate> coors) {
	Postgresql postgresql = new Postgresql();

	Connection con = postgresql.connect(dbName);
	// prepared statement
	PreparedStatement prepItem;
	try {
	    con.setAutoCommit(false);
	    prepItem = con.prepareStatement(sqlPrep);
	    for (int i = 0; i < coors.size(); i++) {
		Coordinate coor = coors.get(i);
		//
		prepItem.setInt(1, i);
		prepItem.setFloat(2, (float) coor.x);
		prepItem.setFloat(3, (float) coor.y);
		prepItem.setFloat(4, (float) coor.x);
		prepItem.setFloat(5, (float) coor.y);

		prepItem.addBatch();
		if (i % 50 == 0) {
		    prepItem.executeBatch();
		}
	    }
	    prepItem.executeBatch();
	    con.commit();
	    prepItem.close();
	    con.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}
