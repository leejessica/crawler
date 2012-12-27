/**
 * 
 */
package mo.umac.crawler;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author kate
 * 
 */
public class Ergodic extends CrawlerStrategy {

	@Override
	public Envelope firstEnvelopeInRegion(Envelope region, Envelope unit,
			boolean overflow) {
		return new Envelope(region.getMinX(), region.getMinX()
				+ unit.getWidth(), region.getMinY(), region.getMinY()
				+ unit.getHeight());
	}

}
