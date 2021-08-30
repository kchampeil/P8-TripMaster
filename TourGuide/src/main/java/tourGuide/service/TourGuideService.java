package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.dto.CurrentLocationDto;
import tourGuide.dto.NearByAttractionDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import javax.money.CurrencyUnit;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static tourGuide.constants.TourGuideExceptionConstants.USER_DOES_NOT_EXIST;

@Service
public class TourGuideService {
    private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer;
    private final IUserPreferencesService userPreferencesService;
    public final Tracker tracker;
    boolean testMode = true;

    public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService, TripPricer tripPricer, IUserPreferencesService userPreferencesService) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;
        this.tripPricer = tripPricer;
        this.userPreferencesService = userPreferencesService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(String userName) {
        User user = this.getUser(userName);
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(String userName) {
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

    public List<Provider> getTripDeals(String userName) {
        User user = this.getUser(userName);

        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

        List<Provider> providers = tripPricer.getPrice(TRIP_PRICER_API_KEY, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    /**
     * Get the closest five tourist attractions to the user - no matter how far away they are
     *
     * @param userName of the user we want to find the closest attractions
     * @return a list of NearByAttractionDto
     */
    public List<NearByAttractionDto> getNearByAttractions(String userName) {
        VisitedLocation visitedLocation = this.getUserLocation(userName);
        List<Attraction> nearbyAttractions = gpsUtil.getAttractions()
                .stream()
                .sorted(Comparator.comparingDouble(attraction ->
                        rewardsService.getDistance(attraction, visitedLocation.location)))
                .limit(5)
                .collect(Collectors.toList());

        List<NearByAttractionDto> nearByAttractionDtoList = new ArrayList<>();

        for (Attraction attraction : nearbyAttractions) {
            NearByAttractionDto nearByAttractionDTO = new NearByAttractionDto();
            nearByAttractionDTO.setAttractionName(attraction.attractionName);
            nearByAttractionDTO.setAttractionLocation(new Location(attraction.latitude, attraction.longitude));
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

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String TRIP_PRICER_API_KEY = "test-server-api-key";
    private static final Random RANDOM = new Random();
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);
            generateUserPreferences(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                    new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + RANDOM.nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + RANDOM.nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    private void generateUserPreferences(User user) {
        UserPreferences userPreferences = new UserPreferences();

        userPreferences.setAttractionProximity(100 + RANDOM.nextInt(Integer.MAX_VALUE - 100));
        userPreferences.setLowerPricePoint(getRandomPrice(10, userPreferences.getCurrency()));
        userPreferences.setHighPricePoint(
                userPreferences.getLowerPricePoint().add(getRandomPrice(10000, userPreferences.getCurrency())));
        userPreferences.setTripDuration(RANDOM.nextInt(45));
        userPreferences.setNumberOfAdults(1 + RANDOM.nextInt(3));
        userPreferences.setNumberOfChildren(RANDOM.nextInt(8));
        userPreferences.setTicketQuantity(userPreferences.getNumberOfAdults() + userPreferences.getNumberOfChildren());

        user.setUserPreferences(userPreferences);
    }

    private Money getRandomPrice(int max, CurrencyUnit currencyUnit) {
        return Money.of(RANDOM.nextInt(max), currencyUnit);
    }

}
