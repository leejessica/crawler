package mo.umac.crawler.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegularExpression {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "/daily_news/all/db_name/cn-9083471239847";
		String rx = "^daily_news/filter_url/";
		Pattern pattern = Pattern.compile(rx);
		Matcher matcher = pattern.matcher(url);
		System.out.println(matcher.matches());

	}

}
