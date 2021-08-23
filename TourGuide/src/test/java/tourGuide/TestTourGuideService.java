package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.testConstants.TestConstants;
import tourGuide.user.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TestTourGuideService {

    private TourGuideService tourGuideService;

    @MockBean
    private GpsUtil gpsUtilMock;

    @MockBean
    private RewardsService rewardsServiceMock;

    @MockBean
    private TripPricer tripPricerMock;

    @BeforeAll
    public static void setUp() {
        Locale.setDefault(Locale.US);
    }

    @BeforeEach
    public void setupPerTest() {
        InternalTestHelper.setInternalUserNumber(0);
        tourGuideService = new TourGuideService(gpsUtilMock, rewardsServiceMock, tripPricerMock);
        tourGuideService.tracker.stopTracking();
    }

    @Test
    public void getUserLocation_WithExistingVisitedLocations() {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = new VisitedLocation(
                user.getUserId(),
                new Location(TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE),
                Date.from(Instant.now()));
        user.addToVisitedLocations(visitedLocation);

        VisitedLocation lastUserLocation = tourGuideService.getUserLocation(user);

        assertEquals(visitedLocation, lastUserLocation);
        assertEquals(user.getUserId(), lastUserLocation.userId);

        verify(gpsUtilMock, Mockito.times(0)).getUserLocation(user.getUserId());
    }

    @Test
    public void getUserLocation_WithoutExistingVisitedLocations() {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        VisitedLocation visitedLocation = new VisitedLocation(
                user.getUserId(),
                new Location(TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilMock.getUserLocation(user.getUserId())).thenReturn(visitedLocation);

        VisitedLocation currentUserLocation = tourGuideService.getUserLocation(user);

        assertEquals(visitedLocation, currentUserLocation);
        assertEquals(user.getUserId(), currentUserLocation.userId);

        verify(gpsUtilMock, Mockito.times(1)).getUserLocation(user.getUserId());
    }

    @Test
    public void addUser() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrievedUser = tourGuideService.getUser(user.getUserName());
        User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

        assertEquals(user, retrievedUser);
        assertEquals(user2, retrievedUser2);
    }

    @Test
    public void getAllUsers() {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void trackUser() {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        VisitedLocation visitedLocation = new VisitedLocation(
                user.getUserId(),
                new Location(TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilMock.getUserLocation(user.getUserId())).thenReturn(visitedLocation);

        VisitedLocation trackedLocation = tourGuideService.trackUserLocation(user);

        assertEquals(visitedLocation, trackedLocation);
        assertEquals(user.getUserId(), trackedLocation.userId);

        verify(gpsUtilMock, Mockito.times(1)).getUserLocation(user.getUserId());
    }

    @Disabled // Not yet implemented
    @Test
    public void getNearbyAttractions() {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);

        assertEquals(5, attractions.size());
    }

    @Test
    public void getTripDeals() {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> expectedProviderList = new ArrayList<>();
        expectedProviderList.add(new Provider(user.getUserId(), "Expected Provider1", 123.45));
        expectedProviderList.add(new Provider(user.getUserId(), "Expected Provider2", 543.32));
        when(tripPricerMock.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(expectedProviderList);

        List<Provider> providers = tourGuideService.getTripDeals(user);

        assertEquals(expectedProviderList.size(), providers.size());
        verify(tripPricerMock, Mockito.times(1))
                .getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt());
    }

}
