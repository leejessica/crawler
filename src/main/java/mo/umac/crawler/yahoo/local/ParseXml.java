/**
 * 
 */
package mo.umac.crawler.yahoo.local;

import java.io.File;

/**
 * Parse the returned xml file.
 * @author Kate Yim
 *
 */
public class ParseXml {
	
	private String xmlFileName = "";
	private File xmlFile = null;
	private int totalResultsAvailable = 0;
	private int totalResultsReturned = 0;
	private int firstResultPosition = 0;
	private boolean limitExceeded = false;
	
	public ParseXml(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}
	
	public ParseXml(File xmlFile){
		this.xmlFile = xmlFile;
	}
	
	public void parse() {
		// TODO parse xml file ...
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
	
	public boolean isLimitExceeded(){
		return limitExceeded;
	}
	
}
