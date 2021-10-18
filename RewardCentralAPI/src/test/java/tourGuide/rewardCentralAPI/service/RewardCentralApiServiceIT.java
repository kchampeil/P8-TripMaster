package tourGuide.rewardCentralAPI.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RewardCentralApiServiceIT {

    @Autowired
    private IRewardCentralApiService rewardCentralApiService;

    @Test
    void getAttractionRewardPoints() {

        int rewardPoints = rewardCentralApiService.getAttractionRewardPoints(UUID.randomUUID(), UUID.randomUUID());

        assertTrue(rewardPoints >= 1 && rewardPoints < 1000);
    }

}
