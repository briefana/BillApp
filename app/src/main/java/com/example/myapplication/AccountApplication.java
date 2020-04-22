package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import com.example.myapplication.bean.TypeBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountApplication extends Application {
    private static String TAG = "AJ:AccountApplication";
    public static Map<Integer, String[]> mTypeMap = new HashMap();  //put 2 [早午晚餐,0] 等账本类型，"type_id":2,"type_name":"早午晚餐","tab":0
    public static HashMap<String, String> mUserMap = new HashMap();  //放置 an 安静 等用户，"user_id":"an","user_name":"安静"

    private static AccountApplication mAccountApplication;
    public static String mDefaultUserName;  //默认的用户名，如安静

    @Override
    public void onCreate() {
        super.onCreate();
        mAccountApplication = this;
        initTypeMap();
        initUserMap();
    }

    public void initUserMap() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String usersResult = Utils.getHtml(Utils.getUsersPath());
                    Log.i(TAG, "getUserMap run(),usersResult=" + usersResult);
                    JSONArray jsonArray = new JSONArray(usersResult);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String user_name = jsonObject.getString("user_name");
                        mUserMap.put(jsonObject.getString("user_id"), user_name);  // an 安静
                        if (i == 0) {
                            mDefaultUserName = user_name;
                        }
                    }
                } catch (Exception e) {
                    Log.i(TAG, "catch" + e);
                    e.printStackTrace();
                }
            }

        });
        thread.start();
    }

    public void initTypeMap() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String result = Utils.getHtml(Utils.getTypesPath());
                    Log.i(TAG, "initTypeData(),result=" + result);
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String type_id = jsonObject.getString("type_id");
                        String type_name = jsonObject.getString("type_name");
                        String tab = jsonObject.getString("tab");
                        TypeBean typeBean = new TypeBean(type_id, type_name, tab);
                        mTypeMap.put(jsonObject.getInt("type_id"), new String[]{type_name, tab});  //2 早午晚餐
                    }
                } catch (Exception e) {
                    Log.i(TAG, "catch" + e);
                    e.printStackTrace();
                }
            }

        });
        thread.start();
    }

    public static AccountApplication getApplication() {
        return mAccountApplication;
    }

}
