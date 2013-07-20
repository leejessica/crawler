package mo.umac.db;

import java.util.HashMap;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.POI;
import mo.umac.crawler.ResultSetYahoo;
import mo.umac.crawler.online.YahooLocalQueryFileDB;

public class Website extends DBExternal {

    @Override
    public void record(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, ResultSetYahoo resultSet) {
	// TODO Auto-generated method stub
	
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
