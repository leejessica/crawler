/**
 * 
 */
package mo.umac.algorithms;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author kate
 * 
 */
public class Quadtree extends CrawlerStrategy {

	@Override
	public Envelope firstEnvelopeInRegion(Envelope region, Envelope unit,
			boolean overflow) {
		return new Envelope(region.getMinX(), region.getMinX()
				+ unit.getWidth() / 2, region.getMinY(), region.getMinY()
				+ unit.getHeight() / 2);
	}

}
