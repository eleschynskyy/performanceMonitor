package com.mtr.dam.utils;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestExecutionListener extends TestListenerAdapter {

	/**
	 * Prints the test results to report.
	 *
	 * @param result the result
	 */
	private void printTestResults(ITestResult result) {

		if (result.getParameters().length != 0) {
			String params = null;
			for (Object parameter : result.getParameters()) {
				params += parameter.toString() + ",";
			}

			TestStepReporter.reportln("Test Method had the following parameters : ", params);
		}

		String status = null;

		switch (result.getStatus()) {
		case ITestResult.SUCCESS:
			status = "Pass";
			break;
		case ITestResult.FAILURE:
			status = "Failed";
			break;
		case ITestResult.SKIP:
			status = "Skipped";
			break;
		}

		TestStepReporter.reportln("Test Status after execution: ", status);
	}

	public void onTestSkipped(ITestResult arg0) {
		printTestResults(arg0);
	}

	public void onTestSuccess(ITestResult arg0) {
		printTestResults(arg0);
	}

	public void onTestFailure(ITestResult arg0) {
		printTestResults(arg0);
	}

}

