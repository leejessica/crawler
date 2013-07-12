/*
 *    HatBox : A user-space spatial add-on for the Java databases
 *    
 *    Copyright (C) 2007 - 2009 Peter Yuill
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package mo.umac.db.test;

/*
 *    HatBox : A user-space spatial add-on for the Java databases
 *    
 *    Copyright (C) 2007 - 2009 Peter Yuill
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.hatbox.HatBoxDerbyDataStoreFactory;
import org.geotools.data.hatbox.HatBoxH2DataStoreFactory;

import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.IsLessThenOrEqualToImpl;
import org.geotools.filter.LengthFunction;
import org.geotools.filter.LiteralExpressionImpl;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.Filter;

import net.sourceforge.hatbox.jts.Proc;

public class Shp2Hatbox {

    public static void main(String[] args) throws Exception {
	String shpDir = null;
	String dbType = null;
	String schema = null;
	String srid = null;
	String host = null;
	String database = null;
	String user = null;
	String password = null;
	String url = null;
	String driver = null;
	boolean derby = true;
	if (args.length > 0) {
	    shpDir = args[0];
	}
	if (args.length > 1) {
	    dbType = args[1];
	}
	if (args.length > 2) {
	    schema = args[2];
	}
	if (args.length > 3) {
	    srid = args[3];
	}
	if (args.length > 4) {
	    host = args[4];
	}
	if (args.length > 5) {
	    database = args[5];
	}
	if (args.length > 6) {
	    user = args[6];
	}
	if (args.length > 7) {
	    password = args[7];
	}
	StringBuilder buf = new StringBuilder();
	if (dbType.equals("derby")) {
	    derby = true;
	    buf.append("jdbc:derby://");
	    buf.append(host);
	    buf.append('/');
	    buf.append(database);
	    url = buf.toString();
	    driver = "org.apache.derby.jdbc.ClientDriver";
	} else {
	    derby = false;
	    buf.append("jdbc:h2:tcp://");
	    buf.append(host);
	    buf.append('/');
	    buf.append(database);
	    url = buf.toString();
	    driver = "org.h2.Driver";
	}
	System.out.println("Using url: " + url + " : " + user + " : "
		+ password);

	File file = promptShapeFile(shpDir);
	try {
	    Map<String, Serializable> connectParameters = new HashMap<String, Serializable>();

	    connectParameters.put("url", file.toURI().toURL());
	    connectParameters.put("create spatial index", false);
	    DataStore dataStore = DataStoreFinder
		    .getDataStore(connectParameters);

	    String[] typeNames = dataStore.getTypeNames();
	    String typeName = typeNames[0];

	    System.out.println("Importing: " + typeName);

	    FeatureSource<SimpleFeatureType, SimpleFeature> sfs;
	    FeatureStore<SimpleFeatureType, SimpleFeature> hfs;
	    FeatureCollection<SimpleFeatureType, SimpleFeature> scol;
	    FeatureCollection<SimpleFeatureType, SimpleFeature> hcol;
	    FeatureIterator<SimpleFeature> iterator;

	    sfs = dataStore.getFeatureSource(typeName);
	    SimpleFeatureType sft = sfs.getSchema();
	    GeometryDescriptor gd = sft.getGeometryDescriptor();
	    List<AttributeDescriptor> atts = sft.getAttributeDescriptors();

	    String create = createTable(derby, schema, typeName, gd, atts);
	    System.out.println("Create table: " + create);
	    Class.forName(driver);
	    Connection con = null;
	    if (user == null) {
		con = DriverManager.getConnection(url);
	    } else {
		con = DriverManager.getConnection(url, user, password);
	    }
	    Statement stmt = con.createStatement();
	    try {
		stmt.executeUpdate(dropTable(schema, typeName));
	    } catch (Exception e) {
	    }
	    try {
		stmt.executeUpdate(dropIndex(schema, typeName));
	    } catch (Exception e) {
	    }
	    stmt.executeUpdate(create);

	    Proc.spatialize(con, schema, typeName, gd.getLocalName(), gd
		    .getType().getName().toString().toUpperCase(), srid,
		    "false", null);

	    DataStoreFactorySpi factory = null;
	    connectParameters = new HashMap<String, Serializable>();
	    connectParameters.put("host", host);
	    connectParameters.put("database", database);
	    connectParameters.put("schema", schema);
	    if (derby) {
		connectParameters.put("dbtype", "HATBOX-DERBY");
		factory = new HatBoxDerbyDataStoreFactory();
	    } else {
		connectParameters.put("dbtype", "HATBOX-H2");
		connectParameters.put("user", user);
		factory = new HatBoxH2DataStoreFactory();
	    }
	    DataStore ds = factory.createDataStore(connectParameters);
	    hfs = (FeatureStore<SimpleFeatureType, SimpleFeature>) ds
		    .getFeatureSource(typeName);
	    Transaction tr = new DefaultTransaction("test");
	    hfs.setTransaction(tr);

	    SimpleFeatureType hft = hfs.getSchema();
	    scol = sfs.getFeatures();
	    hcol = new MemoryFeatureCollection(hft);
	    iterator = scol.features();
	    int count = 0;
	    try {
		while (iterator.hasNext()) {
		    SimpleFeature sf = iterator.next();
		    List<Object> attList = sf.getAttributes();
		    for (int i = 0; i < attList.size(); i++) {
			if (attList.get(i) instanceof java.util.Date) {
			    java.util.Date date = (java.util.Date) attList
				    .get(i);
			    attList.set(i, new Date(date.getTime()));
			}
		    }
		    sf.setAttributes(attList);
		    hcol.add(sf);
		    count++;
		    if ((count % 100) == 0) {
			hfs.addFeatures(hcol);
			tr.commit();
			hcol.clear();
			System.out.println(count);
		    }
		}
		hfs.addFeatures(hcol);
		tr.commit();
		System.out.println(count);
	    } finally {
		if (iterator != null) {
		    iterator.close();
		}
	    }

	    Proc.buildIndex(con, schema, typeName, 100, null);

	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
	System.exit(0);
    }

    public static String dropTable(String schema, String type) {
	StringBuilder buf = new StringBuilder();
	buf.append("drop table \"").append(schema).append("\".\"").append(type)
		.append('"');
	return buf.toString();
    }

    public static String dropIndex(String schema, String type) {
	StringBuilder buf = new StringBuilder();
	buf.append("drop table \"").append(schema).append("\".\"").append(type)
		.append("_HATBOX\"");
	return buf.toString();
    }

    public static String createTable(boolean derby, String schema, String type,
	    AttributeDescriptor geom, List<AttributeDescriptor> atts)
	    throws Exception {
	StringBuilder buf = new StringBuilder();
	buf.append("create table \"").append(schema).append('"').append('.')
		.append('"').append(type);
	if (derby) {
	    buf.append("\" (ID bigint not null generated always as identity, ");
	} else {
	    buf.append("\" (ID identity, ");
	}
	buf.append('"').append(geom.getLocalName()).append('"');
	buf.append(" blob");
	for (AttributeDescriptor ad : atts) {
	    if (ad.equals(geom)) {
		continue;
	    }
	    AttributeType at = ad.getType();
	    buf.append(", \"");
	    buf.append(ad.getLocalName());
	    buf.append("\" ");
	    if (at.getBinding().equals(String.class)) {
		buf.append("varchar");
		List<Filter> filters = at.getRestrictions();
		for (Filter f : filters) {
		    if (f instanceof IsLessThenOrEqualToImpl) {
			IsLessThenOrEqualToImpl lore = (IsLessThenOrEqualToImpl) f;
			if ((lore.getExpression1() instanceof LengthFunction)
				&& (lore.getExpression2() instanceof LiteralExpressionImpl)) {
			    LiteralExpressionImpl lit = (LiteralExpressionImpl) lore
				    .getExpression2();
			    buf.append('(');
			    buf.append(lit.getValue());
			    buf.append(')');
			}
		    }
		}
	    } else if (at.getBinding().equals(Integer.class)) {
		buf.append("int");
	    } else if (at.getBinding().equals(Long.class)) {
		buf.append("bigint");
	    } else if (at.getBinding().equals(Double.class)) {
		buf.append("double");
	    } else if (at.getBinding().equals(java.util.Date.class)) {
		buf.append("date");
	    } else {
		throw new Exception("Unknown type: " + at.getBinding());
	    }
	}
	if (derby) {
	    buf.append(", primary key (ID)");
	}
	buf.append(')');
	return buf.toString();
    }

    private static File promptShapeFile(String shpDir)
	    throws FileNotFoundException {
	File file;
	JFileChooser chooser = new JFileChooser(shpDir);
	chooser.setDialogTitle("Select Shapefile");
	chooser.setFileFilter(new FileFilter() {
	    public boolean accept(File f) {
		return f.isDirectory() || f.getPath().endsWith("shp")
			|| f.getPath().endsWith("SHP");
	    }

	    public String getDescription() {
		return "Shapefiles";
	    }
	});
	int returnVal = chooser.showOpenDialog(null);

	if (returnVal != JFileChooser.APPROVE_OPTION) {
	    System.exit(0);
	}
	file = chooser.getSelectedFile();

	System.out.println("You chose to open this file: " + file.getName());
	if (!file.exists()) {
	    throw new FileNotFoundException(file.getAbsolutePath());
	}
	return file;
    }

}