package com.example.attendancestudent.Auth.Model;

import java.io.Serializable;

public class ModelStudent implements Serializable {

    String sKey,
            sFullName,
            sEmail,
            sPhoneNumber,
            sJopTitle,
            sGender,
            sAddress,
            sNational_ID,
            sAcademicYear,
            sDepartment;

    String sImageUri;

    public ModelStudent() {
    }

    public ModelStudent(String sKey, String sFullName, String sEmail, String sPhoneNumber, String sJopTitle, String sGender, String sAddress, String sNational_ID, String sAcademicYear, String sDepartment, String sImageUri) {
        this.sKey = sKey;
        this.sFullName = sFullName;
        this.sEmail = sEmail;
        this.sPhoneNumber = sPhoneNumber;
        this.sJopTitle = sJopTitle;
        this.sGender = sGender;
        this.sAddress = sAddress;
        this.sNational_ID = sNational_ID;
        this.sAcademicYear = sAcademicYear;
        this.sDepartment = sDepartment;
        this.sImageUri = sImageUri;
    }

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public String getsFullName() {
        return sFullName;
    }

    public void setsFullName(String sFullName) {
        this.sFullName = sFullName;
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getsPhoneNumber() {
        return sPhoneNumber;
    }

    public void setsPhoneNumber(String sPhoneNumber) {
        this.sPhoneNumber = sPhoneNumber;
    }

    public String getsJopTitle() {
        return sJopTitle;
    }

    public void setsJopTitle(String sJopTitle) {
        this.sJopTitle = sJopTitle;
    }

    public String getsGender() {
        return sGender;
    }

    public void setsGender(String sGender) {
        this.sGender = sGender;
    }

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getsNational_ID() {
        return sNational_ID;
    }

    public void setsNational_ID(String sNational_ID) {
        this.sNational_ID = sNational_ID;
    }

    public String getsAcademicYear() {
        return sAcademicYear;
    }

    public void setsAcademicYear(String sAcademicYear) {
        this.sAcademicYear = sAcademicYear;
    }

    public String getsDepartment() {
        return sDepartment;
    }

    public void setsDepartment(String sDepartment) {
        this.sDepartment = sDepartment;
    }

    public String getsImageUri() {
        return sImageUri;
    }

    public void setsImageUri(String sImageUri) {
        this.sImageUri = sImageUri;
    }
}
