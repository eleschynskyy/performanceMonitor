package com.mtr.dam.tests;

import org.testng.annotations.Test;

import com.mtr.dam.core.BaseTest;
import com.mtr.dam.core.CsvDataProvider;
import com.mtr.dam.core.DriverMaster;
import com.mtr.dam.core.web.pages.AssetsPage;
import com.mtr.dam.core.web.pages.HomePage;
import com.mtr.dam.core.web.pages.LoginPage;
import com.mtr.dam.data.objects.DataPackage;
import com.mtr.dam.utils.TestStepReporter;

public class ApproveUploadedAssetsByButtonTest extends BaseTest {

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1)
	public void approveUploadedAssetsByButton(DataPackage dataPackage) {
//		FileHelper.prepareFiles(dataPackage.getfilePrefix(), dataPackage.getfilesNumber(), dataPackage.getTestCaseNum());
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
//		FileHelper.deleteFiles();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
