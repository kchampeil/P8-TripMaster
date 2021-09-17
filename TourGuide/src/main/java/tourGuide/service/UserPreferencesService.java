package tourGuide.service;

import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.service.contracts.IUserPreferencesService;
import tourGuide.user.UserPreferences;

import javax.money.Monetary;

@Service
public class UserPreferencesService implements IUserPreferencesService {

    @Override
    public UserPreferencesDto getUserPreferencesDtoFromUserPreferences(UserPreferences userPreferences) {

        UserPreferencesDto userPreferencesDto = new UserPreferencesDto();
        userPreferencesDto.setTicketQuantity(userPreferences.getTicketQuantity());
        userPreferencesDto.setTripDuration(userPreferences.getTripDuration());
        userPreferencesDto.setNumberOfAdults(userPreferences.getNumberOfAdults());
        userPreferencesDto.setNumberOfChildren(userPreferences.getNumberOfChildren());
        userPreferencesDto.setAttractionProximity(userPreferences.getAttractionProximity());
        userPreferencesDto.setCurrency(userPreferences.getCurrency().getCurrencyCode());
        userPreferencesDto.setHighPricePoint(userPreferences.getHighPricePoint().getNumber().intValue());
        userPreferencesDto.setLowerPricePoint(userPreferences.getLowerPricePoint().getNumber().intValue());

        return userPreferencesDto;
    }

    @Override
    public UserPreferences getUserPreferencesFromUserPreferencesDto(UserPreferencesDto userPreferencesDto) {

        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setTicketQuantity(userPreferencesDto.getTicketQuantity());
        userPreferences.setTripDuration(userPreferencesDto.getTripDuration());
        userPreferences.setNumberOfAdults(userPreferencesDto.getNumberOfAdults());
        userPreferences.setNumberOfChildren(userPreferencesDto.getNumberOfChildren());
        userPreferences.setAttractionProximity(userPreferencesDto.getAttractionProximity());
        userPreferences.setCurrency(Monetary.getCurrency(userPreferencesDto.getCurrency()));
        userPreferences.setHighPricePoint(Money.of(userPreferencesDto.getHighPricePoint(), userPreferences.getCurrency()));
        userPreferences.setLowerPricePoint(Money.of(userPreferencesDto.getLowerPricePoint(), userPreferences.getCurrency()));

        return userPreferences;
    }
}
