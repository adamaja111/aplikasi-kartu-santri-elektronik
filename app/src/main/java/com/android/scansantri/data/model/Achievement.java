package com.android.scansantri.data.model;

import java.io.Serializable;

public class Achievement implements Serializable {
    private String santriId;
    private String achievementId;
    private String eventName;
    private String rankOrParticipant;
    private String achievementDate;
    private String level;

    public Achievement() {
    }

    public Achievement(String santriId, String achievementId, String eventName, String rankOrParticipant, String achievementDate, String level) {
        this.santriId = santriId;
        this.achievementId = achievementId;
        this.eventName = eventName;
        this.rankOrParticipant = rankOrParticipant;
        this.achievementDate = achievementDate;
        this.level = level;
    }

    public String getSantriId() {
        return santriId;
    }

    public void setSantriId(String santriId) {
        this.santriId = santriId;
    }

    public String getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(String achievementId) {
        this.achievementId = achievementId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getRankOrParticipant() {
        return rankOrParticipant;
    }

    public void setRankOrParticipant(String rankOrParticipant) {
        this.rankOrParticipant = rankOrParticipant;
    }

    public String getAchievementDate() {
        return achievementDate;
    }

    public void setAchievementDate(String achievementDate) {
        this.achievementDate = achievementDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
