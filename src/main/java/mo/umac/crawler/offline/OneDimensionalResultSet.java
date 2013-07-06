/**
 * 
 */
package mo.umac.crawler.offline;

import java.util.List;

import mo.umac.parser.POI;

/**
 * @author kate
 * 
 */
public class OneDimensionalResultSet {

    private int numQueries;
    private List<POI> pois;
    
    public void addAll(List<POI> antherPOIs){
	pois.addAll(antherPOIs);
    }
    
    public int getNumQueries() {
        return numQueries;
    }
    public void setNumQueries(int numQueries) {
        this.numQueries = numQueries;
    }

    public List<POI> getPois() {
        return pois;
    }

    public void setPois(List<POI> pois) {
        this.pois = pois;
    }
    
}
