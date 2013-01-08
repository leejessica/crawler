package mo.umac.crawler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import mo.umac.parser.Category;
import mo.umac.parser.Rating;
import mo.umac.parser.Result;
import mo.umac.parser.ResultSet;

public class DBFile {

	/**
	 * A folder stores all crawled .xml file from Yahoo Local.
	 * 
	 */
	public static final String FOLDER_NAME = "../yahoolocal/";

	/**
	 * A file stores the xml file's name, query condition, and the count
	 * information of this query.
	 */
	public static final String QUERY_FILE_NAME = "query";

	/**
	 * A file stores the xml file's name and the detailed results of a query.
	 */
	public static final String RESULT_FILE_NAME = "results";

	/**
	 * @param partFileName
	 * @param qc
	 * @param resultSet
	 *            basic information of the query.
	 */
	public static void writeQueryFile(String partFileName, YahooLocalQuery qc,
			ResultSet resultSet) {
		BufferedWriter dbOutput = qc.getQueryOutput();
		String query = qc.getQuery();
		int zip = qc.getZip();
		int results = qc.getResults();
		int start = qc.getStart();
		double latitude = qc.getCircle().getCenter().y;
		double longitude = qc.getCircle().getCenter().x;
		double radius = qc.getCircle().getRadius();
		try {
			// file name:
			dbOutput.write(partFileName);
			dbOutput.write(";");
			// other information:
			dbOutput.write(query);
			dbOutput.write(";");
			dbOutput.write(zip);
			dbOutput.write(";");
			dbOutput.write(results);
			dbOutput.write(";");
			dbOutput.write(start);
			dbOutput.write(";");
			dbOutput.write(Double.toString(latitude));
			dbOutput.write(";");
			dbOutput.write(Double.toString(longitude));
			dbOutput.write(";");
			dbOutput.write(Double.toString(radius));
			dbOutput.write(";");
			dbOutput.write(resultSet.getTotalResultsAvailable());
			dbOutput.write(";");
			dbOutput.write(resultSet.getTotalResultsReturned());
			dbOutput.write(";");
			dbOutput.write(resultSet.getFirstResultPosition());
			dbOutput.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeResultsFile(String partFileName, ResultSet resultSet) {
		BufferedWriter resultsOutput = resultSet.getResultsOutput();
		List<Result> results = resultSet.getResults();
		for (int i = 0; i < results.size(); i++) {
			Result result = results.get(i);
			int id = result.getId();
			String title = result.getTitle();
			String address = result.getAddress();
			String city = result.getCity();
			String state = result.getState();
			String phone = result.getPhone();
			Double longitude = result.getLongitude();
			Double latitude = result.getLatitude();

			Rating rating = result.getRating();

			double distance = result.getDistance();
			String url = result.getUrl();
			String clickUrl = result.getClickUrl();
			String mapUrl = result.getMapUrl();
			String businessUrl = result.getBusinessUrl();
			String businessClickUrl = result.getBusinessClickUrl();

			List<Category> categories = result.getCategories();

			try {
				// file name:
				resultsOutput.write(partFileName);
				resultsOutput.write(";");
				// other information:
				resultsOutput.write(id);
				resultsOutput.write(";");
				resultsOutput.write(title);
				resultsOutput.write(";");
				resultsOutput.write(city);
				resultsOutput.write(";");
				resultsOutput.write(state);
				resultsOutput.write(";");
				resultsOutput.write(Double.toString(latitude));
				resultsOutput.write(";");
				resultsOutput.write(Double.toString(longitude));
				resultsOutput.write(";");
				resultsOutput.write(Double.toString(distance));
				for (int j = 0; j < categories.size(); j++) {
					Category category = categories.get(j);
					int categoryId = category.getId();
					String categorynName = category.getName();
					resultsOutput.write(";");
					resultsOutput.write(categoryId);
					resultsOutput.write(";");
					resultsOutput.write(categorynName);
				}
				resultsOutput.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
