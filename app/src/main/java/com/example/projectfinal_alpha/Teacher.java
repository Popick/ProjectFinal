package com.example.projectfinal_alpha;

public class Teacher {
        private String name;
        private String userType;
        private String level;

        public Teacher() {
        }

    public Teacher(String name, String userType, String level) {
        this.name = name;
        this.userType = userType;
        this.level = level;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
