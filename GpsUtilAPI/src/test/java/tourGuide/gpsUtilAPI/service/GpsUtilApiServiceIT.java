package tourGuide.gpsUtilAPI.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tourGuide.gpsUtilAPI.constants.TestConstants.ATTRACTION_LIST_SIZE_IN_LIB;

@SpringBootTest
class GpsUtilApiServiceIT {

    @Autowired
    private IGpsUtilApiService gpsUtilApiService;

    @Test
    void getUserLocation() {

        UUID userId = UUID.randomUUID();
        VisitedLocation visitedLocation = gpsUtilApiService.getUserLocation(userId);

        assertEquals(userId, visitedLocation.userId);
        assertNotNull(visitedLocation.location);
        assertNotNull(visitedLocation.timeVisited);
    }

    @Test
    void getAttractions() {

        List<Attraction> attractionList = gpsUtilApiService.getAttractions();

        assertEquals(ATTRACTION_LIST_SIZE_IN_LIB, attractionList.size());
    }

}