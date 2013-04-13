package mo.umac.geo.test;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * This example reads data for point locations and associated attributes from a
 * comma separated text (CSV) file and exports them as a new shapefile. It
 * illustrates how to build a feature type.
 * <p>
 * Note: to keep things simple in the code below the input file should not have
 * additional spaces or tabs between fields.
 */

public class Honglin {
	public static void main(String[] args) throws Exception {

		String folderPath = "E:\\data\\yahoolocal\\";
		List<String> filelist = traverseFolder(folderPath);

		/*
		 * We use the DataUtilities class to create a FeatureType that will
		 * describe the data in our shapefile.
		 * 
		 * See also the createFeatureType method below for another, more
		 * flexible approach.
		 */
		final SimpleFeatureType TYPE = DataUtilities.createType("Location",
				"location:Point:srid=4326," + // <- the geometry attribute:
												// Point type
						"title:String," + // <- a String attribute
						"number:Integer" // a number attribute
		);

		/*
		 * A list to collect features as we create them.
		 */
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();

		for (int i = 0; i < filelist.size(); i++) {
			File file = new File(filelist.get(i));

			/*
			 * GeometryFactory will be used to create the geometry attribute of
			 * each feature (a Point object for the location)
			 */
			GeometryFactory geometryFactory = JTSFactoryFinder
					.getGeometryFactory();

			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				/* First line of the data file is the header */
				// String line = reader.readLine();
				// System.out.println("Header: " + line);

				for (String line = reader.readLine(); line != null; line = reader
						.readLine()) {
					if (line.trim().length() > 0) { // skip blank lines
						String tokens[] = line.split("\\;");

						double latitude = Double.parseDouble(tokens[5]);
						double longitude = Double.parseDouble(tokens[6]);
						String title = tokens[2].trim();
						int number = Integer.parseInt(tokens[1].trim());

						/* Longitude (= x coord) first ! */
						Point point = geometryFactory
								.createPoint(new Coordinate(longitude, latitude));

						featureBuilder.add(point);
						featureBuilder.add(title);
						featureBuilder.add(number);
						SimpleFeature feature = featureBuilder
								.buildFeature(null);
						features.add(feature);
					}
				}
			} finally {
				reader.close();
			}
		}
		// features.add(feature);

		/*
		 * Get an output file name and create the new shapefile
		 */
		// File newFile = getNewShapeFile(file);
		// System.out.println("test");

		String shapeFilePath = "E:/data/shapeFile/restaurant/restaurant.shp";
		File newFile = new File(shapeFilePath);

		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory
				.createNewDataStore(params);
		newDataStore.createSchema(TYPE);

		/*
		 * You can comment out this line if you are using the createFeatureType
		 * method (at end of class file) rather than DataUtilities.createType
		 */
		newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
		/*
		 * Write the features to the shapefile
		 */
		Transaction transaction = new DefaultTransaction("create");

		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore
				.getFeatureSource(typeName);

		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

			/*
			 * SimpleFeatureStore has a method to add features from a
			 * SimpleFeatureCollection object, so we use the
			 * ListFeatureCollection class to wrap our list of features.
			 */
			SimpleFeatureCollection collection = new ListFeatureCollection(
					TYPE, features);
			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(collection);
				transaction.commit();

			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();

			} finally {
				transaction.close();
			}
			// System.exit(0); // success!
		} else {
			System.out
					.println(typeName + " does not support read/write access");
			System.exit(1);
		}
		// System.out.println("test");

		System.exit(0); // success!
	}

	/**
	 * @param folderPath
	 * @return
	 */
	// @SuppressWarnings("rawtypes")
	public static List<String> traverseFolder(String folderPath) {
		List<String> list = new ArrayList<String>();
		File folder = new File(folderPath);
		recursivelyTraverse(folder, list);
		return list;
	}

	// @SuppressWarnings({ "unchecked", "rawtypes" })
	private static void recursivelyTraverse(File file, List<String> list) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				recursivelyTraverse(files[i], list);
			}
		} else {
			if (!file.getName().contains("~")
					&& file.getName().equals("results")) {
				list.add(file.getAbsolutePath());
				System.out.println(file.getName());
				System.out.println(file.getAbsolutePath());
			}
		}
	}

	/**
	 * Prompt the user for the name and path to use for the output shapefile
	 * 
	 * @param csvFile
	 *            the input csv file used to create a default shapefile name
	 * 
	 * @return name and path for the shapefile as a new File object
	 */
	/**
	 * private static File getNewShapeFile(File csvFile) { String path =
	 * csvFile.getAbsolutePath(); String newPath =
	 * "E:/data/yahoolocal/res"+".shp"; // try { //FileWriter writer = new
	 * FileWriter(newPath, true); // writer.write(csvFile); // writer.close();
	 * // }
	 * 
	 * 
	 * JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
	 * chooser.setDialogTitle("Save shapefile"); chooser.setSelectedFile(new
	 * File(newPath));
	 * 
	 * int returnVal = chooser.showSaveDialog(null);
	 * 
	 * if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) { // the user
	 * cancelled the dialog System.exit(0); }
	 * 
	 * File newFile = new File(newPath); if (newFile.equals(csvFile)) {
	 * System.out.println("Error: cannot replace " + csvFile); System.exit(0); }
	 * //System.out.println(newFile.getAbsolutePath()); return newFile; }
	 */

	/**
	 * Here is how you can use a SimpleFeatureType builder to create the schema
	 * for your shapefile dynamically.
	 * <p>
	 * This method is an improvement on the code used in the main method above
	 * (where we used DataUtilities.createFeatureType) because we can set a
	 * Coordinate Reference System for the FeatureType and a a maximum field
	 * length for the 'name' field dddd
	 */
	private static SimpleFeatureType createFeatureType() {

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("Location");
		builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference
													// system

		// add attributes in order
		builder.add("Location", Point.class);
		builder.length(15).add("Name", String.class); // <- 15 chars width for
														// name field

		// build the type
		final SimpleFeatureType LOCATION = builder.buildFeatureType();

		return LOCATION;
	}

}
