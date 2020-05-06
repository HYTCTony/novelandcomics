package com.huli.foxread.ui.activities;

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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.Tos;
import com.huli.page.ui.activity.MoreSettingActivity;
import com.huli.page.utils.DataCleanManager;
import com.kongzue.dialog.v3.MessageDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

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
                Intent intent = new Intent(SettingActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.USER_AGREEMENT_URL);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SettingActivity.this, R.color.txt_red));
                ds.setUnderlineText(false);
            }
        }, 8, 14, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(AboutUsActivity.this, "用户隐私");
                Intent intent = new Intent(SettingActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.PRIVACY_POLICY_URL);
                startActivity(intent);
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
     * 正式用户登出
     */
    private void logout() {
        OkGo.<String>post(Consts.USER_LOGOUT_API)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<LoginRpsEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<LoginRpsEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            UserInfoCache.clearCache(SettingActivity.this);
                            TokenCache.saveToken(SettingActivity.this, entity.getData().getToken());

                            EventBus.getDefault().postSticky(UserInfoCache.getUserInfo(SettingActivity.this));

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Tos.showShort(SettingActivity.this, entity.msg);
                        }
                    }
                });
    }

}
