package com.mtr.dam.core.web;

import static com.mtr.dam.core.Configuration.getConfig;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.mtr.dam.core.Configuration;
import com.mtr.dam.core.Environment;
import com.mtr.dam.utils.TestStepReporter;

public abstract class WebPage<T extends WebPage<T>> extends Component<T> {

	private static final Configuration CONFIG = getConfig();
	private static final Environment ENVIRONMENT = CONFIG.getEnvironmentSettings();
	protected static final String HOST = ENVIRONMENT.protocol + "://" + ENVIRONMENT.host;
	private static final int DEFAULT_TIMEOUT = 30000;
	private static final int DEFAULT_RETRY_DELAY = 500;

	public WebPage(WebDriver driver, String description) {
		super(driver, description);
	}

	public abstract T load();

	public boolean loadEventOccured() {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String ptloadEventEnd = String.valueOf(js.executeScript("return performance.timing.loadEventEnd;"));
			int timePassed = 0;
			while (timePassed < DEFAULT_TIMEOUT) {
				if (!ptloadEventEnd.equals("0")) {
					String jsValue = String.valueOf(js
							.executeScript("return performance.timing.loadEventEnd - performance.timing.fetchStart;"));
//					System.out.println(getDescription() + ": " + jsValue + "ms");
					TestStepReporter.reportln(">" + getDescription() + " loaded: " + jsValue + "ms");
					return true;
				}
				timePassed = timePassed + delay();
			}
			if (ptloadEventEnd.equals("0")) {
				throw new TimeoutException("Timed out after " + DEFAULT_TIMEOUT + "ms of waiting for loadEventEnd");
			}
		} else {
			throw new IllegalStateException("This driver does not support JavaScript!");
		}
		return false;
	}

	public T loadAndWaitUntilAvailable() {
		load();
		return waitUntilAvailable();
	}

	protected List<WebElement> getListOfWebElements(By findByMethod) {
		return driver.findElements(findByMethod);
	}

	protected WebElement getWebElement(By findByMethod) {
		return driver.findElement(findByMethod);
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
