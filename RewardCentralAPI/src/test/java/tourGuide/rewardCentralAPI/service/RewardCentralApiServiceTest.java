package tourGuide.rewardCentralAPI.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import rewardCentral.RewardCentral;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class RewardCentralApiServiceTest {

    @Autowired
    private IRewardCentralApiService rewardCentralApiService;

    @MockBean
    private RewardCentral rewardCentralMock;

    @Test
    void getAttractionRewardPoints() {

        when(rewardCentralMock.getAttractionRewardPoints(any(UUID.class), any(UUID.class)))
                .thenReturn(100);

        int rewardPoints = rewardCentralApiService.getAttractionRewardPoints(UUID.randomUUID(), UUID.randomUUID());

        assertEquals(100, rewardPoints);

        verify(rewardCentralMock, Mockito.times(1))
                .getAttractionRewardPoints(any(UUID.class), any(UUID.class));

    }
}