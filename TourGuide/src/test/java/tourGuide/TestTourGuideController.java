package tourGuide;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tourGuide.dto.CurrentLocationDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.model.LocationBean;
import tourGuide.service.TourGuideService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tourGuide.constants.TestConstants.LILLE_LATITUDE;
import static tourGuide.constants.TestConstants.LILLE_LONGITUDE;
import static tourGuide.constants.TestConstants.NYC_LATITUDE;
import static tourGuide.constants.TestConstants.NYC_LONGITUDE;
import static tourGuide.constants.TestConstants.PARIS_LATITUDE;
import static tourGuide.constants.TestConstants.PARIS_LONGITUDE;
import static tourGuide.constants.TourGuideExceptionConstants.INVALID_INPUT;
import static tourGuide.constants.TourGuideExceptionConstants.USER_DOES_NOT_EXIST;

@WebMvcTest(controllers = TourGuideController.class)
class TestTourGuideController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourGuideService tourGuideServiceMock;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static UserPreferencesDto userPreferencesDto;

    @BeforeAll
    public static void setUp() {

        userPreferencesDto = new UserPreferencesDto();
        userPreferencesDto.setAttractionProximity(10);
        userPreferencesDto.setNumberOfChildren(3);
        userPreferencesDto.setNumberOfAdults(2);
        userPreferencesDto.setTripDuration(10);
        userPreferencesDto.setTicketQuantity(5);
        userPreferencesDto.setCurrency("USD");
        userPreferencesDto.setLowerPricePoint(5);
        userPreferencesDto.setHighPricePoint(100);
    }

    @Test
    void setUserPreferences_WithSuccess() throws Exception {

        userPreferencesDto.setUserName("jon");

        when(tourGuideServiceMock.setUserPreferences(any(UserPreferencesDto.class)))
                .thenReturn(userPreferencesDto);

        mockMvc.perform(post("/userPreferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPreferencesDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(tourGuideServiceMock, Mockito.times(1)).setUserPreferences(any(UserPreferencesDto.class));

    }

    @Test
    void setUserPreferences_WithMissingUserName() throws Exception {

        userPreferencesDto.setUserName(null);

        mockMvc.perform(post("/userPreferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPreferencesDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result
                        .getResolvedException()).getMessage().contains(INVALID_INPUT)));

        verify(tourGuideServiceMock, Mockito.times(0)).setUserPreferences(userPreferencesDto);

    }

    @Test
    void setUserPreferences_WithUnknownUser() throws Exception {

        userPreferencesDto.setUserName("john");

        when(tourGuideServiceMock.setUserPreferences(any(UserPreferencesDto.class)))
                .thenThrow(new Exception(USER_DOES_NOT_EXIST));

        mockMvc.perform(post("/userPreferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPreferencesDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result
                        .getResolvedException()).getMessage().contains(USER_DOES_NOT_EXIST)));

        verify(tourGuideServiceMock, Mockito.times(1)).setUserPreferences(any(UserPreferencesDto.class));
    }

    @Test
    void getAllCurrentLocations_WithSuccess() throws Exception {
        CurrentLocationDto currentLocationDto1 =
                new CurrentLocationDto("019b04a9-067a-4c76-8817-ee75088c3822", new LocationBean(PARIS_LATITUDE, PARIS_LONGITUDE));
        CurrentLocationDto currentLocationDto2 =
                new CurrentLocationDto("019b04a9-067a-4c76-8817-ee75088c3823", new LocationBean(LILLE_LATITUDE, LILLE_LONGITUDE));
        CurrentLocationDto currentLocationDto3 =
                new CurrentLocationDto("019b04a9-067a-4c76-8817-ee75088c3823", new LocationBean(NYC_LATITUDE, NYC_LONGITUDE));
        List<CurrentLocationDto> allCurrentLocationsDto = new ArrayList<>();
        allCurrentLocationsDto.add(currentLocationDto1);
        allCurrentLocationsDto.add(currentLocationDto2);
        allCurrentLocationsDto.add(currentLocationDto3);

        when(tourGuideServiceMock.getAllCurrentLocations()).thenReturn(allCurrentLocationsDto);

        mockMvc.perform(get("/getAllCurrentLocations"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.[0].userId").value(currentLocationDto1.getUserId()))
                .andExpect(jsonPath("$.[0].location.latitude").value(currentLocationDto1.getLocation().latitude))
                .andExpect(jsonPath("$.[0].location.longitude").value(currentLocationDto1.getLocation().longitude))
                .andExpect(jsonPath("$.[1].userId").value(currentLocationDto2.getUserId()))
                .andExpect(jsonPath("$.[2].userId").value(currentLocationDto3.getUserId()));

        verify(tourGuideServiceMock, Mockito.times(1)).getAllCurrentLocations();
    }
}