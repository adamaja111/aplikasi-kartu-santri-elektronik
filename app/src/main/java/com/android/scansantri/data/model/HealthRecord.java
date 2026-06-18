package com.android.scansantri.data.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class HealthRecord implements Serializable {
    private String santriId;
    private String healthRecordId;
    private String checkupDate;
    private String disease;
    private String treatment;
    @Nullable
    private String additionalNotes;

    public HealthRecord() {
    }

    public HealthRecord(String santriId, String healthRecordId, String checkupDate, String disease, String treatment, @Nullable String additionalNotes) {
        this.santriId = santriId;
        this.healthRecordId = healthRecordId;
        this.checkupDate = checkupDate;
        this.disease = disease;
        this.treatment = treatment;
        this.additionalNotes = additionalNotes;
    }

    public String getSantriId() {
        return santriId;
    }

    public void setSantriId(String santriId) {
        this.santriId = santriId;
    }

    public String getHealthRecordId() {
        return healthRecordId;
    }

    public void setHealthRecordId(String healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public String getCheckupDate() {
        return checkupDate;
    }

    public void setCheckupDate(String checkupDate) {
        this.checkupDate = checkupDate;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    @Nullable
    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(@Nullable String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
}
