/**
 * 
 */
package mo.umac.crawler;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import mo.umac.parser.ResultSet;
import mo.umac.parser.StaXParser;
import mo.umac.utils.Circle;
import mo.umac.utils.FileOperator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Kate Yim
 * 
 */
public abstract class CrawlerStrategy {

	//
	public static Logger logger = Logger.getLogger(CrawlerStrategy.class
			.getName());

	/**
	 * A folder stores all crawled .xml file from Yahoo Local.
	 * 
	 */
	protected final String folderName = "../yahoo-local/";

	/**
	 * A file stores the mapping relationship between the crawled file's name
	 * and the search criteria.
	 */
	protected final String mapFileName = folderName + "mapFile";
	/**
	 * The maximum radius (in miles) of the query. TODO allocation MAX_R
	 */
	protected final double MAX_R = 0.0;

	/**
	 * The maximum number of returned results by a query.
	 */
	protected final int maxResults = 20;
	/**
	 * The maximum starting result position to return.
	 */
	protected final int maxStart = 250;

	/**
	 * The maximum number of results on can get through this query by only
	 * changing the start value.
	 */
	protected final int maxTotalResultsReturned = maxStart + maxResults; // =270;

	protected boolean firstCrawl = false;

	protected final long dayTime = 24 * 60 * 60 * 1000;

	/**
	 * Record the time, because of the restriction of 5000 queries per day
	 */
	protected long beginTime = 0;

	protected HttpClient httpClient;

	protected BufferedWriter mapOutput;

	protected int numQueries;

	protected String query = "*";

	protected int zip = 0;

	protected int results = maxResults;

	protected Envelope firstEnvelope = null;

	/**
	 * Crawl points in US by states. All crawled .xml files will be classified
	 * into the subFolders corresponding to their states' name.
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
	 * @return true: End; false: Not End
	 */
	public boolean crawl(String appid, String subFolder, Envelope preEnvelope,
			Envelope region, Envelope unit, boolean overflow) {
		Envelope aEnvelope;
		// If it is the last region, then end the crawling process.
		if (finishedCrawling(preEnvelope, region)) {
			return true;
		}
		// This loop represents traversing every sub-region
		aEnvelope = nextEnvelopeInRegion(region, preEnvelope, unit, overflow);
		Circle circle = Coverage.computeCircle(aEnvelope);
		// the first page for any query
		int start = 1;
		QueryCondition qc = new QueryCondition(subFolder, mapOutput, region,
				appid, start, circle, numQueries, overflow, query, zip, results);
		ResultSet resultSet = query(qc);
		numQueries++;
		// This loop represents turning over the page.
		int maxStartForThisQuery = maxStartForThisQuery(resultSet);
		for (start += maxResults; start < maxStartForThisQuery; start += maxResults) {
			qc = new QueryCondition(subFolder, mapOutput, region, appid, start,
					circle, numQueries, overflow, query, zip, results);
			query(qc);
			numQueries++;
		}
		return false;
	}

	/**
	 * Get next region according to the previous envelope
	 * 
	 * @param envelopeState
	 *            The MBR of all regions
	 * @param aEnvelope
	 *            previous region
	 * @param unit
	 *            the unit region
	 * @param overflow
	 * @return
	 */
	public Envelope nextEnvelopeInRegion(Envelope region,
			Envelope previousEnvelope, Envelope unit, boolean overflow) {
		if (previousEnvelope == null) {
			return firstEnvelopeInRegion(region, unit, overflow);
		} else {
			return nextButNotFirstEnvelopeInRegion(region, previousEnvelope,
					unit, overflow);
		}
	}

	/**
	 * Get the first envelope in this region.
	 * 
	 * @param region
	 *            : the whole region need to be covered
	 * @param unit
	 *            : the unit rectangle
	 * @param overflow
	 *            : if overflow=true, then divide the rectangle, else find the
	 *            left-corner rectangle in the region.
	 * @return
	 */
	public abstract Envelope firstEnvelopeInRegion(Envelope region,
			Envelope unit, boolean overflow);

	/**
	 * Compute the next envelope (not the first one!)
	 * 
	 * @param region
	 * @param unit
	 * @param overflow
	 * @return
	 */
	public Envelope nextButNotFirstEnvelopeInRegion(Envelope region,
			Envelope previousEnvelope, Envelope unit, boolean overflow) {
		// TODO check
		double x1 = 0;
		double y1 = 0;
		// Get to the right-most boundary
		if (previousEnvelope.getMaxX() >= region.getMaxX()) {
			x1 = region.getMinX();
			y1 = previousEnvelope.getMaxY();
		} else {
			x1 = previousEnvelope.getMaxX();
			y1 = previousEnvelope.getMinY();
		}
		Envelope next = new Envelope(x1, x1 + unit.getWidth(), y1, y1
				+ unit.getHeight());
		return next;
	}

	/**
	 * The query process, including construct the url; create the .xml file;
	 * fetching from the web; parse the .xml file, storing the result in the
	 * in-memory db, etc.
	 * 
	 * @param qc
	 *            All information need in one query
	 * @return The parsed result set.
	 */
	protected ResultSet query(QueryCondition qc) {
		String url = qc.toUrl();
		// file's name
		// String partFileName = concatenateFileName(null, 0, results,
		// qc.getStart(), qc.getCircle().getCenter().y, qc.getCircle()
		// .getCenter().x, qc.getCircle().getRadius());
		// String xmlFileName = subFolder + partFileName + ".xml";
		// File xmlFile = FileOperator.creatFileAscending(xmlFileName);

		File xmlFile = FileOperator.createFileAutoAscending(qc.getSubFolder(),
				qc.getNumQueries(), ".xml");

		// FileOperator.writeMapFile(mapOutput, xmlFile.getName(), query, zip,
		// results, start, circle.getCenter().y, circle.getCenter().x,
		// circle.getRadius());

		if (firstCrawl = false) {
			firstCrawl = true;
			beginTime = System.currentTimeMillis();
		}
		checkTime(qc.getNumQueries(), beginTime);
		fetching(httpClient, xmlFile, url);
		//
		StaXParser parseXml = new StaXParser();
		ResultSet resultSet = parseXml.readConfig(xmlFile.getPath());
		// 
		if (resultSet.getResults() != null) {
			FileOperator.writeMapFile(xmlFile.getName(), qc);
		}
		return resultSet;
	}

	protected void analyzeResultSet(ResultSet resultSet) {
		if (resultSet.isLimitExceeded()) {
			// TODO wait
		}
		if (resultSet.isUnexpectedError()) {
			// TODO record which .xml file is error.
			logger.error("TODO");
			// TODO exit or change to next region!
			// System.exit(0);
//			aEnvelope = firstEnvelope;
			// TODO change to next region
			// region = ?
		}
		// Cannot get all tuples by turning over the page
		if (resultSet.getTotalResultsAvailable() > maxTotalResultsReturned) {
			// continue crawling, because this returned tuples are useful in
			// analyzing data distributions in overflow queries
			// do nothing: continue turn over the page
		}
		// underflow
		if (resultSet.getResults() == null) {
			// go to next region
			// TODO change qc
		}
	}

	/**
	 * Issue the query, and then save the returned .xml file.
	 * 
	 * @param httpclient
	 * @param xmlFile
	 * @param url
	 */
	protected void fetching(HttpClient httpclient, File xmlFile, String url) {
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
	 * Check whether it still follows the query limitation
	 * 
	 * @param numQueries
	 * @param beginTime
	 */
	protected void checkTime(int numQueries, long beginTime) {
		if (numQueries % 5000 == 0) {
			// sleep to satisfy 5000/day
			long now = System.currentTimeMillis();
			long diff = (now - beginTime);
			if (diff < dayTime) {
				try {
					Thread.currentThread().sleep(dayTime - diff);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			beginTime = System.currentTimeMillis();
		}
	}

	/**
	 * Calculate the maximum start number
	 * 
	 * @param resultSet
	 * @return the max start value in constructing a query.
	 */
	protected int maxStartForThisQuery(ResultSet resultSet) {
		int totalResultAvailable = resultSet.getTotalResultsAvailable();
		if (totalResultAvailable > maxStart + maxResults) {
			return maxStart;
		}
		return (int) (Math.floor(1.0 * totalResultAvailable / maxResults) * 20);
	}

	protected List<Envelope> divideARectangle() {
		return null;
	}

	/**
	 * Next region, or next one in four!
	 * 
	 * @param preQC
	 * @param resultSet
	 * @return
	 * @deprecated
	 */
	protected QueryCondition nextQC(QueryCondition preQC, ResultSet resultSet) {
		// deal with access limitations
		if (resultSet == null) {
			// TODO how to iteratively increase the sleeping time ?
			try {
				Thread.currentThread().sleep(5 * 60 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // sleep
			return preQC;
		}
		// Cannot get all available results from this query,
		// then only query the first page, and record that page.
		/*
		 * if (resultSet.getTotalResultsAvailable() > maxTotalResultsReturned) {
		 * // TODO divide the region into 4 sub-regions; for (int i = 0; i < 4;
		 * i++) { // FIXME wrong crawlBasedOnState(subFolder, mapOutput,
		 * AEnvelope, appid, numQueries, httpClient, countGz, filesGz, true); }
		 * } else if (start + maxResults <=
		 * resultSet.getTotalResultsAvailable()) { // turn to next page
		 * continue; } else { // crawl another region break; }
		 */
		return null;
	}

	/**
	 * @param previousEnvelope
	 * @param region
	 * @return
	 */
	protected boolean finishedCrawling(Envelope previousEnvelope,
			Envelope region) {
		// Get to the right-most boundary
		if (previousEnvelope.getMaxX() >= region.getMaxX()) {
			// Get to the upper-most boundary
			if (previousEnvelope.getMaxY() >= region.getMaxY()) {
				return true;
			}
		}
		return false;
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
	protected String concatenateFileName(String query, int zip, int results,
			int start, double latitude, double longitude, double radius) {
		StringBuffer sb = new StringBuffer();
		if (query != null) {
			sb.append("query=");
			sb.append(query);
		}
		if (zip > 0) {
			sb.append(",zip=");
			sb.append(zip);
		}
		if (results > 0) {
			sb.append(",results=");
			sb.append(results);
		}
		if (start > 0) {
			sb.append(",start=");
			sb.append(start);
		}
		if (latitude > 0) {
			sb.append(",latitude=");
			sb.append(latitude);
		}
		if (longitude > 0) {
			sb.append(",longitude=");
			sb.append(longitude);
		}
		if (radius > 0) {
			sb.append(",radius=");
			sb.append(radius);
		}
		return sb.toString();
	}
}
