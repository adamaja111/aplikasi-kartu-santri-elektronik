package com.android.scansantri.data.model;


import java.io.Serializable;

public class SantriViolation implements Serializable {
    private String santriUid;
    private String violationId;
    private Violation violation;

    public SantriViolation() {
    }

    public SantriViolation(String santriUid, String violationId, Violation violation) {
        this.santriUid = santriUid;
        this.violationId = violationId;
        this.violation = violation;
    }

    public String getSantriUid() {
        return santriUid;
    }

    public void setSantriUid(String santriUid) {
        this.santriUid = santriUid;
    }

    public String getViolationId() {
        return violationId;
    }

    public void setViolationId(String violationId) {
        this.violationId = violationId;
    }

    public Violation getViolation() {
        return violation;
    }

    public void setViolation(Violation violation) {
        this.violation = violation;
    }
}