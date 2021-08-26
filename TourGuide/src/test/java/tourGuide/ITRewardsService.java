package tourGuide;

import gpsUtil.GpsUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ITRewardsService {

    @Test
    public void nearAllAttractions() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, new TripPricer());

        rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
        List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0).getUserName());
        tourGuideService.tracker.stopTracking();

        assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
    }

    @Test
    public void getTripDealsUsingUserPreferences() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, new TripPricer());

        User user = tourGuideService.getAllUsers().get(0);
        double lowerPrice = user.getUserPreferences().getLowerPricePoint().getNumber().doubleValue();
        double higherPrice = user.getUserPreferences().getHighPricePoint().getNumber().doubleValue();

        List<Provider> providers = tourGuideService.getTripDeals(user.getUserName());

        assertEquals(5, providers.size());
        IntStream.range(0, 5).forEach(i -> {
            assertTrue(lowerPrice <= providers.get(i).price);
            assertTrue(higherPrice >= providers.get(i).price);
        });

    }

}
