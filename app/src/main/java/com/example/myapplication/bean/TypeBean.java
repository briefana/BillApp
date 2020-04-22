package com.example.myapplication.bean;

public class TypeBean {
    private String mUserId;
    private String mUserName;
    private String mTab;

    public TypeBean(String userId, String userName, String tab) {
        mUserId = userId;
        mUserName = userName;
        mTab = tab;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getTab() {
        return mTab;
    }

    public void setTab(String tab) {
        mTab = tab;
    }
}
