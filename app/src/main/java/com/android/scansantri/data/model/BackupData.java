package com.android.scansantri.data.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BackupData implements Serializable {
    private List<SantriData> santriList;
    private List<ParentData> parentList;
    private List<SantriOrganization> organizationList;
    private List<SantriAchievement> achievementList;
    private List<SantriHealthRecord> healthRecordList;
    private List<SantriViolation> violationList;

    public BackupData() {}

    public List<SantriData> getSantriList() { return santriList; }
    public void setSantriList(List<SantriData> santriList) { this.santriList = santriList; }

    public List<ParentData> getParentList() { return parentList; }
    public void setParentList(List<ParentData> parentList) { this.parentList = parentList; }

    public List<SantriOrganization> getOrganizationList() { return organizationList; }
    public void setOrganizationList(List<SantriOrganization> organizationList) { this.organizationList = organizationList; }

    public List<SantriAchievement> getAchievementList() { return achievementList; }
    public void setAchievementList(List<SantriAchievement> achievementList) { this.achievementList = achievementList; }

    public List<SantriHealthRecord> getHealthRecordList() { return healthRecordList; }
    public void setHealthRecordList(List<SantriHealthRecord> healthRecordList) { this.healthRecordList = healthRecordList; }

    public List<SantriViolation> getViolationList() { return violationList; }
    public void setViolationList(List<SantriViolation> violationList) { this.violationList = violationList; }
}
