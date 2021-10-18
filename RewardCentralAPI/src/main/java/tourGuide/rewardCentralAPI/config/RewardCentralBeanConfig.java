package tourGuide.rewardCentralAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

@Configuration
public class RewardCentralBeanConfig {

    @Bean
    public RewardCentral rewardCentral() {
        return new RewardCentral();
    }

}
