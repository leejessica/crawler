package mo.umac.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MainDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainDB m = new MainDB();
		m.prunH2();
		// m.convertFromH2ToFile();
	}

	public void convertFromQRFileToH2() {
		String dbNameSource = "../crawler-data/yahoolocal-h2/source/ny";
		String dbNameTarget = "";
		H2DB h2 = new H2DB(dbNameSource, dbNameTarget);
		String state = "NY";
		// DBExternal.FOLDER_NAME = "../crawler-data/yahoolocal/"
		String folderPath = DBExternal.FOLDER_NAME + "/96926236+Restaurants/" + state + "/";
		h2.convertFileDBToH2DB(folderPath, "");

		// exam the converted data
		int c1 = h2.count(dbNameSource, "QUERY");
		System.out.println("c1 = " + c1);
		int c2 = h2.count(dbNameSource, "ITEM");
		System.out.println("c2 = " + c2);
		h2.closeConnection(dbNameSource);
	}

	public void prunH2() {
		// FIXME check here
		String dbNameSource = "../crawler-data/yahoolocal-h2/source/ny";
		String dbNameTarget = "../crawler-data/yahoolocal-h2/source/ny-prun";
		String categoryQ = "Restaurants";
		String stateQ = "NY";

		H2DB h2 = new H2DB(dbNameSource, dbNameTarget);
		h2.prun(categoryQ, stateQ);

		int c1 = h2.count(dbNameSource, "ITEM");
		System.out.println("c1 = " + c1);
		int c2 = h2.count(dbNameTarget, "ITEM");
		System.out.println("c2 = " + c2);
		h2.closeConnection(dbNameSource);
		h2.closeConnection(dbNameTarget);
	}

	public void convertFromH2ToFile() {
		// String dbName = "../crawler-data/yahoolocal-h2/source/ny";
		// String dbName = "../crawler-data/yahoolocal-h2/source/ny-prun";
		String dbName = "../crawler-data/yahoolocal-h2/target/ny-prun";
		String tableName = "item";
		String fileName = "../data-map/ny-prun-target.pois";
		H2DB h2 = new H2DB(dbName, "");
		h2.extractValuesFromItemTable(dbName, tableName, fileName);
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
