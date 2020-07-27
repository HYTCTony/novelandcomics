package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.AvatarGroup;
import com.huli.foxread.entity.SysAvatarEntity;
import com.huli.foxread.entity.sections.AvatarSection;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.adapters.SectionAvatarAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.GlideUtil;
import com.kongzue.dialog.v3.TipDialog;
import com.rxjava.rxlife.RxLife;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AvatarSelectActivity extends BaseActivity implements SectionAvatarAdapter.OnRecyAvatarCheckListener {

    private RecyclerView recyclerView;
    private SectionAvatarAdapter mAdapter;

    private View flBottomBar;
    private Button btnConfirm;
    private ImageView ivCurAvatar;
    private String headPicUrl;      //真正的头像地址

    private String paramValue;      //上传的参数值

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_avatar_select;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_change_avatar);

        recyclerView = $(R.id.recyclerView_system_avatar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        headPicUrl = UserInfoCache.getHeadPic(this);
        mAdapter = new SectionAvatarAdapter(headPicUrl, this);
        recyclerView.setAdapter(mAdapter);

        flBottomBar = $(R.id.fl_bottom_bar_avatar_select);
        btnConfirm = $(R.id.btn_confirm);
        ivCurAvatar = $(R.id.tv_user_cur_avatar);
        GlideUtil.loadCircle(this, ivCurAvatar, headPicUrl);
    }

    @Override
    public void setListener() {
        btnConfirm.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                reqSetAvatar(paramValue);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        reqSystemAvatar();
    }

    @Override
    public void OnAvatarCheck(int pos, String avatarUrl, String paramValue) {
        headPicUrl = avatarUrl;
        this.paramValue = paramValue;
        flBottomBar.setVisibility(View.VISIBLE);

        GlideUtil.loadCircle(this, ivCurAvatar, headPicUrl);
    }


    /**
     * 获取系统头像列表
     */
    private void reqSystemAvatar() {
        RxHttp.postForm(Consts.AVATAR_LIST_API)
                .asResponseList(AvatarGroup.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(datas->{
                    List<AvatarSection<SysAvatarEntity>> list = new ArrayList<>();
                    for (int i = 0; i < datas.size(); i++) {
                        AvatarGroup avatarGroup = datas.get(i);
                        List<SysAvatarEntity> avatars = avatarGroup.getProfileAvatar();
                        list.add(new AvatarSection<>(true, avatarGroup.getName(), null));
                        for (int j = 0; j < avatars.size(); j++) {
                            list.add(new AvatarSection<>(false, "", avatars.get(j)));
                        }
                    }
                    mAdapter.setList(list);
                });
    }


    private void reqSetAvatar(String paramValue) {
        if (TextUtils.isEmpty(paramValue)) {
            return;
        }
        RxHttp.postForm(Consts.SET_USER_PROFILE_API)
                .add(Consts.AVATAR, paramValue)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
//                            UserInfoCache.saveHeadPic(AvatarSelectActivity.this, headPicUrl);
                    Intent intent = getIntent();
                    intent.putExtra(Common.KEY_HTTP_AVATAR, headPicUrl);
                    TipDialog.show(AvatarSelectActivity.this, R.string.txt_setup_success, TipDialog.TYPE.SUCCESS)
                            .setOnDismissListener(() -> {
                                setResult(RESULT_OK, intent);
                                finish();
                            });
                }, (OnError) error -> TipDialog.show(AvatarSelectActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

}
