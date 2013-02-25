package mo.umac.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * @author yyy
 * 
 */
public class CompressFile {

	// TODO check CompressFile
	private static CompressFile instance = new CompressFile();

	private static final int BUFFEREDSIZE = 1024;
	private static final int numOfFilesInZip = 10000;
	private static final String baseDir = "E:/data/FlickrZips/";

	public CompressFile() {
	}

	public static CompressFile getInstance() {
		return instance;
	}

	public synchronized void zip(String inputFilename, String zipFilename)
			throws IOException {
		zip(new File(inputFilename), zipFilename);
	}

	public synchronized void zip(File inputFile, String zipFilename)
			throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFilename));

		try {
			zip(inputFile, out, "");
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	private synchronized void zip(File inputFile, ZipOutputStream out,
			String base) throws IOException {
		if (inputFile.isDirectory()) {
			File[] inputFiles = inputFile.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < inputFiles.length; i++) {
				zip(inputFiles[i], out, base + inputFiles[i].getName());
			}

		} else {
			if (base.length() > 0) {
				out.putNextEntry(new ZipEntry(base));
			} else {
				out.putNextEntry(new ZipEntry(inputFile.getName()));
			}

			FileInputStream in = new FileInputStream(inputFile);
			try {
				int c;
				byte[] by = new byte[BUFFEREDSIZE];
				while ((c = in.read(by)) != -1) {
					out.write(by, 0, c);
				}
			} catch (IOException e) {
				throw e;
			} finally {
				in.close();
			}
		}
	}

	private synchronized void zip(String[] inputFiles, ZipOutputStream out,
			String base) throws IOException {
		out.putNextEntry(new ZipEntry(base + "/"));
		base = base.length() == 0 ? "" : base + "/";

		String filename = null;
		String dir = null;
		File file = null;
		for (int i = 0; i < inputFiles.length; i++) {
			file = new File(inputFiles[i]);

			zip(file, out, base + file.getName());
		}
	}

	private synchronized void zipKeepFolder(String[] inputFiles,
			ZipOutputStream out, String base, String prefix) throws IOException {
		out.putNextEntry(new ZipEntry(base + "/"));
		base = base.length() == 0 ? "" : base + "/";

		String filename = null;
		String dir = null;
		File file = null;

		for (int i = 0; i < inputFiles.length; i++) {
			file = new File(inputFiles[i]);

			zip(file,
					out,
					base
							+ file.getParent()
									.replaceFirst(
											prefix.replaceFirst("\\\\",
													"\\\\\\\\"), "")
							+ File.separator + file.getName());
			// System.out.println(file.getParent().replaceFirst(prefix.replaceFirst("\\\\",
			// "\\\\\\\\"), ""));
		}
	}

	private synchronized void zip(String[] inputFiles, String zipFilename)
			throws FileNotFoundException, IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFilename));

		try {
			zip(inputFiles, out, "");
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	private synchronized void zip(String[] inputFiles, String zipFilename,
			String prefixDir) throws FileNotFoundException, IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFilename));

		try {

			zipKeepFolder(inputFiles, out, "", prefixDir);

		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	private synchronized void zipKeepFolder(String[] inputFiles,
			String zipFilename, String prefixDir) throws FileNotFoundException,
			IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFilename));

		try {
			zipKeepFolder(inputFiles, out, "", prefixDir);
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	public synchronized void unzip(String zipFilename, String outputDirectory)
			throws IOException {
		File outFile = new File(outputDirectory);
		if (!outFile.exists()) {
			outFile.mkdirs();
		}

		ZipFile zipFile = new ZipFile(zipFilename);
		Enumeration en = zipFile.getEntries();
		ZipEntry zipEntry = null;
		while (en.hasMoreElements()) {
			zipEntry = (ZipEntry) en.nextElement();
			if (zipEntry.isDirectory()) {
				// mkdir directory
				String dirName = zipEntry.getName();
				dirName = dirName.substring(0, dirName.length() - 1);

				File f = new File(outFile.getPath() + File.separator + dirName);
				f.mkdirs();

			} else {
				// unzip file
				File f = new File(outFile.getPath() + File.separator
						+ zipEntry.getName());
				f.createNewFile();
				InputStream in = zipFile.getInputStream(zipEntry);
				FileOutputStream out = new FileOutputStream(f);
				try {
					int c;
					byte[] by = new byte[BUFFEREDSIZE];
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}

					out.flush();
					// out.flush();
				} catch (IOException e) {
					throw e;
				} finally {

					out.close();
					in.close();
				}
			}
		}
	}

	// check existence before the unzip
	// if already exists, do nothing and jump to the next element
	// if file is 1111.xml, dir is data
	// with MD five calculattion, the file will be copied to
	// data\\[mdfivename]\\1111.xml
	// public synchronized void unzipWithMDFive(MySQL mysql, String zipFilename,
	// String outputDirectory) throws IOException,
	// NoSuchAlgorithmException, SQLException {
	// File outFile = new File(outputDirectory);
	// if (!outFile.exists()) {
	// outFile.mkdirs();
	// }
	//
	// ZipFile zipFile = new ZipFile(zipFilename);
	// Enumeration en = zipFile.getEntries();
	// ZipEntry zipEntry = null;
	// while (en.hasMoreElements()) {
	// zipEntry = (ZipEntry) en.nextElement();
	// if (zipEntry.isDirectory()) {
	// // mkdir directory
	// String dirName = zipEntry.getName();
	// dirName = dirName.substring(0, dirName.length() - 1);
	// File f = new File(outFile.getPath() + File.separator + dirName);
	// f.mkdirs();
	//
	// } else {
	// // unzip file
	// // String digest =
	// // MDFive.genrateDigest(MDFive.getIDFromName(zipEntry.getName()));
	// String digest = MDFive
	// .generateDigest16Bytes(getIDFromName(zipEntry.getName()));
	//
	// if (null == digest) {
	// return;
	// }
	//
	// // System.out.println(digest);
	// File f = new File(outFile.getPath() + File.separator
	// + MDFive.generateDirectory(digest, MDFive.wellPathGap)
	// + File.separator + zipEntry.getName());
	// System.out.println("Unzip : " + f.getAbsolutePath());
	//
	// if (!f.exists()) {
	// MDFive.createParentDir(f);
	// f.createNewFile();
	// InputStream in = zipFile.getInputStream(zipEntry);
	// FileOutputStream out = new FileOutputStream(f);
	// try {
	// int c;
	// byte[] by = new byte[BUFFEREDSIZE];
	// while ((c = in.read(by)) != -1) {
	// out.write(by, 0, c);
	// }
	// // out.flush();
	// } catch (IOException e) {
	// throw e;
	// } finally {
	// out.close();
	// in.close();
	// }
	// } else {
	// System.out.println(f.getAbsolutePath() + " exists!");
	// }
	//
	// String filename = zipEntry.getName();
	// String id = filename.substring(0, filename.lastIndexOf("."));
	// mysql.updatePhotoCrawledInfo(Long.parseLong(id), true);
	// System.out.println("Set " + id + " to be crawled!");
	// }
	// }
	// }

	public static String getIDFromName(String name) {
		int lastDot = name.lastIndexOf(".");

		if (lastDot != -1) {
			return name.substring(0, lastDot);
		} else {
			return null;
		}
	}

	// public static void unzipAllFiles(String loadDir, String destDir)
	// throws IOException, NoSuchAlgorithmException, SQLException {
	// File loadFile = new File(loadDir);
	// File destFile = new File(destDir);
	// MySQL mysql = new MySQL();
	//
	// if (loadFile.exists() && loadFile.isDirectory()) {
	// if (!destFile.exists()) {
	// destFile.mkdirs();
	// }
	//
	// CompressFile zip = new CompressFile();
	//
	// String paths[] = CompressFile.getFilesIn(loadFile,
	// new ZipFileFilter()).toArray(new String[0]);
	//
	// System.out.println("Totally : " + paths.length + " files in "
	// + loadFile.getCanonicalPath());
	//
	// for (int i = 0; i < paths.length; i++) {
	// // for (int i = 0; i < 10; i++) {
	//
	// System.out.println("=========================");
	// System.out.println("Process " + paths[i]);
	// zip.unzipWithMDFive(mysql, paths[i], destDir);
	// }
	//
	// } else {
	// System.err.println("Load File does not exist!");
	// }
	// }

	// unzip the srcFile to the dest dir
	// then return all files in the destDir
	public static File[] unzipFile(String src, String destDir) {
		File destFile = new File(destDir);

		if (!destFile.exists() || !destFile.isDirectory()) {
			System.out.println("File error : " + destFile.getAbsolutePath());
			return null;
		}

		CompressFile zip = new CompressFile();
		try {
			zip.unzip(src, destDir);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Error in unziping file " + src);
			// return null;
		}

		return destFile.listFiles();
	}

	public static ArrayList<String> getFilesIn(File file, FileFilter filter) {
		ArrayList<String> strList = new ArrayList<String>();

		if (!file.exists()) {
		} else {
			if (file.isDirectory()) {
				File[] files = file.listFiles(filter);

				for (int i = 0; i < files.length; i++) {
					strList.addAll(MDFive.getFilesIn(files[i]));
				}
			} else if (file.isFile()) {
				strList.add(file.getAbsolutePath());
			}
		}

		return strList;
	}

	public static void compressAllFiles() {
		CompressFile bean = new CompressFile();
		try {
			File file = new File(MDFive.sourceDir);
			File[] files = file.listFiles();

			ArrayList<String> strList = new ArrayList<String>();
			String filename = "flickr";
			int count = 0;

			for (int i = 0; i < files.length; i++) {
				if (strList.size() < CompressFile.numOfFilesInZip) {
					strList.addAll(MDFive.getFilesIn(files, i));// add files to
																// list until
																// its number
																// exceed 1000
				} else {
					bean.zip(strList.toArray(new String[0]), baseDir + filename
							+ count + ".zip");
					System.out.println("Add " + strList.size() + " files into "
							+ baseDir + filename + count + ".zip");
					strList.clear();
					count++;
				}
			}

			if (strList.size() > 0) {
				bean.zip(strList.toArray(new String[0]), baseDir + filename
						+ count + ".zip");
				System.out.println("Add " + strList.size() + " files into "
						+ baseDir + filename + count + ".zip");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void zipFilesTo(String[] files, String destDir,
			String destFile) throws FileNotFoundException, IOException {
		CompressFile bean = new CompressFile();

		File dirFile = new File(destDir);

		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		bean.zip(files, destDir + destFile + ".zip");
	}

	public static void zipFilesTo(File[] files, String destDir, String destFile)
			throws FileNotFoundException, IOException {
		// CompressFile bean = new CompressFile();

		File dirFile = new File(destDir);

		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		String[] strs = new String[files.length];

		for (int i = 0; i < strs.length; i++) {
			strs[i] = files[i].getAbsolutePath();
		}

		CompressFile.instance.zip(strs, destDir + destFile + ".zip");
	}

	public static void zipOSM(File[] files, String destDir, String destFile)
			throws FileNotFoundException, IOException {
		// CompressFile bean = new CompressFile();

		File dirFile = new File(destDir);

		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		String[] strs = new String[files.length];

		for (int i = 0; i < strs.length; i++) {
			strs[i] = files[i].getAbsolutePath();
		}

		String file = destDir + destFile + ".zip";
		file.replace(".osm", "");
		CompressFile.instance.zip(strs, file);
	}

	public static void zipAllFiles(String srcDir, String destDir, String baseStr) {
		CompressFile bean = new CompressFile();

		File srcFile = new File(srcDir);
		File destFile = new File(destDir);

		if (srcFile.exists() && srcFile.isDirectory()) {
			if (!destFile.exists()) {
				destFile.mkdirs();
			}

			try {

				File[] files = srcFile.listFiles();
				int totalNum = 0;

				ArrayList<String> strList = new ArrayList<String>();
				String filename = baseStr;
				int count = 0;

				for (int i = 0; i < files.length; i++) {
					// for (int i = 0; i < 1; i++) {
					if (strList.size() < CompressFile.numOfFilesInZip) {
						strList.addAll(MDFive.getFilesIn(files, i));// add files
																	// to list
																	// until its
																	// number
																	// exceed
																	// 1000
					} else {
						bean.zip(strList.toArray(new String[0]), destDir
								+ filename + count + ".zip");
						System.out.println("Add " + strList.size()
								+ " files into " + destDir + filename + count
								+ ".zip");
						totalNum += strList.size();
						strList.clear();
						count++;
						System.out.println("Total Files : " + totalNum);
					}
				}

				if (strList.size() > 0) {
					bean.zip(strList.toArray(new String[0]), destDir + filename
							+ count + ".zip");
					System.out.println("Add " + strList.size() + " files into "
							+ destDir + filename + count + ".zip");
					totalNum += strList.size();

				}

				System.out.println("Total Files : " + totalNum);
				// LOG.write("log/zip.txt", "add " + totalNum + " files to "
				// + destDir + "\n");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void calculateFilesInDir(String dir) {
		File file = new File(dir);

		if (file.exists()) {
			int totalNumber = 0;

			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				totalNumber += MDFive.getNumOfFiles(files[i]);
			}

			System.out.println("Num of Files in " + dir + " : " + totalNumber);
		} else {
			System.out.println("Error : folder " + dir + " does not exists!");
		}
	}

	class ZipFileFilter implements FileFilter {

		public final String[] accepetedSuffix = { ".zip" };

		@Override
		public boolean accept(File pathname) {
			String filename = pathname.getName().toLowerCase();

			for (int i = 0; i < accepetedSuffix.length; i++) {

				if (filename.contains(accepetedSuffix[i])) {
					return true;
				}
			}

			return false;
		}
	}
}
