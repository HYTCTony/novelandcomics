package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.SMsgBean;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.adapters.MsgNotifyAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.MessageDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import androidx.annotation.NonNull;
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
        reqMsgNotifyDatas(curPage, true);
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SMsgBean sMsgBean = mAdapter.getData().get(position);
        MessageDialog.show(this, sMsgBean.getProfileMessageIssue().getName(), sMsgBean.getProfileMessageIssue().getContent());
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        curPage = 0;
        reqMsgNotifyDatas(curPage, false);

        //可以上拉加载
        mAdapter.getLoadMoreModule().setEnableLoadMore(true);
    }

    @Override
    public void onLoadMore() {
        reqMsgNotifyDatas(curPage, false);
    }


    private void reqMsgNotifyDatas(int page, boolean showDialog) {
        OkGo.<String>post(Consts.MSG_LIST_API)
                .params(Consts.PAGE, page + 1)
                .execute(new LtbCallback(this, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<SMsgBean>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<SMsgBean>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<SMsgBean>> datas = entity.getData();
                            curPage = datas.getCurrent_page();
                            List<SMsgBean> bookList = datas.getData();
                            if (curPage == 1) {
                                mAdapter.setNewData(bookList);
                            } else {
                                mAdapter.addData(bookList);
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
