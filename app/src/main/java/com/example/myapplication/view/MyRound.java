package com.example.myapplication.view;
/* 代码来源 https://blog.csdn.net/q12q1ty/article/details/51504540 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.Utils;

import java.util.ArrayList;

public class MyRound extends View {

    private int mRoundwidth;//环形的宽度
    private Paint mPaint;//定义画笔
    private int mRoundradius;//圆环的半径
    private RectF mRectF;
    private Context mContext;
    private static String TAG = "MyRound:AJ";
    private Rect mCurrentRound = new Rect();
    private ArrayList<Integer> mAangleList;
    private String mMonthCount;
    private int[] mColors = {
            R.color.red,
            R.color.orange,
            R.color.yellow,
            R.color.green,
            R.color.cyan,
            R.color.blue,
            R.color.violet};

    public MyRound(Context context) {
        super(context);
        mContext = context;
    }

    public MyRound(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setMyRoundAttributes(attrs);
    }

    public MyRound(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setMyRoundAttributes(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);  //抗锯齿

        //绘制圆圈内的文字
        mPaint.setTextSize(30);

        if (!TextUtils.isEmpty(mMonthCount)) {
            mPaint.getTextBounds(mMonthCount, 0, mMonthCount.length(), mCurrentRound);
            float x = getWidth() / 2 - 20 - mRoundwidth;
            float y = mRoundwidth / 2 + mRoundradius - 10;  //50+180=230, - 10是设置的与下面一排字的距离
            Log.d(TAG, "onDraw(),x=" + x + ",y=" + y); //x=165.0,y=230.0
            canvas.drawText(mMonthCount + "", x, y, mPaint);

            mPaint.setColor(getResources().getColor(R.color.greytwo));
            mPaint.getTextBounds("总支出", 0, 3, mCurrentRound);
            float x1 = getWidth() / 2 - mCurrentRound.width() / 2;
            float y1 = mRoundwidth / 2 + mRoundradius + mCurrentRound.height() + 10; // + 10是设置的与下面一排字的距离
            Log.d(TAG, "onDraw(),x1=" + x1 + ",y1=" + y1); //x1=225.0,y1=230.0
            canvas.drawText("总支出", x1, y1, mPaint);
        }


        //设置画圆是的风格和圆的宽度
        mPaint.setStyle(Paint.Style.STROKE);  //置画笔样式，如果不设置，默认是全部填充（FILL）。可选项为：FILL，FILL_OR_STROKE，或STROKE
        mPaint.setStrokeCap(Paint.Cap.SQUARE);  //设置画笔笔刷类型 如影响画笔但始末端，如圆形样式ROUND
        mPaint.setStrokeWidth(mRoundwidth);
        mRectF = new RectF(getWidth() / 2 - mRoundradius, mRoundwidth / 2, getWidth() / 2 + mRoundradius, mRoundwidth / 2 + 2 * mRoundradius);

        /*public void drawArc(
        RectF oval,  圆的范围值，用矩形约束.指定圆弧的外轮廓矩形区域
         float startAngle,  从0度开始，多少度.圆弧扫过的角度，顺时针方向，单位为度,从右中间开始为零度。即平时xy轴的x正方向为0度
         float sweepAngle,  需要画多少度
         boolean useCenter,  如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
         Paint paint  绘制圆弧的画板属性，如颜色，是否填充等。
         )*/

        if (mAangleList != null && mAangleList.size() > 0) {
            int startAngle = -90;
            for (int i = 0; i < mAangleList.size(); i++) {
                int sweepAngle = mAangleList.get(i);
                mPaint.setColor(getResources().getColor(mColors[i]));
                canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
                Log.d(TAG, "onDraw(),i=" + i + ",startAngle=" + startAngle + ",sweepAngle=" + sweepAngle);  //-80.0,sweepAngle=170.0*/
                startAngle += sweepAngle;
            }
        } else {
            mPaint.setColor(getResources().getColor(R.color.greytwo));
            canvas.drawArc(mRectF, -90, 360, false, mPaint);
        }

    }

    //在这里配置可以在xml中直接引用
    private void setMyRoundAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.myround);
        mRoundwidth = a.getInteger(R.styleable.myround_roundwidth, Utils.dip2px(mContext, 14));
        mRoundradius = Utils.dip2px(mContext, 60); //则圆环总宽度为60+14
        Log.d(TAG, "setMyRoundAttributes(),mRoundwidth=" + mRoundwidth + ",roundradius=" + mRoundradius);
        a.recycle();
    }

    public void setAngleListAndMonthCount(ArrayList<Integer> angleList, String monthCount) {
        mAangleList = angleList;
        mMonthCount = monthCount;
        invalidate();
    }

}