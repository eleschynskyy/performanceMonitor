package com.mtr.dam.core;

import java.util.HashMap;
import java.util.Map;

public enum BrowserType {
	FIREFOX("firefox"),
	CHROME("chrome"),
	IE("iexplorer"),
	HTML("htmlunit");

	private String browserKey;
	private static Map<String, BrowserType> browserMap = new HashMap<String, BrowserType>();
	static {
		for (BrowserType bt: BrowserType.values()) {
			browserMap.put(bt.key(), bt);
		}
	}

	private BrowserType(String key) {
		this.browserKey = key;
	}

	private String key() {
		return this.browserKey;
	}

	public static BrowserType get(String key) {
		if (browserMap.containsKey(key)) {
			return browserMap.get(key);
		}
		return CHROME;
	}
}
