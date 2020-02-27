package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.entity.NbSection;
import com.huli.foxread.entity.NewBookEntity;
import com.huli.foxread.ui.adapters.SectionNewBookAdapter;
import com.huli.foxread.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EndBooksFragment extends BaseFragment {

    private RecyclerView recyclerView;

    //TODO-------------------
    private SectionNewBookAdapter mAdapter;

    private int mType;

    public static EndBooksFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        EndBooksFragment frag = new EndBooksFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_end_books;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        recyclerView = $(view, R.id.recyclerView_end_book);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4));
        mAdapter = new SectionNewBookAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        List<NbSection<NewBookEntity>> choiSections = initDatas();

        mAdapter.setNewData(choiSections);
    }

    private List<NbSection<NewBookEntity>> initDatas() {
        List<NbSection<NewBookEntity>> list = new ArrayList<>();

        list.add(new NbSection<>(true, false, "主编精选", null));
        for (int i = 0; i < 8; i++) {
            NewBookEntity newbook = new NewBookEntity();
            newbook.setCoverImgUrl("sssssssss");
            newbook.setTitle("无能狂怒" + "title" + i);
            list.add(new NbSection<>(false, "", newbook));
        }

        list.add(new NbSection<>(true, true,"上周更新", null));
        for (int i = 0; i < 8; i++) {
            NewBookEntity newbook = new NewBookEntity();
            newbook.setCoverImgUrl("sssssssss");
            newbook.setTitle("英文字幕英文字幕英文字幕英文字幕英文字幕" + "title" + i);
            list.add(new NbSection<>(false, "", newbook));
        }

        list.add(new NbSection<>(true, true,"现代萌宝", null));
        for (int i = 0; i < 8; i++) {
            NewBookEntity newbook = new NewBookEntity();
            newbook.setCoverImgUrl("sssssssss");
            newbook.setTitle("压脉带" + "title" + i);
            list.add(new NbSection<>(false, "", newbook));
        }

        list.add(new NbSection<>(true, true,"穿越架空", null));
        for (int i = 0; i < 8; i++) {
            NewBookEntity newbook = new NewBookEntity();
            newbook.setCoverImgUrl("sssssssss");
            newbook.setTitle("高能*******" + i);
            list.add(new NbSection<>(false, "", newbook));
        }
        return list;
    }
}
