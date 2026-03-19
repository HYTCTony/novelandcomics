package com.nnmedia.page.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.nnmedia.novel.R;
import com.nnmedia.read.FrApp;

public class HProgressBar extends View {
    private final String TAG = "HProgressBar";
    private int mProgress_outline_color = Color.parseColor("#DFDFDF");//外边框颜色
    private int mProgress_color_1 = Color.parseColor("#F2AA91"); //进度条颜色（渐变1）
    private int mProgress_color_2 = Color.parseColor("#F88CB3"); //进度条颜色（渐变2）
    private int mProgress_circle_color = Color.parseColor("#F88CB3");  //圆圈颜色
    private int mProgress_text_color = Color.parseColor("#FFFFFF");  //进度字体颜色
    private int mProgress_text_bg_color = Color.parseColor("#FD60A5");  //进度背景颜色
    private float mProgress_circle_height = dpToPx(16);  //圆圈高度
    private float mProgress_height = dpToPx(6);  //progress高度
    private float mProgress_bar_height = dpToPx(6);  //progress进度条高度
    private float mProgress_text_height = dpToPx(8);  //进度文字高度
    private float triangle_height = dpToPx(2);  //三角形补偿高度（数字越大，三角形角度越小）
    private float mProgress_text_paddingH = dpToPx(8);  //进度文字左右padding
    private float mProgress_text_paddingV = dpToPx(6);  //进度文字上下pading
    private float mProgress_text_size = dpToPx(10);  //进度文字字体大小
    private float mMaxProgress = 100;
    private float mProgress_progress_bar = 0;
    private int mProgressWidth;

    private Paint mOutLinePaint;
    private Paint mProgressPaint;
    private Paint mCirClePaint;
    private Paint mTextPain;
    private Paint mTextBgPain;

    public HProgressBar(Context context) {
        super(context);
        Log.d(TAG, "HProgressBar: 1");
    }

    public HProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "HProgressBar: 2");
        initDefStyleAttr(attrs);
        mOutLinePaint = new Paint();
        mOutLinePaint.setColor(mProgress_outline_color);
        mOutLinePaint.setAntiAlias(true);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);

        mCirClePaint = new Paint();
        mCirClePaint.setColor(mProgress_circle_color);
        mCirClePaint.setAntiAlias(true);

        mTextBgPain = new Paint();
        mTextBgPain.setColor(mProgress_text_bg_color);
        mTextBgPain.setAntiAlias(true);

        mTextPain = new Paint();
        mTextPain.setColor(mProgress_text_color);
        mTextPain.setAntiAlias(true);
        mTextPain.setTextSize(mProgress_text_size);
        mTextPain.setTextAlign(Paint.Align.CENTER);
        mTextPain.setTextSize(mProgress_text_size);
    }

    public HProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "HProgressBar: 3");
    }

    public HProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.d(TAG, "HProgressBar: 4");
    }

    @SuppressLint({"CustomViewStyleable", "Recycle"})
    private void initDefStyleAttr(AttributeSet attrs) {

        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.HProgress);
        mProgress_outline_color = attributes.getColor(R.styleable.HProgress_hProgress_outline_color, mProgress_outline_color);
        mProgress_color_1 = attributes.getColor(R.styleable.HProgress_hProgress_color_1, mProgress_color_1);
        mProgress_color_2 = attributes.getColor(R.styleable.HProgress_hProgress_color_2, mProgress_color_2);
        mProgress_circle_color = attributes.getColor(R.styleable.HProgress_hProgress_circle_color, mProgress_circle_color);
        mProgress_text_color = attributes.getColor(R.styleable.HProgress_hProgress_text_color, mProgress_text_color);
        mProgress_text_bg_color = attributes.getColor(R.styleable.HProgress_hProgress_text_bg_color, mProgress_text_bg_color);
        mProgress_circle_height = (int) attributes.getDimension(R.styleable.HProgress_hProgress_circle_height, mProgress_circle_height);
        mProgress_height = (int) attributes.getDimension(R.styleable.HProgress_hProgress_height, mProgress_height);
        mProgress_bar_height = (int) attributes.getDimension(R.styleable.HProgress_hProgress_bar_height, mProgress_bar_height);
        mProgress_text_height = (int) attributes.getDimension(R.styleable.HProgress_hProgress_text_height, mProgress_text_height);
        mProgress_text_size = (int) attributes.getDimension(R.styleable.HProgress_hProgress_text_size, mProgress_text_size);
        mProgress_text_paddingH = (int) attributes.getDimension(R.styleable.HProgress_hProgress_text_paddingH, mProgress_text_paddingH);
        mProgress_text_paddingV = (int) attributes.getDimension(R.styleable.HProgress_hProgress_text_paddingV, mProgress_text_paddingV);
        mProgress_progress_bar = attributes.getInteger(R.styleable.HProgress_hProgress_progress_bar, (int) mProgress_progress_bar);
        mMaxProgress = attributes.getInteger(R.styleable.HProgress_hProgress_maxProgress, (int) mMaxProgress);
    }

    private int dpToPx(int dp) {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private DisplayMetrics getDisplayMetrics() {
        return FrApp.getInstance().getResources().getDisplayMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mProgressWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(mProgressWidth, (int) (mProgress_text_height + mProgress_circle_height + mProgress_text_paddingV));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mTextPain.getTextBounds(String.valueOf(getProgress()), 0, String.valueOf(getProgress()).length(), rect);
        float w = rect.width();
        float h = rect.height();

        float spaceA = (mProgress_circle_height - mProgress_height) / 2;
        float spaceB = (mProgress_height - mProgress_bar_height) / 2;
        float progressMargin = mProgress_circle_height / 2;
        //长度变化范围等于控件长度减去两边的边距和空隙，由于是圆角进度条，还要减去内进度条的高度，即内进度条两边半圆的长度
        float changeRange = mProgressWidth - (progressMargin + spaceB) * 2 - mProgress_bar_height;
        float startPoint = progressMargin + spaceB;
        float circleX = startPoint + mProgress_bar_height / 2 + changeRange * getProgress() / mMaxProgress;

        float textStartX;
        float textEndX;
        if (circleX - w / 2 - mProgress_text_paddingH < 0) {
            textStartX = 0;
            textEndX = textStartX + w + mProgress_text_paddingH * 2;
        } else if (circleX + w / 2 + mProgress_text_paddingH >= mProgressWidth) {
            textStartX = mProgressWidth - mProgress_text_paddingH * 2 - w;
            textEndX = mProgressWidth;
        } else {
            textStartX = circleX - w / 2 - mProgress_text_paddingH;
            textEndX = circleX + w / 2 + mProgress_text_paddingH;
        }

        //画三角形
        mTextBgPain.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.moveTo(textStartX + (textEndX - textStartX) / 2, mProgress_height + spaceA);
        path.lineTo(textEndX - (textEndX - textStartX) / 3, mProgress_circle_height + triangle_height);
        path.lineTo(textStartX + (textEndX - textStartX) / 3, mProgress_circle_height + triangle_height);
        canvas.drawPath(path, mTextBgPain);

        //画数字背景框
        RectF rectF2 = new RectF(textStartX, mProgress_circle_height, textEndX, mProgress_circle_height + mProgress_text_height + mProgress_text_paddingV);
        canvas.drawRoundRect(rectF2, mProgress_text_height, mProgress_text_height, mTextBgPain);

        //画数字进度
        Paint.FontMetrics fontMetrics = mTextPain.getFontMetrics();
        canvas.drawText(getProgress() + "", (textStartX + textEndX) / 2,
                mProgress_circle_height + mProgress_text_height / 2 + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent + mProgress_text_paddingV / 2, mTextPain);

        //画progressBar 外边
        RectF rectF = new RectF(progressMargin, spaceA, mProgressWidth - progressMargin, mProgress_height + spaceA);
        canvas.drawRoundRect(rectF, mProgress_height / 2, mProgress_height / 2, mOutLinePaint);

        //画progressBar 内进度条
        Shader shader = new LinearGradient(startPoint, spaceA, startPoint + mProgress_bar_height + changeRange * getProgress() / mMaxProgress,
                mProgress_height + spaceA, mProgress_color_1, mProgress_color_2, Shader.TileMode.CLAMP);
        mProgressPaint.setShader(shader);
        RectF rectF1 = new RectF(startPoint, spaceA, startPoint + mProgress_bar_height + changeRange * getProgress() / mMaxProgress,
                mProgress_height + spaceA);
        canvas.drawRoundRect(rectF1, mProgress_bar_height / 2, mProgress_bar_height / 2, mProgressPaint);

        //画圆
//        canvas.drawCircle(circleX, mProgress_circle_height / 2, mProgress_circle_height / 2, mCirClePaint);
    }

    public int getProgress() {
        return (int) mProgress_progress_bar;
    }

    public void setProgress(int progress) {
        if (progress > mMaxProgress) {
            throw new RuntimeException("progress mast less than  mMaxProgress");
        }
        mProgress_progress_bar = progress;
        postInvalidate();
    }

    public void setmMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }
}