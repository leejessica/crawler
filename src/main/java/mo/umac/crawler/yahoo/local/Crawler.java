/**
 * 
 */
package mo.umac.crawler.yahoo.local;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import mo.umac.crawler.utils.Circle;
import mo.umac.crawler.utils.FileOperator;
import mo.umac.crawler.utils.UScensusData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Conduct a search using the Yahoo! Local search Service.
 * 
 * @author Kate Yim
 * 
 */
public class Crawler {
	//
	public static Logger logger = Logger.getLogger(Crawler.class.getName());

	/**
	 * The maximum radius (in miles) of the query. TODO allocation MAX_R
	 */
	private final double MAX_R = 0.0;

	/**
	 * My Yahoo appid.
	 * 
	 * @author Kate Yim
	 */
	private final String appid = "l6QevFbV34H1VKW58naZ8keJohc8NkMNvuWfVs2lR3ROJMtw63XOWBePbDcMBFfkDnU-";
	/**
	 * The maximum number of returned results by a query.
	 */
	private final int results = 20;
	/**
	 * The maximum starting result position to return.
	 */
	private final int maxStart = 250;

	/**
	 * The maximum number of results on can get through this query by only changing the start value.
	 */
	private final int maxTotalResultsReturned = 270;
	
	public Crawler() {

	}

	/**
	 * Crawling points in US by states
	 * 
	 * @param folder
	 *            stores the crawled .xml files
	 */
	private void crawlBasedOnState(String folder, String mapFileName) {
		HttpClient httpclient = createHttpClient();
		// TODO create a file recording the mapping relationship
		File mapFile = new File(folder + mapFileName);
		// state by state
		@SuppressWarnings({ "unchecked" })
		ArrayList<Envelope> envelopeStates = (ArrayList<Envelope>) UScensusData
				.MBR(UScensusData.STATE_SHP_FILE_NAME);
		@SuppressWarnings("unchecked")
		ArrayList<String> nameStates = (ArrayList<String>) UScensusData
				.stateName(UScensusData.STATE_DBF_FILE_NAME);
		try {
			for (int i = 0; i < envelopeStates.size(); i++) {
				Envelope envelopeState = envelopeStates.get(i);
				String stateName = nameStates.get(i);
				// All crawled .xml files will be classified into the subFolders
				// corresponding to their states' name
				String subFolder = FileOperator.createFolder(folder, stateName);

				Envelope unit = Coverage.computeUnit(envelopeState, MAX_R);
				Envelope firstEnvelope = Coverage.firstEnvelopeInRegion(
						envelopeState, unit);
				Circle circle = Coverage.computeCircle(firstEnvelope);
				// indicator for the while loop
				int numSubRegions = Coverage
						.numsSubRegions(envelopeState, unit);
				while (numSubRegions >= 0) {
					for (int start = 1; start <= maxStart; start += results) {
						String url = concatenateUrl(appid, "*", 0, results,
								start, circle.getCenter().y,
								circle.getCenter().x, circle.getRadius());
						// crawl...
						String partFileName = concatenateFileName(null, 0,
								numSubRegions, start, circle.getCenter().y,
								circle.getCenter().x, circle.getRadius());
						String xmlFile = folder + partFileName;
						// TODO record the mapping relationship between url &
						// fileName
						File file = FileOperator.creatFileAscending(xmlFile);
						OutputStream output = new BufferedOutputStream(
								new FileOutputStream(file));
						logger.debug("fetching... " + xmlFile);
						HttpGet httpget = new HttpGet(xmlFile);
						HttpResponse response = httpclient.execute(httpget);
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							entity.writeTo(output);
						}
						output.close();
						ParseXml parseXml = new ParseXml(xmlFile);
						parseXml.parse();
						// deal with access limitations
						Thread.currentThread().sleep(17280); // sleep 17.28s
						if (parseXml.isLimitExceeded()) {
							Thread.currentThread().sleep(5 * 60 * 1000); // sleep
							continue;												// 5min
						}
						// TODO change to another region
						if(parseXml.getTotalResultsAvailable() > maxTotalResultsReturned) {
							
						}
						
					}
					// finished crawling all points in this sub-region
					numSubRegions--;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in crawler", e);
		} finally {
			//TODO add it
//			try {
//				outputMap.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
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
	 * @deprecated
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
				// TODO deal with access limitations

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

	/**
	 * Using HttpClient to crawl the xml web page.
	 * 
	 * The access limitation is 5000 queries per day.
	 * 
	 * @param mapFile
	 *            A file record the map relationship between the name of saved
	 *            XML file and the url.
	 * @param folder
	 *            A folder stores all saved XML files.
	 * @deprecated
	 */
	public void crawl(String mapFileName, String folder) {
		HttpClient httpclient = createHttpClient();
		int i = 0;
		boolean finish = false;
		String partFileName = "";
		String fileName = "";

		BufferedWriter outputMap = null;
		try {
			outputMap = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(mapFileName, true)));

			while (!finish) {
				// TODO
				// parse url -> partFileName
				// latitude, longitude, query, zipcode, results, start

				fileName = folder + partFileName;
				// TODO
				// outputMap.write(conent);

				// crawling the file
				File file = FileOperator.creatFileAscending(fileName);
				OutputStream output = new BufferedOutputStream(
						new FileOutputStream(file));
				logger.debug("fetching... " + fileName);
				HttpGet httpget = new HttpGet(fileName);
				HttpResponse response;
				response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					entity.writeTo(output);
				}
				output.close();
				// TODO deal with access limitations

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in crawler", e);
			logger.error(fileName);
		} finally {
			try {
				outputMap.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}

	public HttpClient createHttpClient() {
		// ThreadSafeClientConnManager manager = new
		// ThreadSafeClientConnManager();
		// TODO check
		PoolingClientConnectionManager manager = new PoolingClientConnectionManager();

		HttpParams params = new BasicHttpParams();
		// TODO reset the time out
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
	 *            The starting result position to return (1-based).
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	private String concatenateUrl(String appid, String query, int zip,
			int results, int start, double latitude, double longitude,
			double radius) {
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
	 * Concatenate the file name using these fields.
	 * 
	 * @param query
	 * @param zip
	 * @param results
	 * @param start
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	private String concatenateFileName(String query, int zip, int results,
			int start, double latitude, double longitude, double radius) {
		// TODO
		return null;
	}

}
