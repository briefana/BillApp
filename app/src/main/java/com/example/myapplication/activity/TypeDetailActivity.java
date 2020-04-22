package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.AccountApplication;
import com.example.myapplication.adapter.DetailMonthAdapter;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.bean.AccountBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class TypeDetailActivity extends AppCompatActivity {

    private String TAG = "TypeDetailActivity:AJ";
    private ListView mLvDetail;
    private ArrayList<AccountBean> mAllUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_type_detail);

        mLvDetail = (ListView) findViewById(R.id.lv_detail);
        ((ImageView) findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (AccountApplication.mTypeMap.size() == 0) {
            AccountApplication.getApplication().initTypeMap();
        }

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String typeName = intent.getStringExtra("typeName");
        ((TextView) findViewById(R.id.tv_title)).setText(typeName);
        String totalMoney = intent.getStringExtra("totalMoney");
        String averageMoney = intent.getStringExtra("averageMoney");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        if (endDate.substring(0, 5).equals(startDate.substring(0, 5))) {
            endDate = endDate.substring(5);
        }
        ((TextView) findViewById(R.id.tv_note)).setText(startDate + " - " + endDate + "\n" + "共支出 " + totalMoney + ", 月均 " + averageMoney);
        //onCreate(),typeName=房租房贷,totalMoney=8741.47,averageMoney=4370.73,startDate=20200218,endDate=20200319
        Log.i(TAG, "onCreate(),typeName=" + typeName + ",totalMoney=" + totalMoney + ",averageMoney=" + averageMoney
                + ",startDate=" + startDate + ",endDate=" + endDate);
//        updateAdapter(result);
        updateDoubleAdapter(result);
    }

    private void updateDoubleAdapter(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            ArrayList<ArrayList<AccountBean>> doubleList = new ArrayList();
            ArrayList<String> yearMonthList = new ArrayList();  //2020-01
            ArrayList<Float> moneyList = new ArrayList();
            Log.i(TAG, "updateDoubleAdapter run(),jsonArray.length()=" + jsonArray.length());
            String lastYearMonth = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                final String time = jsonObject.getString("time"); //13位数，2020030541634，2020-03-05 周四 16:34

                String year = time.substring(0, 4);
                String month = time.substring(4, 6);
                String day = time.substring(6, 8);
                String week = Utils.getWeekStr(Integer.parseInt(time.substring(8, 9)));
                String hour = time.substring(9, 11);
                String minute = time.substring(11);
                String timeStr = year + "-" + month + "-" + day + " " + week + " " + hour + ":" + minute; //2020-03-05 周四 16:34

                final int type_id = jsonObject.getInt("type_id");
                final String money = jsonObject.getString("money");
                final String user_id = jsonObject.getString("user_id");
                final String note = jsonObject.getString("note");
                final String bill_id = jsonObject.getString("bill_id");
                float moneyFloat = Float.parseFloat(money);

                AccountBean account = new AccountBean(timeStr, type_id, moneyFloat, user_id, note, bill_id, "");  //暂时缺少tab字段

                String yearMonth = timeStr.substring(0, 7);
                if (yearMonth.equals(lastYearMonth)) {
                    int size = doubleList.size();
                    ArrayList<AccountBean> list = doubleList.get(size - 1);
                    list.add(account);
                    float sumMoney = moneyList.get(size - 1) + moneyFloat;
                    moneyList.set(size - 1, sumMoney);
                    Log.i(TAG, "updateDoubleAdapter run(),if,i=" + i + ",yearMonth=" + yearMonth + ",moneyFloat=" + moneyFloat + ",sumMoney=" + sumMoney);
                } else {
                    ArrayList<AccountBean> list = new ArrayList();
                    list.add(account);
                    doubleList.add(list);
                    yearMonthList.add(yearMonth);
                    moneyList.add(moneyFloat);
                    Log.i(TAG, "updateDoubleAdapter run(),else,i=" + i + ",yearMonth=" + yearMonth + ",moneyFloat=" + moneyFloat);
                }
                lastYearMonth = yearMonth;
            }

            for (int i = 0; i < yearMonthList.size(); i++) {
                String yearMonth = yearMonthList.get(i);
                Float money = moneyList.get(i);
                Log.i(TAG, "updateDoubleAdapter run(),i=" + i + ",yearMonth=" + yearMonth + ",money=" + money);
            }

            MonthAccountAdapter adapter = new MonthAccountAdapter(doubleList, yearMonthList, moneyList);
            mLvDetail.setAdapter(adapter);
//            setListViewHeightBasedOnChildren(mLvDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MonthAccountAdapter extends BaseAdapter {
        ArrayList<ArrayList<AccountBean>> mDoubleList;
        ArrayList<String> mYearMonthList;
        ArrayList<Float> mMoneyList;

        public MonthAccountAdapter(ArrayList<ArrayList<AccountBean>> doubleList, ArrayList<String> yearMonthList, ArrayList<Float> moneyList) {
            mDoubleList = doubleList;
            mYearMonthList = yearMonthList;
            mMoneyList = moneyList;
        }

        @Override
        public int getCount() {
            return mYearMonthList.size();
        }

        @Override
        public Object getItem(int position) {
            return mYearMonthList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(TypeDetailActivity.this).inflate(R.layout.type_month_item, null);
            TextView tvMonth = (TextView) convertView.findViewById(R.id.tv_month);
            final ImageView arrow = (ImageView) convertView.findViewById(R.id.iv);
            TextView tvExpend = (TextView) convertView.findViewById(R.id.tv_expend);
            final ListView lv_inside = convertView.findViewById(R.id.lv_inside);
            String yearMonth = mYearMonthList.get(position); //2020-01
            float money = mMoneyList.get(position);
            tvMonth.setText(yearMonth.replace("-", "年") + "月");
            tvExpend.setText(Utils.keepTwoDecimal(money) + "");
            DetailMonthAdapter adapter = new DetailMonthAdapter(TypeDetailActivity.this, mDoubleList.get(position));
            lv_inside.setAdapter(adapter);
            setListViewHeightBasedOnChildren(lv_inside);
            lv_inside.setVisibility(View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick");
                    int visibility = lv_inside.getVisibility();
                    if (visibility == View.VISIBLE) {
                        arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
                        lv_inside.setVisibility(View.GONE);
                    } else {
                        arrow.setImageDrawable(getResources().getDrawable(R.drawable.jz_spinner_down));
                        lv_inside.setVisibility(View.VISIBLE);
                    }

                }
            });
            return convertView;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
