package com.example.projectfinal_alpha;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 10/17/2022
 * Constants for the users' table in the data base
 */
public class Student implements Serializable {
    private String name;
    private String grade;
    private String aClass;
    private String userType;
    private ArrayList<String> groups;
    private String QR_data;
    private ArrayList<String> approvalID;
    private String lastRequest;

    private  ArrayList<String> permanentApprovalID;



    public boolean isAllowed = false;

    public Student() {
    }

    public Student(String name, String grade, String aClass, String userType, ArrayList<String> groups, String QR_data, ArrayList<String> approvalID,  ArrayList<String> permanentApprovalID) {
        this.name = name;
        this.grade = grade;
        this.aClass = aClass;
        this.userType = userType;
        this.groups = groups;
        this.QR_data = QR_data;
        this.approvalID = approvalID;
        this.permanentApprovalID = permanentApprovalID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getaClass() {
        return aClass;
    }

    public void setaClass(String aClass) {
        this.aClass = aClass;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }



    public String getQR_data() {
        return QR_data;
    }

    public void setQR_data(String QR_data) {
        this.QR_data = QR_data;
    }

    public ArrayList<String> getApprovalID() {
        return approvalID;
    }

    public void setApprovalID(ArrayList<String> approvalID) {
        this.approvalID = approvalID;
    }

    public String getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public ArrayList<String> getPermanentApprovalID() {
        return permanentApprovalID;
    }

    public void setPermanentApprovalID(ArrayList<String> permanentApprovalID) {
        this.permanentApprovalID = permanentApprovalID;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public void setAllowed(boolean allowed) {
        isAllowed = allowed;
    }
}
