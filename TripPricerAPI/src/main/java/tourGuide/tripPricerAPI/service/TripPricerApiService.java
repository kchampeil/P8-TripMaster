package tourGuide.tripPricerAPI.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TripPricerApiService implements ITripPricerApiService {

    @Autowired
    private TripPricer tripPricer;

    /**
     * Get a list of providers with prices taken into account the input parameters
     *
     * @param apiKey        api key
     * @param attractionId  attraction id
     * @param adults        number of adults
     * @param children      number of children
     * @param nightsStay    number of night's stay
     * @param rewardsPoints number of rewards points
     * @return list of providers
     */
    @Override
    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) {
        log.debug("Service getPrice called");
        return tripPricer.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

    /**
     * Get the provider name
     *
     * @param apiKey api key
     * @param adults number of adults
     * @return provider name
     */
    @Override
    public String getProviderName(String apiKey, int adults) {
        log.debug("Service getPrice called");
        return tripPricer.getProviderName(apiKey, adults);
    }
}
