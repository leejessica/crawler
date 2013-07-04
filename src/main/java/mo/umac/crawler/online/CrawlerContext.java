/**
 * 
 */
package mo.umac.crawler.online;

import java.util.LinkedList;
import java.util.List;


/**
 * @author kate
 * 
 */
public class CrawlerContext {

	private OnlineYahooLocalCrawlerStrategy crawlerStrategy;

	public CrawlerContext(OnlineYahooLocalCrawlerStrategy crawlerStrategy) {
		this.crawlerStrategy = crawlerStrategy;
	}

	public void callCrawling(LinkedList<String> listNameStates,
			List<String> listCategoryNames) {
		this.crawlerStrategy.callCrawling(listNameStates, listCategoryNames);
	}

}
