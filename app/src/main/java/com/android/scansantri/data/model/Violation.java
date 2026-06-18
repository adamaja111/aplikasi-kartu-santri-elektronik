package com.android.scansantri.data.model;

import java.io.Serializable;

public class Violation implements Serializable {
    private String santriId;
    private String violationId;
    private String violationDate;
    private String violationName;
    private String violationType;
    private String sanction;
    private String sanctionDate;
    private int points;

    public Violation() {
    }

    public Violation(String santriId, String violationId, String violationDate, String violationName, String violationType, String sanction, String sanctionDate) {
        this.santriId = santriId;
        this.violationId = violationId;
        this.violationDate = violationDate;
        this.violationName = violationName;
        this.violationType = violationType;
        this.sanction = sanction;
        this.sanctionDate = sanctionDate;
    }

    public Violation(String santriId, String violationId, String violationDate, String violationName, String violationType, String sanction, String sanctionDate, int points) {
        this.santriId = santriId;
        this.violationId = violationId;
        this.violationDate = violationDate;
        this.violationName = violationName;
        this.violationType = violationType;
        this.sanction = sanction;
        this.sanctionDate = sanctionDate;
        this.points = points;
    }

    public String getSantriId() {
        return santriId;
    }

    public void setSantriId(String santriId) {
        this.santriId = santriId;
    }

    public String getViolationId() {
        return violationId;
    }

    public void setViolationId(String violationId) {
        this.violationId = violationId;
    }

    public String getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(String violationDate) {
        this.violationDate = violationDate;
    }

    public String getViolationName() {
        return violationName;
    }

    public void setViolationName(String violationName) {
        this.violationName = violationName;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public String getSanction() {
        return sanction;
    }

    public void setSanction(String sanction) {
        this.sanction = sanction;
    }

    public String getSanctionDate() {
        return sanctionDate;
    }

    public void setSanctionDate(String sanctionDate) {
        this.sanctionDate = sanctionDate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
