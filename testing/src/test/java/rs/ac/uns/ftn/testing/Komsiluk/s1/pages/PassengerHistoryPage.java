package rs.ac.uns.ftn.testing.Komsiluk.s1.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import rs.ac.uns.ftn.testing.Komsiluk.s1.util.FavoriteRoute;

public class PassengerHistoryPage {

	private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(css="h1.history-page__title")
	private WebElement titleElement;
    
    @FindBy(css="tr.ride-row")
	private WebElement rideRowElement;
    
    @FindBy(css=".detail-value")
    private List<WebElement> rideDetailValueElements;
    
    @FindBy(css="button.btn--secondary")
    private WebElement saveAsFavoriteButtonElement;
    
    @FindBy(css="app-add-favorite-dialog input")
    private WebElement addFavoriteDialogInputElement;
    
    @FindBy(css="app-add-favorite-dialog .btn--primary")
    private WebElement addFavoriteDialogConfirmButtonElement;
	
	public PassengerHistoryPage(WebDriver driver, WebDriverWait wait)
	{
		this.driver=driver;
		this.wait=wait;
		
		PageFactory.initElements(driver, this);
	}
	
	public void isLoaded()
	{
		wait.until(ExpectedConditions.visibilityOf(titleElement));
	}
	
	public void clickRideRow() {

	    wait.until(ExpectedConditions.elementToBeClickable(rideRowElement));
	    rideRowElement.click();
	}
	
	public FavoriteRoute clickSaveAsFavoriteButton()
	{
		wait.until(ExpectedConditions.elementToBeClickable(saveAsFavoriteButtonElement));
		
		String pickupLocation=rideDetailValueElements.get(0).getText();
		int numberOfStations=rideDetailValueElements.size()-5;
		List<String> stations=new ArrayList<>();
		for(int i=1;i<=numberOfStations;i++)
		{
			stations.add(rideDetailValueElements.get(i).getText());
		}
		String destination=rideDetailValueElements.get(numberOfStations+1).getText();
		
		FavoriteRoute favoriteRoute=new FavoriteRoute(null, pickupLocation, numberOfStations, -1, destination, null, false, false, stations, null);
		
		saveAsFavoriteButtonElement.click();
		
		return favoriteRoute;
	}
	
	public boolean isAddFavoriteDialogDisplayed()
	{
		try {
			wait.until(ExpectedConditions.visibilityOf(addFavoriteDialogInputElement));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void insertFavoriteRouteName(String name)
	{
		addFavoriteDialogInputElement.clear();
		addFavoriteDialogInputElement.sendKeys(name);
	}
	
	public void clickAddFavoriteDialogConfirmButton()
	{
		wait.until(ExpectedConditions.elementToBeClickable(addFavoriteDialogConfirmButtonElement));
		addFavoriteDialogConfirmButtonElement.click();
	}
	
	public MainPassengerPage clickBackButton()
	{
		driver.navigate().back();
		return new MainPassengerPage(driver, wait);
	}
}
