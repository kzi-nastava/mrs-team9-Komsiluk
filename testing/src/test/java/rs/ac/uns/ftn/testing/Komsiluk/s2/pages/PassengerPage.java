package rs.ac.uns.ftn.testing.Komsiluk.s2.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class PassengerPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public PassengerPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "a[routerlink='/passenger-history']")
    private WebElement rideHistoryMenuItem;

    @FindBy(css = "tr.ride-row")
    private List<WebElement> rideRows;


    @FindBy(css = "div.modal-content button.btn.btn--primary")
    private WebElement leaveRatingButton;

    @FindBy(css = "div.modal-content")
    private WebElement rideDetailsModal;

    @FindBy(css = "div.rating-dialog")
    private WebElement ratingDialog;

    @FindBy(css = "div.rating-section:nth-of-type(1) .star")
    private java.util.List<WebElement> driverStars;

    @FindBy(css = "div.rating-section:nth-of-type(2) .star")
    private java.util.List<WebElement> vehicleStars;

    @FindBy(css = "textarea")
    private WebElement commentTextArea;

    @FindBy(css = "button.btn-submit")
    private WebElement submitRatingButton;

    private By toastMessage = By.cssSelector("app-toast .toast");

    private static final String RIDE_HISTORY_URL = "/passenger-history";

    public void openRideHistory() {
        wait.until(ExpectedConditions.elementToBeClickable(rideHistoryMenuItem)).click();
        wait.until(ExpectedConditions.urlContains(RIDE_HISTORY_URL));
    }

    public void clickFirstRideRow(int row) {

        wait.until(ExpectedConditions.visibilityOfAllElements(rideRows));

        if (rideRows.isEmpty()) {
            throw new RuntimeException("Nema vožnji u istoriji!");
        }

        wait.until(ExpectedConditions.elementToBeClickable(rideRows.get(row))).click();
    }

    public void clickLeaveRating() {

        wait.until(ExpectedConditions.visibilityOf(rideDetailsModal));

        wait.until(ExpectedConditions.elementToBeClickable(leaveRatingButton)).click();
    }


    public boolean isDetailsModalOpened() {
        return wait.until(ExpectedConditions.visibilityOf(rideDetailsModal)).isDisplayed();
    }

    public void leaveRating(int driverRate, int vehicleRate, String comment) {

        wait.until(ExpectedConditions.visibilityOf(ratingDialog));

        wait.until(ExpectedConditions.elementToBeClickable(driverStars.get(driverRate - 1))).click();

        wait.until(ExpectedConditions.elementToBeClickable(vehicleStars.get(vehicleRate - 1))).click();

        wait.until(ExpectedConditions.visibilityOf(commentTextArea));
        commentTextArea.clear();
        commentTextArea.sendKeys(comment);

        wait.until(ExpectedConditions.elementToBeClickable(submitRatingButton)).click();
    }

    public boolean isToastWithTextVisible(String expectedText) {
        WebElement toast = wait.until(
                ExpectedConditions.presenceOfElementLocated(toastMessage)
        );
        return toast.getText().contains(expectedText);
    }
    public void waitForToastToDisappear() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(toastMessage));
    }



}
