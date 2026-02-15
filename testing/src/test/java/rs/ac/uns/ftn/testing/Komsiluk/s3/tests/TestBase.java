package rs.ac.uns.ftn.testing.Komsiluk.s3.tests;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;
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

        ChromeOptions options=new ChromeOptions();
        options.addArguments("--incognito");
        driver=new ChromeDriver(options);
        driver.manage().window().maximize();
        wait=new WebDriverWait(driver, 10);

        driver.get("http://localhost:4200");
    }

    @AfterAll
    void tearDown()
    {
        if (driver != null) {
            driver.quit();
        }
    }
}
