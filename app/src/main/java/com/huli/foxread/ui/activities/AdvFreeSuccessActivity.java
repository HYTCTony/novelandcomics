package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ebsevent.WelfareChangeEvent;
import com.huli.foxread.entity.FreeAdvRespone;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.services.CountService;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.page.model.event.AdMessage;
import com.huli.page.model.local.ReadSettingManager;
import com.kongzue.dialog.v3.TipDialog;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

/**
 * 激励视频免广告---成功
 */
public class AdvFreeSuccessActivity extends BaseActivity {

    private TextView tvAdvFreeSuccess;
    private TextView tvRewardTips;
    private TextView tvResidueDegree;
    private Button btnComplete;

    public static void start(Context context) {
        Intent starter = new Intent(context, AdvFreeSuccessActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_adv_free_success;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_enjoy_immediately_ad_free_reading);

        tvAdvFreeSuccess = $(R.id.tv_adv_free_success);
        tvRewardTips = $(R.id.tv_reward_tips);
        tvResidueDegree = $(R.id.tv_residue_degree_today);
        btnComplete = $(R.id.btn_complete);

        tvAdvFreeSuccess.setVisibility(View.GONE);
        tvRewardTips.setVisibility(View.GONE);
        tvResidueDegree.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);
    }

    @Override
    public void setListener() {
        btnComplete.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void doBusiness(Context mContext) {
        reqAdvMissionComplete();
    }


    /**
     * 完成任务领取奖励
     */
    private void reqAdvMissionComplete() {
        RxHttp.get(Consts.WELFARE_COMPLETE_ADVERT_API)
                .asResponse(FreeAdvRespone.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(data -> {
                    tvRewardTips.setText(String.format(getString(R.string.txt_adblock_plus_tips_x), data.getSpace()));
                    String residueDegreeStr = String.format(getString(R.string.txt_residue_degree_today_x), data.getSite());
                    tvResidueDegree.setText(setNumColor(AdvFreeSuccessActivity.this, residueDegreeStr));
                    ReadSettingManager.getInstance().setAdvertTime(data.getAdvert_time());

                    tvAdvFreeSuccess.setVisibility(View.VISIBLE);
                    tvRewardTips.setVisibility(View.VISIBLE);
                    tvResidueDegree.setVisibility(View.VISIBLE);
                    btnComplete.setVisibility(View.VISIBLE);

                    /*通知刷新福利列表*/
                    EventBus.getDefault().post(new WelfareChangeEvent(true));
                    //启动免广告倒计时服务
                    Intent intent = new Intent(AdvFreeSuccessActivity.this, CountService.class);
                    intent.putExtra(CountService.EXTRA_COUNT_MIN, data.getInterval());
                    startService(intent);
                    EventBus.getDefault().post(new AdMessage());
                }, (OnError) error -> TipDialog.show(AdvFreeSuccessActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR).setOnDismissListener(this::finish));
    }


    private static SpannableStringBuilder setNumColor(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            if (a >= '0' && a <= '9') {
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.txt_orange_ff6600)), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }
}
