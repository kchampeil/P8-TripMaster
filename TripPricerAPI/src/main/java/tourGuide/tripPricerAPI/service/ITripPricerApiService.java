package tourGuide.tripPricerAPI.service;

import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

public interface ITripPricerApiService {

    List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints);

    String getProviderName(String apiKey, int adults);
}
