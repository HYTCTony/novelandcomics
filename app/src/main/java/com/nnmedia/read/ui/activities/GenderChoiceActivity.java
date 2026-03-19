package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.FrApp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.listeners.OnClickEvent;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.StatusBarUtils;
import com.nnmedia.read.utils.Tos;
import com.rxjava.rxlife.RxLife;

import androidx.core.content.ContextCompat;

public class GenderChoiceActivity extends BaseActivity {
    private static final long INTERVAL = 2000;  //按两次返回键退出间隔的时间
    private long mExitFirstTime;  //用于暂存第一次按返回键的时间


    private RadioGroup rgGenderReg;

    private Button btnConfirm;

    private int gender = 1;


    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.color_f2), 0);
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
        return R.layout.activity_gender_choice;
    }

    @Override
    public void initView(View view) {
        rgGenderReg = $(R.id.rg_gender_register);

        btnConfirm = $(R.id.btn_reg_gender_choice_confirm);
    }

    @Override
    public void setListener() {
        rgGenderReg.setOnCheckedChangeListener((radioGroup, i) -> {
            if (radioGroup.getCheckedRadioButtonId() == R.id.rb_gender_male_register) {
                gender = 1;
            } else {
                gender = 2;
            }
        });

        btnConfirm.setOnClickListener(new OnClickEvent(3000) {
            @Override
            public void singleClick(View v) {
                reqInitUserGender(gender);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }


    /**
     * 游客性别选择
     */
    private void reqInitUserGender(int gender) {
        RxHttp.postForm(Consts.USER_SET_GENDER_API)
                .add(Consts.GENDER, String.valueOf(gender))
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(data -> {
                    UserInfoCache.saveGender(GenderChoiceActivity.this, gender);
                    startActivity(new Intent(GenderChoiceActivity.this, MainActivity.class));
                    finish();
                }, (OnError) error ->{
                    TipDialog.show(GenderChoiceActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR);
                });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitFirstTime) > INTERVAL) {
                Tos.showShort(this, R.string.text_point_out_once_again);
                mExitFirstTime = System.currentTimeMillis();
            } else {
                FrApp.getInstance().exitApp();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
