package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.SearchRecordEvent;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.adapters.SearchResultBooksAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.AESCBCUtil;
import com.nnmedia.read.utils.Tos;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SearchResultActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener, OnLoadMoreListener {

    private EditText etKeyword;
    private TextView btnSearch;

    private RecyclerView recyclerView;
    private SearchResultBooksAdapter mAdapter;

    private String keyWord;

    private int curPage = 0;        //当前页码，下一页 +1

    @Override
    public void initParms(Bundle parms) {
        keyWord = parms.getString(Common.KEY_KEYWORD);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_search_book;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, "");

        etKeyword = $(R.id.editText_search_keyword);
        btnSearch = $(R.id.tv_asBtn_search);

        recyclerView = $(R.id.recyclerView_search_hot_books);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchResultBooksAdapter();
        mAdapter.setAnimationEnable(true);
        mAdapter.setAnimationFirstOnly(false);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(this);
        etKeyword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 在这里写搜索的操作,一般都是网络请求数据
                btnSearch.performClick();
                return true;
            }
            return false;
        });
        mAdapter.setOnItemClickListener(this);
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        etKeyword.setText(keyWord);
        reqCategoryDatas(keyWord, curPage, true);
    }


    @Override
    public void onLoadMore() {
        reqCategoryDatas(keyWord, curPage, false);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_asBtn_search) {
            if (onMoreClick()) {
                return;
            }
            keyWord = etKeyword.getText().toString().trim();
            if (TextUtils.isEmpty(keyWord)) {
                Tos.showShort(this, R.string.txt_plz_input_keyword);
                return;
            }
            //点击搜索的时候隐藏软键盘
            hideKeyboard(etKeyword);
            keyWord = keyWord.trim();
            curPage = 0;
            reqCategoryDatas(keyWord, curPage, true);

            EventBus.getDefault().post(new SearchRecordEvent(keyWord));
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (onMoreClick()) {
            return;
        }
        BookEntity entity = mAdapter.getData().get(position);
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
        startActivity(intent);
    }


    /**
     * 隐藏软键盘
     *
     * @param view 一般为EditText
     */
    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void reqCategoryDatas(String keyword, int page, boolean showDialog) {
        keyword = AESCBCUtil.encrypt2(keyword, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        RxHttp.postForm(Consts.SEARCH_NOVEL_API)
                .add(Consts.FILTRATE_KEYWORD, keyword)
                .add(Consts.PAGE, page + 1)
                .asResponsePageList(BookEntity.class)
                .doOnSubscribe(disposable -> {
                    if (showDialog) {
                        showLoadingDialog();
                    }
                })
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    curPage = entity.getCurrent_page();
                    List<BookEntity> bookList = entity.getData();
                    if (curPage == 1) {
                        mAdapter.setList(bookList);
                    } else {
                        mAdapter.addData(bookList);
                    }
                    if (entity.getLast_page() <= curPage) {    //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }

}