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

public class BrowsePage extends WebPage<BrowsePage> {

	private static final int DEFAULT_TIMEOUT = 3600000;
	private static final int DEFAULT_RETRY_DELAY = 500;
	public static final int WAIT_FOR_SEARCH_TO_START = 3000;
	public static final int WAIT_TIME_TO_CONTINUE_WORKING = 3000;
	private int forcedWait = 0;

	private static final String PAGE_URL = HOST + "/Assets/Records/Browse";

	public BrowsePage(WebDriver driver, String description) {
		super(driver, description);
	}

	@Override
	public BrowsePage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getMacMenu().isAvailable() && loadEventOccured();
	}

	public boolean showContent() {
		getMacMenu().click();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getFilterBox().isAvailable() && getResultsBox().isAvailable()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getFilterBox().isAvailable() && getResultsBox().isAvailable()) {
			return true;
		}
		return false;
	}

	private CustomElement getResultsBox() {
		return new CustomElement(driver,
				By.xpath("//span[parent::div[@class='item-count'] and starts-with(text(), 'Showing')]"),
				"'Showing label'");
	}

	private CustomElement getFilterBox() {
		return new CustomElement(driver, By.xpath("//span[@class='title' and contains(text(), 'Filter')]"),
				"Filter lable");
	}

	private Link getMacMenu() {
		return new Link(driver, By.xpath("//span[@class='tree-node-label' and contains(text(), 'Metro Asset Center')]"),
				"Metro Asset Center link");
	}

	private int delay() {
		try {
			Thread.sleep(DEFAULT_RETRY_DELAY);
			return DEFAULT_RETRY_DELAY;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
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

	/*
	 * private boolean resultsRefreshedAgainstPrevious(int number) { String
	 * status = getResultsBox().getText(); Pattern statusPattern =
	 * Pattern.compile("Showing \\d - (\\d+) of (\\d+) record\\(s\\)"); Matcher
	 * statusMatcher = statusPattern.matcher(status); if
	 * (statusMatcher.matches()) { if (Integer.parseInt(statusMatcher.group(2))
	 * != number) { return true; } } return false; }
	 */

	/*
	 * private int getResultsNumber() { String status =
	 * getResultsBox().getText(); Pattern statusPattern =
	 * Pattern.compile("Showing \\d - (\\d+) of (\\d+) record\\(s\\)"); Matcher
	 * statusMatcher = statusPattern.matcher(status); if
	 * (statusMatcher.matches()) { return
	 * Integer.parseInt(statusMatcher.group(2)); } return -1; }
	 */

	private CustomElement getRequiredMenuOption(int i) {
		return new CustomElement(driver,
				By.xpath("//div[contains(@class, 'menu-radiobutton') and contains(text(), '" + i + "')]"),
				"Page size=" + i);
	}

	private CustomElement getPageSizeDropDownMenu() {
		return new CustomElement(driver,
				By.xpath(
						"//span[contains(@class, 'labeldropdown-arrow') and ancestor::div[@class='adam-page-size' and contains(text(), 'Page size')]]"),
				"Page Size dropdowm menu");
	}

	public boolean filterByYear(ArrayList<String> list) {
		// int resultsTotal = 0;
		for (String filter : list) {
			// resultsTotal = resultsTotal +
			// Integer.parseInt(getNumberByYearFilter(filter).getText());
			// getting current number of results
			/*
			 * int currentResultsNumber = getResultsNumber(); if
			 * (currentResultsNumber == -1) { return false; }
			 */
			getYearFilterFor(filter).click();
			// wait for search to start
			waitForSearchToStart();
			/*
			 * int timePassed = 0; while (timePassed < DEFAULT_TIMEOUT) { if
			 * (searchStarted()) { break; } timePassed = timePassed + delay(); }
			 */
			// wait for results to refresh
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getResultsBox().isAvailable() && /*
														 * resultsRefreshedAgainstPrevious(
														 * currentResultsNumber)
														 */ searchCompleted()) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (!getResultsBox().isAvailable() || !searchCompleted()/*
																	 * ||
																	 * !resultsRefreshedAgainstPrevious(
																	 * currentResultsNumber)
																	 */) {
				return false;
			}
		}
		return true;
	}

	private boolean searchCompleted() {
		// System.out.println("style=" +
		// getStatusInfo().asWebElement().getAttribute("style"));
		return getStatusInfo().asWebElement().getAttribute("style").contains("display: none;");
	}

	/*
	 * private boolean searchStarted() { // System.out.println("style=" +
	 * getStatusInfo().asWebElement().getAttribute("style")); return
	 * getStatusInfo().asWebElement().getAttribute("style").
	 * contains("display: block;"); }
	 */

	private CustomElement getStatusInfo() {
		return new CustomElement(driver,
				By.xpath(
						"//div[@class='status-indiciator status-pleasewait' and child::span[@class='status-info' and starts-with(text(), 'Please wait...')]]"),
				"Status indication").waitUntilAvailable();
	}

	/*
	 * private CustomElement getNumberByYearFilter(String filter) { return new
	 * CustomElement(driver, By.
	 * xpath("//i[preceding-sibling::div[preceding-sibling::span[contains(text(), '"
	 * + filter +
	 * "') and preceding-sibling::input[@type='checkbox'] and ancestor::div[@class='filter-control-checkboxes']]]]"
	 * ), "Year '" + filter + "' checkbox"); }
	 */

	private CustomElement getYearFilterFor(String filter) {
		return new CustomElement(driver,
				By.xpath("//span[contains(text(), '" + filter
						+ "') and preceding-sibling::input[@type='checkbox'] and ancestor::div[@class='filter-control-checkboxes']]"),
				"Year '" + filter + "' checkbox");
	}

	public boolean filterByAssetStatus(ArrayList<String> list) {
		for (String filter : list) {
			getAssetStatusFilterFor(filter).click();
			waitForSearchToStart();
			// wait for results to refresh
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getResultsBox().isAvailable() && searchCompleted()) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (!getResultsBox().isAvailable() || !searchCompleted()) {
				return false;
			}
		}
		return true;
	}

	public boolean filterByText(ArrayList<String> list) {
		for (String filter : list) {
			addSearchTerm(filter);
			waitForSearchToStart();
			// wait for results to refresh
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getResultsBox().isAvailable() && searchCompleted()) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (!getResultsBox().isAvailable() || !searchCompleted()) {
				return false;
			}
		}
		return true;
	}

	private void addSearchTerm(String filter) {
		getTextFilterInput().inputText(filter);
		getAddTextFilterButton().click();
	}

	private Button getAddTextFilterButton() {
		return new Button(driver, By.xpath("//div[parent::div[@class='filter-content'] and @class='add-button']"),
				"Add text filter button");
	}

	private TextInput getTextFilterInput() {
		return new TextInput(driver,
				By.xpath(
						"//input[parent::div[@class='filter-content'] and @class='text-input' and @placeholder='Type to add a filter']"),
				"Text filter");
	}

	public boolean filterByCountry(ArrayList<String> list) {
		for (String filter : list) {
			if (getCountryFilterFor(filter).asWebElement().isDisplayed()) {
				getCountryFilterFor(filter).click();
			} else {
				accessCountryDialog(filter);
			}
			waitForSearchToStart();
			// wait for results to refresh
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getResultsBox().isAvailable() && searchCompleted()) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (!getResultsBox().isAvailable() || !searchCompleted()) {
				return false;
			}
		}
		return true;
	}

	private void accessCountryDialog(String filter) {
		getShowMoreLink().click();
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

	private Link getConfirmFilterButton() {
		return new Link(driver,
				By.xpath(
						"//a[@class='button default-button ok-button' and ancestor::div[starts-with(@class, 'dialog-panel')]]"),
				"Confirm filter button").waitUntilAvailable();
	}

	private CustomElement getOptionFromFilterPanel(String filter) {
		return new CustomElement(driver,
				By.xpath("//span[contains(text(), '" + filter + "') and ancestor::div[@class='filter-panel']]"),
				"Country filter value from filter panel");
	}

	private Link getShowMoreLink() {
		return new Link(driver,
				By.xpath(
						"//a[@class='show-more' and ancestor::div[child::span[@class='filter-header' and contains(text(), 'Country')]]]"),
				"Show more 'Country'").waitUntilAvailable();
	}

	private CustomElement getCountryFilterFor(String filter) {
		return new CustomElement(driver,
				By.xpath("//span[contains(text(), '" + filter
						+ "') and preceding-sibling::input[@type='checkbox'] and ancestor::div[@class='filter-control-checkboxes'] and ancestor::div[child::span[@class='filter-header' and contains(text(), 'Country')]]]"),
				"Country '" + filter + "' checkbox");
	}

	private void waitForSearchToStart() {
		forcedWait++;
		try {
			Thread.sleep(WAIT_FOR_SEARCH_TO_START);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private CustomElement getAssetStatusFilterFor(String filter) {
		return new CustomElement(driver,
				By.xpath("//span[contains(text(), '" + filter
						+ "') and preceding-sibling::input[@type='checkbox'] and ancestor::div[@class='filter-control-checkboxes'] and ancestor::div[child::span[@class='filter-header' and contains(text(), 'Asset Status')]]]"),
				"Asset Type '" + filter + "' checkbox");
	}

	public int getForcedWait() {
		return forcedWait;
	}

	public void resetForcedWait() {
		forcedWait = 0;
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
			// System.out.println((i + 1) + ": " +
			// assetContainer.getAttribute("id"));
			action.moveToElement(selectBox).click().build().perform();
			i++;
		}
		waitUntilDownloadActionPanelAppears(number);
		return true;
	}

	private boolean waitUntilDownloadActionPanelAppears(int number) {
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getSelectedItemsCount().isAvailable() && getSelectedItemsCount().asWebElement().isDisplayed()
					&& getSelectedItemsCount().getText().equals("" + number)) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getSelectedItemsCount().isAvailable() && getSelectedItemsCount().asWebElement().isDisplayed()
				&& getSelectedItemsCount().getText().equals("" + number)) {
			return true;
		}
		return false;
	}

	private CustomElement getSelectedItemsCount() {
		return new CustomElement(driver,
				By.xpath("//span[@class='selection-counter' and parent::span[contains(text(), 'items selected')]]"),
				"Action panel");
	}

	private List<WebElement> getSelectBoxes() {
		return getListOfWebElements(By.xpath("//span[@class='adam-selectbox']"));
	}

	private List<WebElement> getAssetContainers() {
		return getListOfWebElements(By.xpath("//div[@class='adam-display-item']"));
	}

	public void downloadSelectedAssets() {
		getDownloadButton().click();
		continueWorking();
	}

	private void continueWorking() {
		try {
			Thread.sleep(WAIT_TIME_TO_CONTINUE_WORKING);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (getContinueWorkingButton().isAvailable() && getContinueWorkingButton().asWebElement().isDisplayed()) {
			getContinueWorkingButton().click();
		}
	}

	private Button getContinueWorkingButton() {
		return new Button(driver,
				By.xpath(
						"//input[@type='button' and @value='Continue working' and ancestor::div[starts-with(@class, 'adam-processing-download') and contains(@style, 'visibility: visible;') and contains(@style, 'display: block;')]]"),
				"Continue working button");
	}

	private Button getDownloadButton() {
		return new Button(driver,
				By.xpath("//div[contains(@class, 'action-download') and contains(text(), 'Download')]"),
				"Download button");
	}

}
