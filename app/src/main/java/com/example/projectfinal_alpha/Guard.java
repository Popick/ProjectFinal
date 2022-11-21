package com.example.projectfinal_alpha;

public class Guard {
    private String NAME;
    private String USERTYPE;

    public Guard() {
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getUSERTYPE() {
        return USERTYPE;
    }

    public void setUSERTYPE(String USERTYPE) {
        this.USERTYPE = USERTYPE;
    }

    public Guard(String NAME, String USERTYPE) {
        this.NAME = NAME;
        this.USERTYPE = USERTYPE;
    }
}
