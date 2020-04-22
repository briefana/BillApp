package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.AccountApplication;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.activity.AccounDetailActivity;
import com.example.myapplication.bean.AccountBean;

import java.util.ArrayList;

public class DetailMonthAdapter extends BaseAdapter {
    private ArrayList<AccountBean> mData;
    private Context mContext;
    private String TAG = "DetailMonthAdapter:AJ";

    public DetailMonthAdapter(Context context, ArrayList<AccountBean> data) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.account_item, null);
        }
        TextView tv_type = (TextView) convertView.findViewById(R.id.tv_type);
        TextView tv_time_note = (TextView) convertView.findViewById(R.id.tv_time_note);
        TextView tv_money = (TextView) convertView.findViewById(R.id.tv_money);
        TextView tv_person = (TextView) convertView.findViewById(R.id.tv_person);
        TextView tv_position = (TextView) convertView.findViewById(R.id.tv_position);
        ImageView iv_type = (ImageView) convertView.findViewById(R.id.iv_type);

        final AccountBean account = mData.get(position);
        String typeStr = AccountApplication.mTypeMap.get(account.getTypeId())[0];

        tv_type.setText(typeStr);
        iv_type.setImageDrawable(mContext.getResources().getDrawable(Utils.getDrawableId(typeStr)));
        tv_time_note.setText(account.getTime() + " " + account.getNote());  //2020-03-05 周四 16:34 备注

        tv_money.setText(account.getMoney() + "");
        String tab = account.getTab();
        if (tab.equals("0")) { //支出
            tv_money.setTextColor(mContext.getColor(R.color.black));
        } else if (tab.equals("1")) { //收入
            tv_money.setTextColor(mContext.getColor(R.color.payColor));
        } else if (tab.equals("2")) { //转账
            tv_money.setTextColor(mContext.getColor(R.color.greytwo));
        }

        tv_person.setText(account.getPerson());
        tv_position.setText(position + 1 + "");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                Intent intent = new Intent(mContext, AccounDetailActivity.class);  //点击条目，传对象Account
                intent.putExtra("account", account);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
}
