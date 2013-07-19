package mo.umac.db;

import java.util.List;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.POI;
import mo.umac.crawler.online.YahooLocalQueryFileDB;
import mo.umac.parser.YahooResultSet;
import mo.umac.rtree.MyRTree;

import com.vividsolutions.jts.geom.Coordinate;

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

    public abstract void record(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, YahooResultSet resultSet);

    private List<POI> points;

    private MyRTree rtree;

    public abstract void init();

    /**
     * Read dataset from external database.
     */
    public void read() {

    }

    public void index(List<Coordinate> coordinate) {
	rtree = new MyRTree(coordinate);
    }

    public YahooResultSet query(AQuery qc) {
	List<Integer> results = rtree.searchNN(qc.getPoint(), qc.getTopK());
	// TODO
	return null;
    }
}
