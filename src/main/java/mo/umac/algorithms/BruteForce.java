/**
 * 
 */
package mo.umac.algorithms;

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

import mo.umac.utils.Circle;
import mo.umac.utils.FileOperator;
import mo.umac.utils.UScensusData;
import mo.umac.crawler.Coverage;
import mo.umac.crawler.QueryCondition;
import mo.umac.parser.ResultSet;
import mo.umac.parser.StaXParser;

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
import org.geotools.feature.visitor.MaxVisitor.MaxResult;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Conduct a search using the Yahoo! Local search Service.
 * 
 * @author Kate Yim
 * 
 */
public class BruteForce extends AlgorithmStrategy{
	//
	public static Logger logger = Logger.getLogger(BruteForce.class.getName());

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

	/**
	 * Record the time, because of the restriction of 5000 queries per day
	 */
	private long beginTime = 0;

	HttpClient httpClient;

	BufferedWriter mapOutput;

	public BruteForce() {

	}

	/**
	 * Entry of the crawler
	 */
	public void callCrawling() {
		// state by state
		@SuppressWarnings({ "unchecked" })
		ArrayList<Envelope> envelopeStates = (ArrayList<Envelope>) UScensusData
				.MBR(UScensusData.STATE_SHP_FILE_NAME);
		@SuppressWarnings("unchecked")
		ArrayList<String> nameStates = (ArrayList<String>) UScensusData
				.stateName(UScensusData.STATE_DBF_FILE_NAME);

		FileOperator.createFolder("", folderName);
		FileOperator.createFile(mapFileName);

		httpClient = createHttpClient();

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
				// initial crawl
				String query = "*";
				int zip = 0;
				int results = maxResults;
				Envelope firstEnvelope = null;
				Envelope unit = Coverage.computeUnit(envelopeState, MAX_R);
				Envelope aEnvelope = firstEnvelope;
				Envelope region = envelopeState;
				int numSubRegions = Coverage
						.numsSubRegions(envelopeState, unit);
				crawl(subFolder, mapOutput, envelopeStates.get(i), appid, 0,
						false);
			}
			mapOutput.close();
			httpClient.getConnectionManager().shutdown();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

/*
	*//**
	 * Using HttpClient to crawl the xml web page.
	 * 
	 * The access limitation is 5000 queries per day.
	 * 
	 * @param urls
	 *            A set of urls
	 * @param filePaths
	 *            Urls' corresponding filePaths
	 * @deprecated
	 *//*
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

	*//**
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
	 *//*
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

*/

}
