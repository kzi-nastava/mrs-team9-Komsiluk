package rs.ac.uns.ftn.testing.Komsiluk.s2.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "input[formcontrolname='email']")
    private WebElement emailInput;

    @FindBy(css = "input[formcontrolname='password']")
    private WebElement passwordInput;

    @FindBy(css = "button[form='loginForm']")
    private WebElement loginSubmitButton;


    public void login(String email, String password) {

        wait.until(ExpectedConditions.visibilityOf(emailInput));
        emailInput.clear();
        emailInput.sendKeys(email);

        passwordInput.clear();
        passwordInput.sendKeys(password);

        wait.until(ExpectedConditions.elementToBeClickable(loginSubmitButton)).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/login")
        ));
    }

}
