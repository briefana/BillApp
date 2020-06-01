package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.AccountApplication;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.activity.AccounDetailActivity;
import com.example.myapplication.bean.AccountBean;

import java.util.ArrayList;

public class DetailMonthAdapterNew extends BaseAdapter {
    private ArrayList<AccountBean> mData;
    private Context mContext;
    private String TAG = "DetailMonthAdapter:AJ";

    public DetailMonthAdapterNew(Context context, ArrayList<AccountBean> data) {
        mData = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.time_account_item, null);
        TextView tv_income = (TextView) convertView.findViewById(R.id.tv_income);
        TextView tv_pay = (TextView) convertView.findViewById(R.id.tv_pay);
        TextView tv_pay_note = (TextView) convertView.findViewById(R.id.tv_pay_note);
        TextView tv_incom_note = (TextView) convertView.findViewById(R.id.tv_incom_note);
        ImageView iv_type = (ImageView) convertView.findViewById(R.id.iv_type);
        View v_top_gap = convertView.findViewById(R.id.v_top_gap);
        View v_bottom_gap = convertView.findViewById(R.id.v_bottom_gap);

        final AccountBean account = mData.get(position);
        int typeId = account.getTypeId();
        String typeStr;
        String day = account.getTime().substring(8, 10);
        if (typeId == -1) {
            typeStr = day;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v_top_gap.getLayoutParams();
            params.height = Utils.dip2px(mContext, 2);
            v_top_gap.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) v_bottom_gap.getLayoutParams();
            params2.height = Utils.dip2px(mContext, 2);
            v_bottom_gap.setLayoutParams(params2);

        } else {
            typeStr = AccountApplication.mTypeMap.get(typeId)[0];
            iv_type.setImageDrawable(mContext.getResources().getDrawable(Utils.getDrawableId(typeStr)));
        }

        String text = typeStr + " " + Utils.keepTwoDecimalStr(account.getMoney()) + " ";
        String tab = account.getTab();
        if (tab.equals("0")) { //支出
            tv_pay.setText(text);
            tv_pay_note.setText(account.getNote());
            tv_income.setVisibility(View.INVISIBLE);
            tv_incom_note.setVisibility(View.INVISIBLE);
        } else if (tab.equals("1")) { //收入
            tv_income.setText(text);
            tv_incom_note.setText(account.getNote());
            tv_pay.setVisibility(View.INVISIBLE);
            tv_pay_note.setVisibility(View.INVISIBLE);
        } else if (tab.equals("2")) { //转账
        }

        if (typeId == -1) {
            tv_income.setVisibility(View.GONE);
            tv_pay.setVisibility(View.GONE);
            tv_incom_note.setText(day + "日");
            tv_pay_note.setText("" + Utils.keepTwoDecimalStr(account.getMoney()));
        } else {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick");
                    Intent intent = new Intent(mContext, AccounDetailActivity.class);  //点击条目，传对象Account
                    intent.putExtra("account", account);
                    intent.putExtra("from", "FirstFragment");
                    mContext.startActivity(intent);
                }
            });
        }

        Log.d(TAG, "getView(),position=" + position + ",text=" + text);
        return convertView;
    }
}
