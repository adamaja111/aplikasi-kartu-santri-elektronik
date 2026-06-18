package com.android.scansantri.data.model;

import java.io.Serializable;

public class FatherData implements Serializable {
    private String santriId;
    private String fatherId;
    private String fatherName;
    private String fatherJob;
    private String fatherPhone;

    public FatherData() {
    }

    public FatherData(String santriId, String fatherId, String fatherName, String fatherJob, String fatherPhone) {
        this.santriId = santriId;
        this.fatherId = fatherId;
        this.fatherName = fatherName;
        this.fatherJob = fatherJob;
        this.fatherPhone = fatherPhone;
    }

    public String getSantriId() {
        return santriId;
    }

    public void setSantriId(String santriId) {
        this.santriId = santriId;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherJob() {
        return fatherJob;
    }

    public void setFatherJob(String fatherJob) {
        this.fatherJob = fatherJob;
    }

    public String getFatherPhone() {
        return fatherPhone;
    }

    public void setFatherPhone(String fatherPhone) {
        this.fatherPhone = fatherPhone;
    }
}
