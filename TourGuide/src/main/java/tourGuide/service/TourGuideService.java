package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.dto.CurrentLocationDto;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.model.AttractionBean;
import tourGuide.model.LocationBean;
import tourGuide.model.ProviderBean;
import tourGuide.model.VisitedLocationBean;
import tourGuide.service.contracts.IGpsUtilAPIRequestService;
import tourGuide.service.contracts.ITripPricerAPIRequestService;
import tourGuide.service.contracts.IUserPreferencesService;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static tourGuide.constants.TourGuideExceptionConstants.USER_DOES_NOT_EXIST;

@Service
public class TourGuideService {
    private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final IGpsUtilAPIRequestService gpsUtilAPIRequestService;
    private final RewardsService rewardsService;
    private final ITripPricerAPIRequestService tripPricerAPIRequestService;
    private final IUserPreferencesService userPreferencesService;
    public final Tracker tracker;
    boolean testMode = true;

    private final ExecutorService tourGuideExecutorService = Executors.newFixedThreadPool(20);
    private final ExecutorCompletionService<VisitedLocationBean> completionService
            = new ExecutorCompletionService<>(tourGuideExecutorService);

    private static final String TRIP_PRICER_API_KEY = "test-server-api-key";
    // Database connection will be used for external users,
    // but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    public TourGuideService(IGpsUtilAPIRequestService gpsUtilAPIRequestService,
                            RewardsService rewardsService,
                            ITripPricerAPIRequestService tripPricerAPIRequestService,
                            IUserPreferencesService userPreferencesService) {
        this.gpsUtilAPIRequestService = gpsUtilAPIRequestService;
        this.rewardsService = rewardsService;
        this.tripPricerAPIRequestService = tripPricerAPIRequestService;
        this.userPreferencesService = userPreferencesService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            InternalTestingInitializationService.initializeInternalUsers(internalUserMap);
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(String userName) {
        User user = this.getUser(userName);
        return user.getUserRewards();
    }

    public VisitedLocationBean getUserLocation(String userName) {
        User user = this.getUser(userName);
        return (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
    }

    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<ProviderBean> getTripDeals(String userName) {
        User user = this.getUser(userName);

        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

        List<ProviderBean> providers = tripPricerAPIRequestService.getPrice(TRIP_PRICER_API_KEY, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    /**
     * track user's location by getting his current location, adding it to his visited location list
     * and calculating corresponding rewards
     *
     * @param user the user whom we want to track the location
     * @return the current visited location of the user
     */
    public VisitedLocationBean trackUserLocation(User user) {
        VisitedLocationBean visitedLocation = gpsUtilAPIRequestService.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    /**
     * track user's location for all users of a list in multi-thread mode
     *
     * @param userList the list of users whom we want to track the location
     */
    public void trackUserLocationForUserList(List<User> userList) {

        for (User user : userList) {
            completionService.submit(() -> trackUserLocation(user));
        }

        try {
            for (int i = 0; i < userList.size(); i++) {
                Future<VisitedLocationBean> future = completionService.take();
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error while tracking users", e.getCause());
        }

    }

    public void shutDownTourGuideExecutorService() {
        tourGuideExecutorService.shutdown();
        try {
            if (!tourGuideExecutorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                tourGuideExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            tourGuideExecutorService.shutdownNow();
        }
    }

    /**
     * Get the closest five tourist attractions to the user - no matter how far away they are
     *
     * @param userName of the user we want to find the closest attractions
     * @return a list of NearByAttractionDto
     */
    public List<NearByAttractionDto> getNearByAttractions(String userName) {
        VisitedLocationBean visitedLocation = this.getUserLocation(userName);
        List<AttractionBean> nearbyAttractions = gpsUtilAPIRequestService.getAttractions()
                .stream()
                .sorted(Comparator.comparingDouble(attraction ->
                        rewardsService.getDistance(attraction, visitedLocation.location)))
                .limit(5)
                .collect(Collectors.toList());

        List<NearByAttractionDto> nearByAttractionDtoList = new ArrayList<>();

        for (AttractionBean attraction : nearbyAttractions) {
            NearByAttractionDto nearByAttractionDTO = new NearByAttractionDto();
            nearByAttractionDTO.setAttractionName(attraction.attractionName);
            nearByAttractionDTO.setAttractionLocation(new LocationBean(attraction.latitude, attraction.longitude));
            nearByAttractionDTO.setUserLocation(visitedLocation.location);
            nearByAttractionDTO.setAttractionDistanceFromUser(
                    rewardsService.getDistance(attraction, visitedLocation.location));
            nearByAttractionDTO.setAttractionRewardsPoint(
                    rewardsService.getRewardPoints(attraction, visitedLocation.userId));

            nearByAttractionDtoList.add(nearByAttractionDTO);
        }
        return nearByAttractionDtoList;
    }

    /**
     * Set the user preferences for a user (userName included in the UserPreferencesDto)
     *
     * @param userPreferencesDto userName and other user preferences
     * @return a UserPreferencesDto object with information from the updated UserPreferences
     * @throws Exception if no user exists for the given userName
     */
    public UserPreferencesDto setUserPreferences(UserPreferencesDto userPreferencesDto) throws Exception {

        User user = this.getUser(userPreferencesDto.getUserName());

        if (user != null) {

            UserPreferences userPreferences = userPreferencesService.getUserPreferencesFromUserPreferencesDto(userPreferencesDto);
            user.setUserPreferences(userPreferences);
            logger.info("User preferences set for: " + userPreferencesDto.getUserName());

            //return the updated data
            UserPreferencesDto userPreferencesDtoReturned =
                    userPreferencesService.getUserPreferencesDtoFromUserPreferences(user.getUserPreferences());
            userPreferencesDtoReturned.setUserName(user.getUserName());

            return userPreferencesDtoReturned;

        } else {
            throw new Exception(USER_DOES_NOT_EXIST);
        }
    }

    /**
     * Get a list of every user's most recent location gathered from their stored location history
     *
     * @return the list of every user's most recent location
     */
    public List<CurrentLocationDto> getAllCurrentLocations() {
        List<CurrentLocationDto> allCurrentLocationsDto = new ArrayList<>();
        this.getAllUsers().stream()
                .filter(user -> user.getLastVisitedLocation() != null)
                .forEach(user -> allCurrentLocationsDto
                        .add(new CurrentLocationDto(user.getUserId().toString(), user.getLastVisitedLocation().location)));

        return allCurrentLocationsDto;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }
}
