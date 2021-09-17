package tourGuide.service.contracts;

import tourGuide.model.AttractionBean;
import tourGuide.model.VisitedLocationBean;

import java.util.List;
import java.util.UUID;

public interface IGpsUtilAPIRequestService {
    VisitedLocationBean getUserLocation(UUID userId);

    List<AttractionBean> getAttractions();
}
