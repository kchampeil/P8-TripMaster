package tourGuide.service;

import tourGuide.dto.UserPreferencesDto;
import tourGuide.user.UserPreferences;

public interface IUserPreferencesService {

    UserPreferencesDto getUserPreferencesDtoFromUserPreferences(UserPreferences userPreferences);

    UserPreferences getUserPreferencesFromUserPreferencesDto(UserPreferencesDto userPreferencesDto);

}
