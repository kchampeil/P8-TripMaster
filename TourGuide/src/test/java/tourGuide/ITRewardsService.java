package tourGuide;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserPreferencesService;
import tourGuide.service.contracts.IGpsUtilAPIRequestService;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ITRewardsService {

    @Autowired
    private IGpsUtilAPIRequestService gpsUtilAPIRequestService;

    @Test
    public void nearAllAttractions() {

        RewardsService rewardsService = new RewardsService(gpsUtilAPIRequestService, new RewardCentral());
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilAPIRequestService, rewardsService, new TripPricer(), new UserPreferencesService());

        rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
        List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0).getUserName());
        tourGuideService.tracker.stopTracking();

        assertEquals(gpsUtilAPIRequestService.getAttractions().size(), userRewards.size());
    }

    @Test
    public void getTripDealsUsingUserPreferences() {

        RewardsService rewardsService = new RewardsService(gpsUtilAPIRequestService, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilAPIRequestService, rewardsService, new TripPricer(), new UserPreferencesService());

        User user = tourGuideService.getAllUsers().get(0);

        List<Provider> providers = tourGuideService.getTripDeals(user.getUserName());

        assertEquals(5, providers.size());
    }

}
