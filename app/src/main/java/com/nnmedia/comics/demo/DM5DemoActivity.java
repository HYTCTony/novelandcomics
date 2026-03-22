package com.nnmedia.comics.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nnmedia.novel.R;

/**
 * 腾讯动漫 Demo Activity
 * 点击按钮运行 demo，查看日志输出
 */
public class DM5DemoActivity extends AppCompatActivity {

    private static final String TAG = "TencentDemoActivity";

    private ScrollView mScrollView;
    private TextView mLogTextView;
    private Button mRunButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dm5_demo);

        initViews();
        initListeners();

        log("========== 腾讯动漫 Demo ==========");
        log("点击按钮开始运行 demo");
        log("所有输出会显示在日志（Logcat）中，过滤标签：TencentDemo 或 TencentDemoActivity");
    }

    private void initViews() {
        mScrollView = findViewById(R.id.scroll_view);
        mLogTextView = findViewById(R.id.log_text_view);
        mRunButton = findViewById(R.id.run_demo_button);
    }

    private void initListeners() {
        mRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDemo();
            }
        });
    }

    /**
     * 运行 demo
     */
    private void runDemo() {
        log("\n========== 开始运行 Demo ==========");

        // 在后台线程运行
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TencentDemo demo = new TencentDemo(DM5DemoActivity.this);
                    demo.runDemo();
                } catch (Exception e) {
                    log("Demo 运行异常: " + e.getMessage());
                    Log.e(TAG, "Demo 运行异常", e);
                }
            }
        }).start();
    }

    /**
     * 记录日志
     * 
     * @param message 日志消息
     */
    private void log(final String message) {
        Log.d(TAG, message);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = mLogTextView.getText().toString();
                text += message + "\n";
                mLogTextView.setText(text);
                
                // 滚动到底部
                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
    }
}
