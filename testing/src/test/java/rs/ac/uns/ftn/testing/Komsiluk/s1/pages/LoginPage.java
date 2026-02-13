package rs.ac.uns.ftn.testing.Komsiluk.s1.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
	
	private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(css=".auth-input[type='email']")
	private WebElement usernameInputElement;
	
    @FindBy(css=".auth-input[type='password']")
	private WebElement passwordInputElement;
	
    @FindBy(css="button[form]")
	private WebElement loginButtonElement;
	
	public LoginPage(WebDriver driver, WebDriverWait wait)
	{
		this.driver=driver;
		this.wait=wait;
		
		PageFactory.initElements(driver, this);
	}
	
	public void isLoaded()
	{
		wait.until(ExpectedConditions.visibilityOf(loginButtonElement));
	}
	
	public void insertUsername(String username)
	{
		usernameInputElement.clear();
		usernameInputElement.sendKeys(username);
	}
	
	public void insertPassword(String password)
	{
		passwordInputElement.clear();
		passwordInputElement.sendKeys(password);
	}
	
	public MainPassengerPage loginPassenger()
	{
		loginButtonElement.click();
		return new MainPassengerPage(driver, wait);
	}
}