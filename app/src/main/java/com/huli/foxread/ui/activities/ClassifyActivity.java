package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CategoryEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.ClassifyFragment;
import com.huli.foxread.ui.widget.NoScrollViewPager;
import com.huli.foxread.ui.widget.verticaltablayout.ITabView;
import com.huli.foxread.ui.widget.verticaltablayout.TabAdapter;
import com.huli.foxread.ui.widget.verticaltablayout.TabView;
import com.huli.foxread.ui.widget.verticaltablayout.VerticalTabLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ClassifyActivity extends BaseActivity {

    private ImageView btnSearch;
    private VerticalTabLayout tabLayout;
    private NoScrollViewPager viewPager;

    private int textSelectCol, textUnSelectCol;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_classify;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_classify);

        btnSearch = $(R.id.iv_asBtn_search);
        tabLayout = $(R.id.verticaltablayout_classify);
        viewPager = $(R.id.noScrollViewPager_content_classify);
        viewPager.setNoScroll(true);

        textSelectCol = ContextCompat.getColor(this, R.color.txt_red);
        textUnSelectCol = ContextCompat.getColor(this, R.color.txt_black);
    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Intent intent = new Intent(ClassifyActivity.this, SearchBookActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        reqDataFromNet();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter implements TabAdapter {

        private List<CategoryEntity> datas;

        public MyPagerAdapter(@NonNull FragmentManager fm, List<CategoryEntity> datas) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.datas = datas;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            CategoryEntity data = datas.get(position);
            return ClassifyFragment.newInstance(data.getId(), data.getNovel_sum());
        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public TabView.TabIcon getIcon(int position) {
            return null;
        }

        @Override
        public TabView.TabTitle getTitle(int position) {
            return new ITabView.TabTitle.Builder().setTextColor(textSelectCol, textUnSelectCol).setContent(datas.get(position).getName()).build();
        }

        @Override
        public int getBackground(int position) {
            return 0;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (datas != null && datas.size() > position) {
                return datas.get(position).getName();
            }
            return "";
        }
    }


    /**
     * 大分类
     */
    private void reqDataFromNet() {
        OkGo.<LzyResponse<List<CategoryEntity>>>post(Consts.NOVEL_CATEGORY_API)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheTime(60 * 60 * 1000)
                .execute(new LtbJsonCallback<LzyResponse<List<CategoryEntity>>>(this, false,
                        new TypeReference<LzyResponse<List<CategoryEntity>>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<List<CategoryEntity>>> response) {
                        LzyResponse<List<CategoryEntity>> entity = response.body();
                        if (entity.error_code == 0) {
                            List<CategoryEntity> datas = entity.getData();
                            viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), datas));
                            tabLayout.setupWithViewPager(viewPager);
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<LzyResponse<List<CategoryEntity>>> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }
                });
    }

}
