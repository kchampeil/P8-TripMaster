package tourGuide.service.contracts;

import java.util.UUID;

public interface IRewardCentralAPIRequestService {

    int getAttractionRewardPoints(UUID attractionId, UUID userId);
}
