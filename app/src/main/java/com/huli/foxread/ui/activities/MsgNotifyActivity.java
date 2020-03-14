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
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.SMsgBean;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.adapters.MsgNotifyAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MsgNotifyActivity extends BaseActivity implements View.OnClickListener, OnItemChildClickListener {

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

        View headView = LayoutInflater.from(this).inflate(R.layout.layout_rv_head_msg_notify, recyclerView, false);
        mAdapter.addHeaderView(headView);

        btnClose = headView.findViewById(R.id.iv_asBtn_close);
        btnOpen = headView.findViewById(R.id.btn_immediately_open);
    }

    @Override
    public void setListener() {
        btnClose.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        reqMsgNotifyDatas(curPage + 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_asBtn_close:
                mAdapter.removeAllHeaderView();
                break;
            case R.id.btn_immediately_open:
                Tos.showShort(this, "开启");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }


    private void reqMsgNotifyDatas(int reqPage) {
        OkGo.<String>post(Consts.MSG_LIST_API)
                .params(Consts.PAGE, reqPage)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<SMsgBean>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<SMsgBean>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<SMsgBean>> datas = entity.getData();
                            curPage = datas.getCurrent_page();
                            int lastPage = datas.getLast_page();
                            List<SMsgBean> bookList = datas.getData();
                            if (curPage == 1) {
                                mAdapter.setNewData(bookList);
                            } else {
                                mAdapter.addData(bookList);
                            }

                            if (lastPage == 0) {
                                //TODO 没有下一页
                            }
                        }
                    }
                });
    }
}
