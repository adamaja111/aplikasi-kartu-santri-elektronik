package com.android.scansantri.data.model;

import java.io.Serializable;

public class StepParentData implements Serializable {
    private String santriId;
    private String stepId;
    private String stepParentName;
    private String stepParentPhone;
    private String stepParentAddress;

    public StepParentData() {
    }

    public StepParentData(String santriId, String stepId, String stepParentName, String stepParentPhone, String stepParentAddress) {
        this.santriId = santriId;
        this.stepId = stepId;
        this.stepParentName = stepParentName;
        this.stepParentPhone = stepParentPhone;
        this.stepParentAddress = stepParentAddress;
    }

    public String getSantriId() {
        return santriId;
    }

    public void setSantriId(String santriId) {
        this.santriId = santriId;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getStepParentName() {
        return stepParentName;
    }

    public void setStepParentName(String stepParentName) {
        this.stepParentName = stepParentName;
    }

    public String getStepParentPhone() {
        return stepParentPhone;
    }

    public void setStepParentPhone(String stepParentPhone) {
        this.stepParentPhone = stepParentPhone;
    }

    public String getStepParentAddress() {
        return stepParentAddress;
    }

    public void setStepParentAddress(String stepParentAddress) {
        this.stepParentAddress = stepParentAddress;
    }
}
