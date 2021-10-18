package tourGuide.gpsUtilAPI.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class GpsUtilApiService implements IGpsUtilApiService {

    @Autowired
    private GpsUtil gpsUtil;

    /**
     * Get current location for the user corresponding to the given userId
     *
     * @param userId whom current location we want to get
     * @return the current location (visitedLocation)
     */
    @Override
    public VisitedLocation getUserLocation(UUID userId) {
        log.debug("Service getUserLocation called for userId: " + userId);
        return gpsUtil.getUserLocation(userId);
    }

    /**
     * Get a list with all attractions
     * @return a list with all attractions
     */
    @Override
    public List<Attraction> getAttractions() {
        log.debug("Service getAttractions called");
        return gpsUtil.getAttractions();
    }
}
