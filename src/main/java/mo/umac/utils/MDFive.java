package mo.umac.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


/**
 * @author yyy
 *
 */
public class MDFive {

	// TODO check MDFive
	
    //the path gap, it means the number of the characters which is large enough to build a sub-directory
    //more over the file name is also the value of the path gap

    public static final int pathGap = 2;
    public static final int pathGapNumber = 4;
    private MessageDigest md = null;
    public static final String storeDir = "F:\\data\\FlickrData\\viewSport\\";
    public static final String sourceDir = "F:\\data\\FlickrData\\viewSport\\";
//    public static final String destDir = "F:/data/test/ParisGeo/buffer/";
//    public static final String zipDir = "F:/data/test/ParisGeo/zip/";
//    public static final String destDir = "F:/data/NewYorkGeo/buffer/";
//    public static final String zipDir = "F:/data/NewYorkGeo/zip/";
//    public static final String destDir = "/mnt/filestore/flickr/data/LondonGeo/buffer/";
//    public static final String zipDir = "/mnt/filestore/flickr/data/LondonGeo/zip/";
    public static final String destDir = "/mnt/filestore/flickr/data/RomeGeo/buffer/";
    public static final String zipDir = "/mnt/filestore/flickr/data/RomeGeo/zip/";
//    public static final String zipDir = "/mnt/filestore/flickr/data/NewyorkGeo/zip/";

//    public static final String systemBuffer = "F:/data/SystemBuffer/";
    public static final String systemBuffer = "/mnt/filestore/flickr/Workspace/SystemBuffer/";
    public static final String searchResDir = "F:\\data\\FlickrData\\searchRes\\";
    public static final int wellPathGap = 2;
    public static final int wellPathGapNumber = 16;
    public static final char hexDigits[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String genrateDigest(String filename) throws NoSuchAlgorithmException {
        String hash = null;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(filename.getBytes());

        byte[] arr = md.digest();
        hash = new java.math.BigInteger(1, arr).toString(pathGap * pathGapNumber);

        if (hash.length() != 2 * pathGap * pathGapNumber) {
            int numOfZero = 2 * pathGap * pathGapNumber - hash.length();
            for (int j = 0; j < numOfZero; j++) {
                hash = "0" + hash;
                //add zeros at the beginning of the digest
            }
        }

        return hash;
    }

    public static String getMD5(byte[] source) {
        String s = null;

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();          // the result of MD5 128 bits which equals to 16 bytes
            char str[] = new char[16 * 2];   //16 * 2
            int k = 0;
            for (int i = 0; i < 16; i++) {

                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];

                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getMD5(String s) {
        return MDFive.getMD5(s.getBytes());
    }

    public static String generateDigest16Bytes(String s) {
        return MDFive.getMD5(s).substring(8, 24);
    }

    //defaultly start from 0
    public static String generateDigestNBytes(String s, int length) {
        if (length < 1 || length >= s.length()) {
            return MDFive.getMD5(s);
        } else {
            return MDFive.getMD5(s).substring(0, length);
        }
    }

    public static String genrateDigestWell(String filename) throws NoSuchAlgorithmException {
        String hash = null;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(filename.getBytes());

        byte[] arr = md.digest();
        hash = new java.math.BigInteger(1, arr).toString(wellPathGap * wellPathGapNumber);

        if (hash.length() != 2 * wellPathGap * wellPathGapNumber) {
            int numOfZero = 2 * wellPathGap * wellPathGapNumber - hash.length();
            for (int j = 0; j < numOfZero; j++) {
                hash = "0" + hash;
                //add zeros at the beginning of the digest
            }
        }

        return hash;
    }

    //suppose name is given as 123456.xml
    //the result would be 123456
    public static String getIDFromName(String name) {
        int lastDot = name.lastIndexOf(".");

        int lastSeparator = name.lastIndexOf(File.separatorChar);

        if (lastDot == -1) {
            lastDot = name.length();
        }

        if (-1 == lastSeparator) {
            lastSeparator = 0;
        }
        return name.substring(lastSeparator, lastDot);
    }

    public static int getNumOfFiles(File file) {

        int totalCount = 0;

        if (!file.exists()) {
            return 0;
//            System.out.println("Store Dir does not exist!");
        } else {
            if (file.isDirectory()) {

                File[] files = file.listFiles();

                for (int i = 0; i < files.length; i++) {
                    totalCount += MDFive.getNumOfFiles(files[i]);
                }
            } else if (file.isFile()) {
                return 1;
            }
        }

        return totalCount;
    }

    public static ArrayList<String> getFilesIn(File file) {
        ArrayList<String> strList = new ArrayList<String>();

        if (!file.exists()) {
        } else {
            if (file.isDirectory()) {
                File[] files = file.listFiles();

                for (int i = 0; i < files.length; i++) {
                    strList.addAll(MDFive.getFilesIn(files[i]));
                }
            } else if (file.isFile()) {
                strList.add(file.getAbsolutePath());
            }
        }

        return strList;
    }

    //this function will create the corresponding srcDir
    //remember it
    public static String generateProperPath(String filename, String directory, String digest, int gap) {


        //the direcory is different and keep unchange
        String dir = directory + MDFive.generateDirectory(digest, gap);

        File dirFile = new File(dir);

//        if (!dirFile.exists()) {
//            System.out.println("Create srcDir " + dirFile.getAbsolutePath() + " - " + dirFile.mkdirs());
//        }


        return dirFile.getAbsoluteFile() + "\\" + filename + ".xml";
    }

    public static String generateProperDirWell(String basedDir, String filename, int gap) throws NoSuchAlgorithmException {
        String digest = genrateDigest(filename);

        String distDir = basedDir + MDFive.generateDirectory(digest, gap);

        return distDir;
    }

    public static void createParentDir(File fileDir) {
        File pDir = fileDir.getParentFile();

        if (!pDir.exists()) {

            if (!pDir.mkdirs()) {
                System.out.println("Error in Creating " + pDir.getAbsolutePath());
            }
        }
    }

    public static String generateDirectory(String mdfiveCode, int gap) {

        if (mdfiveCode != null) {

            int length = mdfiveCode.length();
            if (length % gap == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(File.separatorChar);
                for (int i = 0; i < mdfiveCode.length(); i++) {
                    sb.append(mdfiveCode.charAt(i));
                    if ((i + 1) % gap == 0) {
                        sb.append(File.separatorChar);
                    }
                }

//                System.out.println(sb.toString());
//                sb.append("/");
                return sb.toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String generateProperPath(String filename, String secret) throws NoSuchAlgorithmException {

        String digest = MDFive.genrateDigest(filename);
//        System.out.println("Digest " + digest);

        String path = MDFive.generateProperPath(filename, MDFive.storeDir, digest, pathGap);

        return path;
    }

    public static String generateProperPathWell(String filename, String secret) throws NoSuchAlgorithmException {

        String digest = MDFive.genrateDigest(filename);
//        System.out.println("Digest " + digest);

        String path = MDFive.generateProperPath(filename, MDFive.storeDir, digest, wellPathGap);

        return path;
    }

    //process the index(th) srcDir in the level folder
    public static void processFiles(String dir, int index, String suffix) {
        File file = new File(dir);
        File focusFile = null;

        if (file.isDirectory() && file.exists()) {
            File[] files = file.listFiles();

            if (index >= 0 && index < files.length) {
                focusFile = files[index];

//                System.out.println("Num Of files in " + focusFile.getAbsolutePath() + ": " + MDFive.getNumOfFiles(focusFile));
                ArrayList fileList = MDFive.getFilesIn(focusFile);
                System.out.println("Num Of files in " + fileList.size());

                for (int i = 0, size = fileList.size(); i < size; i++) {
                    System.out.println("File : " + fileList.get(i));
                }
            }
        }
    }

    public static ArrayList<String> getFilesIn(File[] files, int index) {
        if (index >= 0 && index < files.length) {

            return MDFive.getFilesIn(files[index]);
        } else {
            return new ArrayList<String>();
        }
    }

    public static void copyFromSourceToDest(String srcPath) throws NoSuchAlgorithmException {
        File srcFile = new File(srcPath);
        File srcDir = srcFile.getParentFile();
        String file = srcFile.getName();
        String strDistDir = MDFive.generateProperDirWell(MDFive.destDir, file, wellPathGap);
        String strDistFile = strDistDir + file;

//        System.out.println(srcFile.getAbsolutePath());
//        System.out.println(srcDir.getAbsolutePath());
//        System.out.println(file);
//        System.out.println(strDistFile);

        MDFive.copyFile(srcFile.getAbsolutePath(), strDistFile);

        System.out.println("Copy " + file + " to " + strDistDir);
    }

    public static void copyFile(String source, String dest) {
        try {
            File in = new File(source);
            File out = new File(dest);
            File outDir = out.getParentFile();

            if (!outDir.exists()) {
                if (!outDir.mkdirs()) {
                    System.out.println("Error in Creating : " + outDir.getAbsolutePath());
                }
            }

            if (out.exists()) {
                System.out.println("File : " + out.getAbsolutePath() + " exists ! Ignore copy operation");
                return;
            }

            FileInputStream inFile = new FileInputStream(in);
            FileOutputStream outFile = new FileOutputStream(out);
            byte[] buffer = new byte[1024];
            int i = 0;
            while ((i = inFile.read(buffer)) != -1) {
                outFile.write(buffer, 0, i);
            }//end while
            inFile.close();
            outFile.close();
        }//end try
        catch (Exception e) {
        }//end catch
    }//end copyFile

    public static void copyFiles() throws NoSuchAlgorithmException {
        File file = new File(MDFive.sourceDir);
        File[] files = file.listFiles();

        ArrayList<String> fileList = null;

        for (int i = 0; i < files.length; i++) {
            fileList = MDFive.getFilesIn(files, i);

            for (int j = 0, size = fileList.size(); j < size; j++) {
                MDFive.copyFromSourceToDest(fileList.get(j));
            }

            System.out.println("Finish file " + i);
            System.out.println("===================");
        }
    }

    public static String generateDirForSearchResult(String keyword, boolean hasGeo, int page, String minDate, String maxDate) {

        String geoStatus;

        if (hasGeo) {
            geoStatus = "GeoTagged";
        } else {
            geoStatus = "GeoNoSepecified";
        }
        String path = MDFive.searchResDir + keyword + File.separatorChar + geoStatus + File.separatorChar + minDate + "-" + maxDate + "Page" + page + ".xml";

        return path;
    }

    public static String generateProperpath16Bytes(String baseDir, String filename, String extension) {
        int dotIndex = filename.lastIndexOf(".");
        String name = null;
        if (-1 != dotIndex) {
            name = filename.substring(0, dotIndex);
        } else {
            name = filename;
        }

        String digest = MDFive.generateDigest16Bytes(name);

        String properDir = MDFive.generateDirectory(digest, pathGap);

        if (null == extension) {
            return baseDir + properDir + filename;
        } else if (null != extension && !"".equals(extension) && extension.charAt(0) != '.') {
            return baseDir + properDir + filename + "." + extension;
        } else {
            return baseDir + properDir + filename + extension;
        }
    }

    public static String generateProperpath16Bytes(String filename) {
        if (-1 != filename.indexOf(".")) {
            return MDFive.generateProperpath16Bytes(destDir, filename, "");
        } else {
            return MDFive.generateProperpath16Bytes(destDir, filename, ".xml");
        }
    }

    public static void deleteFiles(File f) {
        if (f.isFile()) {
            f.delete();
        } else if (f.isDirectory()) {
            File[] files = f.listFiles();

            for (int i = 0; i < files.length; i++) {
                MDFive.deleteFiles(files[i]);
            }

            f.delete();
        } else {
            System.out.println("?? Case ??? : " + f.getAbsolutePath());
        }
    }
	
}
