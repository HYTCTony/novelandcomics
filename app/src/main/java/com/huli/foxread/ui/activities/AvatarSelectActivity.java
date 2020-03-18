package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.AvatarGroup;
import com.huli.foxread.entity.SysAvatarEntity;
import com.huli.foxread.entity.sections.AvatarSection;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.adapters.SectionAvatarAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.GlideUtil;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

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
        headPicUrl = "http://novel.hongyutiancheng.com.cn/uploads/20200316/49ddb9e07d37460b60799b3167742edd.jpg";
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
    }


    /**
     * 获取系统头像列表
     */
    private void reqSystemAvatar() {
        OkGo.<String>post(Consts.AVATAR_LIST_API)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<AvatarGroup>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<AvatarGroup>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<AvatarSection<SysAvatarEntity>> list = new ArrayList<>();
                            List<AvatarGroup> datas = entity.getData();
                            for (int i = 0; i < datas.size(); i++) {
                                AvatarGroup avatarGroup = datas.get(i);
                                List<SysAvatarEntity> avatars = avatarGroup.getAvatar();
                                list.add(new AvatarSection<>(true, avatarGroup.getName(), null));
                                for (int j = 0; j < avatars.size(); j++) {
                                    list.add(new AvatarSection<>(false, "", avatars.get(j)));
                                }
                            }
                            mAdapter.setNewData(list);
                        }
                    }
                });
    }


    private void reqSetAvatar(String paramValue) {
        if (TextUtils.isEmpty(paramValue)) {
            return;
        }
        OkGo.<String>post(Consts.SET_USER_PROFILE_API)
                .params(Consts.AVATAR, paramValue)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code == 0) {
                            UserInfoCache.saveHeadPic(AvatarSelectActivity.this, headPicUrl);
                            TipDialog.show(AvatarSelectActivity.this, entity.msg, TipDialog.TYPE.SUCCESS)
                                    .setOnDismissListener(() -> {
                                        setResult(RESULT_OK);
                                        finish();
                                    });
                        } else {
                            TipDialog.show(AvatarSelectActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

}
