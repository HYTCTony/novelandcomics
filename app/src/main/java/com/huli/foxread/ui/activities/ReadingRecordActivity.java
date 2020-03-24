package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.ReadRecordEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.adapters.ReadingRecordsAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.SimpleDividerDecoration;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
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

public class ReadingRecordActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {

    private TextView btnManagerRecords;

    private TextView btnDone, btnSelectAll;
    private LinearLayout layoutBottomBar;
    private TextView btnDelBooks, btnAddBookcase;

    private SmartRefreshLayout layout;
    private RecyclerView recyclerView;
    private ReadingRecordsAdapter mAdapter;

    private boolean isManagerMode = false;

    private int curPage = 1;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_reading_records;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_reading_records);

        btnManagerRecords = $(R.id.tv_asBtn_manager_records);

        btnDone = $(R.id.tv_asBtn_done);
        btnSelectAll = $(R.id.tv_asBtn_select_all_record);
        layoutBottomBar = $(R.id.ll_bottom_bar_rc);
        btnDelBooks = $(R.id.tv_asBtn_del_books);
        btnAddBookcase = $(R.id.tv_asBtn_add_to_bookcase);

        layout = $(R.id.smart);
        recyclerView = $(R.id.recyclerView_reading_records);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerDecoration(this, R.dimen.dp_1, R.dimen.dp_16, R.color.col_gray_e5e5e5));
        mAdapter = new ReadingRecordsAdapter(isManagerMode);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        btnDone.setOnClickListener(this);
        btnSelectAll.setOnClickListener(this);
        btnManagerRecords.setOnClickListener(this);
        btnDelBooks.setOnClickListener(this);
        btnAddBookcase.setOnClickListener(this);

        mAdapter.setOnItemClickListener(this);
        layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //可以上拉加载
                curPage = 1;
                reqReadingRecord(curPage);
                mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            }
        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                reqReadingRecord(curPage + 1);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        reqReadingRecord(curPage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_done:
                btnDone.setVisibility(View.GONE);
                btnSelectAll.setVisibility(View.GONE);
                layoutBottomBar.setVisibility(View.GONE);
                btnManagerRecords.setVisibility(View.VISIBLE);
                isManagerMode = false;
                mAdapter.setManagerMode(isManagerMode);
                break;
            case R.id.tv_asBtn_select_all_record:
                int count = mAdapter.funCheckAll();
                btnDelBooks.setText(String.format(getString(R.string.txt_del_books_x), count));
                btnAddBookcase.setText(String.format(getString(R.string.txt_add_to_bookcase_x), count));
                break;
            case R.id.tv_asBtn_manager_records:
                btnDone.setVisibility(View.VISIBLE);
                btnSelectAll.setVisibility(View.VISIBLE);
                layoutBottomBar.setVisibility(View.VISIBLE);
                btnManagerRecords.setVisibility(View.GONE);
                btnDelBooks.setText(String.format(getString(R.string.txt_del_books_x), 0));
                btnAddBookcase.setText(String.format(getString(R.string.txt_add_to_bookcase_x), 0));
                isManagerMode = true;
                mAdapter.setManagerMode(isManagerMode);
                break;
            case R.id.tv_asBtn_del_books:
                if (mAdapter.getSelectedCount() <= 0) {
                    Toast.makeText(this, "请选择书籍记录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, mAdapter.getSelectedIds());
                reqDeleteBookRecord(mAdapter.getSelectedIds());
                break;
            case R.id.tv_asBtn_add_to_bookcase:
                if (mAdapter.getSelectedCount() <= 0) {
                    Toast.makeText(this, "请选择书籍记录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mAdapter.getSelectedCount() > 15) {
                    MessageDialog.show(ReadingRecordActivity.this, R.string.nb_common_tip, R.string.hint_content_exceed_limit_message, R.string.txt_got_it)
                            .setCancelable(false)
                            .setOnOkButtonClickListener((baseDialog, v) -> {
                                baseDialog.doDismiss();
                                return false;
                            });
                    return;
                }
                Log.d(TAG, mAdapter.getSelectedIds());
                reqAddBookrack(mAdapter.getSelectedBookId());
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (isManagerMode) {
            int count = mAdapter.funCheck(position);
            btnDelBooks.setText(String.format(getString(R.string.txt_del_books_x), count));
            btnAddBookcase.setText(String.format(getString(R.string.txt_add_to_bookcase_x), count));
        } else {
            ReadRecordEntity data = (ReadRecordEntity) adapter.getItem(position);
            Intent intent = new Intent(ReadingRecordActivity.this, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, data.getNovel_id());
            startActivity(intent);
        }
    }

    /**
     * 小说阅读记录
     *
     * @param reqPage
     */
    private void reqReadingRecord(int reqPage) {
        OkGo.<String>get(Consts.RECORD_READ_API)
                .params(Consts.PAGE, reqPage)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<ReadRecordEntity>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<ReadRecordEntity>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<ReadRecordEntity>> datas = entity.getData();
                            List<ReadRecordEntity> record = datas.getData();
                            curPage = datas.getCurrent_page();
                            if (curPage == 1) {
                                mAdapter.setNewData(record);
                                layout.finishRefresh();
                            } else {
                                if (record != null && record.size() > 1) {
                                    mAdapter.addData(record);
                                }
                            }
                            if (datas.getLast_page() <= curPage) {
                                mAdapter.getLoadMoreModule().loadMoreEnd();
                            } else {
                                mAdapter.getLoadMoreModule().loadMoreComplete();
                            }
                        } else {
                            TipDialog.show(ReadingRecordActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mAdapter.getLoadMoreModule().loadMoreFail();
                    }
                });
    }

    /**
     * 加入书架
     *
     * @param novelId 小说ID
     */
    private void reqAddBookrack(String novelId) {
        OkGo.<String>post(Consts.BOOKRACK_ADD_API)
                .params(Consts.NOVEL_IDS, novelId)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            TipDialog.show(ReadingRecordActivity.this, entity.msg, TipDialog.TYPE.SUCCESS);
                        } else {
                            TipDialog.show(ReadingRecordActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

    /**
     * 批量删除阅读记录
     *
     * @param novelId 小说ID
     */
    private void reqDeleteBookRecord(String novelId) {
        OkGo.<String>get(Consts.RECORD_DELETE_API)
                .params(Consts.NOVEL_ID, novelId)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            curPage = 1;
                            reqReadingRecord(curPage);
                            btnDone.setVisibility(View.GONE);
                            btnSelectAll.setVisibility(View.GONE);
                            layoutBottomBar.setVisibility(View.GONE);
                            btnManagerRecords.setVisibility(View.VISIBLE);
                            isManagerMode = false;
                            mAdapter.setManagerMode(isManagerMode);
                            TipDialog.show(ReadingRecordActivity.this, entity.msg, TipDialog.TYPE.SUCCESS);
                        } else {
                            TipDialog.show(ReadingRecordActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }
}