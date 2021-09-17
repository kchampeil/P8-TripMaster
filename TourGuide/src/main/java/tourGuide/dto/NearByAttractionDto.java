package tourGuide.dto;

import lombok.Getter;
import lombok.Setter;
import tourGuide.model.LocationBean;

@Getter
@Setter
public class NearByAttractionDto {
    private String attractionName; // Name of Tourist attraction
    private LocationBean attractionLocation; // Tourist attractions lat/long,
    private LocationBean userLocation; // The user's location lat/long,
    private double attractionDistanceFromUser; // The distance in miles between the user's location and the attraction
    private int attractionRewardsPoint; // The reward points for visiting the attraction

}