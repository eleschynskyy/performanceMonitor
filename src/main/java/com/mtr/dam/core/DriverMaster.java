package com.mtr.dam.core;

import static org.openqa.selenium.Platform.LINUX;
import static org.openqa.selenium.Platform.WINDOWS;

import java.net.MalformedURLException;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.mtr.dam.utils.ConfigProperties;

public class DriverMaster {

	private static HashMap<Long, WebDriver> driverMap = new HashMap<Long, WebDriver>();
	private static String driverLocation = ConfigProperties.getSystemProperties("driver.location");

	/*
	 * private static String hubURL = ConfigProperties
	 * .getSystemProperties("hub.protocol") + "://" +
	 * ConfigProperties.getSystemProperties("hub.ip") + ":" +
	 * ConfigProperties.getSystemProperties("hub.port") + "/wd/hub";
	 */

	private DriverMaster() {
	};

	public static WebDriver startDriverInstance(String platformKey, String driverKey, String version)
			throws MalformedURLException {
		BrowserType browser = BrowserType.get(driverKey);
		PlatformType platform = PlatformType.get(platformKey);
		WebDriver driver;
		DesiredCapabilities capabilitiesRC = new DesiredCapabilities();
		// capabilitiesRC.setVersion(version);
		switch (platform) {
		case WIN:
			capabilitiesRC.setPlatform(WINDOWS);
			break;
		case LINUX:
			capabilitiesRC.setPlatform(LINUX);
			break;
		default:
			capabilitiesRC.setPlatform(WINDOWS);
			break;
		}
		setExternalDriver(browser);

		switch (browser) {
			case FIREFOX:
				driver = new FirefoxDriver();
				break;
			case CHROME:
				DesiredCapabilities capabilities = DesiredCapabilities.chrome();
				ChromeOptions options = new ChromeOptions();
				options.addArguments("test-type");
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				driver = new ChromeDriver(capabilities);
				break;
			case IE:
				driver = new InternetExplorerDriver();
				break;
			default:
				driver = new FirefoxDriver();
				break;
			}
		// driver = new RemoteWebDriver(new URL(hubURL), capabilitiesRC);
		driver.manage().window().maximize();
		driverMap.put(Thread.currentThread().getId(), driver);
		return driver;
	}

	public static WebDriver getDriverInstance() {
		WebDriver driver = driverMap.get(Thread.currentThread().getId());
		return driver;
	}

	public static void stopDriverInstance() {
		WebDriver driver = driverMap.get(Thread.currentThread().getId());
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}

	private static void setExternalDriver(BrowserType browser) {
		String os = System.getProperty("os.name").toLowerCase().substring(0, 3);
		String externalDriver;
		switch (browser) {
		case FIREFOX:
			externalDriver = driverLocation + "geckodriver" + (os.equals("win") ? ".exe" : "");
			System.setProperty("webdriver.gecko.driver", externalDriver);
			break;
		case CHROME:
			externalDriver = driverLocation + "chromedriver" + (os.equals("win") ? ".exe" : "");
			System.setProperty("webdriver.chrome.driver", externalDriver);
			break;
		case IE:
			externalDriver = driverLocation + "IEDriverServer" + (os.equals("win") ? ".exe" : "");
			System.setProperty("webdriver.ie.driver", externalDriver);
			break;
		default:
			externalDriver = driverLocation + "chromedriver" + (os.equals("win") ? ".exe" : "");
			System.setProperty("webdriver.chrome.driver", externalDriver);
			break;
		}
	}

}
