package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tourGuide.constants.TestConstants;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserPreferencesService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;
import tripPricer.TripPricer;

import javax.money.Monetary;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @MockBean
    private UserPreferencesService userPreferencesServiceMock;

    private User user1;
    private User user2;

    private static UserPreferences userPreferences;
    private static UserPreferencesDto userPreferencesDto;
    private static Attraction attraction1;
    private static Attraction attraction2;
    private static Attraction attraction3;
    private static Attraction attraction4;
    private static Attraction attraction5;
    private static Attraction attraction6;

    @BeforeAll
    public static void setUp() {
        Locale.setDefault(Locale.US);

        attraction1 = new Attraction("MoMA", "New York City",
                "New York", TestConstants.NYC_LATITUDE, TestConstants.NYC_LONGITUDE);
        attraction2 = new Attraction("Louvre", "Paris",
                "France", TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE);
        attraction3 = new Attraction("Orsay", "Paris",
                "France", TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE);
        attraction4 = new Attraction("Quai Branly", "Paris",
                "France", TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE);
        attraction5 = new Attraction("Palais des Beaux Arts", "Lille",
                "France", TestConstants.LILLE_LATITUDE, TestConstants.LILLE_LONGITUDE);
        attraction6 = new Attraction("Musée d'Histoire Naturelle", "Lille",
                "France", TestConstants.LILLE_LATITUDE, TestConstants.LILLE_LONGITUDE);

        userPreferences = new UserPreferences();
        userPreferences.setAttractionProximity(10);
        userPreferences.setNumberOfChildren(3);
        userPreferences.setNumberOfAdults(2);
        userPreferences.setTripDuration(10);
        userPreferences.setTicketQuantity(5);
        userPreferences.setCurrency(Monetary.getCurrency("USD"));
        userPreferences.setLowerPricePoint(Money.of(5, userPreferences.getCurrency()));
        userPreferences.setHighPricePoint(Money.of(100, userPreferences.getCurrency()));

        userPreferencesDto = new UserPreferencesDto();
        userPreferencesDto.setAttractionProximity(userPreferences.getAttractionProximity());
        userPreferencesDto.setNumberOfChildren(userPreferences.getNumberOfChildren());
        userPreferencesDto.setNumberOfAdults(userPreferences.getNumberOfAdults());
        userPreferencesDto.setTripDuration(userPreferences.getTripDuration());
        userPreferencesDto.setTicketQuantity(userPreferences.getTicketQuantity());
        userPreferencesDto.setCurrency(userPreferences.getCurrency().toString());
        userPreferencesDto.setLowerPricePoint(userPreferences.getLowerPricePoint().getNumber().intValue());
        userPreferencesDto.setHighPricePoint(userPreferences.getHighPricePoint().getNumber().intValue());
    }

    @BeforeEach
    public void setupPerTest() {
        InternalTestHelper.setInternalUserNumber(0);
        tourGuideService = new TourGuideService(gpsUtilMock, rewardsServiceMock, tripPricerMock, userPreferencesServiceMock);
        tourGuideService.tracker.stopTracking();

        user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
    }

    @Test
    public void getUserLocation_WithExistingVisitedLocations() {

        VisitedLocation visitedLocation = new VisitedLocation(
                user1.getUserId(),
                new Location(TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE),
                Date.from(Instant.now()));
        user1.addToVisitedLocations(visitedLocation);

        tourGuideService.addUser(user1);
        VisitedLocation lastUserLocation = tourGuideService.getUserLocation(user1.getUserName());

        assertEquals(visitedLocation, lastUserLocation);
        assertEquals(user1.getUserId(), lastUserLocation.userId);

        verify(gpsUtilMock, Mockito.times(0)).getUserLocation(user1.getUserId());
    }

    @Test
    public void getUserLocation_WithoutExistingVisitedLocations() {

        VisitedLocation visitedLocation = new VisitedLocation(
                user1.getUserId(),
                new Location(TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilMock.getUserLocation(user1.getUserId())).thenReturn(visitedLocation);

        tourGuideService.addUser(user1);
        VisitedLocation currentUserLocation = tourGuideService.getUserLocation(user1.getUserName());

        assertEquals(visitedLocation, currentUserLocation);
        assertEquals(user1.getUserId(), currentUserLocation.userId);

        verify(gpsUtilMock, Mockito.times(1)).getUserLocation(user1.getUserId());
    }

    @Test
    public void addUser() {

        tourGuideService.addUser(user1);
        tourGuideService.addUser(user2);

        User retrievedUser = tourGuideService.getUser(user1.getUserName());
        User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

        assertEquals(user1, retrievedUser);
        assertEquals(user2, retrievedUser2);
    }

    @Test
    public void getAllUsers() {

        tourGuideService.addUser(user1);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void trackUser() {

        VisitedLocation visitedLocation = new VisitedLocation(
                user1.getUserId(),
                new Location(TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilMock.getUserLocation(user1.getUserId())).thenReturn(visitedLocation);

        VisitedLocation trackedLocation = tourGuideService.trackUserLocation(user1);

        assertEquals(visitedLocation, trackedLocation);
        assertEquals(user1.getUserId(), trackedLocation.userId);

        verify(gpsUtilMock, Mockito.times(1)).getUserLocation(user1.getUserId());
    }

    @Test
    public void trackUserLocationForUserList() {

        VisitedLocation visitedLocation1 = new VisitedLocation(
                user1.getUserId(),
                new Location(TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilMock.getUserLocation(user1.getUserId())).thenReturn(visitedLocation1);

        VisitedLocation visitedLocation2 = new VisitedLocation(
                user2.getUserId(),
                new Location(TestConstants.NYC_LATITUDE, TestConstants.NYC_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilMock.getUserLocation(user2.getUserId())).thenReturn(visitedLocation2);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);
        tourGuideService.trackUserLocationForUserList(allUsers);

        assertEquals(visitedLocation1, user1.getLastVisitedLocation());
        assertEquals(visitedLocation2, user2.getLastVisitedLocation());

        verify(gpsUtilMock, Mockito.times(1)).getUserLocation(user1.getUserId());
        verify(gpsUtilMock, Mockito.times(1)).getUserLocation(user2.getUserId());
    }

    @Test
    public void getNearbyAttractions() {

        tourGuideService.addUser(user1);

        VisitedLocation visitedLocation = new VisitedLocation(
                user1.getUserId(),
                new Location(TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE),
                Date.from(Instant.now()));
        user1.addToVisitedLocations(visitedLocation);
        when(gpsUtilMock.getUserLocation(user1.getUserId())).thenReturn(visitedLocation);

        List<Attraction> attractionList = new ArrayList<>();
        attractionList.add(attraction1);
        attractionList.add(attraction2);
        attractionList.add(attraction3);
        attractionList.add(attraction4);
        attractionList.add(attraction5);
        attractionList.add(attraction6);

        when(gpsUtilMock.getAttractions()).thenReturn(attractionList);

        when(rewardsServiceMock.getDistance(attraction1,
                visitedLocation.location)).thenReturn(TestConstants.DISTANCE_NYC_TO_PARIS_IN_MI);
        when(rewardsServiceMock.getDistance(attraction2,
                visitedLocation.location)).thenReturn(Double.valueOf(0));
        when(rewardsServiceMock.getDistance(attraction3,
                visitedLocation.location)).thenReturn(Double.valueOf(0));
        when(rewardsServiceMock.getDistance(attraction4,
                visitedLocation.location)).thenReturn(Double.valueOf(0));
        when(rewardsServiceMock.getDistance(attraction5,
                visitedLocation.location)).thenReturn(TestConstants.DISTANCE_LILLE_TO_PARIS_IN_MI);
        when(rewardsServiceMock.getDistance(attraction6,
                visitedLocation.location)).thenReturn(TestConstants.DISTANCE_LILLE_TO_PARIS_IN_MI);

        when(rewardsServiceMock.getRewardPoints(any(), any())).thenReturn(100);

        List<NearByAttractionDto> nearByAttractionDtoList = tourGuideService.getNearByAttractions(user1.getUserName());

        assertEquals(5, nearByAttractionDtoList.size());
        assertEquals("Louvre", nearByAttractionDtoList.get(0).getAttractionName());
        assertEquals(0D, nearByAttractionDtoList.get(0).getAttractionDistanceFromUser());
        assertEquals(100, nearByAttractionDtoList.get(0).getAttractionRewardsPoint());

        verify(gpsUtilMock, Mockito.times(1)).getAttractions();
        verify(rewardsServiceMock, Mockito.atLeast(6)).getDistance(any(), any());
        verify(rewardsServiceMock, Mockito.times(5)).getRewardPoints(any(), any());

    }

    @Test
    public void getTripDeals() {

        tourGuideService.addUser(user1);

        List<Provider> expectedProviderList = new ArrayList<>();
        expectedProviderList.add(new Provider(user1.getUserId(), "Expected Provider1", 123.45));
        expectedProviderList.add(new Provider(user1.getUserId(), "Expected Provider2", 543.32));
        when(tripPricerMock.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(expectedProviderList);

        List<Provider> providers = tourGuideService.getTripDeals(user1.getUserName());

        assertEquals(expectedProviderList.size(), providers.size());
        verify(tripPricerMock, Mockito.times(1))
                .getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void setUserPreferences_WithNoUser_ThrowsException() {

        userPreferencesDto.setUserName("john");

        assertThrows(Exception.class, () -> tourGuideService.setUserPreferences(userPreferencesDto));
    }

    @Test
    void setUserPreferences_WithSuccess() throws Exception {

        tourGuideService.addUser(user1);
        userPreferencesDto.setUserName(user1.getUserName());

        when(userPreferencesServiceMock.getUserPreferencesDtoFromUserPreferences(userPreferences)).thenReturn(userPreferencesDto);
        when(userPreferencesServiceMock.getUserPreferencesFromUserPreferencesDto(userPreferencesDto)).thenReturn(userPreferences);

        UserPreferencesDto userPreferencesDtoReturned = tourGuideService.setUserPreferences(userPreferencesDto);

        assertEquals(userPreferencesDto.getUserName(), userPreferencesDtoReturned.getUserName());
        assertEquals(userPreferencesDto.getNumberOfAdults(), userPreferencesDtoReturned.getNumberOfAdults());
        assertEquals(userPreferencesDto.getCurrency(), userPreferencesDtoReturned.getCurrency());

        verify(userPreferencesServiceMock, Mockito.times(1)).getUserPreferencesDtoFromUserPreferences(userPreferences);
        verify(userPreferencesServiceMock, Mockito.times(1)).getUserPreferencesFromUserPreferencesDto(userPreferencesDto);
    }

    @Test
    void getAllCurrentLocations() {
        VisitedLocation visitedLocation1 = new VisitedLocation(user1.getUserId(), attraction1, new Date());
        VisitedLocation visitedLocation2 = new VisitedLocation(user1.getUserId(), attraction2, new Date());
        user1.addToVisitedLocations(visitedLocation1);
        user1.addToVisitedLocations(visitedLocation2);
        tourGuideService.addUser(user1);

        VisitedLocation visitedLocation3 = new VisitedLocation(user1.getUserId(), attraction3, new Date());
        user2.addToVisitedLocations(visitedLocation3);
        tourGuideService.addUser(user2);

        assertEquals(tourGuideService.getAllUsers().size(),
                tourGuideService.getAllCurrentLocations().size());
        assertEquals(attraction2,
                tourGuideService.getAllCurrentLocations().get(0).getLocation());
        assertEquals(user2.getUserId().toString(),
                tourGuideService.getAllCurrentLocations().get(1).getUserId());
    }
}
