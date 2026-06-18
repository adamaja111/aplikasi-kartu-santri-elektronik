package com.android.scansantri.data.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Organization implements Serializable {
    private String santriId;
    private String organizationId;
    private String organizationName;
    private String position;
    private String startDate;
    @Nullable
    private String endDate;

    public Organization() {
    }

    public Organization(String santriId, String organizationId, String organizationName, String position, String startDate, @Nullable String endDate) {
        this.santriId = santriId;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getSantriId() {
        return santriId;
    }

    public void setSantriId(String santriId) {
        this.santriId = santriId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Nullable
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nullable String endDate) {
        this.endDate = endDate;
    }
}
