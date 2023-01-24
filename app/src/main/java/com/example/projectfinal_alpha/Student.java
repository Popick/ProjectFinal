package com.example.projectfinal_alpha;

import static com.example.projectfinal_alpha.FBref.refApprovals;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
    private ArrayList<Integer> groups;
    private String QR_data;
    private String approvalID;
    private String lastRequest;

    public boolean isAllowed = false;

    public Student() {
    }

    public Student(String name, String grade, String aClass, String userType, ArrayList<Integer> groups, String QR_data, String approvalID) {
        this.name = name;
        this.grade = grade;
        this.aClass = aClass;
        this.userType = userType;
        this.groups = groups;
        this.QR_data = QR_data;
        this.approvalID = approvalID;


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

    public ArrayList<Integer> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Integer> groups) {
        this.groups = groups;
    }

    public String getQR_data() {
        return QR_data;
    }

    public void setQR_data(String QR_data) {
        this.QR_data = QR_data;
    }

    public String getApprovalID() {
        return approvalID;
    }

    public void setApprovalID(String approvalID) {
        this.approvalID = approvalID;
    }

    public String getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    public void checkApproval(){

        refApprovals.child(approvalID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Approval appTemp = dataSnapshot.getValue(Approval.class);
                if (appTemp != null) {

                    Log.d("boolboolean",!Helper.isMoreThan30Minutes(appTemp.getTimeStampApproval())+" --> isMoreThan30Minutes");
                    Log.d("boolboolean",(Helper.getClassNumber(appTemp.getTimeStampApproval()) == Helper.getClassNumber(Helper.getCurrentDateString()))+" --> getClassNumber");

                    isAllowed = (!Helper.isMoreThan30Minutes(appTemp.getTimeStampApproval()) || ((Helper.getClassNumber(appTemp.getTimeStampApproval())
                            == Helper.getClassNumber(Helper.getCurrentDateString()) && Helper.getClassNumber(Helper.getCurrentDateString()) != -1)));

                } else {

                    isAllowed = false;
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("failed", "Failed to read value.", error.toException());
                isAllowed = false;

            }

        });
        Log.d("boolean","is allowed on data change "+ isAllowed);

    }

}
