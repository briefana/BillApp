package com.example.myapplication.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.myapplication.activity.MonthDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class SecondFragment extends Fragment implements View.OnClickListener {

    private ListView mLvMonth;
    private MainActivity mActivity;
    private TextView mTvUser;
    private int mTab;

    private static String TAG = "SecondFragment:AJ";

    public SecondFragment(MainActivity activity) {
        // Required empty public constructor
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = inflater.inflate(R.layout.fragment_second, container, false);

        mLvMonth = inflate.findViewById(R.id.lv_month);
        mTvUser = inflate.findViewById(R.id.tv_user);
        mTvUser.setText("-支出");
        mTab = 0;

        mTvUser.setOnClickListener(this);
        loadDataFromNet(mTab);

        return inflate;
    }

    private void loadDataFromNet(final int tab) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String tabPath = "";
                    if (tab != 3) {
                        tabPath = "&tab=" + tab;
                    }
                    final String result = Utils.getHtml(Utils.getMonthsPayPath("my") + tabPath);
                    Log.i(TAG, "loadDataFromNet(),result=" + result);
                    mLvMonth.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                ArrayList<String> list = new ArrayList<>();
                                float totalPay = 0;
                                int monthAccount = jsonArray.length();
                                for (int i = 0; i < monthAccount; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final String t = jsonObject.getString("t");
                                    final String sumMoney = jsonObject.getString("sumMoney");

                                    float sumMoneyFloat = Float.parseFloat(sumMoney);
                                    totalPay += sumMoneyFloat;
                                    list.add(t + sumMoney);  //2020025247.47 ，前6位是某年某月，后面当月总支出
                                }
                                /*mTvAccount.setText(monthAccount + "个月共支出" + totalPay);*/

                                MonthAccountAdapter adapter = new MonthAccountAdapter(list);
                                mLvMonth.setAdapter(adapter);
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
        Log.d(TAG, "onClick(),v=" + v);
        if (id == R.id.tv_user) {
            setMoreDialog();
        }
    }

    private void setMoreDialog() {
        String[] itemArr = new String[]{"支 出", "收 入", "转 账", "全部"};
        final ArrayList<String> itemList = new ArrayList();
        for (int i = 0; i < itemArr.length; i++) {
            itemList.add(itemArr[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setCancelable(true);
        builder.setItems(itemArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String type = itemList.get(which);
                mTvUser.setText("-" + type);  //点击时，选择不同user
                mTab = which;
                loadDataFromNet(mTab);
                Log.i(TAG, "setMoreDialog run(),name=" + type);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class MonthAccountAdapter extends BaseAdapter {
        private ArrayList<String> mContent;

        public MonthAccountAdapter(ArrayList<String> content) {
            mContent = content;
        }

        @Override
        public int getCount() {
            return mContent.size();
        }

        @Override
        public Object getItem(int position) {
            return mContent.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.month_item, null);
            }
            TextView tvMonth = (TextView) convertView.findViewById(R.id.tv_month);
            TextView tvExpend = (TextView) convertView.findViewById(R.id.tv_expend);
            String montent = mContent.get(position);
            final String yearMonth = montent.substring(0, 6); //202002
            tvMonth.setText(yearMonth.substring(0, 4) + "年" + yearMonth.substring(4) + "月");
            final String sumMoney = montent.substring(6);
            tvExpend.setText(sumMoney);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick");
                    Intent intent = new Intent(getActivity(), MonthDetailActivity.class);
                    intent.putExtra("yearmonth", yearMonth); //202002
                    intent.putExtra("tab", mTab); //0指支出、1指收入、2指转账、3指全部
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

}
