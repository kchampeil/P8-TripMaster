package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserPreferencesService;
import tourGuide.user.User;
import tripPricer.TripPricer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tourGuide.constants.TourGuideConstants.THREAD_POOL_SIZE;

public class TestPerformance {

    /*
     * A note on performance improvements:
     *
     *     The number of users generated for the high volume tests can be easily adjusted via this method:
     *
     *     		InternalTestHelper.setInternalUserNumber(100000);
     *
     *
     *     These tests can be modified to suit new solutions, just as long as the performance metrics
     *     at the end of the tests remains consistent.
     *
     *     These are performance metrics that we are trying to hit:
     *
     *     highVolumeTrackLocation: 100,000 users within 15 minutes:
     *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
     *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */

    @BeforeAll
    public static void setUp() {
        Locale.setDefault(Locale.US);
    }

    //@Disabled
    @Test
    public void highVolumeTrackLocation() throws ExecutionException, InterruptedException {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        // Users should be incremented up to 100,000, and test finishes within 15 minutes
        InternalTestHelper.setInternalUserNumber(100000);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService,
                new TripPricer(), new UserPreferencesService());

        List<User> allUsers = tourGuideService.getAllUsers();

        ExecutorService tourGuideExecutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE); //KC

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        /* KC for (User user : allUsers) {
            tourGuideService.trackUserLocation(user);
        }

         */
        List<Future> allFutures = new ArrayList<>();
        for (User user : allUsers) {
            Future<VisitedLocation> visitedLocationFuture = tourGuideExecutorService.submit(
                    () -> tourGuideService.trackUserLocation(user));
            allFutures.add(visitedLocationFuture);
        }

        for (int i = 0; i < allFutures.size(); i++) {
            Future<VisitedLocation> future = allFutures.get(i);

            try {
                VisitedLocation visitedLocation = future.get();
                System.out.println("Visited Loc of future#" + i + " = " + visitedLocation);
            } catch (InterruptedException | ExecutionException e) {
                //TODO
                e.printStackTrace();
            }
        }

        tourGuideExecutorService.shutdown();
        try {
            if (!tourGuideExecutorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                tourGuideExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            tourGuideExecutorService.shutdownNow();
        }

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    //@Disabled
    @Test
    public void highVolumeGetRewards() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        // Users should be incremented up to 100,000, and test finishes within 20 minutes
        InternalTestHelper.setInternalUserNumber(100000);

        ExecutorService rewardsExecutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE); //KC
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, new TripPricer(), new UserPreferencesService());

        Attraction attraction = gpsUtil.getAttractions().get(0);
        List<User> allUsers = tourGuideService.getAllUsers();
        allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

        //KC allUsers.forEach(u -> rewardsService.calculateRewards(u));

        List<Future> allFutures = new ArrayList<>();
        allUsers.forEach(user -> {
            Future future = rewardsExecutorService.submit(() -> rewardsService.calculateRewards(user));
            allFutures.add(future);
        });

        for (int i = 0; i < allFutures.size(); i++) {
            Future future = allFutures.get(i);

            try {
                future.get();
                System.out.println("Rewards of future#" + i);
            } catch (InterruptedException | ExecutionException e) {
                //TODO
                e.printStackTrace();
            }
        }

        rewardsExecutorService.shutdown();
        try {
            if (!rewardsExecutorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                rewardsExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            rewardsExecutorService.shutdownNow();
        }

        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0);
        }
        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

}
