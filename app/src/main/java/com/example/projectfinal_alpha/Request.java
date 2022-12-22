package com.example.projectfinal_alpha;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Request {
//TODO: UPDATE OBJECT ACCORDING TO THE FIREBASE
    private String name;
    private String timeStamp;
    private String grade;
    private String id;
    private boolean isPermanent;


    public Request(String id, String name, String grade, boolean isPermanent) {
        this.name = name;
        this.id = id;
        this.isPermanent = isPermanent;
        this.grade = grade;
    }
    public Request() {
        this.name = name;
        this.id = id;
        this.isPermanent = isPermanent;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(Boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    public void setCurrentTime() {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
            Date date = new Date();
            this.timeStamp = formatter.format(date);


    }
}
