package tourGuide.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "rewardCentral-api", url = "http://localhost:9003")
public interface RewardCentralAPIProxy {

    @GetMapping("/attractionRewardPoints")
    int getAttractionRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId);
}
