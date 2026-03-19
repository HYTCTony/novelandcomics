package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.ui.activity.MoreSettingActivity;
import com.nnmedia.page.utils.DataCleanManager;
import com.nnmedia.read.cache.TokenCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.LoginRpsEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.UniqueIdManager;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    public static final int REQCODE_USER_ATTR = 0x9999;

    private TextView tvNickname, tvPushNotifyState, tvCacheSize;
    private TextView btnAccountSecurity, btnAboutUs;

    private RelativeLayout btnUserBasicInfo;

    private SwitchCompat switchNightMode;
    private Button btnLogout;

    private TextView tvPolicy;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_setting);

        tvNickname = $(R.id.tv_user_nickname);
        tvPushNotifyState = $(R.id.tv_push_notification_state);
        tvCacheSize = $(R.id.tv_cache_size);
        btnAboutUs = $(R.id.tv_asBtn_about_us);
        switchNightMode = $(R.id.switchCompat_night_mode);

        btnUserBasicInfo = $(R.id.rtl_asBtn_user_basic_info);
        btnAccountSecurity = $(R.id.tv_asBtn_account_security);
        btnLogout = $(R.id.btn_logout_account);

        tvPolicy = $(R.id.tv_fox_policy);
    }

    @Override
    public void setListener() {
        btnUserBasicInfo.setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_push_notification).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_clear_cache).setOnClickListener(this);
        findViewById(R.id.tv_asBtn_reader_settings).setOnClickListener(this);
        btnAccountSecurity.setOnClickListener(this);
        btnAboutUs.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        FUser userInfo = UserInfoCache.getUserInfo(mContext);
        //游客状态不可见
        if (userInfo.isIs_tourist()) {
            btnAccountSecurity.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            btnUserBasicInfo.setVisibility(View.GONE);
        } else {
            btnAccountSecurity.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            btnUserBasicInfo.setVisibility(View.VISIBLE);
        }

        tvNickname.setText(userInfo.getUsername());
        tvPushNotifyState.setText("已开启");
        String cache = "0.00k";
        try {
            cache = DataCleanManager.getTotalCacheSize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCacheSize.setText(cache);
        switchNightMode.setChecked(true);


        SpannableString spannableString = new SpannableString(getString(R.string.txt_foxread_user_and_privacy_policy));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(AboutUsActivity.this, "用户协议");
//                Intent intent = new Intent(SettingActivity.this, CommonWebActivity.class);
//                intent.putExtra(Common.KEY_URL, Consts.USER_AGREEMENT_URL);
//                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SettingActivity.this, R.color.txt_red));
                ds.setUnderlineText(false);
            }
        }, 4, 14, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(AboutUsActivity.this, "用户隐私");
//                Intent intent = new Intent(SettingActivity.this, CommonWebActivity.class);
//                intent.putExtra(Common.KEY_URL, Consts.PRIVACY_POLICY_URL);
//                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SettingActivity.this, R.color.txt_red));
                ds.setUnderlineText(false);
            }
        }, spannableString.length() - 6, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvPolicy.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        tvPolicy.setHighlightColor(ContextCompat.getColor(this, R.color.transparent));
        tvPolicy.setText(spannableString);
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.rtl_asBtn_user_basic_info:
                startActivityForResult(new Intent(this, UserBasicInfoActivity.class), REQCODE_USER_ATTR);
                break;
            case R.id.rtl_asBtn_push_notification:
                MessageDialog.show(this, "", getString(R.string.hint_content_push_message), getString(R.string.txt_got_it))
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            baseDialog.doDismiss();
                            return false;
                        });
                break;
            case R.id.rtl_asBtn_clear_cache:
                MessageDialog.show(this, R.string.txt_clear_cache, R.string.hint_content_clear_cache, R.string.txt_confirm, R.string.txt_cancel)
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            DataCleanManager.clearAllCache(SettingActivity.this);
                            tvCacheSize.setText("0.00k");
                            baseDialog.doDismiss();
                            return false;
                        });
                break;
            case R.id.tv_asBtn_reader_settings:
                startActivity(new Intent(SettingActivity.this, MoreSettingActivity.class));
                break;
            case R.id.tv_asBtn_account_security:
                startActivity(new Intent(this, AccountSecurityActivity.class));
                break;
            case R.id.tv_asBtn_about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.btn_logout_account:
                logout();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQCODE_USER_ATTR) {
                tvNickname.setText(UserInfoCache.getUserName(this));
            }
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        RxHttp.postForm(Consts.USER_LOGOUT_API) //发送登出请求
                .asResponse(LoginRpsEntity.class)
                .flatMap(loginRpsEntity -> {
                    UserInfoCache.clearCache(SettingActivity.this);
                    TokenCache.saveToken(SettingActivity.this, loginRpsEntity.getToken());
                    String uniqueID = UniqueIdManager.getUniqueID(this);
                    //登出成功，得到新的Token去获取游客身份信息，并返回User对象
                    return RxHttp.postForm(Consts.USERS_INFO_API) //发送登录请求
                            .add(Consts.UNIQUE_ID, uniqueID)
                            .subscribeOnCurrent() //当前线程发送登录请求
                            .asResponse(FUser.class);
                })
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(SettingActivity.this, fUser);
                    EventBus.getDefault().postSticky(fUser);
                    finish();
                }, (OnError) error -> TipDialog.show(this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }
}
