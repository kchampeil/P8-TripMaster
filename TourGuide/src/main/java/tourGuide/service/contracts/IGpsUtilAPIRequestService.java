package tourGuide.service.contracts;

import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.model.AttractionBean;
import tourGuide.model.VisitedLocationBean;

import java.util.List;
import java.util.UUID;

public interface IGpsUtilAPIRequestService {
    VisitedLocationBean getUserLocation(@RequestParam UUID userId);

    List<AttractionBean> getAttractions();
}
