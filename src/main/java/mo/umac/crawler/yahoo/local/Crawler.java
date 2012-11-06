/**
 * 
 */
package mo.umac.crawler.yahoo.local;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import mo.umac.crawler.utils.FileOperator;

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
 * @author Kate YAN
 * 
 */
public class Crawler {
	//
	public static Logger logger = Logger.getLogger(Crawler.class.getName());

	public Crawler() {

	}

	/**
	 * Using HttpClient to crawl the xml web page.
	 * 
	 * The access limitation is 5000 queries per day.
	 * 
	 * @param urls
	 *            A set of urls
	 * @param filePaths
	 *            Urls' corresponding filePaths
	 */
	public void crawl(String[] urls, String[] filePaths) {
		HttpClient httpclient = createHttpClient();
		int i = 0;
		try {
			for (; i < filePaths.length; i++) {
				File file = FileOperator.creatFileAscending(filePaths[i]);
				OutputStream output = new BufferedOutputStream(
						new FileOutputStream(file));
				logger.debug("fetching... " + urls[i]);
				HttpGet httpget = new HttpGet(urls[i]);
				HttpResponse response;
				response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					entity.writeTo(output);
				}
				output.close();
				//TODO deal with access limitations

			}
		} catch (Exception e) {
			logger.error("Error in crawler", e);
			logger.error(urls[i]);
			logger.error(filePaths[i]);
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}


	public HttpClient createHttpClient() {
		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager();

		HttpParams params = new BasicHttpParams();
		//TODO reset the time out
		int timeout = 1000 * 60 * 5;
		
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpClient httpClient = new DefaultHttpClient(manager, params);
		return httpClient;
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

}
