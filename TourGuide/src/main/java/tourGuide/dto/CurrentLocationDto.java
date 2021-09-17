package tourGuide.dto;

import lombok.Getter;
import lombok.Setter;
import tourGuide.model.LocationBean;

@Getter
@Setter
public class CurrentLocationDto {

    private String userId;

    private LocationBean location;

    public CurrentLocationDto(String userId, LocationBean location) {
        this.userId = userId;
        this.location = location;
    }
}
