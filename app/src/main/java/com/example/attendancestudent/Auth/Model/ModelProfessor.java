package com.example.attendancestudent.Auth.Model;

import java.io.Serializable;

public class ModelProfessor implements Serializable {

    String pKey,
            pFullName,
            pEmail,
            pPhoneNumber,
            pJopTitle,
            pGender,
            pSubject_Name,
            pAcademicYear;

    String pImageUri;

    int pNumLecture;

    public ModelProfessor() {
    }

    public ModelProfessor(String pKey, String pFullName, String pEmail, String pPhoneNumber, String pJopTitle, String pGender, String pSubject_Name, String pAcademicYear, String pImageUri, int pNumLecture) {
        this.pKey = pKey;
        this.pFullName = pFullName;
        this.pEmail = pEmail;
        this.pPhoneNumber = pPhoneNumber;
        this.pJopTitle = pJopTitle;
        this.pGender = pGender;
        this.pSubject_Name = pSubject_Name;
        this.pAcademicYear = pAcademicYear;
        this.pImageUri = pImageUri;
        this.pNumLecture = pNumLecture;
    }

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getpFullName() {
        return pFullName;
    }

    public void setpFullName(String pFullName) {
        this.pFullName = pFullName;
    }

    public String getpEmail() {
        return pEmail;
    }

    public void setpEmail(String pEmail) {
        this.pEmail = pEmail;
    }

    public String getpPhoneNumber() {
        return pPhoneNumber;
    }

    public void setpPhoneNumber(String pPhoneNumber) {
        this.pPhoneNumber = pPhoneNumber;
    }

    public String getpJopTitle() {
        return pJopTitle;
    }

    public void setpJopTitle(String pJopTitle) {
        this.pJopTitle = pJopTitle;
    }

    public String getpGender() {
        return pGender;
    }

    public void setpGender(String pGender) {
        this.pGender = pGender;
    }

    public String getpSubject_Name() {
        return pSubject_Name;
    }

    public void setpSubject_Name(String pSubject_Name) {
        this.pSubject_Name = pSubject_Name;
    }

    public String getpAcademicYear() {
        return pAcademicYear;
    }

    public void setpAcademicYear(String pAcademicYear) {
        this.pAcademicYear = pAcademicYear;
    }

    public String getpImageUri() {
        return pImageUri;
    }

    public void setpImageUri(String pImageUri) {
        this.pImageUri = pImageUri;
    }

    public int getpNumLecture() {
        return pNumLecture;
    }

    public void setpNumLecture(int pNumLecture) {
        this.pNumLecture = pNumLecture;
    }
}
