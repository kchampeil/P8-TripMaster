package tourGuide.service.contracts;

import tourGuide.model.ProviderBean;

import java.util.List;
import java.util.UUID;

public interface ITripPricerAPIRequestService {

    List<ProviderBean> getPrice(String apiKey, UUID attractionId, int adults, int children,
                                int nightsStay, int rewardsPoints);

    String getProviderName(String apiKey, int adults);
}
