package mo.umac.mo.crawler.yahoo.local.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ApiFetcher {
	
	//crawls the xml from url and changes it into DOM tree (using Jsoup)
	public static Document getDocoment(String query, String zip, int result) {
		String url = "http://local.yahooapis.com/LocalSearchService/V3/localSearch?appid=YahooDemo&query=" +
				query + "&zip=" + zip + "&results=" + result;
		//crawls the xml from url and changes it into DOM tree (using Jsoup)
		Document doc = XmlFetcher.getHtml(url);
		
		/*Demo for extracting the desired information*/
		//get all the "Result" nodes
		Elements results = doc.getElementsByTag("Result");
		//get the corresponding "Title" node under each "Result" node
		for (Element resultElem : results) {
			System.out.println(resultElem.getElementsByTag("Title").first().text());
		}
		/*Demo end*/
		
		return doc;
	}
	
	//only save the xml to "demo.xml"
	public static void fetchAPI(String query, String zip, int result) {
		
		Document doc = getDocoment(query, zip, result);
		//save the whole xml into "demo.xml"
		File demo = new File("/home/xulijie/Desktop/demo.xml");
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(demo));
			writer.write(doc.html());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//save the query/zip/result and corresponding xml text into a plain file
	public static void fetchAPIBetter(BufferedWriter writer, String query, String zip, int result) {
		
		Document doc = getDocoment(query, zip, result);
		
		try {
			writer.write(query);
			writer.write("\t");
			writer.write(zip);
			writer.write("\t");
			writer.write(result);
			writer.write("\t");
  
			writer.write(doc.text());
			writer.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		
		File outputFile = new File("/home/xulijie/Desktop/crawlResults.txt");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write("query\tzip\tresult\txmlText");
			fetchAPIBetter(writer, "pizza", "94306", 2);
			Thread.sleep(1000);
			fetchAPIBetter(writer, "apple", "94306", 3);
			Thread.sleep(1000);	
			fetchAPIBetter(writer, "iphone", "94301", 4);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}

class XmlFetcher {
	public static Document getHtml(String url) {
		Document doc = null;
		try {
			doc = Jsoup
			.connect(url)
			.ignoreContentType(true) //ignore the content type of this html
			.header("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1")
			.header("Accept", "text/html,application/xhtml+xml")
			.header("Accept-Language", "zh-cn,zh;q=0.5")
			.header("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7")
			.get();


		} catch (IOException e) {

			e.printStackTrace();
		}
		return doc;
	}

}
