package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.HotKeywordBean;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.adapters.SHotBooksAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchBookActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {

    private EditText etKeyword;
    private TextView btnSearch;

    private RecyclerView recyclerView;
    private SHotBooksAdapter mAdapter;

    private StackLabel sLabelHistory, sLabelHot;
    private TextView btnClearHistory;

    private List<HotKeywordBean> hotKwList = new ArrayList<>();

    private int mType = 0;
    private int curPage = 0;        //当前页码，下一页 +1

    @Override
    public void initParms(Bundle parms) {
        if (parms != null) {
            mType = parms.getInt(Consts.TYPE, 0);
        }
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

        View headView = LayoutInflater.from(this).inflate(R.layout.layout_rc_head_search_hot_book_top, recyclerView, false);
        mAdapter.addHeaderView(headView);
        mAdapter.setEmptyView(R.layout.layout_empty);
        mAdapter.setHeaderWithEmptyEnable(true);

        sLabelHistory = headView.findViewById(R.id.stackLabelView_history_search);
        sLabelHot = headView.findViewById(R.id.stackLabelView_hot_search);
        btnClearHistory = headView.findViewById(R.id.tv_asBtn_clear_history_search);
    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(this);
        etKeyword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 在这里写搜索的操作
                btnSearch.performClick();
                return true;
            }
            return false;
        });

        btnClearHistory.setOnClickListener(this);
        sLabelHistory.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                if (!sLabelHistory.isDeleteButton()) {
                    // 搜索
                    go2Search(s);
                }
            }
        });
        sLabelHot.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                if (!sLabelHot.isDeleteButton()) {
                    //TODO 搜索
                   /* if (hotKwList.size() > index) {
                        HotKeywordBean data = hotKwList.get(index);
                        Tos.showShort(SearchBookActivity.this, "热门搜索===" + s);
                    }*/
                    addHistoryLabel(s);

                    // 搜索
                    go2Search(s);
                }
            }
        });

        mAdapter.setOnItemClickListener(this);
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                reqGetHotNovel(curPage + 1);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        List<String> historySearchList = getHistorySearchSp(mContext, Common.SPFKEY_SEARCH_HISTORY);
        sLabelHistory.setLabels(historySearchList);

        reqHotSearchData();
        reqGetHotNovel(curPage + 1);
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
                keyword = keyword.trim();

                addHistoryLabel(keyword);
                //点击搜索的时候隐藏软键盘
                hideKeyboard(etKeyword);

                go2Search(keyword);

                etKeyword.setText("");
                break;
            case R.id.tv_asBtn_clear_history_search:
                clearHistoryLabel();
                clearHistorySearchSp(this, Common.SPFKEY_SEARCH_HISTORY);
                break;
            default:
                break;
        }
    }


    /**
     * 前往搜索
     *
     * @param keyword 关键词
     */
    private void go2Search(String keyword) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(Common.KEY_KEYWORD, keyword);
        startActivity(intent);
    }

    private void clearHistoryLabel() {
        sLabelHistory.setLabels(new ArrayList<>());
//        sLabelHistory.setVisibility(View.GONE);
    }

    private void addHistoryLabel(String keyword) {
        List<String> labelList = sLabelHistory.getLabels();
        if (labelList != null && labelList.contains(keyword)) {
            return;
        }
        sLabelHistory.addLabel(keyword);
        if (sLabelHistory.getVisibility() != View.VISIBLE) {
            sLabelHistory.setVisibility(View.VISIBLE);
        }
        saveHistorySearchSp(this, Common.SPFKEY_SEARCH_HISTORY, sLabelHistory.getLabels());
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


    private List<String> getHistorySearchSp(Context context, String key) {
        String str = (String) SPFUtils.get(context, key, "");
        if (!TextUtils.isEmpty(str)) {
            return JSONArray.parseArray(str, String.class);
        }
        return null;
    }

    private void saveHistorySearchSp(Context context, String key, List<String> values) {
        if (values != null) {
            String str = JSON.toJSONString(values);
            SPFUtils.put(context, key, str);
        } else {
            clearHistorySearchSp(this, Common.SPFKEY_SEARCH_HISTORY);
        }
    }

    private void clearHistorySearchSp(Context context, String key) {
        SPFUtils.remove(context, key);
    }


    /**
     * 热门搜索---关键词
     */
    private void reqHotSearchData() {
        OkGo.<String>get(Consts.KEYWORD_INDEX_API)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<HotKeywordBean>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<HotKeywordBean>>>() {
                                });
                        if (entity.error_code == 0) {
                            hotKwList = entity.getData();
                            List<String> labelList = new ArrayList<>();
                            for (HotKeywordBean hkb : hotKwList) {
                                labelList.add(hkb.getKeyword());
                            }
                            sLabelHot.setLabels(labelList);
                        }
                    }
                });
    }

    /**
     * 热门书籍
     *
     * @param reqPage 请求的页码
     */
    private void reqGetHotNovel(int reqPage) {
        OkGo.<String>post(Consts.NOVEL_HOT_API)
                .params(Consts.TYPE, mType)
                .params(Consts.PAGE, reqPage)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<BookEntity>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<BookEntity>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<BookEntity>> data = entity.getData();
                            curPage = data.getCurrent_page();
                            List<BookEntity> bookList = data.getData();
                            if (curPage == 1) {
                                mAdapter.setNewData(bookList);
                            } else {
                                mAdapter.addData(bookList);
                            }
                            if (data.getLast_page() <= curPage) {    //没有下一页
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
                });
    }

}
