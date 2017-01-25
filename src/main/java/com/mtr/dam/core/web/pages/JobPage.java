package com.mtr.dam.core.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Text;

public class JobPage extends WebPage<JobPage> {

	private static final String PAGE_URL = HOST;

	public JobPage(WebDriver driver, String description) {
		super(driver, description);
		// loadAndWaitUntilAvailable();
	}

	@Override
	public JobPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getStartedOn().isAvailable() && getDuration().isAvailable() && loadEventOccured();
	}

	public String getJobDuration() {
		Text durationLabel = new Text(driver,
				By.xpath(
						"//td[@class='job-data' and preceding-sibling::td[@class='job-label' and contains(text(), 'Duration')]]"),
				"Duration label");
		return durationLabel.getText();
	}

	public String getJobStartedOn() {
		Text startedOnLabel = new Text(driver,
				By.xpath(
						"//td[@class='job-data' and preceding-sibling::td[@class='job-label' and contains(text(), 'Started On')]]"),
				"Started On label");
		return startedOnLabel.getText();
	}

	private Text getDuration() {
		return new Text(driver,
				By.xpath(
						"//td[@class='job-data' and preceding-sibling::td[@class='job-label' and contains(text(), 'Duration')]]"),
				"Duration label");
	}

	private Text getStartedOn() {
		return new Text(driver,
				By.xpath(
						"//td[@class='job-data' and preceding-sibling::td[@class='job-label' and contains(text(), 'Started On')]]"),
				"Started On label");
	}

}
