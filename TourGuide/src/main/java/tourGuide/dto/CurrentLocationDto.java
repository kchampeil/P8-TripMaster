package tourGuide.dto;

import gpsUtil.location.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentLocationDto {

    private String userId;
    
    private Location location;

    public CurrentLocationDto(String userId, Location location) {
        this.userId = userId;
        this.location = location;
    }
}
