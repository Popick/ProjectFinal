package com.example.projectfinal_alpha;

import java.io.Serializable;

/**
 * @author Etay Sabag <itay45520@gmail.com>
 * @version 1.0
 * @since 10/17/2022
 * Constants for the users' table in the data base
 */
public class Student implements Serializable {
    private String NAME;
    private String GRADE;
    private String CLASS;
    private String USERTYPE;
    private String GROUP1;
    private String GROUP2;
    private String GROUP3;
    private String QR_DATA;
    private Boolean ALLOWED;

    public Student() {
    }


    public Student(String NAME, String GRADE, String CLASS, String GROUP1, String GROUP2, String GROUP3, String USERTYPE, String QR_DATA, Boolean ALLOWED) {
        this.NAME = NAME;
        this.GRADE = GRADE;
        this.CLASS = CLASS;
        this.GROUP1 = GROUP1;
        this.GROUP2 = GROUP2;
        this.GROUP3 = GROUP3;
        this.QR_DATA = QR_DATA;
        this.USERTYPE = USERTYPE;
        this.ALLOWED = ALLOWED;
    }

    public String getUSERTYPE() {
        return USERTYPE;
    }

    public void setUSERTYPE(String USERTYPE) {
        this.USERTYPE = USERTYPE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getGRADE() {
        return GRADE;
    }

    public void setGRADE(String GRADE) {
        this.GRADE = GRADE;
    }

    public String getCLASS() {
        return CLASS;
    }

    public void setCLASS(String CLASS) {
        this.CLASS = CLASS;
    }

    public String getGROUP1() {
        return GROUP1;
    }

    public void setGROUP1(String GROUP1) {
        this.GROUP1 = GROUP1;
    }

    public String getGROUP2() {
        return GROUP2;
    }

    public void setGROUP2(String GROUP2) {
        this.GROUP2 = GROUP2;
    }

    public String getGROUP3() {
        return GROUP3;
    }

    public void setGROUP3(String GROUP3) {
        this.GROUP3 = GROUP3;
    }

    public String getQR_DATA() {
        return QR_DATA;
    }

    public void setQR_DATA(String QR_DATA) {
        this.QR_DATA = QR_DATA;
    }

    public Boolean isALLOWED() {
        return ALLOWED;
    }

    public void setALLOWED(Boolean ALLOWED) {
        this.ALLOWED = ALLOWED;
    }

    @Override
    public String toString() {
        return "Student{" +
                "NAME='" + NAME + '\'' +
                ", GRADE='" + GRADE + '\'' +
                ", CLASS='" + CLASS + '\'' +
                ", GROUP1='" + GROUP1 + '\'' +
                ", GROUP2='" + GROUP2 + '\'' +
                ", GROUP3='" + GROUP3 + '\'' +
                ", QR_DATA='" + QR_DATA + '\'' +
                ", ALLOWED=" + ALLOWED +
                '}';
    }


}
