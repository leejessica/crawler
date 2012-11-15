/**
 * 
 */
package mo.umac.crawler.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author Kate Yim
 * 
 */
public class UScensusData {

	/**
	 * The geometry information file (.shp) for the US states. It has been downloaded from
	 * {@link http://www.census.gov/geo/maps-data/data/tiger.html}
	 */
	public static String STATE_SHP_FILE_NAME = "./src/main/resources/UScensus/tl_2012_us_state/tl_2012_us_state.shp";
	
	/**
	 * The geometry information file (.dbf) for the US states. It has been downloaded from
	 * {@link http://www.census.gov/geo/maps-data/data/tiger.html}
	 */
	public static String STATE_DBF_FILE_NAME = "./src/main/resources/UScensus/tl_2012_us_state/tl_2012_us_state.dbf";
		
	/**
	 * Get the minimum boundary rectangles from the .shp file.
	 * 
	 * @param shpFileName
	 *            .shp file
	 * @return An array contains all MBRs of the areas contained in the .shp file. 
	 */
	public static List MBR(String shpFileName) {
		List<Envelope> envelopeList = new ArrayList<Envelope>();
		try {
			ShpFiles shpFiles = new ShpFiles(shpFileName);
			GeometryFactory gf = new GeometryFactory();
			ShapefileReader r = new ShapefileReader(shpFiles, true, true, gf);
			while (r.hasNext()) {
				Geometry shape = (Geometry) r.nextRecord().shape();
				Envelope envelope = shape.getEnvelopeInternal();
				envelopeList.add(envelope);
			}
			r.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ShapefileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return envelopeList;
	}
	
	/**
	 * Get the list of States and Equivalent Entities' name from .dbf file
	 * 
	 * @param dbfFileName .dbf file
	 * @return An array contains all names in the .dbf file
	 */
	public static List stateName(String dbfFileName) {
		List<String> stateNameList = new ArrayList<String>();
		// TODO read the .dbf file
		// TODO test whether the order corresponds to the .shp file 
		return stateNameList;
	}


}
