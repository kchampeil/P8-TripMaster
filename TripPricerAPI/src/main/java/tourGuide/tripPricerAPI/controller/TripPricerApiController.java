package tourGuide.tripPricerAPI.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.tripPricerAPI.service.ITripPricerApiService;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
public class TripPricerApiController {

    private final ITripPricerApiService tripPricerApiService;

    @Autowired
    public TripPricerApiController(ITripPricerApiService tripPricerApiService) {
        this.tripPricerApiService = tripPricerApiService;
    }

    @ApiOperation(value = "Get a list of providers with prices taken into account " +
            "the input parameters (apiKey,  attractionId,  adults,  children,  nightsStay,  rewardsPoints)")
    @GetMapping("/price")
    public List<Provider> getPrice(@RequestParam String apiKey, @RequestParam UUID attractionId, @RequestParam int adults, @RequestParam int children, @RequestParam int nightsStay, @RequestParam int rewardsPoints) {
        log.debug("GET request /price received in TripPricerApiController");
        return tripPricerApiService.getPrice(apiKey,  attractionId,  adults,  children,  nightsStay,  rewardsPoints);
    }

    @ApiOperation(value = "Get the provider name for the given apiKey and number of adults")
    @GetMapping("/providerName")
    public String getProviderName(@RequestParam String apiKey,@RequestParam  int adults) {
        log.debug("GET request /providerName received in TripPricerApiController");
        return tripPricerApiService.getProviderName(apiKey, adults);
    }
}
