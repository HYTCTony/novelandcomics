package com.huli.foxread.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import androidx.appcompat.widget.Toolbar;

public class UserBasicInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQCODE_CHANGE_AVATAR = 0x5236;

    private ImageView ivHeadImg;
    private TextView tvNickname, tvGender, tvAccountId;

    private int gender = 0;    //选择性别

    private ClipboardManager myClipboard;
    private ClipData myClip;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_user_basic_info;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_basic_info);

        ivHeadImg = $(R.id.iv_user_headImg);
        tvNickname = $(R.id.tv_user_nickname);
        tvGender = $(R.id.tv_user_gender);
        tvAccountId = $(R.id.tv_user_account_id);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rtl_asBtn_user_headImg).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_user_nickname).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_user_gender).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_user_account_id).setOnLongClickListener(view -> {
            myClip = ClipData.newPlainText("my_id", tvAccountId.getText().toString());
            myClipboard.setPrimaryClip(myClip);
            Tos.showShort(UserBasicInfoActivity.this, R.string.tips_copy_success);
            return true;
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        FUser userInfo = UserInfoCache.getUserInfo(mContext);
        displayUserInfo(userInfo);
    }

    private void displayUserInfo(FUser userInfo) {
        GlideUtil.loadCircle(this, ivHeadImg, userInfo.getHttp_avatar());
        tvNickname.setText(userInfo.getUsername());
        tvAccountId.setText(userInfo.getId());
        if (userInfo.getGender() == Consts.TYPE_BOY) {
            tvGender.setText(getString(R.string.txt_male));
        } else if (userInfo.getGender() == Consts.TYPE_GIRL) {
            tvGender.setText(getString(R.string.txt_female));
        } else {
            tvGender.setText(null);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtl_asBtn_user_headImg:
                startActivityForResult(new Intent(this, AvatarSelectActivity.class), REQCODE_CHANGE_AVATAR);
                break;
            case R.id.rtl_asBtn_user_nickname:
                InputDialog.show(this, R.string.txt_hint_input_nickname, R.string.hint_nickname_length_limit, R.string.txt_confirm, R.string.txt_cancel)
                        .setInputInfo(new InputInfo()
                                .setMAX_LENGTH(8)     //限制最大输入长度
                                .setMultipleLines(false)       //是否支持多行输入
                        )
                        .setOnOkButtonClickListener((baseDialog, v1, inputStr) -> {
                            String name = inputStr.trim();
                            if (TextUtils.isEmpty(name) || name.length() < 4) {
                                return true;
                            }
                            //提交请求
                            reqSetUserProfile(Consts.USERNAME, name);
                            return false;
                        });
                break;
            case R.id.rtl_asBtn_user_gender:
                MessageDialog.build(this)
                        .setTitle(R.string.txt_plz_select_your_gender)
                        .setMessage("")
                        .setOkButton(R.string.txt_confirm)
                        .setCancelButton(R.string.txt_cancel)
                        .setBackgroundResId(R.drawable.shape_round_whitebg)
                        .setCustomView(R.layout.layout_custom_gender_select2, (dialog, v) -> {
                            //绑定布局事件，可使用v.findViewById(...)来获取子组件
                            RadioGroup rg = v.findViewById(R.id.radioGroup_gender);
                            rg.setOnCheckedChangeListener((radioGroup, i) -> {
                                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_man_gender) {
                                    gender = Consts.TYPE_BOY;
                                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_female_gender) {
                                    gender = Consts.TYPE_GIRL;
                                } else {
                                    gender = 0;
                                }
                            });
                        })
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            if (gender == Consts.TYPE_BOY) {
                                tvGender.setText(getString(R.string.txt_male));
                            } else if (gender == Consts.TYPE_GIRL) {
                                tvGender.setText(getString(R.string.txt_female));
                            } else {
                                return true;
                            }
                            //提交请求
                            reqSetUserProfile(Consts.GENDER, String.valueOf(gender));
                            return false;
                        })
                        .show();

                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQCODE_CHANGE_AVATAR) {
//                String headPicUrl = UserInfoCache.getHeadPic(this);
                String headPicUrl = data.getStringExtra(Common.KEY_HTTP_AVATAR);
                GlideUtil.loadCircle(UserBasicInfoActivity.this, ivHeadImg, headPicUrl);
                setResult(RESULT_OK);

                FUser user = UserInfoCache.saveHeadPic(this, headPicUrl);
                //通知ui刷新
                EventBus.getDefault().postSticky(user);
            }
        }
    }


    private void reqSetUserProfile(String paramKey, String paramValue) {
        OkGo.<String>post(Consts.SET_USER_PROFILE_API)
                .params(paramKey, paramValue)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code == 0) {
                            FUser userInfo;
                            if (paramKey.equals(Consts.USERNAME)) {
                                tvNickname.setText(paramValue);
                                userInfo = UserInfoCache.saveUserName(UserBasicInfoActivity.this, paramValue);
                                setResult(RESULT_OK);
                                Tos.showShort(UserBasicInfoActivity.this, R.string.hint_modify_success);
                                //通知ui刷新
                                EventBus.getDefault().postSticky(userInfo);
                            } else if (paramKey.equals(Consts.GENDER)) {
                                if (gender == Consts.TYPE_BOY) {
                                    tvGender.setText(getString(R.string.txt_male));
                                } else if (gender == Consts.TYPE_GIRL) {
                                    tvGender.setText(getString(R.string.txt_female));
                                } else {
                                    gender = 0;
                                }
                                userInfo = UserInfoCache.saveGender(UserBasicInfoActivity.this, gender);
                                setResult(RESULT_OK);
                                Tos.showShort(UserBasicInfoActivity.this, R.string.hint_modify_success);
                                //通知ui刷新
                                EventBus.getDefault().postSticky(userInfo);
                            }
                        } else {
                            Tos.showShort(UserBasicInfoActivity.this, entity.msg);
                        }
                    }
                });
    }

}
