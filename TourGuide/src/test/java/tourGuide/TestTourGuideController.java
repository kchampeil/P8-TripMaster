package tourGuide;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.service.TourGuideService;
import tourGuide.user.UserPreferences;

import javax.money.Monetary;
import java.util.Locale;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tourGuide.constants.TourGuideExceptionConstants.INVALID_INPUT;
import static tourGuide.constants.TourGuideExceptionConstants.USER_DOES_NOT_EXIST;

@WebMvcTest(controllers = TourGuideController.class)
class TestTourGuideController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourGuideService tourGuideServiceMock;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static UserPreferences userPreferences;
    private static UserPreferencesDto userPreferencesDto;

    @BeforeAll
    public static void setUp() {
        Locale.setDefault(Locale.US);

        userPreferences = new UserPreferences();
        userPreferences.setAttractionProximity(10);
        userPreferences.setNumberOfChildren(3);
        userPreferences.setNumberOfAdults(2);
        userPreferences.setTripDuration(10);
        userPreferences.setTicketQuantity(5);
        userPreferences.setCurrency(Monetary.getCurrency("USD"));
        userPreferences.setLowerPricePoint(Money.of(5, userPreferences.getCurrency()));
        userPreferences.setHighPricePoint(Money.of(100, userPreferences.getCurrency()));

        userPreferencesDto = new UserPreferencesDto();
        userPreferencesDto.setAttractionProximity(userPreferences.getAttractionProximity());
        userPreferencesDto.setNumberOfChildren(userPreferences.getNumberOfChildren());
        userPreferencesDto.setNumberOfAdults(userPreferences.getNumberOfAdults());
        userPreferencesDto.setTripDuration(userPreferences.getTripDuration());
        userPreferencesDto.setTicketQuantity(userPreferences.getTicketQuantity());
        userPreferencesDto.setCurrency(userPreferences.getCurrency().toString());
        userPreferencesDto.setLowerPricePoint(userPreferences.getLowerPricePoint().getNumber().intValue());
        userPreferencesDto.setHighPricePoint(userPreferences.getHighPricePoint().getNumber().intValue());
    }

    //@Disabled
    @Test
    void setUserPreferences_WithSuccess() throws Exception {

        userPreferencesDto.setUserName("jon");

        when(tourGuideServiceMock.setUserPreferences(any(UserPreferencesDto.class)))
                .thenReturn(userPreferencesDto);

        mockMvc.perform(post("/userPreferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPreferencesDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //TOASK
                .andExpect(jsonPath("$").isNotEmpty());

        verify(tourGuideServiceMock, Mockito.times(1)).setUserPreferences(any(UserPreferencesDto.class));

    }

    @Test
    void setUserPreferences_WithMissingUserName() throws Exception {

        userPreferencesDto.setUserName(null);

        mockMvc.perform(post("/userPreferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPreferencesDto)))
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
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result
                        .getResolvedException()).getMessage().contains(USER_DOES_NOT_EXIST)));

        verify(tourGuideServiceMock, Mockito.times(1)).setUserPreferences(any(UserPreferencesDto.class));
    }
}