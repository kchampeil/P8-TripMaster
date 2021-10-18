package tourGuide.tripPricerAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tripPricer.TripPricer;

@Configuration
public class TripPricerBeanConfig {

    @Bean
    public TripPricer tripPricer() {
        return new TripPricer();
    }

}
