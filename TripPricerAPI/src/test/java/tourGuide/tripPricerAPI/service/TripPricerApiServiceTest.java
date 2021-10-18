package tourGuide.tripPricerAPI.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class TripPricerApiServiceTest {

    @Autowired
    private ITripPricerApiService tripPricerApiService;

    @MockBean
    private TripPricer tripPricerMock;

    @Test
    void getPrice() {
        Provider provider1 = new Provider(UUID.randomUUID(), "DisneyLand Paris", 65.5);
        Provider provider2 = new Provider(UUID.randomUUID(), "Giverny", 25.9);
        List<Provider> providerListExpected = new ArrayList<>();
        providerListExpected.add(provider1);
        providerListExpected.add(provider2);

        when(tripPricerMock.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(),
                anyInt(), anyInt())).thenReturn(providerListExpected);

        List<Provider> providerListActual = tripPricerApiService.getPrice("api-key-001",
                UUID.randomUUID(), 2, 3, 2, 100);

        assertEquals(providerListExpected, providerListActual);

        verify(tripPricerMock, Mockito.times(1))
                .getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt());

    }

    @Test
    void getProviderName() {
        when(tripPricerMock.getProviderName(anyString(), anyInt())).thenReturn("Giverny");

        String providerName = tripPricerApiService.getProviderName("api-key-001", 2);

        assertEquals("Giverny", providerName);
        verify(tripPricerMock, Mockito.times(1)).getProviderName(anyString(), anyInt());
    }
}