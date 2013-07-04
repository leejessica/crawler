package mo.umac.db;

import mo.umac.crawler.online.YahooLocalQuery;
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
	    YahooLocalQuery qc, YahooResultSet resultSet) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public YahooResultSet query(YahooLocalQuery qc) {
	// TODO Auto-generated method stub
	return null;
    }

}
