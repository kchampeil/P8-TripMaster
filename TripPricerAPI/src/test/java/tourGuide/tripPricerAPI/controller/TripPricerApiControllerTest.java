package tourGuide.tripPricerAPI.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tourGuide.tripPricerAPI.service.TripPricerApiService;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TripPricerApiController.class)
class TripPricerApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripPricerApiService tripPricerApiServiceMock;

    @Test
    void getPrice() throws Exception {
        Provider provider1 = new Provider(UUID.randomUUID(), "DisneyLand Paris", 65.5);
        Provider provider2 = new Provider(UUID.randomUUID(), "Giverny", 25.9);
        List<Provider> providerList = new ArrayList<>();
        providerList.add(provider1);
        providerList.add(provider2);

        when(tripPricerApiServiceMock.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(),
                anyInt(), anyInt())).thenReturn(providerList);

        mockMvc.perform(get("/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("apiKey", "api-key-001")
                        .param("attractionId", UUID.randomUUID().toString())
                        .param("adults", "2")
                        .param("children", "3")
                        .param("nightsStay", "2")
                        .param("rewardsPoints", "100"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[0].name").value("DisneyLand Paris"))
                .andExpect(jsonPath("$.[0].price").value("65.5"));

        verify(tripPricerApiServiceMock, Mockito.times(1)).getPrice(anyString(), any(UUID.class), anyInt(), anyInt(),
                anyInt(), anyInt());
    }

    @Test
    void getProviderName() throws Exception {

        when(tripPricerApiServiceMock.getProviderName(anyString(), anyInt())).thenReturn("Giverny");

        mockMvc.perform(get("/providerName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("apiKey", "api-key-001")
                        .param("adults", "2"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Giverny"));

        verify(tripPricerApiServiceMock, Mockito.times(1)).getProviderName(anyString(), anyInt());
    }
}