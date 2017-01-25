package com.mtr.dam.core;

import java.util.HashMap;
import java.util.Map;

public enum PlatformType {
	WIN("WIN"),
	LINUX("LINUX");

	private String platformKey;
	private static Map<String, PlatformType> platformMap = new HashMap<String, PlatformType>();
	static {
		for (PlatformType pt: PlatformType.values()) {
			platformMap.put(pt.key(), pt);
		}
	}

	private PlatformType(String key) {
		this.platformKey = key;
	}

	private String key() {
		return this.platformKey;
	}

	public static PlatformType get(String key) {
		if (platformMap.containsKey(key)) {
			return platformMap.get(key);
		}
		return WIN;
	}
}
