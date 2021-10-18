package tourGuide.gpsUtilAPI.controller;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tourGuide.gpsUtilAPI.controller.GpsUtilApiController;
import tourGuide.gpsUtilAPI.service.GpsUtilApiService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tourGuide.gpsUtilAPI.constants.TestConstants.NYC_LATITUDE;
import static tourGuide.gpsUtilAPI.constants.TestConstants.NYC_LONGITUDE;
import static tourGuide.gpsUtilAPI.constants.TestConstants.PARIS_LATITUDE;
import static tourGuide.gpsUtilAPI.constants.TestConstants.PARIS_LONGITUDE;

@WebMvcTest(controllers = GpsUtilApiController.class)
class GpsUtilApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GpsUtilApiService gpsUtilApiServiceMock;

    @Test
    void getUserLocation() throws Exception {
        UUID userId = UUID.randomUUID();
        VisitedLocation visitedLocation =
                new VisitedLocation(userId, new Location(PARIS_LATITUDE, PARIS_LONGITUDE), Date.from(Instant.now()));
        when(gpsUtilApiServiceMock.getUserLocation(userId)).thenReturn(visitedLocation);

        mockMvc.perform(get("/userLocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", userId.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("userId").value(userId.toString()))
                .andExpect(jsonPath("location.latitude").value(PARIS_LATITUDE))
                .andExpect(jsonPath("location.longitude").value(PARIS_LONGITUDE));

        verify(gpsUtilApiServiceMock, Mockito.times(1)).getUserLocation(userId);
    }

    @Test
    void getAttractions() throws Exception {
        Attraction attraction1 = new Attraction("MoMA", "New York City",
                "New York", NYC_LATITUDE, NYC_LONGITUDE);
        Attraction attraction2 = new Attraction("Louvre", "Paris",
                "France", PARIS_LATITUDE, PARIS_LONGITUDE);
        List<Attraction> attractionList = new ArrayList<>();
        attractionList.add(attraction1);
        attractionList.add(attraction2);
        when(gpsUtilApiServiceMock.getAttractions()).thenReturn(attractionList);

        mockMvc.perform(get("/attractions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[0].attractionName").value("MoMA"))
                .andExpect(jsonPath("$.[0].latitude").value(NYC_LATITUDE))
                .andExpect(jsonPath("$.[0].longitude").value(NYC_LONGITUDE))
                .andExpect(jsonPath("$.[1].latitude").value(PARIS_LATITUDE))
                .andExpect(jsonPath("$.[1].longitude").value(PARIS_LONGITUDE));


        verify(gpsUtilApiServiceMock, Mockito.times(1)).getAttractions();
    }
}