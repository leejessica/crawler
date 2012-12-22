/**
 * 
 */
package mo.umac.crawler.yahoo.local.test;

import mo.umac.parser.Result;
import mo.umac.parser.ResultSet;
import mo.umac.parser.StaXParser;

/**
 * @author kate
 * 
 */
public class StaXParserTest {

	public static void main(String args[]) {
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

}
