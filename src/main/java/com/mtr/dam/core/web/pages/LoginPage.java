package com.mtr.dam.core.web.pages;

import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import com.mtr.dam.core.web.WebPage;
import com.mtr.dam.core.web.elements.Button;
import com.mtr.dam.core.web.elements.TextInput;
import com.mtr.dam.data.objects.DataPackage;
import com.mtr.dam.data.objects.User;
import com.mtr.dam.data.objects.UserAndAssetsToDownload;

public class LoginPage extends WebPage<LoginPage> {

	private static final String PAGE_URL = HOST + "/Central/Login";

	public LoginPage(WebDriver driver, String description) {
		super(driver, description);
		loadAndWaitUntilAvailable();
	}

	@Override
	public LoginPage load() {
		driver.get(PAGE_URL);
		return this;
	}

	@Override
	public boolean isAvailable() {
		return getUsernameInput().waitUntilAvailable().isAvailable()
				&& getPasswordInput().waitUntilAvailable().isAvailable()
				&& getLoginButton().waitUntilAvailable().isAvailable() && loadEventOccured();
	}

	public HomePage loginAs(DataPackage dataPackage) {
		fillFormAndClick(dataPackage);
		return new HomePage(driver, "Home page").waitUntilAvailable();
	}
	
	public HomePage loginAs(User user) {
		fillFormAndClick(user);
		return new HomePage(driver, "Home page").waitUntilAvailable();
	}
	
	public HomePage loginAs(UserAndAssetsToDownload userAndAssetsToDownload) {
		fillFormAndClick(userAndAssetsToDownload);
		return new HomePage(driver, "Home page").waitUntilAvailable();
	}

	private TextInput getUsernameInput() {
		return new TextInput(driver, By.id("AdamLogin_UserName"), "User name");
	}

	private TextInput getPasswordInput() {
		return new TextInput(driver, By.id("AdamLogin_Password"), "Password input");
	}

	private Button getLoginButton() {
		return new Button(driver, By.id("AdamLogin_Login"), "Log in button");
	}

	private void fillFormAndClick(DataPackage dataPackage) {
		getUsernameInput().inputText(dataPackage.getUsername());
		getPasswordInput().inputText(dataPackage.getPassword());
		getLoginButton().click();
	}
	
	private void fillFormAndClick(User user) {
		getUsernameInput().inputText(user.getUsername());
		getPasswordInput().inputText(user.getPassword());
		getLoginButton().click();
	}
	
	private void fillFormAndClick(UserAndAssetsToDownload userAndAssetsToDownload) {
		getUsernameInput().inputText(userAndAssetsToDownload.getUsername());
		getPasswordInput().inputText(userAndAssetsToDownload.getPassword());
		getLoginButton().click();
	}

}
