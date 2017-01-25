package com.mtr.dam.core.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Button;

public class PublishRecordsPage extends WebPage<PublishRecordsPage> {

	private static final String PAGE_URL = HOST + "/Assets/Uploads/Select/PublishRecords";

	public PublishRecordsPage(WebDriver driver, String description) {
		super(driver, description);
	}

	@Override
	public PublishRecordsPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getMakeAvailableButton().waitUntilAvailable().isAvailable() && loadEventOccured();
	}

	public UploadedFilesPage makeRecordsAvailable() {
		getMakeAvailableButton().click();
		getUpdateButton().click();
		return new UploadedFilesPage(driver, "Uploaded files page").waitUntilAvailable();
	}

	private Button getUpdateButton() {
		return new Button(driver,
				By.xpath(
						"//a[ancestor::div[contains(@class, 'dialog-panel')] and starts-with(text(), 'Update ') and contains(text(), ' records')]"),
				"Update record(s) button").waitUntilAvailable();
	}

	private Button getMakeAvailableButton() {
		return new Button(driver,
				By.xpath(
						"//a[parent::span[@class='main-buttons' and parent::div[@class='page-tools']] and contains(text(), 'Make available')]"),
				"Make Available button");
	}

}
