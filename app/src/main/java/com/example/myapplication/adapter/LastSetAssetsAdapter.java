package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.bean.AccountAssetsBean;

import java.util.ArrayList;

public class LastSetAssetsAdapter extends BaseAdapter {
    private ArrayList<AccountAssetsBean> mDataList;
    private Context mContext;
    private String mLastSetBalanceTime;
    private String TAG = "LastSetAssetsAdapter:AJ";

    public LastSetAssetsAdapter(ArrayList<AccountAssetsBean> list, Context context) {
        mDataList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDataList.size() + 1;
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.last_set_assets_item, null);
        AccountAssetsBean bean = null;
        if (position > 0) {
            bean = mDataList.get(position - 1);
        }
        TextView tv_account = (TextView) convertView.findViewById(R.id.tv_account);
        EditText tv_current_balance = (EditText) convertView.findViewById(R.id.tv_current_balance);
        TextView tv_last_set_balance = (TextView) convertView.findViewById(R.id.tv_last_set_balance);
        if (position == 0) {  //最后一个条目展示总数
            tv_account.setText("账户");
            tv_current_balance.setText("当前余额");
            tv_current_balance.setEnabled(false);
            tv_current_balance.setCompoundDrawables(null, null, null, null);
            tv_last_set_balance.setText(mLastSetBalanceTime + "设置的余额");
        } else if (position == mDataList.size()) {  //最后一个条目展示总数
            tv_account.setText("总余额");
            tv_current_balance.setText(bean.getCurrentBalance());
            tv_current_balance.setEnabled(false);
            tv_current_balance.setCompoundDrawables(null, null, null, null);
            tv_last_set_balance.setText(bean.getLastSetBalance());
        } else {
            tv_account.setText(bean.getUserName());
            tv_current_balance.setText(bean.getCurrentBalance());
            tv_current_balance.setEnabled(true);
            tv_last_set_balance.setText(bean.getLastSetBalance());
        }
        return convertView;
    }

    public void setLastBalanceTiem(String lastSetBalanceTime) {
        mLastSetBalanceTime = lastSetBalanceTime;  //2020040160545
    }

}
