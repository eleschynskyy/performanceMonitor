package com.mtr.dam.core;

import static com.mtr.dam.core.Configuration.setGlobalEnvironment;
import static com.mtr.dam.core.DriverMaster.startDriverInstance;
import static com.mtr.dam.core.DriverMaster.stopDriverInstance;

import java.net.MalformedURLException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.mtr.dam.utils.S3Loader;
import com.mtr.dam.utils.TestStepReporter;

public abstract class BaseTest {
	
//	@BeforeSuite
	public void getData(){
		S3Loader loader = S3Loader.getInstance();
		long startTime = System.currentTimeMillis();
		loader.download("data/", "data");
		loader.download("drivers/", "drivers");
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">Getting data from S3: " + (endTime - startTime) + "ms");
	}

	@BeforeMethod(alwaysRun = true)
	@Parameters({ "platform", "browser", "version", "environment" })
	public void setUp(@Optional("WIN") String platform,
			@Optional("chrome") String browser,
			@Optional("1.0") String version,
			@Optional("test") String environment) {
		try {
			startDriverInstance(platform, browser, version);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		setGlobalEnvironment(environment);
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		stopDriverInstance();
	}

}
