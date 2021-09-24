package tourGuide.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.ProviderBean;
import tourGuide.service.contracts.IGpsUtilAPIRequestService;
import tourGuide.service.contracts.IRewardCentralAPIRequestService;
import tourGuide.service.contracts.ITripPricerAPIRequestService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ITRewardsService {

    @Autowired
    private IGpsUtilAPIRequestService gpsUtilAPIRequestService;

    @Autowired
    private ITripPricerAPIRequestService tripPricerAPIRequestService;

    @Autowired
    private IRewardCentralAPIRequestService rewardCentralAPIRequestService;

    @Test
    public void nearAllAttractions() {

        RewardsService rewardsService = new RewardsService(gpsUtilAPIRequestService, rewardCentralAPIRequestService);
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService =
                new TourGuideService(gpsUtilAPIRequestService, rewardsService,
                        tripPricerAPIRequestService, new UserPreferencesService());

        rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
        List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0).getUserName());
        tourGuideService.tracker.stopTracking();

        assertEquals(gpsUtilAPIRequestService.getAttractions().size(), userRewards.size());
    }

    @Test
    public void getTripDealsUsingUserPreferences() {

        RewardsService rewardsService = new RewardsService(gpsUtilAPIRequestService, rewardCentralAPIRequestService);
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilAPIRequestService, rewardsService,
                tripPricerAPIRequestService, new UserPreferencesService());

        User user = tourGuideService.getAllUsers().get(0);

        List<ProviderBean> providers = tourGuideService.getTripDeals(user.getUserName());

        assertEquals(5, providers.size());
    }

}
