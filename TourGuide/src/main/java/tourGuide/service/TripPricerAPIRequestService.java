package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.model.ProviderBean;
import tourGuide.proxies.TripPricerAPIProxy;
import tourGuide.service.contracts.ITripPricerAPIRequestService;

import java.util.List;
import java.util.UUID;

@Service
public class TripPricerAPIRequestService implements ITripPricerAPIRequestService {

    @Autowired
    private TripPricerAPIProxy tripPricerAPIProxy;

    @Override
    public List<ProviderBean> getPrice(String apiKey, UUID attractionId, int adults,
                                       int children, int nightsStay, int rewardsPoints) {
        return tripPricerAPIProxy.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

    @Override
    public String getProviderName(String apiKey, int adults) {
        return tripPricerAPIProxy.getProviderName(apiKey, adults);
    }
}
