package com.mtr.dam.core.web.pages;

import com.mtr.dam.utils.ConfigProperties;
import com.mtr.dam.utils.FileHelper;
import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Button;
import com.mtr.dam.core.web.elements.CustomElement;

public class ProductUploadPage extends WebPage<ProductUploadPage> {

	private static final String PAGE_URL = HOST + "/ProductUpload/";
	private static final int DEFAULT_TIMEOUT = 3600000;
	private static final int DEFAULT_RETRY_DELAY = 500;
	private static String dataLocation = ConfigProperties.getSystemProperties("data.location");

	public ProductUploadPage(WebDriver driver, String description) {
		super(driver, description);
	}

	@Override
	public ProductUploadPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getAddFilesButton().waitUntilAvailable().isAvailable() && loadEventOccured();
	}

	private Button getAddFilesButton() {
		return new Button(driver, By.xpath("//a[@href='#']"), "Add files button");
	}

	public AssetsPage uploadFilesAndReturnToAssetsPage(String filePrefix, int numberOfFiles, String windowHandle) {
		String generatedFilePrefix = FileHelper.getGeneratedFilePrefix();
		String fileSuffix = FileHelper.getFileSuffix();
		for (int i = 1; i <= numberOfFiles; i++) {
			File file = new File(
					dataLocation + generatedFilePrefix + String.format("%03d", i) + fileSuffix);
//			System.out.println(file.getAbsolutePath());
			getFileUploadDialog().asWebElement().sendKeys(file.getAbsolutePath());
			// getFileUploadDialog().asWebElement().sendKeys(file1.getAbsolutePath()
			// + "\n" + file2.getAbsolutePath());
		}
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getDownloadSuccess().size() == numberOfFiles) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if ((getDownloadSuccess().size() == numberOfFiles) && getStartUploadButton().asWebElement().isDisplayed()) {
			getStartUploadButton().click();
			if (allFilesUploaded(numberOfFiles)) {
				getCloseButton().click();
				driver.switchTo().window(windowHandle);
				return new AssetsPage(driver, "Assets page").waitUntilAvailable(); 
			} else {
				throw new TimeoutException(
						"Timed out after " + DEFAULT_TIMEOUT + "ms of waiting for all files to be uploaded");
			}
		} else {
			throw new TimeoutException(
					"Timed out after " + DEFAULT_TIMEOUT + "ms of waiting for all files to be loaded");
		}
	}

	private Button getCloseButton() {
		return new Button(driver, By.id("cancel-button"), "Close Upload button");
	}

	private boolean allFilesUploaded(int numberOfFiles) {
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getDownloadAdded().size() == numberOfFiles) {
				return true;
			}
			timePassed = timePassed + delay();
		}
		return false;
	}

	private List<WebElement> getDownloadAdded() {
		return getListOfWebElements(By.xpath("//div[@class='file template-download added']"));
	}

	private CustomElement getFileUploadDialog() {
		return new CustomElement(driver, By.id("input-file"), "File Upload dialog").waitUntilAvailable();
	}

	private Button getStartUploadButton() {
		return new Button(driver, By.id("upload-button"), "Start Upload button");
	}

	private List<WebElement> getDownloadSuccess() {
		return getListOfWebElements(By.xpath("//div[@class='file template-download success']"));
	}

	private int delay() {
		try {
			Thread.sleep(DEFAULT_RETRY_DELAY);
			return DEFAULT_RETRY_DELAY;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
