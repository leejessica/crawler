/**
 * 
 */
package mo.umac.crawler.offline;

import java.util.List;

import mo.umac.geo.Circle;
import mo.umac.parser.POI;

/**
 * @author kate
 * 
 */
public class OneDimensionalResultSet {

    private List<POI> pois;
    private int numQueries;
    /**
     * The query circles
     */
    private List<Circle> circles;

    public void addAll(List<POI> antherPOIs) {
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

    public List<Circle> getCircles() {
	return circles;
    }

    public void setCircles(List<Circle> circles) {
	this.circles = circles;
    }

}
