package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

@Configuration
public class TourGuideModule {

	/*TODEL
	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}

	 */


	/* unactivated to mock  the other RewardsService bean
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsUtil(), getRewardCentral());
	}

	 */

    @Bean
    public RewardCentral getRewardCentral() {
        return new RewardCentral();
    }

    @Bean
    public TripPricer getTripPricer() {
        return new TripPricer();
    }

}
