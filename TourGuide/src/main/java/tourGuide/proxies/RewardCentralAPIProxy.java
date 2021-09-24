package tourGuide.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "reward-central-api", url = "${reward.feign.url}" + ":" + "${reward.feign.port}")
public interface RewardCentralAPIProxy {

    @GetMapping("/attractionRewardPoints")
    int getAttractionRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId);
}
