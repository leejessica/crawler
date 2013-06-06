package mo.umac.db;


import mo.umac.crawler.YahooLocalQuery;
import mo.umac.parser.ResultSet;

public abstract class DataSet {

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

	public abstract void record(int queryID, int level, int parentID, YahooLocalQuery qc, ResultSet resultSet);

}
