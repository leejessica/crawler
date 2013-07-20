/**
 * 
 */
package mo.umac.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.util.List;

import mo.umac.parser.YahooXmlType;

/**
 * ResultsSet (the xml page) of one query returned by Yahoo Local!
 * 
 * @author kate
 * 
 */
public class ResultSetYahoo extends ResultSet{

    // add at 2013-4-5
    /**
     * The page returned by Yahoo! Local. There are mainly two kinds of pages:
     * valid returned page, or an error page
     */
    private YahooXmlType xmlType = YahooXmlType.UNKNOWN;

    private String xmlFileName = "";

    private File xmlFile = null;

    private BufferedWriter resultsOutput;

    private int totalResultsAvailable = 0;

    private int totalResultsReturned = 0;

    private int firstResultPosition = 0;

    private List<POI> pois;

    /**
     * TODO The radius of this query circle
     */
    private double radius = 0.0;

    // private boolean limitExceeded = false;

    private boolean UnexpectedError = false;

    public ResultSetYahoo() {

    }

    public YahooXmlType getXmlType() {
	return xmlType;
    }

    public void setXmlType(YahooXmlType xmlType) {
	this.xmlType = xmlType;
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

    // public void setLimitExceeded(boolean limitExceeded) {
    // this.limitExceeded = limitExceeded;
    // }
    //
    // public boolean isLimitExceeded() {
    // return limitExceeded;
    // }

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

    public List<POI> getPOIs() {
	return pois;
    }

    public void setPOIs(List<POI> pois) {
	this.pois = pois;
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

    public BufferedWriter getResultsOutput() {
	return resultsOutput;
    }

    public void setResultsOutput(BufferedWriter resultsOutput) {
	this.resultsOutput = resultsOutput;
    }

    public double getRadius() {
	return radius;
    }

    public void setRadius(double radius) {
	this.radius = radius;
    }

}
