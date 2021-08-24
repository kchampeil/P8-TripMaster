package tourGuide.dto;

import gpsUtil.location.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearByAttractionDto {
    private String attractionName; // Name of Tourist attraction
    private Location attractionLocation; // Tourist attractions lat/long,
    private Location userLocation; // The user's location lat/long,
    private double attractionDistanceFromUser; // The distance in miles between the user's location and the attraction
    private int attractionRewardsPoint; // The reward points for visiting the attraction

}