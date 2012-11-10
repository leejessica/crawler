/**
 * 
 */
package mo.umac.crawler.geo;

import com.vividsolutions.jts.geom.Envelope;


/**
 * @author Kate Yim
 *
 */
public class USCity {
	private Envelope cityBoundary = null;
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
	private Envelope boundCity() {
		//TODO
		return cityBoundary;
	}
}
