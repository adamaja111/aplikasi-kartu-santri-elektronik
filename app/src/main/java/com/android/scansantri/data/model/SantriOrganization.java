package com.android.scansantri.data.model;

import java.io.Serializable;

public class SantriOrganization implements Serializable {
    private String santriUid;
    private String organizationId;
    private Organization organization;

    public SantriOrganization() {
    }

    public SantriOrganization(String santriUid, String organizationId, Organization organization) {
        this.santriUid = santriUid;
        this.organizationId = organizationId;
        this.organization = organization;
    }

    public String getSantriUid() {
        return santriUid;
    }

    public void setSantriUid(String santriUid) {
        this.santriUid = santriUid;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
