package tourGuide.gpsUtilAPI.config;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class GpsUtilBeanConfig {

    @Bean
    public GpsUtil gpsUtil() {
        Locale.setDefault(Locale.US);
        return new GpsUtil();
    }

}
