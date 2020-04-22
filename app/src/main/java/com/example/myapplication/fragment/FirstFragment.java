package com.example.myapplication.fragment;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.adapter.DetailMonthAdapter;
import com.example.myapplication.bean.AccountBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {

    private MainActivity mActivity;
    private ListView mLvDetail;
    private TextView mTvPay, mTvIncome, mTvBalance;
    private String TAG = "FirstFragment:AJ";
    private ArrayList<AccountBean> mAllUserData;

    public FirstFragment(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_first, container, false);
        mLvDetail = inflate.findViewById(R.id.lv_detail);
        mTvPay = inflate.findViewById(R.id.pay);
        mTvIncome = inflate.findViewById(R.id.income);
        mTvBalance = inflate.findViewById(R.id.balance);
        String nowDate = Utils.getNowDateStr();      // 2020/04/13   当前日期
        queryMonthDataFromNet(nowDate.substring(0, 7).replaceAll("/", ""));//2020/04

        /**/
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

        /**/

        return inflate;
    }

    private void queryMonthDataFromNet(final String month) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = Utils.getBillPathByMonth("my", month);
                    final String result = Utils.getHtml(path);
                    Log.d(TAG, "queryData(),month=" + month + ",result=" + result);
                    Log.i(TAG, result);
                    mLvDetail.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                mAllUserData = new ArrayList<>();

                                float pay = 0;
                                float income = 0;

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

                                    int type_id = jsonObject.getInt("type_id");
                                    String money = jsonObject.getString("money");
                                    String user_id = jsonObject.getString("user_id");
                                    String note = jsonObject.getString("note");
                                    String bill_id = jsonObject.getString("bill_id");
                                    String tab = jsonObject.getString("tab");
                                    float moneyFloat = Float.parseFloat(money);

                                    if (tab.equals("0")) { //支出
                                        pay += moneyFloat;
                                    } else if (tab.equals("1")) {  //收入
                                        income += moneyFloat;
                                    }

                                    AccountBean account = new AccountBean(timeStr, type_id, moneyFloat, user_id, note, bill_id, tab);
                                    mAllUserData.add(account);
                                }
                                DetailMonthAdapter adapter = new DetailMonthAdapter(mActivity, mAllUserData);
                                mLvDetail.setAdapter(adapter);
//                                mTvPay.setText(Utils.keepTwoDecimal(pay) + "");

                                String payStr = Utils.keepTwoDecimal(pay) + "";
                                Spannable sp = new SpannableString(payStr);
                                sp.setSpan(new AbsoluteSizeSpan(15, true), payStr.length() - 2, payStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                mTvPay.setText(sp);

                                mTvIncome.setText(Utils.keepTwoDecimal(income) + "");
                                mTvBalance.setText(Utils.keepTwoDecimal(income - pay) + "");
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

}
