package rs.ac.uns.ftn.testing.Komsiluk.s3.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainAdminPage {

    WebDriver driver;

    WebDriverWait wait;

    @FindBy(xpath="//button[@aria-label='Admin menu']")
    WebElement adminToolsKebab;

    @FindBy(xpath="//a[contains(@href, '/admin/ride-history')]")
    WebElement rideHistoryMenuItem;

    public MainAdminPage(WebDriver driver, WebDriverWait wait)
    {
        this.driver=driver;
        this.wait=wait;

        PageFactory.initElements(driver, this);
    }

    public void isLoaded()
    {
        wait.until(ExpectedConditions.visibilityOf(adminToolsKebab));
    }

    public RideHistoryPage openRideHistoryPage() {
        adminToolsKebab.click();
        wait.until(ExpectedConditions.visibilityOf(rideHistoryMenuItem));
        rideHistoryMenuItem.click();
        return new RideHistoryPage(driver, wait);
    }
}
