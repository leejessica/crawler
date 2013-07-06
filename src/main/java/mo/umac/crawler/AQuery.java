/**
 * 
 */
package mo.umac.crawler;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * 
 * @author kate
 * 
 */
public class AQuery {
    /**
     * keyword of this query
     */
    private String query;

    private int topK;
    private String state;
    private int category;

    public AQuery(String query, int topK, String state, int category,
	    Coordinate point) {
	super();
	this.query = query;
	this.topK = topK;
	this.state = state;
	this.category = category;
	this.point = point;
    }

    public AQuery() {
    }

    /**
     * The geometric information of the query point
     */
    private Coordinate point;

    public String getQuery() {
	return query;
    }

    public void setQuery(String query) {
	this.query = query;
    }

    public Coordinate getPoint() {
	return point;
    }

    public void setPoint(Coordinate point) {
	this.point = point;
    }

    public int getTopK() {
	return topK;
    }

    public void setTopK(int topK) {
	this.topK = topK;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public int getCategory() {
	return category;
    }

    public void setCategory(int category) {
	this.category = category;
    }

}
