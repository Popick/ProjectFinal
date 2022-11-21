package com.example.projectfinal_alpha;

public class Teacher {
        private String NAME;
        private String GRADE;
        private String CLASS;
        private String USERTYPE;
        private String LEVEL;

        public Teacher() {
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

    public String getUSERTYPE() {
        return USERTYPE;
    }

    public void setUSERTYPE(String USERTYPE) {
        this.USERTYPE = USERTYPE;
    }

    public String getLEVEL() {
        return LEVEL;
    }

    public void setLEVEL(String LEVEL) {
        this.LEVEL = LEVEL;
    }

    public Teacher(String NAME, String GRADE, String CLASS, String USERTYPE, String LEVEL) {
            this.NAME = NAME;
            this.GRADE = GRADE;
            this.CLASS = CLASS;
            this.USERTYPE = USERTYPE;
            this.LEVEL = LEVEL;
        }

}
