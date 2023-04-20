package com.example.projectfinal_alpha;

import java.util.ArrayList;

public class Group {
    String TeacherName;
    String teacherID;
    ArrayList<String> studentsIDs;
    ArrayList<String> waitingStudentsIDs;
    String approvalID;
    String groupName;
    String joinCode;

    boolean canLeave;
    String key_id;

    public Group() {
    }

    public Group(String teacherName, String teacherID, String groupName, String joinCode, boolean canLeave) {
        TeacherName = teacherName;
        this.teacherID = teacherID;
        this.groupName = groupName;
        this.joinCode = joinCode;
        this.canLeave = canLeave;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public void setTeacherName(String teacherName) {
        TeacherName = teacherName;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public ArrayList<String> getStudentsIDs() {
        return studentsIDs;
    }

    public void setStudentsIDs(ArrayList<String> studentsIDs) {
        this.studentsIDs = studentsIDs;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public boolean isCanLeave() {
        return canLeave;
    }

    public void setCanLeave(boolean canLeave) {
        this.canLeave = canLeave;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public ArrayList<String> getWaitingStudentsIDs() {
        return waitingStudentsIDs;
    }

    public void setWaitingStudentsIDs(ArrayList<String> waitingStudentsIDs) {
        this.waitingStudentsIDs = waitingStudentsIDs;
    }

    public String getApprovalID() {
        return approvalID;
    }

    public void setApprovalID(String approvalID) {
        this.approvalID = approvalID;
    }

    @Override
    public String toString() {
        return "Group{" +
                "TeacherName='" + TeacherName + '\'' +
                ", teacherID='" + teacherID + '\'' +
                ", studentsIDs=" + studentsIDs +
                ", waitingStudentsIDs=" + waitingStudentsIDs +
                ", approvalID='" + approvalID + '\'' +
                ", groupName='" + groupName + '\'' +
                ", joinCode='" + joinCode + '\'' +
                ", canLeave=" + canLeave +
                ", key_id='" + key_id + '\'' +
                '}';
    }
}
