package com.nnmedia.page.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nnmedia.novel.R;
import com.nnmedia.page.ui.base.BaseActivity;
import com.nnmedia.read.luckwheel.LuckBean;
import com.nnmedia.read.luckwheel.LuckItemInfo;
import com.nnmedia.read.luckwheel.NewLuckView;
import com.nnmedia.read.utils.StatusBarUtils;

import java.util.ArrayList;

public class ReadTestActivity extends BaseActivity {

    private NewLuckView luckView;
    private int[] images = new int[]{R.drawable.huawei, R.drawable.image_one, R.drawable.iphone, R.drawable.image_one, R.drawable.macbook,
            R.drawable.image_one, R.drawable.meizu, R.drawable.xiaomi};
    private String[] strs = {"谢谢惠顾", "三天VIP", "一周VIP", "一月VIP", "一年VIP", "永久VIP", "1000金币", "9999金币"};

    public static void start(Context context) {
        Intent starter = new Intent(context, ReadTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {
        StatusBarUtils.setStatusBarTextDark(this, false);

        luckView = (NewLuckView) findViewById(R.id.luck_view);
        luckView.setIndicatorResourceId(R.drawable.node);
        ArrayList<LuckItemInfo> items = new ArrayList<>();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            LuckItemInfo luckItem = new LuckItemInfo();
            luckItem.prize_name = strs[i];
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), images[i]);
            bitmaps.add(bitmap);
            items.add(luckItem);
        }

        LuckBean luck = new LuckBean();
        luck.details = items;
//        load数据
        luckView.loadData(luck, bitmaps);
//        luckView.setEnable(false); 设置是否可用 默认为true
        //添加监听 当luckview检测到indicator所在位置被点击时，会自动开始旋转
        luckView.setLuckViewListener(new NewLuckView.LuckViewListener() {
            @Override
            public void onStart() {

                //模拟网络请求获取抽奖结果，然后设置选中项的index值
                luckView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                            Random random = new Random();
//                            int i = random.nextInt(6);
                        luckView.setStop(1);
                    }
                }, 3000);
            }

            @Override
            public void onStop(int index) {

            }
        });


    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_read_test;
    }

}
