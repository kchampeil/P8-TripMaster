package tourGuide.dto;

import lombok.Getter;
import lombok.Setter;
import tourGuide.user.UserPreferences;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserPreferencesDto {

    @NotNull
    private String userName;

    private UserPreferences userPreferences;

    public UserPreferencesDto(String userName, UserPreferences userPreferences) {
        this.userName = userName;
        this.userPreferences = userPreferences;
    }

    public UserPreferencesDto() {
    }
}
