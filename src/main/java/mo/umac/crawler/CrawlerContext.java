/**
 * 
 */
package mo.umac.crawler;

/**
 * @author kate
 *
 */
public class CrawlerContext{

	private CrawlerStrategy crawlerStrategy;
	
	public CrawlerContext(CrawlerStrategy crawlerStrategy){
		this.crawlerStrategy = crawlerStrategy;
	}
	
	public void callCrawling(){
		this.crawlerStrategy.callCrawling();
	}
	
}
