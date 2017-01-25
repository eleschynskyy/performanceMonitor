package com.mtr.dam.core.web.pages;

import com.mtr.dam.utils.FileHelper;
import java.io.File;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Button;
import com.mtr.dam.core.web.elements.CustomElement;

public class UploadFilesPage extends WebPage<UploadFilesPage> {

	private static final String PAGE_URL = HOST + "/Assets/Uploads/+";
	private static final int DEFAULT_TIMEOUT = 3600000;
	private static final int DEFAULT_RETRY_DELAY = 500;

	public UploadFilesPage(WebDriver driver, String description) {
		super(driver, description);
	}

	@Override
	public UploadFilesPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getFileUploadDialog().waitUntilAvailable().isAvailable() && loadEventOccured();
	}

//	private Button getAddFilesButton() {
//		return new Button(driver, By.xpath("//a[@href='#']"), "Add files button");
//	}

	public UploadedFilesPage uploadFilesAndReturnToUploadedFilesPage(String filePrefix, int numberOfFiles) {
		String generatedFilePrefix = FileHelper.getGeneratedFilePrefix();
		String fileSuffix = FileHelper.getFileSuffix();
		for (int i = 1; i <= numberOfFiles; i++) {
			File file = new File("src/main/resources/toUpload/" + generatedFilePrefix + String.format("%03d", i) + fileSuffix);
			getFileUploadDialog().asWebElement().sendKeys(file.getAbsolutePath());
		}
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getDoneFiles().size() == numberOfFiles) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if ((getDoneFiles().size() == numberOfFiles) && getToUploadedFilesButton().asWebElement().isDisplayed()) {
			getToUploadedFilesButton().click();
			Set<String> windowsSet = driver.getWindowHandles();
			driver.switchTo().window(windowsSet.iterator().next());
			return new UploadedFilesPage(driver, "Uploaded Files page").waitUntilAvailable();
		} else {
			throw new TimeoutException(
					"Timed out after " + DEFAULT_TIMEOUT + "ms of waiting for all files to be loaded");
		}
	}

	private CustomElement getFileUploadDialog() {
		return new CustomElement(driver, By.xpath("//input[@type='file']"), "File Upload dialog").waitUntilAvailable();
	}

	private Button getToUploadedFilesButton() {
		return new Button(driver, By.xpath("//a[contains(text(), 'To uploaded files >')]"), "To uploaded files button");
	}

	private List<WebElement> getDoneFiles() {
		return getListOfWebElements(By.xpath("//div[@class='file success']"));
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
