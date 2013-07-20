package mo.umac.db;

import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import mo.umac.crawler.AQuery;
import mo.umac.crawler.POI;
import mo.umac.crawler.ResultSetYahoo;
import mo.umac.rtree.MyRTree;
import mo.umac.spatial.ECEFLLA;
import mo.umac.spatial.GeoOperator;

public class DBInMemory {

    /**
     * All tuples; Integer is the item's id
     */
    private HashMap<Integer, POI> pois;
    
    private MyRTree rtree;


    /**
     * @param externalDataSet
     */
    public void readFromExtenalDB(DBExternal dbExternal) {
	pois = dbExternal.readFromExtenalDB();
    }
    
    public void writeToExternalDB(DBExternal dbExternal){
	dbExternal.writeToExternalDB();
    }
    
    public void index(List<Coordinate> coordinate) {
	rtree = new MyRTree(coordinate);
    }
    
    /**
     * Indexing all pois
     */
    public void index(){
	rtree = new MyRTree(pois);
    }

    public ResultSetYahoo query(AQuery qc) {
	Coordinate ecef = qc.getPoint();
	Coordinate lla = ECEFLLA.ecef2lla(ecef);
 	List<Integer> results = rtree.searchNN(qc.getPoint(), qc.getTopK());
 	// TODO
 	return null;
     }
}
