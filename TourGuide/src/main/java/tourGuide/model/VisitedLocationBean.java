package tourGuide.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class VisitedLocationBean {
    public UUID userId;
    public LocationBean location;
    public Date timeVisited;

    public VisitedLocationBean(UUID userId, LocationBean location, Date timeVisited) {
        this.userId = userId;
        this.location = location;
        this.timeVisited = timeVisited;
    }
}
