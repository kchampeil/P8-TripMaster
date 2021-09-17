package tourGuide.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.model.AttractionBean;
import tourGuide.model.LocationBean;
import tourGuide.model.VisitedLocationBean;
import tourGuide.service.contracts.IGpsUtilAPIRequestService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private static final int DEFAULT_PROXIMITY_BUFFER = 10;
    private int proximityBuffer = DEFAULT_PROXIMITY_BUFFER;
    private static final int ATTRACTION_PROXIMITY_RANGE = 200;

    private final IGpsUtilAPIRequestService gpsUtilAPIRequestService;
    private final RewardCentral rewardsCentral;

    private final ExecutorService rewardsExecutorService = Executors.newFixedThreadPool(60);

    public RewardsService(IGpsUtilAPIRequestService gpsUtilAPIRequestService, RewardCentral rewardCentral) {
        this.gpsUtilAPIRequestService = gpsUtilAPIRequestService;
        this.rewardsCentral = rewardCentral;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = DEFAULT_PROXIMITY_BUFFER;
    }

    /**
     * Calculate rewards for a given user
     * One reward is attributed only one time for each attraction if user's visited location is near the attraction
     *
     * @param user user whom we want to calculate the rewards
     */
    public void calculateRewards(User user) {
        List<VisitedLocationBean> userLocations = new CopyOnWriteArrayList<>(user.getVisitedLocations());
        List<AttractionBean> attractions = new CopyOnWriteArrayList<>(gpsUtilAPIRequestService.getAttractions());

        for (VisitedLocationBean visitedLocation : userLocations) {
            for (AttractionBean attraction : attractions) {
                if (user.getUserRewards().stream()
                        .noneMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName))) {
                    if (nearAttraction(visitedLocation, attraction)) {
                        user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user.getUserId())));
                    }
                }
            }
        }
    }

    /**
     * calculate rewards for all users of a list in multi-thread mode
     *
     * @param userList the list of users whom we want to calculate rewards
     */
    public void calculateRewardsForUserList(List<User> userList) {

        for (User user : userList) {
            Future<?> future = rewardsExecutorService.submit(() -> calculateRewards(user));

            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error while calculating rewards", e.getCause());
            }
        }
    }

    public void shutDownRewardsExecutorService() {
        rewardsExecutorService.shutdown();
        try {
            if (!rewardsExecutorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                rewardsExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            rewardsExecutorService.shutdownNow();
        }
    }

    public boolean isWithinAttractionProximity(AttractionBean attraction, LocationBean location) {
        return !(getDistance(attraction, location) > ATTRACTION_PROXIMITY_RANGE);
    }

    private boolean nearAttraction(VisitedLocationBean visitedLocation, AttractionBean attraction) {
        return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
    }

    public int getRewardPoints(AttractionBean attraction, UUID userId) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userId);
    }

    public double getDistance(LocationBean loc1, LocationBean loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }

}
