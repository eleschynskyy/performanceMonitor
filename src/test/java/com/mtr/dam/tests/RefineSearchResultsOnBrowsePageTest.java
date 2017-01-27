package com.mtr.dam.tests;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import com.mtr.dam.core.BaseTest;
import com.mtr.dam.core.CsvDataProvider;
import com.mtr.dam.core.DriverMaster;
import com.mtr.dam.core.web.pages.AssetsPage;
import com.mtr.dam.core.web.pages.BrowsePage;
import com.mtr.dam.core.web.pages.HomePage;
import com.mtr.dam.core.web.pages.JobsPage;
import com.mtr.dam.core.web.pages.LoginPage;
import com.mtr.dam.data.objects.UserAndAssetsToDownload;
import com.mtr.dam.utils.TestStepReporter;

public class RefineSearchResultsOnBrowsePageTest extends BaseTest {

	@Test(dataProvider = "provideUserAndAssetsToDownload", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1)
	public void refineSearchResultsOnBrowsePage(UserAndAssetsToDownload userAndAssetsToDownload) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(userAndAssetsToDownload);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		BrowsePage browsePage = assetsPage.navigateToBrowsePage();
		startTime = System.currentTimeMillis();
		browsePage.showContent();
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription() + " show content: " + (endTime - startTime) + "ms");
		startTime = System.currentTimeMillis();
		browsePage.setPageSize(100);
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(
				">" + browsePage.getDescription() + " show content Page Size=100: " + (endTime - startTime) + "ms");
		// filter by Year={2015, 2016}
		startTime = System.currentTimeMillis();
		browsePage.filterByYear(new ArrayList<String>(Arrays.asList("2015", "2016")));
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription() + " show content by 'Year' filter: "
				+ (endTime - startTime - browsePage.getForcedWait() * BrowsePage.WAIT_FOR_SEARCH_TO_START) + "ms");
		browsePage.resetForcedWait();
		// filter by Asset Status=Approved
		startTime = System.currentTimeMillis();
		browsePage.filterByAssetStatus(new ArrayList<String>(Arrays.asList("Approved")));
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription()
				+ " show content by 'Asset Status=Approved' filter: "
				+ (endTime - startTime - browsePage.getForcedWait() * BrowsePage.WAIT_FOR_SEARCH_TO_START) + "ms");
		browsePage.resetForcedWait();
		// filter by Country=Head Office
		startTime = System.currentTimeMillis();
		browsePage.filterByCountry(new ArrayList<String>(Arrays.asList("Head Office")));
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription() + " show content by 'Country=Head Office' filter: "
				+ (endTime - startTime - browsePage.getForcedWait() * BrowsePage.WAIT_FOR_SEARCH_TO_START) + "ms");
		browsePage.resetForcedWait();
		// filter by free text search
		startTime = System.currentTimeMillis();
		browsePage.filterByText(new ArrayList<String>(Arrays.asList("wine", "meat")));
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription()
				+ " show content by 'text search: {wine, meat}' filter: "
				+ (endTime - startTime - browsePage.getForcedWait() * BrowsePage.WAIT_FOR_SEARCH_TO_START) + "ms");
		browsePage.resetForcedWait();
		// select N assets
		startTime = System.currentTimeMillis();
		browsePage.selectAssets(userAndAssetsToDownload.getAssetsToDownload());
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription() + " select ("
				+ userAndAssetsToDownload.getAssetsToDownload() + " assets): "
				+ (endTime - startTime - browsePage.getForcedWait() * BrowsePage.WAIT_FOR_SEARCH_TO_START) + "ms");
		browsePage.downloadSelectedAssets();
	}

	@Test(dataProvider = "provideUserAndAssetsToDownload", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1, dependsOnMethods = {
			"refineSearchResultsOnBrowsePage" })
	public void checkDurationOfDownloadJobBrowse(UserAndAssetsToDownload userAndAssetsToDownload) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(userAndAssetsToDownload);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		JobsPage jobsPage = assetsPage.navigateToJobsPage();
		if (jobsPage.jobsProcessed()) {
			TestStepReporter.reportln(">" + jobsPage.getDescription() + " download job duration ("
					+ userAndAssetsToDownload.getAssetsToDownload() + " assets): "
					+ jobsPage.getJobDuration(userAndAssetsToDownload.getUsername(),
							userAndAssetsToDownload.getAssetsToDownload())
					+ "ms");
		}
	}

}