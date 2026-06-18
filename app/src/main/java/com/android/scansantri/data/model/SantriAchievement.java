package com.android.scansantri.data.model;


import java.io.Serializable;

public class SantriAchievement implements Serializable {
    private String santriUid;
    private String achievementId;
    private Achievement achievement;

    public SantriAchievement() {
    }

    public SantriAchievement(String santriUid, String achievementId, Achievement achievement) {
        this.santriUid = santriUid;
        this.achievementId = achievementId;
        this.achievement = achievement;
    }

    public String getSantriUid() {
        return santriUid;
    }

    public void setSantriUid(String santriUid) {
        this.santriUid = santriUid;
    }

    public String getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(String achievementId) {
        this.achievementId = achievementId;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }
}
