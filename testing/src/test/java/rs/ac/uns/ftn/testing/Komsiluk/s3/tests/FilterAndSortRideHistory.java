package rs.ac.uns.ftn.testing.Komsiluk.s3.tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import rs.ac.uns.ftn.testing.Komsiluk.s3.models.Ride;
import rs.ac.uns.ftn.testing.Komsiluk.s3.pages.LoginPage;
import rs.ac.uns.ftn.testing.Komsiluk.s3.pages.MainGuestPage;
import rs.ac.uns.ftn.testing.Komsiluk.s3.pages.MainAdminPage;
import rs.ac.uns.ftn.testing.Komsiluk.s3.pages.RideHistoryPage;
import rs.ac.uns.ftn.testing.Komsiluk.s3.util.SortCriteria;
import rs.ac.uns.ftn.testing.Komsiluk.s3.util.SortDirection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilterAndSortRideHistory extends TestBase {

    private RideHistoryPage rideHistoryPage;


    private MainAdminPage loginAdmin() {
        MainGuestPage mainGuestPage = new MainGuestPage(driver, wait);
        assertDoesNotThrow(mainGuestPage::isLoaded, "Main guest page should load successfully");

        mainGuestPage.clickAvatarButton();

        LoginPage loginPage = mainGuestPage.clickLoginButton();
        assertDoesNotThrow(loginPage::isLoaded, "Login page should load successfully");

        loginPage.insertUsername("admin@test.com");
        loginPage.insertPassword("admin12345");
        MainAdminPage mainAdminPage = loginPage.loginAdmin();
        assertDoesNotThrow(mainAdminPage::isLoaded, "Main admin page should load successfully");

        return mainAdminPage;
    }

    private RideHistoryPage navigateToRideHistory(MainAdminPage mainAdminPage) {
        RideHistoryPage rideHistoryPage = mainAdminPage.openRideHistoryPage();
        assertDoesNotThrow(rideHistoryPage::isLoaded, "Ride history page should load successfully");
        return rideHistoryPage;
    }

    @BeforeAll
    public void setupSuite() {
        MainAdminPage mainAdminPage = loginAdmin();
        rideHistoryPage = navigateToRideHistory(mainAdminPage);
        rideHistoryPage.getUserRideHistory("passenger@test.com");
    }

    /**
     * SORTING TESTING
     */

    @Test
    @Order(1)
    public void testInitialSortingIsStartTimeDescending() {
        assumeTrue(rideHistoryPage.getInitialRideCount() >= 2, "Test should have at least 2 rides to verify sorting");
        List<Ride> actualRides = rideHistoryPage.getDisplayedRides();

        List<Ride> expectedRides = actualRides.stream()
                .sorted(Comparator.comparing(Ride::getStartTime).reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < actualRides.size(); i++) {
            assertEquals(expectedRides.get(i).getStartTime(), actualRides.get(i).getStartTime(),
                    "Rides are not sorted correctly by start date (descending) on index: " + i);
        }
    }

    @ParameterizedTest
    @Order(2)
    @MethodSource("sortProvider")
    public void testAllSortings(SortCriteria criteria, SortDirection direction) {
        assumeTrue(rideHistoryPage.getInitialRideCount() >= 2, "Test should have at least 2 rides to verify sorting");

        List<Ride> initialRides = rideHistoryPage.getDisplayedRides();

        List<Ride> expectedSortedRides = sort(initialRides, criteria, direction);

        rideHistoryPage.sort(criteria, direction);

        String expectedSign = (direction == SortDirection.ASCENDING) ? "↑" : "↓";
        String actualSign = rideHistoryPage.getSortSign(criteria);

        assertTrue(actualSign.contains(expectedSign),
                String.format("The arrow for %s is incorrect! Expected: %s, Actual: %s",
                        criteria, expectedSign, actualSign));

        List<Ride> actualSortedRides = rideHistoryPage.getDisplayedRides();
        assertEquals(expectedSortedRides.size(), actualSortedRides.size(), "Number of rides changed after sorting!");

        for (int i = 0; i < expectedSortedRides.size(); i++) {
            assertEquals(expectedSortedRides.get(i).getRoute(), actualSortedRides.get(i).getRoute(),
                    "Mismatch at index " + i + " for criterion: " + criteria + " and direction: " + direction);
        }
    }

    /**
     * FILTER TESTING
     */

    @Test
    @Order(3)
    @DisplayName("Filter - No Results Case")
    public void testFilterNoResults() {
        assumeTrue(rideHistoryPage.getInitialRideCount() >= 1, "Test should have at least 1 ride to verify filtering");
        rideHistoryPage.toggleFilterPanel();

        rideHistoryPage.insertFromDate("01022031");
        assertEquals(formatToISO("01022031") + " : ---", rideHistoryPage.getActiveFilterChipText(),  "Active filter chip should display correct 'from' date after input");

        rideHistoryPage.insertToDate("01052031");
        assertEquals(formatToISO("01022031") + " : " + formatToISO("01052031"), rideHistoryPage.getActiveFilterChipText(), "Active filter chip should display correct 'from' and 'to' dates after input");

        rideHistoryPage.applyFilters();

        assertEquals(0, rideHistoryPage.getRideCount(), "Table should show no rides for a future date range where no rides exist");

        resetFilterState();
    }

    @Test
    @Order(4)
    @DisplayName("Filter - Reset Functionality")
    public void testFilterReset() {
        assumeTrue(rideHistoryPage.getInitialRideCount() >= 1, "Test should have at least 1 ride to verify filtering");
        resetFilterState();

        assertTrue(rideHistoryPage.getRideCount() == rideHistoryPage.getInitialRideCount(), "After resetting filters, the number of displayed rides should return to the initial count. Expected " + rideHistoryPage.getInitialRideCount() + " but got " + rideHistoryPage.getRideCount());
    }

    @Test
    @Order(5)
    @DisplayName("Filter - Dynamic Scenarios: Deep Route Verification")
    public void testFilterDateRange() {
        assumeTrue(rideHistoryPage.getInitialRideCount() >= 1, "Test should have at least 1 ride to verify filtering");
        Map<LocalDate, List<Ride>> ridesByStartDate = mapRidesByDate();
        if (ridesByStartDate.isEmpty()) return;

        List<LocalDate> sortedRideDates = ridesByStartDate.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMddyyyy");


        if (sortedRideDates.size() == 1) {
            // single ride
            LocalDate firstDate = sortedRideDates.get(0);
            List<String> expectedRoutes = extractRoutes(ridesByStartDate.get(firstDate));

            performFilterAction(firstDate.format(inputFormatter), firstDate.format(inputFormatter));
            verifyRoutesMatch(expectedRoutes, "Single day filter");

            resetFilterState();
        } else {
            // multi ride
            int mid = sortedRideDates.size() / 2;

            // first half
            List<Ride> expected1 = new ArrayList<>();
            for (int i = 0; i < mid; i++) {
                expected1.addAll(ridesByStartDate.get(sortedRideDates.get(i)));
            }
            performFilterAction(sortedRideDates.get(0).format(inputFormatter),
                    sortedRideDates.get(mid - 1).format(inputFormatter));
            verifyRoutesMatch(extractRoutes(expected1), "First half range");
            resetFilterState();

            // second half
            List<Ride> expected2 = new ArrayList<>();
            for (int i = mid; i < sortedRideDates.size(); i++) {
                expected2.addAll(ridesByStartDate.get(sortedRideDates.get(i)));
            }
            performFilterAction(sortedRideDates.get(mid).format(inputFormatter),
                    sortedRideDates.get(sortedRideDates.size() - 1).format(inputFormatter));
            verifyRoutesMatch(extractRoutes(expected2), "Second half range");
            resetFilterState();

            // whole range
            int expectedFullCount = rideHistoryPage.getInitialRideCount();
            performFilterAction(sortedRideDates.get(0).format(inputFormatter),
                    sortedRideDates.get(sortedRideDates.size() - 1).format(inputFormatter));
            assertEquals(expectedFullCount, rideHistoryPage.getRideCount(), "Full range filter failed on count!");
            resetFilterState();
        }
    }

    //helpers

    private Map<LocalDate, List<Ride>> mapRidesByDate() {
        return rideHistoryPage.getDisplayedRides().stream()
                .collect(Collectors.groupingBy(ride -> ride.getStartTime().toLocalDate()));
    }

    private void performFilterAction(String from, String to) {
        rideHistoryPage.toggleFilterPanel();
        rideHistoryPage.insertFromDate(from);
        rideHistoryPage.insertToDate(to);
        rideHistoryPage.applyFilters();
    }

    private void verifyRoutesMatch(List<String> expectedRoutes, String scenarioName) {
        List<Ride> actualRides = rideHistoryPage.getDisplayedRides();
        List<String> actualRoutes = actualRides.stream().map(Ride::getRoute).collect(Collectors.toList());

        Collections.sort(expectedRoutes);
        Collections.sort(actualRoutes);

        assertAll(scenarioName + " verification",
                () -> assertEquals(expectedRoutes.size(), actualRoutes.size(), "Number of rides doesn't match!"),
                () -> assertEquals(expectedRoutes, actualRoutes, "The routes displayed do not match the expected ones!")
        );
    }

    private void resetFilterState() {
        rideHistoryPage.toggleFilterPanel();
        rideHistoryPage.resetFilters();
    }

    private List<String> extractRoutes(List<Ride> rides) {
        return rides.stream().map(Ride::getRoute).collect(Collectors.toList());
    }


    private static Stream<Arguments> sortProvider() {
        return Stream.of(
                Arguments.of(SortCriteria.ROUTE, SortDirection.ASCENDING),
                Arguments.of(SortCriteria.ROUTE, SortDirection.DESCENDING),
                Arguments.of(SortCriteria.PRICE, SortDirection.ASCENDING),
                Arguments.of(SortCriteria.PRICE, SortDirection.DESCENDING),
                Arguments.of(SortCriteria.START_TIME, SortDirection.ASCENDING),
                Arguments.of(SortCriteria.START_TIME, SortDirection.DESCENDING),
                Arguments.of(SortCriteria.END_TIME, SortDirection.ASCENDING),
                Arguments.of(SortCriteria.END_TIME, SortDirection.DESCENDING),
                Arguments.of(SortCriteria.PANIC, SortDirection.ASCENDING),
                Arguments.of(SortCriteria.PANIC, SortDirection.DESCENDING),
                Arguments.of(SortCriteria.CANCELED, SortDirection.ASCENDING),
                Arguments.of(SortCriteria.CANCELED, SortDirection.DESCENDING)
        );
    }

    private static List<Ride> sort(List<Ride> rides, SortCriteria criteria, SortDirection direction) {
        Comparator<Ride> comparator = switch (criteria) {
            case ROUTE -> Comparator.comparing(Ride::getRoute);
            case START_TIME -> Comparator.comparing(Ride::getStartTime);
            case END_TIME -> Comparator.comparing(Ride::getEndTime);
            case PRICE -> Comparator.comparing(Ride::getPrice);
            case PANIC -> Comparator.comparing(Ride::getPanicTriggered);
            case CANCELED -> Comparator.comparing(ride ->
                            normalizeForSort(ride.getCancelledBy()),
                    String.CASE_INSENSITIVE_ORDER);
        };

        if (direction == SortDirection.DESCENDING) {
            comparator = comparator.reversed();
        }

        return rides.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static String formatToISO(String inputDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMddyyyy");

        LocalDate date = LocalDate.parse(inputDate, inputFormatter);

        return date.toString();
    }

    private static String normalizeForSort(String value) {
        if (value == null || value.equals("—") || value.equals("-") || value.trim().isEmpty()) {
            return "";
        }
        return value.trim();
    }
}
