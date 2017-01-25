package com.mtr.dam.core.web;

import org.openqa.selenium.WebDriver;

public abstract class Component<T extends Component<T>> {

	protected WebDriver driver;
	protected final String description;

	public Component(WebDriver driver, String description) {
		this.driver = driver;
		this.description = description;
	}
	
	public abstract boolean isAvailable();

	public T waitUntilAvailable() {
		return new Waiter<T>().forComponent((T) this).toBeAvailable();
	}

	public String getDescription() {
		return description;
	}

}
