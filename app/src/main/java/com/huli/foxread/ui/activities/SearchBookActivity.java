package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.SHotBooksAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchBookActivity extends BaseActivity implements View.OnClickListener {

    private EditText etKeyword;
    private TextView btnSearch;

    private RecyclerView recyclerView;
    private SHotBooksAdapter mAdapter;

    private StackLabel sLabelHistory, sLabelHot;
    private TextView btnClearHistory;

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
        recyclerView.setAdapter(mAdapter);

        View headView = LayoutInflater.from(this).inflate(R.layout.layout_rc_head_search_hot_book_top, recyclerView, false);
        mAdapter.addHeaderView(headView);

        sLabelHistory = headView.findViewById(R.id.stackLabelView_history_search);
        sLabelHot = headView.findViewById(R.id.stackLabelView_hot_search);
        btnClearHistory = headView.findViewById(R.id.tv_asBtn_clear_history_search);
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

        btnClearHistory.setOnClickListener(this);
        sLabelHistory.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                if (!sLabelHistory.isDeleteButton()) {
                    //TODO 搜索
                    Log.e(TAG, "stackLabelHistory搜索===" + s);
                }
            }
        });
        sLabelHot.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                if (!sLabelHot.isDeleteButton()) {
                    //TODO 搜索
                    Log.e(TAG, "stackLabelHot搜索===" + s);
                    addHistoryLabel(s);
                }
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("ssssssss" + i);
        }
        mAdapter.setNewData(list);

        sLabelHistory.setLabels(new String[]{"Label1", "Label2", "Label3", "Label4", "Label5", "Label6", "Label7", "Label8", "Label9"});
        sLabelHot.setLabels(new String[]{"标签1", "标签2", "标签3", "标签4", "标签5", "标签6", "标签7", "标签8", "标签9"});
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_search:
                String keyword = etKeyword.getText().toString();
                addHistoryLabel(keyword);
                //点击搜索的时候隐藏软键盘
                hideKeyboard(etKeyword);
                etKeyword.setText("");
                break;
            case R.id.tv_asBtn_clear_history_search:
                clearHistoryLabel();
                break;
            default:
                break;
        }
    }

    private void clearHistoryLabel() {
        sLabelHistory.setLabels(new ArrayList<>());
        sLabelHistory.setVisibility(View.GONE);
    }

    private void addHistoryLabel(String keyword) {
        List<String> labelList = sLabelHistory.getLabels();
        if (labelList.contains(keyword)) {
            return;
        }
        sLabelHistory.addLabel(keyword);
        if (sLabelHistory.getVisibility() != View.VISIBLE) {
            sLabelHistory.setVisibility(View.VISIBLE);
        }
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

}
