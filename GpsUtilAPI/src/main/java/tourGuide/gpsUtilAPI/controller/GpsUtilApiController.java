package tourGuide.gpsUtilAPI.controller;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.gpsUtilAPI.service.IGpsUtilApiService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
public class GpsUtilApiController {

    private final IGpsUtilApiService gpsUtilApiService;

    @Autowired
    public GpsUtilApiController(IGpsUtilApiService gpsUtilApiService) {
        this.gpsUtilApiService = gpsUtilApiService;
    }

    @ApiOperation(value = "Get current location for the user corresponding to the given userId")
    @GetMapping("/userLocation")
    public VisitedLocation getUserLocation(@RequestParam UUID userId) {
        log.debug("GET request /userLocation received for userId: " + userId);
        return gpsUtilApiService.getUserLocation(userId);
    }

    @ApiOperation(value = "Get a list with all attractions")
    @GetMapping("/attractions")
    public List<Attraction> getAttractions() {
        log.debug("GET request /attractions received");
        return gpsUtilApiService.getAttractions();
    }
}
