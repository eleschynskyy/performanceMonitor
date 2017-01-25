package com.mtr.dam.core.web;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

//import com.automation.training.utils.TestStepReporter;

public abstract class WebComponent<T extends WebComponent<T>> extends Component<T> {

	protected final By findByMethod;

	public WebComponent(WebDriver driver, By findByMethod, String description) {
		super(driver, description);
		this.findByMethod = findByMethod;
	}

	@Override
	public boolean isAvailable() {
		try {
			return getWebElement() != null;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public void click() {
//		long start = System.currentTimeMillis();
		getWebElement().click();
//		long end = System.currentTimeMillis();
//		TestStepReporter.reportln("click(): " + "'" + description + "': " + (end - start) + "ms");
	}

	public String getText() {
		return getWebElement().getText();
	}

	protected WebElement getWebElement() {
//		long start = System.currentTimeMillis();
		WebElement element = driver.findElement(findByMethod);
//		long end = System.currentTimeMillis();
//		TestStepReporter.reportln("getWebElement(): " + "'" + description + "': " + (end - start) + "ms");
		return element;
	}

	public boolean isElementTextEqualTo(String text) {
		return getText().equals(text);
	}
	
	public WebElement asWebElement() {
		return getWebElement();
	}

}
