package mo.umac.db;

import java.util.HashMap;
import java.util.Map;

import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.ResultSetYahooOnline;
import mo.umac.metadata.YahooLocalQueryFileDB;

public abstract class DBExternal {

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
    
    
    public static String dbNameSource = "";

    public static String dbNameTarget = "";
    
    /**
     * String represents the name of the database;
     */
    public static Map connMap = new HashMap<String, java.sql.Connection>();
    
    public abstract void writeToExternalDB(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, ResultSetYahooOnline resultSet);

    public abstract void init();

    /**
     * Read dataset from external database.
     */
    public abstract HashMap<Integer, APOI> readFromExtenalDB(String category, String state);

    public abstract void writeToExternalDB(
	    int queryID, AQuery query, ResultSet resultSet) ;

    public abstract void createTables(String dbNameTarget);

    public abstract int numCrawlerPoints();
}
