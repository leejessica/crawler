package mo.umac.utils.test;

import java.io.File;

import mo.umac.utils.FileOperator;

public class FileOperatorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileOperatorTest test = new FileOperatorTest();
		test.createFileAutoAscendingTest();
	}

	public void createFileAutoAscendingTest() {
		String folderName = "../yahoolocal/";
		String stateName = "HI";
		String subFolder = FileOperator.createFolder(folderName, stateName);
		int numQueries = 10;
		File xmlFile = FileOperator.createFileAutoAscending(subFolder,
				numQueries, ".xml");
		System.out.println(xmlFile.getPath());
		System.out.println(xmlFile.getName());
	}

}
