package tourGuide.rewardCentralAPI.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.rewardCentralAPI.service.IRewardCentralApiService;

import java.util.UUID;

@Slf4j
@RestController
public class RewardCentralApiController {

    private final IRewardCentralApiService rewardCentralApiService;

    @Autowired
    public RewardCentralApiController(IRewardCentralApiService rewardCentralApiService) {
        this.rewardCentralApiService = rewardCentralApiService;
    }

    @ApiOperation(value = "Get the reward points for a given attraction and user")
    @GetMapping("/attractionRewardPoints")
    public int getAttractionRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId) {
        log.debug("GET request /attractionRewardPoints received in RewardCentralApiController for attractionId: " + attractionId + " and userId: " + userId);
        return rewardCentralApiService.getAttractionRewardPoints(attractionId, userId);
    }
}
