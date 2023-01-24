package com.example.projectfinal_alpha;

import java.util.ArrayList;

public class Approval {
    String timeStampApproval;
    String teAnswer;
    String teAnswerID;
    int day;
    ArrayList<Integer> hour;
    ArrayList<String> studentsIDs;
    ArrayList<String> groupsIDs;
    String requestID;

    public Approval(String timeStampApproval, String teAnswer, String teAnswerID, int day, ArrayList<Integer> hour, ArrayList<String> studentsIDs, ArrayList<String> groupsIDs, String requestID) {
        this.timeStampApproval = timeStampApproval;
        this.teAnswer = teAnswer;
        this.teAnswerID = teAnswerID;
        this.day = day;
        this.hour = hour;
        this.studentsIDs = studentsIDs;
        this.groupsIDs = groupsIDs;
        this.requestID = requestID;
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

    public ArrayList<String> getStudentsIDs() {
        return studentsIDs;
    }

    public void setStudentsIDs(ArrayList<String> studentsIDs) {
        this.studentsIDs = studentsIDs;
    }

    public ArrayList<String> getGroupsIDs() {
        return groupsIDs;
    }

    public void setGroupsIDs(ArrayList<String> groupsIDs) {
        this.groupsIDs = groupsIDs;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }


}
