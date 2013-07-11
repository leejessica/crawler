package mo.umac.crawler;

import java.util.LinkedList;
import java.util.List;

import mo.umac.crawler.offline.SliceCrawler;
import mo.umac.crawler.online.OnlineYahooLocalCrawlerStrategy;
import mo.umac.crawler.online.QuadTreeCrawler;
import mo.umac.spatial.UScensusData;

import org.apache.log4j.xml.DOMConfigurator;

public class Main {

    public static String LOG_PROPERTY_PATH = "./src/main/resources/log4j.xml";

    public static void main(String[] args) {
	DOMConfigurator.configure(Main.LOG_PROPERTY_PATH);

	/************************* Change these lines *************************/
	initForServer(false);
	// YahooLocalCrawlerStrategy crawlerStrategy = new QuadTreeCrawler();
	YahooLocalCrawlerStrategy crawlerStrategy = new SliceCrawler();
	/**************************************************************************/

	CrawlerContext crawlerContext = new CrawlerContext(crawlerStrategy);
	//
	LinkedList<String> listNameStates = new LinkedList<String>();
	String city1 = "NY";
	listNameStates.add(city1);

	List<String> listCategoryNames = new LinkedList<String>();
	// String category1 = "Hotels & Motels";
	String category2 = "Restaurants";
	// listCategoryNames.add(category1);
	listCategoryNames.add(category2);
	//
	crawlerContext.callCrawling(listNameStates, listCategoryNames);
    }

    /**
     * If packaging, then changing the destiny of paths of the configure files
     * 
     * @param packaging
     */
    public static void initForServer(boolean packaging) {
	if (packaging) {
	    // for packaging, set the resources folder as
	    OnlineYahooLocalCrawlerStrategy.PROPERTY_PATH = "target/crawler.properties";
	    OnlineYahooLocalCrawlerStrategy.CATEGORY_ID_PATH = "target//cat_id.txt";
	    Main.LOG_PROPERTY_PATH = "target/log4j.xml";
	    UScensusData.STATE_SHP_FILE_NAME = "target/UScensus/tl_2012_us_state/tl_2012_us_state.shp";
	    UScensusData.STATE_DBF_FILE_NAME = "target/UScensus/tl_2012_us_state/tl_2012_us_state.dbf";
	} else {
	    // for debugging, set the resources folder as
	    OnlineYahooLocalCrawlerStrategy.PROPERTY_PATH = "./src/main/resources/crawler.properties";
	    OnlineYahooLocalCrawlerStrategy.CATEGORY_ID_PATH = "./src/main/resources/cat_id.txt";
	    Main.LOG_PROPERTY_PATH = "./src/main/resources/log4j.xml";
	    UScensusData.STATE_SHP_FILE_NAME = "./src/main/resources/UScensus/tl_2012_us_state/tl_2012_us_state.shp";
	    UScensusData.STATE_SHP_FILE_NAME = "./src/main/resources/UScensus/tl_2012_us_state/tl_2012_us_state.shp";
	}
    }

}
