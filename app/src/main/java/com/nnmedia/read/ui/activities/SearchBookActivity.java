package com.nnmedia.read.ui.activities;

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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.stacklabelview.StackLabel;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.SearchRecordEvent;
import com.nnmedia.read.entity.HotKeywordBean;
import com.nnmedia.read.entity.RankBookEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.adapters.SHotBooksAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.SPFUtils;
import com.nnmedia.read.utils.Tos;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    private int curPage = 0;        //当前页码


    @Override
    public void initParms(Bundle parms) {

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
        mAdapter.setAnimationEnable(true);
        mAdapter.setAnimationFirstOnly(false);
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
        sLabelHistory.setOnLabelClickListener((index, v, s) -> {
            if (!sLabelHistory.isDeleteButton()) {
                // 搜索
                go2Search(s);
            }
        });
        sLabelHot.setOnLabelClickListener((index, v, s) -> {
            if (!sLabelHot.isDeleteButton()) {
                addHistoryLabel(s);

                // 搜索
                go2Search(s);
            }
        });

        mAdapter.setOnItemClickListener(this);
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqGetHotNovel(curPage));
    }

    @Override
    public void doBusiness(Context mContext) {
        List<String> historySearchList = getHistorySearchSp(mContext, Common.SPFKEY_SEARCH_HISTORY);
        if (historySearchList != null && historySearchList.size() > 0) {
            sLabelHistory.setVisibility(View.VISIBLE);
            sLabelHistory.setLabels(historySearchList);
        } else {
            sLabelHistory.setVisibility(View.GONE);
        }

        reqHotSearchData();

        reqGetHotNovel(0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserInfoCache.getIsTourist(SearchBookActivity.this)){
            goRegister("亲爱的用户，为了有更好的阅读体验，请登录以后使用搜索功能！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchRecordEvent(SearchRecordEvent event) {
        if (!TextUtils.isEmpty(event.getKeyword())) {
            addHistoryLabel(event.getKeyword());
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (onMoreClick()) {
            return;
        }
        RankBookEntity entity = mAdapter.getData().get(position);
        Intent intent = new Intent(this, BookDetailsActivity.class);
        intent.putExtra(Common.KEY_BOOK_ID, entity.getNovel_id());
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_search:
                if (onMoreClick()) {
                    return;
                }
                String keyword = etKeyword.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) {
                    Tos.showShort(this, R.string.txt_plz_input_keyword);
                    return;
                }

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
        sLabelHistory.setVisibility(View.GONE);
    }

    private void addHistoryLabel(String keyword) {
        List<String> labelList = sLabelHistory.getLabels();
        if (labelList != null && labelList.contains(keyword)) {
            return;
        }
        if (labelList == null) {
            labelList = new ArrayList<>();
        }
        labelList.add(0, keyword);
        sLabelHistory.setLabels(labelList);
        sLabelHistory.setVisibility(View.VISIBLE);
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

    private void goRegister(String content) {
        MessageDialog.show(SearchBookActivity.this, "提示", content, "去登录", "返回")
                .setCancelable(false)
                .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        finish();
                        baseDialog.doDismiss();
                        return false;
                    }
                })
                .setOnOkButtonClickListener((baseDialog, v) -> {
                    LoginActivity2.start(this);
                    baseDialog.doDismiss();
                    return false;
                });
    }


    /**
     * 热门搜索---关键词
     */
    private void reqHotSearchData() {
        RxHttp.get(Consts.HOT_KEYWORD_API)
                .setAssemblyEnabled(false)
                .asResponseList(HotKeywordBean.class)
                .to(RxLife.toMain(this))
                .subscribe(list -> {
                    hotKwList = list;
                    List<String> labelList = new ArrayList<>();
                    for (HotKeywordBean hkb : hotKwList) {
                        labelList.add(hkb.getKeyword());
                    }
                    sLabelHot.setLabels(labelList);
                });
    }


    /**
     * 热门书籍
     */
    private void reqGetHotNovel(int prePage) {
        RxHttp.get(Consts.POPULAR_RANKING_API)
                .add(Consts.CATEGORY, Consts.RANK_TYPE_HOT_BOT)
                .add(Consts.PAGE, prePage + 1)
                .asResponsePageList(RankBookEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    curPage = entity.getCurrent_page();
                    List<RankBookEntity> bookList = entity.getData();
                    if (curPage == 1) {
                        mAdapter.setList(bookList);
                    } else {
                        mAdapter.addData(bookList);
                    }
                    if (entity.getLast_page() <= curPage) {
                        //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }

}
