/**
 * 
 */
package mo.umac.crawler.utils;

import java.io.File;
import java.io.IOException;

/**
 * Safety check
 * 
 * @author Kate YAN
 * 
 */
public class FileOperator {

	public static File creatFileAscending(String filePath) {
		File file = new File(filePath);
		int i = 0;
		int endIndex = 0;
		while (file.exists()) {
			if(filePath.indexOf("-") != -1){
				endIndex = filePath.indexOf(".xml");
			}else {
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

}
