package rs.ac.uns.ftn.testing.Komsiluk.s1.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import rs.ac.uns.ftn.testing.Komsiluk.s1.util.FavoriteRoute;

public class MainPassengerPage {
	
	private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(css="button[aria-label='Menu']")
	private WebElement menuButtonElement;
    
    @FindBy(id="favButton")
    private WebElement favButtonElement;
    
    @FindBy(css="div.fav-card")
	private WebElement favCardElement;
    
    @FindBy(css="div.fav-title")
	private WebElement favCardTitleElement;
    
    @FindBy(css=".fav-row .text-semibold")
    private List<WebElement> favCardPreferenceElements;
    
    @FindBy(css="app-favorite-details-dialog .fd-title")
	private WebElement favCardDetailsTitleElement;
    
    @FindBy(css="app-favorite-details-dialog span.text-regular")
    private List<WebElement> favCardDetailsPreferenceElements;
    
    @FindBy(css="app-favorite-details-dialog div.text-regular.fd-accent")
    private List<WebElement> favCardDetailsListsElements;
    
    @FindBy(css="app-favorite-details-dialog button.btn--primary")
    private WebElement bookButtonElement;
    
    @FindBy(css="app-passenger-book-ride-panel .br-input")
    private List<WebElement> bookRidePanelInputElements;
    
    @FindBy(css=".br-check input")
    private List<WebElement> bookRidePanelCheckboxElements;
    
    @FindBy(css="button.fd-rename")
    private WebElement renameFavButtonElement;
    
    @FindBy(css="input.rf-input")
    private WebElement renameFavInputElement;
    
    @FindBy(css="app-rename-favorite-dialog .btn--primary")
    private WebElement renameFavConfirmButtonElement;
    
    @FindBy(css="app-favorite-details-dialog .btn--ghost:nth-child(2)")
    private WebElement favDeleteButtonElement;
    
    @FindBy(css="app-delete-favorite-dialog .df-title")
    private WebElement deleteFavDialogTitleElement;
    
    @FindBy(css="app-delete-favorite-dialog .btn--primary")
    private WebElement deleteConfirmButton;
    
    @FindBy(css="button.app-navbar__avatar-btn")
	private WebElement avatarButtonElement;
    
    @FindBy(css="a[href='/passenger-history']")
    private WebElement historyButtonElement;
    
    @FindBy(css=".fav-card")
    private List<WebElement> favCardElements;
	
	public MainPassengerPage(WebDriver driver, WebDriverWait wait)
	{
		this.driver=driver;
		this.wait=wait;
		
		PageFactory.initElements(driver, this);
	}
	
	public void isLoaded()
	{
		wait.until(ExpectedConditions.visibilityOf(menuButtonElement));
	}
	
	public void clickMenuButton()
	{
		menuButtonElement.click();
	}
	
	public void clickFavButton()
	{
		wait.until(ExpectedConditions.elementToBeClickable(favButtonElement));
		favButtonElement.click();
	}
	
	public FavoriteRoute clickFavCard()
	{
		wait.until(ExpectedConditions.elementToBeClickable(favCardElement));
		
		String title = favCardTitleElement.getText();
		String pickupLocation = favCardPreferenceElements.get(0).getText();
		String numberOfStations = favCardPreferenceElements.get(1).getText();
		String numberOfPassengers = favCardPreferenceElements.get(2).getText();
		String destination = favCardPreferenceElements.get(3).getText();
		String vehicleType = favCardPreferenceElements.get(4).getText();
		String petFriendly = favCardPreferenceElements.get(5).getText();
		String babyFriendly = favCardPreferenceElements.get(6).getText();
		
		FavoriteRoute favoriteRoute = new FavoriteRoute(title, pickupLocation, Integer.parseInt(numberOfStations), Integer.parseInt(numberOfPassengers), destination, vehicleType, petFriendly.equals("Yes"), babyFriendly.equals("Yes"), null, null);
		
		favCardElement.click();
		
		return favoriteRoute;
	}
	
	public boolean isFavCardDetailsDialogDisplayed()
	{
		try {
			wait.until(ExpectedConditions.visibilityOf(favCardDetailsTitleElement));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isFavCardDetailsDialogContentCorrect(FavoriteRoute favoriteRoute)
	{
		String title = favCardDetailsTitleElement.getText();
		String pickupLocation = favCardDetailsPreferenceElements.get(0).getText();
		String numberOfStations = favCardDetailsPreferenceElements.get(1).getText();
		String numberOfPassengers = favCardDetailsPreferenceElements.get(2).getText();
		String destination = favCardDetailsPreferenceElements.get(3).getText();
		String vehicleType = favCardDetailsPreferenceElements.get(4).getText();
		String petFriendly = favCardDetailsPreferenceElements.get(5).getText();
		String babyFriendly = favCardDetailsPreferenceElements.get(6).getText();
		
		return title.equals(favoriteRoute.getTitle()) &&
				pickupLocation.equals(favoriteRoute.getPickupLocation()) &&
				numberOfStations.equals(String.valueOf(favoriteRoute.getNumberOfStations())) &&
				numberOfPassengers.equals(String.valueOf(favoriteRoute.getNumberOfPassengers())) &&
				destination.equals(favoriteRoute.getDestination()) &&
				vehicleType.equals(favoriteRoute.getVehicleType()) &&
				petFriendly.equals(favoriteRoute.isPetFriendly() ? "Yes" : "No") &&
				babyFriendly.equals(favoriteRoute.isBabyFriendly() ? "Yes" : "No");
	}
	
	public FavoriteRoute setFavoriteRouteLists(FavoriteRoute favoriteRoute)
	{
		List<String> stations;
		if(favoriteRoute.getNumberOfStations() > 0) {
			stations = new ArrayList<>();
			for(int i=0; i<favoriteRoute.getNumberOfStations(); i++) {
				stations.add(favCardDetailsListsElements.get(i).getText());
			}
		} else {
			stations = null;
		}
		List<String> passengers;
		if(favoriteRoute.getNumberOfPassengers() > 0) {
			passengers = new ArrayList<>();
			for(int i=favoriteRoute.getNumberOfStations(); i<favoriteRoute.getNumberOfStations() + favoriteRoute.getNumberOfPassengers(); i++) {
				passengers.add(favCardDetailsListsElements.get(i).getText());
			}
		} else {
			passengers = null;
		}
		
		favoriteRoute.setStations(stations);
		favoriteRoute.setPassengers(passengers);
		
		return favoriteRoute;
	}
	
	public void clickBookButton()
	{
		bookButtonElement.click();
	}
	
	public boolean isBookRidePanelDisplayed()
	{
		try {
			wait.until(ExpectedConditions.visibilityOf(bookRidePanelInputElements.get(0)));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isBookRidePanelContentCorrect(FavoriteRoute favoriteRoute)
	{
		if(bookRidePanelInputElements.size() != favoriteRoute.getNumberOfStations() + favoriteRoute.getNumberOfPassengers() + 4) {
			return false;
		}
		
		String pickupLocation = bookRidePanelInputElements.get(0).getAttribute("value");
		List<String> stations = new ArrayList<>();
		for(int i=1; i<=favoriteRoute.getNumberOfStations(); i++) {
			stations.add(bookRidePanelInputElements.get(i).getAttribute("value"));
		}
		String destination = bookRidePanelInputElements.get(favoriteRoute.getNumberOfStations()+1).getAttribute("value");
		List<String> passengers = new ArrayList<>();
		for(int i=favoriteRoute.getNumberOfStations()+2; i<bookRidePanelInputElements.size()-2; i++) {
			passengers.add(bookRidePanelInputElements.get(i).getAttribute("value"));
		}
		String vehicleType = bookRidePanelInputElements.get(bookRidePanelInputElements.size()-2).getAttribute("value");
		
		boolean petFriendly = bookRidePanelCheckboxElements.get(0).isSelected();
		boolean babyFriendly = bookRidePanelCheckboxElements.get(1).isSelected();
		
		if(favoriteRoute.getNumberOfStations() > 0) {
			for(int i=0; i<favoriteRoute.getNumberOfStations(); i++) {
				if(!stations.get(i).equals(favoriteRoute.getStations().get(i))) {
					return false;
				}
			}
		}
		
		if(favoriteRoute.getNumberOfPassengers() > 0) {
			for(int i=0; i<favoriteRoute.getNumberOfPassengers(); i++) {
				if(!passengers.get(i).equals(favoriteRoute.getPassengers().get(i))) {
					return false;
				}
			}
		}
		
		return pickupLocation.equals(favoriteRoute.getPickupLocation()) &&
				destination.equals(favoriteRoute.getDestination()) &&
				vehicleType.equals(favoriteRoute.getVehicleType()) &&
				petFriendly == favoriteRoute.isPetFriendly() &&
				babyFriendly == favoriteRoute.isBabyFriendly();
	}
	
	public void clickRenameFavButton()
	{
		renameFavButtonElement.click();
	}
	
	public boolean isRenameFavoriteRouteDialogDisplayed()
	{
		try {
			wait.until(ExpectedConditions.visibilityOf(renameFavInputElement));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isRenameFavoriteRouteInputPrefilledWithCurrentName(FavoriteRoute favoriteRoute)
	{
		String currentName = renameFavInputElement.getAttribute("value");
		return currentName.equals(favoriteRoute.getTitle());
	}
	
	public void insertNewNameInRenameFavoriteRouteInput(String newName)
	{
		renameFavInputElement.clear();
		renameFavInputElement.sendKeys(newName);
	}
	
	public void clickRenameFavConfirmButton()
	{
		renameFavConfirmButtonElement.click();
	}
	
	public boolean isFavoriteRouteNameChangedInFavCard(String newName)
	{
	    try {
	        By newTitleBy = By.xpath("//div[contains(@class,'fav-title') and normalize-space()='" + newName + "']");
	        wait.until(ExpectedConditions.visibilityOfElementLocated(newTitleBy));
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public boolean isRenameFavConfirmButtonEnabled()
	{
		return renameFavConfirmButtonElement.isEnabled();
	}
	
	public void clickFavDeleteButton()
	{
		favDeleteButtonElement.click();
	}
	
	public boolean isDeleteFavoriteRouteDialogDisplayed()
	{
		try {
			wait.until(ExpectedConditions.visibilityOf(deleteFavDialogTitleElement));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isDeleteFavoriteRouteTitlePrefilledWithCurrentName(FavoriteRoute favoriteRoute)
	{
		String title = deleteFavDialogTitleElement.getText();
		return title.equals("Delete " + favoriteRoute.getTitle());
	}
	
	public void clickDeleteFavConfirmButton()
	{
		deleteConfirmButton.click();
	}
	
	public boolean isFavoriteRouteRemovedFromFavCards(FavoriteRoute favoriteRoute) {
	    try {
	        wait.until(ExpectedConditions.invisibilityOfElementWithText(By.cssSelector("div.fav-card div.fav-title"), favoriteRoute.getTitle()));
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public void clickAvatarButton()
	{
		avatarButtonElement.click();
	}

	public PassengerHistoryPage clickHistoryButton()
	{
		wait.until(ExpectedConditions.elementToBeClickable(historyButtonElement));
		historyButtonElement.click();
		return new PassengerHistoryPage(driver, wait);
	}
	
	public void clickLastFavCard()
	{
		WebElement lastFavCardElement = favCardElements.get(favCardElements.size() - 1);
		wait.until(ExpectedConditions.elementToBeClickable(lastFavCardElement));
		
		lastFavCardElement.click();
	}
	
	public boolean isFavoriteDetailsCorrectFromHistory(FavoriteRoute favoriteRoute)
	{
		String title = favCardDetailsTitleElement.getText();
		String pickupLocation = favCardDetailsPreferenceElements.get(0).getText();
		String numberOfStations = favCardDetailsPreferenceElements.get(1).getText();
		String destination = favCardDetailsPreferenceElements.get(3).getText();
		
		FavoriteRoute favoriteRouteFromHistory = new FavoriteRoute(title, pickupLocation, Integer.parseInt(numberOfStations), -1, destination, null, false, false, null, null);
		favoriteRouteFromHistory = setFavoriteRouteLists(favoriteRouteFromHistory);
		
		for(int i=0; i<favoriteRoute.getNumberOfStations(); i++) {
			if(!favoriteRouteFromHistory.getStations().get(i).equals(favoriteRoute.getStations().get(i))) {
				return false;
			}
		}
		
		return title.equals(favoriteRoute.getTitle()) &&
				pickupLocation.contains(favoriteRoute.getPickupLocation()) &&
				numberOfStations.equals(String.valueOf(favoriteRoute.getNumberOfStations())) &&
				destination.contains(favoriteRoute.getDestination());
	}
	
}
