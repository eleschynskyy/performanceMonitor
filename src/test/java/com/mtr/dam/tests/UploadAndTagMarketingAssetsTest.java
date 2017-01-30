package com.mtr.dam.tests;

import org.testng.annotations.Test;

import com.mtr.dam.core.BaseTest;
import com.mtr.dam.core.CsvDataProvider;
import com.mtr.dam.core.DriverMaster;
import com.mtr.dam.core.web.pages.AssetsPage;
import com.mtr.dam.core.web.pages.HomePage;
import com.mtr.dam.core.web.pages.LoginPage;
import com.mtr.dam.core.web.pages.PublishRecordsPage;
import com.mtr.dam.core.web.pages.UploadFilesPage;
import com.mtr.dam.core.web.pages.UploadedFilesPage;
import com.mtr.dam.data.objects.DataPackage;
import com.mtr.dam.utils.FileHelper;
import com.mtr.dam.utils.TestStepReporter;
import com.mtr.dam.utils.TimeModifier;

public class UploadAndTagMarketingAssetsTest extends BaseTest {

	@Test(dataProvider = "provideDataPackage", dataProviderClass = CsvDataProvider.class, enabled = true, invocationCount = 1, threadPoolSize = 1)
	public void uploadAndTagMarketingAssets(DataPackage dataPackage) {
		LoginPage loginPage = new LoginPage(DriverMaster.getDriverInstance(), "Login page");
		long startTime = System.currentTimeMillis();
		HomePage homePage = loginPage.loginAs(dataPackage);
		long endTime = System.currentTimeMillis();
		TestStepReporter.reportln(">" + loginPage.getDescription() + " logging in: " + (endTime - startTime) + "ms");
		AssetsPage assetsPage = homePage.navigateToAssetsPage();
		UploadedFilesPage uploadedFilesPage = assetsPage.navigateToUploadedFilesPage();
		if (uploadedFilesPage.uploadsCleared()) {
			FileHelper.prepareFiles(dataPackage.getfilePrefix(), dataPackage.getfilesNumber(), dataPackage.getTestCaseNum());
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

}
