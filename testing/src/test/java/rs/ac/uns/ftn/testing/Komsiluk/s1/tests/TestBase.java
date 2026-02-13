package rs.ac.uns.ftn.testing.Komsiluk.s1.tests;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBase {
	
	protected WebDriver driver;
	protected WebDriverWait wait;
	
	@BeforeAll
	void beforeAll()
	{
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
	}

	@BeforeEach
	void setUp()
	{
		ChromeOptions options=new ChromeOptions();
		options.addArguments("--incognito");
		driver=new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		wait=new WebDriverWait(driver, 10);
		
		driver.get("http://localhost:4200");
	}
	
	@AfterEach
	void tearDown()
	{
		driver.quit();
	}
}
