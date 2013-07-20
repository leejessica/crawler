package mo.umac.db;

import java.util.HashMap;
import java.util.List;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.POI;
import mo.umac.crawler.ResultSetYahoo;
import mo.umac.crawler.online.YahooLocalQueryFileDB;
import mo.umac.rtree.MyRTree;

import com.vividsolutions.jts.geom.Coordinate;

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

    public abstract void record(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, ResultSetYahoo resultSet);

    public abstract void init();

    /**
     * Read dataset from external database.
     */
    public abstract HashMap<Integer, POI> readFromExtenalDB();

    public abstract void writeToExternalDB();
}
