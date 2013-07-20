/**
 * 
 */
package mo.umac.db;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.POI;
import mo.umac.crawler.ResultSetYahoo;
import mo.umac.crawler.online.YahooLocalQueryFileDB;
import mo.umac.parser.Category;
import mo.umac.parser.Rating;

/**
 * @author kate
 * 
 */
public class FileDB extends DBExternal {

    @Override
    public void record(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, ResultSetYahoo resultSet) {
	writeQueryFile(Integer.toString(queryID), qc.getQueryOutput(),
		qc.queryInfo(), resultSet);
	if (resultSet.getTotalResultsReturned() > 0) {
	    writeResultsFile(Integer.toString(queryID), resultSet);
	}
    }

    /**
     * @param partFileName
     * @param dbOutput
     * @param queryInfo
     * @param resultSet
     */
    private void writeQueryFile(String partFileName, BufferedWriter dbOutput,
	    String queryInfo, ResultSetYahoo resultSet) {
	try {
	    dbOutput.write(partFileName);
	    dbOutput.write(";");
	    dbOutput.write(queryInfo);
	    dbOutput.write(";");
	    dbOutput.write(Integer.toString(resultSet
		    .getTotalResultsAvailable()));
	    dbOutput.write(";");
	    dbOutput.write(Integer.toString(resultSet.getTotalResultsReturned()));
	    dbOutput.write(";");
	    dbOutput.write(Integer.toString(resultSet.getFirstResultPosition()));
	    dbOutput.newLine();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * @param partFileName
     * @param qc
     * @param resultSet
     *            basic information of the query.
     * @deprecated
     */
    private void writeQueryFile(String partFileName, YahooLocalQueryFileDB qc,
	    ResultSetYahoo resultSet) {
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
	    dbOutput.write(Integer.toString(zip));
	    dbOutput.write(";");
	    dbOutput.write(Integer.toString(results));
	    dbOutput.write(";");
	    dbOutput.write(Integer.toString(start));
	    dbOutput.write(";");
	    dbOutput.write(Double.toString(latitude));
	    dbOutput.write(";");
	    dbOutput.write(Double.toString(longitude));
	    dbOutput.write(";");
	    dbOutput.write(Double.toString(radius));
	    dbOutput.write(";");
	    dbOutput.write(Integer.toString(resultSet
		    .getTotalResultsAvailable()));
	    dbOutput.write(";");
	    dbOutput.write(Integer.toString(resultSet.getTotalResultsReturned()));
	    dbOutput.write(";");
	    dbOutput.write(Integer.toString(resultSet.getFirstResultPosition()));
	    dbOutput.newLine();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void writeResultsFile(String partFileName, ResultSetYahoo resultSet) {
	BufferedWriter resultsOutput = resultSet.getResultsOutput();
	List<POI> results = resultSet.getPOIs();
	for (int i = 0; i < results.size(); i++) {
	    POI result = results.get(i);
	    int id = result.getId();
	    String title = result.getTitle();
	    String city = result.getCity();
	    String state = result.getState();
	    Double longitude = result.getLongitude();
	    Double latitude = result.getLatitude();

	    Rating rating = result.getRating();

	    double distance = result.getDistance();

	    List<Category> categories = result.getCategories();

	    try {
		// file name:
		resultsOutput.write(partFileName);
		resultsOutput.write(";");
		// other information:
		resultsOutput.write(Integer.toString(id));
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
		    resultsOutput.write(Integer.toString(categoryId));
		    resultsOutput.write(";");
		    resultsOutput.write(categorynName);
		}
		resultsOutput.newLine();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub
	
    }

    @Override
    public HashMap<Integer, POI> readFromExtenalDB() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void writeToExternalDB() {
	// TODO Auto-generated method stub
	
    }

}
