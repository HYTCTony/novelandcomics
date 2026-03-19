package com.nnmedia.read.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import com.nnmedia.novel.R;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;


public class TimingButton extends AppCompatTextView {
    private static final String UNIT = " S";
    private int total, interval;
    private String psText;
    private Context context;

    public TimingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 获取自定义属性，并赋值
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TimingButton);
        total = typedArray.getInteger(R.styleable.TimingButton_tb_totalTime, 60 * 1000);
        interval = typedArray.getInteger(R.styleable.TimingButton_tb_timeInterval, 1000);
        psText = typedArray.getString(R.styleable.TimingButton_tb_psText);
        setBackgroundResource(R.drawable.ripple_normal_btn_bg_white); //设置默认样式
        typedArray.recycle();
    }

    //执行
    public void start() {
        time = new TimeCount(total, interval);
        time.start();
    }


    public void reset(){
        if(time!=null){
            setText(psText);
            setEnabled(true);
            setTextColor(ContextCompat.getColor(context, R.color.col_red));
            time.cancel();
        }
    }

    private TimeCount time;

    public class TimeCount extends CountDownTimer {
        private long countDownInterval;

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
            this.countDownInterval = countDownInterval;
        }


        @Override
        public void onFinish() {//计时完毕时触发
            setText(psText);
            setEnabled(true);
            setTextColor(ContextCompat.getColor(context, R.color.col_red));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            setEnabled(false);
            setTextColor(ContextCompat.getColor(context, R.color.txt_gray_999));
            setText(millisUntilFinished / countDownInterval + UNIT);
        }
    }

}
