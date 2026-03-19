package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.kongzue.stacklabelview.StackLabel;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.entity.CategoryEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.adapters.BooksListAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.GlideUtil;
import com.rxjava.rxlife.RxLife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rxhttp.wrapper.cahce.CacheMode;

public class ClassifyDetailActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener, OnLoadMoreListener {

    private RecyclerView recyclerView;
    private BooksListAdapter mAdapter;

    private StackLabel stackLabel_1, stackLabel_2, stackLabel_3, stackLabel_4;
    private TextView btnMoreFilter;
    private View layoutMoreFilter;

    private View headViewTop3;
    private TextView tvClassifyTop3Title;
    private ImageView ivCoverFirst, ivCoverSecond, ivCoverThird;
    private TextView tvBookNameFirst, tvBookNameSecond, tvBookNameThird;

    private List<String> stack1Datas = new ArrayList<>();
    private List<CategoryEntity> datas = new ArrayList<>();

    private int pCatId, subCatID;
    private String mTitle;

    private int isParent = 1;       // 1为父分类(查看全部的意思)，0不是父分类
    private int paramIsEnd = 0;
    private int paramWordsNum = 0;
    private int paramStatus = 1;
    private int paramCurPage = 0;

    @Override
    public void initParms(Bundle parms) {
        pCatId = parms.getInt(Common.KEY_CAT_ID);
        mTitle = parms.getString(Common.KEY_CAT_TITLE);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_classify_detail;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, mTitle);

        recyclerView = $(R.id.recyclerView_classify_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BooksListAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(this);
        View headViewFilter = inflater.inflate(R.layout.layout_rv_head_classify_detail_filter, recyclerView, false);
        headViewTop3 = inflater.inflate(R.layout.layout_rv_head_classify_detail_top3, recyclerView, false);
        mAdapter.addHeaderView(headViewFilter, 0);

        stackLabel_1 = headViewFilter.findViewById(R.id.stackLabelView_filter_1);
        stackLabel_2 = headViewFilter.findViewById(R.id.stackLabelView_filter_2);
        stackLabel_3 = headViewFilter.findViewById(R.id.stackLabelView_filter_3);
        stackLabel_4 = headViewFilter.findViewById(R.id.stackLabelView_filter_4);
        btnMoreFilter = headViewFilter.findViewById(R.id.tv_asBtn_more_filter);
        layoutMoreFilter = headViewFilter.findViewById(R.id.ll_more_filter_layout);

        tvClassifyTop3Title = headViewTop3.findViewById(R.id.tv_classify_top3_title);
        ivCoverFirst = headViewTop3.findViewById(R.id.iv_top3_bookCover_first);
        ivCoverSecond = headViewTop3.findViewById(R.id.iv_top3_bookCover_second);
        ivCoverThird = headViewTop3.findViewById(R.id.iv_top3_bookCover_third);
        tvBookNameFirst = headViewTop3.findViewById(R.id.tv_top3_bookName_first);
        tvBookNameSecond = headViewTop3.findViewById(R.id.tv_top3_bookName_second);
        tvBookNameThird = headViewTop3.findViewById(R.id.tv_top3_bookName_third);

        stack1Datas.add(getString(R.string.txt_all));
        List<String> stack2Datas = Arrays.asList(getResources().getStringArray(R.array.cat_is_end));
        List<String> stack3Datas = Arrays.asList(getResources().getStringArray(R.array.cat_word_num));
        List<String> stack4Datas = Arrays.asList(getResources().getStringArray(R.array.cat_state));
        stackLabel_1.setLabels(stack1Datas);
        stackLabel_2.setLabels(stack2Datas);
        stackLabel_3.setLabels(stack3Datas);
        stackLabel_4.setLabels(stack4Datas);
        stackLabel_1.setSelectMode(true, stack1Datas);
        stackLabel_2.setSelectMode(true, stack2Datas.subList(0, 1));
        stackLabel_3.setSelectMode(true, stack3Datas.subList(0, 1));
        stackLabel_4.setSelectMode(true, stack4Datas.subList(0, 1));
        filter4Str = stack4Datas.get(0);
        tvClassifyTop3Title.setText(String.format(getString(R.string.txt_category_dt_sub_title), mTitle, filter4Str));

    }

    private String filter4Str = "";

    @Override
    public void setListener() {
        btnMoreFilter.setOnClickListener(view -> {
            btnMoreFilter.setVisibility(View.GONE);
            layoutMoreFilter.setVisibility(View.VISIBLE);
        });

        stackLabel_1.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "1***选中===" + s + "----" + index);
            if (index > 0) {
                if (datas != null && datas.size() >= index) {
                    tvClassifyTop3Title.setText(String.format(getString(R.string.txt_category_dt_sub_title), s, filter4Str));

                    isParent = 0;
                    subCatID = datas.get(index - 1).getId();
                }
            } else {
                tvClassifyTop3Title.setText(String.format(getString(R.string.txt_category_dt_sub_title), mTitle, filter4Str));

                subCatID = pCatId;
                isParent = 1;
            }
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            paramCurPage = 0;
            reqCategoryDatas(false);
        });

        stackLabel_2.setOnLabelClickListener((index, v, s) -> {
//            Log.e(TAG, "2***选中===" + s + "----" + index);
            paramIsEnd = index;
            paramCurPage = 0;
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            reqCategoryDatas(false);
        });
        stackLabel_3.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "3***选中===" + s + "----" + index);
            paramWordsNum = index;
            paramCurPage = 0;
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            reqCategoryDatas(false);
        });
        stackLabel_4.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "4***选中===" + s + "----" + index);
            filter4Str = s;
            tvClassifyTop3Title.setText(String.format(getString(R.string.txt_category_dt_sub_title), mTitle, filter4Str));

            paramStatus = index + 1;
            paramCurPage = 0;
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            reqCategoryDatas(false);
        });

        ivCoverFirst.setOnClickListener(this);
        ivCoverSecond.setOnClickListener(this);
        ivCoverThird.setOnClickListener(this);

        mAdapter.setOnItemClickListener(this);
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
    }


    @Override
    public void doBusiness(Context mContext) {
        List<BookEntity> list = new ArrayList<>();
        BookEntity data;
        for (int i = 0; i < 10; i++) {
            data = new BookEntity();
            data.setId("" + i);
            list.add(data);
        }

        reqSubCategory(pCatId);

        subCatID = pCatId;
        reqCategoryDatas(true);
    }


    @Override
    public void onLoadMore() {
        reqCategoryDatas(false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top3_bookCover_first:
            case R.id.iv_top3_bookCover_second:
            case R.id.iv_top3_bookCover_third:
                String bookId = (String) view.getTag();
                Intent intent = new Intent(this, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, bookId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        BookEntity entity = mAdapter.getData().get(position);
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
        startActivity(intent);
    }


    /**
     * 查询分类下的书籍数据
     *
     * @param showDialog
     */
    private void reqCategoryDatas(boolean showDialog) {
        RxHttp.postForm(Consts.NOVEL_CHOICE_API)
                .add(Consts.CAT_IS_PARENT, isParent)
                .add(Consts.CAT_ID, subCatID)
                .add(Consts.CAT_IS_END, paramIsEnd)
                .add(Consts.CAT_WORD_NUM, paramWordsNum)
                .add(Consts.CAT_STATUS, paramStatus)
                .add(Consts.PAGE, paramCurPage + 1)
                .asResponsePageList(BookEntity.class)
                .doOnSubscribe(disposable -> {
                    if (showDialog) {
                        showLoadingDialog();
                    }
                })
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    paramCurPage = entity.getCurrent_page();
                    int lastPage = entity.getLast_page();
                    List<BookEntity> bookList = entity.getData();
                    if (paramCurPage == 1) {
                        int size = bookList.size();
                        if (size >= 3) {
                            mAdapter.setHeaderView(headViewTop3, 1);
                            BookEntity book1 = bookList.get(0);
                            BookEntity book2 = bookList.get(1);
                            BookEntity book3 = bookList.get(2);
                            ivCoverFirst.setTag(book1.getId());
                            ivCoverSecond.setTag(book2.getId());
                            ivCoverThird.setTag(book3.getId());
                            GlideUtil.loadRoundRect(ClassifyDetailActivity.this, ivCoverFirst, book1.getHttp_image());
                            GlideUtil.loadRoundRect(ClassifyDetailActivity.this, ivCoverSecond, book2.getHttp_image());
                            GlideUtil.loadRoundRect(ClassifyDetailActivity.this, ivCoverThird, book3.getHttp_image());
                            tvBookNameFirst.setText(book1.getName());
                            tvBookNameSecond.setText(book2.getName());
                            tvBookNameThird.setText(book3.getName());
                            mAdapter.setList(bookList.subList(3, size));
                        } else {
                            mAdapter.removeHeaderView(headViewTop3);
                            mAdapter.setList(bookList);
                        }
                    } else {
                        mAdapter.addData(bookList);
                    }

                    if (lastPage <= paramCurPage) {
                        //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }


    /**
     * 三级子分类
     */
    private void reqSubCategory(int pid) {
        RxHttp.postForm(Consts.NOVEL_CATEGORY_SUB_API)
                .setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)
                .setCacheValidTime(10 * 60 * 1000)
                .setCacheKey(Consts.NOVEL_CATEGORY_SUB_API + "_" + pid)
                .add(Consts.CAT_PID, pid)
                .asResponseList(CategoryEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(list -> {
                    datas = list;
                    for (CategoryEntity ce : datas) {
                        stack1Datas.add(ce.getName());
                    }
                    stackLabel_1.setLabels(stack1Datas);
                    stackLabel_1.setSelectMode(true, stack1Datas.subList(0, 1));
                });
    }

}
