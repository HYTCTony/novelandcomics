package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.MainMineFragment;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.MessageDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvNickname, tvPushNotifyState, tvCacheSize;
    private TextView btnAccountSecurity, btnAboutUs;

    private RelativeLayout btnUserBasicInfo;

    private SwitchCompat switchNightMode;
    private Button btnLogout;

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
        //游客状态不可见
        if (UserInfoCache.getIsVisitor(this)) {
            btnAccountSecurity.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            btnUserBasicInfo.setVisibility(View.GONE);
        } else {
            btnAccountSecurity.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            btnUserBasicInfo.setVisibility(View.VISIBLE);
        }
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
        tvNickname.setText(UserInfoCache.getUserName(this));
        tvPushNotifyState.setText("未开启");
        tvCacheSize.setText("256.89M");

        switchNightMode.setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtl_asBtn_user_basic_info:
                startActivityForResult(new Intent(this, UserBasicInfoActivity.class), MainMineFragment.REQCODE_USER_ATTR);
                break;
            case R.id.rtl_asBtn_push_notification:

                break;
            case R.id.rtl_asBtn_clear_cache:
                MessageDialog.show(this, R.string.txt_clear_cache, R.string.hint_content_clear_cache, R.string.txt_confirm, R.string.txt_cancel)
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            Tos.showShort(SettingActivity.this, "确定");
                            baseDialog.doDismiss();
                            return false;
                        });
                break;
            case R.id.tv_asBtn_reader_settings:

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
            if (requestCode == MainMineFragment.REQCODE_USER_ATTR) {
                tvNickname.setText(UserInfoCache.getUserName(this));
                setResult(MainMineFragment.REQCODE_USER_ATTR);
            }
        }
    }

    /**
     * 正式用户登出
     */
    private void logout() {
        OkGo.<String>get(Consts.USER_LOGOUT_API)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            UserInfoCache.clearCache(SettingActivity.this);

                            JSONObject object = JSONObject.parseObject(entity.getData());
                            String token = object.getString("token");
                            UserInfoCache.saveToken(SettingActivity.this, token);

                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Tos.showShort(SettingActivity.this, entity.msg);
                        }
                    }
                });
    }

}
