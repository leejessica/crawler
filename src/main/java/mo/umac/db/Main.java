package mo.umac.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSet;

import com.vividsolutions.jts.geom.Coordinate;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dbNameSource = "../crawler-data/yahoolocal-h2/source/ar";
		String dbNameTarget = "";
		H2DB h2 = new H2DB(dbNameSource, dbNameTarget);
		String state = "AR";
		// DBExternal.FOLDER_NAME = "../crawler-data/yahoolocal/"
		String folderPath = DBExternal.FOLDER_NAME + "/96926236+Restaurants/" + state + "/";
		h2.convertFileDBToH2DB(folderPath, "");
		
		// TODO exam the converted data

		// h2.createTables(H2DB.DB_NAME_TARGET);
		// //
		// int queryID = 100198;
		// Coordinate point = new Coordinate(20, -554);
		// String state = "NY";
		// int category = 4546546;
		// String query = "Rest";
		// int topK = 100;
		// AQuery aQuary = new AQuery(point, state, category, query, topK);
		// ResultSet resultSet = new ResultSet();
		// List pois = new ArrayList<APOI>();
		// APOI p1 = new APOI(1, "title", "city", state, new Coordinate(22,
		// -12), null, 58, null, 0);
		// pois.add(p1);
		// resultSet.setPOIs(pois);
		//
		// h2.writeToExternalDB(queryID, aQuary, resultSet);
		// //
		// h2.examData(H2DB.DB_NAME_TARGET);
		//
		// Main main = new Main();
		// main.convertFromH2ToPostgresql();
	}

	private void convertFromH2ToPostgresql() {
		String sqlSelectItem = H2DB.sqlSelectStar + H2DB.ITEM;
		try {
			Class.forName("org.h2.Driver");
			Connection connH2 = DriverManager.getConnection("jdbc:h2:file:" + H2DB.dbNameSource + ";AUTO_SERVER=TRUE", "sa", "");
			Postgresql post = new Postgresql();
			Statement stat = connH2.createStatement();
			//
			Connection connPostgresql = post.connect(Postgresql.DB_NAME);
			PreparedStatement prepItem = null;
			connPostgresql.setAutoCommit(false);
			prepItem = connPostgresql.prepareStatement(Postgresql.sqlPrepInsertItem);
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
					post.setPrepItemSimple(itemID, title, city, state, longitude, latitude, distance, averageRating, totalRating, totalReviews, prepItem);
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
