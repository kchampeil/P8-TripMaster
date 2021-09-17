package tourGuide.service;

import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.LocationBean;
import tourGuide.model.VisitedLocationBean;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;

import javax.money.CurrencyUnit;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
public class InternalTestingInitializationService {

    private static final Random RANDOM = new Random();

    /**
     * Initialization of users for internal testing
     *
     * @param internalUserMap map of userName/user
     */
    public static Map<String, User> initializeInternalUsers(Map<String, User> internalUserMap) {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);
            generateUserPreferences(user);

            internalUserMap.put(userName, user);
        });
        log.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
        return internalUserMap;
    }

    private static void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocationBean(user.getUserId(),
                    new LocationBean(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private static double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + RANDOM.nextDouble() * (rightLimit - leftLimit);
    }

    private static double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + RANDOM.nextDouble() * (rightLimit - leftLimit);
    }

    private static Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    private static void generateUserPreferences(User user) {
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

    private static Money getRandomPrice(int max, CurrencyUnit currencyUnit) {
        return Money.of(RANDOM.nextInt(max), currencyUnit);
    }
}
