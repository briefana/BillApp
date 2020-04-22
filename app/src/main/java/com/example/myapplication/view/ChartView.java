package com.example.myapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wangke on 2017/2/20.
 * 自定义折线图
 * 文章来源 https://blog.csdn.net/qq_19560943/article/details/56295318
 */

public class ChartView extends View {

    private static String TAG = "ChartView:AJ";
    private int mViewHeight; //当前View的高
    private int mViewWidth; //当前View的宽

    private Paint mPaintCdt;// 绘制坐标系的画笔
    private Paint mPaintSysPoint; //绘制坐标系上刻度点
    private Paint mPaintLinePoint; //绘制折线上的点
    private Paint mPaintSys; //绘制x,y轴
    private Paint mPaintText; //绘制文字
    private Paint mPaintLine; //绘制折线
    private Paint mPaintDash; //绘制虚线

    private Rect mXBound;
    private Rect mYBound;

    private ArrayList<Point> mActualPointList = null;  //实际坐标数值，年月为横坐标，支出为纵坐标
    private ArrayList<Point> mPointList = null;  //换算坐标，X轴坐标从1、2、3算
    private int mXMax; //传入点的X的最大坐标
    private int mYMax; //传入点的Y的最大坐标
    private float mScreenXdistance; //x轴刻度在屏幕上的间隔
    private float mScreenYdistance; //y轴刻度在屏幕上的间隔

    //折线图距离四周的像素大小
    private int mMargin = 80;

    private int mCoordinateSystemColor;
    private float mCoordinateSystemSize;
    private int mLineColor;
    private float mLineSize;
    private int mLineColorPoint;
    private float mLineColorPointRadius;
    private int mScalePointColor;
    private float mScalePointRadius;
    private boolean mIsShowDash;
    private int mXScale;
    private int mYScale;
    private float mDashSize;
    private int mDashColor;

    public ChartView(Context context) {
        super(context);
        initPaint();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);   //获取属性值
        initPaint();
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        initPaint();
    }

    //设置点的数据
    public void setPoint(ArrayList<Point> points) {
        mActualPointList = points;
        mPointList = new ArrayList();
        for (int i = 0; i < mActualPointList.size(); i++) {
            Point point = mActualPointList.get(i);
            Point p = new Point(i + 1, point.y);  //换算坐标，X轴坐标从1、2、3算
            Log.i(TAG, "setPoint(),Point x=" + (i + 1) + ",y=" + point.y + ",actual.x=" + point.x);
            mPointList.add(p);
        }

        int[] xPointArray = new int[100];
        int[] yPointArray = new int[100];

        //遍历传入的点的坐标，获取最大的x,y点的坐标，用来计算刻度
        for (int i = 0; i < mPointList.size(); i++) {
            Point point = mPointList.get(i);
            xPointArray[i] = point.x;
            yPointArray[i] = point.y;
        }
        Arrays.sort(xPointArray);
        Arrays.sort(yPointArray);
        mXMax = xPointArray[xPointArray.length - 1];
        mYMax = yPointArray[yPointArray.length - 1];
        Log.i(TAG, "setPoint(),X的最大坐标：mXMax=" + mXMax);
        Log.i(TAG, "setPoint(),y的最大坐标：mYMax=" + mYMax);

        invalidate();  //调用绘制
    }

    //初始化画笔
    private void initPaint() {
        // 绘制坐标系的画笔
        mPaintCdt = new Paint(Paint.ANTI_ALIAS_FLAG);  //抗锯齿标志
        mPaintCdt.setStyle(Paint.Style.STROKE); //设置画线
        mPaintCdt.setStrokeWidth(mLineSize); //设置线的宽度
        mPaintCdt.setColor(mLineColor);

        //绘制坐标系上刻度点
        mPaintSysPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSysPoint.setStyle(Paint.Style.FILL); //设置填充
        mPaintSysPoint.setColor(mScalePointColor);

        //绘制折线上的点
        mPaintLinePoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLinePoint.setStyle(Paint.Style.FILL);  //设置填充
        mPaintLinePoint.setColor(mLineColorPoint);

        //绘制xy轴
        mPaintSys = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSys.setStyle(Paint.Style.STROKE); //设置画线
        mPaintSys.setStrokeWidth(mCoordinateSystemSize);  //设置线的宽度
        mPaintSys.setColor(mCoordinateSystemColor);

        //绘制文字
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize(30);

        //绘制折线
        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setStyle(Paint.Style.STROKE); //设置画线
        mPaintLine.setStrokeWidth(mLineSize); //设置线的宽度
        mPaintLine.setColor(mLineColor);  //设置画笔的颜色
        /*mPaintLine.setStrokeJoin(Paint.Join.ROUND);  //设置曲线，不顶用
        CornerPathEffect cornerPathEffect = new CornerPathEffect(200);
        mPaintLine.setPathEffect(cornerPathEffect);*/

        //绘制虚线
        mPaintDash = new Paint();
        mPaintDash.setStyle(Paint.Style.STROKE);
        mPaintDash.setStrokeWidth(mDashSize);
        mPaintDash.setColor(mDashColor);
        mPaintDash.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        mXBound = new Rect();
        mYBound = new Rect();

        invalidate();
    }

    //获取设置的属性
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChartView);
        //坐标系颜色
        mCoordinateSystemColor = ta.getColor(R.styleable.ChartView_coordinateSystemColor, Color.RED);
        mCoordinateSystemSize = ta.getDimension(R.styleable.ChartView_coordinateSystemLineSize, 3f);
        //折线颜色
        mLineColor = ta.getColor(R.styleable.ChartView_lineColor, Color.BLACK);
        mLineSize = ta.getDimension(R.styleable.ChartView_lineSize, 2f);
        //折线上坐标点颜色
        mLineColorPoint = ta.getColor(R.styleable.ChartView_linePointColor, Color.RED);
        //折线上坐标点的半径
        mLineColorPointRadius = ta.getDimension(R.styleable.ChartView_linePointRadius, 6f);
        //刻度点颜色
        mScalePointColor = ta.getColor(R.styleable.ChartView_scalePointColor, Color.RED);
        //刻度点半径
        mScalePointRadius = ta.getDimension(R.styleable.ChartView_scalePointRadius, 6);
        //设置是否显示虚线
        mIsShowDash = ta.getBoolean(R.styleable.ChartView_showDash, true);
        mDashSize = ta.getDimension(R.styleable.ChartView_setDashSize, 2f);
        mDashColor = ta.getColor(R.styleable.ChartView_setDashColor, Color.GRAY);

        mXScale = ta.getInt(R.styleable.ChartView_setXScale, 5); //x轴坐标点间隔的值,xml文件中设为1
        mYScale = ta.getInt(R.styleable.ChartView_setYScale, 5); //y轴坐标点间隔的值,xml文件中设为1000

        ta.recycle();
        Log.i(TAG, "getAttrs(),mXScale=" + mXScale + ",mYScale=" + mYScale);
    }

    //测量view
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    //测量view宽度
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specSize = MeasureSpec.getSize(widthMeasureSpec); //获取高的高度，xml中是match_parent，1080px
        int specMode = MeasureSpec.getMode(widthMeasureSpec);//获取测量的模式
        //如果是精确测量，就将获取View的大小设置给将要返回的测量值
        if (specMode == MeasureSpec.EXACTLY) {
            Log.i(TAG, "measureWidth(),宽度:精确测量 specSize=" + specSize);  //1080
            result = specSize;
        } else {
            Log.i(TAG, "measureWidth(),宽度:UNSPECIFIED specSize=" + specSize);
            result = 400;
            //如果设置成wrap_content时，给高度指定一个值
            if (specMode == MeasureSpec.AT_MOST) {
                Log.i(TAG, "measureWidth(),宽度:最大值模式 specSize=" + specSize);
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    //测量view高度
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的高度 单位 为px，xml中是 300dp，900px
        int specMode = MeasureSpec.getMode(heightMeasureSpec);//获取测量的模式
        //如果是精确测量，就将获取View的大小设置给将要返回的测量值
        if (specMode == MeasureSpec.EXACTLY) {
            Log.i(TAG, "measureHeight(),高度:精确测量,specSize=" + specSize);
            result = specSize;
        } else {
            Log.i(TAG, "measureHeight(),高度:UNSPECIFIED,specSize=" + specSize);
            result = 400;
            //如果设置成wrap_content时，给高度指定一个值
            if (specMode == MeasureSpec.AT_MOST) {
                Log.i(TAG, "measureHeight(),高度:最大值模式,specSize=" + specSize);
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取当前View的宽高
        mViewWidth = w;
        mViewHeight = h;
        Log.i(TAG, "onSizeChanged(),mViewWidth=" + w);
        Log.i(TAG, "onSizeChanged(),mViewHeight=" + h);
    }

    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPointList == null || mPointList.size() == 0) {
            return;
        }
        //绘制X轴Y轴 以及原点,drawLine(float startX, float startY, float stopX, float stopY, Paint paint)
        canvas.drawLine(mMargin, mViewHeight - mMargin, mMargin, 5, mPaintSys);  //mMargin = 80;
        canvas.drawLine(mMargin, mViewHeight - mMargin, mViewWidth - 5, mViewHeight - mMargin, mPaintSys);
        canvas.drawCircle(mMargin, mViewHeight - mMargin, mScalePointRadius, mPaintSysPoint);

        /**
         * 计算两个刻度之间的间距：
         *     1.刻度的个数 = 传入坐标最大的坐标点/坐标轴间距
         *     2.两个刻度之间的间距 = 屏幕的宽或高 /刻度的个数
         */
        int num_x = mXMax / mXScale; //x轴上需要绘制的刻度的个数
        if (mXMax % mXScale != 0) {
            num_x += 1;  //如果最大值除以间隔还有余数，那刻度个数再增加1
        }
        mScreenXdistance = (mViewWidth - mMargin * 2) / (num_x * 1f);

        Log.i(TAG, "onDraw(),需要绘制的刻度个数 num_x=" + num_x + ",两个刻度间间隔 mScreenXdistance=" + mScreenXdistance);

        int num_y = mYMax / mYScale;
        if (mYMax % mYScale != 0) {
            num_y += 1;
        }
        mScreenYdistance = (mViewHeight - mMargin * 2) / (num_y * 1f);
        Log.i(TAG, "onDraw(),需要绘制的刻度个数 num_y=" + num_y + ",两个刻度间间隔 mScreenXdistance=" + mScreenXdistance);

        //绘制 X,y轴刻度标记
        int gap = 0;
        if (num_x > 7) {
            gap = num_x / 7;
            if (gap > 0 && num_x % 7 > 0) {
                gap++;
            }
        }
        for (int i = 0; i <= num_x; i++) {
            if (gap > 0) {
                if ((i - 1) % gap == 0) {

                } else {
                    continue;
                }
            }

            canvas.drawCircle(mMargin + (i * mScreenXdistance), mViewHeight - mMargin, mScalePointRadius, mPaintSysPoint);
            canvas.drawCircle(mMargin, mViewHeight - mMargin - (i * mScreenYdistance), mScalePointRadius, mPaintSysPoint);

            //计算刻度字体的宽高
            String index_x = String.valueOf(i * mXScale);
            String index_y = String.valueOf(i * mYScale);
            mPaintText.getTextBounds(index_x, 0, index_x.length(), mXBound);
            mPaintText.getTextBounds(index_y, 0, index_x.length(), mYBound);
            int indexWidth_x = mXBound.width();
            int indexHeight_x = mXBound.height();
            int indexWidth_y = mYBound.width();
            int indexHeight_y = mYBound.height();

            Log.i(TAG, "onDraw(),绘制 X ,i=" + i + ",字体的宽度：" + indexWidth_x + "字体的高度：" + indexHeight_x);
            if (i != 0) {
                String yearMonth = mActualPointList.get(i - 1).x + "";
                index_x = yearMonth.substring(0, 4) + "-" + yearMonth.substring(4);
            }
            canvas.drawText(index_x, mMargin + (i * mScreenXdistance), mViewHeight - mMargin + indexHeight_x + indexHeight_x / 2, mPaintText);
            /*if (i != 0) {
                canvas.drawText(index_y, Margin - indexHeight_y - indexWidth_y / 2, mViewHeight - Margin - (i * mScreenYdistance), mPaintText);
            }*/
        }

        for (int i = 0; i <= num_y; i++) {
            canvas.drawCircle(mMargin + (i * mScreenXdistance), mViewHeight - mMargin, mScalePointRadius, mPaintSysPoint);
            canvas.drawCircle(mMargin, mViewHeight - mMargin - (i * mScreenYdistance), mScalePointRadius, mPaintSysPoint);

            //计算刻度字体的宽高
            String index_x = String.valueOf(i * mXScale);
            String index_y = String.valueOf(i * mYScale);
            mPaintText.getTextBounds(index_x, 0, index_x.length(), mXBound);
            mPaintText.getTextBounds(index_y, 0, index_x.length(), mYBound);
            int indexWidth_x = mXBound.width();
            int indexHeight_x = mXBound.height();
            int indexWidth_y = mYBound.width();
            int indexHeight_y = mYBound.height();

            Log.i(TAG, "onDraw(),绘制 Y ,i=" + i + ",字体的宽度：" + indexWidth_x + "字体的高度：" + indexHeight_x);
//            canvas.drawText(index_x, Margin + (i * mScreenXdistance), mViewHeight - Margin + indexHeight_x + indexHeight_x / 2, mPaintText);
            if (i != 0 && i % 2 == 0) {
                canvas.drawText(index_y, mMargin - indexHeight_y - indexWidth_y / 2, mViewHeight - mMargin - (i * mScreenYdistance), mPaintText);
            }
        }

        /**
         * 绘制折线
         */
        Point LastPoint = new Point(); //记录上一个坐标点
        LastPoint.x = mMargin;
        LastPoint.y = mViewHeight - mMargin;

        for (int i = 0; i < mPointList.size(); i++) {
            /**
             * 计算绘制点的坐标位置
             * 绘制点的坐标 =  (传入点的的最大的xy坐标/坐标轴上的间隔） * 坐标间隔对应的屏幕上的间隔
             */
//            canvas.drawCircle(LastPoint.x,LastPoint.y,4f,mPaintPoint);
            //计算出脱离坐标系的点所处的位置
            float point_x = (mPointList.get(i).x / (mXScale * 1f)) * mScreenXdistance;
            float point_y = (mPointList.get(i).y / (mYScale * 1f)) * mScreenYdistance;
            Log.i(TAG, "onDraw(),point_x=" + point_x + ",point_y=" + point_y);

            //坐标系内的点的位置
            float startX = LastPoint.x;
            float startY = LastPoint.y;

            float endX = mMargin + point_x;
            float endY = mViewHeight - mMargin - point_y;

            //需要计算此处,绘制折线，加上if (i != 0)，即不绘制坐标原点到第一个坐标间的线
            if (i != 0) {
                canvas.drawLine(startX, startY, endX, endY, mPaintLine);
            }

            //记录上一个坐标点的位置
            LastPoint.x = (int) endX;
            LastPoint.y = (int) endY;
            Log.i(TAG, "onDraw(),绘制横向虚线，mIsShowDash=" + mIsShowDash);
            if (true) {
                Log.i(TAG, "onDraw(),绘制横向虚线");
                //绘制横向虚线
                canvas.drawLine(mMargin, mViewHeight - mMargin - point_y - mLineColorPointRadius / 2, mMargin + point_x - mLineColorPointRadius / 2, mViewHeight - mMargin - point_y - mLineColorPointRadius / 2, mPaintDash);
                //绘制竖向虚线
                canvas.drawLine(LastPoint.x, LastPoint.y, LastPoint.x, mViewHeight - mMargin - mLineColorPointRadius, mPaintDash);
            }

            canvas.drawCircle(LastPoint.x, LastPoint.y, mLineColorPointRadius, mPaintLinePoint);
        }
    }

}
