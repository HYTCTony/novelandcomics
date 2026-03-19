package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.InvitedFriendInfo;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.adapters.InvitedFriendsAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.rxjava.rxlife.RxLife;
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
        RxHttp.postForm(Consts.INVITATION_INDEX_API)
                .add(Consts.PAGE, page + 1)
                .asResponsePageList(InvitedFriendInfo.class)
                .doOnSubscribe(disposable -> {
                    if (showDialog) {
                        showLoadingDialog();
                    }
                })
                .doFinally(() -> {
                    dismissLoadingDialog();
                    mRefreshLayout.finishRefresh();
                })
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    curPage = entity.getCurrent_page();
                    List<InvitedFriendInfo> friendInfos = entity.getData();
                    if (curPage == 1) {
                        mAdapter.setList(friendInfos);
                    } else {
                        mAdapter.addData(friendInfos);
                    }
                    if (entity.getLast_page() <= curPage) {    //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }
}
