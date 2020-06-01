package com.example.myapplication.bean;

public class AccountAssetsBean {

    public AccountAssetsBean(String userId, String lastSetTime, String lastSetBalance, String userName, String currentBalance) {
        mLastSetTime = lastSetTime;
        mLastSetBalance = lastSetBalance;
        mUserId = userId;
        mUserName = userName;
        mCurrentBalance = currentBalance;
    }

    /* 上次设置的余额 */
    private String mCurrentBalance;

    public void setCurrentBalance(String currentBalance) {
        mCurrentBalance = currentBalance;
    }

    public String getCurrentBalance() {
        return mCurrentBalance;
    }

    /* 上次设置的余额 */
    private String mLastSetBalance;

    public String getLastSetBalance() {
        return mLastSetBalance;
    }

    /* 上次设置余额的时间 */
    private String mLastSetTime;

    public String getLastSetTime() {
        return mLastSetTime;
    }

    /* user_id */
    private String mUserId;

    public String getUserId() {
        return mUserId;
    }

    /* user_name */
    private String mUserName;

    public String getUserName() {
        return mUserName;
    }

}
