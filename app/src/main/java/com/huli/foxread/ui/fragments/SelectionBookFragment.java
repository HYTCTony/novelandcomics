package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.listener.GridSpanSizeLookup;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.engines.GlideImageLoader;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.BookMultiEntity;
import com.huli.foxread.entity.HomePageEntity;
import com.huli.foxread.entity.HpClassifyNvET;
import com.huli.foxread.entity.HpNewBookET;
import com.huli.foxread.entity.HpSpecialEntity;
import com.huli.foxread.entity.SearchEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.listeners.OnClickEvent;
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
import com.huli.foxread.utils.ClickJumpUtil;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


public class SelectionBookFragment extends BaseFragment implements View.OnClickListener, OnBannerListener {

    private SmartRefreshLayout mRefreshLayout;
    public RecyclerView recyclerView;
    private BooksListAdapter mAdapter;

    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;

    private View headViewTop;

    private View headViewHot;
    private RecyclerView rvHot;

    private View headViewExcellentWorks;
    private ViewPager vpExWorks;

    private View headViewSpecial;
    private ViewPager vpSpt;

    private View headViewTopSearch;
    private RecyclerView rvTopSearch;

    private View headViewNewBooks;
    private RecyclerView rvLeadUpBooks;

    private View headViewHighScore;

    private int curPage = 1;

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
        mAdapter.setEmptyView(R.layout.layout_empty);

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        headViewTop = inflater.inflate(R.layout.layout_rv_head_sb_top, recyclerView, false);
        mAdapter.setHeaderView(headViewTop, 0);
        initBannerView(headViewTop);
        initCenterBar(headViewTop);


       /* View headViewHot = inflater.inflate(R.layout.layout_rv_head_sb_hotlist_today, recyclerView, false);
        mAdapter.setHeaderView(headViewHot, 1);
        View headViewExcellentWorks = inflater.inflate(R.layout.layout_rc_head_sb_classify_excellent_work, recyclerView, false);
        mAdapter.setHeaderView(headViewExcellentWorks, 2);
        View headViewSpecial = inflater.inflate(R.layout.layout_rv_head_sb_special_topic, recyclerView, false);
        mAdapter.setHeaderView(headViewSpecial, 3);
        View headViewTopSearch = inflater.inflate(R.layout.layout_rv_head_sb_actual_time_top_search, recyclerView, false);
        mAdapter.setHeaderView(headViewTopSearch, 4);
        View headViewNewBooks = inflater.inflate(R.layout.layout_rv_head_multiitem_books, recyclerView, false);
        mAdapter.setHeaderView(headViewNewBooks, 5);
        View headViewHighScore = inflater.inflate(R.layout.layout_rv_head_normal_title, recyclerView, false);
        mAdapter.setHeaderView(headViewHighScore, 6);


        initHotlistView(headViewHot);

        initExcellentWorksView(headViewExcellentWorks);

        initSpecialTopicView(headViewSpecial);

        initTopSearchView(headViewTopSearch);

        initNewBooksLeadUpView(headViewNewBooks);*/

    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onMoreClick()) {
                return;
            }
            BookEntity entity = mAdapter.getData().get(position);
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
            startActivity(intent);
        });
        // 设置加载更多监听事件
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqHighMarksDatas(curPage));

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
//                reqTopBannerData();

            reqIndexDatas(false);

            //可以上拉加载
            curPage = 1;
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        reqTopBannerData();

        reqIndexDatas(true);
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
        if (bannerDatas != null && bannerDatas.size() > position) {
            BannerADEntity entity = bannerDatas.get(position);
            ClickJumpUtil.handleJump(mActivity, entity.getLink(), entity.getJump(), entity.getNeed_login());
        }
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
                Intent intent = new Intent(mActivity, NewBooksActivity.class);
                intent.putExtra(Consts.TYPE, Consts.TYPE_SELECTION);
                startActivity(intent);
                break;

            case R.id.tv_asBtn_book_finished:
                Intent ebIntent = new Intent(mActivity, EndBooksActivity.class);
                ebIntent.putExtra(Consts.TYPE, Consts.TYPE_SELECTION);
                startActivity(ebIntent);
                break;

            case R.id.tv_asBtn_full_list:       //今日大热榜 -> 完整榜单
                Tos.showShort(mActivity, "今日大热榜 -> 完整榜单");
                break;

            case R.id.tv_asBtn_excellent_work_more:       //分类佳作 -> 更多
                Tos.showShort(mActivity, "分类佳作 -> 更多");
                break;

            case R.id.tv_asBtn_get_a_new_batch_top_search:       //实时热搜 -> 换一批
                Tos.showShort(mActivity, "实时热搜 -> 换个锤子");
                break;

            case R.id.tv_asBtn_newBooks_refresh:       //新书抢先 -> 换一批
                Tos.showShort(mActivity, "新书抢先 -> 换个锤子");
                break;

            default:
                break;
        }
    }


    /*新书抢先*/
    private void initNewBooksLeadUpView(LayoutInflater inflater, List<BookEntity> novelList) {
        if (headViewNewBooks == null) {
            headViewNewBooks = inflater.inflate(R.layout.layout_rv_head_multiitem_books, recyclerView, false);
            mAdapter.setHeaderView(headViewNewBooks, 5);
            $(headViewNewBooks, R.id.tv_asBtn_newBooks_refresh).setOnClickListener(this);
            $(headViewNewBooks, R.id.tv_asBtn_newBooks_refresh).setVisibility(View.GONE);
            rvLeadUpBooks = $(headViewNewBooks, R.id.recyclerView_new_books);
            rvLeadUpBooks.setLayoutManager(new GridLayoutManager(mActivity, 4));
        }

        List<BookMultiEntity> datas = new ArrayList<>();
        BookMultiEntity entity;
        for (int i = 0; i < novelList.size(); i++) {
            entity = new BookMultiEntity();
            BookEntity bookEntity = novelList.get(i);
            if (i == 0) {
                entity.setId(bookEntity.getId());
                entity.setItemType(BookMultiEntity.DETAILED);
                entity.setSpanSize(BookMultiEntity.SPAN_SIZE_4);
                entity.setAuthor(bookEntity.getAuthor());
                entity.setName(bookEntity.getName());
                entity.setScore(bookEntity.getScore());
                entity.setIntroduce(bookEntity.getIntroduce());
                entity.setWord(bookEntity.getWord());
                entity.setHttp_image(bookEntity.getHttp_image());
                datas.add(entity);
            } else {
                entity.setId(bookEntity.getId());
                entity.setItemType(BookMultiEntity.ITEM_FIRST);
                entity.setSpanSize(BookMultiEntity.SPAN_SIZE_1);
                entity.setAuthor(bookEntity.getAuthor());
                entity.setName(bookEntity.getName());
                entity.setScore(bookEntity.getScore());
                entity.setIntroduce(bookEntity.getIntroduce());
                entity.setHttp_image(bookEntity.getHttp_image());
                datas.add(entity);
            }
        }
        BooksMultiItemAdapter lubAdapter = new BooksMultiItemAdapter(datas);
        lubAdapter.setGridSpanSizeLookup(new GridSpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int viewType, int position) {
                return datas.get(position).getSpanSize();
            }
        });
        rvLeadUpBooks.setAdapter(lubAdapter);
        lubAdapter.setOnItemClickListener((adapter, view, position) -> {
            BookEntity entity1 = lubAdapter.getData().get(position);
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, entity1.getId());
            startActivity(intent);
        });
    }

    private void initTopSearchView(LayoutInflater inflater, List<SearchEntity> searchNvList) {
        if (headViewTopSearch == null) {
            headViewTopSearch = inflater.inflate(R.layout.layout_rv_head_sb_actual_time_top_search, recyclerView, false);
            mAdapter.setHeaderView(headViewTopSearch, 4);

            $(headViewTopSearch, R.id.tv_asBtn_get_a_new_batch_top_search).setOnClickListener(this);
            $(headViewTopSearch, R.id.tv_asBtn_get_a_new_batch_top_search).setVisibility(View.GONE);
            rvTopSearch = $(headViewTopSearch, R.id.recyclerView_top_search);
            rvTopSearch.setNestedScrollingEnabled(false);
            rvTopSearch.setLayoutManager(new GridLayoutManager(mActivity, 4));
            rvTopSearch.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(mActivity, 16), false));
        }
        AttTopSearchAdapter attTopSearchAdapter = new AttTopSearchAdapter(searchNvList);
        rvTopSearch.setAdapter(attTopSearchAdapter);
        attTopSearchAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onMoreClick()) {
                return;
            }
            SearchEntity entity = attTopSearchAdapter.getData().get(position);
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, entity.getNovel_id());
            startActivity(intent);
        });
    }

    private void initSpecialTopicView(LayoutInflater inflater, List<HpSpecialEntity> specialList) {
        if (headViewSpecial == null) {
            headViewSpecial = inflater.inflate(R.layout.layout_rv_head_sb_special_topic, recyclerView, false);
            mAdapter.setHeaderView(headViewSpecial, 3);
            $(headViewSpecial, R.id.tv_asBtn_special_topic_more).setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    Tos.showShort(mActivity, "点个锤子，这个模块没了！");
                }
            });
            $(headViewSpecial, R.id.tv_asBtn_special_topic_more).setVisibility(View.GONE);
            vpSpt = $(headViewSpecial, R.id.viewPager_special_topic);
            vpSpt.setPageMargin(DensityUtils.dp2px(mActivity, 16));
        }

        vpSpt.setOffscreenPageLimit(specialList.size());
        SpecialTopicPagerAdapter stPagerAdapter = new SpecialTopicPagerAdapter(getContext(), specialList);
        stPagerAdapter.setmOnPagerItemClickListener(bookID -> {
            if (onMoreClick()) {
                return;
            }
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, bookID);
            startActivity(intent);
        });
        vpSpt.setAdapter(stPagerAdapter);
    }

    private void initExcellentWorksView(LayoutInflater inflater, List<HpClassifyNvET> classifyNvList) {
        if (headViewExcellentWorks == null) {
            headViewExcellentWorks = inflater.inflate(R.layout.layout_rc_head_sb_classify_excellent_work, recyclerView, false);
            mAdapter.setHeaderView(headViewExcellentWorks, 2);
            $(headViewExcellentWorks, R.id.tv_asBtn_excellent_work_more).setOnClickListener(this);
            $(headViewExcellentWorks, R.id.tv_asBtn_excellent_work_more).setVisibility(View.GONE);
            vpExWorks = $(headViewExcellentWorks, R.id.viewPager_excellent_works);
            vpExWorks.setPageMargin(DensityUtils.dp2px(mActivity, 16));
        }

        List<Fragment> exwFragments = new ArrayList<>();
        for (int i = 0; i < classifyNvList.size(); i++) {
            exwFragments.add(ExWorksShowFargment.newInstance(classifyNvList.get(i)));
        }
        vpExWorks.setOffscreenPageLimit(exwFragments.size());
        vpExWorks.setAdapter(new MultiplePagerAdapter(getChildFragmentManager(), exwFragments));
    }

    private void initHotlistView(LayoutInflater inflater, List<BookEntity> rankNvList) {
        if (headViewHot == null) {
            headViewHot = inflater.inflate(R.layout.layout_rv_head_sb_hotlist_today, recyclerView, false);
            mAdapter.setHeaderView(headViewHot, 1);
            $(headViewHot, R.id.tv_asBtn_full_list).setOnClickListener(this);
            $(headViewHot, R.id.tv_asBtn_full_list).setVisibility(View.GONE);
            rvHot = $(headViewHot, R.id.recyclerView_hotlist);
            rvHot.setNestedScrollingEnabled(false);
            rvHot.setLayoutManager(new GridLayoutManager(mActivity, 2));
            rvHot.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtils.dp2px(mActivity, 16), false));
        }
        HotTodayAdapter hotTodayAdapter = new HotTodayAdapter(rankNvList);
        rvHot.setAdapter(hotTodayAdapter);
        hotTodayAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onMoreClick()) {
                return;
            }
            BookEntity entity = hotTodayAdapter.getData().get(position);
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
            startActivity(intent);
        });
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

        //设置图片集合(可先不设置,最后update)
//        mBanner.setImages(bannerDatas);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }

    private void initCenterBar(View rootView) {
        $(rootView, R.id.tv_asBtn_classify).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_ranking).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_new_book).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_book_finished).setOnClickListener(this);
    }

    private void reqIndexDatas(boolean isInit) {
        OkGo.<LzyResponse<HomePageEntity>>get(Consts.INDEX_PAGE_API)
                .params(Consts.TYPE, Consts.TYPE_SELECTION)
                .cacheKey(Consts.INDEX_PAGE_API + Consts.TYPE_SELECTION)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheTime(12 * 60 * 60 * 1000)
                .execute(new LtbJsonCallback<LzyResponse<HomePageEntity>>((AppCompatActivity) mActivity, isInit,
                        new TypeReference<LzyResponse<HomePageEntity>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<HomePageEntity>> response) {
                        LzyResponse<HomePageEntity> entity = response.body();
                        if (entity.error_code == 0) {
                            HomePageEntity hpDatas = entity.getData();
                            if (hpDatas == null) {
                                return;
                            }
                            LayoutInflater inflater = LayoutInflater.from(mActivity);
                            //今日大热榜
                            List<BookEntity> rankNvList = hpDatas.getRank_novel();
                            if (rankNvList != null && rankNvList.size() > 0) {
                                initHotlistView(inflater, rankNvList);
                            }

                            //分类佳作
                            List<HpClassifyNvET> classifyNvList = hpDatas.getClassify_novel();
                            if (classifyNvList != null && classifyNvList.size() > 0) {
                                initExcellentWorksView(inflater, classifyNvList);
                            }

                            //专题
                            List<HpSpecialEntity> specialList = hpDatas.getSpecial();
                            initSpecialTopicView(inflater, specialList);

                            //实时热搜
                            List<SearchEntity> searchNvList = hpDatas.getSearch_novel();
                            if (searchNvList != null && searchNvList.size() > 0) {
                                initTopSearchView(inflater, searchNvList);
                            }

                            //新书抢先
                            List<HpNewBookET> newOrigNvList = hpDatas.getNew_or_original();
                            if (newOrigNvList != null && newOrigNvList.size() > 0) {
                                // 取newOrigNvList.get(0)
                                HpNewBookET newBookET = newOrigNvList.get(0);
                                List<BookEntity> novelList = newBookET.getNovel();
                                initNewBooksLeadUpView(inflater, novelList);
                            }

                            //高分精选
                            if (headViewHighScore == null) {
                                headViewHighScore = inflater.inflate(R.layout.layout_rv_head_normal_title, recyclerView, false);
                                mAdapter.setHeaderView(headViewHighScore, 6);
                            }
                            List<BookEntity> hotNvdata = hpDatas.getHot_novel().getData();
                            mAdapter.setNewData(hotNvdata);

                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<LzyResponse<HomePageEntity>> response) {
                        super.onCacheSuccess(response);
                        if (isInit) {
                            onSuccess(response);
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<HomePageEntity>> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }

                });
    }

    /**
     * 高分精选
     */
    private void reqHighMarksDatas(int page) {
        OkGo.<String>post(Consts.NOVEL_POPULAR_API)
                .params(Consts.PAGE, page + 1)
                .params(Consts.TYPE, Consts.TYPE_SELECTION)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<BookEntity>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<BookEntity>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<BookEntity>> datas = entity.getData();
                            curPage = datas.getCurrent_page();
                            List<BookEntity> bookList = datas.getData();
                            if (bookList != null && bookList.size() > 1) {
                                mAdapter.addData(bookList);
                            }
                            if (datas.getLast_page() <= curPage) {
                                //没有下一页
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

    /**
     * banner
     */
    private void reqTopBannerData() {
        OkGo.<String>post(Consts.BANNER_READ_API)
                .params(Consts.POSITION, Consts.TYPE_SELECTION)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<BannerADEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BannerADEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            bannerDatas = entity.getData();
                            if (bannerDatas != null) {
                                mBanner.update(bannerDatas);
                            }
                        }
                    }
                });
    }


}
