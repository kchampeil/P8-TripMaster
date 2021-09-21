package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.proxies.RewardCentralAPIProxy;
import tourGuide.service.contracts.IRewardCentralAPIRequestService;

import java.util.UUID;

@Service
public class RewardCentralAPIRequestService implements IRewardCentralAPIRequestService {

    @Autowired
    private RewardCentralAPIProxy rewardCentralAPIProxy;

    @Override
    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentralAPIProxy.getAttractionRewardPoints(attractionId, userId);
    }
}
