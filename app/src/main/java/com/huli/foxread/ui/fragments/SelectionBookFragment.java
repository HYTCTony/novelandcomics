package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.GridSpanSizeLookup;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.engines.GlideImageLoader;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.entity.BookMultiEntity;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.activities.BookRankingActivity;
import com.huli.foxread.ui.activities.ClassifyActivity;
import com.huli.foxread.ui.activities.EndBooksActivity;
import com.huli.foxread.ui.activities.NewBooksActivity;
import com.huli.foxread.ui.adapters.AttTopSearchAdapter;
import com.huli.foxread.ui.adapters.BooksListAdapter;
import com.huli.foxread.ui.adapters.BooksMultiItemAdapter;
import com.huli.foxread.ui.adapters.HotTodayAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.pageradapter.MultiplePagerAdapter;
import com.huli.foxread.ui.pageradapter.SpecialTopicPagerAdapter;
import com.huli.foxread.utils.DensityUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


public class SelectionBookFragment extends BaseFragment implements View.OnClickListener, OnBannerListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private BooksListAdapter mAdapter;

    private Banner mBanner;

    @Override
    public int bindLayout() {
        return R.layout.fragment_bookstore_general_refresh_recy;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        mRefreshLayout = $(view, R.id.smartRefreshLayout_book_store);

        recyclerView = $(view, R.id.recyclerView_book_store);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new BooksListAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View headViewTop = inflater.inflate(R.layout.layout_rv_head_sb_top, recyclerView, false);
        mAdapter.addHeaderView(headViewTop, 0);
        View headViewHot = inflater.inflate(R.layout.layout_rv_head_sb_hotlist_today, recyclerView, false);
        mAdapter.addHeaderView(headViewHot, 1);
        View headViewExcellentWorks = inflater.inflate(R.layout.layout_rc_head_sb_classify_excellent_work, recyclerView, false);
        mAdapter.addHeaderView(headViewExcellentWorks, 2);
        View headViewSpecial = inflater.inflate(R.layout.layout_rv_head_sb_special_topic, recyclerView, false);
        mAdapter.addHeaderView(headViewSpecial, 3);
        View headViewTopSearch = inflater.inflate(R.layout.layout_rv_head_sb_actual_time_top_search, recyclerView, false);
        mAdapter.addHeaderView(headViewTopSearch, 4);
        View headViewNewBooks = inflater.inflate(R.layout.layout_rv_head_multiitem_books, recyclerView, false);
        mAdapter.addHeaderView(headViewNewBooks, 5);
        View headViewHighScore = inflater.inflate(R.layout.layout_rv_head_normal_title, recyclerView, false);
        mAdapter.addHeaderView(headViewHighScore, 6);


        initBannerView(headViewTop);
        initCenterBar(headViewTop);

        initHotlistView(headViewHot);

        initExcellentWorksView(headViewExcellentWorks);

        initSpecialTopicView(headViewSpecial);

        initTopSearchView(headViewTopSearch);

        initNewBooksLeadUpView(headViewNewBooks);
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(mActivity, BookDetailsActivity.class));
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {


        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("sssssssssss");
        }
        mAdapter.setNewData(list);
    }

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        if (mBanner != null) {
            mBanner.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        if (mBanner != null) {
            mBanner.stopAutoPlay();
        }
    }


    @Override
    public void OnBannerClick(int position) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_asBtn_classify:
                startActivity(new Intent(mActivity, ClassifyActivity.class));
                break;

            case R.id.tv_asBtn_ranking:
                startActivity(new Intent(mActivity, BookRankingActivity.class));
                break;

            case R.id.tv_asBtn_new_book:
                startActivity(new Intent(mActivity, NewBooksActivity.class));
                break;

            case R.id.tv_asBtn_book_finished:
                startActivity(new Intent(mActivity, EndBooksActivity.class));
                break;

            case R.id.tv_asBtn_full_list:       //今日大热榜 -> 完整榜单

                break;

            case R.id.tv_asBtn_get_a_new_batch_top_search:       //实时热搜 -> 换一批

                break;

            case R.id.tv_asBtn_newBooks_refresh:       //新书抢先 -> 换一批

                break;

            default:
                break;
        }
    }


    private void initNewBooksLeadUpView(View rootView) {
        $(rootView, R.id.tv_asBtn_newBooks_refresh).setOnClickListener(this);

        RecyclerView rvLeadUpBooks = $(rootView, R.id.recyclerView_new_books);

        final List<BookMultiEntity> datas = new ArrayList<>();
        BookMultiEntity entity = new BookMultiEntity();
        entity.setItemType(BookMultiEntity.DETAILED);
        entity.setSpanSize(BookMultiEntity.SPAN_SIZE_4);
        datas.add(entity);
        for (int i = 0; i < 8; i++) {
            entity = new BookMultiEntity();
            entity.setItemType(BookMultiEntity.SUCCINCT);
            entity.setSpanSize(BookMultiEntity.SPAN_SIZE_1);
            datas.add(entity);
        }

        final BooksMultiItemAdapter lubAdapter = new BooksMultiItemAdapter(datas);
        rvLeadUpBooks.setLayoutManager(new GridLayoutManager(mActivity, 4));
//        rvLeadUpBooks.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(mActivity, 16), false));
        lubAdapter.setGridSpanSizeLookup(new GridSpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int viewType, int position) {
                return datas.get(position).getSpanSize();
            }
        });
        rvLeadUpBooks.setAdapter(lubAdapter);
    }


    private void initTopSearchView(View rootView) {
        $(rootView, R.id.tv_asBtn_get_a_new_batch_top_search).setOnClickListener(this);

        RecyclerView rvTopSearch = $(rootView, R.id.recyclerView_top_search);
        rvTopSearch.setNestedScrollingEnabled(false);
        rvTopSearch.setLayoutManager(new GridLayoutManager(mActivity, 4));
        rvTopSearch.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(mActivity, 16), false));
        AttTopSearchAdapter topSearchAdapter = new AttTopSearchAdapter();
        rvTopSearch.setAdapter(topSearchAdapter);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add("sssssssssss");
        }
        topSearchAdapter.setNewData(list);
    }

    private void initSpecialTopicView(View rootView) {
        ViewPager vpSpt = $(rootView, R.id.viewPager_special_topic);

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(i);
        }
        vpSpt.setOffscreenPageLimit(list.size());
        vpSpt.setAdapter(new SpecialTopicPagerAdapter(getContext(), list));
        vpSpt.setPageMargin(DensityUtils.dp2px(mActivity, 16));
    }

    private void initExcellentWorksView(View rootView) {
        ViewPager vpExWorks = $(rootView, R.id.viewPager_excellent_works);
        List<Fragment> exwFragments = new ArrayList<>();
        exwFragments.add(ExWorksShowFargment.newInstance(0));
        exwFragments.add(ExWorksShowFargment.newInstance(1));
        exwFragments.add(ExWorksShowFargment.newInstance(2));
        exwFragments.add(ExWorksShowFargment.newInstance(3));
        vpExWorks.setOffscreenPageLimit(exwFragments.size());
        vpExWorks.setAdapter(new MultiplePagerAdapter(getChildFragmentManager(), exwFragments));
        vpExWorks.setPageMargin(DensityUtils.dp2px(mActivity, 16));
    }

    private void initHotlistView(View rootView) {
        $(rootView, R.id.tv_asBtn_full_list).setOnClickListener(this);

        RecyclerView rvHot = $(rootView, R.id.recyclerView_hotlist);
        rvHot.setNestedScrollingEnabled(false);
        rvHot.setLayoutManager(new GridLayoutManager(mActivity, 2));
        rvHot.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtils.dp2px(mActivity, 16), false));
        HotTodayAdapter hotAdapter = new HotTodayAdapter();
        rvHot.setAdapter(hotAdapter);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("sssssssssss");
        }
        hotAdapter.setNewData(list);
    }


    private void initBannerView(View rootView) {
        mBanner = $(rootView, R.id.banner_choiceness);
        //设置banner样式
//        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(3500);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setOnBannerListener(this);

        List<BannerADEntity> bannerADs = getBannerADs();
        //设置图片集合
        mBanner.setImages(bannerADs);
        //banner设置方法全部调用完毕时最后调用

        mBanner.start();
    }

    private void initCenterBar(View rootView) {
        $(rootView, R.id.tv_asBtn_classify).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_ranking).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_new_book).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_book_finished).setOnClickListener(this);
    }

    private List<BannerADEntity> getBannerADs() {
        List<BannerADEntity> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BannerADEntity ad = new BannerADEntity();
            ad.setTitle("AD标题-----" + i);
            ad.setType(1);
            ad.setImgUrl("https://p9-tt.byteimg.com/large/pgc-image/5489f6a4f7ac41e18a9164b650a4ba9b");
            list.add(ad);
        }
        return list;
    }

}
