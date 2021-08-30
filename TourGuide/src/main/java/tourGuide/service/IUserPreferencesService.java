package tourGuide.service;

import tourGuide.dto.UserPreferencesDto;
import tourGuide.user.UserPreferences;

public interface IUserPreferencesService {

    public UserPreferencesDto getUserPreferencesDtoFromUserPreferences(UserPreferences userPreferences);

    public UserPreferences getUserPreferencesFromUserPreferencesDto(UserPreferencesDto userPreferencesDto);

}
