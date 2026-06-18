package com.android.scansantri.data.model;


import java.io.Serializable;

public class MotherData implements Serializable {
    private String santriId;
    private String motherId;
    private String motherName;
    private String motherJob;
    private String motherPhone;

    public MotherData() {
    }

    public MotherData(String santriId, String motherId, String motherName, String motherJob, String motherPhone) {
        this.santriId = santriId;
        this.motherId = motherId;
        this.motherName = motherName;
        this.motherJob = motherJob;
        this.motherPhone = motherPhone;
    }

    public String getSantriId() {
        return santriId;
    }

    public void setSantriId(String santriId) {
        this.santriId = santriId;
    }

    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherJob() {
        return motherJob;
    }

    public void setMotherJob(String motherJob) {
        this.motherJob = motherJob;
    }

    public String getMotherPhone() {
        return motherPhone;
    }

    public void setMotherPhone(String motherPhone) {
        this.motherPhone = motherPhone;
    }
}