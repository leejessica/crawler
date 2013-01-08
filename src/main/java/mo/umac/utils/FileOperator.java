/**
 * 
 */
package mo.umac.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mo.umac.crawler.YahooLocalQuery;

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

	/**
	 * Create the corresponding .xml file for the query.
	 * 
	 * @param folder
	 * @param number
	 *            the number of queries when issuing this corresponding query.
	 * @param suffix
	 *            which is ".xml"
	 * @return
	 */
	public static File createFileAutoAscending(String folder, int number,
			String suffix) {
		String filePath = folder + number + suffix;
		File file = new File(filePath);
		String lastNumString;
		int lastNum;
		while (file.exists()) {
			if (filePath.indexOf("-") != -1) {
				// has "-"
				lastNumString = filePath.substring(filePath.indexOf("-") + 1,
						filePath.indexOf(".xml"));
				lastNum = Integer.parseInt(lastNumString);
				filePath = filePath.substring(0, filePath.indexOf("-")) + "-"
						+ (lastNum + 1) + ".xml";
			} else {
				filePath = filePath.substring(0, filePath.indexOf(".xml"))
						+ "-" + 0 + ".xml";
			}
			file = new File(filePath);
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
		sb.append(folderName);
		String folderPath = sb.toString();
		File dir = new File(folderPath);
		boolean success = dir.mkdir();
		if (!success) {
			logger.error("Creating folder failed!");
		}
		return folderPath + "/";
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
