package tourGuide.user;

import tourGuide.model.ProviderBean;
import tourGuide.model.VisitedLocationBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID userId;
    private final String userName;
    private String phoneNumber;
    private String emailAddress;
    private Date latestLocationTimestamp;
    private List<VisitedLocationBean> visitedLocations = new ArrayList<>();
    private List<UserReward> userRewards = new ArrayList<>();
    private UserPreferences userPreferences = new UserPreferences();
    private List<ProviderBean> tripDeals = new ArrayList<>();

    public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
        this.latestLocationTimestamp = latestLocationTimestamp != null ? new Date(latestLocationTimestamp.getTime()) : null;
    }

    public Date getLatestLocationTimestamp() {
        return latestLocationTimestamp != null ? new Date(latestLocationTimestamp.getTime()) : null;
    }

    public void addToVisitedLocations(VisitedLocationBean visitedLocation) {
        visitedLocations.add(visitedLocation);
    }

    public List<VisitedLocationBean> getVisitedLocations() {
        return visitedLocations;
    }

    public void clearVisitedLocations() {
        visitedLocations.clear();
    }

    /**
     * add the userReward to userRewards
     * only if the associated attraction has not already been rewarded
     *
     * @param userReward userReward to add
     */
    public void addUserReward(UserReward userReward) {
        if (userRewards.stream()
                .noneMatch(reward -> reward.attraction.attractionName.equals(userReward.attraction.attractionName))) {
            userRewards.add(userReward);
        }
    }

    public List<UserReward> getUserRewards() {
        return userRewards;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public VisitedLocationBean getLastVisitedLocation() {
        if (visitedLocations.size() == 0) {
            return null;
        }
        return visitedLocations.get(visitedLocations.size() - 1);
    }

    public void setTripDeals(List<ProviderBean> tripDeals) {
        this.tripDeals = tripDeals;
    }

    public List<ProviderBean> getTripDeals() {
        return tripDeals;
    }

}
