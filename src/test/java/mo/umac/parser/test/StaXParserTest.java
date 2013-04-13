/**
 * 
 */
package mo.umac.parser.test;

import org.apache.log4j.xml.DOMConfigurator;

import mo.umac.crawler.Client;
import mo.umac.parser.Result;
import mo.umac.parser.ResultSet;
import mo.umac.parser.StaXParser;

/**
 * @author kate
 * 
 */
public class StaXParserTest {

	public static void main(String args[]) {
		DOMConfigurator.configure(Client.LOG_PROPERTY_PATH);
		StaXParserTest test = new StaXParserTest();
		test.parsePage();
	}

	private void parsePage() {
		StaXParser read = new StaXParser();
		String testXmlFile = "./src/test/resources/localSearch.xml";
		ResultSet resultSet = read.readConfig(testXmlFile);
		System.out.println(resultSet.getTotalResultsAvailable());
		System.out.println(resultSet.getTotalResultsReturned());
		System.out.println(resultSet.getFirstResultPosition());
		for (Result result : resultSet.getResults()) {
			System.out.println(result.toString());
			System.out.println(result.getRating().toString());
			for (int i = 0; i < result.getCategories().size(); i++) {
				System.out.println(result.getCategories().get(i).toString());
			}
		}
	}

	private void parseErrorPage() {
		StaXParser read = new StaXParser();
		String testXmlFile = "./src/test/resources/limitexceed.xml";
		ResultSet resultSet = read.readConfig(testXmlFile);
		System.out.println(resultSet.getXmlType());
	}

}
