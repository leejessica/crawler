/**
 * 
 */
package mo.umac.crawler.yahoo.local;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import mo.umac.crawler.utils.Circle;
import mo.umac.crawler.utils.FileOperator;
import mo.umac.crawler.utils.UScensusData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
	 * A folder stores all crawled .xml file from Yahoo Local.
	 * 
	 */
	private final String folderName = "../yahoo-local/";

	/**
	 * A file stores the mapping relationship between the crawled file's name
	 * and the search criteria.
	 */
	private final String mapFileName = folderName + "mapFile";
	/**
	 * The maximum radius (in miles) of the query. TODO allocation MAX_R
	 */
	private final double MAX_R = 0.0;

	/**
	 * The maximum number of returned results by a query.
	 */
	private final int maxResults = 20;
	/**
	 * The maximum starting result position to return.
	 */
	private final int maxStart = 250;

	/**
	 * The maximum number of results on can get through this query by only
	 * changing the start value.
	 */
	private final int maxTotalResultsReturned = maxStart + maxResults; // =270;

	private boolean firstCrawl = false;

	private final long dayTime = 24 * 60 * 60 * 1000;

	public Crawler() {

	}

	/**
	 * Entry of the crawler
	 */
	private void callCrawl() {
		// state by state
		@SuppressWarnings({ "unchecked" })
		ArrayList<Envelope> envelopeStates = (ArrayList<Envelope>) UScensusData
				.MBR(UScensusData.STATE_SHP_FILE_NAME);
		@SuppressWarnings("unchecked")
		ArrayList<String> nameStates = (ArrayList<String>) UScensusData
				.stateName(UScensusData.STATE_DBF_FILE_NAME);

		FileOperator.createFolder(folderName);
		FileOperator.createFile(mapFileName);

		HttpClient httpclient = createHttpClient();

		BufferedWriter mapOutput;
		try {
			mapOutput = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(mapFileName, true)));

			// TODO Change it for different crawling machines
			String appid = "l6QevFbV34H1VKW58naZ8keJohc8NkMNvuWfVs2lR3ROJMtw63XOWBePbDcMBFfkDnU-";
			for (int i = 0; i < nameStates.size(); i++) {
				/* For counting the number of files in one gzip folder */
				int countGz = 0;
				/* all file will be compressed into one .gz file */
				List filesGz = new ArrayList<File>();
				String stateName = nameStates.get(i);
				String subFolder = FileOperator.createFolder(folderName,
						stateName);
				crawlBasedOnState(subFolder, mapOutput, envelopeStates.get(i),
						appid, 0, httpclient, countGz, filesGz, false);
			}
			mapOutput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Crawling points in US by states. All crawled .xml files will be
	 * classified into the subFolders corresponding to their states' name.
	 * 
	 * @param mapOutput
	 *            store the crawled file's name , the corresponding query
	 *            criteria, and .gz file's name.
	 * @param envelopeState
	 *            a MBR of a state
	 * @param overflow
	 *            TODO
	 * @param folder
	 *            stores the crawled .xml files, consist by a folder's name +
	 *            the state's name
	 * 
	 * @return numQueries used ?
	 */
	private int crawlBasedOnState(String subFolder, BufferedWriter mapOutput,
			Envelope envelopeState, String appid, int numQueries,
			HttpClient httpclient, int countGz, List filesGz, boolean overflow) {
		long beginTime = 0;
		String query = "*";
		int zip = 0;
		int results = maxResults;
		try {

			Envelope unit = Coverage.computeUnit(envelopeState, MAX_R);
			Envelope AEnvelope = Coverage.firstEnvelopeInRegion(envelopeState,
					unit, overflow);
			Circle circle = Coverage.computeCircle(AEnvelope);
			// indicator for the while loop
			int numSubRegions = Coverage.numsSubRegions(envelopeState, unit);
			while (numSubRegions >= 0) {
				// This loop represents traversing every sub-region.
				for (int start = 1; start <= maxStart; start += maxResults) {
					// This loop represents turning to next page.
					String url = concatenateUrl(appid, query, zip, results,
							start, circle.getCenter().y, circle.getCenter().x,
							circle.getRadius());
					// crawl...
					String partFileName = concatenateFileName(null, 0,
							numSubRegions, start, circle.getCenter().y,
							circle.getCenter().x, circle.getRadius());
					String xmlFileName = subFolder + partFileName + ".xml";

					File xmlFile = FileOperator.creatFileAscending(xmlFileName);

					// TODO need checking...
					FileOperator.writeMapFile(mapOutput, xmlFile.getName(), query,
							zip, results, start, circle.getCenter().y,
							circle.getCenter().x, circle.getRadius());

					if (firstCrawl = false) {
						firstCrawl = true;
						beginTime = System.currentTimeMillis();
					}
					fetching(httpclient, xmlFile, url);
					numQueries++;
					if (numQueries % 5000 == 0) {
						// sleep to satisfy 5000/day
						long now = System.currentTimeMillis();
						long diff = (now - beginTime);
						if (diff < dayTime) {
							Thread.currentThread().sleep(dayTime - diff);
						}
						beginTime = System.currentTimeMillis();
					}

					//
					ParseXml parseXml = new ParseXml(xmlFile);
					parseXml.parse();

					// deal with access limitations
					if (parseXml.isLimitExceeded()) {
						// TODO how to iteratively increase the sleeping time ?
						Thread.currentThread().sleep(5 * 60 * 1000); // sleep
						// TODO check
						// continue the previous loop
						start -= maxResults;
						continue;
					}

					countGz++;
					filesGz.add(xmlFile);
					if (countGz % 5000 == 0) {
						FileOperator.gzFiles(filesGz, subFolder, "local");
						filesGz.clear();
					}

					// Cannot get all available results from this query,
					// then only query the first page, and record that page.
					if (parseXml.getTotalResultsAvailable() > maxTotalResultsReturned) {
						// TODO divide the region into 4 sub-regions;
						for (int i = 0; i < 4; i++) {
							// TODO check
							crawlBasedOnState(subFolder, mapOutput, AEnvelope,
									appid, numQueries, httpclient, countGz,
									filesGz, true);
						}
					} else if (start + maxResults <= parseXml
							.getTotalResultsAvailable()) {
						// turn to next page
						continue;
					} else {
						// crawl another region
						break;
					}
				}
				// change circle to the next subRegion
				AEnvelope = Coverage.nextEnvelopeInRegion(envelopeState,
						AEnvelope, unit, overflow);
				circle = Coverage.computeCircle(AEnvelope);
				// finished crawling all points in this sub-region
				numSubRegions--;
			}
			// gzip
			FileOperator.gzFiles(filesGz, subFolder, "local");
			filesGz.clear();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in crawler", e);
		} finally {
			// TODO add it
			// try {
			// outputMap.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return -1;
	}

	/**
	 * Issue the query, and then save the returned .xml file.
	 * 
	 * @param httpclient
	 * @param xmlFile
	 * @param url
	 */
	private void fetching(HttpClient httpclient, File xmlFile, String url) {
		OutputStream output;
		try {
			output = new BufferedOutputStream(new FileOutputStream(xmlFile));
			logger.debug("fetching... " + url);
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity.writeTo(output);
			}
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		int timeout = 1000 * 10;

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
		// TODO concatenateFileName
		return null;
	}

}
