package com.example.projectfinal_alpha;

import java.util.ArrayList;

public class Teacher {
    private String name;
    private String userType;
    private ArrayList<String> groups;

    public Teacher() {
    }

    public Teacher(String name, String userType) {
        this.name = name;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }
}
