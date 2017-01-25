package com.mtr.dam.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.testng.annotations.DataProvider;

import com.mtr.dam.data.objects.DataPackage;
import com.mtr.dam.data.objects.User;
import com.mtr.dam.utils.ConfigProperties;

import au.com.bytecode.opencsv.CSVReader;

public class CsvDataProvider {

	private static String numberOfUsers = ConfigProperties.getTestDataProperties("number.users");

	@DataProvider(name = "provideRandomUserFromList")
	public static Iterator<Object[]> provideRandomUserFromList(Method method) {
		List<Object[]> list = new ArrayList<Object[]>();
		String pathname = "test_data" + File.separator + method.getDeclaringClass().getSimpleName() + "."
				+ method.getName() + ".csv";
		File file = new File(pathname);
		Random rndUser = new Random();
		int rndUsrIndex = rndUser.nextInt(Integer.valueOf(numberOfUsers)) + 1;
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				int j = 1;
				while ((dataParts = reader.readNext()) != null && j < rndUsrIndex) {
					j++;
				}
				Map<String, String> testData = new HashMap<String, String>();
				for (int i = 0; i < keys.length; i++) {
					testData.put(keys[i], dataParts[i]);
				}
				User user = new User().setUsername(testData.get("username")).setPassword(testData.get("password"));
				list.add(new Object[] { user });
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + pathname + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not read " + pathname + " file.\n" + e.getStackTrace().toString());
		}
		return list.iterator();
	}

	@DataProvider(name = "provideUserFromList")
	public static Iterator<Object[]> provideUserFromList(Method method) {
		List<Object[]> list = new ArrayList<Object[]>();
		String pathname = "test_data" + File.separator + method.getDeclaringClass().getSimpleName() + "."
				+ method.getName() + ".csv";
		File file = new File(pathname);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					Map<String, String> testData = new HashMap<String, String>();
					for (int i = 0; i < keys.length; i++) {
						testData.put(keys[i], dataParts[i]);
					}
					User user = new User().setUsername(testData.get("username")).setPassword(testData.get("password"));
					list.add(new Object[] { user });
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + pathname + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not read " + pathname + " file.\n" + e.getStackTrace().toString());
		}
		return list.iterator();
	}

	@DataProvider(name = "provideDataPackage")
	public static Iterator<Object[]> provideDataPackage(Method method) {
		List<Object[]> list = new ArrayList<Object[]>();
		String pathname = "test_data" + File.separator + method.getDeclaringClass().getSimpleName() + "."
				+ method.getName() + ".csv";
		File file = new File(pathname);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					Map<String, String> testData = new HashMap<String, String>();
					for (int i = 0; i < keys.length; i++) {
						testData.put(keys[i], dataParts[i]);
					}
					DataPackage dataPackage = new DataPackage()
							.setUsername(testData.get("username"))
							.setPassword(testData.get("password"))
							.setfilePrefix(testData.get("file_prefix"))
							.setfilesNumber(testData.get("files_to_upload"));
					list.add(new Object[] { dataPackage });
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + pathname + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not read " + pathname + " file.\n" + e.getStackTrace().toString());
		}
		return list.iterator();
	}

}
