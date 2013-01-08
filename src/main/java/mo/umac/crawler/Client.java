package mo.umac.crawler;

public class Client {

	public static String LOG_PROPERTY_PATH = "./src/main/resources/log4j.xml";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CrawlerStrategy crawlerStrategy = new QuadTreeCrawler();
		CrawlerContext crawlerContext = new CrawlerContext(crawlerStrategy);
		crawlerContext.callCrawling();
	}

}
