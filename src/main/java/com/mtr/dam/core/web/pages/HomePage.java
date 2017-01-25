package com.mtr.dam.core.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Button;

public class HomePage extends WebPage<HomePage> {

	private static final String PAGE_URL = HOST + "/Central/";

	public HomePage(WebDriver driver, String description) {
		super(driver, description);
//		loadAndWaitUntilAvailable();
	}

	@Override
	public HomePage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getAssetsButton().waitUntilAvailable().isAvailable() && loadEventOccured();
	}

	private Button getAssetsButton() {
		return new Button(driver, By.xpath("//a[@href='/Assets/']"), "Assets button");
	}

	public AssetsPage navigateToAssetsPage() {
		getAssetsButton().click();
		return new AssetsPage(driver, "Assets page").waitUntilAvailable();
	}

}
