package com.mtr.dam.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ConfigProperties {

	private static Properties SYSTEM_PROPERTIES;
	private static Properties TESTDATA_PROPERTIES;

	static {
		SYSTEM_PROPERTIES = new Properties();
		URL sysProps = ClassLoader.getSystemResource("system.properties");
		try {
			SYSTEM_PROPERTIES.load(sysProps.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		TESTDATA_PROPERTIES = new Properties();
		String pathname = "test_data" + File.separator + "testdata.properties";
		File file = new File(pathname);
		try {
			TESTDATA_PROPERTIES.load(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getSystemProperties(String key) {
		return SYSTEM_PROPERTIES.getProperty(key);
	}

	public static String getTestDataProperties(String key) {
		return TESTDATA_PROPERTIES.getProperty(key);
	}

}

