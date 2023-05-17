package com.example.projectfinal_alpha;

import java.util.ArrayList;

public class Approval {
    String timeStampApproval;
    String expirationDate;
    String teAnswer;
    String teAnswerID;
    int day;
    ArrayList<Integer> hour;
    String studentsID;
    String groupsID;
    String requestID;
    Boolean isValid;
    Boolean isPermanent;


    public Approval(String timeStampApproval, String teAnswer, String teAnswerID, int day, ArrayList<Integer> hour, String studentsID, String groupsID, String requestID, String expirationDate, Boolean isValid, Boolean isPermanent)  {
        this.timeStampApproval = timeStampApproval;
        this.teAnswer = teAnswer;
        this.teAnswerID = teAnswerID;
        this.day = day;
        this.hour = hour;
        this.studentsID = studentsID;
        this.groupsID = groupsID;
        this.requestID = requestID;
        this.expirationDate = expirationDate;
        this.isValid = isValid;
        this.isPermanent = isPermanent;
    }

    public Approval(){};

    public String getTimeStampApproval() {
        return timeStampApproval;
    }

    public void setTimeStampApproval(String timeStampApproval) {
        this.timeStampApproval = timeStampApproval;
    }

    public String getTeAnswer() {
        return teAnswer;
    }

    public void setTeAnswer(String teAnswer) {
        this.teAnswer = teAnswer;
    }

    public String getTeAnswerID() {
        return teAnswerID;
    }

    public void setTeAnswerID(String teAnswerID) {
        this.teAnswerID = teAnswerID;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public ArrayList<Integer> getHour() {
        return hour;
    }

    public void setHour(ArrayList<Integer> hour) {
        this.hour = hour;
    }

    public String getStudentsID() {
        return studentsID;
    }

    public void setStudentsID(String studentsID) {
        this.studentsID = studentsID;
    }

    public String getGroupsID() {
        return groupsID;
    }

    public void setGroupsID(String groupsID) {
        this.groupsID = groupsID;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean isValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public Boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(Boolean permanent) {
        isPermanent = permanent;
    }
}
