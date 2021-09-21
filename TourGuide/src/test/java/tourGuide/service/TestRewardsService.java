package tourGuide.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tourGuide.model.AttractionBean;
import tourGuide.model.VisitedLocationBean;
import tourGuide.service.contracts.IGpsUtilAPIRequestService;
import tourGuide.service.contracts.IRewardCentralAPIRequestService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tourGuide.constants.TestConstants.LILLE_LATITUDE;
import static tourGuide.constants.TestConstants.LILLE_LONGITUDE;
import static tourGuide.constants.TestConstants.NYC_LATITUDE;
import static tourGuide.constants.TestConstants.NYC_LONGITUDE;
import static tourGuide.constants.TestConstants.PARIS_LATITUDE;
import static tourGuide.constants.TestConstants.PARIS_LONGITUDE;

@SpringBootTest
public class TestRewardsService {

    @Autowired
    private RewardsService rewardsService;

    @MockBean
    private IGpsUtilAPIRequestService gpsUtilAPIRequestService;

    @MockBean
    private IRewardCentralAPIRequestService rewardCentralAPIRequestServiceMock;

    @MockBean
    private TourGuideService tourGuideServiceMock;

    @Test
    public void userGetRewards() {

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        AttractionBean attraction1 = new AttractionBean("MoMA", "New York City",
                "New York", NYC_LATITUDE, NYC_LONGITUDE);
        AttractionBean attraction2 = new AttractionBean("Orsay", "Paris",
                "France", PARIS_LATITUDE, PARIS_LONGITUDE);
        AttractionBean attraction3 = new AttractionBean("Musée des Beaux-Arts", "Lille",
                "France", LILLE_LATITUDE, LILLE_LONGITUDE);

        List<AttractionBean> attractionList = new ArrayList<>();
        attractionList.add(attraction1);
        attractionList.add(attraction2);
        attractionList.add(attraction3);

        user.addToVisitedLocations(new VisitedLocationBean(user.getUserId(), attraction2, new Date()));

        when(gpsUtilAPIRequestService.getAttractions()).thenReturn(attractionList);
        when(rewardCentralAPIRequestServiceMock.getAttractionRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(100);

        rewardsService.calculateRewards(user);
        List<UserReward> userRewards = user.getUserRewards();

        assertEquals(1, userRewards.size());
        assertEquals(attraction2.latitude, userRewards.get(0).visitedLocation.location.latitude);
        assertEquals(attraction2.attractionName, userRewards.get(0).attraction.attractionName);
        assertEquals(100, userRewards.get(0).getRewardPoints());

        verify(gpsUtilAPIRequestService, Mockito.times(1)).getAttractions();
        verify(rewardCentralAPIRequestServiceMock, Mockito.times(1))
                .getAttractionRewardPoints(any(UUID.class), any(UUID.class));

    }

    @Test
    public void isWithinAttractionProximity() {

        AttractionBean attraction1 = new AttractionBean("MoMA", "New York City",
                "New York", NYC_LATITUDE, NYC_LONGITUDE);
        assertTrue(rewardsService.isWithinAttractionProximity(attraction1, attraction1));

        AttractionBean attraction2 = new AttractionBean("Orsay", "Paris",
                "France", PARIS_LATITUDE, PARIS_LONGITUDE);
        assertFalse(rewardsService.isWithinAttractionProximity(attraction2, attraction1));
    }

    @Test
    public void getRewardsForUserList() {

        User user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        AttractionBean attraction1 = new AttractionBean("MoMA", "New York City",
                "New York", NYC_LATITUDE, NYC_LONGITUDE);
        AttractionBean attraction2 = new AttractionBean("Orsay", "Paris",
                "France", PARIS_LATITUDE, PARIS_LONGITUDE);
        AttractionBean attraction3 = new AttractionBean("Musée des Beaux-Arts", "Lille",
                "France", LILLE_LATITUDE, LILLE_LONGITUDE);

        List<AttractionBean> attractionList = new ArrayList<>();
        attractionList.add(attraction1);
        attractionList.add(attraction2);
        attractionList.add(attraction3);

        user1.addToVisitedLocations(new VisitedLocationBean(user1.getUserId(), attraction2, new Date()));
        user2.addToVisitedLocations(new VisitedLocationBean(user2.getUserId(), attraction1, new Date()));

        List<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);

        when(gpsUtilAPIRequestService.getAttractions()).thenReturn(attractionList);
        when(rewardCentralAPIRequestServiceMock.getAttractionRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(100);

        rewardsService.calculateRewardsForUserList(allUsers);

        for (User user : allUsers) {
            List<UserReward> userRewards = user.getUserRewards();
            assertEquals(1, userRewards.size());
            assertEquals(100, userRewards.get(0).getRewardPoints());
        }

        verify(gpsUtilAPIRequestService, Mockito.times(allUsers.size())).getAttractions();
        verify(rewardCentralAPIRequestServiceMock, Mockito.times(allUsers.size()))
                .getAttractionRewardPoints(any(UUID.class), any(UUID.class));

    }
}
