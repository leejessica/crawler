/**
 * 
 */
package mo.umac.crawler.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Safety check
 * 
 * @author Kate Yim
 * 
 */
public class FileOperator {

	/**
	 * Create file. If there exist the file in the folder, then rename it by
	 * adding an ascending number.
	 * 
	 * @param filePath
	 * @return
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
//		sb.append("/");
		sb.append(folderName);
		sb.append("/");
		// TODO create folder
		
		return sb.toString();
	}
	
	public static void createFolder(String folderName) {
		// TODO create folder
		
	}

	/**
	 * UScensus data are compressed in .zip format.
	 * 
	 * @param zipFileName the compressed zip file.
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
	 * @param folder
	 * @param gzFileName the name of the .gz
	 */
	public static void gzFiles(List files, String folder, String gzFileName) {
		// TODO create the gzFile file according to the order.
		
	}

	public static void writeMapFile(String mapFileName) { 
		// TODO write appendix
	}
	
}
