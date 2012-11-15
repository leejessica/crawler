/**
 * 
 */
package mo.umac.crawler.yahoo.local;

/**
 * Parse the returned xml file.
 * @author Kate Yim
 *
 */
public class ParseXml {
	
	private String xmlFile = "";
	private int totalResultsAvailable = 0;
	private int totalResultsReturned = 0;
	private int firstResultPosition = 0;
	private boolean limitExceeded = false;
	
	public ParseXml(String xmlFile) {
		this.xmlFile = xmlFile;
	}
	
	public void parse() {
		// TODO parse
	}

	public String getXmlFile() {
		return xmlFile;
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
