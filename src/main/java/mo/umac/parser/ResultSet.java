/**
 * 
 */
package mo.umac.parser;

import java.io.File;
import java.util.List;

/**
 * ResultsSet of the xml page returned by Yahoo Local!
 * 
 * @author kate
 * 
 */
public class ResultSet {
	private String xmlFileName = "";

	private File xmlFile = null;

	private int totalResultsAvailable = 0;

	private int totalResultsReturned = 0;

	private int firstResultPosition = 0;

	private List<Result> results;

	private boolean limitExceeded = false;

	private boolean UnexpectedError = false;
	
	public void setLimitExceeded(boolean limitExceeded) {
		this.limitExceeded = limitExceeded;
	}

	public void setUnexpectedError(boolean unexpectedError) {
		UnexpectedError = unexpectedError;
	}

	public boolean isUnexpectedError() {
		return UnexpectedError;
	}

	public int getTotalResultsAvailable() {
		return totalResultsAvailable;
	}

	public int getTotalResultsReturned() {
		return totalResultsReturned;
	}

	public int getFirstResultPosition() {
		return firstResultPosition;
	}

	public boolean isLimitExceeded() {
		return limitExceeded;
	}

	public String getXmlFileName() {
		return xmlFileName;
	}

	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}

	public File getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public void setTotalResultsAvailable(int totalResultsAvailable) {
		this.totalResultsAvailable = totalResultsAvailable;
	}

	public void setTotalResultsReturned(int totalResultsReturned) {
		this.totalResultsReturned = totalResultsReturned;
	}

	public void setFirstResultPosition(int firstResultPosition) {
		this.firstResultPosition = firstResultPosition;
	}

}
