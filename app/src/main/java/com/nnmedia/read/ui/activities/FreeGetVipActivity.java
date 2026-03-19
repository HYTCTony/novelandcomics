package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.VipChargerEvent;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.luckwheel.LuckBean;
import com.nnmedia.read.luckwheel.LuckItemInfo;
import com.nnmedia.read.luckwheel.NewLuckView;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.StatusBarUtils;
import com.nnmedia.read.utils.UniqueIdManager;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FreeGetVipActivity extends BaseActivity implements View.OnClickListener {

    private NewLuckView luckView;
    private int[] images = new int[]{R.drawable.ic_permanent_vip, R.drawable.ic_three_day_vip, R.drawable.ic_week_vip,
            R.drawable.ic_month_vip, R.drawable.ic_year_vip, R.drawable.ic_gold_large};
    private String[] strs = {"永久VIP", "三天VIP", "一周VIP", "一月VIP", "一年VIP", "999金币"};

    private FrameLayout flTopBar;
    private ImageView ivAsBtnClosePage;

    public static void start(Context context) {
        Intent starter = new Intent(context, FreeGetVipActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setTransparentForImageView(this, flTopBar);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_free_get_vip;
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initView(View view) {
        flTopBar = findViewById(R.id.fl_top_bar);
        ivAsBtnClosePage = findViewById(R.id.iv_asBtn_close_page);
        luckView = findViewById(R.id.luck_view);
        luckView.setIndicatorResourceId(R.drawable.node);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity2.start4Result(this, LoginActivity2.REQCODE_LOGIN);
            return;
        }
        if (!UserInfoCache.isGetFree(this)) {
            GoldExchangeVipActivity.start(this, 0);
            finish();
            return;
        }
//        if (EmulatorDetectUtil.isEmulator(this)) {
//            GoldExchangeVipActivity.start(this, 0);
//            finish();
//            return;
//        }
        ArrayList<LuckItemInfo> items = new ArrayList<>();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        for (int i = 0; i < images.length; i++) {
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
                reqGetVip();
            }
        });

    }

    @Override
    public void setListener() {
        ivAsBtnClosePage.setOnClickListener(this);
    }


    private void reqGetVip() {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.get(Consts.GET_FREE_VIP_API)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(String.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    luckView.setEnable(false);
                    reqUserInfo();
                }, (OnError) error -> TipDialog.show(FreeGetVipActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 获取用户信息---刷新会员时间
     */
    private void reqUserInfo() {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USERS_INFO_API)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(FUser.class)
                .to(RxLife.toMain(this))
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(FreeGetVipActivity.this, fUser);
                    if (UserInfoCache.getIsVip(FreeGetVipActivity.this)) {
                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
                        MessageDialog.build(FreeGetVipActivity.this)
                                .setTitle("获取成功")
                                .setCancelable(false)
                                .setMessage("恭喜获得3天VIP会员，马上去体验吧！")
                                .setOkButton("马上体验")
                                .setCustomView(R.layout.dialog_get_free_vip_success, (dialog, v) -> {
                                }).setOnOkButtonClickListener((baseDialog, v) -> {
                            finish();
                            return false;
                        }).show();
                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
                        EventBus.getDefault().postSticky(fUser);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }
}