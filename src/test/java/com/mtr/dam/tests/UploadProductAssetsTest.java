package com.mtr.dam.tests;

import org.testng.annotations.Test;

import com.mtr.dam.core.BaseTest;
import com.mtr.dam.core.CsvDataProvider;
import com.mtr.dam.core.DriverMaster;
import com.mtr.dam.core.web.pages.AssetsPage;
import com.mtr.dam.core.web.pages.HomePage;
import com.mtr.dam.core.web.pages.JobsPage;
import com.mtr.dam.core.web.pages.LoginPage;
import com.mtr.dam.core.web.pages.ProductUploadPage;
import com.mtr.dam.data.objects.DataPackage;
import com.mtr.dam.utils.FileHelper;
import com.mtr.dam.utils.TestStepReporter;
import com.mtr.dam.utils.TimeModifier;

public class UploadProductAssetsTest extends BaseTest {

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1)
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
		/*
		 * LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(),
		 * "Login page"); HomePage homePage = loginPage.loginAs(dataPackage);
		 * AssetsPage assetsPage = homePage.navigateToAssetsPage(); JobsPage
		 * jobsPage = assetsPage.navigateToJobsPage(); long startTime =
		 * System.currentTimeMillis();
		 */
	}

}
