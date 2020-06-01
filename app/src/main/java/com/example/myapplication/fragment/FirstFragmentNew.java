package com.example.myapplication.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.adapter.DetailMonthAdapterNew;
import com.example.myapplication.bean.AccountBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragmentNew extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private ListView mLvDetail;
    private TextView mTvPay, mTvIncome, mTvYearMonth;
    private String TAG = "FirstFragmentNew:AJ";
    private ArrayList<AccountBean> mAllUserData;

    public FirstFragmentNew(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_first_new, container, false);
        mLvDetail = inflate.findViewById(R.id.lv_detail);
        mTvPay = inflate.findViewById(R.id.pay);
        mTvYearMonth = inflate.findViewById(R.id.tv_year_month);
        mTvYearMonth.setOnClickListener(this);
        mTvIncome = inflate.findViewById(R.id.income);

        double d = 756.999;

        //方法一：最简便的方法，调用DecimalFormat类
        DecimalFormat df = new DecimalFormat(".00");
        System.out.println("1ttt=" + df.format(d));

        //方法二：直接通过String类的format函数实现
        System.out.println("2ttt=" + String.format("%.2f", d));

        //方法三：通过BigDecimal类实现
        BigDecimal bg = new BigDecimal(d);
        double d3 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println("3ttt=" + d3);

        //方法四：通过NumberFormat类实现
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        System.out.println("4ttt=" + nf.format(d));
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = mActivity.getIntent();
        String monthYear = intent.getStringExtra("monthYear");
        Log.d(TAG, "onResume(),monthYear=" + monthYear + ",intent=" + intent);
        if (TextUtils.isEmpty(monthYear)) {
            String nowDate = Utils.getNowDateStr();      // 2020/04/13   当前日期
            queryMonthDataFromNet(nowDate.substring(0, 7).replaceAll("/", ""));//2020/04
        } else {
            queryMonthDataFromNet(monthYear);//2020/04
        }
    }

    private void queryMonthDataFromNet(final String yearMonth) {  //202004
        mTvYearMonth.setText(yearMonth.substring(0, 4) + "年" + yearMonth.substring(4) + "月");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = Utils.getBillPathByMonth("my", yearMonth);
                    String result = Utils.doGet(path);
                    JSONArray jsonArray = new JSONArray(result);
                    Log.d(TAG, "queryMonthDataFromNet(),yearMonth=" + yearMonth + ",result=" + result);
                    String lastYearMonth;
                    if (jsonArray.length() == 0) {
                        String year = yearMonth.substring(0, 4);
                        String month = yearMonth.substring(4);
                        if (month.equals("01")) {
                            lastYearMonth = Integer.parseInt(year) - 1 + "12";
                        } else {
                            lastYearMonth = year + Utils.addZero(Integer.parseInt(month) - 1);
                        }
                        path = Utils.getBillPathByMonth("my", lastYearMonth);
                        result = Utils.doGet(path);
                        jsonArray = new JSONArray(result);
                        Log.d(TAG, "queryMonthDataFromNet(),lastYearMonth=" + lastYearMonth + ",result=" + result);
                        final String finalLastYearMonth = lastYearMonth;
                        mTvYearMonth.post(new Runnable() {
                            @Override
                            public void run() {
                                mTvYearMonth.setText(finalLastYearMonth.substring(0, 4) + "年" + finalLastYearMonth.substring(4) + "月");
                            }
                        });
                    }
                    final JSONArray finalJsonArray = jsonArray;
                    mLvDetail.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mAllUserData = new ArrayList<>();
                                float pay = 0;
                                String oneDay = ""; //某一天
                                float totalPayOfOneDay = 0; //某一天的总支出
                                int lastIndex = -1; //某一天的总支出
                                float income = 0;

                                for (int i = 0; i < finalJsonArray.length(); i++) {
                                    JSONObject jsonObject = finalJsonArray.getJSONObject(i);
                                    final String time = jsonObject.getString("time"); //13位数，2020030541634，2020-03-05 周四 16:34

                                    String year = time.substring(0, 4);
                                    String month = time.substring(4, 6);
                                    String day = time.substring(6, 8);
                                    String week = Utils.getWeekStr(Integer.parseInt(time.substring(8, 9)));
                                    String hour = time.substring(9, 11);
                                    String minute = time.substring(11);
                                    String timeStr = year + "-" + month + "-" + day + " " + week + " " + hour + ":" + minute; //2020-03-05 周四 16:34

                                    int type_id = jsonObject.getInt("type_id");
                                    String money = jsonObject.getString("money");
                                    String user_id = jsonObject.getString("user_id");
                                    String note = jsonObject.getString("note");
                                    String bill_id = jsonObject.getString("bill_id");
                                    String tab = jsonObject.getString("tab");
                                    String get_user_id = jsonObject.getString("get_user_id");
                                    float moneyFloat = Float.parseFloat(money);

                                    if (tab.equals("0")) { //支出
                                        pay += moneyFloat;

                                        if (day.equals(oneDay)) {
                                            totalPayOfOneDay += moneyFloat;
                                            mAllUserData.get(lastIndex).setMoney(totalPayOfOneDay);
                                            Log.d(TAG, "queryData(),i=" + i + ",day=" + day + ",oneDay=" + oneDay + ",if totalPayOfOneDay=" + totalPayOfOneDay);
                                        } else {
                                            totalPayOfOneDay = moneyFloat;
                                            AccountBean account = new AccountBean(timeStr, -1, moneyFloat, user_id, note, bill_id, "", get_user_id);
                                            mAllUserData.add(account);
                                            lastIndex = mAllUserData.size() - 1;
                                            Log.d(TAG, "queryData(),i=" + i + ",day=" + day + ",oneDay=" + oneDay + ",else totalPayOfOneDay=" + totalPayOfOneDay);
                                        }
                                        oneDay = day;

                                    } else if (tab.equals("1")) {  //收入
                                        income += moneyFloat;
                                    } else if (tab.equals("2")) { //转账
                                        continue;
                                    }

                                    AccountBean account = new AccountBean(timeStr, type_id, moneyFloat, user_id, note, bill_id, tab, get_user_id);
                                    mAllUserData.add(account);
                                }
                                DetailMonthAdapterNew adapter = new DetailMonthAdapterNew(mActivity, mAllUserData);
                                mLvDetail.setAdapter(adapter);
//                                mTvPay.setText(Utils.keepTwoDecimal(pay) + "");

                                String payStr = Utils.keepTwoDecimal(pay) + "";
                                Spannable sp = new SpannableString(payStr);
                                sp.setSpan(new AbsoluteSizeSpan(15, true), payStr.length() - 2, payStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                mTvPay.setText(sp);

                                mTvIncome.setText(Utils.keepTwoDecimal(income) + "");
                            } catch (Exception e) {
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
        Log.d(TAG, "onClick(),v=" + v);
        if (id == R.id.tv_year_month) {
            showDatePickDlg();
        }
    }

    private DatePicker findDatePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof DatePicker) {
                    return (DatePicker) child;
                } else if (child instanceof ViewGroup) {
                    DatePicker result = findDatePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;
    }

    CustomerDatePickerDialog mDialog;

    protected void showDatePickDlg() {

        final Calendar cal = Calendar.getInstance();
        /*mDialog = new CustomerDatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String yearMonth = year + Utils.addZero(monthOfYear + 1);
                Log.d(TAG, "onClick(),yearMonth=" + yearMonth);
                queryMonthDataFromNet(yearMonth);
            }
        },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        mDialog.show();

        DatePicker dp = findDatePicker((ViewGroup) mDialog.getWindow().getDecorView());
        if (dp != null) {
            View childAt = ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2);
            View childAt1 = (ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0);
            Log.d(TAG, "showDatePickDlg(),childAt=" + childAt);
            Log.d(TAG, "showDatePickDlg(),childAt1=" + childAt1);
            childAt.setVisibility(View.GONE);
        }*/

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String yearMonth = year + Utils.addZero(monthOfYear + 1);
                Log.d(TAG, "onClick(),yearMonth=" + yearMonth);
                queryMonthDataFromNet(yearMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    class CustomerDatePickerDialog extends DatePickerDialog {
        public CustomerDatePickerDialog(Context context,
                                        OnDateSetListener callBack, int year, int monthOfYear,
                                        int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            super.onDateChanged(view, year, month, day);
            mDialog.setTitle(year + "年" + (month + 1) + "月");
        }
    }
}
