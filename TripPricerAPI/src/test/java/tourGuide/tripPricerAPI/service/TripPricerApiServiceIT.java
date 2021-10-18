package tourGuide.tripPricerAPI.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TripPricerApiServiceIT {

    @Autowired
    private ITripPricerApiService tripPricerApiService;

    @Test
    void getPrice() {
        UUID attractionId = UUID.randomUUID();
        List<Provider> providerList = tripPricerApiService.getPrice("api-key-001",
                attractionId, 2, 3, 2, 100);

        assertThat(providerList).isNotEmpty();
        assertEquals(attractionId, providerList.get(0).tripId);
        assertNotNull(providerList.get(0).name);
    }

    @Test
    void getProviderName() {

        String providerName = tripPricerApiService.getProviderName("api-key-001", 2);

        assertNotNull(providerName);
    }
}
