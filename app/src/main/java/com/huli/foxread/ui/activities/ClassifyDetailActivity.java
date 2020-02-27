package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.BooksListAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.GlideUtil;
import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClassifyDetailActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BooksListAdapter mAdapter;

    private StackLabel stackLabel_1, stackLabel_2, stackLabel_3, stackLabel_4;
    private TextView btnMoreFilter;
    private View layoutMoreFilter;

    private TextView tvClassifyTop3Title;
    private ImageView ivCoverFirst, ivCoverSecond, ivCoverThird;
    private TextView tvBookNameFirst, tvBookNameSecond, tvBookNameThird;

    @Override
    public void initParms(Bundle parms) {

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
        //TODO-------------------title
        initToolBar(toolbar, "sub分类");

        recyclerView = $(R.id.recyclerView_classify_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BooksListAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(this);
        View headViewFilter = inflater.inflate(R.layout.layout_rv_head_classify_detail_filter, recyclerView, false);
        View headViewTop3 = inflater.inflate(R.layout.layout_rv_head_classify_detail_top3, recyclerView, false);
        mAdapter.addHeaderView(headViewFilter, 0);
        mAdapter.addHeaderView(headViewTop3, 1);

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

    }

    @Override
    public void setListener() {
        btnMoreFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMoreFilter.setVisibility(View.GONE);
                layoutMoreFilter.setVisibility(View.VISIBLE);
            }
        });

        stackLabel_1.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                Log.e(TAG, "1***选中===" + s + "----" + index);
            }
        });
        stackLabel_2.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                Log.e(TAG, "2***选中===" + s + "----" + index);
            }
        });
        stackLabel_3.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                Log.e(TAG, "3***选中===" + s + "----" + index);
            }
        });
        stackLabel_4.setOnLabelClickListener(new OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                Log.e(TAG, "4***选中===" + s + "----" + index);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("sssssss" + i);
        }
        mAdapter.setNewData(list);

        stackLabel_1.setLabels(new String[]{"全部", "总裁豪门", "都市人生", "婚恋爱情", "职场情缘", "民国旧影", "娱乐明星"});
        stackLabel_2.setLabels(new String[]{"全部", "完结", "连载"});
        stackLabel_3.setLabels(new String[]{"全部", "50万字以下", "50-100万字", "100-200万字", "200-500万字", "500万字以上"});
        stackLabel_4.setLabels(new String[]{"最热", "最新", "评分"});

        List<String> selected = Collections.singletonList("全部");
        List<String> selected2 = Collections.singletonList("最热");
        stackLabel_1.setSelectMode(true, selected);
        stackLabel_2.setSelectMode(true, selected);
        stackLabel_3.setSelectMode(true, selected);
        stackLabel_4.setSelectMode(true, selected2);

        tvClassifyTop3Title.setText("现代言情类全网最热前三名");
        GlideUtil.loadRoundRect(this, ivCoverFirst, "url", 0);
        GlideUtil.loadRoundRect(this, ivCoverSecond, "url", 0);
        GlideUtil.loadRoundRect(this, ivCoverThird, "url", 0);
        tvBookNameFirst.setText("豪门千金的超级战神");
        tvBookNameSecond.setText("天启时代");
        tvBookNameThird.setText("傲娇总裁侨萌妻");
    }

}
