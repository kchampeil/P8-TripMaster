package tourGuide.rewardCentralAPI.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.UUID;

@Slf4j
@Service
public class RewardCentralApiService implements IRewardCentralApiService {

    @Autowired
    private RewardCentral rewardCentral;

    @Override
    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        log.debug("Service getAttractionRewardPoints called for attractionId: " + attractionId + " and userId: " + userId);
        return rewardCentral.getAttractionRewardPoints(attractionId, userId);
    }
}
