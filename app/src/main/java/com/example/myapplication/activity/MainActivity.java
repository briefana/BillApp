package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.fragment.FirstFragmentNew;
import com.example.myapplication.fragment.FourFragment;
import com.example.myapplication.fragment.SecondFragment;
import com.example.myapplication.fragment.ThirdFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mOneLin, mTwoLin, mThreeLin, mFourLin;
    private ImageView mOneImg, mTwoImg, mThreeImg, mFourImg;
    private TextView mOneTv, mTwoTv, mThreeTv, mFourTv;

    //    private FirstFragment mOneFragment;
    private FirstFragmentNew mOneFragment;
    private SecondFragment mTwoFragment;
    private ThirdFragment mThreeFragment;
    private FourFragment mFourFragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private ImageView mIvAdd;
    private static String TAG = "MainActivity:AJ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);

        mOneLin = findViewById(R.id.one_lin);
        mTwoLin = findViewById(R.id.two_lin);
        mThreeLin = findViewById(R.id.three_lin);
        mFourLin = findViewById(R.id.four_lin);

        mOneImg = findViewById(R.id.one_img);
        mOneTv = findViewById(R.id.one_tv);

        mTwoImg = findViewById(R.id.two_img);
        mTwoTv = findViewById(R.id.two_tv);

        mThreeImg = findViewById(R.id.three_img);
        mThreeTv = findViewById(R.id.three_tv);

        mFourImg = findViewById(R.id.four_img);
        mFourTv = findViewById(R.id.four_tv);

        mOneLin.setOnClickListener(this);
        mTwoLin.setOnClickListener(this);
        mThreeLin.setOnClickListener(this);
        mFourLin.setOnClickListener(this);

        mIvAdd.setOnClickListener(this);

        //获取FragmentManager对象
        manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        transaction = manager.beginTransaction();
        setSwPage(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.one_lin:
                setSwPage(0);
                break;
            case R.id.two_lin:
                setSwPage(1);
                break;
            case R.id.three_lin:
                setSwPage(2);
                break;
            case R.id.four_lin:
                setSwPage(3);
                break;
            case R.id.iv_add:    //点击主页的加号图标
                Log.d(TAG, "onClick");
                Intent intent = new Intent(MainActivity.this, AccounDetailActivity.class);
                intent.putExtra("from", "FirstFragment");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

    }

    public void setSwPage(int i) {
        //获取FragmentManager对象
        manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            case 0:
                reImgSelect();
                mOneImg.setSelected(true);
                mOneTv.setSelected(true);
                if (mOneFragment == null) {
                    mOneFragment = new FirstFragmentNew(MainActivity.this);
                    transaction.add(R.id.frame, mOneFragment);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mOneFragment);
                }
                break;
            case 1:
                reImgSelect();
                mTwoImg.setSelected(true);
                mTwoTv.setSelected(true);
                if (mTwoFragment == null) {
                    mTwoFragment = new SecondFragment(MainActivity.this);
                    transaction.add(R.id.frame, mTwoFragment);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mTwoFragment);
                }
                break;
            case 2:
                reImgSelect();
                mThreeImg.setSelected(true);
                mThreeTv.setSelected(true);
                if (mThreeFragment == null) {
                    mThreeFragment = new ThirdFragment(MainActivity.this);
                    transaction.add(R.id.frame, mThreeFragment);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mThreeFragment);
                }
                break;
            case 3:
                reImgSelect();
                mFourImg.setSelected(true);
                mFourTv.setSelected(true);
                if (mFourFragment == null) {
                    mFourFragment = new FourFragment();
                    transaction.add(R.id.frame, mFourFragment);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mFourFragment);
                }
                break;
        }
        transaction.commit();
    }

    //将四个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (mOneFragment != null) {
            transaction.hide(mOneFragment);
        }
        if (mTwoFragment != null) {
            transaction.hide(mTwoFragment);
        }
        if (mThreeFragment != null) {
            transaction.hide(mThreeFragment);
        }
        if (mFourFragment != null) {
            transaction.hide(mFourFragment);
        }
    }

    //初始化底部菜单选择状态
    private void reImgSelect() {
        mOneImg.setSelected(false);
        mOneTv.setSelected(false);

        mTwoImg.setSelected(false);
        mTwoTv.setSelected(false);

        mThreeImg.setSelected(false);
        mThreeTv.setSelected(false);

        mFourImg.setSelected(false);
        mFourTv.setSelected(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(),resultCode=" + resultCode);
        if (resultCode == 1) {
            String startTime = data.getStringExtra("startTime");  //2019/03/16 或 2019/03
            String endTime = data.getStringExtra("endTime");
            String showTime = data.getStringExtra("showTime");
            mThreeFragment.mStartDate = startTime;
            mThreeFragment.mEndDate = endTime;
            mThreeFragment.updateMonthAvager(showTime);
            Log.d(TAG, "onActivityResult(),startTime=" + startTime + ",endTime=" + endTime);
        }
    }
}
