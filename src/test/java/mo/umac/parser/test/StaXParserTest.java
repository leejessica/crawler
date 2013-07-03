/**
 * 
 */
package mo.umac.parser.test;

import org.apache.log4j.xml.DOMConfigurator;

import mo.umac.crawler.Main;
import mo.umac.parser.POI;
import mo.umac.parser.YahooResultSet;
import mo.umac.parser.StaXParser;

/**
 * @author kate
 * 
 */
public class StaXParserTest {

	public static void main(String args[]) {
		DOMConfigurator.configure(Main.LOG_PROPERTY_PATH);
		StaXParserTest test = new StaXParserTest();
		test.parseErrorPage3();
	}

	private void parsePage() {
		StaXParser read = new StaXParser();
		String testXmlFile = "./src/test/resources/returnedpages/demo.xml";
		YahooResultSet resultSet = read.readConfig(testXmlFile);
		System.out.println(resultSet.getTotalResultsAvailable());
		System.out.println(resultSet.getTotalResultsReturned());
		System.out.println(resultSet.getFirstResultPosition());
		for (POI result : resultSet.getPOIs()) {
			System.out.println(result.toString());
			System.out.println(result.getRating().toString());
			for (int i = 0; i < result.getCategories().size(); i++) {
				System.out.println(result.getCategories().get(i).toString());
			}
		}
	}

	private void parseErrorPage1() {
		StaXParser read = new StaXParser();
		String testXmlFile = "./src/test/resources/returnedpages/limitexceed.xml";
		YahooResultSet resultSet = read.readConfig(testXmlFile);
		System.out.println(resultSet.getXmlType());
	}
	
	private void parseErrorPage2() {
		StaXParser read = new StaXParser();
		String testXmlFile = "./src/test/resources/returnedpages/invalidvalue.xml";
		YahooResultSet resultSet = read.readConfig(testXmlFile);
		System.out.println(resultSet.getXmlType());
	}
	
	private void parseErrorPage3() {
		// XXX I'm not sure whether this condition will appear in Yahoo! Local.
		StaXParser read = new StaXParser();
		String testXmlFile = "./src/test/resources/returnedpages/empty.xml";
		YahooResultSet resultSet = read.readConfig(testXmlFile);
		System.out.println(resultSet.getXmlType());
	}

}
