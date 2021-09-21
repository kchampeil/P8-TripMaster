package tourGuide;

import org.springframework.context.annotation.Configuration;

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

    /*TODEL
    @Bean
    public RewardCentral getRewardCentral() {
        return new RewardCentral();
    }

     */

/*TODEL
    @Bean
    public TripPricer getTripPricer() {
        return new TripPricer();
    }

 */

}
