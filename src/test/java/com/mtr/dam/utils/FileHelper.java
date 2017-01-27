package com.mtr.dam.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;

public class FileHelper {

	private static String dataLocation = ConfigProperties.getSystemProperties("data.location");
	private static final String AB = "0123456789";
	private static SecureRandom rnd = new SecureRandom();
	private static String generatedFilePrefix;
	private static String fileSuffix;
	private static long fileSizeKB;

	public static void prepareFiles(String filePrefix, int filesNumber) {
//		File folder = new File("src/main/resources/toUpload/");
		File folder = new File(dataLocation);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.getName().startsWith(filePrefix + "original")) {
				String fileName;
				String random = randomString(9);
				generatedFilePrefix = filePrefix + random;
				fileSuffix = "_01" + file.getName().substring(file.getName().indexOf("."), file.getName().length());
				fileSizeKB = file.length() / 1000;
				for (int i = 1; i <= filesNumber; i++) {
					fileName = filePrefix + random + String.format("%03d", i) + fileSuffix;
					// System.out.println("File " + fileName + " created");
					File destFile = new File(dataLocation + fileName);
					try {
						copyFile(file, destFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}

	}

	public static void deleteFiles() {
		File folder = new File(dataLocation);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.getName().startsWith(generatedFilePrefix)) {
				file.delete();
				// System.out.println("File " + file.getName() + " deleted");
			}
		}
	}

	public static String getGeneratedFilePrefix() {
		return generatedFilePrefix;
	}

	public static String getFileSuffix() {
		return fileSuffix;
	}

	public static long getFileSize() {
		return fileSizeKB;
	}

	private static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}

	private static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

}
