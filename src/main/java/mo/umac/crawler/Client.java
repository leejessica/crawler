package mo.umac.crawler;

import mo.umac.geo.UScensusData;

import org.apache.log4j.xml.DOMConfigurator;

public class Client {

	public static String LOG_PROPERTY_PATH = "./src/main/resources/log4j.xml";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialResourceFolder(false);
		DOMConfigurator.configure(Client.LOG_PROPERTY_PATH);
		CrawlerStrategy crawlerStrategy = new QuadTreeCrawler();
		CrawlerContext crawlerContext = new CrawlerContext(crawlerStrategy);
//		String category = null;
		String category = "Restaurants";
		crawlerContext.callCrawling(category);
	}

	/**
	 * If packaging, then changing the destiny of paths of the configure files
	 * 
	 * @param packaging
	 */
	public static void initialResourceFolder(boolean packaging) {
		if (packaging) {
			// for packaging, set the resources folder as
			CrawlerStrategy.PROPERTY_PATH = "target/crawler.properties";
			CrawlerStrategy.CATEGORY_ID_PATH = "target//cat_id.txt";
			Client.LOG_PROPERTY_PATH = "target/log4j.xml";
			UScensusData.STATE_SHP_FILE_NAME = "target/UScensus/tl_2012_us_state/tl_2012_us_state.shp";
			UScensusData.STATE_DBF_FILE_NAME = "target/UScensus/tl_2012_us_state/tl_2012_us_state.dbf";
		} else {
			// for debugging, set the resources folder as
			CrawlerStrategy.PROPERTY_PATH = "./src/main/resources/crawler.properties";
			CrawlerStrategy.CATEGORY_ID_PATH = "./src/main/resources/cat_id.txt";
			Client.LOG_PROPERTY_PATH = "./src/main/resources/log4j.xml";
			UScensusData.STATE_SHP_FILE_NAME = "./src/main/resources/UScensus/tl_2012_us_state/tl_2012_us_state.shp";
			UScensusData.STATE_SHP_FILE_NAME = "./src/main/resources/UScensus/tl_2012_us_state/tl_2012_us_state.shp";
		}
	}

}
