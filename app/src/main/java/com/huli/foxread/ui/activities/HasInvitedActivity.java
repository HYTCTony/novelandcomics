package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.HasInvitedFriendsAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.SimpleDividerDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HasInvitedActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private HasInvitedFriendsAdapter mAdapter;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_has_invited;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, "已邀好友");

        recyclerView = $(R.id.recyclerView_has_invited);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerDecoration(this, R.dimen.dp_1, R.dimen.dp_16, R.color.color_f2));
        mAdapter = new HasInvitedFriendsAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        List<String> list = new ArrayList<>();
        for(int i =0 ;i<10;i++){
            list.add("ssssssssss" + i);
        }
        mAdapter.setNewData(list);
    }
}
