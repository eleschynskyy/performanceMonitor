package com.mtr.dam.core.web;

//import java.util.concurrent.TimeUnit;
//import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;

//import com.automation.training.utils.TestStepReporter;
//import org.openqa.selenium.support.ui.ExpectedCondition;
//import org.openqa.selenium.support.ui.FluentWait;
//import org.openqa.selenium.support.ui.Wait;

public class Waiter<T extends Component<T>> {

	private static final int DEFAULT_TIMEOUT = 30000;
	private static final int DEFAULT_RETRY_DELAY = 500;
	private T component;
//	private WebDriver driver;

	public Waiter<T> forComponent(T component/*, WebDriver driver*/) {
		this.component = component;
//		this.driver = driver;
		return this;
	}

	public T toBeAvailable() {
//		long startSearching = System.currentTimeMillis();
		int timePassed = 0;
		while (timePassed < DEFAULT_TIMEOUT) {
			if (this.component.isAvailable()) {
//				long endSearching = System.currentTimeMillis();
//				TestStepReporter.reportln("Found " + this.component.getDescription() + " after " + (endSearching - startSearching) + "ms");
				return this.component;
			}
			timePassed = timePassed + delay();
		}
		if (!this.component.isAvailable()) {
//			TestStepReporter.reportln("Timed out after " + DEFAULT_TIMEOUT + "ms of waiting for " + this.component.getDescription() + " to be available.");
			throw new TimeoutException("Timed out after " + DEFAULT_TIMEOUT
					+ "ms of waiting for "
					+ this.component.getDescription()
					+ " to be available.");
		}
		return this.component;
		/*
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver expectationDriver) {
				JavascriptExecutor js = (JavascriptExecutor) expectationDriver;
				return js.executeScript("return document.readyState").equals("complete");
			}
		};
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(DEFAULT_TIMEOUT_TO_FULLY_LOAD, TimeUnit.MILLISECONDS)
				.pollingEvery(DEFAULT_RETRY_DELAY_TO_FULLY_LOAD, TimeUnit.MILLISECONDS);
		try {
			wait.until(expectation);
			return this.component;
		} catch (Throwable error) {
			Reporter.log("Timeout waiting page to Load because of: "
					+ error.getMessage());
		}
		return this.component;
		*/
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
