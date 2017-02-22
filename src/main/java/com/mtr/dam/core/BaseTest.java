package com.mtr.dam.core;

import static com.mtr.dam.core.Configuration.setGlobalEnvironment;
import static com.mtr.dam.core.DriverMaster.startDriverInstance;
import static com.mtr.dam.core.DriverMaster.stopDriverInstance;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.mtr.dam.utils.S3Loader;
import com.mtr.dam.utils.TestStepReporter;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.ExtentTestInterruptedException;

public abstract class BaseTest {
	
	public static ExtentReports extent;
	public static ExtentTest test;
	public static ExtentTestInterruptedException testexception;
	
	@BeforeSuite
	public void getData(){
		/*
		S3Loader loader = S3Loader.getInstance();
		long startTime = System.currentTimeMillis();
		loader.download("data/", "data");
		loader.download("drivers/", "drivers");
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">Getting data from S3: " + (endTime - startTime) + "ms");
		*/
		extent = new ExtentReports("extent_report//report.html", true); //Provide Desired Report Directory Location and Name
		extent.loadConfig(new File("extent-config.xml")); //Supporting File for Extent Reporting
		extent.addSystemInfo("Environment","mac-pp"); //It will provide Execution Machine Information
	}
	
	@BeforeMethod
	public  void beforeMethod(Method method) 
	{
		test = extent.startTest( (this.getClass().getSimpleName() + " :: " +  method.getName()), method.getName()); //Test Case Start Here
//		test.assignAuthor("Keshav Kashyap"); //Test Script Author Name
		test.assignCategory("MAC UI performance"); //Test Category Defined Here
	}
	
	@AfterMethod
	public void afterMethod() 
	{
		extent.endTest(test);
		extent.flush();
	}
	
	@AfterSuite
	public void afterSuite() 
	{
		extent.close();
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
