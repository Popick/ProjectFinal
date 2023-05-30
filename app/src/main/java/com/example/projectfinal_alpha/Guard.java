package com.example.projectfinal_alpha;

public class Guard {
    String Name;
    String userType;

    public Guard() {
    }

    public Guard(String name, String userType) {
        Name = name;
        this.userType = userType;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
