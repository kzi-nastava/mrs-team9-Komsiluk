package rs.ac.uns.ftn.testing.Komsiluk.s3.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainGuestPage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(css="button.app-navbar__avatar-btn")
    private WebElement avatarButtonElement;

    @FindBy(css="[href='/login']")
    private WebElement loginButtonElement;

    public MainGuestPage(WebDriver driver, WebDriverWait wait)
    {
        this.driver=driver;
        this.wait=wait;

        PageFactory.initElements(driver, this);
    }

    public void isLoaded()
    {
        wait.until(ExpectedConditions.visibilityOf(avatarButtonElement));
    }

    public void clickAvatarButton()
    {
        avatarButtonElement.click();
    }

    public LoginPage clickLoginButton()
    {
        wait.until(ExpectedConditions.elementToBeClickable(loginButtonElement));
        loginButtonElement.click();
        return new LoginPage(driver, wait);
    }
}