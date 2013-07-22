package mo.umac.metadata;

import java.util.List;

public class ResultSet {
    protected List<APOI> pois;

    /**
     * TODO The radius of this query circle
     */
    private double radius = -1;

    private int totalResultsAvailable = DefaultValues.INIT_INT;

    private int totalResultsReturned = DefaultValues.INIT_INT;

    private int firstResultPosition = DefaultValues.INIT_INT;

    public List<APOI> getPOIs() {
	return pois;
    }

    public void setPOIs(List<APOI> pois) {
	this.pois = pois;
    }

    public int getTotalResultsAvailable() {
	return totalResultsAvailable;
    }

    public int getTotalResultsReturned() {
	return totalResultsReturned;
    }

    public int getFirstResultPosition() {
	return firstResultPosition;
    }

    public void setTotalResultsAvailable(int totalResultsAvailable) {
	this.totalResultsAvailable = totalResultsAvailable;
    }

    public void setTotalResultsReturned(int totalResultsReturned) {
	this.totalResultsReturned = totalResultsReturned;
    }

    public void setFirstResultPosition(int firstResultPosition) {
	this.firstResultPosition = firstResultPosition;
    }

    public double getRadius() {
	return radius;
    }

    public void setRadius(double radius) {
	this.radius = radius;
    }

}
