package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.bean.AccountBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class TypeScaleActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "TypeScaleActivity:AJ";
    private ListView mLvMonthAverage;
    private TypeScaleActivity mContext;
    private MonthAverageAdapter mAdapter;
    private TextView mTvSelectTime;
    private TextView mTvTotalPay;
    private TextView mTvAverageMonth;
    private LinearLayout mLlMonthPay;
    public int mMonthCount;
    public String mStartDate;   //2019/03/16 或 2019/03
    public String mEndDate;   //2019/03/16 或 2019/03
    private double mTotalMoney;

    private int[] mColors = {
            R.color.red,
            R.color.orange,
            R.color.yellow,
            R.color.green,
            R.color.cyan,
            R.color.blue,
            R.color.violet};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        getSupportActionBar().hide();
        setContentView(R.layout.activity_type_scale);
        mContext = this;

        findViewById(R.id.iv_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText("支出比例");
        mLvMonthAverage = findViewById(R.id.lv_month_average);
        mTvSelectTime = findViewById(R.id.tv_select_time);
        mTvTotalPay = findViewById(R.id.tv_total_pay);
        mTvAverageMonth = findViewById(R.id.tv_average_month);
        mLlMonthPay = findViewById(R.id.ll_month_pay);

        Intent intent = getIntent();
        mMonthCount = intent.getIntExtra("monthCount", 1);
        mStartDate = intent.getStringExtra("startDate");
        mEndDate = intent.getStringExtra("endDate");
        if (mMonthCount == 1) {
            mTvSelectTime.setText(mStartDate);
            mLlMonthPay.setVisibility(View.GONE);
        } else {
            if (mStartDate.substring(0, 7).equals(mEndDate.substring(0, 7))) { //同年同月，不同日
                mTvSelectTime.setText(mStartDate + "-" + mEndDate.substring(8) + ",共" + mMonthCount + "个月");
            } else if (mStartDate.substring(0, 3).equals(mEndDate.substring(0, 3))) { //同年，不同月不同日
                mTvSelectTime.setText(mStartDate + "-" + mEndDate.substring(5) + ",共" + mMonthCount + "个月");
            } else {
                mTvSelectTime.setText(mStartDate + "-" + mEndDate + ",共" + mMonthCount + "个月");
            }
            mLlMonthPay.setVisibility(View.VISIBLE);
        }
        String data = intent.getStringExtra("data");
        try {
            JSONArray jsonArray = new JSONArray(data);
            ArrayList<AccountBean> mList = new ArrayList();
            ArrayList<Float> moneyList = new ArrayList();
            mTotalMoney = 0.0;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int type_id = jsonObject.getInt("type_id");
                String type_name = jsonObject.getString("type_name");
                float sumMoney = Float.parseFloat(jsonObject.getString("sumMoney"));
                AccountBean account = new AccountBean(type_id, type_name, sumMoney);
                mList.add(account);

                moneyList.add(sumMoney);
                mTotalMoney += sumMoney;
            }

            ArrayList<Double> angleList = new ArrayList();
            for (int i = 0; i < moneyList.size(); i++) {
                double percentage = moneyList.get(i) / mTotalMoney * 100;
                double perentageTwo = Utils.keepTwoDecimal(percentage);
                Log.d(TAG, "onCreate(),i=" + i + ",perentageTwo=" + perentageTwo);
                angleList.add(perentageTwo);
            }

            mAdapter = new MonthAverageAdapter();
            mAdapter.setList(mList);
            mAdapter.setAngleList(angleList);
            mLvMonthAverage.setAdapter(mAdapter);

            mTvTotalPay.setText("" + Utils.keepTwoDecimal(mTotalMoney));
            mTvAverageMonth.setText("" + Utils.keepTwoDecimal(mTotalMoney / mMonthCount));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.d(TAG, "onClick(),v=" + v);
        if (id == R.id.iv_back) {
            finish();
        }
    }

    private class MonthAverageAdapter extends BaseAdapter {
        private ArrayList<AccountBean> mList;
        private ArrayList<Double> mAngleList;

        public MonthAverageAdapter() {
        }

        public void setList(ArrayList<AccountBean> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.type_scale_item, null);
            View vColorBar = convertView.findViewById(R.id.v_color_bar);
            final TextView tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            final TextView average_money = (TextView) convertView.findViewById(R.id.average_money);
            final TextView scale = (TextView) convertView.findViewById(R.id.scale);
            final ImageView ivType = convertView.findViewById(R.id.iv_type);

            final AccountBean account = mList.get(position);
            final String typeName = account.getTypeName();
            tv_type.setText(typeName + " " + account.getMoney() + "");
            ivType.setImageDrawable(getResources().getDrawable(Utils.getDrawableId(typeName)));
            scale.setText(mAngleList.get(position) + "%");
            final String monthAvager = Utils.keepTwoDecimal(account.getMoney() / mMonthCount) + "";
            if (mMonthCount == 1) {
                average_money.setVisibility(View.GONE);
            } else {
                average_money.setVisibility(View.VISIBLE);
                average_money.setText("月均 " + monthAvager);
            }
            if (position < 7) {
                vColorBar.setBackgroundColor(mContext.getColor(mColors[position]));
            } else {
                vColorBar.setBackgroundColor(mContext.getColor(R.color.violet));
            }

            /**/
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vColorBar.getLayoutParams();
            params.width = (int) (6 * mAngleList.get(position));
            Log.d(TAG, "getView(),position=" + position + ",params.height=" + params.height + ",params.width=" + params.width + ",=" + vColorBar.getMeasuredWidth());
            vColorBar.setLayoutParams(params);
            /**/
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int typeId = account.getTypeId();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String startDate = mStartDate.replaceAll("/", "");
                                String endDate = mEndDate.replaceAll("/", "");
                                final String result = Utils.getHtml(Utils.showTypeDetailsByDatePath("my", startDate, endDate, typeId));
                                Intent intent = new Intent(mContext, TypeDetailActivity.class);
                                intent.putExtra("result", result);
                                intent.putExtra("typeName", typeName);
                                intent.putExtra("totalMoney", account.getMoney() + "");
                                intent.putExtra("averageMoney", monthAvager);
                                intent.putExtra("startDate", mStartDate);
                                intent.putExtra("endDate", mEndDate);
                                Log.i(TAG, "onClick run(),next startActivity TypeDetailActivity,result=" + result);
                                startActivity(intent);
                            } catch (Exception e) {
                                Log.i(TAG, "catch" + e);
                                e.printStackTrace();
                            }
                        }

                    });
                    thread.start();

                }
            });
            return convertView;
        }

        public void setAngleList(ArrayList<Double> angleList) {
            mAngleList = angleList;
        }
    }


}
