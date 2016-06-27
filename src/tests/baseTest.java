package tests;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class baseTest {

	protected WebDriver driver;

	@Before
	public void setUp() throws InterruptedException {
		driver = new FirefoxDriver();
		
		driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
		driver.get("http://localhost:8888/addressbook/addressbook/index.php");
		
		Thread.sleep(1000);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

}
