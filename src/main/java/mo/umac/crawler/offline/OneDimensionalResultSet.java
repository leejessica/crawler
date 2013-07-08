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
    private List<POI> leftPOIs;
    private List<POI> rightPOIs;
    private List<POI> onPOIs;

    private int numQueries;
    /**
     * The query circles
     */
    private List<Circle> circles;

    public void addAll(List oneList, List antherList) {
	oneList.addAll(antherList);
    }

    public void addACircle(Circle aCircle) {
	circles.add(aCircle);
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

    public List<POI> getLeftPOIs() {
	return leftPOIs;
    }

    public void setLeftPOIs(List<POI> leftPOIs) {
	this.leftPOIs = leftPOIs;
    }

    public List<POI> getRightPOIs() {
	return rightPOIs;
    }

    public void setRightPOIs(List<POI> rightPOIs) {
	this.rightPOIs = rightPOIs;
    }

    public List<POI> getOnPOIs() {
	return onPOIs;
    }

    public void setOnPOIs(List<POI> onLinePOI) {
	this.onPOIs = onLinePOI;
    }

}
