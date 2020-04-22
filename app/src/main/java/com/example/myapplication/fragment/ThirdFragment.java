package com.example.myapplication.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.activity.SelectTimeActivity;
import com.example.myapplication.activity.TypeScaleActivity;
import com.example.myapplication.bean.AccountBean;
import com.example.myapplication.view.ChartView;
import com.example.myapplication.view.ClickableLinerLayout;
import com.example.myapplication.view.MyRound;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "ThirdFragment:AJ";
    private MainActivity mActivity;
    private MyRound mRound;
    private TextView mTvSelectTime;
    private ListView mLvMonthAverage;
    private ClickableLinerLayout mLlCircular;
    public String mStartDate;   //2019/03/16 或 2019/03
    public String mEndDate;   //2019/03/16 或 2019/03
    public int mMonthCount;
    private double mTotalMoney;
    private MonthAverageAdapter mAdapter;
    private ArrayList<AccountBean> mList;
    private ChartView mChartView;
    private String mResult;

    public ThirdFragment(MainActivity activity) {
        mActivity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_third, container, false);
        mRound = inflate.findViewById(R.id.round);
        mTvSelectTime = inflate.findViewById(R.id.tv_select_time);
        mLvMonthAverage = inflate.findViewById(R.id.lv_month_average);

        mLvMonthAverage.setEnabled(false);
        mLvMonthAverage.setPressed(false);

        mLlCircular = inflate.findViewById(R.id.ll_circular);
        mLlCircular.setOnClickListener(this);
        mTvSelectTime.setOnClickListener(this);
        (inflate.findViewById(R.id.iv_arrow)).setOnClickListener(this);
        setDefaultDate();  //取近半年的日期
        mChartView = inflate.findViewById(R.id.myChartView);
        return inflate;
    }

    private void setDefaultDate() {
        String nowDate = Utils.getNowDateStr();      // 2020/03/28   当前日期
        mEndDate = nowDate.substring(0, 7);  //2020/03
        int endYear = Integer.parseInt(nowDate.substring(0, 4));
        int endMonth = Integer.parseInt(nowDate.substring(5, 7));
        int startYear;
        int startMonth;
        if (endMonth > 5) {
            startMonth = endMonth - 5;
            startYear = endYear;
        } else {
            startMonth = endMonth + 12 - 5;
            startYear = endYear - 1;
        }
        mStartDate = startYear + "/" + Utils.addZero(startMonth);  // 2020/02  半年前
        Log.d(TAG, "setDefaultDate(),startDate=" + mStartDate + ",endDate=" + mEndDate); //startDate=2020/02,endDate=2020/03
        updateMonthAvager(mStartDate + "-" + mEndDate);
    }

    public void updateMonthAvager(String showTime) {
        if (TextUtils.isEmpty(showTime)) {
            mTvSelectTime.setText(mStartDate + "-" + mEndDate);
        } else {
            mTvSelectTime.setText(showTime);
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String startDate = mStartDate.replaceAll("/", "");
                    String endDate = mEndDate.replaceAll("/", "");
                    mResult = Utils.getHtml(Utils.showTypeDataByDatePath("my", startDate, endDate));
                    Log.i(TAG, "updateMonthAvager run(),startDate=" + startDate + ",endDate=" + endDate + ",result=" + mResult);

                    final String monthResult = Utils.getHtml(Utils.getAppointMonthsPayPath("my", startDate.substring(0, 6), endDate.substring(0, 6)));  //折线图，获取指定月到指定月的每月支出
                    final JSONArray monthJsonArray = new JSONArray(monthResult);
                    mMonthCount = monthJsonArray.length();
                    Log.i(TAG, "updateMonthAvager run(),mMonthCount=" + mMonthCount + ",monthResult=" + monthResult);

                    if (mMonthCount == 1) {  //说明是一个月
                        mMonthCount = 1;
                        mChartView.post(new Runnable() {
                            @Override
                            public void run() {
                                mChartView.setVisibility(View.GONE);
                            }
                        });
                    }

                    mRound.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(mResult);
                                mList = new ArrayList();
                                ArrayList<Float> moneyList = new ArrayList();
                                mTotalMoney = 0.0;
                                double sevenMoney = 0.0;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int type_id = jsonObject.getInt("type_id");
                                    String type_name = jsonObject.getString("type_name");
                                    float sumMoney = Float.parseFloat(jsonObject.getString("sumMoney"));
                                    if (i < 5) {  //只展示5种类型的列表
                                        AccountBean account = new AccountBean(type_id, type_name, sumMoney);
                                        mList.add(account);
                                    }

                                    if (i < 6) {
                                        moneyList.add(sumMoney);
                                    } else {
                                        sevenMoney += sumMoney;
                                    }
                                    mTotalMoney += sumMoney;
                                }
                                mAdapter = new MonthAverageAdapter();
                                mAdapter.setList(mList);
                                mLvMonthAverage.setAdapter(mAdapter);

                                //画百分比的饼状图
                                ArrayList<Integer> angleList = new ArrayList();
                                for (int i = 0; i < moneyList.size(); i++) {
                                    angleList.add((int) (moneyList.get(i) / mTotalMoney * 360));
                                }
                                if (sevenMoney != 0.0) {
                                    angleList.add((int) (sevenMoney / mTotalMoney * 360));
                                }

                                mRound.setAngleListAndMonthCount(angleList, "" + Utils.keepTwoDecimal(mTotalMoney));

                                if (mMonthCount != 1) {  //说明是多个月
                                    mChartView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mChartView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    //更新折线图
                                    ArrayList pointList = new ArrayList<Point>();
                                    for (int i = monthJsonArray.length() - 1; i >= 0; i--) {
                                        JSONObject jsonObject = monthJsonArray.getJSONObject(i);
                                        final String t = jsonObject.getString("t");
                                        final String sumMoney = jsonObject.getString("sumMoney");
                                        Point p = new Point(Integer.parseInt(t), (int) Float.parseFloat(sumMoney));
                                        pointList.add(p);
                                    }

                                    mChartView.setPoint(pointList);    //给ChartView设置坐标
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_select_time || id == R.id.iv_arrow) {
            startActivityForResult(new Intent(mActivity, SelectTimeActivity.class), 1);
        } else if (id == R.id.ll_circular) {
            Intent intent = new Intent(mActivity, TypeScaleActivity.class);
            intent.putExtra("data", mResult);
            intent.putExtra("monthCount", mMonthCount);
            intent.putExtra("startDate", mStartDate);
            intent.putExtra("endDate", mEndDate);
            startActivity(intent);
        }
    }

    private int[] mColors = {
            R.color.red,
            R.color.orange,
            R.color.yellow,
            R.color.green,
            R.color.cyan,
            R.color.blue,
            R.color.violet};

    private class MonthAverageAdapter extends BaseAdapter {
        private ArrayList<AccountBean> mList;

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
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.month_average_item, null);
            }
            View view = convertView.findViewById(R.id.view);
            final TextView tv_type = convertView.findViewById(R.id.tv_type);
            final TextView total_money = convertView.findViewById(R.id.total_money);

            view.setVisibility(View.VISIBLE);
            final AccountBean account = mList.get(position);
            tv_type.setText(account.getTypeName());
            total_money.setText(account.getMoney() + "");
            if (position < 8) {
                view.setBackgroundColor(mActivity.getColor(mColors[position]));
            } else {
                view.setBackgroundColor(mActivity.getColor(R.color.violet));
            }
            return convertView;
        }
    }

}
