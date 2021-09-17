package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.model.AttractionBean;
import tourGuide.model.VisitedLocationBean;
import tourGuide.proxies.GpsUtilAPIProxy;
import tourGuide.service.contracts.IGpsUtilAPIRequestService;

import java.util.List;
import java.util.UUID;

@Service
public class GpsUtilAPIRequestService implements IGpsUtilAPIRequestService {

    @Autowired
    private GpsUtilAPIProxy gpsUtilAPIProxy;

    @Override
    public VisitedLocationBean getUserLocation(UUID userId) {
        return gpsUtilAPIProxy.getUserLocation(userId);
    }

    @Override
    public List<AttractionBean> getAttractions() {
        return gpsUtilAPIProxy.getAttractions();
    }
}
