package com.example.projectfinal_alpha;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Request {
//TODO: UPDATE OBJECT ACCORDING TO THE FIREBASE

    String stuID;
    String requestID;
    String timeStampRequest;
    String reason;
    boolean isTemp;
    int day;
    ArrayList<Integer> hour;
    boolean isPending;
    boolean isApproved;

    public Request(String stuID, String timeStampRequest, String reason, boolean isTemp, int day, ArrayList<Integer> hour, boolean isPending, boolean isApproved) {
        this.stuID = stuID;
        this.timeStampRequest = timeStampRequest;
        this.reason = reason;
        this.isTemp = isTemp;
        this.day = day;
        this.hour = hour;
        this.isPending = isPending;
        this.isApproved = isApproved;
    }
    public Request(){}

    public String getStuID() {
        return stuID;
    }

    public void setStuID(String stuID) {
        this.stuID = stuID;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getTimeStampRequest() {
        return timeStampRequest;
    }

    public void setTimeStampRequest(String timeStampRequest) {
        this.timeStampRequest = timeStampRequest;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
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

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
