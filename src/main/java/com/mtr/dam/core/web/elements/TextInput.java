package com.mtr.dam.core.web.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.mtr.dam.core.web.WebComponent;

public class TextInput extends WebComponent<TextInput> {

	public TextInput(WebDriver driver, By findByMethod, String description) {
		super(driver, findByMethod, description);
	}

	public TextInput inputText(String text) {
		getWebElement().clear();
		getWebElement().sendKeys(text);
		return this;
	}

}
