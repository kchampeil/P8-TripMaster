package tourGuide.user;

import tourGuide.model.AttractionBean;
import tourGuide.model.VisitedLocationBean;

public class UserReward {

    public final VisitedLocationBean visitedLocation;
    public final AttractionBean attraction;
    private int rewardPoints;

    public UserReward(VisitedLocationBean visitedLocation, AttractionBean attraction, int rewardPoints) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
        this.rewardPoints = rewardPoints;
    }

    public UserReward(VisitedLocationBean visitedLocation, AttractionBean attraction) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

}
