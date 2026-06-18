package com.android.scansantri.data.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class SantriData implements Serializable {
    private String uid;
    private String nis;
    private String fullName;
    private String birthPlace;
    private String birthDate;
    private String gender;
    private String address;
    private String phoneNumber;
    private String email;
    private String barcodeLink;
    @Nullable
    private String facebook;
    @Nullable
    private String instagram;
    @Nullable
    private String twitter;

    public SantriData() {
    }

    public SantriData(String uid, String nis, String fullName, String birthPlace, String birthDate, String gender, String address, String phoneNumber, String email, String barcodeLink, @Nullable String facebook, @Nullable String instagram, @Nullable String twitter) {
        this.uid = uid;
        this.nis = nis;
        this.fullName = fullName;
        this.birthPlace = birthPlace;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.barcodeLink = barcodeLink;
        this.facebook = facebook;
        this.instagram = instagram;
        this.twitter = twitter;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(@Nullable String facebook) {
        this.facebook = facebook;
    }

    @Nullable
    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(@Nullable String instagram) {
        this.instagram = instagram;
    }

    @Nullable
    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(@Nullable String twitter) {
        this.twitter = twitter;
    }

    public String getBarcodeLink() {
        return barcodeLink;
    }

    public void setBarcodeLink(String barcodeLink) {
        this.barcodeLink = barcodeLink;
    }
}
