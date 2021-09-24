package tourGuide.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.model.ProviderBean;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "trip-pricer-api", url = "${trip.feign.url}" + ":" + "${trip.feign.port}")
public interface TripPricerAPIProxy {

    @GetMapping("/price")
    List<ProviderBean> getPrice(@RequestParam String apiKey, @RequestParam UUID attractionId,
                                @RequestParam int adults, @RequestParam int children,
                                @RequestParam int nightsStay, @RequestParam int rewardsPoints);

    @GetMapping("/providerName")
    String getProviderName(@RequestParam String apiKey, @RequestParam int adults);
}
