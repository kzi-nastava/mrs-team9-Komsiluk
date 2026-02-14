package rs.ac.uns.ftn.testing.Komsiluk.s2.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rs.ac.uns.ftn.testing.Komsiluk.s1.tests.TestBase;
import rs.ac.uns.ftn.testing.Komsiluk.s2.pages.HomePage;
import rs.ac.uns.ftn.testing.Komsiluk.s2.pages.LoginPage;
import rs.ac.uns.ftn.testing.Komsiluk.s2.pages.PassengerPage;

import static org.junit.jupiter.api.Assertions.*;

public class Student2E2ETest extends TestBase {


    private PassengerPage loginAsPassenger() {

        HomePage homePage = new HomePage(driver, wait);
        LoginPage loginPage = new LoginPage(driver, wait);
        PassengerPage passengerPage = new PassengerPage(driver, wait);

        homePage.openHamburgerMenu();
        homePage.clickLogin();

        loginPage.login("passenger@test.com", "pass12345");

        assertDoesNotThrow(() -> wait.until(d -> !d.getCurrentUrl().contains("/login")),
                "Login nije uspešno završen.");

        return passengerPage;
    }

    private PassengerPage openRideHistory() {

        PassengerPage passengerPage = loginAsPassenger();
        HomePage homePage = new HomePage(driver, wait);

        homePage.openHamburgerMenu();
        passengerPage.openRideHistory();

        return passengerPage;
    }

    private void openRideDetails(PassengerPage passengerPage, int index) {

        passengerPage.clickFirstRideRow(index);

        assertTrue(passengerPage.isDetailsModalOpened(),
                "Details modal nije otvoren!");
    }

    @Test
    void shouldRateRideSuccessfully() {

        PassengerPage passengerPage = openRideHistory();

        openRideDetails(passengerPage, 0);

        passengerPage.clickLeaveRating();
        passengerPage.leaveRating(4, 5, "Voznja je bila skroz okej.");

        assertTrue(
                passengerPage.isToastWithTextVisible("Rating submitted successfully."),
                "Toast poruka o uspesnom ocenjivanju nije prikazana!"
        );

        passengerPage.waitForToastToDisappear();
    }

    @Test
    void shouldShowNotificationWhenRideAlreadyRated() {

        PassengerPage passengerPage = openRideHistory();

        openRideDetails(passengerPage, 1);

        passengerPage.clickLeaveRating();
        passengerPage.leaveRating(2, 3,
                "Vozac je bio los a i auto nije bilo bas najbolje.");

        assertTrue(
                passengerPage.isToastWithTextVisible(
                        "Rating from this rater for this ride already exists"
                ),
                "Očekivana poruka da je vožnja već ocenjena nije prikazana!"
        );

        passengerPage.waitForToastToDisappear();
    }

    @Test
    void shouldShowNotificationWhenRatingPeriodExpired() {

        PassengerPage passengerPage = openRideHistory();

        openRideDetails(passengerPage, 2);

        passengerPage.clickLeaveRating();
        passengerPage.leaveRating(5, 5, "Odlicna voznja.");

        assertTrue(
                passengerPage.isToastWithTextVisible(
                        "Rating period has expired."
                ),
                "Toast poruka o isteklom periodu ocenjivanja nije prikazana!"
        );

        passengerPage.waitForToastToDisappear();
    }
}
