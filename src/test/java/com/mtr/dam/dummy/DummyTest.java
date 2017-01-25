package com.mtr.dam.dummy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class DummyTest {

	@Test
	public void dummyTest() throws InterruptedException {
//		System.setProperty("webdriver.gecko.driver", "src/main/resources/drivers/geckodriver.exe");
//		WebDriver driver = new FirefoxDriver();
		System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		System.setProperty("webdriver.ie.driver", "src/main/resources/drivers/IEDriverServer.exe");
//		DesiredCapabilities cap = new DesiredCapabilities();
//		cap.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
//		WebDriver driver = new InternetExplorerDriver(cap);
//		driver.get("http://www.google.com/ncr");
		driver.get("https://www.yahoo.com/");
		Thread.sleep(2000);
		String sTitle = driver.getTitle();
		System.out.println(sTitle);
		driver.close();
	}

}
