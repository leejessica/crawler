/**
 * 
 */
package mo.umac.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mo.umac.crawler.QueryCondition;

import org.apache.log4j.Logger;

/**
 * Safety check
 * 
 * @author Kate Yim
 * 
 */
public class FileOperator {

	public static Logger logger = Logger.getLogger(FileOperator.class);

	/**
	 * Create file. If there exist the file in the folder, then rename it by
	 * adding an ascending number.
	 * 
	 * @param filePath
	 * @return
	 * @deprecated
	 */
	public static File creatFileAscending(String filePath) {
		File file = new File(filePath);
		int i = 0;
		int endIndex = 0;
		while (file.exists()) {
			if (filePath.indexOf("-") != -1) {
				endIndex = filePath.indexOf(".xml");
			} else {
				endIndex = filePath.indexOf(".xml");
			}
			filePath = filePath.substring(0, endIndex) + "-" + i + ".xml";
			file = new File(filePath);
			i++;
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static File createFileAutoAscending(String folder, int number,
			String suffix) {
		// FIXME
		return null;
	}

	public static File createFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * Create a folder under the parent folder
	 * 
	 * @param parentFolderPath
	 * @param folderName
	 * @return the subfolder's path
	 */
	public static String createFolder(String parentFolderPath, String folderName) {
		StringBuffer sb = new StringBuffer();
		sb.append(parentFolderPath);
		sb.append(folderName);
		String folderPath = sb.toString();
		File dir = new File(folderPath);
		boolean success = dir.mkdir();
		if (!success) {
			logger.error("Creating folder failed!");
		}
		return folderPath;
	}

	/**
	 * UScensus data are compressed in .zip format.
	 * 
	 * @param zipFileName
	 *            the compressed zip file.
	 */
	public static void readFromZipFile(String zipFileName) {
		// TODO unzip this file into a temporate folder.
	}

	public static void gzFolder(String folderPath) {
		// TODO
	}

	/**
	 * compress files in the list.
	 * 
	 * @param files
	 *            , list of files; element: File
	 * @param folder
	 * @param gzFileNamePrefix
	 *            the prefix of the .gz file
	 */
	public static void gzFiles(List files, String folder,
			String gzFileNamePrefix) {
		// TODO create the gzFile file according to the order.

	}

	/**
	 * Writing the parameters into the mapFile
	 * 
	 * @param mapFileName
	 * @param s
	 */
	public static void writeMapFile(BufferedWriter mapOutput,
			String partFileName, String query, int zip, int results, int start,
			double latitude, double longitude, double radius) {
		// TODO
		try {
			// file name:
			mapOutput.write(partFileName);
			mapOutput.newLine();
			// other information:
			mapOutput.write(query);
			mapOutput.write(";");
			mapOutput.write(zip);
			mapOutput.write(";");
			mapOutput.write(results);
			mapOutput.write(";");
			mapOutput.write(start);
			mapOutput.write(";");
			mapOutput.write(Double.toString(latitude));
			mapOutput.write(";");
			mapOutput.write(Double.toString(longitude));
			mapOutput.write(";");
			mapOutput.write(Double.toString(radius));
			// mapOutput.write(";");
			mapOutput.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeMapFile(String partFileName, QueryCondition qc) {

	}

	/**
	 * @param folderPath
	 * @return
	 */
	public static List traverseFolder(String folderPath) {
		List<File> list = new ArrayList<File>();
		File folder = new File(folderPath);
		recursivelyTraverse(folder, list);
		return list;
	}

	private static void recursivelyTraverse(File file, List list) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				recursivelyTraverse(files[i], list);
			}
		} else {
			if (!file.getName().contains("~")) {
				list.add(file.getAbsolutePath());
				// System.out.println(file.getName());
				// System.out.println(file.getAbsolutePath());
			}
		}
	}

}
