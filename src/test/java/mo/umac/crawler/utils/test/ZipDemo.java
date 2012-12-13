package mo.umac.crawler.utils.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import mo.umac.crawler.utils.FileOperator;

public class ZipDemo {

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	public static final byte[] compress(final String uncompressed)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(baos);

		byte[] uncompressedBytes = uncompressed.getBytes();

		zos.write(uncompressedBytes, 0, uncompressedBytes.length);
		zos.close();

		return baos.toByteArray();
	}

	public static final String uncompress(final byte[] compressed)
			throws IOException {
		String uncompressed = "";

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
			GZIPInputStream zis = new GZIPInputStream(bais);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int numBytesRead = 0;
			byte[] tempBytes = new byte[DEFAULT_BUFFER_SIZE];
			while ((numBytesRead = zis.read(tempBytes, 0, tempBytes.length)) != -1) {
				baos.write(tempBytes, 0, numBytesRead);
			}

			uncompressed = new String(baos.toByteArray());
		} catch (ZipException e) {
			e.printStackTrace(System.err);
		}

		return uncompressed;
	}

	public static final String uncompress(final String compressed)
			throws IOException {
		return ZipDemo.uncompress(compressed.getBytes());
	}

	public static void main(String[] args) throws Exception {

		String folderPath = "./src/test/resources/gzFolder/";
		
		List<String> files = FileOperator.traverseFolder(folderPath);
		
//		for (int i = 0; i < files.size(); i++) {
//			System.out.println(files.get(i));
//		}
		
		for (int i = 0; i < files.size(); i++) {
			String uncompressed = "";
			File f = new File(files.get(i));

			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f));

				String line = "";
				StringBuffer buffer = new StringBuffer();

				while ((line = br.readLine()) != null)
					buffer.append(line);

				br.close();
				uncompressed = buffer.toString();
			} else {
				uncompressed = args[i];
			}

			byte[] compressed = ZipDemo.compress(uncompressed);

			String compressedAsString = new String(compressed);

			byte[] bytesFromCompressedAsString = compressedAsString.getBytes();

			bytesFromCompressedAsString.equals(compressed);
			System.out.println(ZipDemo.uncompress(compressed));
			System.out.println(ZipDemo.uncompress(compressedAsString));
		}
	}

}
