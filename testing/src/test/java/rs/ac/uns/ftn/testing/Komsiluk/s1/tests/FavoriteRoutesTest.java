package rs.ac.uns.ftn.testing.Komsiluk.s1.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rs.ac.uns.ftn.testing.Komsiluk.s1.pages.LoginPage;
import rs.ac.uns.ftn.testing.Komsiluk.s1.pages.MainGuestPage;
import rs.ac.uns.ftn.testing.Komsiluk.s1.pages.MainPassengerPage;
import rs.ac.uns.ftn.testing.Komsiluk.s1.pages.PassengerHistoryPage;
import rs.ac.uns.ftn.testing.Komsiluk.s1.util.FavoriteRoute;

public class FavoriteRoutesTest extends TestBase {
	
	private MainPassengerPage loginPassenger() {
        
		MainGuestPage mainGuestPage = new MainGuestPage(driver, wait);
		assertDoesNotThrow(() -> mainGuestPage.isLoaded(), "Main guest page should load successfully");
		
		mainGuestPage.clickAvatarButton();
		
		LoginPage loginPage = mainGuestPage.clickLoginButton();
		assertDoesNotThrow(() -> loginPage.isLoaded(), "Login page should load successfully");
		
		loginPage.insertUsername("passenger@test.com");
		loginPage.insertPassword("pass12345");
		MainPassengerPage mainPassengerPage = loginPage.loginPassenger();
		assertDoesNotThrow(() -> mainPassengerPage.isLoaded(), "Main passenger page should load successfully");
		
		return mainPassengerPage;
	}
	
	private FavoriteRoute openFavoriteRouteDetails(MainPassengerPage mainPassengerPage) {
		
		mainPassengerPage.clickMenuButton();
		mainPassengerPage.clickFavButton();
		
		FavoriteRoute favoriteRoute = mainPassengerPage.clickFavCard();
		assertTrue(mainPassengerPage.isFavCardDetailsDialogDisplayed(), "Book ride form should be prefilled with favorite route details");
		assertTrue(mainPassengerPage.isFavCardDetailsDialogContentCorrect(favoriteRoute));
		
		favoriteRoute=mainPassengerPage.setFavoriteRouteLists(favoriteRoute);
		
		return favoriteRoute;
	}
	
	private void openRenameFavoriteRouteDialog(MainPassengerPage mainPassengerPage, FavoriteRoute favoriteRoute) {
		mainPassengerPage.clickRenameFavButton();
		assertTrue(mainPassengerPage.isRenameFavoriteRouteDialogDisplayed(), "Rename favorite route dialog should be displayed after clicking rename button");
		assertTrue(mainPassengerPage.isRenameFavoriteRouteInputPrefilledWithCurrentName(favoriteRoute), "Rename favorite route input should be prefilled with current favorite route name");
	}
	
	private void openDeleteFavoriteRouteDialog(MainPassengerPage mainPassengerPage, FavoriteRoute favoriteRoute) {
		mainPassengerPage.clickFavDeleteButton();
		assertTrue(mainPassengerPage.isDeleteFavoriteRouteDialogDisplayed(), "Delete favorite route dialog should be displayed after clicking delete button");
		assertTrue(mainPassengerPage.isDeleteFavoriteRouteTitlePrefilledWithCurrentName(favoriteRoute), "Delete favorite route dialog title should contain current favorite route name");
	}
	
	private PassengerHistoryPage openRideHistoryPage(MainPassengerPage mainPassengerPage) {
		mainPassengerPage.clickAvatarButton();
		PassengerHistoryPage passengerHistoryPage = mainPassengerPage.clickHistoryButton();
		assertDoesNotThrow(() -> passengerHistoryPage.isLoaded(), "Passenger history page should load successfully");
		
		return passengerHistoryPage;
	}

	@Test
    void bookFromFavorites_prefillsBookRideForm_withRouteAndPreferences() {

		MainPassengerPage mainPassengerPage = loginPassenger();
		FavoriteRoute favoriteRoute = openFavoriteRouteDetails(mainPassengerPage);

		mainPassengerPage.clickBookButton();
		assertTrue(mainPassengerPage.isBookRidePanelDisplayed(), "Book ride panel should be displayed after clicking book button in favorite route details");
		assertTrue(mainPassengerPage.isBookRidePanelContentCorrect(favoriteRoute), "Book ride panel should be prefilled with favorite route details");
    }

    @Test
    void renameFavoriteRoute_changesNameInFavoritesList() {
		MainPassengerPage mainPassengerPage = loginPassenger();
		FavoriteRoute favoriteRoute = openFavoriteRouteDetails(mainPassengerPage);
		openRenameFavoriteRouteDialog(mainPassengerPage, favoriteRoute);
		
		String newName = "New Favorite Name " + System.currentTimeMillis();
		mainPassengerPage.insertNewNameInRenameFavoriteRouteInput(newName);
		mainPassengerPage.clickRenameFavConfirmButton();
		assertTrue(mainPassengerPage.isFavoriteRouteNameChangedInFavCard(newName), "Favorite route name should be updated in favorites list after renaming");
    }

    @Test
    void renameFavoriteRoute_withSmallName_withLongName_doesNotChangeName() {
		MainPassengerPage mainPassengerPage = loginPassenger();
		FavoriteRoute favoriteRoute = openFavoriteRouteDetails(mainPassengerPage);
		openRenameFavoriteRouteDialog(mainPassengerPage, favoriteRoute);
		
		mainPassengerPage.insertNewNameInRenameFavoriteRouteInput("a");
		assertFalse(mainPassengerPage.isRenameFavConfirmButtonEnabled(), "Rename confirm button should be disabled when new name is to small or too long");
		String longName="";
		for(int i=0;i<=500;i++) {
			longName+="a";
		}
		mainPassengerPage.insertNewNameInRenameFavoriteRouteInput(longName);
		assertFalse(mainPassengerPage.isRenameFavConfirmButtonEnabled(), "Rename confirm button should be disabled when new name is to small or too long");
    }

    @Test
    void deleteFavoriteRoute_removesItFromFavoritesList() {
		MainPassengerPage mainPassengerPage = loginPassenger();
		FavoriteRoute favoriteRoute = openFavoriteRouteDetails(mainPassengerPage);
		openDeleteFavoriteRouteDialog(mainPassengerPage, favoriteRoute);
		
		mainPassengerPage.clickDeleteFavConfirmButton();
		assertTrue(mainPassengerPage.isFavoriteRouteRemovedFromFavCards(favoriteRoute), "Favorite route should be removed from favorites list after confirming deletion");
    }

    @Test
    void rideHistory_addToFavorites_thenAppearsInFavorites() {
		MainPassengerPage mainPassengerPage = loginPassenger();
		PassengerHistoryPage passengerHistoryPage = openRideHistoryPage(mainPassengerPage);
		
		passengerHistoryPage.clickRideRow();
		FavoriteRoute favoriteRoute = passengerHistoryPage.clickSaveAsFavoriteButton();
		assertTrue(passengerHistoryPage.isAddFavoriteDialogDisplayed(), "Add favorite dialog should be displayed after clicking save as favorite button in ride history");
		
		passengerHistoryPage.insertFavoriteRouteName("History Favorite");
		favoriteRoute.setTitle("History Favorite");
		passengerHistoryPage.clickAddFavoriteDialogConfirmButton();
		mainPassengerPage = passengerHistoryPage.clickBackButton();
		
		mainPassengerPage.clickMenuButton();
		mainPassengerPage.clickFavButton();
		mainPassengerPage.clickLastFavCard();
		assertTrue(mainPassengerPage.isFavoriteDetailsCorrectFromHistory(favoriteRoute), "Favorite route added from history should have correct details in favorite route details dialog");
    }
}
