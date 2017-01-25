package com.mtr.dam.core.web.pages;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.CustomElement;

public class JobsPage extends WebPage<JobsPage> {

	private static final String PAGE_URL = HOST + "/Assets/Jobs/";
	private static final int DEFAULT_TIMEOUT = 3600000;
	private static final int DEFAULT_RETRY_DELAY = 500;
	private String JOB_TYPE = "SynchronizationWithArticleCacheJob";
	private String CREATED_ON_DATE_FORMAT = "MM/dd/yyyy hh:mm:ss a";
	private int REFRESH_TIME = 2000;
	private boolean latestJobFound = false;
	private boolean jobIsOutOfRange = false;
	// private long latestJobTimestamp = 0;
	private String latestJobHref = "";

	public JobsPage(WebDriver driver, String description) {
		super(driver, description);
	}

	@Override
	public JobsPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getRootMenu().isAvailable() && getStatusDropDown().isAvailable() && loadEventOccured();
	}

	private CustomElement getRootMenu() {
		return new CustomElement(driver,
				By.xpath("//span[@class and preceding-sibling::span[contains(text(), 'Assets')]]"), "Root menu");
	}

	private List<WebElement> getJobCreatedOnTimeByJobTypeAndOwner(String username) {
		return getListOfWebElements(By
				.xpath("//a[parent::td[parent::tr[@class='adam-row'] and preceding-sibling::td[child::a[contains(@href, '~')]] and following-sibling::td[child::a[contains(text(), '"
						+ JOB_TYPE + "')]] and following-sibling::td[child::a[contains(text(), '" + username
						+ "')]]]]"));
	}

	private List<WebElement> getJobIdByJobTypeAndOwner(String username) {
		return getListOfWebElements(By
				.xpath("//a[contains(@href, '~') and parent::td[parent::tr[@class='adam-row'] and following-sibling::td[child::a[contains(text(), '"
						+ JOB_TYPE + "')]] and following-sibling::td[child::a[contains(text(), '" + username
						+ "')]]]]"));
	}

	private List<WebElement> nextPagesLinks() {
		return getListOfWebElements(By.xpath("//a[@class='number-button']"));
	}

	private CustomElement getStatusDropDown() {
		return new CustomElement(driver,
				By.xpath("//span[ancestor::div[@class='list-filter'] and parent::span[@class='combobox-arrow']]"),
				"Status drop-down");
	}

	private List<String> getHrefs(List<WebElement> list) {
		// System.out.println("getHrefs.size=" + list.size());
		List<String> hrefs = new ArrayList<String>();
		for (WebElement webElement : list) {
			hrefs.add(webElement.getAttribute("href"));
		}
		return hrefs;
	}

	public int getTotalTimeinSecondsStartingFrom(long timestamp, String username) {
		long earliestCreatedOnTimestamp = processPage(timestamp, username);
		int currentPage = 1;
		while (nextPagePresentAfter(currentPage) && !jobIsOutOfRange) {
			currentPage++;
			getNextPageLinkByNumber(currentPage).click();
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (previousPagePresentBefore(currentPage)) {
					break;
				}
				timePassed = timePassed + delay();
			}
			earliestCreatedOnTimestamp = processPage(timestamp, username);
		}
		if (latestJobFound && (earliestCreatedOnTimestamp != 0)) {
			// System.out.println("Latest job started on " + latestJobTimestamp
			// + " (" + new Date(latestJobTimestamp) + ")");
			// System.out.println("Earliest job created on " +
			// earliestCreatedOnTimestamp + " (" + new
			// Date(earliestCreatedOnTimestamp) + ")");
			JobPage latestJobPage = navigateToJobPageBy(latestJobHref);
			// System.out.println("latestJobPage.getJobStartedOn()=" +
			// latestJobPage.getJobStartedOn());
			// System.out.println("latestJobPage.getJobDuration()=" +
			// latestJobPage.getJobDuration() + " (" +
			// getMsFromDuration(latestJobPage.getJobDuration()) + "ms)");
			DateFormat dateFormat = new SimpleDateFormat(CREATED_ON_DATE_FORMAT);
			long latestJobStartedOnTimestamp = getTimestampByDate(latestJobPage.getJobStartedOn(), dateFormat);
			// System.out.println("TOTAL: " + (latestJobStartedOnTimestamp +
			// getMsFromDuration(latestJobPage.getJobDuration()) -
			// earliestCreatedOnTimestamp)/1000 + " seconds");
			return (int) (latestJobStartedOnTimestamp + getMsFromDuration(latestJobPage.getJobDuration())
					- earliestCreatedOnTimestamp) / 1000;

		}
		return 0;
	}

	private long getMsFromDuration(String duration) {
		String[] tokens = duration.split(":");
		String[] seconds = tokens[2].split("\\.");
		int secondsToMs = Integer.parseInt(seconds[0]) * 1000 + Integer.parseInt(seconds[1].substring(0, 3));
		int minutesToMs = Integer.parseInt(tokens[1]) * 60000;
		int hoursToMs = Integer.parseInt(tokens[0]) * 3600000;
		long total = secondsToMs + minutesToMs + hoursToMs;
		return total;
	}

	private JobPage navigateToJobPageBy(String url) {
		driver.get(url);
		return new JobPage(driver, "Job page").waitUntilAvailable();
	}

	private long processPage(long timestamp, String username) {
		DateFormat dateFormat = new SimpleDateFormat(CREATED_ON_DATE_FORMAT);
		long lastCreatedOnTimestamp = 0;
		Iterator<String> hrefsIterator = getHrefs(getJobIdByJobTypeAndOwner(username)).iterator();
		// System.out.println("getJobCreatedOnTimeByJobTypeAndOwner(username).size()="
		// + getJobCreatedOnTimeByJobTypeAndOwner(username).size());
		for (WebElement jobWeb : getJobCreatedOnTimeByJobTypeAndOwner(username)) {
			String jobHref = hrefsIterator.next();
			long createdOnTimestamp = getTimestampByDate(jobWeb.getText(), dateFormat);
			// System.out.println("createdOnTimestamp=" + createdOnTimestamp +
			// "(" + jobWeb.getText() + ")");
			if (createdOnTimestamp != 0) {
				if (createdOnTimestamp < timestamp) {
					// System.out.println("--------(" + jobWeb.getText() + ")" +
					// createdOnTimestamp + " " + timestamp);
					jobIsOutOfRange = true;
					break;
				}
				if (createdOnTimestamp >= timestamp) {
					if (!latestJobFound) {
						latestJobFound = true;
						// latestJobTimestamp = createdOnTimestamp;
						latestJobHref = jobHref;
					}
					lastCreatedOnTimestamp = createdOnTimestamp;
				}
			}
		}
		return lastCreatedOnTimestamp;
	}

	private boolean previousPagePresentBefore(int number) {
		for (WebElement link : nextPagesLinks()) {
			int nextPageNumber = Integer.parseInt(link.getText());
			if (nextPageNumber == number - 1) {
				return true;
			}
		}
		return false;
	}

	private WebElement getNextPageLinkByNumber(int number) {
		for (WebElement link : nextPagesLinks()) {
			int nextPageNumber = Integer.parseInt(link.getText());
			if (nextPageNumber == number) {
				return link;
			}
		}
		return null;
	}

	private boolean nextPagePresentAfter(int number) {
		for (WebElement link : nextPagesLinks()) {
			int nextPageNumber = Integer.parseInt(link.getText());
			if (nextPageNumber == number + 1) {
				return true;
			}
		}
		return false;
	}

	private long getTimestampByDate(String time, DateFormat dateFormat) {
		try {
			long createdOnTimestamp = dateFormat.parse(time).getTime();
			return createdOnTimestamp;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int delay() {
		try {
			Thread.sleep(DEFAULT_RETRY_DELAY);
			return DEFAULT_RETRY_DELAY;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean jobsProcessed() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Actions action;
		while (!pendingEmpty()) {
			// System.out.println("Entered CLEAR");
			action = new Actions(driver);
			action.moveToElement(getStatusDropDown().asWebElement()).click().build().perform();
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getClearMenu().isAvailable()) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (getClearMenu().isAvailable()) {
				action.moveToElement(getClearMenu().asWebElement()).click().build().perform();
				// System.out.println("CLEAR");
				/*
				 * timePassed = 0; while (timePassed < DEFAULT_TIMEOUT) { if
				 * (!searchResultsEmpty()) { break; } timePassed = timePassed +
				 * delay(); }
				 */
				try {
					Thread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		while (!executingEmpty()) {
			// System.out.println("Entered CLEAR");
			action = new Actions(driver);
			action.moveToElement(getStatusDropDown().asWebElement()).click().build().perform();
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (getClearMenu().isAvailable()) {
					break;
				}
				timePassed = timePassed + delay();
			}
			if (getClearMenu().isAvailable()) {
				action.moveToElement(getClearMenu().asWebElement()).click().build().perform();
				// System.out.println("CLEAR");
				/*
				 * timePassed = 0; while (timePassed < DEFAULT_TIMEOUT) { if
				 * (!searchResultsEmpty()) { break; } timePassed = timePassed +
				 * delay(); }
				 */
				try {
					Thread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		action = new Actions(driver);
		action.moveToElement(getStatusDropDown().asWebElement()).click().build().perform();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getClearMenu().isAvailable()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getClearMenu().isAvailable()) {
			action.moveToElement(getClearMenu().asWebElement()).click().build().perform();
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private boolean noResultsIsPresent() {
		// System.out.println("noResultsEmpty=" +
		// getNoJobsMessage().isAvailable());
		return getNoJobsMessage().isAvailable();
	}

	private boolean executingEmpty() {
		Actions action = new Actions(driver);
		action.moveToElement(getStatusDropDown().asWebElement()).click().build().perform();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getExecutingMenu().isAvailable()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getExecutingMenu().isAvailable()) {
			action.moveToElement(getExecutingMenu().asWebElement()).click().build().perform();
			/*
			 * timePassed = 0; while (timePassed < DEFAULT_TIMEOUT) { if
			 * (!searchResultsEmpty() || !noResultsEmpty()) { break; }
			 * timePassed = timePassed + delay(); }
			 */
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (noResultsIsPresent()) {
				// System.out.println("!!!!true+++++");
				return true;
			}
		}
		// System.out.println("!!!!false+++++");
		return false;
	}

	private boolean pendingEmpty() {
		// System.out.println("Entered pendingEmpty()");
		Actions action = new Actions(driver);
		action.moveToElement(getStatusDropDown().asWebElement()).click().build().perform();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (getPendingMenu().isAvailable()) {
				break;
			}
			timePassed = timePassed + delay();
		}
		if (getPendingMenu().isAvailable()) {
			action.moveToElement(getPendingMenu().asWebElement()).click().build().perform();
			/*
			 * timePassed = 0; while (timePassed < DEFAULT_TIMEOUT) { if
			 * (!searchResultsEmpty() || !noResultsEmpty()) { break; }
			 * timePassed = timePassed + delay(); }
			 */
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (noResultsIsPresent()) {
				// System.out.println("++true+++++");
				return true;
			}
		}
		// System.out.println("++false+++++");
		return false;
	}

	private CustomElement getNoJobsMessage() {
		return new CustomElement(driver, By.xpath("//p[ancestor::tr[@class='adam-no-result']]"),
				"No jobs found message");
	}

	private CustomElement getExecutingMenu() {
		return new CustomElement(driver, By.xpath("//div[contains(text(), 'Executing')]"), "Executing dropdown menu");
	}

	private CustomElement getPendingMenu() {
		return new CustomElement(driver, By.xpath("//div[contains(text(), 'Pending')]"), "Pending dropdown menu");
	}

	private CustomElement getClearMenu() {
		return new CustomElement(driver, By.xpath("//div[contains(text(), 'Clear')]"), "Clear dropdown menu");
	}

	public long getJobDuration(String username, int assetsToDownload) {
		List<WebElement> downloadJobs = getDownloadJobsFilteredByOwner(username, assetsToDownload);
		downloadJobs.get(0).click();
		JobPage jobPage = new JobPage(driver, "Download Job page");
		return getMsFromDuration(jobPage.getJobDuration());
	}

	private List<WebElement> getDownloadJobsFilteredByOwner(String username, int assetsToDownload) {
		return getListOfWebElements(By
				.xpath("//a[parent::td[parent::tr[@class='adam-row'] and preceding-sibling::td[child::a[contains(@href, '~')]] and following-sibling::td[child::a[contains(text(), 'Download of "
						+ assetsToDownload + " files')]] and following-sibling::td[child::a[contains(text(), '"
						+ username + "')]]]]"));
	}

}
