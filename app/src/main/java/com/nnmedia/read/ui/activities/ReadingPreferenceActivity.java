package com.nnmedia.read.ui.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RadioGroup;

import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.StatusBarUtils;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;

/**
 * 创建时间：2020/6/16  20:14
 * 备注：阅读偏好
 */
public class ReadingPreferenceActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup group;
    private AppCompatRadioButton rbBoy, rbGirl;

    @Override
    protected void setStatusBar() {
//        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.white), 0);
        StatusBarUtils.setTranslucent(this);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
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
        return R.layout.activity_reading_pregerence;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_reading_preference);
        StatusBarUtils.offsetView(this, toolbar);

        group = $(R.id.radioGroup_reading_preference);
        rbBoy = $(R.id.rb_boy_novel);
        rbGirl = $(R.id.rb_girl_novel);
    }

    @Override
    public void setListener() {
        group.setOnCheckedChangeListener(this);
    }


    @Override
    public void doBusiness(Context mContext) {
        int preference = UserInfoCache.getPreference(mContext);
        if (preference == 1) {
            group.check(R.id.rb_boy_novel);
        } else if (preference == 2) {
            group.check(R.id.rb_girl_novel);
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (!rbBoy.isPressed() && !rbGirl.isPressed()) {
            return;
        }
        if (checkedId == R.id.rb_boy_novel) {
            getAnimatorSet(rbBoy);
            reqSetPreference(1);
        } else if (checkedId == R.id.rb_girl_novel) {
            getAnimatorSet(rbGirl);
            reqSetPreference(2);
        }
    }


    /**
     * 组合动画：先缩小后放大
     */
    public void getAnimatorSet(AppCompatRadioButton rb) {
        //组合动画
        AnimatorSet animatorSetsuofang = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rb, "scaleX", 1f, 0.82f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rb, "scaleY", 1f, 0.82f, 1f);
        animatorSetsuofang.setDuration(380);
        animatorSetsuofang.setInterpolator(new DecelerateInterpolator());
        //两个动画同时开始
        animatorSetsuofang.play(scaleX).with(scaleY);
        animatorSetsuofang.start();
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void reqSetPreference(int preference) {
        RxHttp.postForm(Consts.SET_USER_PROFILE_API) //发送登出请求
                .add(Consts.PERFRENCE, preference)
                .asString()
                .subscribe(s -> {
//                    Log.e("ssssss", "设置偏好===" + preference);
                    UserInfoCache.savePreference(ReadingPreferenceActivity.this, preference);
                }, (OnError) error -> {
                    TipDialog.show(this, error.getErrorMsg(), TipDialog.TYPE.ERROR);
                    group.clearCheck();
                });
    }

}
