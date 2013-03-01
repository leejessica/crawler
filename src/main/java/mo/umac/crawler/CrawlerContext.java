/**
 * 
 */
package mo.umac.crawler;

/**
 * @author kate
 *
 */
public class CrawlerContext{

	private OnlineYahooLocalCrawlerStrategy crawlerStrategy;
	
	public CrawlerContext(OnlineYahooLocalCrawlerStrategy crawlerStrategy){
		this.crawlerStrategy = crawlerStrategy;
	}
	
	public void callCrawling(){
		this.crawlerStrategy.callCrawling(null);
	}
	
	public void callCrawling(String category){
		this.crawlerStrategy.callCrawling(category);
	}
	
}
