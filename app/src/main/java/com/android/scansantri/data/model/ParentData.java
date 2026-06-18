package com.android.scansantri.data.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class ParentData implements Serializable {
    private String santriId;
    private String parentId;
    @Nullable
    private FatherData fatherData;
    @Nullable
    private MotherData motherData;
    private boolean divorced;
    @Nullable
    private StepParentData stepParentData;

    public ParentData() {
    }

    public ParentData(String santriId, String parentId, @Nullable FatherData fatherData, @Nullable MotherData motherData, boolean divorced, @Nullable StepParentData stepParent) {
        this.santriId = santriId;
        this.parentId = parentId;
        this.fatherData = fatherData;
        this.motherData = motherData;
        this.divorced = divorced;
        this.stepParentData = stepParent;
    }


    public String getSantriId() {
        return santriId;
    }

    public void setSantriId(String santriId) {
        this.santriId = santriId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Nullable
    public FatherData getFatherData() {
        return fatherData;
    }

    public void setFatherData(@Nullable FatherData fatherData) {
        this.fatherData = fatherData;
    }

    @Nullable
    public MotherData getMotherData() {
        return motherData;
    }

    public void setMotherData(@Nullable MotherData motherData) {
        this.motherData = motherData;
    }

    public boolean isDivorced() {
        return divorced;
    }

    public void setDivorced(boolean divorced) {
        this.divorced = divorced;
    }

    @Nullable
    public StepParentData getStepParentData() {
        return stepParentData;
    }

    public void setStepParentData(@Nullable StepParentData stepParentData) {
        this.stepParentData = stepParentData;
    }
}
