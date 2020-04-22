package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Utils;

import androidx.appcompat.app.AppCompatActivity;

public class SelectTimeActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "SelectTimeActivity:AJ";
    private LinearLayout mLl_start_time;
    private TextView mTv_start_time;
    private LinearLayout mLl_end_time;
    private TextView mTv_end_time;
    private DatePicker mDate_picker;
    private CheckBox mCbCustom;
    private boolean mSetStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        getSupportActionBar().hide();
        setContentView(R.layout.activity_select_time);

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        ((TextView) findViewById(R.id.tv_title)).setText("时间");
        TextView tv_this_month = (TextView) findViewById(R.id.tv_this_month);
        TextView tv_last_month = (TextView) findViewById(R.id.tv_last_month);
        TextView tv_this_quarter = (TextView) findViewById(R.id.tv_this_quarter);
        TextView tv_last_quarter = (TextView) findViewById(R.id.tv_last_quarter);
        TextView tv_this_year = (TextView) findViewById(R.id.tv_this_year);
        TextView tv_last_year = (TextView) findViewById(R.id.tv_last_year);

        iv_back.setOnClickListener(this);
        tv_this_month.setOnClickListener(this);
        tv_last_month.setOnClickListener(this);
        tv_this_quarter.setOnClickListener(this);
        tv_last_quarter.setOnClickListener(this);
        tv_this_year.setOnClickListener(this);
        tv_last_year.setOnClickListener(this);

        mLl_start_time = (LinearLayout) findViewById(R.id.ll_start_time);
        mTv_start_time = (TextView) findViewById(R.id.tv_start_time);
        mLl_end_time = (LinearLayout) findViewById(R.id.ll_end_time);
        mTv_end_time = (TextView) findViewById(R.id.tv_end_time);
        mDate_picker = (DatePicker) findViewById(R.id.date_picker);
        mCbCustom = (CheckBox) findViewById(R.id.cb_custom);

        mLl_start_time.setOnClickListener(this);
        mLl_end_time.setOnClickListener(this);

        mCbCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLl_start_time.setVisibility(View.VISIBLE);
                    mLl_end_time.setVisibility(View.VISIBLE);
                    mDate_picker.setVisibility(View.VISIBLE);
                } else {
                    mLl_start_time.setVisibility(View.GONE);
                    mLl_end_time.setVisibility(View.GONE);
                    mDate_picker.setVisibility(View.GONE);
                }
            }
        });

        mDate_picker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month = monthOfYear + 1;
                String monthStr = Utils.addZero(month);
                String dayStr = Utils.addZero(dayOfMonth);
                String date = year + "/" + monthStr + "/" + dayStr;
                Log.d(TAG, "onDateChanged(),date=" + date);
                if (mSetStart) {
                    mTv_start_time.setText(date);
                } else {
                    mTv_end_time.setText(date);
                }
            }
        });

        setDefaultDate();
    }

    private void setDefaultDate() {
        String nowDate = Utils.getNowDateStr();    // 2020/03/28
        Log.d(TAG, "setDefaultDate(),nowDate=" + nowDate);
        mTv_start_time.setText(nowDate);
        mTv_end_time.setText(nowDate);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.d(TAG, "onClick(),v=" + v);
        if (id == R.id.iv_back) {
            if (mCbCustom.isChecked()) {
                setCustomDate();
            } else {
                finish();
            }
        } else if (id == R.id.tv_this_month) { //选择本月
            setCurrentMonth();
        } else if (id == R.id.tv_last_month) { //选择上月
            setLastMonth();
        } else if (id == R.id.tv_this_quarter) { //选择本季
            setCurrentQuarter();
        } else if (id == R.id.tv_last_quarter) { //选择上季
            setLastQuarter();
        } else if (id == R.id.tv_this_year) { //选择本年
            setCurrentYear();
        } else if (id == R.id.tv_last_year) { //选择上年
            setLastYear();
        } else if (id == R.id.ll_start_time) {
            setBackground(true);
        } else if (id == R.id.ll_end_time) {
            setBackground(false);
        }
    }

    private void setCurrentQuarter() { //选择本季
        String nowDate = Utils.getNowDateStr();      // 2020/03/28
        int nowYear = Integer.parseInt(nowDate.substring(0, 4));
        int nowMonth = Integer.parseInt(nowDate.substring(5, 7));
        String startMonth;
        String endMonth = Utils.addZero(nowMonth);
        if (nowMonth >= 1 && nowMonth <= 3) {
            startMonth = "01";
        } else if (nowMonth >= 4 && nowMonth <= 6) {
            startMonth = "04";
        } else if (nowMonth >= 7 && nowMonth <= 9) {
            startMonth = "07";
        } else {
            startMonth = "10";
        }
        String startTime = nowYear + "/" + startMonth;
        String endTime = nowYear + "/" + endMonth;
        startDetailActivity(startTime, endTime, startTime + "-" + endTime);
    }

    private void setLastQuarter() { //选择上季
        String nowDate = Utils.getNowDateStr();      // 2020/03/28
        int nowYear = Integer.parseInt(nowDate.substring(0, 4));
        int nowMonth = Integer.parseInt(nowDate.substring(5, 7));
        String startTime;
        String endTime;
        if (nowMonth >= 1 && nowMonth <= 3) {
            startTime = nowYear - 1 + "/10";
            endTime = nowYear - 1 + "/12";
        } else if (nowMonth >= 4 && nowMonth <= 6) {
            startTime = nowYear - 1 + "/01";
            endTime = nowYear - 1 + "/03";
        } else if (nowMonth >= 7 && nowMonth <= 9) {
            startTime = nowYear - 1 + "/04";
            endTime = nowYear - 1 + "/06";
        } else {
            startTime = nowYear - 1 + "/07";
            endTime = nowYear - 1 + "/09";
        }
        startDetailActivity(startTime, endTime, startTime + "-" + endTime);
    }

    private void setLastYear() { //选择上年
        String nowDate = Utils.getNowDateStr();      // 2020/03/28
        int lastYear = Integer.parseInt(nowDate.substring(0, 4)) - 1;
        startDetailActivity(lastYear + "/01", lastYear + "/12", lastYear + "");// 2019/01 2019/12  2019
    }

    private void setCurrentYear() { //选择本年
        String nowDate = Utils.getNowDateStr();      // 2020/03/28
        String startDate = nowDate.substring(0, 5) + "01/01";  // 2020/03/28
        startDetailActivity(startDate, nowDate, startDate.substring(0, 4));
    }

    private void setLastMonth() { //选择上月
        String nowDate = Utils.getNowDateStr();      // 2020/03/28
        int nowYear = Integer.parseInt(nowDate.substring(0, 4));
        int nowMonth = Integer.parseInt(nowDate.substring(5, 7));
        String lastMonth;
        if (nowMonth == 1) {
            lastMonth = (nowYear - 1) + "/12";
        } else {
            lastMonth = nowYear + "/" + Utils.addZero(nowMonth - 1);
        }
        startDetailActivity(lastMonth, lastMonth, lastMonth);
    }

    private void setCurrentMonth() { //选择本月
        String nowDate = Utils.getNowDateStr();    // 2020/03/28
        String monthStartDate = nowDate.substring(0, 7);  // 2020/03
        startDetailActivity(monthStartDate, monthStartDate, monthStartDate);
    }

    private void setCustomDate() {
        String tvStartTime = mTv_start_time.getText().toString(); // 2020/03/28
        String tvEndTime = mTv_end_time.getText().toString(); // 2020/03/28
        String startTime = tvStartTime.replaceAll("/", ""); // 20200328
        String endTime = tvEndTime.replaceAll("/", ""); // 20200328
        if (Integer.parseInt(startTime) == Integer.parseInt(endTime)) {
            startDetailActivity(tvStartTime, tvStartTime, tvStartTime);
            finish();
        } else if (Integer.parseInt(startTime) < Integer.parseInt(endTime)) {
            startDetailActivity(tvStartTime, tvEndTime, "");
            finish();
        } else {
            Toast.makeText(this, "请设置合理的时间段", Toast.LENGTH_SHORT).show();
        }
    }

    private void startDetailActivity(String startTime, String endTime, String showTime) {
        Log.d(TAG, "startDetailActivity(),startTime=" + startTime + ",endTime=" + endTime);
        Intent intent = new Intent();
        intent.putExtra("startTime", startTime);
        intent.putExtra("endTime", endTime);
        intent.putExtra("showTime", showTime);
        setResult(1, intent);
        finish();
    }

    private void setBackground(boolean isSetStart) {
        if (isSetStart) {
            mLl_start_time.setBackgroundColor(getColor(R.color.greythree));
            mLl_end_time.setBackgroundColor(getColor(R.color.white));
        } else {
            mLl_start_time.setBackgroundColor(getColor(R.color.white));
            mLl_end_time.setBackgroundColor(getColor(R.color.greythree));
        }
        mSetStart = isSetStart;
    }

}
