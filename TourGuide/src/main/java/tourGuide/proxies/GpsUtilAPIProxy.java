package tourGuide.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.model.AttractionBean;
import tourGuide.model.VisitedLocationBean;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "gps-util-api", url = "${gps.feign.url}" + ":" + "${gps.feign.port}")
public interface GpsUtilAPIProxy {

    @GetMapping(value = "/userLocation")
    VisitedLocationBean getUserLocation(@RequestParam UUID userId);

    @GetMapping(value = "/attractions")
    List<AttractionBean> getAttractions();
}
