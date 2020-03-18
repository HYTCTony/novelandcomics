package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.InvitedFriendInfo;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.adapters.InvitedFriendsAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyInviteFriendsActivity extends BaseActivity {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private InvitedFriendsAdapter mAdapter;

    private int curPage = 0;        //当前页码，下一页 +1

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_refresh_recy_normal;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_has_been_invited_friends);

        mRefreshLayout = $(R.id.smartRefreshLayout_ac_normal);
        recyclerView = $(R.id.recyclerView_ac_normal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new InvitedFriendsAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            curPage = 0;
            reqMyFriendsList(curPage, false);

            //可以上拉加载
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqMyFriendsList(curPage, false));
    }

    @Override
    public void doBusiness(Context mContext) {
        reqMyFriendsList(curPage, true);
    }

    /**
     * 已邀好友列表
     */
    private void reqMyFriendsList(int page, boolean showDialog) {
        OkGo.<String>post(Consts.INVITATION_INDEX_API)
                .params(Consts.PAGE, page + 1)
                .execute(new LtbCallback(this, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<InvitedFriendInfo>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<InvitedFriendInfo>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<InvitedFriendInfo>> datas = entity.getData();
                            curPage = datas.getCurrent_page();
                            List<InvitedFriendInfo> friendInfos = datas.getData();
                            if (curPage == 1) {
                                mAdapter.setNewData(friendInfos);
                            } else {
                                mAdapter.addData(friendInfos);
                            }
                            if (datas.getLast_page() <= curPage) {    //没有下一页
                                mAdapter.getLoadMoreModule().loadMoreEnd();
                            } else {
                                mAdapter.getLoadMoreModule().loadMoreComplete();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mAdapter.getLoadMoreModule().loadMoreFail();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }
                });
    }
}
