package mo.umac.db;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.online.YahooLocalQueryFileDB;
import mo.umac.parser.YahooResultSet;

/**
 * H2 dataset with spatial index
 * 
 * @author kate
 *
 */
public class H2DBGeo extends DataSet{

    @Override
    public void record(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, YahooResultSet resultSet) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public YahooResultSet query(AQuery qc) {
	// TODO Auto-generated method stub
	return null;
    }

}
