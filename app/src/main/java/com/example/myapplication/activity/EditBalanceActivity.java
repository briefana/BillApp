package com.example.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AccountApplication;
import com.example.myapplication.Constans;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.adapter.LastSetAssetsAdapter;
import com.example.myapplication.bean.AccountAssetsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class EditBalanceActivity extends Activity implements View.OnClickListener {
    private static String TAG = "EditBalanceActivity:AJ";
    private Button mBtSave;
    private ListView mLvLastSetAssets;
    private EditBalanceActivity mContext;
    private HashMap<String, Float> mCurrentBalanceMap;
    private String mLastSetBalanceTime;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int what = msg.what;
            if (what == 0) {
                saveAccountDialog(mSb);
            } else if (what == 1) {
                Toast.makeText(mContext, "未修改余额", Toast.LENGTH_SHORT).show();
            } else if (what == 3) {
                Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String mBalanceresult = Utils.doGet(Utils.getShowUserBalancePath("my"));
                            queryLastSetAssetsData(mBalanceresult);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else if (what == 4) {
                Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private StringBuilder mSb;
    private ArrayList<Map<String, String>> mMapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_edit_balance);
        Log.d(TAG, "onCreate()");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        ((TextView) findViewById(R.id.tv_title)).setText("总资产");
        iv_back.setOnClickListener(this);

        mContext = this;
        mBtSave = findViewById(R.id.save);
        mLvLastSetAssets = findViewById(R.id.lv_last_set_assets);
        mBtSave.setOnClickListener(this);

        Intent intent = getIntent();
        String balanceresult = intent.getStringExtra("balanceresult");
        Log.i(TAG, "onCreate(),balanceresult=" + balanceresult);

        queryLastSetAssetsData(balanceresult);
    }

    private void queryLastSetAssetsData(final String balanceresult) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String lastSetBalanceResult = Utils.doGet(Utils.getShowLastUsersBalancePath("my"));  //查询上次设置家庭用户余额
                    mLvLastSetAssets.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray lastSetBalanceJsonArray = new JSONArray(lastSetBalanceResult);  //上次设置的余额
                                int lastSetBalanceJsonLength = lastSetBalanceJsonArray.length();

                                JSONArray balanceJsonArray = new JSONArray(balanceresult);  //当前余额
                                int balanceJsonLength = balanceJsonArray.length();

                                ArrayList<AccountAssetsBean> list = new ArrayList();
                                float lastTotalBalance = 0;
                                float currentTotalBalance = 0;
                                mCurrentBalanceMap = new HashMap();
                                LastSetAssetsAdapter adapter = new LastSetAssetsAdapter(list, mContext);
                                for (int i = 0; i < lastSetBalanceJsonLength; i++) {
                                    JSONObject jsonObject = lastSetBalanceJsonArray.getJSONObject(i);
                                    final String user_id = jsonObject.getString("user_id");
                                    String user_name = AccountApplication.mUserIdNameMap.get(user_id);
                                    final String time = jsonObject.getString("time");
                                    mLastSetBalanceTime = time.substring(0, 4) + "/" + time.substring(4, 6) + "/" + time.substring(6, 8);
                                    adapter.setLastBalanceTiem(mLastSetBalanceTime);
                                    final String lastSetBalance = jsonObject.getString("balance");
                                    lastTotalBalance += Float.parseFloat(lastSetBalance);

                                    for (int j = 0; j < balanceJsonLength; j++) {
                                        JSONObject balanceJsonObject = balanceJsonArray.getJSONObject(j);
                                        if (user_id.equals(balanceJsonObject.getString("user_id"))) {
                                            final String balance = balanceJsonObject.getString("bmoney");
                                            float balanceFloat = Float.parseFloat(balance);
                                            mCurrentBalanceMap.put(user_id, balanceFloat);
                                            currentTotalBalance += balanceFloat;
                                            AccountAssetsBean bean = new AccountAssetsBean(user_id, "", lastSetBalance, user_name, balance);
                                            Log.i(TAG, "queryLastSetAssetsData(),list.add(bean),time=" + time);
                                            list.add(bean);
                                            break;
                                        }
                                    }
                                }
                                AccountAssetsBean bean = new AccountAssetsBean("", "", lastTotalBalance + "", "", currentTotalBalance + "");
                                Log.i(TAG, "queryLastSetAssetsData(),list.add(bean) total");
                                list.add(bean);
                                mLvLastSetAssets.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Log.i(TAG, "queryLastSetAssetsData(),lastSetBalanceResult=" + lastSetBalanceResult);
                } catch (Exception e) {
                    Log.i(TAG, "catch" + e);
                    e.printStackTrace();
                }
            }

        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.i(TAG, "onClick(),id=" + id);
        if (id == R.id.save) {
            setBalance();
        } else if (id == R.id.iv_back) {
            finish();
        }
    }

    private void setBalance() {
        final int childCount = mLvLastSetAssets.getChildCount();
        mSb = new StringBuilder(mLastSetBalanceTime + "至今" + "\n");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mMapList = new ArrayList();
                for (int i = 1; i < childCount - 1; i++) {
                    RelativeLayout childAt = (RelativeLayout) mLvLastSetAssets.getChildAt(i);
                    TextView tvUserName = (TextView) childAt.getChildAt(0);
                    final String userName = tvUserName.getText().toString();
                    final String userId = AccountApplication.mUserNameIdMap.get(userName);
                    EditText tvBalance = (EditText) childAt.getChildAt(1);
                    final String balance = tvBalance.getText().toString();

                    final Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("family_id", "my");
                    paramsMap.put("user_id", userId);
                    String time = getNowDateStr();
                    paramsMap.put("time", time);
                    paramsMap.put("balance", balance);
                    final String path = Utils.getUpdateUserBalancePath();

                    try {
                        float setBalanceFloat = Float.parseFloat(balance);
                        Float currentBalance = mCurrentBalanceMap.get(userId);

                        float gapBalance = currentBalance - setBalanceFloat;
                        if (gapBalance > 0) {
                            final Map<String, String> accountParamsMap = new HashMap<>();
                            accountParamsMap.put("time", time);
                            accountParamsMap.put("type_id", 14 + "");
                            accountParamsMap.put("money", gapBalance + "");
                            accountParamsMap.put("user_id", userId);
                            accountParamsMap.put("note", mLastSetBalanceTime + "到" + time.substring(0, 4) + "/" + time.substring(4, 6) + "/" + time.substring(6, 8) + "的零碎未记的支出总额"); //返回格式2020040160545,即 2020 04 01 6 0545
                            mSb.append(userName + "的余额差值为" + gapBalance + "\n");
                            mMapList.add(accountParamsMap);

                            String result = Utils.doPost(path, paramsMap);  //余额有变动，才去设置
                            Log.i(TAG, "setBalance(),result=" + result);
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "catch" + e);
                        e.printStackTrace();
                    }
                }
                mSb.append("是否记录为零碎未记?");
                if (mMapList.size() > 0) {
                    mHandler.sendEmptyMessage(0);
                } else {
                    mHandler.sendEmptyMessage(1);
                }
                Log.i(TAG, "setBalance(),sb=" + mSb.toString());
            }
        });
        thread.start();
    }

    private void saveAccountDialog(StringBuilder sb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("余额设置成功");
        builder.setMessage(sb.toString());
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "saveAccountDialog(),onClick(),setPositiveButton");
                save();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void save() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = Utils.getInsertPath();
                    int responseCode = -1;
                    for (int i = 0; i < mMapList.size(); i++) {
                        Map<String, String> map = mMapList.get(i);
                        responseCode = Utils.doPostReturnResponseCode(path, map);
                        Log.i(TAG, "save(),i=" + i + ",responseCode=" + responseCode);
                    }
                    mHandler.sendEmptyMessage(3);
                    if (responseCode == 200) {
                        mHandler.sendEmptyMessage(3);
                    } else {
                        mHandler.sendEmptyMessage(4);
                    }
                } catch (Exception e) {
                    Log.i(TAG, "catch" + e);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static String getNowDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constans.sFormatDetailDate);  //202004010545,即 2020 04 01 0545
        Date today = new Date();  //当前日期

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekIndex < 0) {
            weekIndex = 0;
        }
        Log.i(TAG, "setBalance(),weekIndex=" + weekIndex);
        String date = sdf.format(today); //202004010545,即 2020 04 01 0545
        return date.substring(0, 8) + weekIndex + date.substring(8);    // 2020/03/28   返回格式2020040160545,即 2020 04 01 6 0545
    }

}
