package tourGuide;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.jackson.datatype.money.MoneyModule;
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

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new MoneyModule());

    private static UserPreferences userPreferences;

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
    }

    @Disabled
    @Test
    void setUserPreferences_WithSuccess() throws Exception {

        UserPreferencesDto userPreferencesDto = new UserPreferencesDto("jon", userPreferences);

        /* TODEL : test serialize/deserialize UserPreferences ok */
        String json = objectMapper.writeValueAsString(userPreferencesDto);//TODEL
        System.out.println(json);//TODEL
        UserPreferencesDto upDto = objectMapper.readValue(json, UserPreferencesDto.class); //TODEL
        System.out.println("username: " + upDto.getUserName());//TODEL
        System.out.println("lower price: " + upDto.getUserPreferences().getLowerPricePoint());//TODEL
        System.out.println("currency: " + upDto.getUserPreferences().getCurrency());//TODEL

        when(tourGuideServiceMock.setUserPreferences(userPreferencesDto))
                .thenReturn(userPreferencesDto);

        //TOASK : dans postman pas d'erreur mais n'affiche pas tout ce qui est monétaire
        mockMvc.perform(post("/userPreferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPreferencesDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(tourGuideServiceMock, Mockito.times(1)).setUserPreferences(userPreferencesDto);

    }

    @Disabled
    @Test
    void setUserPreferences_WithMissingUserName() throws Exception {

        UserPreferencesDto userPreferencesDto = new UserPreferencesDto(null, userPreferences);

        //TOASK
        mockMvc.perform(post("/userPreferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPreferencesDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result
                        .getResolvedException()).getMessage().contains(INVALID_INPUT)));

        verify(tourGuideServiceMock, Mockito.times(0)).setUserPreferences(userPreferencesDto);

    }

    @Disabled
    @Test
    void setUserPreferences_WithUnknownUser() throws Exception {

        UserPreferencesDto userPreferencesDto = new UserPreferencesDto("john", userPreferences);

        when(tourGuideServiceMock.setUserPreferences(any(UserPreferencesDto.class)))
                .thenThrow(new Exception(USER_DOES_NOT_EXIST));
        //TOASK
        mockMvc.perform(post("/userPreferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPreferencesDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result
                        .getResolvedException()).getMessage().contains(USER_DOES_NOT_EXIST)));

        verify(tourGuideServiceMock, Mockito.times(1)).setUserPreferences(any(UserPreferencesDto.class));

    }
}