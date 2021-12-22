package com.justclick.clicknbook.jctPayment.Models;

public class UserInfo {


    private String id, type, name, iat, exp, iss;

    public UserInfo(String id, String type, String name, String iat, String exp, String iss) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.iat = iat;
        this.exp = exp;
        this.iss = iss;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIat() {
        return iat;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }
}
