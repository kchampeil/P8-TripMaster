package tourGuide;

import com.jsoniter.output.JsonStream;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.model.VisitedLocationBean;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import javax.validation.Valid;
import java.util.List;

import static tourGuide.constants.TourGuideExceptionConstants.INVALID_INPUT;

@Slf4j
@RestController
public class TourGuideController {

    private final TourGuideService tourGuideService;

    @Autowired
    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @ApiOperation(value = "Get the most recent location for the user corresponding to the given userName")
    @GetMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocationBean visitedLocation = tourGuideService.getUserLocation(userName);
        return JsonStream.serialize(visitedLocation.location);
    }

    //  DONE: Change this method to no longer return a List of Attractions.
    //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
    //  Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral
    @ApiOperation(value = "Get the closest five tourist attractions to the user - no matter how far away they are")
    @GetMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getNearByAttractions(userName));
    }

    @ApiOperation(value = "Get the list of user's rewards for a given user")
    @GetMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(userName));
    }

    @ApiOperation(value = "Get a list of every user's most recent location")
    @GetMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        // DONE: Get a list of every user's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }

        return JsonStream.serialize(tourGuideService.getAllCurrentLocations());
    }

    @ApiOperation(value = "Get a list of of trip deals (providers) for a given user")
    @GetMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(userName);
        return JsonStream.serialize(providers);
    }

    @ApiOperation(value = "Set given user preferences for the user")
    @PostMapping(value = "/userPreferences", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPreferencesDto> setUserPreferences(@RequestBody @Valid UserPreferencesDto userPreferencesDto,
                                                                 BindingResult bindingResult) {
        log.debug("POST request to set UserPreferences received");

        if (bindingResult.hasErrors()) {
            log.debug("bindingResult has errors");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_INPUT);
        }

        try {
            UserPreferencesDto addedUserPreferencesDto = tourGuideService.setUserPreferences(userPreferencesDto);
            return new ResponseEntity<>(addedUserPreferencesDto, HttpStatus.CREATED);

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

    }
}