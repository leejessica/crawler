package mo.umac.crawler.yahoo.local;

import java.io.BufferedWriter;
import java.util.List;
import mo.umac.crawler.utils.Circle;
import org.apache.http.client.HttpClient;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Kate Yim
 * 
 */
public class QueryCondition {
	private String subFolder;
	private BufferedWriter mapOutput;
	private Envelope envelopeState;
	private String appid;
	private int start;
	private Circle circle;
	private int numQueries;
	private int countGz;
	private List filesGz;
	private boolean overflow;
	private String query;
	private int zip;
	private int results;

	public QueryCondition() {

	}

	public QueryCondition(String subFolder, BufferedWriter mapOutput,
			Envelope envelopeState, String appid, int start, Circle circle,
			int numQueries, int countGz, List filesGz,
			boolean overflow, String query, int zip, int results) {
		super();
		this.subFolder = subFolder;
		this.mapOutput = mapOutput;
		this.envelopeState = envelopeState;
		this.appid = appid;
		this.start = start;
		this.circle = circle;
		this.numQueries = numQueries;
		this.countGz = countGz;
		this.filesGz = filesGz;
		this.overflow = overflow;
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
	 * @param results
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
		if (query != null) {
			sb.append("&query=");
			sb.append(query);
		}
		if (zip > 0) {
			sb.append("&zip=");
			sb.append(zip);
		}
		if (results > 0) {
			sb.append("&results=");
			sb.append(results);
		}
		if (start > 0) {
			sb.append("&start=");
			sb.append(start);
		}
		if (circle.getCenter().y > 0) {
			sb.append("&latitude=");
			sb.append(circle.getCenter().y);
		}
		if (circle.getCenter().x > 0) {
			sb.append("&longitude=");
			sb.append(circle.getCenter().x);
		}
		if (circle.getRadius() > 0) {
			sb.append("&radius=");
			sb.append(circle.getRadius());
		}
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

	public BufferedWriter getMapOutput() {
		return mapOutput;
	}

	public void setMapOutput(BufferedWriter mapOutput) {
		this.mapOutput = mapOutput;
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

	public int getCountGz() {
		return countGz;
	}

	public void setCountGz(int countGz) {
		this.countGz = countGz;
	}

	public List getFilesGz() {
		return filesGz;
	}

	public void setFilesGz(List filesGz) {
		this.filesGz = filesGz;
	}

	public boolean isOverflow() {
		return overflow;
	}

	public void setOverflow(boolean overflow) {
		this.overflow = overflow;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
