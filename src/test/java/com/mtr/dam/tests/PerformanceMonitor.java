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
import com.mtr.dam.core.web.pages.ProductUploadPage;
import com.mtr.dam.core.web.pages.PublishRecordsPage;
import com.mtr.dam.core.web.pages.SearchResultPage;
import com.mtr.dam.core.web.pages.UploadFilesPage;
import com.mtr.dam.core.web.pages.UploadedFilesPage;
import com.mtr.dam.data.objects.DataPackage;
import com.mtr.dam.utils.FileHelper;
import com.mtr.dam.utils.TestStepReporter;
import com.mtr.dam.utils.TimeModifier;

public class PerformanceMonitor extends BaseTest {

	private int assetsToDownload = 15;

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = false, invocationCount = 1, threadPoolSize = 1)
	public void uploadProductAssets(DataPackage dataPackage) {
		FileHelper.prepareFiles(dataPackage.getfilePrefix(), dataPackage.getfilesNumber());
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		ProductUploadPage productUploadPage = assetsPage.openUploadProductFilesDialogWindow();
		startTime = System.currentTimeMillis();
		assetsPage = productUploadPage.uploadFilesAndReturnToAssetsPage(dataPackage.getfilePrefix(),
				dataPackage.getfilesNumber(), assetsPage.getWindowHandle());
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + productUploadPage.getDescription() + " upload " + dataPackage.getfilesNumber()
				+ " files (" + FileHelper.getFileSize() + "KB each): " + (endTime - startTime) + "ms");
		FileHelper.deleteFiles();
		JobsPage jobsPage = assetsPage.navigateToJobsPage();
		if (jobsPage.jobsProcessed()) {
			int totalTime = jobsPage.getTotalTimeinSecondsStartingFrom(TimeModifier.adjustTimezone(startTime),
					dataPackage.getUsername());
			if (!(totalTime == 0)) {
				TestStepReporter.reportln(">Total processing time: " + totalTime + " seconds");
			} else {
				TestStepReporter.reportln(">Total processing time: impossible to get total time");
			}
		}
		;
		/*
		 * LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(),
		 * "Login page"); HomePage homePage = loginPage.loginAs(dataPackage);
		 * AssetsPage assetsPage = homePage.navigateToAssetsPage(); JobsPage
		 * jobsPage = assetsPage.navigateToJobsPage(); long startTime =
		 * System.currentTimeMillis();
		 */
	}

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = false, invocationCount = 1, threadPoolSize = 1)
	public void uploadAndTagMarketingAssets(DataPackage dataPackage) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		UploadedFilesPage uploadedFilesPage = assetsPage.navigateToUploadedFilesPage();
		if (uploadedFilesPage.uploadsCleared()) {
			FileHelper.prepareFiles(dataPackage.getfilePrefix(), dataPackage.getfilesNumber());
			UploadFilesPage uploadFilesPage = uploadedFilesPage.openUploadFilesDialogWindow();
			startTime = System.currentTimeMillis();
			uploadedFilesPage = uploadFilesPage.uploadFilesAndReturnToUploadedFilesPage(dataPackage.getfilePrefix(),
					dataPackage.getfilesNumber());
			endTime = System.currentTimeMillis();
			TestStepReporter.reportln(">" + uploadFilesPage.getDescription() + " upload " + dataPackage.getfilesNumber()
					+ " files (" + FileHelper.getFileSize() + "KB each): " + (endTime - startTime) + "ms");
			FileHelper.deleteFiles();
			PublishRecordsPage publishRecordsPage = uploadedFilesPage
					.makeAvailableUploadedFiles(TimeModifier.adjustTimezone(startTime), dataPackage.getUsername());
			startTime = System.currentTimeMillis();
			uploadedFilesPage = publishRecordsPage.makeRecordsAvailable();
			endTime = System.currentTimeMillis();
			if (uploadedFilesPage.allFilesUploaded()) {
				TestStepReporter.reportln(
						">" + publishRecordsPage.getDescription() + " make available " + dataPackage.getfilesNumber()
								+ " files (" + FileHelper.getFileSize() + "KB each): " + (endTime - startTime) + "ms");
			}
		}
	}

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = false, invocationCount = 1, threadPoolSize = 1)
	public void refineSearchResultsOnBrowsePage(DataPackage dataPackage) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
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
		browsePage.selectAssets(assetsToDownload);
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription() + " select (" + assetsToDownload + " assets): "
				+ (endTime - startTime - browsePage.getForcedWait() * BrowsePage.WAIT_FOR_SEARCH_TO_START) + "ms");
		browsePage.downloadSelectedAssets();
	}

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = false, invocationCount = 1, threadPoolSize = 1, dependsOnMethods = {
			"refineSearchResultsOnBrowsePage" })
	public void checkDurationOfDownloadJobBrowse(DataPackage dataPackage) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		JobsPage jobsPage = assetsPage.navigateToJobsPage();
		if (jobsPage.jobsProcessed()) {
			TestStepReporter.reportln(">" + jobsPage.getDescription() + " download job duration (" + assetsToDownload
					+ " assets): " + jobsPage.getJobDuration(dataPackage.getUsername(), assetsToDownload) + "ms");
		}
	}

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1)
	public void freeTextAndFacetedSearch(DataPackage dataPackage) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		// search for 'wine'
		startTime = System.currentTimeMillis();
		SearchResultPage searchResultPage = assetsPage.searchForTerm("wine");
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(
				">" + searchResultPage.getDescription() + " show results for 'wine': " + (endTime - startTime) + "ms");
		// search for 'meat'
		startTime = System.currentTimeMillis();
		searchResultPage = searchResultPage.searchForTerm("meat");
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(
				">" + searchResultPage.getDescription() + " show results for 'meat': " + (endTime - startTime) + "ms");
		// setting page size 100
		startTime = System.currentTimeMillis();
		searchResultPage.setPageSize(100);
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + searchResultPage.getDescription() + " show content Page Size=100: "
				+ (endTime - startTime) + "ms");
		// filter by Asset Status={Key Visual, Product Image}
		startTime = System.currentTimeMillis();
		searchResultPage.filterByAssetStatus(new ArrayList<String>(Arrays.asList("Key Visual", "Product Image")));
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + searchResultPage.getDescription()
				+ " show content by 'Asset Status={Key Visual, Product Image}' filter: "
				+ (endTime - startTime - searchResultPage.getForcedWait() * SearchResultPage.WAIT_FOR_SEARCH_TO_START)
				+ "ms");
		searchResultPage.resetForcedWait();
		// filter by Country=Head Office
		startTime = System.currentTimeMillis();
		searchResultPage.filterByCountry(new ArrayList<String>(Arrays.asList("Head Office")));
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + searchResultPage.getDescription()
				+ " show content by 'Country=Head Office' filter: "
				+ (endTime - startTime - searchResultPage.getForcedWait() * SearchResultPage.WAIT_FOR_SEARCH_TO_START)
				+ "ms");
		searchResultPage.resetForcedWait();
		// select all found assets
		startTime = System.currentTimeMillis();
		// int selectedAssetsCount = searchResultPage.selectAllAssets();
		searchResultPage.selectAssets(assetsToDownload);
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + searchResultPage.getDescription() + " select (" + assetsToDownload
				+ " assets): "
				+ (endTime - startTime - searchResultPage.getForcedWait() * SearchResultPage.WAIT_FOR_SEARCH_TO_START)
				+ "ms");
		searchResultPage.downloadSelectedAssets();
	}

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1, dependsOnMethods = {
			"freeTextAndFacetedSearch" })
	public void checkDurationOfDownloadJobSearch(DataPackage dataPackage) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		JobsPage jobsPage = assetsPage.navigateToJobsPage();
		if (jobsPage.jobsProcessed()) {
			TestStepReporter.reportln(">" + jobsPage.getDescription() + " download job duration (" + assetsToDownload
					+ " assets): " + jobsPage.getJobDuration(dataPackage.getUsername(), assetsToDownload) + "ms");
		}
	}

}
