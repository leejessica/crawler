package mo.umac.db;

import java.util.HashMap;

import mo.umac.metadata.AQuery;
import mo.umac.metadata.APOI;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.ResultSetYahooOnline;
import mo.umac.metadata.YahooLocalQueryFileDB;

public class Website extends DBExternal {

    @Override
    public void writeToExternalDBFromOnline(int queryID, int level, int parentID,
	    YahooLocalQueryFileDB qc, ResultSetYahooOnline resultSet) {
	// TODO Auto-generated method stub
	
    }


    @Override
    public void init() {
	// TODO Auto-generated method stub
	
    }


    @Override
    public HashMap<Integer, APOI> readFromExtenalDB(String category,
	    String state) {
	// TODO Auto-generated method stub
	return null;
    }


    @Override
    public void writeToExternalDB(int queryID, AQuery query, ResultSet resultSet) {
	// TODO Auto-generated method stub
	
    }


    @Override
    public void createTables(String dbNameTarget) {
	// TODO Auto-generated method stub
	
    }


    @Override
    public int numCrawlerPoints() {
	// TODO Auto-generated method stub
	return 0;
    }



}
