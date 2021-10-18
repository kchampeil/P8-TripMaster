package tourGuide.rewardCentralAPI.service;

import java.util.UUID;

public interface IRewardCentralApiService {

    int getAttractionRewardPoints(UUID attractionId, UUID userId);

}
