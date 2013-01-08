package mo.umac.crawler;

import java.io.BufferedWriter;
import java.util.List;

import mo.umac.geo.Circle;

import org.apache.http.client.HttpClient;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Kate Yim
 * 
 */
public class YahooLocalQuery {
	private String subFolder;
	private BufferedWriter queryOutput;
	private BufferedWriter resultsOutput;
	private Envelope envelopeState;
	private String appid;
	private int start;
	private Circle circle;
	private int numQueries;
	private String query;
	private int zip;
	private int results;

	public YahooLocalQuery() {

	}

	public YahooLocalQuery(String subFolder, BufferedWriter queryOutput,
			BufferedWriter resultsOutput, Envelope envelopeState, String appid, int start,
			Circle circle, int numQueries, String query, int zip, int results) {
		super();
		this.subFolder = subFolder;
		this.queryOutput = queryOutput;
		this.resultsOutput = resultsOutput;
		this.envelopeState = envelopeState;
		this.appid = appid;
		this.start = start;
		this.circle = circle;
		this.numQueries = numQueries;
		this.query = query;
		this.zip = zip;
		this.results = results;
	}

	/**
	 * Construct the query url according to the Yahoo Local API {@link http
	 * ://developer.yahoo.com/search/local/V3/localSearch.html}.
	 * 
	 * @param appid
	 *            Yahoo application id
	 * @param query
	 *            A keyword to search
	 * @param zip
	 * @param numOfResults
	 * @param start
	 *            The starting result position to return (1-based).
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	public String toUrl() {
		StringBuffer sb = new StringBuffer();
		String head = "http://local.yahooapis.com/LocalSearchService/V3/localSearch?";
		sb.append(head);
		sb.append("appid=");
		sb.append(appid);
		sb.append("&query=");
		sb.append(query);
		if (zip > 0) {
			sb.append("&zip=");
			sb.append(zip);
		}
		sb.append("&results=");
		sb.append(results);
		sb.append("&start=");
		sb.append(start);
		sb.append("&latitude=");
		sb.append(circle.getCenter().y);
		sb.append("&longitude=");
		sb.append(circle.getCenter().x);
		sb.append("&radius=");
		sb.append(circle.getRadius());
		return sb.toString();
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public String getSubFolder() {
		return subFolder;
	}

	public void setSubFolder(String subFolder) {
		this.subFolder = subFolder;
	}

	public Envelope getEnvelopeState() {
		return envelopeState;
	}

	public void setEnvelopeState(Envelope envelopeState) {
		this.envelopeState = envelopeState;
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

	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}

	public int getNumQueries() {
		return numQueries;
	}

	public void setNumQueries(int numQueries) {
		this.numQueries = numQueries;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getResults() {
		return results;
	}

	public void setResults(int results) {
		this.results = results;
	}

	public BufferedWriter getQueryOutput() {
		return queryOutput;
	}

	public void setQueryOutput(BufferedWriter queryOutput) {
		this.queryOutput = queryOutput;
	}

	public BufferedWriter getResultsOutput() {
		return resultsOutput;
	}

	public void setResultsOutput(BufferedWriter resultsOutput) {
		this.resultsOutput = resultsOutput;
	}

}
