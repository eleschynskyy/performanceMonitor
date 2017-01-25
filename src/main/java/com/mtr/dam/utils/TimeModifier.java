package com.mtr.dam.utils;

import java.util.Calendar;

public class TimeModifier {
	
	private static final long OFFSET = 60000;
	private static final String SIGN = "-";
	
	public static long adjustTimezone(long time) {
		long offset = OFFSET;
		Calendar cal = Calendar.getInstance();
		long milliDiff = cal.get(Calendar.ZONE_OFFSET);
		if (SIGN.equals("-")) {
			offset = -1 * OFFSET;
		}
		return time - milliDiff + offset;
	}

}
