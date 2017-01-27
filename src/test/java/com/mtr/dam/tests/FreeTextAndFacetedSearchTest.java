package com.mtr.dam.tests;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import com.mtr.dam.core.BaseTest;
import com.mtr.dam.core.CsvDataProvider;
import com.mtr.dam.core.DriverMaster;
import com.mtr.dam.core.web.pages.AssetsPage;
import com.mtr.dam.core.web.pages.HomePage;
import com.mtr.dam.core.web.pages.LoginPage;
import com.mtr.dam.core.web.pages.SearchResultPage;
import com.mtr.dam.data.objects.User;
import com.mtr.dam.utils.TestStepReporter;

public class FreeTextAndFacetedSearchTest extends BaseTest {

	@Test(dataProvider = "provideUserFromList", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1)
	public void freeTextAndFacetedSearch(User user) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(user);
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
		int selectedAssetsCount = searchResultPage.selectAllAssets();
		// searchResultPage.selectAssets(assetsToDownload);
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + searchResultPage.getDescription() + " select (" + selectedAssetsCount
				+ " assets): "
				+ (endTime - startTime - searchResultPage.getForcedWait() * SearchResultPage.WAIT_FOR_SEARCH_TO_START)
				+ "ms");
		// searchResultPage.downloadSelectedAssets();
	}

}