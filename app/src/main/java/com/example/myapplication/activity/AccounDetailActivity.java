package com.example.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.AccountApplication;
import com.example.myapplication.DBHelper;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.view.WordWrapView;
import com.example.myapplication.bean.AccountBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AccounDetailActivity extends Activity implements View.OnClickListener {
    private DBHelper mHelper;
    private SQLiteDatabase mDatabase;
    private static String TAG = "AccounDetailActivity:AJ";
    private TextView mDateTv;
    private TextView mTypeTextView;
    private ImageView mTypeImageView;
    private int mTypeId = -1;
    private TextView mMoneyTv;
    private EditText mNoteTv;
    private Button mSaveBt;
    private Button mDeleteBt;
    private AccountBean mAccount;  //从 DetailMonthAdapter 的 getView 的条目点击事件中传来的对象
    private TextView mTvUser;
    private View mDivider;
    private LinearLayout mTypePay;

    private WordWrapView mWordWrapView;
    private String mBillId;
    private AccounDetailActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_account_detail);
        Log.d(TAG, "onCreate()");

        mContext = this;

        mDateTv = findViewById(R.id.date);
        mTypeTextView = findViewById(R.id.type);
        mMoneyTv = findViewById(R.id.money);
        mNoteTv = findViewById(R.id.note);
        mDeleteBt = findViewById(R.id.delete);
        mSaveBt = findViewById(R.id.save);
        mWordWrapView = findViewById(R.id.wordwrap);
        mTypeImageView = findViewById(R.id.iv_type);
        mTvUser = findViewById(R.id.tv_user);
        mDivider = findViewById(R.id.divider);
        mTypePay = findViewById(R.id.ll_big_type);

        findViewById(R.id.iv_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText("日常账本");

        mDateTv.setOnClickListener(mContext);
        mDeleteBt.setOnClickListener(mContext);
        mSaveBt.setOnClickListener(mContext);
        mTvUser.setOnClickListener(mContext);

        if (AccountApplication.mUserMap.size() == 0) {
            AccountApplication.getApplication().initUserMap();
        }

        mAccount = (AccountBean) getIntent().getSerializableExtra("account");
        if (mAccount != null) {
            String time = mAccount.getTime(); //2020-03-05 周四 16:34
            mBillId = mAccount.getBillId();
            mDateTv.setText(time);

            mTypeId = mAccount.getTypeId();  //如果 Account 不为 null，即从某一账单条目点击过来的，便赋值 mTypeId
            String[] typeArr = AccountApplication.mTypeMap.get(mTypeId);
            mTypeTextView.setText(typeArr[0]);
            mTypeImageView.setImageDrawable(getResources().getDrawable(Utils.getDrawableId(typeArr[0])));
            initBigTypeView(Integer.parseInt(typeArr[1]));

            mMoneyTv.setText(mAccount.getMoney() + "");
            mNoteTv.setText(mAccount.getNote());

            String user_name = AccountApplication.mUserMap.get(mAccount.getPerson());
            mTvUser.setText(user_name);
        } else {
            mTvUser.setText(AccountApplication.mDefaultUserName); //未传 Account ，是新建账本，则默认显示第一个用户，如安静
            initBigTypeView(0); //未传 Account ，是新建账本，则默认类型为支出
        }
        Log.d(TAG, "onCreate(),mAccount=" + mAccount + ",mTvUser=" + mTvUser.getText());

        mHelper = new DBHelper(this);
        mDatabase = mHelper.getWritableDatabase();
    }

    int selectChildIndex;

    private void initSmallTypeView(int setTab) {
        Log.d(TAG, "initSmallTypeView(),setTab=" + setTab + ",start---------------------------");
        selectChildIndex = -1;
        int childIndex = -1;
        mWordWrapView.removeAllViews();
        for (final int key : AccountApplication.mTypeMap.keySet()) {
            String[] typeArr = AccountApplication.mTypeMap.get(key);
            final String tab = typeArr[1];

            if (setTab == Integer.parseInt(tab)) {
                final String type_name = typeArr[0];
                childIndex++;  //0、1、2....
                final TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.type_item, null);
                view.setText(type_name);
                if (key == mTypeId) {
                    view.setSelected(true);
                    selectChildIndex = childIndex;
                }
                final int finalChildIndex = childIndex;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTypeTextView.setText(type_name);
                        mTypeId = key;   //点击某一类别时，设置下 mTypeId
                        view.setSelected(true);
                        mTypeImageView.setImageDrawable(getResources().getDrawable(Utils.getDrawableId(type_name)));

                        if (selectChildIndex != -1) {
                            TextView childAt = (TextView) mWordWrapView.getChildAt(selectChildIndex);
                            childAt.setSelected(false);
                        }
                        selectChildIndex = finalChildIndex;
                    }
                });
                mWordWrapView.addView(view);
            }
        }
    }

    private int mSelectBigType = -1;

    private void initBigTypeView(int tab) {
        String[] types = new String[]{"支 出", "收 入", "转 账"};
        Log.d(TAG, "initBigType()");
        for (int i = 0; i < types.length; i++) {
            final int finalIndex = i;
            final View view = LayoutInflater.from(mContext).inflate(R.layout.big_type_item, null);
            final View vDivider = view.findViewById(R.id.v_divider);
            final TextView tvType = view.findViewById(R.id.tv_type);
            tvType.setText(types[finalIndex]);

            if (tab == i) {
                tvType.setSelected(true);
                vDivider.setVisibility(View.VISIBLE);
                mSelectBigType = tab;
                initSmallTypeView(tab);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvType.setSelected(true);
                    vDivider.setVisibility(View.VISIBLE);
                    initSmallTypeView(finalIndex);
                    Log.d(TAG, "initBigTypeView onClick(),finalIndex=" + finalIndex + ",mSelectBigType=" + mSelectBigType);
                    if (mSelectBigType != -1) {
                        View childAt = mTypePay.getChildAt(mSelectBigType);
                        TextView tv_type = childAt.findViewById(R.id.tv_type);
                        View v_divider = childAt.findViewById(R.id.v_divider);
                        tv_type.setSelected(false);
                        v_divider.setVisibility(View.INVISIBLE);
                    }
                    mSelectBigType = finalIndex;
                }
            });

            mTypePay.addView(view);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Log.d(TAG, "onClick(),view=" + view);
        if (id == R.id.date) {
            insertDateAnTime();
        } else if (id == R.id.delete) {
            delete();
        } else if (id == R.id.save) {
            save();
        } else if (id == R.id.tv_user) {
            setMoreDialog();
        } else if (id == R.id.iv_back) {
            finish();
        }

    }

    private void setMoreDialog() {
        final ArrayList<String> itemList = new ArrayList();
        Iterator it = AccountApplication.mUserMap.entrySet().iterator();
        String[] itemArr = new String[AccountApplication.mUserMap.size()];
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String user_id = (String) entry.getKey();
            String user_name = AccountApplication.mUserMap.get(user_id);
            itemList.add(user_name);
            itemArr[itemList.size() - 1] = user_name;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setItems(itemArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user_name = itemList.get(which);
                mTvUser.setText(user_name);  //点击时，选择不同user
                Log.i(TAG, "setMoreDialog run(),name=" + user_name);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void insertDateAnTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.date_time_dialog, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        builder.setView(view);
        builder.setTitle("选取时间");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = datePicker.getYear();

                int month = datePicker.getMonth() + 1;
                String monthStr = Utils.addZero(month);

                int day = datePicker.getDayOfMonth();
                String dayStr = Utils.addZero(day);


                int hour = timePicker.getHour();
                String hourStr = Utils.addZero(hour);
                int minute = timePicker.getMinute();
                String minuteStr = Utils.addZero(minute);

                String strDate = year + "-" + monthStr + "-" + dayStr;
                final Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date strD = simpleDateFormat.parse(strDate);
                    calendar.setTime(strD);
                    int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    if (weekIndex < 0) {
                        weekIndex = 0;
                    }
                    String week = Utils.getWeekStr(weekIndex);
                    String time = strDate + " " + week + " " + hourStr + ":" + minuteStr; //2020-03-09 周一 08:09
                    mDateTv.setText(time);
                    Log.d(TAG, "insertDateAnTime onDateSet(),time=" + time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void delete() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(mBillId)) {
                        return;
                    }
                    final Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("bill_id", mBillId);
                    String result = Utils.doPost(Utils.getDeletePath(), paramsMap);
                    Log.i(TAG, result);
                    mTypeTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (Exception e) {
                    Log.i(TAG, "catch" + e);
                    e.printStackTrace();
                }
            }

        });
        thread.start();
    }

    private void save() {
        String money = mMoneyTv.getText().toString();
        String user_name = mTvUser.getText().toString();
        String user_id = "";
        Iterator it = AccountApplication.mUserMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = AccountApplication.mUserMap.get(key);
            if (value.equals(user_name)) {
                user_id = key;
                break;
            }
        }

        final String date = mDateTv.getText().toString();
        String note = mNoteTv.getText().toString();
        String dateData = date.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
        dateData = dateData.replace("周日", "0");
        dateData = dateData.replace("周一", "1");
        dateData = dateData.replace("周二", "2");
        dateData = dateData.replace("周三", "3");
        dateData = dateData.replace("周四", "4");
        dateData = dateData.replace("周五", "5");
        dateData = dateData.replace("周六", "6");

        if (TextUtils.isEmpty(user_id)) {
            Toast.makeText(this, "请输入账户", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mTypeId == -1) {
            Toast.makeText(this, "请选择支出类型", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isFloat(money)) {
            Toast.makeText(this, "请输入正确的数字格式", Toast.LENGTH_SHORT).show();
            return;
        }

//        insertLocalData(dateData, type, Float.parseFloat(money), person, note);

        final Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("time", dateData);  //paramsMap.put("time", dateData);
        paramsMap.put("type_id", mTypeId + "");  //9
        paramsMap.put("money", money);  //120.0
        paramsMap.put("user_id", user_id);  //cai
        paramsMap.put("note", note);  //正药堂，口罩？
        Log.i(TAG, "save(),time=" + dateData + ",type_id=" + mTypeId
                + ",money=" + money + ",user_id=" + user_id + ",note=" + note);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path;
                    if (TextUtils.isEmpty(mBillId)) {
                        path = Utils.getInsertPath();
                    } else {
                        paramsMap.put("bill_id", mBillId);
                        path = Utils.getUpdatePath();
                    }
                    String result = Utils.doPost(path, paramsMap);  //71
                    Log.i(TAG, "save(),mBillId=" + mBillId + ",result=" + result);
                } catch (Exception e) {
                    Log.i(TAG, "catch" + e);
                    e.printStackTrace();
                }
                mTypeTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(mBillId)) {
                            Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                        } else {
                            paramsMap.put("bill_id", mBillId);
                            Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(mContext, MonthDetailActivity.class);
                        String toMonth = date.substring(0, 7).replaceAll("-", "");
                        intent.putExtra("yearmonth", toMonth);  //格式 201912
                        intent.putExtra("needUpdate", true);
                        intent.putExtra("tab", 0); //0指支出、1指收入、2指转账、3指全部
                        Log.d(TAG, "save(),startActivity MonthDetailActivity,toMonth=" + toMonth);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
        thread.start();
    }

    private boolean isFloat(String money) {
        try {
            float moneyFloat = Float.parseFloat(money);
            Log.d(TAG, "isFloat(),true moneyFloat=" + moneyFloat);
            if (moneyFloat > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "isFloat(),false money=" + money);
            return false;
        }
    }

    // 表名
// null。数据库如果插入的数据为null，会引起数据库不稳定。为了防止崩溃，需要传入第二个参数要求的对象
// 如果插入的数据不为null，没有必要传入第二个参数避免崩溃，所以为null
// 插入的数据
    private void insertLocalData(String time, String type, float money, String person, String note) {
        Log.d(TAG, "insertData(),time=" + time + ",type=" + type + ",money=" + money + ",person=" + person + ",note=" + note);
        ContentValues values = new ContentValues();
        values.put(DBHelper.TIME, time);
        values.put(DBHelper.TYPE, type);
        values.put(DBHelper.MONEY, money);
        values.put(DBHelper.PERSON, person);
        values.put(DBHelper.NOTE, note);
        long insert = mDatabase.insert(DBHelper.TABLE_NAME, null, values);
        Toast.makeText(this, "插入成功=" + insert, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext, MonthDetailActivity.class);
        String toMonth = time.substring(0, 6);
        intent.putExtra("yearmonth", toMonth);  //格式 201912
        intent.putExtra("tab", 0); //0指支出、1指收入、2指转账、3指全部
        Log.d(TAG, "insertData(),startActivity MonthDetailActivity,toMonth=" + toMonth);
        startActivity(intent);
    }

    // 表名
// 删除条件
// 满足删除的值
    private void deleteData() {
        int count = mDatabase.delete(DBHelper.TABLE_NAME, DBHelper.TIME + " = ?", new String[]{"鹿晗"});
        Toast.makeText(this, "删除数量：" + count, Toast.LENGTH_SHORT).show();
    }

    // 表名
// 修改后的数据
// 修改条件
// 满足修改的值
    private void updateData() {
        ContentValues values = new ContentValues();
        values.put(DBHelper.TIME, "小茗同学");
        int count = mDatabase
                .update(DBHelper.TABLE_NAME, values, DBHelper.TIME + " = ?", new String[]{"鹿晗"});
        Toast.makeText(this, "修改成功：" + count, Toast.LENGTH_SHORT).show();
    }

    // 表名
// 查询字段
// 查询条件
// 满足查询的值
// 分组
// 分组筛选关键字
// 排序
    private void queryData() {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME,
                new String[]{DBHelper.TIME, DBHelper.PERSON},
                DBHelper.TYPE + " > ?",
                new String[]{"16"},
                null,
                null,
                DBHelper.TIME + " desc");// 注意空格！

        int nameIndex = cursor.getColumnIndex(DBHelper.PERSON);
        int ageIndex = cursor.getColumnIndex(DBHelper.MONEY);
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String age = cursor.getString(ageIndex);

            Log.d("1507", "name: " + name + ", age: " + age);
        }
    }

}
