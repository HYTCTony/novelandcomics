package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.kongzue.dialog.v3.MessageDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.callbacks.DiffMsgCallback;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.SMsgBean;
import com.nnmedia.read.ui.adapters.MsgNotifyAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.SPFUtils;
import com.nnmedia.read.utils.Tos;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.Method;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MsgNotifyActivity extends BaseActivity implements View.OnClickListener, OnLoadMoreListener, OnRefreshListener, OnItemClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private MsgNotifyAdapter mAdapter;

    private ImageView btnClose;
    private Button btnOpen;

    private int curPage = 0;

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
        initToolBar(toolbar, R.string.txt_msg_notify);

        mRefreshLayout = $(R.id.smartRefreshLayout_ac_normal);
        recyclerView = $(R.id.recyclerView_ac_normal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MsgNotifyAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty_msg);
        mAdapter.setDiffCallback(new DiffMsgCallback());

        View headView = LayoutInflater.from(this).inflate(R.layout.layout_rv_head_msg_notify, recyclerView, false);

        boolean isTuisong = (boolean) SPFUtils.get(this, "key_tuisong", false);
        if (!isTuisong) {
            mAdapter.addHeaderView(headView);
        }

        btnClose = headView.findViewById(R.id.iv_asBtn_close);
        btnOpen = headView.findViewById(R.id.btn_immediately_open);
    }

    @Override
    public void setListener() {
        btnClose.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
//        reqMsgNotifyDatas(curPage, true);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_msg, menu);
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_all_read) {
            if (!onMoreClick()) {
                reqSetMsgAllRead();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_asBtn_close:
                mAdapter.removeAllHeaderView();
                break;
            case R.id.btn_immediately_open:
                mAdapter.removeAllHeaderView();
                SPFUtils.put(this, "key_tuisong", true);
                Tos.showShort(this, "推送已开启");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        List<SMsgBean> msgs = mAdapter.getData();
        SMsgBean sMsgBean = msgs.get(position);
        MessageDialog.show(this, sMsgBean.getProfileMessageIssue().getName(), sMsgBean.getProfileMessageIssue().getContent());
        if (sMsgBean.getStatus() == 2) {
            reqMarkMsgRead(sMsgBean.getId());
        }

        //改变msg已读状态
        msgs.get(position).setStatus(1);
        mAdapter.setDiffNewData(msgs);
        mAdapter.notifyItemChanged(position + mAdapter.getHeaderLayoutCount(), "read_status");
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        curPage = 0;
        reqMsgNotifyDatas(curPage);

        //可以上拉加载
        mAdapter.getLoadMoreModule().setEnableLoadMore(true);
    }

    @Override
    public void onLoadMore() {
        reqMsgNotifyDatas(curPage);
    }


    private void reqMsgNotifyDatas(int page) {
        RxHttp.get(Consts.MSG_LIST_API) //发送登出请求
                .add(Consts.PAGE, page + 1)
                .asResponsePageList(SMsgBean.class)
                .doFinally(() -> {
                    mRefreshLayout.finishRefresh();
                })
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(result -> {
                    curPage = result.getCurrent_page();
                    List<SMsgBean> bookList = result.getData();
                    if (curPage == 1) {
                        mAdapter.setList(bookList);
                    } else {
                        mAdapter.addData(bookList);
                    }
                    if (result.getLast_page() <= curPage) {    //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                });
    }

    /**
     * 标记为已读
     */
    private void reqMarkMsgRead(String msgId) {
        RxHttp.postForm(Consts.MSG_MARKED_READ_API)
                .add("id", msgId)
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
                });
    }

    /**
     * 全部标记为已读
     */
    private void reqSetMsgAllRead() {
        RxHttp.postForm(Consts.MSG_SET_ALL_READ_API)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
                    curPage = 0;
                    reqMsgNotifyDatas(curPage);
                });
    }

}