package com.mtr.dam.tests;

import org.testng.annotations.Test;

import com.mtr.dam.core.BaseTest;
import com.mtr.dam.core.CsvDataProvider;
import com.mtr.dam.core.DriverMaster;
import com.mtr.dam.core.web.pages.AssetsPage;
import com.mtr.dam.core.web.pages.BrowsePage;
import com.mtr.dam.core.web.pages.HomePage;
import com.mtr.dam.core.web.pages.LoginPage;
import com.mtr.dam.data.objects.DataPackage;
import com.mtr.dam.utils.TestStepReporter;
import com.relevantcodes.extentreports.LogStatus;

public class ApproveUploadedAssetsByButtonTest extends BaseTest {

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1)
	public void approveUploadedAssetsByButton(DataPackage dataPackage) {
		test.log(LogStatus.PASS, "TEXT");
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		/*
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		BrowsePage browsePage = assetsPage.navigateToBrowsePage();
		startTime = System.currentTimeMillis();
		browsePage.drillDownToWorkInProgress();
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription() + " show content: " + (endTime - startTime) + "ms");
		//save view
		////div[contains(@class, 'action-addsavedview') and contains(text(), 'Save view')]
		/*
		browsePage.resetForcedWait();
		startTime = System.currentTimeMillis();
		int selectedAssetsCount = browsePage.selectAllAssets();
		endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + browsePage.getDescription() + " select (" + selectedAssetsCount
				+ " assets): "
				+ (endTime - startTime - browsePage.getForcedWait() * BrowsePage.WAIT_FOR_SEARCH_TO_START)
				+ "ms");
				*/
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
