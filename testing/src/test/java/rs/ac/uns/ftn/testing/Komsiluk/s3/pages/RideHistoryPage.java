package rs.ac.uns.ftn.testing.Komsiluk.s3.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import rs.ac.uns.ftn.testing.Komsiluk.s3.models.Ride;
import rs.ac.uns.ftn.testing.Komsiluk.s3.util.SortCriteria;
import rs.ac.uns.ftn.testing.Komsiluk.s3.util.SortDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RideHistoryPage {

    WebDriver driver;

    WebDriverWait wait;

    int initialRideCount = -1;


    @FindBy(xpath="//input[@id='search-input']")
    WebElement emailInput;

    @FindBy(xpath="//button[@id='search-button']")
    WebElement searchButton;

    @FindBy(css=".search-info")
    WebElement searchInfo;

    @FindBy(css=".ride-row")
    List<WebElement> rideRows;

    @FindBy(id="route-sorter")
    WebElement routeSorter;

    @FindBy(id="start-time-sorter")
    WebElement startTimeSorter;

    @FindBy(id="end-time-sorter")
    WebElement endTimeSorter;

    @FindBy(id="price-sorter")
    WebElement priceSorter;

    @FindBy(id="panic-sorter")
    WebElement panicSorter;

    @FindBy(id="canceled-sorter")
    WebElement canceledSorter;

    @FindBy(css=".filter-toggle-btn")
    WebElement filterToggleButton;

    @FindBy(css=".filter-btn--apply")
    WebElement applyFilterButton;

    @FindBy(css=".filter-btn--reset")
    private WebElement resetFilterButton;

    @FindBy(xpath = "//label[contains(text(), 'From')]/following-sibling::input")
    private WebElement fromDateInput;

    @FindBy(xpath = "//label[contains(text(), 'To')]/following-sibling::input")
    private WebElement toDateInput;

    @FindBy(css=".filter-chip")
    WebElement filterChip;

    public RideHistoryPage(WebDriver driver, WebDriverWait wait)
    {
        this.driver=driver;
        this.wait=wait;

        PageFactory.initElements(driver, this);
    }

    public void isLoaded() { wait.until(ExpectedConditions.visibilityOf(emailInput)); }

    public void getUserRideHistory(String email) {
        emailInput.sendKeys(email);
        searchButton.click();
        wait.until(ExpectedConditions.visibilityOf(searchInfo));
        initialRideCount = getRideCount();
    }


    public List<Ride> getDisplayedRides() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ride-row")));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        List<Ride> rides = rideRows.stream().map(row -> {
            Ride ride = new Ride();

            ride.setRoute(row.findElement(By.id("route-cell")).getText());

            String startTimeRaw = row.findElement(By.id("start-time-cell")).getText();
            ride.setStartTime(LocalDateTime.parse(startTimeRaw, formatter));

            String endTimeRaw = row.findElement(By.id("end-time-cell")).getText();
            ride.setEndTime(LocalDateTime.parse(endTimeRaw, formatter));

            String priceRaw = row.findElement(By.id("price-cell")).getText()
                    .replace(",", "")
                    .trim();
            ride.setPrice(new BigDecimal(priceRaw));

            ride.setPanicTriggered(row.findElement(By.id("panic-cell")).getText());
            ride.setCancelledBy(row.findElement(By.id("canceled-cell")).getText());

            return ride;
        }).collect(Collectors.toList());
        return rides;
    }

    public int getInitialRideCount() {
        return initialRideCount;
    }


    public String getSortSign(SortCriteria criteria) {
        return getSorterElement(criteria).getText();
    }

    private WebElement getSorterElement(SortCriteria criteria) {
        return switch (criteria) {
            case ROUTE -> routeSorter;
            case START_TIME -> startTimeSorter;
            case END_TIME -> endTimeSorter;
            case PRICE -> priceSorter;
            case PANIC -> panicSorter;
            case CANCELED -> canceledSorter;
        };
    }

    public void sort(SortCriteria criteria, SortDirection direction) {
        applySorting(getSorterElement(criteria), direction);
    }

    private void applySorting(WebElement sorter, SortDirection direction) {
        String targetSymbol = (direction == SortDirection.ASCENDING) ? "↑" : "↓";

        for (int i = 0; i < 3; i++) {
            String currentText = sorter.getText();

            if (currentText.contains(targetSymbol)) {
                break;
            }

            sorter.click();

            wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(sorter, currentText)));
        }

        wait.until(ExpectedConditions.visibilityOfAllElements(rideRows));
    }

    /**
     * FILTER METHODS
     */

    public void toggleFilterPanel() {
        filterToggleButton.click();
        wait.until(ExpectedConditions.visibilityOf(fromDateInput));
    }


    public void insertFromDate(String fromDate) {
        fromDateInput.clear();
        fromDateInput.sendKeys(fromDate);
    }

    public void insertToDate(String toDate) {
        toDateInput.clear();
        toDateInput.sendKeys(toDate);
    }

    public void applyFilters() {
        applyFilterButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".filter-badge")));
    }

    public void resetFilters() {
        resetFilterButton.click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ride-row")));
    }

    public int getRideCount() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".rides-table")));
        return driver.findElements(By.cssSelector(".ride-row")).size();
    }

    public String getActiveFilterChipText() {
        wait.until(ExpectedConditions.visibilityOf(filterChip));
        return filterChip.getText();
    }
}
