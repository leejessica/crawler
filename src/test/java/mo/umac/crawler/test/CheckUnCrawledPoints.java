package mo.umac.crawler.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mo.umac.db.H2DB;

/**
 * We find that the number of points crawled is less than the total number of points in the database.
 * Check where is wrong!
 * 
 * @author kate
 *
 */
public class CheckUnCrawledPoints {

    
    public void compareMemeoryAndDB(){
	String file56922 = "./src/test/resources/crawlerTestFiles/56922";
	String file54738 = "./src/test/resources/crawlerTestFiles/54738";
	//FIMXE !!! check which id didn't write into the external db
	Set set56922 = new TreeSet();
	Set set54738 = new TreeSet();
	
	//
	File file = new File(file56922);
	String pre56922 = "INFO (mo.umac.crawler.offline.OfflineStrategy:149)- ";
	BufferedReader br = null;
	try {
	    br = new BufferedReader(new InputStreamReader(new FileInputStream(
		    file56922)));
	    String data = null;
	    while ((data = br.readLine()) != null) {
		data = data.trim();
		String numString = data.substring(pre56922.length());
		int num = Integer.parseInt(numString);
		set56922.add(num);
	    }
	    br.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	System.out.println("set56922 = " + set56922.size());
	//
	numOfTuplesInExternalDB(set54738);
	System.out.println("set54738 = " + set54738.size());
	//find the differences
	Set setD = new TreeSet();
	Iterator it = set56922.iterator();
	while(it.hasNext()){
	    int id = (Integer)it.next();
	    if (!set54738.contains(id)) {
		setD.add(id);
	    }
	}
	//
	System.out.println("number of missing ids is: " + setD.size());
	it = setD.iterator();
	while(it.hasNext()){
	    int id = (Integer)it.next();
	    System.out.println(id);
	}
	
	
    }
    
    public List findMissingPoints(){
	List missingList = new ArrayList<Integer>();
	
	
	return missingList;
    }
    
    public int numOfTuplesInExternalDB(Set set) {
	if (set == null) {
	    set = new TreeSet();
	}
	H2DB h2db = new H2DB();
	String dbName = H2DB.DB_NAME_TARGET;
	Connection conn = h2db.getConnection(dbName);
	try {
	    Statement stat = conn.createStatement();
	    String sql = "select distinct itemid from item";
	    java.sql.ResultSet rs = stat.executeQuery(sql);
	    while (rs.next()) {
		int i = rs.getInt(1);
		set.add(i);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return set.size();
    }
    
        
    /**
     * @param args
     */
    public static void main(String[] args) {
	CheckUnCrawledPoints ccp = new CheckUnCrawledPoints();
//	ccp.compareMemeoryAndDB();
	int number = ccp.numOfTuplesInExternalDB(null);
	System.out.println(number);

    }

}
