package com.mtr.dam.core.web.pages;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Button;
import com.mtr.dam.core.web.elements.CustomElement;
import com.mtr.dam.core.web.elements.TextInput;

public class AssetsPage extends WebPage<AssetsPage> {

	private static final String PAGE_URL = HOST + "/Assets/";
	private String windowHandle;

	public AssetsPage(WebDriver driver, String description) {
		super(driver, description);
	}

	@Override
	public AssetsPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getRootMenu().isAvailable() && loadEventOccured();
	}

	public ProductUploadPage openUploadProductFilesDialogWindow() {
		setWindowHandle();
		Set<String> oldWindowsSet = driver.getWindowHandles();
		Actions action = new Actions(driver);
		action.moveToElement(getRootMenu().asWebElement()).click().build().perform();
		action.moveToElement(getRecordsMenu().asWebElement()).click().build().perform();
		action.moveToElement(getUploadProductFilesMenu().asWebElement()).click().build().perform();
		Set<String> newWindowsSet = driver.getWindowHandles();
		newWindowsSet.removeAll(oldWindowsSet);
		String newWindowHandle = newWindowsSet.iterator().next();
		driver.switchTo().window(newWindowHandle);
		ProductUploadPage productUploadPage = new ProductUploadPage(driver, "Product Upload dialog")
				.waitUntilAvailable();
		return productUploadPage;
	}

	private void setWindowHandle() {
		this.windowHandle = driver.getWindowHandle();
	}

	private CustomElement getUploadProductFilesMenu() {
		return new CustomElement(driver, By.xpath("//div[contains(text(), 'Upload product files ...')]"),
				"Upload Product Files menu").waitUntilAvailable();
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

	private CustomElement getViewUploadsMenu() {
		return new CustomElement(driver, By.xpath("//div[contains(text(), 'View uploads')]"), "View uploads menu");
	}

	public String getWindowHandle() {
		return windowHandle;
	}

	public UploadFilesPage openUploadFilesDialogWindow() {
		// setWindowHandle();
		Set<String> oldWindowsSet = driver.getWindowHandles();
		Actions action = new Actions(driver);
		action.moveToElement(getRootMenu().asWebElement()).click().build().perform();
		action.moveToElement(getRecordsMenu().asWebElement()).click().build().perform();
		action.moveToElement(getUploadFilesMenu().asWebElement()).click().build().perform();
		UploadFilesPage uploadFilesPage = new UploadFilesPage(driver, "Files Upload dialog").waitUntilAvailable();
		Set<String> newWindowsSet = driver.getWindowHandles();
		newWindowsSet.removeAll(oldWindowsSet);
		String newWindowHandle = newWindowsSet.iterator().next();
		driver.switchTo().window(newWindowHandle);
		return uploadFilesPage;
	}

	public JobsPage navigateToJobsPage() {
		return new JobsPage(driver, "Jobs page").loadAndWaitUntilAvailable();
	}

	public UploadedFilesPage navigateToUploadedFilesPage() {
		Actions action = new Actions(driver);
		action.moveToElement(getRootMenu().asWebElement()).click().build().perform();
		action.moveToElement(getRecordsMenu().asWebElement()).click().build().perform();
		action.moveToElement(getViewUploadsMenu().asWebElement()).click().build().perform();
		return new UploadedFilesPage(driver, "Uploaded Files page").waitUntilAvailable();
	}

	public BrowsePage navigateToBrowsePage() {
		getBrowseMenu().click();
		return new BrowsePage(driver, "Browse page").waitUntilAvailable();
	}

	private Button getBrowseMenu() {
		return new Button(driver, By.xpath("//div[ancestor::div[@id='menuBar'] and contains(text(), 'Browse')]"),
				"Browse button");
	}

	public SearchResultPage searchForTerm(String term) {
		getSearchInput().inputText(term);
		getSearchButton().click();
		return new SearchResultPage(driver, "Search Result Page for '" + term + "'").waitUntilAvailable();
	}

	private Button getSearchButton() {
		return new Button(driver,
				By.xpath(
						"//a[@href='#' and preceding-sibling::input[parent::span[starts-with(@class, 'adam-search-box')] and @type='text']]"),
				"Search button");
	}

	private TextInput getSearchInput() {
		return new TextInput(driver,
				By.xpath("//input[parent::span[starts-with(@class, 'adam-search-box')] and @type='text']"),
				"Search Text Box");
	}

}
