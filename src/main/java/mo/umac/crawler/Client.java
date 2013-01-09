package mo.umac.crawler;

import org.apache.log4j.xml.DOMConfigurator;

public class Client {

	public static String LOG_PROPERTY_PATH = "./src/main/resources/log4j.xml";
	
//	public static String LOG_PROPERTY_PATH = "log4j.xml";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DOMConfigurator.configure(Client.LOG_PROPERTY_PATH);
		CrawlerStrategy crawlerStrategy = new QuadTreeCrawler();
		CrawlerContext crawlerContext = new CrawlerContext(crawlerStrategy);
		crawlerContext.callCrawling();
	}

}
