package mo.umac.crawler;

import com.vividsolutions.jts.geom.Coordinate;

public class OnlineYahooLocalQuery extends AQuery {

    private String appid;
    
    private int start;
    
    /**
     * TODO radius unit?
     */
    private double radius;

    public OnlineYahooLocalQuery(String query, int topK, String state,
	    int category, Coordinate point) {
	super(query, topK, state, category, point);

    }

    public OnlineYahooLocalQuery(String query, int topK, String state,
	    int category, Coordinate point, String appid, int start,
	    double radius) {
	super(query, topK, state, category, point);
	this.appid = appid;
	this.start = start;
	this.radius = radius;
    }

    public String getAppid() {
	return appid;
    }

    public void setAppid(String appid) {
	this.appid = appid;
    }

    public int getStart() {
	return start;
    }

    public void setStart(int start) {
	this.start = start;
    }

    public double getRadius() {
	return radius;
    }

    public void setRadius(double radius) {
	this.radius = radius;
    }

}
