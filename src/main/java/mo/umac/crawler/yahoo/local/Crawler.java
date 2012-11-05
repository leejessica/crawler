/**
 * 
 */
package mo.umac.crawler.yahoo.local;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

/**
 * Conduct a search for a product or business name within a specified zipcode
 * using the Yahoo! Local search Service.
 * 
 * @author yanhui
 * 
 */
public class Crawler {
	//
	public static Logger logger = Logger.getLogger(Crawler.class.getName());

	/**
	 * Construct the query url according to the Yahoo Local API.
	 * 
	 * @see http://developer.yahoo.com/search/local/V3/localSearch.html
	 * @param appid
	 * @param query
	 * @param zip
	 * @param results
	 * @param start
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	private String concatenateUrl(String appid, String query, int zip,
			int results, int start, double latitude, double longitude,
			int radius) {
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
		if (latitude > 0) {
			sb.append("&latitude=");
			sb.append(latitude);
		}
		if (longitude > 0) {
			sb.append("&longitude=");
			sb.append(longitude);
		}
		if (radius > 0) {
			sb.append("&radius=");
			sb.append(radius);
		}
		return sb.toString();
	}
	
	/**
	 * Store a web page
	 * @param url
	 * @param filePath
	 * @return
	 */
	private boolean crawler(String url, String filePath) {
		
		HttpClient httpclient = createHttpClient();
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				logger.debug("file doesn't exists, creating...");
				if (!file.createNewFile()) {
					logger.error("create file failed.");

				} else {
					
					OutputStream output = new BufferedOutputStream(
							new FileOutputStream(file));
					logger.debug("fetching... " + url);
					HttpGet httpget = new HttpGet(url);
					HttpResponse response;
					response = httpclient.execute(httpget);
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						entity.writeTo(output);
					}
					output.close();
				}

			} else {
//				logger.debug("file exists, do nothing...");
				logger.debug("having in local... " + url);
			}
		} catch (Exception e) {
			logger.error("Wrong simpleCrawl", e);
			logger.error(url);
			logger.error(filePath);
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}

		return true;
	}
	
	private HttpClient createHttpClient() {
		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager();
		
		HttpParams params = new BasicHttpParams();

		int timeout = 1000 * 60 * 5;
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		// HttpConnectionParams.setSocketBufferSize(params, size);

//		HttpClient httpClient = new DefaultHttpClient(params);
		HttpClient httpClient = new DefaultHttpClient(manager, params);
		return httpClient;
	}

}
