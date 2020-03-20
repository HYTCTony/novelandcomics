package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.ui.adapters.SHotBooksAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SearchResultActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {

    private EditText etKeyword;
    private TextView btnSearch;

    private RecyclerView recyclerView;
    private SHotBooksAdapter mAdapter;

    private String keyWord;

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
        mAdapter = new SHotBooksAdapter();
        recyclerView.setAdapter(mAdapter);
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
    }

    @Override
    public void doBusiness(Context mContext) {
        etKeyword.setText(keyWord);
        reqCategoryDatas(keyWord, true);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_search:
                if(onMoreClick()){
                    return;
                }
                String keyword = etKeyword.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    Tos.showShort(this, R.string.txt_plz_input_keyword);
                    return;
                }
                //点击搜索的时候隐藏软键盘
                hideKeyboard(etKeyword);
                keyword = keyword.trim();
                reqCategoryDatas(keyword, true);
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if(onMoreClick()){
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
     * @param view :一般为EditText
     */
    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void reqCategoryDatas(String keyword, boolean showDialog) {
        OkGo.<String>post(Consts.NOVEL_KEYWORD_API)
                .params(Consts.FILTRATE_KEYWORD, keyword)
                .execute(new LtbCallback(this, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<BookEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BookEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<BookEntity> data = entity.getData();
                            mAdapter.setNewData(data);
                        }
                    }
                });
    }

}
