/**
 * 
 */
package mo.umac.parser;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author kate
 * 
 */
public class POI {
    private int id;
    private String title;
    private String address;
    private String city;
    private String state;
    private String phone;
    // change at 05-07-2013
    // private Double longitude;
    // private Double latitude;
    private Coordinate coordinate;

    private Rating rating;

    /**
     * The distance parsed by the returned web pages. The unit is mile.
     */
    private double distanceInMiles;

    private String url;
    private String clickUrl;
    private String mapUrl;
    private String businessUrl;
    private String businessClickUrl;

    private List<Category> categories;

    public POI() {

    }

    public POI(int id, String title, String address, String city, String state,
	    String phone, Coordinate coordinate, Rating rating,
	    double distanceInMiles, String url, String clickUrl, String mapUrl,
	    String businessUrl, String businessClickUrl,
	    List<Category> categories) {
	super();
	this.id = id;
	this.title = title;
	this.address = address;
	this.city = city;
	this.state = state;
	this.phone = phone;
	this.coordinate = coordinate;
	this.rating = rating;
	this.distanceInMiles = distanceInMiles;
	this.url = url;
	this.clickUrl = clickUrl;
	this.mapUrl = mapUrl;
	this.businessUrl = businessUrl;
	this.businessClickUrl = businessClickUrl;
	this.categories = categories;
    }

    public POI(int id, String title, String address, String city, String state,
	    String phone, Double longitude, Double latitude, Rating rating,
	    double distance, String url, String clickUrl, String mapUrl,
	    String businessUrl, String businessClickUrl,
	    List<Category> categories) {
	super();
	this.id = id;
	this.title = title;
	this.address = address;
	this.city = city;
	this.state = state;
	this.phone = phone;
	this.coordinate.x = longitude;
	this.coordinate.y = latitude;
	this.rating = rating;
	this.distanceInMiles = distance;
	this.url = url;
	this.clickUrl = clickUrl;
	this.mapUrl = mapUrl;
	this.businessUrl = businessUrl;
	this.businessClickUrl = businessClickUrl;
	this.categories = categories;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("Result [id=" + id + ", title=" + title + ", address="
		+ address + ", city=" + city + ", state=" + state + ", phone="
		+ phone + ", longitude=" + coordinate.x + ", latitude="
		+ coordinate.y + ", distance=" + distanceInMiles + "]");
	return sb.toString();
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getPhone() {
	return phone;
    }

    public void setPhone(String phone) {
	this.phone = phone;
    }

    public double getLongitude() {
	return coordinate.x;
    }

    public void setLongitude(double longitude) {
	this.coordinate.x = longitude;
    }

    public double getLatitude() {
	return coordinate.y;
    }

    public void setLatitude(double latitude) {
	this.coordinate.y = latitude;
    }

    public Coordinate getCoordinate() {
	return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
	this.coordinate = coordinate;
    }

    public Rating getRating() {
	return rating;
    }

    public void setRating(Rating rating) {
	this.rating = rating;
    }

    public double getDistance() {
	return distanceInMiles;
    }

    public void setDistance(double distance) {
	this.distanceInMiles = distance;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getClickUrl() {
	return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
	this.clickUrl = clickUrl;
    }

    public String getMapUrl() {
	return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
	this.mapUrl = mapUrl;
    }

    public String getBusinessUrl() {
	return businessUrl;
    }

    public void setBusinessUrl(String businessUrl) {
	this.businessUrl = businessUrl;
    }

    public String getBusinessClickUrl() {
	return businessClickUrl;
    }

    public void setBusinessClickUrl(String businessClickUrl) {
	this.businessClickUrl = businessClickUrl;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

}
