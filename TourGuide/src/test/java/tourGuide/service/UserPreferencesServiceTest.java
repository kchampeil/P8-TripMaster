package tourGuide.service;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.user.UserPreferences;

import javax.money.Monetary;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserPreferencesServiceTest {

    private static UserPreferences userPreferences;
    private static UserPreferencesDto userPreferencesDto;
    private static UserPreferencesService userPreferencesService;

    @BeforeAll
    public static void setUp() {
        userPreferences = new UserPreferences();
        userPreferences.setAttractionProximity(10);
        userPreferences.setNumberOfChildren(3);
        userPreferences.setNumberOfAdults(2);
        userPreferences.setTripDuration(10);
        userPreferences.setTicketQuantity(5);
        userPreferences.setCurrency(Monetary.getCurrency("USD"));
        userPreferences.setLowerPricePoint(Money.of(5, userPreferences.getCurrency()));
        userPreferences.setHighPricePoint(Money.of(100, userPreferences.getCurrency()));

        userPreferencesDto = new UserPreferencesDto();
        userPreferencesDto.setAttractionProximity(userPreferences.getAttractionProximity());
        userPreferencesDto.setNumberOfChildren(userPreferences.getNumberOfChildren());
        userPreferencesDto.setNumberOfAdults(userPreferences.getNumberOfAdults());
        userPreferencesDto.setTripDuration(userPreferences.getTripDuration());
        userPreferencesDto.setTicketQuantity(userPreferences.getTicketQuantity());
        userPreferencesDto.setCurrency(userPreferences.getCurrency().toString());
        userPreferencesDto.setLowerPricePoint(userPreferences.getLowerPricePoint().getNumber().intValue());
        userPreferencesDto.setHighPricePoint(userPreferences.getHighPricePoint().getNumber().intValue());

        userPreferencesService = new UserPreferencesService();
    }

    @Test
    void getUserPreferencesDtoFromUserPreferences() {
        userPreferencesService.getUserPreferencesDtoFromUserPreferences(userPreferences);
        assertEquals(userPreferencesDto.getCurrency(), userPreferences.getCurrency().toString());
        assertEquals(userPreferencesDto.getNumberOfAdults(), userPreferences.getNumberOfAdults());
        assertEquals(userPreferencesDto.getLowerPricePoint(), userPreferences.getLowerPricePoint().getNumber().intValue());
    }

    @Test
    void getUserPreferencesFromUserPreferencesDto() {
        userPreferencesService.getUserPreferencesFromUserPreferencesDto(userPreferencesDto);
        assertEquals(userPreferences.getCurrency().toString(), userPreferencesDto.getCurrency());
        assertEquals(userPreferences.getNumberOfAdults(), userPreferencesDto.getNumberOfAdults());
        assertEquals(userPreferences.getLowerPricePoint().getNumber().intValue(), userPreferencesDto.getLowerPricePoint());
    }
}