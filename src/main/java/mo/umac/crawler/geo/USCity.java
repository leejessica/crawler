/**
 * 
 */
package mo.umac.crawler.geo;

import mo.umac.crawler.utils.Rectangle;

/**
 * @author Kate YAN
 *
 */
public class USCity {
	private Rectangle cityBoundary = null;
	private String cityName = "";
	
	/**
	 * Construct the country of US
	 */
	public USCity() {
		
	}
	
	/**
	 * Construct a city of US
	 * @param cityName A specified city in US
	 */
	public USCity(String cityName) {
		this.cityName = cityName;
	}
	
	/**
	 * Using a rectangle to bound this city or this country.
	 * @return
	 */
	private Rectangle boundCity() {
		//TODO
		return cityBoundary;
	}
}
