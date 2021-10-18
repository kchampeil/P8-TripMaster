package tourGuide.gpsUtilAPI.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tourGuide.gpsUtilAPI.constants.TestConstants.NYC_LATITUDE;
import static tourGuide.gpsUtilAPI.constants.TestConstants.NYC_LONGITUDE;
import static tourGuide.gpsUtilAPI.constants.TestConstants.PARIS_LATITUDE;
import static tourGuide.gpsUtilAPI.constants.TestConstants.PARIS_LONGITUDE;

@SpringBootTest
class GpsUtilApiServiceTest {

    @Autowired
    private IGpsUtilApiService gpsUtilApiService;

    @MockBean
    private GpsUtil gpsUtilMock;

    @Test
    void getUserLocation() {
        UUID userId = UUID.randomUUID();
        VisitedLocation visitedLocationExpected =
                new VisitedLocation(userId, new Location(PARIS_LATITUDE, PARIS_LONGITUDE), Date.from(Instant.now()));
        when(gpsUtilMock.getUserLocation(userId)).thenReturn(visitedLocationExpected);

        VisitedLocation visitedLocationActual = gpsUtilApiService.getUserLocation(userId);
        assertEquals(visitedLocationExpected, visitedLocationActual);
    }

    @Test
    void getAttractions() {
        Attraction attraction1 = new Attraction("MoMA", "New York City",
                "New York", NYC_LATITUDE, NYC_LONGITUDE);
        Attraction attraction2 = new Attraction("Louvre", "Paris",
                "France", PARIS_LATITUDE, PARIS_LONGITUDE);
        List<Attraction> attractionListExpected = new ArrayList<>();
        attractionListExpected.add(attraction1);
        attractionListExpected.add(attraction2);
        when(gpsUtilMock.getAttractions()).thenReturn(attractionListExpected);

        List<Attraction> attractionListActual = gpsUtilApiService.getAttractions();

        assertEquals(attractionListExpected, attractionListActual);

        verify(gpsUtilMock, Mockito.times(1)).getAttractions();
    }
}