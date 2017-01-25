package com.mtr.dam.utils;

import java.text.DecimalFormat;

import com.mtr.dam.utils.TestStepReporter;

public class TimeReporter {

	public static void reportTime(String description, double t) {
		DecimalFormat df = new DecimalFormat("###.##");
		TestStepReporter.reportln(description + t + "ms (~" + df.format(t/1000) + "sec)");
	}

}
