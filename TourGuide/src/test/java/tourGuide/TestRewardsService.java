package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import rewardCentral.RewardCentral;
import tourGuide.constants.TestConstants;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

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

    @Autowired
    private RewardsService rewardsService;

    @MockBean
    private GpsUtil gpsUtilMock;

    @MockBean
    private RewardCentral rewardCentralMock;

    @MockBean
    private TourGuideService tourGuideServiceMock;

    @BeforeAll
    public static void setUp() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void userGetRewards() {

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

        Attraction attraction1 = new Attraction("MoMA", "New York City",
                "New York", TestConstants.NYC_LATITUDE, TestConstants.NYC_LONGITUDE);
        assertTrue(rewardsService.isWithinAttractionProximity(attraction1, attraction1));

        Attraction attraction2 = new Attraction("Orsay", "Paris",
                "France", TestConstants.PARIS_LATITUDE, TestConstants.PARIS_LONGITUDE);
        assertFalse(rewardsService.isWithinAttractionProximity(attraction2, attraction1));
    }

}
