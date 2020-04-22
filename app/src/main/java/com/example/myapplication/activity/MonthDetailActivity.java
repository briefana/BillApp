package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.myapplication.AccountApplication;
import com.example.myapplication.DBHelper;
import com.example.myapplication.adapter.DetailMonthAdapter;
import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.example.myapplication.bean.AccountBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MonthDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "MonthAccountActivity:AJ";
    private TextView mTvMonthDetail;
    private ListView mLvDetail;
    private DBHelper mHelper;
    private SQLiteDatabase mDatabase;
    private String mYearMonth;
    private float mSumMoney;
    private float mAnMoney = 0;
    private float mCaiMoney = 0;
    private TextView mTvAdd;
    private TextView mTv_sum;
    private ImageView mIv_open;
    private LinearLayout mLl_open;
    private ArrayList<AccountBean> mAllUserData;
    private ArrayList<AccountBean> mAnData;
    private ArrayList<AccountBean> mCaiData;
    private View mDivider;
    private boolean mNeedUpdate;
    private MonthDetailActivity mContext;
    private int mTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_month_detail);
        mContext = this;

        mTvMonthDetail = (TextView) findViewById(R.id.tv_month_detail);
        mLvDetail = (ListView) findViewById(R.id.lv_detail);
        mTv_sum = (TextView) findViewById(R.id.tv_sum);
        mIv_open = (ImageView) findViewById(R.id.iv_open);
        mTvAdd = (TextView) findViewById(R.id.tv_add);
        mLl_open = findViewById(R.id.ll_title_bar);
        mDivider = findViewById(R.id.divider);

        Intent intent = getIntent();
        mYearMonth = intent.getStringExtra("yearmonth");
        mNeedUpdate = intent.getBooleanExtra("needUpdate", false);
        mTab = intent.getIntExtra("tab", 0); //0指支出、1指收入、2指转账、3指全部
        Log.d(TAG, "onCreate(),mYearMonth=" + mYearMonth + ",mNeedUpdate=" + mNeedUpdate);
        mTvMonthDetail.setText(mYearMonth.substring(0, 4) + "年" + mYearMonth.substring(4) + "月");

        mTvAdd.setOnClickListener(this);
        mIv_open.setOnClickListener(this);

        mHelper = new DBHelper(this);
        mDatabase = mHelper.getWritableDatabase();

        if (AccountApplication.mTypeMap.size() == 0) {
            AccountApplication.getApplication().initTypeMap();
        }
        queryMonthDataFromNet(mYearMonth, mTab);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mYearMonth = intent.getStringExtra("yearmonth");
        mNeedUpdate = intent.getBooleanExtra("needUpdate", false);
        Log.d(TAG, "onNewIntent(),mYearMonth=" + mYearMonth + ",mNeedUpdate=" + mNeedUpdate);
        if (mNeedUpdate) {
            queryMonthDataFromNet(mYearMonth, mTab);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_add) {
            startActivity(new Intent(mContext, AccounDetailActivity.class));  //点击“记一笔”
        } else if (id == R.id.iv_open) {
            setMoreDialog();
        }
    }

    /**
     * 上到下
     *
     * @param dialog
     */
    public static void setWindowAnimationsTop(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_animation);
    }

    private void queryMonthDataFromNet(final String month, final int tab) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String tabPath = "";
                    if (tab != 3) {
                        tabPath = "&tab=" + tab;
                    }
                    String path = Utils.getBillPathByMonth("my", month) + tabPath;
                    final String result = Utils.getHtml(path);
                    Log.d(TAG, "queryData(),month=" + month + ",path=" + path + ",result=" + result);
                    mLvDetail.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                mAllUserData = new ArrayList<>();
                                mAnData = new ArrayList<>();
                                mCaiData = new ArrayList<>();
                                mSumMoney = 0;
                                mAnMoney = 0;
                                mCaiMoney = 0;
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
                                    String tab = jsonObject.getString("tab");
                                    float moneyFloat = Float.parseFloat(money);

                                    AccountBean account = new AccountBean(timeStr, type_id, moneyFloat, user_id, note, bill_id,tab);
                                    mAllUserData.add(account);

                                    if (user_id.equals("an")) {
                                        mAnData.add(account);
                                        mAnMoney += moneyFloat;
                                    } else if (user_id.equals("cai")) {
                                        mCaiData.add(account);
                                        mCaiMoney += moneyFloat;
                                    }
                                }
                                mSumMoney = mAnMoney + mCaiMoney;
                                mTv_sum.setText("合计:" + mSumMoney);
                                DetailMonthAdapter adapter = new DetailMonthAdapter(mContext, mAllUserData);
                                mLvDetail.setAdapter(adapter);
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

    private static final int SUM = 0;
    private static final int AN = 1;
    private static final int CAI = 2;
    private AlertDialog mMoreDialog;

    private void setMoreDialog() {
        Log.d(TAG, "setMoreDialog()");
        ArrayList<String> itemList = new ArrayList();
        final ArrayList<Integer> whichList = new ArrayList();

        itemList.add("合计:" + mSumMoney);
        whichList.add(SUM);

        itemList.add("an:" + mAnMoney);
        whichList.add(AN);

        itemList.add("cai:" + mCaiMoney);
        whichList.add(CAI);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        int size = itemList.size();
        String[] itemArr = new String[size];
        for (int i = 0; i < size; i++) {
            itemArr[i] = itemList.get(i);
        }

        builder.setCancelable(true);
        builder.setItems(itemArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (whichList.get(which)) {
                    case SUM:
                        mLvDetail.setAdapter(new DetailMonthAdapter(mContext, mAllUserData));
                        mTv_sum.setText("合计:" + mSumMoney);
                        break;
                    case AN:
                        mLvDetail.setAdapter(new DetailMonthAdapter(mContext, mAnData));
                        mTv_sum.setText("an:" + mAnMoney);
                        break;
                    case CAI:
                        mLvDetail.setAdapter(new DetailMonthAdapter(mContext, mCaiData));
                        mTv_sum.setText("cai:" + mCaiMoney);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });

        mMoreDialog = builder.create();


        Window window = mMoreDialog.getWindow();
        window.setWindowAnimations(R.style.dialog_animation);

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setGravity(Gravity.TOP);

        int[] dividerLocate = new int[2];
        mDivider.getLocationOnScreen(dividerLocate);

        int statusbarHeight = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusbarHeight = this.getResources().getDimensionPixelSize(resourceId);
        }

        layoutParams.y = dividerLocate[1] - statusbarHeight + Utils.dip2px(this, 1);
        mMoreDialog.show();
    }

    private PopupMenu mPopMenu;

    @SuppressLint("RestrictedApi")
    private void setPopupMenu() {
        Log.d(TAG, "setMoreMenu()");
        if (mPopMenu != null) {
            mPopMenu.getMenu().clear();
        } else {
            mPopMenu = new PopupMenu(this, mDivider, Gravity.CENTER);
        }
        mPopMenu.setGravity(Gravity.END);
        Menu menu = mPopMenu.getMenu();
        menu.add(0, SUM, 0, "合计:" + mSumMoney).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, AN, 0, "an:" + mAnMoney).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, CAI, 0, "cai:" + mCaiMoney).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        // PCI <dongxinran> add end for RCS
        mPopMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case SUM:
                        mLvDetail.setAdapter(new DetailMonthAdapter(mContext, mAllUserData));
                        mTv_sum.setText("合计:" + mSumMoney);
                        break;
                    case AN:
                        mLvDetail.setAdapter(new DetailMonthAdapter(mContext, mAnData));
                        mTv_sum.setText("an:" + mAnMoney);
                        break;
                    case CAI:
                        mLvDetail.setAdapter(new DetailMonthAdapter(mContext, mCaiData));
                        mTv_sum.setText("cai:" + mCaiMoney);
                        break;
                    default:
                        mPopMenu.dismiss();
                        mPopMenu = null;
                        break;
                }
                return false;
            }
        });

        if (mPopMenu != null) {
            mPopMenu.show();
        }
    }

}
