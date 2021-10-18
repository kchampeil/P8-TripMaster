package tourGuide.gpsUtilAPI.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import java.util.List;
import java.util.UUID;

public interface IGpsUtilApiService {
    VisitedLocation getUserLocation(UUID userId);

    List<Attraction> getAttractions();
}
