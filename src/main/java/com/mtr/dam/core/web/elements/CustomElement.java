package com.mtr.dam.core.web.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.mtr.dam.core.web.WebComponent;

public class CustomElement extends WebComponent<CustomElement> {

	public CustomElement(WebDriver driver, By findByMethod, String description) {
		super(driver, findByMethod, description);
	}
	
//	public WebElement returnAsWebElement() {
//		return this.getWebElement();
//	}

}
