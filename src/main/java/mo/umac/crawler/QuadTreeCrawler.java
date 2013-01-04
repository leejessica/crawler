/**
 * 
 */
package mo.umac.crawler;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import mo.umac.parser.ResultSet;
import mo.umac.utils.Circle;
import mo.umac.utils.FileOperator;
import mo.umac.utils.UScensusData;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author kate
 *
 */
public class QuadTreeCrawler extends CrawlerStrategy {
	//
	public static Logger logger = Logger.getLogger(QuadTreeCrawler.class
			.getName());

	public QuadTreeCrawler() {

	}

	/**
	 * Entrance of the crawler
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

			// Change it for different crawling machines
			String appid = "l6QevFbV34H1VKW58naZ8keJohc8NkMNvuWfVs2lR3ROJMtw63XOWBePbDcMBFfkDnU-";
			for (int i = 0; i < nameStates.size(); i++) {
				String stateName = nameStates.get(i);
				String subFolder = FileOperator.createFolder(folderName,
						stateName);
				Envelope firstEnvelope = null;
				Envelope envelopeState = envelopeStates.get(i);
				Envelope unit = Coverage.computeUnit(envelopeState, MAX_R);
				Envelope aEnvelope = firstEnvelope;
				Envelope region = envelopeState;

				crawl(appid, subFolder, aEnvelope, region, unit, false);
				// FIXME not finished
			}
			mapOutput.close();
			httpClient.getConnectionManager().shutdown();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
	 * @param folder
	 *            stores the crawled .xml files, consist by a folder's name +
	 *            the state's name
	 * 
	 * @return true: End; false: Not End
	 */
	public boolean crawlOne(String appid, String subFolder,
			Envelope preEnvelope, Envelope region, Envelope unit,
			boolean overflow) {
		Envelope aEnvelope;
		// If it is the last region, then end the crawling process.
		if (finishedCrawling(preEnvelope, region)) {
			return true;
		}
		// This loop represents traversing every sub-region
		aEnvelope = nextEnvelopeInRegion(region, preEnvelope, unit, overflow);
		crawl(appid, subFolder, preEnvelope, region, unit, overflow);
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

	public boolean crawl(String appid, String subFolder, Envelope preEnvelope,
			Envelope region, Envelope unit, boolean overflow) {
		Envelope aEnvelope;
		// If it is the last region, then end the crawling process.
		if (finishedCrawling(preEnvelope, region)) {
			return true;
		}
		// This region can be crawled at once
		if (inOneCrawling(region, unit)) {
			// the first page for any query
			int start = 1;
			Circle circle = Coverage.computeCircle(aEnvelope);
			QueryCondition qc = new QueryCondition(subFolder, mapOutput,
					region, appid, start, circle, numQueries, overflow, query,
					zip, results);
			ResultSet resultSet = query(qc);
			numQueries++;
			// This loop represents turning over the page.
			int maxStartForThisQuery = maxStartForThisQuery(resultSet);
			for (start += maxResults; start < maxStartForThisQuery; start += maxResults) {
				qc = new QueryCondition(subFolder, mapOutput, region, appid,
						start, circle, numQueries, overflow, query, zip,
						results);
				query(qc);
				numQueries++;
			}
		} else {
			// divide this region for further crawling
		}
		// This loop represents traversing every sub-region
		// aEnvelope = nextEnvelopeInRegion(region, preEnvelope, unit,
		// overflow);

		return false;
	}

	private boolean inOneCrawling(Envelope region, Envelope unit) {
		// TODO Auto-generated method stub
		return false;
	}
}
