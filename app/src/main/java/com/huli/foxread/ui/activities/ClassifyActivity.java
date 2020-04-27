package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CategoryEntity;
import com.huli.foxread.entity.sections.CommonSection;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.adapters.SectionClassifyAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.verticaltablayout.ITabView;
import com.huli.foxread.ui.widget.verticaltablayout.TabAdapter;
import com.huli.foxread.ui.widget.verticaltablayout.TabView;
import com.huli.foxread.ui.widget.verticaltablayout.VerticalTabLayout;
import com.huli.foxread.utils.NetworkUtil;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClassifyActivity extends BaseActivity implements OnItemClickListener {

    private ImageView btnSearch;
    private VerticalTabLayout tabLayout;
    //    private NoScrollViewPager viewPager;
    private RecyclerView recyclerView;
    private SectionClassifyAdapter mAdapter;

    private int textSelectCol, textUnSelectCol;

    private SparseIntArray intArray;

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
//        viewPager = $(R.id.noScrollViewPager_content_classify);
//        viewPager.setNoScroll(true);
        recyclerView = $(R.id.recyclerView_content_classify);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SectionClassifyAdapter();
        recyclerView.setAdapter(mAdapter);

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
        mAdapter.setOnItemClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstIndex = layoutManager.findFirstVisibleItemPosition();
                int lastPos = layoutManager.findLastVisibleItemPosition();
                if (dy > 0) {
                    //向上滚动
                    //lastPos ==
                    int index = intArray.indexOfValue(lastPos);
                    if (index > -1 && tabLayout.getSelectedTabPosition() != index) {
                        tabLayout.setTabSelected(index);
                    }
                } else {
                    //向下滚动
                    int index = intArray.indexOfValue(firstIndex);
                    if (index > -1 && tabLayout.getSelectedTabPosition() != index) {
                        tabLayout.setTabSelected(index);
                    }
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                if (tab.isPressed()) {
                    int itemPos = intArray.get(position);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    layoutManager.scrollToPositionWithOffset(itemPos, 0);
//                    recyclerView.smoothScrollToPosition(itemPos);
                }
            }

            @Override
            public void onTabReselected(TabView tab, int position) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        reqDataFromNet();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Tos.showShort(this, R.string.txt_network_error);
            return;
        } else {
            if (onMoreClick()) {
                return;
            }
        }

        CommonSection<CategoryEntity> commonSection = mAdapter.getData().get(position);
        CategoryEntity cate = commonSection.getObject();
        if (cate != null) {
            Intent intent = new Intent(this, ClassifyDetailActivity.class);
            intent.putExtra(Common.KEY_CAT_ID, cate.getId());
            intent.putExtra(Common.KEY_CAT_TITLE, cate.getName());
            startActivity(intent);
        }
    }

    private class MyTabAdapter implements TabAdapter {
        private List<CategoryEntity> datas;

        public MyTabAdapter(List<CategoryEntity> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
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
            return R.drawable.selector_vtab_bg_on_gray;
        }
    }


    /**
     * 全部分类
     */
    private void reqDataFromNet() {
        OkGo.<String>post(Consts.NOVEL_CATEGORY_ALL_API)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheTime(60 * 60 * 1000)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<CategoryEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<CategoryEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<CategoryEntity> datas = entity.getData();
                            tabLayout.setTabAdapter(new MyTabAdapter(datas));
                           /* tabLayout.setTabAdapter(new TabAdapter() {
                                @Override
                                public int getCount() {
                                    return datas.size();
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
                                    return R.drawable.selector_vtab_bg_on_gray;
                                }
                            });*/

                            intArray = new SparseIntArray();
                            List<CommonSection<CategoryEntity>> list = new ArrayList<>();
                            for (int i = 0; i < datas.size(); i++) {
                                CategoryEntity cateGroup = datas.get(i);
                                List<CategoryEntity> cate = cateGroup.getList();
                                list.add(new CommonSection<>(true, cateGroup.getName(), null));
                                intArray.put(i, list.size() - 1);
                                for (int j = 0; j < cate.size(); j++) {
                                    list.add(new CommonSection<>(false, "", cate.get(j)));
                                }
                            }
                            mAdapter.setNewData(list);
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }
                });
    }

}
