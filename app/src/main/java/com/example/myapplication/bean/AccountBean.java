package com.example.myapplication.bean;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class AccountBean implements Serializable {

    public AccountBean(String time, int typeId, float money, String userId, String note, String billId, String tab, String getUserId) {
        mTime = time;
        mTypeId = typeId;
        mMoney = money;
        mUserId = userId;
        mNote = note;
        mBillId = billId;
        mTab = tab;
        mGetUserId = getUserId;
    }

    public AccountBean(int typeId, String typeName, float money/*, String getUserId*/) {
        mTypeId = typeId;
        mMoney = money;
        mTypeName = typeName;
        /*mGetUserId = getUserId;*/
    }

    @NonNull
    @Override
    public String toString() {
        return "toString(),time=" + mTime + ",typeId=" + mTypeId + ",money=" + mMoney + ",person=" + mUserId + ",note=" + mNote + ",mBillId=" + mBillId + ",mTab=" + mTab;
    }

    private String mTab;

    public String getTab() {
        return mTab;
    }

    private String mBillId;

    public String getBillId() {
        return mBillId;
    }

    /*时间*/
    private String mTime; //2020-03-05 周四 16:34

    public String getTime() {
        return mTime;
    }

    private String mTypeName;

    public String getTypeName() {
        return mTypeName;
    }

    /*支出类型：房贷或房租水电、护肤品、衣包鞋等*/
    private int mTypeId;


    public int getTypeId() {
        return mTypeId;
    }

    /*支出金额*/
    private float mMoney;

    public float getMoney() {
        return mMoney;
    }

    public void setMoney(float money) {
        mMoney = money;
    }

    /*谁花出*/
    private String mUserId;

    public String getUserId() {  //an
        return mUserId;
    }

    /* 相互转账的话，收账人是谁 */
    private String mGetUserId;

    public String getGetUserId() {  //an
        return mGetUserId;
    }

    /*备注*/
    private String mNote;

    public String getNote() {
        return mNote;
    }

}
