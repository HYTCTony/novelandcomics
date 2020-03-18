package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.adapters.ReadingRecordsAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.SimpleDividerDecoration;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReadingRecordActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {

    private TextView btnManagerRecords;

    private TextView btnDone, btnSelectAll;
    private LinearLayout layoutBottomBar;
    private TextView btnDelBooks, btnAddBookcase;

    private RecyclerView recyclerView;
    private ReadingRecordsAdapter mAdapter;

    private boolean isManagerMode = false;

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
    }

    @Override
    public void doBusiness(Context mContext) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("ssssssss" + i);
        }
        mAdapter.setNewData(list);

        reqReadingRecord(curPage + 1);
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

                break;

            case R.id.tv_asBtn_add_to_bookcase:

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
        }
    }


    /**
     * 小说阅读记录
     *
     * @param reqPage
     */
    private void reqReadingRecord(int reqPage) {
        OkGo.<String>get(Consts.READ_NOVEL_RECORD_API)
                .params(Consts.PAGE, reqPage)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {

                    }
                });
    }
}
