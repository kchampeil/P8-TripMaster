package tourGuide.rewardCentralAPI.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tourGuide.rewardCentralAPI.service.RewardCentralApiService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RewardCentralApiController.class)
class RewardCentralApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardCentralApiService rewardCentralApiServiceMock;

    @Test
    void getAttractionRewardPoints() throws Exception {

        when(rewardCentralApiServiceMock.getAttractionRewardPoints(any(UUID.class), any(UUID.class)))
                .thenReturn(100);

        mockMvc.perform(get("/attractionRewardPoints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("attractionId", UUID.randomUUID().toString())
                        .param("userId", UUID.randomUUID().toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("100"));

        verify(rewardCentralApiServiceMock, Mockito.times(1))
                .getAttractionRewardPoints(any(UUID.class), any(UUID.class));
    }
}