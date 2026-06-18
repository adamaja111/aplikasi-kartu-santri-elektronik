package com.android.scansantri.data.model;


import java.io.Serializable;

public class SantriHealthRecord implements Serializable {
    private String santriUid;
    private String healthRecordId;
    private HealthRecord healthRecord;

    public SantriHealthRecord() {
    }

    public SantriHealthRecord(String santriUid, String healthRecordId, HealthRecord healthRecord) {
        this.santriUid = santriUid;
        this.healthRecordId = healthRecordId;
        this.healthRecord = healthRecord;
    }

    public String getSantriUid() {
        return santriUid;
    }

    public void setSantriUid(String santriUid) {
        this.santriUid = santriUid;
    }

    public String getHealthRecordId() {
        return healthRecordId;
    }

    public void setHealthRecordId(String healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public HealthRecord getHealthRecord() {
        return healthRecord;
    }

    public void setHealthRecord(HealthRecord healthRecord) {
        this.healthRecord = healthRecord;
    }
}
