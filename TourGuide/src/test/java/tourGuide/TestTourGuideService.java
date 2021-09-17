package tourGuide;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.AttractionBean;
import tourGuide.model.LocationBean;
import tourGuide.model.VisitedLocationBean;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserPreferencesService;
import tourGuide.service.contracts.IGpsUtilAPIRequestService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;
import tripPricer.TripPricer;

import javax.money.Monetary;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tourGuide.constants.TestConstants.DISTANCE_LILLE_TO_PARIS_IN_MI;
import static tourGuide.constants.TestConstants.DISTANCE_NYC_TO_PARIS_IN_MI;
import static tourGuide.constants.TestConstants.LILLE_LATITUDE;
import static tourGuide.constants.TestConstants.LILLE_LONGITUDE;
import static tourGuide.constants.TestConstants.NYC_LATITUDE;
import static tourGuide.constants.TestConstants.NYC_LONGITUDE;
import static tourGuide.constants.TestConstants.PARIS_LATITUDE;
import static tourGuide.constants.TestConstants.PARIS_LONGITUDE;

@SpringBootTest
public class TestTourGuideService {

    private TourGuideService tourGuideService;

    @MockBean
    private IGpsUtilAPIRequestService gpsUtilAPIRequestServiceMock;

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
    private static AttractionBean attraction1;
    private static AttractionBean attraction2;
    private static AttractionBean attraction3;
    private static AttractionBean attraction4;
    private static AttractionBean attraction5;
    private static AttractionBean attraction6;

    @BeforeAll
    public static void setUp() {

        attraction1 = new AttractionBean("MoMA", "New York City",
                "New York", NYC_LATITUDE, NYC_LONGITUDE);
        attraction2 = new AttractionBean("Louvre", "Paris",
                "France", PARIS_LATITUDE, PARIS_LONGITUDE);
        attraction3 = new AttractionBean("Orsay", "Paris",
                "France", PARIS_LATITUDE, PARIS_LONGITUDE);
        attraction4 = new AttractionBean("Quai Branly", "Paris",
                "France", PARIS_LATITUDE, PARIS_LONGITUDE);
        attraction5 = new AttractionBean("Palais des Beaux Arts", "Lille",
                "France", LILLE_LATITUDE, LILLE_LONGITUDE);
        attraction6 = new AttractionBean("Musée d'Histoire Naturelle", "Lille",
                "France", LILLE_LATITUDE, LILLE_LONGITUDE);

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
        tourGuideService = new TourGuideService(gpsUtilAPIRequestServiceMock, rewardsServiceMock, tripPricerMock, userPreferencesServiceMock);
        tourGuideService.tracker.stopTracking();

        user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
    }

    @Test
    public void getUserLocation_WithExistingVisitedLocations() {

        VisitedLocationBean visitedLocation = new VisitedLocationBean(
                user1.getUserId(),
                new LocationBean(PARIS_LATITUDE, PARIS_LONGITUDE),
                Date.from(Instant.now()));
        user1.addToVisitedLocations(visitedLocation);

        tourGuideService.addUser(user1);
        VisitedLocationBean lastUserLocation = tourGuideService.getUserLocation(user1.getUserName());

        assertEquals(visitedLocation, lastUserLocation);
        assertEquals(user1.getUserId(), lastUserLocation.userId);

        verify(gpsUtilAPIRequestServiceMock, Mockito.times(0)).getUserLocation(user1.getUserId());
    }

    @Test
    public void getUserLocation_WithoutExistingVisitedLocations() {

        VisitedLocationBean visitedLocation = new VisitedLocationBean(
                user1.getUserId(),
                new LocationBean(PARIS_LATITUDE, PARIS_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilAPIRequestServiceMock.getUserLocation(user1.getUserId())).thenReturn(visitedLocation);

        tourGuideService.addUser(user1);
        VisitedLocationBean currentUserLocation = tourGuideService.getUserLocation(user1.getUserName());

        assertEquals(visitedLocation, currentUserLocation);
        assertEquals(user1.getUserId(), currentUserLocation.userId);

        verify(gpsUtilAPIRequestServiceMock, Mockito.times(1)).getUserLocation(user1.getUserId());
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

        VisitedLocationBean visitedLocation = new VisitedLocationBean(
                user1.getUserId(),
                new LocationBean(PARIS_LATITUDE, PARIS_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilAPIRequestServiceMock.getUserLocation(user1.getUserId())).thenReturn(visitedLocation);

        VisitedLocationBean trackedLocation = tourGuideService.trackUserLocation(user1);

        assertEquals(visitedLocation, trackedLocation);
        assertEquals(user1.getUserId(), trackedLocation.userId);

        verify(gpsUtilAPIRequestServiceMock, Mockito.times(1)).getUserLocation(user1.getUserId());
    }

    @Test
    public void trackUserLocationForUserList() {

        VisitedLocationBean visitedLocation1 = new VisitedLocationBean(
                user1.getUserId(),
                new LocationBean(PARIS_LATITUDE, PARIS_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilAPIRequestServiceMock.getUserLocation(user1.getUserId())).thenReturn(visitedLocation1);

        VisitedLocationBean visitedLocation2 = new VisitedLocationBean(
                user2.getUserId(),
                new LocationBean(NYC_LATITUDE, NYC_LONGITUDE),
                Date.from(Instant.now()));
        when(gpsUtilAPIRequestServiceMock.getUserLocation(user2.getUserId())).thenReturn(visitedLocation2);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);
        tourGuideService.trackUserLocationForUserList(allUsers);

        assertEquals(visitedLocation1, user1.getLastVisitedLocation());
        assertEquals(visitedLocation2, user2.getLastVisitedLocation());

        verify(gpsUtilAPIRequestServiceMock, Mockito.times(1)).getUserLocation(user1.getUserId());
        verify(gpsUtilAPIRequestServiceMock, Mockito.times(1)).getUserLocation(user2.getUserId());
    }

    @Test
    public void getNearbyAttractions() {

        tourGuideService.addUser(user1);

        VisitedLocationBean visitedLocation = new VisitedLocationBean(
                user1.getUserId(),
                new LocationBean(PARIS_LATITUDE, PARIS_LONGITUDE),
                Date.from(Instant.now()));
        user1.addToVisitedLocations(visitedLocation);
        when(gpsUtilAPIRequestServiceMock.getUserLocation(user1.getUserId())).thenReturn(visitedLocation);

        List<AttractionBean> attractionList = new ArrayList<>();
        attractionList.add(attraction1);
        attractionList.add(attraction2);
        attractionList.add(attraction3);
        attractionList.add(attraction4);
        attractionList.add(attraction5);
        attractionList.add(attraction6);

        when(gpsUtilAPIRequestServiceMock.getAttractions()).thenReturn(attractionList);

        when(rewardsServiceMock.getDistance(attraction1,
                visitedLocation.location)).thenReturn(DISTANCE_NYC_TO_PARIS_IN_MI);
        when(rewardsServiceMock.getDistance(attraction2,
                visitedLocation.location)).thenReturn(Double.valueOf(0));
        when(rewardsServiceMock.getDistance(attraction3,
                visitedLocation.location)).thenReturn(Double.valueOf(0));
        when(rewardsServiceMock.getDistance(attraction4,
                visitedLocation.location)).thenReturn(Double.valueOf(0));
        when(rewardsServiceMock.getDistance(attraction5,
                visitedLocation.location)).thenReturn(DISTANCE_LILLE_TO_PARIS_IN_MI);
        when(rewardsServiceMock.getDistance(attraction6,
                visitedLocation.location)).thenReturn(DISTANCE_LILLE_TO_PARIS_IN_MI);

        when(rewardsServiceMock.getRewardPoints(any(), any())).thenReturn(100);

        List<NearByAttractionDto> nearByAttractionDtoList = tourGuideService.getNearByAttractions(user1.getUserName());

        assertEquals(5, nearByAttractionDtoList.size());
        assertEquals("Louvre", nearByAttractionDtoList.get(0).getAttractionName());
        assertEquals(0D, nearByAttractionDtoList.get(0).getAttractionDistanceFromUser());
        assertEquals(100, nearByAttractionDtoList.get(0).getAttractionRewardsPoint());

        verify(gpsUtilAPIRequestServiceMock, Mockito.times(1)).getAttractions();
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
        VisitedLocationBean visitedLocation1 = new VisitedLocationBean(user1.getUserId(), attraction1, new Date());
        VisitedLocationBean visitedLocation2 = new VisitedLocationBean(user1.getUserId(), attraction2, new Date());
        user1.addToVisitedLocations(visitedLocation1);
        user1.addToVisitedLocations(visitedLocation2);
        tourGuideService.addUser(user1);

        VisitedLocationBean visitedLocation3 = new VisitedLocationBean(user1.getUserId(), attraction3, new Date());
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
