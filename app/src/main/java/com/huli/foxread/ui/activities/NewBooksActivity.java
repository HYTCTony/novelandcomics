package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.entity.NbSection;
import com.huli.foxread.entity.NewBookEntity;
import com.huli.foxread.ui.adapters.SectionNewBookAdapter;
import com.huli.foxread.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewBooksActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SectionNewBookAdapter mAdapter;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_new_books;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_new_book);

        recyclerView = $(R.id.recyclerView_new_book);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
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
