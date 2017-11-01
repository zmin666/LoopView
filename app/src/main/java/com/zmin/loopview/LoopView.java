package com.zmin.loopview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * @author: ZhangMin
 * @date:  2017/11/1 14:43
 * @desc: 自定义倒计时环形控件
 */
public class LoopView extends View {

    /** 画笔 */
    private Paint mPaint;
    private Paint mTextPaint;
    /** 画图的范围 */
    private RectF mRectF;
    /** 偏移量.防止画的图超出屏幕范围 默认取值30 */
    private int t = 7;

    private int mMWidth;
    private int mHeight;
    public float mPersent;
    private String mM;
    private String mS;

    private CountDownTimer mCountDownTimer;
    private DisplayMetrics mDisplayMetrics;

    /** 总时间 默认30s */
    private long mTotalTime = 30 * 60 * 1000;
    /** 剩余时间 */
    private long mRemineTime = -1;


    public interface OnTimeCountListener {
        void finish();
    }

    OnTimeCountListener mOnTimeCountListener;

    public void setOnTimeCountListener(OnTimeCountListener onTimeCountListener) {
        mOnTimeCountListener = onTimeCountListener;
    }

    public LoopView(Context context) {
        this(context, null);
    }

    public LoopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mDisplayMetrics = getContext().getResources().getDisplayMetrics();
        t = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, t, mDisplayMetrics);
        //环的画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mDisplayMetrics);
        mPaint.setStrokeWidth(strokeWidth);//圆圈的线条粗细
        //文本画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);

        //默认总时间
        mM = String.valueOf(mTotalTime / 1000 / 60);
        mS = "00";

        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        int mHigh = mMWidth;
        //范围
        mRectF = new RectF(t, t, mMWidth - t, mHigh - t);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //整体背景
        mPaint.setStyle(Paint.Style.FILL); //绘制图形的描边
        mPaint.setColor(getResources().getColor(R.color.black_27));
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        //外环 背景绿色
        mPaint.setStyle(Paint.Style.STROKE); //绘制图形的描边
        mPaint.setColor(getResources().getColor(R.color.blue_86));
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        //剩余时间 文字
        String s = "剩余时间";
        float ts = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, mDisplayMetrics);
        mTextPaint.setTextSize(ts);
        mTextPaint.setColor(getResources().getColor(R.color.text_color_99));
        float x = mTextPaint.measureText(s);
        canvas.drawText(s, mMWidth / 2 - x / 2, (float) mHeight * 0.4f, mTextPaint);

//        动态改变的部分
        //内环 背景灰色
        mPaint.setColor(getResources().getColor(R.color.gray_c1));
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        float swap = mPersent * 360;
        canvas.drawArc(mRectF, -90, swap, false, mPaint);

        // 绘制进度文案显示
        String text = mM + ":" + mS;
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 21, mDisplayMetrics);
        float textWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, mDisplayMetrics);
        mTextPaint.setStrokeWidth(textWidth);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(getResources().getColor(R.color.text_color_ff));
        mTextPaint.setStyle(Paint.Style.FILL);
        float width = mTextPaint.measureText(text);
        canvas.drawText(text, mMWidth / 2 - width / 2, (float) mHeight * 0.65f, mTextPaint);
    }

    /**
     * 设置总时间时间
     *
     * @param second 秒
     */
    public void setTotalTime(int second) {
        mTotalTime = second * 1000;
        changeForTimeText(second);
    }

    /**
     * 设置倒计时时间
     *
     * @param second 剩余时间单位 秒
     */
    public void setRemineTime(int second) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        if (second > mTotalTime / 1000) {
            try {
                throw new Exception("the time your set is less than total time, please reset it ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        mRemineTime = second * 1000;
        changeForTimeText(second);
        starTimecount();
    }

    /**
     * 根据倒计时时间设置文本
     *
     * @param second
     */
    private void changeForTimeText(int second) {
        int m = second / 60;
        int s1 = second % 60;
        mM = m < 10 ? "0" + m : String.valueOf(m);
        mS = s1 < 10 ? "0" + s1 : String.valueOf(s1);
    }

    /**
     * 开始倒计时
     */
    public void starTimecount() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mRemineTime = mRemineTime == -1 ? mTotalTime : mRemineTime;

        mCountDownTimer = new CountDownTimer(mRemineTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mPersent = (mTotalTime - millisUntilFinished) / (float)mTotalTime;
                Log.i("zmin.............", ".persent..." + mPersent);
                changeForTimeText((int) millisUntilFinished / 1000);
                invalidate();
            }

            @Override
            public void onFinish() {
                mPersent = 1;
                changeForTimeText(0);
                invalidate();
                if (mOnTimeCountListener != null) {
                    mOnTimeCountListener.finish();
                }
            }
        };
        mCountDownTimer.start();
    }


}
