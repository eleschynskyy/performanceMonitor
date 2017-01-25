package com.mtr.dam.core.web.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Button;
import com.mtr.dam.core.web.elements.CustomElement;
import com.mtr.dam.core.web.elements.Link;
import com.mtr.dam.core.web.elements.TextInput;

public class SearchResultPage extends WebPage<SearchResultPage> {

	private static final int DEFAULT_TIMEOUT = 3600000;
	private static final int DEFAULT_RETRY_DELAY = 500;
	public static final int WAIT_FOR_SEARCH_TO_START = 3000;
	private int forcedWait = 0;

	private static final String PAGE_URL = HOST + "/Assets/Records/";

	public SearchResultPage(WebDriver driver, String description) {
		super(driver, description);
	}

	@Override
	public SearchResultPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return urlRefreshed() && getResultsBox().isAvailable() && loadEventOccured();
	}

	public int getForcedWait() {
		return forcedWait;
	}

	public void resetForcedWait() {
		forcedWait = 0;
	}

	public SearchResultPage searchForTerm(String term) {
		getSearchInput().asWebElement().clear();
		getSearchInput().inputText(term);
		getSearchButton().click();
		return new SearchResultPage(driver, "Search Result Page for '" + term + "'").waitUntilAvailable();
	}

	public boolean setPageSize(int i) {
		getPageSizeDropDownMenu().waitUntilAvailable().click();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getRequiredMenuOption(i).isAvailable()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getRequiredMenuOption(i).isAvailable()) {
			getRequiredMenuOption(i).click();
			timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getResultsBox().isAvailable() && resultsRefreshed()) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (getResultsBox().isAvailable() && resultsRefreshed()) {
				return true;
			}
		}
		return false;
	}

	public boolean filterByAssetStatus(ArrayList<String> list) {
		for (String filter : list) {
			if (getAssetStatusFilterFor(filter).asWebElement().isDisplayed()) {
				getAssetStatusFilterFor(filter).click();
			} else {
				accessAssetTypeDialog(filter);
			}
			waitForSearchToStart();
			// wait for results to refresh
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getResultsBox().isAvailable() && searchCompleted(filter)) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (!getResultsBox().isAvailable() || !searchCompleted(filter)) {
				return false;
			}
		}
		return true;
	}

	public boolean filterByCountry(ArrayList<String> list) {
		for (String filter : list) {
			if (getCountryFilterFor(filter).asWebElement().isDisplayed()) {
				getCountryFilterFor(filter).click();
			} else {
				expandCountryOptionsAndClickOn(filter);
			}
			waitForSearchToStart();
			// wait for results to refresh
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getResultsBox().isAvailable() && searchCompleted(filter)) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (!getResultsBox().isAvailable() || !searchCompleted(filter)) {
				return false;
			}
		}
		return true;
	}

	public int selectAllAssets() {
		int recordsTotal = getResultsTotal();
		getSelectAllLink().click();
		waitUntilDownloadActionPanelAppears(recordsTotal);
		return recordsTotal;
	}
	
	public boolean selectAssets(int number) {
		Actions action;
		int i = 0;
		List<WebElement> assetContainers = getAssetContainers();
		List<WebElement> getSelectBoxes = getSelectBoxes();
		Iterator<WebElement> assetContainersIterator = assetContainers.iterator();
		Iterator<WebElement> assetSelectBoxesIterator = getSelectBoxes.iterator();
		WebElement assetContainer;
		WebElement selectBox;
		while (i < number && assetContainersIterator.hasNext()) {
			action = new Actions(driver);
			assetContainer = assetContainersIterator.next();
			selectBox = assetSelectBoxesIterator.next();
			action.moveToElement(assetContainer).build().perform();
//			System.out.println((i + 1) + ": " + assetContainer.getAttribute("id"));
			action.moveToElement(selectBox).click().build().perform();
			i++;
		}
		waitUntilDownloadActionPanelAppears(number);
		return true;
	}
	
	private List<WebElement> getAssetContainers() {
		return getListOfWebElements(By.xpath("//div[@class='adam-display-item']"));
	}
	
	private List<WebElement> getSelectBoxes() {
		return getListOfWebElements(By.xpath("//span[@class='adam-selectbox']"));
	}
	
	private boolean waitUntilDownloadActionPanelAppears(int number) {
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getSelectedItemsCount().isAvailable() && 
				getSelectedItemsCount().asWebElement().isDisplayed() && 
				getSelectedItemsCount().getText().equals("" + number)) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getSelectedItemsCount().isAvailable() && 
			getSelectedItemsCount().asWebElement().isDisplayed() &&
			getSelectedItemsCount().getText().equals("" + number)) {
				return true;
		}
		return false;
	}
	
	public void downloadSelectedAssets() {
		getDownloadButton().click();
	}

	private Button getDownloadButton() {
		return new Button(driver, By.xpath("//div[contains(@class, 'action-download') and contains(text(), 'Download')]"), "Download button");
	}
	
	private CustomElement getSelectedItemsCount() {
		return new CustomElement(driver, By.xpath("//span[@class='selection-counter' and parent::span[contains(text(), 'items selected')]]"), "Action panel");
	}

	private int getResultsTotal() {
		String status = getResultsBox().getText(); 
		Pattern statusPattern = Pattern.compile("Showing \\d - (\\d+) of (\\d+) record\\(s\\)");
		Matcher statusMatcher = statusPattern.matcher(status);
		if (statusMatcher.matches()) { 
			return Integer.parseInt(statusMatcher.group(2));
		}
		return -1; 
	}
	
	private Link getSelectAllLink() {
		return new Link(driver, By.xpath("//a[contains(text(), 'Select all') and ancestor::div[@class='item-count']]"),
				"Select all link");
	}

	private CustomElement getCountryFilterFor(String filter) {
		return new CustomElement(driver,
				By.xpath("//span[contains(text(), '" + filter
						+ "') and preceding-sibling::input[@type='checkbox'] and ancestor::div[@class='filter-control-checkboxes'] and ancestor::div[child::span[@class='filter-header' and contains(text(), 'Country')]]]"),
				"Country '" + filter + "' checkbox");
	}

	private void expandCountryOptionsAndClickOn(String filter) {
		getShowMoreLinkCountry().click();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getCountryFilterFor(filter).isAvailable() && getCountryFilterFor(filter).asWebElement().isDisplayed()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getCountryFilterFor(filter).isAvailable() && getCountryFilterFor(filter).asWebElement().isDisplayed()) {
			getCountryFilterFor(filter).click();
		}
	}

	private void accessAssetTypeDialog(String filter) {
		getShowMoreLinkAssetType().click();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getOptionFromFilterPanel(filter).isAvailable()
					&& getOptionFromFilterPanel(filter).asWebElement().isDisplayed()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getOptionFromFilterPanel(filter).isAvailable()
				&& getOptionFromFilterPanel(filter).asWebElement().isDisplayed()) {
			getOptionFromFilterPanel(filter).click();
			getConfirmFilterButton().click();
		}
	}

	private CustomElement getOptionFromFilterPanel(String filter) {
		return new CustomElement(driver,
				By.xpath("//span[contains(text(), '" + filter + "') and ancestor::div[@class='filter-panel']]"),
				"'" + filter + "' filter value from filter panel");
	}

	private Link getConfirmFilterButton() {
		return new Link(driver,
				By.xpath(
						"//a[@class='button default-button ok-button' and ancestor::div[starts-with(@class, 'dialog-panel')]]"),
				"Confirm filter button").waitUntilAvailable();
	}

	private Link getShowMoreLinkAssetType() {
		return new Link(driver,
				By.xpath(
						"//a[@class='show-more' and ancestor::div[child::span[@class='filter-header' and starts-with(text(), 'Asset Type')]]]"),
				"Show more 'Asset Type'").waitUntilAvailable();
	}

	private Link getShowMoreLinkCountry() {
		return new Link(driver,
				By.xpath(
						"//a[@class='show-more' and ancestor::div[child::span[@class='filter-header' and starts-with(text(), 'Country')]]]"),
				"Show more 'Country'").waitUntilAvailable();
	}

	private CustomElement getAssetStatusFilterFor(String filter) {
		return new CustomElement(driver,
				By.xpath("//span[contains(text(),'" + filter
						+ "') and preceding-sibling::input[@type='checkbox' and ancestor::div[child::span[starts-with(text(), 'Asset Type')]]]]"),
				"Asset Type '" + filter + "' checkbox");
	}

	private CustomElement getSelectedFacets(String filter) {
		return new CustomElement(driver,
				By.xpath("//span[contains(text(), '" + filter
						+ "') and ancestor::div[contains(@style, 'visibility: visible;')] and ancestor::div[@class='facet-selection']]"),
				"Selected Facets");
	}

	private boolean searchCompleted(String filter) {
		return getSelectedFacets(filter).isAvailable();
	}

	private CustomElement getResultsBox() {
		return new CustomElement(driver,
				By.xpath("//span[parent::div[@class='item-count'] and starts-with(text(), 'Showing')]"),
				"'Showing label'");
	}

	private TextInput getSearchInput() {
		return new TextInput(driver,
				By.xpath("//input[parent::span[starts-with(@class, 'adam-search-box')] and @type='text']"),
				"Search Text Box");
	}

	private Button getSearchButton() {
		return new Button(driver,
				By.xpath(
						"//a[@href='#' and preceding-sibling::input[parent::span[starts-with(@class, 'adam-search-box')] and @type='text']]"),
				"Search button");
	}

	private CustomElement getPageSizeDropDownMenu() {
		return new CustomElement(driver,
				By.xpath(
						"//span[contains(@class, 'labeldropdown-arrow') and ancestor::div[@class='adam-page-size' and contains(text(), 'Page size')]]"),
				"Page Size dropdowm menu");
	}

	private CustomElement getRequiredMenuOption(int i) {
		return new CustomElement(driver,
				By.xpath("//div[contains(@class, 'menu-radiobutton') and contains(text(), '" + i + "')]"),
				"Page size=" + i);
	}

	private int delay() {
		try {
			Thread.sleep(DEFAULT_RETRY_DELAY);
			return DEFAULT_RETRY_DELAY;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean resultsRefreshed() {
		String status = getResultsBox().getText();
		Pattern statusPattern = Pattern.compile("Showing \\d - (\\d+) of (\\d+) record\\(s\\)");
		Matcher statusMatcher = statusPattern.matcher(status);
		if (statusMatcher.matches()) {
			if (Integer.parseInt(statusMatcher.group(1)) > 10) {
				return true;
			}
		}
		return false;
	}

	private void waitForSearchToStart() {
		forcedWait++;
		try {
			Thread.sleep(WAIT_FOR_SEARCH_TO_START);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean urlRefreshed() {
		Pattern pageDescriptionPattern = Pattern.compile("Search Result Page for '(.+)'");
		Matcher pageDescriptionMatcher = pageDescriptionPattern.matcher(getDescription());
		if (pageDescriptionMatcher.matches()) {
			if (driver.getCurrentUrl().contains(PAGE_URL + "?defaultSearch=" + pageDescriptionMatcher.group(1))) {
				return true;
			}
		}
		return false;
	}

}
