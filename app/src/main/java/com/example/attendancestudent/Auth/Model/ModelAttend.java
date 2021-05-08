package com.example.attendancestudent.Auth.Model;

import java.io.Serializable;

public class ModelAttend implements Serializable {

    String StudentName;
    String StudentImageUri;
    String Date;
    int NumOfAttend;

    public ModelAttend() {
    }

    public ModelAttend(String studentName, String studentImageUri, String date, int numOfAttend) {
        StudentName = studentName;
        StudentImageUri = studentImageUri;
        Date = date;
        NumOfAttend = numOfAttend;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getStudentImageUri() {
        return StudentImageUri;
    }

    public void setStudentImageUri(String studentImageUri) {
        StudentImageUri = studentImageUri;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getNumOfAttend() {
        return NumOfAttend;
    }

    public void setNumOfAttend(int numOfAttend) {
        NumOfAttend = numOfAttend;
    }
}
