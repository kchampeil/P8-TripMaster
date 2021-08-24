package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.testConstants.TestConstants;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.TripPricer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TestRewardsService {
    //KC à @Autowired
    private RewardsService rewardsService;

    @MockBean
    private GpsUtil gpsUtilMock;

    @MockBean
    private RewardCentral rewardCentralMock;

    @MockBean
    private TripPricer tripPricerMock;

    @MockBean
    private TourGuideService tourGuideServiceMock;

    @BeforeAll
    public static void setUp() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void userGetRewards() { // KC plutôt calculateRewards ?
        rewardsService = new RewardsService(gpsUtilMock, rewardCentralMock);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction1 = new Attraction("MoMA", "New York City",
                "New York", TestConstants.NYC_LATITUDE, TestConstants.NYC_LONGITUDE);
        Attraction attraction2 = new Attraction("Orsay", "Paris",
                "France", TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE);
        Attraction attraction3 = new Attraction("Musée des Beaux-Arts", "Lille",
                "France", TestConstants.LILLE_LATITUDE, TestConstants.LILLE_LONGITUDE);

        List<Attraction> attractionList = new ArrayList<>();
        attractionList.add(attraction1);
        attractionList.add(attraction2);
        attractionList.add(attraction3);

        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction2, new Date()));

        when(gpsUtilMock.getAttractions()).thenReturn(attractionList);
        when(rewardCentralMock.getAttractionRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(100);

        rewardsService.calculateRewards(user);
        List<UserReward> userRewards = user.getUserRewards();

        assertEquals(1, userRewards.size());
        assertEquals(attraction2.latitude, userRewards.get(0).visitedLocation.location.latitude);
        assertEquals(attraction2.attractionName, userRewards.get(0).attraction.attractionName);
        assertEquals(100, userRewards.get(0).getRewardPoints());

        verify(gpsUtilMock, Mockito.times(1)).getAttractions();
        verify(rewardCentralMock, Mockito.times(1))
                .getAttractionRewardPoints(any(UUID.class), any(UUID.class));

    }

    @Test
    public void isWithinAttractionProximity() {
        //GpsUtil gpsUtil = new GpsUtil();
        rewardsService = new RewardsService(gpsUtilMock, rewardCentralMock);
        Attraction attraction1 = new Attraction("MoMA", "New York City",
                "New York", TestConstants.NYC_LATITUDE, TestConstants.NYC_LONGITUDE);
        assertTrue(rewardsService.isWithinAttractionProximity(attraction1, attraction1));

        Attraction attraction2 = new Attraction("Orsay", "Paris",
                "France", TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE);
        assertFalse(rewardsService.isWithinAttractionProximity(attraction2, attraction1));
    }

    @Test
    // TOASK plus un test d'intégration ? ==> à mettre dans une autre classe IT ou à passer en TU ?
    public void nearAllAttractions() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, new TripPricer());

        rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
        List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
        tourGuideService.tracker.stopTracking();

        assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
    }

}
