package com.mtr.dam.core.web.pages;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Button;
import com.mtr.dam.core.web.elements.CustomElement;
import com.mtr.dam.core.web.elements.Link;

public class UploadedFilesPage extends WebPage<UploadedFilesPage> {

	private static final String PAGE_URL = HOST + "/Assets/Uploads/";
	private static final int DEFAULT_TIMEOUT = 3600000;
	private static final int DEFAULT_RETRY_DELAY = 500;

	public UploadedFilesPage(WebDriver driver, String description) {
		super(driver, description);
	}

	@Override
	public UploadedFilesPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return (getUploadedFiles().size() > 0 || getNoFilesLabel().waitUntilAvailable().isAvailable())
				&& loadEventOccured();
	}

	private CustomElement getNoFilesLabel() {
		return new CustomElement(driver,
				By.xpath(
						"//p[ancestor::tr[@class='adam-no-result'] and ancestor::table[contains(@class, 'adam-search-results')] and contains(text(), 'There are no files to put into ADAM')]"),
				"'There are no files to put into ADAM.' label");
	}

	private List<WebElement> getUploadedFiles() {
		return getListOfWebElements(
				By.xpath("//tr[@class='adam-row' and ancestor::table[contains(@class, 'adam-search-results')]]"));
	}

	public boolean uploadsCleared() {
		if (selectAll()) {
			if (allDeleted()) {
				return true;
			}
			return false;
		}
		return true;
	}

	private boolean selectAll() {
		if (getSelectAllCheckbox().isAvailable()) {
			getSelectAllCheckbox().click();
			if (nextPagesLinks().size() > 0) {
				getSelectAllLink().click();
			}
			return true;
		}
		return false;
	}

	private boolean allDeleted() {
		Actions action = new Actions(driver);
		action.moveToElement(getMakeAvailableMenu().asWebElement()).click().build().perform();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getDeleteMenu().isAvailable()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getDeleteMenu().isAvailable()) {
			action.moveToElement(getDeleteMenu().asWebElement()).click().build().perform();
			timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getConfirmDeleteButton().isAvailable()) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (getConfirmDeleteButton().isAvailable()) {
				confirmDelete();
				if (successfullyDeleted()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean successfullyDeleted() {
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getMakeAvailableButton().isAvailable()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getMakeAvailableButton().isAvailable()) {
			return true;
		}
		return false;
	}

	private void confirmDelete() {
		getConfirmDeleteButton().click();
	}

	private Button getConfirmDeleteButton() {
		return new Button(driver, By.xpath("//input[@type='submit' and @value='Delete']"), "Confirm Delete button");
	}

	public PublishRecordsPage makeAvailableUploadedFiles(long timestamp, String username) {
		getSelectAllCheckbox().click();
		if (nextPagesLinks().size() > 0) {
			getSelectAllLink().click();
		}
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getMakeNAvailableButton().isAvailable()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getMakeNAvailableButton().isAvailable()) {
			getMakeNAvailableButton().click();
		}
		return new PublishRecordsPage(driver, "Publish Records page").waitUntilAvailable();
	}

	private Button getMakeNAvailableButton() {
		return new Button(driver, By.xpath("//span[starts-with(text(), 'Make ')  and contains(text(), ' available')]"),
				"Make N Available button");
	}

	private Button getMakeAvailableButton() {
		return new Button(driver, By.xpath("//span[contains(text(), 'Make available')]"), "Make Available button");
	}

	private Link getSelectAllLink() {
		return new Link(driver, By.xpath("//a[contains(text(), 'Select all items that match the current filter')]"),
				"Select all link");
	}

	private Link getSelectAllCheckbox() {
		return new Link(driver, By.xpath("//a[parent::td[@class='checkbox-cell' and @title='Select all']]"),
				"Select all checkbox");
	}

	private List<WebElement> nextPagesLinks() {
		return getListOfWebElements(By.xpath("//a[@class='number-button']"));
	}

	private CustomElement getMakeAvailableMenu() {
		return new CustomElement(driver,
				By.xpath(
						"//span[preceding-sibling::span[starts-with(text(), 'Make ')  and contains(text(), ' available')]]"),
				"Make Available root menu");
	}

	private CustomElement getDeleteMenu() {
		return new CustomElement(driver,
				By.xpath("//div[parent::div[@class='menu-group'] and contains(text(), 'Delete')]"), "Delete menu");
	}

	private int delay() {
		try {
			Thread.sleep(DEFAULT_RETRY_DELAY);
			return DEFAULT_RETRY_DELAY;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private CustomElement getRecordsMenu() {
		return new CustomElement(driver,
				By.xpath("//div[contains(text(), 'Records') and following-sibling::div[contains(text(), 'Spaces')]]"),
				"Records menu").waitUntilAvailable();
	}

	private CustomElement getRootMenu() {
		return new CustomElement(driver,
				By.xpath("//span[@class and preceding-sibling::span[contains(text(), 'Assets')]]"), "Root menu");
	}

	private CustomElement getUploadFilesMenu() {
		return new CustomElement(driver, By.xpath("//div[contains(text(), 'Upload files ...')]"), "Upload Files menu")
				.waitUntilAvailable();
	}

	public UploadFilesPage openUploadFilesDialogWindow() {
		Set<String> oldWindowsSet = driver.getWindowHandles();
		Actions action = new Actions(driver);
		action.moveToElement(getRootMenu().asWebElement()).click().build().perform();
		action.moveToElement(getRecordsMenu().asWebElement()).click().build().perform();
		action.moveToElement(getUploadFilesMenu().asWebElement()).click().build().perform();
		Set<String> newWindowsSet = driver.getWindowHandles();
		newWindowsSet.removeAll(oldWindowsSet);
		String newWindowHandle = newWindowsSet.iterator().next();
		driver.switchTo().window(newWindowHandle);
		UploadFilesPage uploadFilesPage = new UploadFilesPage(driver, "Files Upload dialog").waitUntilAvailable();
		return uploadFilesPage;
	}

	public boolean allFilesUploaded() {
		return getNoFilesLabel().isAvailable();
	}

}
