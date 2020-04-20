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
import com.huli.foxread.entity.ForestallNewEntity;
import com.huli.foxread.entity.GemGroupEntity;
import com.huli.foxread.entity.HighScoresEntity;
import com.huli.foxread.entity.HomePageEntity;
import com.huli.foxread.entity.HotSearchEntity;
import com.huli.foxread.entity.RankBookEntity;
import com.huli.foxread.entity.HpSpecialEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.entity.multi.ForestallNewMultiEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.activities.BookRankingActivity;
import com.huli.foxread.ui.activities.ClassifyActivity;
import com.huli.foxread.ui.activities.EndBooksActivity;
import com.huli.foxread.ui.activities.NewBooksActivity;
import com.huli.foxread.ui.adapters.AttTopSearchAdapter;
import com.huli.foxread.ui.adapters.BooksHighScoreAdapter;
import com.huli.foxread.ui.adapters.ForestBookMultiItemAdapter;
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

/**
 * 书城---精选
 */
public class BookStoreSelectionFragment extends BaseFragment implements View.OnClickListener, OnBannerListener {

    private SmartRefreshLayout mRefreshLayout;
    public RecyclerView recyclerView;
    private BooksHighScoreAdapter mAdapter;

    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;

    private View headViewTop;

    private View headViewHot;
    private RecyclerView rvHot;
    private HotTodayAdapter hotTodayAdapter;

    private View headViewExcellentWorks;
    private ViewPager vpExWorks;

    private View headViewSpecial;
    private ViewPager vpSpt;
    private SpecialTopicPagerAdapter stPagerAdapter;

    private View headViewTopSearch;
    private RecyclerView rvTopSearch;
    private AttTopSearchAdapter attTopSearchAdapter;

    private View headViewNewBooks;
    private RecyclerView rvLeadUpBooks;
    private ForestBookMultiItemAdapter lubAdapter;

    private int curPage = 0;

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
        mAdapter = new BooksHighScoreAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
        mAdapter.setHeaderWithEmptyEnable(true);

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        initTopView(inflater);

        initHotlistView(inflater);

        initExcellentWorksView(inflater);

        initSpecialTopicView(inflater);

        initTopSearchView(inflater);

        initNewBooksLeadUpView(inflater);

        View headViewHighScore = inflater.inflate(R.layout.layout_rv_head_normal_title, recyclerView, false);
        mAdapter.addHeaderView(headViewHighScore);
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onMoreClick()) {
                return;
            }
            HighScoresEntity entity = mAdapter.getData().get(position);
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, entity.getNovel_id());
            startActivity(intent);
        });
        // 设置加载更多监听事件
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqHighMarksDatas(curPage));

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            reqIndexDatas(false);

            reqHighMarksDatas(0);

            //可以上拉加载
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        reqIndexDatas(true);

        reqHighMarksDatas(0);
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
            ClickJumpUtil.handleJump(mActivity, entity.getLink(), entity.getJump());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_asBtn_classify:
                startActivity(new Intent(mActivity, ClassifyActivity.class));
                break;

            case R.id.tv_asBtn_ranking:
                Intent rankIntent = new Intent(mActivity, BookRankingActivity.class);
                rankIntent.putExtra(Consts.TYPE, Consts.TYPE_SELECTION);
                startActivity(rankIntent);
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


    /**
     * 新书抢先
     */
    private void initNewBooksLeadUpView(LayoutInflater inflater) {
        headViewNewBooks = inflater.inflate(R.layout.layout_rv_head_multiitem_books, recyclerView, false);
        mAdapter.addHeaderView(headViewNewBooks);
        $(headViewNewBooks, R.id.tv_asBtn_newBooks_refresh).setOnClickListener(this);
        $(headViewNewBooks, R.id.tv_asBtn_newBooks_refresh).setVisibility(View.GONE);
        rvLeadUpBooks = $(headViewNewBooks, R.id.recyclerView_new_books);
        rvLeadUpBooks.setLayoutManager(new GridLayoutManager(mActivity, 4));
    }

    /**
     * 实时热搜
     */
    private void initTopSearchView(LayoutInflater inflater) {
        headViewTopSearch = inflater.inflate(R.layout.layout_rv_head_sb_actual_time_top_search, recyclerView, false);
        mAdapter.addHeaderView(headViewTopSearch);
        $(headViewTopSearch, R.id.tv_asBtn_get_a_new_batch_top_search).setOnClickListener(this);
        $(headViewTopSearch, R.id.tv_asBtn_get_a_new_batch_top_search).setVisibility(View.GONE);
        rvTopSearch = $(headViewTopSearch, R.id.recyclerView_top_search);
        rvTopSearch.setNestedScrollingEnabled(false);
        rvTopSearch.setLayoutManager(new GridLayoutManager(mActivity, 4));
        rvTopSearch.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(mActivity, 16), false));
        attTopSearchAdapter = new AttTopSearchAdapter();
        rvTopSearch.setAdapter(attTopSearchAdapter);
        attTopSearchAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onMoreClick()) {
                return;
            }
            HotSearchEntity hotSearchEntity = attTopSearchAdapter.getData().get(position);
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, hotSearchEntity.getNovel_id());
            startActivity(intent);
        });
    }

    /**
     * 专题
     */
    private void initSpecialTopicView(LayoutInflater inflater) {
        headViewSpecial = inflater.inflate(R.layout.layout_rv_head_sb_special_topic, recyclerView, false);
        mAdapter.addHeaderView(headViewSpecial);
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

    /**
     * 分类佳作
     */
    private void initExcellentWorksView(LayoutInflater inflater) {
        headViewExcellentWorks = inflater.inflate(R.layout.layout_rc_head_sb_classify_excellent_work, recyclerView, false);
        mAdapter.addHeaderView(headViewExcellentWorks);
        $(headViewExcellentWorks, R.id.tv_asBtn_excellent_work_more).setOnClickListener(this);
        $(headViewExcellentWorks, R.id.tv_asBtn_excellent_work_more).setVisibility(View.GONE);
        vpExWorks = $(headViewExcellentWorks, R.id.viewPager_excellent_works);
        vpExWorks.setPageMargin(DensityUtils.dp2px(mActivity, 16));
    }

    /**
     * 今日大热榜
     */
    private void initHotlistView(LayoutInflater inflater) {
        headViewHot = inflater.inflate(R.layout.layout_rv_head_sb_hotlist_today, recyclerView, false);
        mAdapter.addHeaderView(headViewHot);
        $(headViewHot, R.id.tv_asBtn_full_list).setOnClickListener(this);
        $(headViewHot, R.id.tv_asBtn_full_list).setVisibility(View.GONE);
        rvHot = $(headViewHot, R.id.recyclerView_hotlist);
        rvHot.setNestedScrollingEnabled(false);
        rvHot.setLayoutManager(new GridLayoutManager(mActivity, 2));
        rvHot.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtils.dp2px(mActivity, 16), false));

        hotTodayAdapter = new HotTodayAdapter();
        rvHot.setAdapter(hotTodayAdapter);
        hotTodayAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onMoreClick()) {
                return;
            }
            RankBookEntity hotToday = hotTodayAdapter.getData().get(position);
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, hotToday.getNovel_id());
            startActivity(intent);
        });
    }

    /**
     * 顶部headerView
     */
    private void initTopView(LayoutInflater inflater) {
        headViewTop = inflater.inflate(R.layout.layout_rv_head_sb_top, recyclerView, false);
        mAdapter.addHeaderView(headViewTop);
        initBannerView(headViewTop);
        initCenterBar(headViewTop);
    }

    /**
     * 轮播广告
     */
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
        mBanner.setDelayTime(4200);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setOnBannerListener(this);

        //设置图片集合(可先不设置,最后update)
//        mBanner.setImages(bannerDatas);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }

    /**
     * 中间导航栏
     */
    private void initCenterBar(View rootView) {
        $(rootView, R.id.tv_asBtn_classify).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_ranking).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_new_book).setOnClickListener(this);
        $(rootView, R.id.tv_asBtn_book_finished).setOnClickListener(this);
    }


    /**
     * 抢先新书数据
     */
    private void setForestallBookDatas(HomePageEntity hpDatas) {
        List<ForestallNewEntity> newOrigNvList = hpDatas.getPrior();
        if (newOrigNvList != null && newOrigNvList.size() > 0) {
            List<ForestallNewMultiEntity> datas = new ArrayList<>();
            ForestallNewMultiEntity fnMulEntity;
            for (int i = 0; i < newOrigNvList.size(); i++) {
                fnMulEntity = new ForestallNewMultiEntity();
                ForestallNewEntity bookEntity = newOrigNvList.get(i);
                if (i == 0) {
                    fnMulEntity.setItemType(ForestallNewMultiEntity.DETAILED);
                    fnMulEntity.setSpanSize(ForestallNewMultiEntity.SPAN_SIZE_4);
                    fnMulEntity.setId(bookEntity.getId());
                    fnMulEntity.setNovel_id(bookEntity.getNovel_id());
                    fnMulEntity.setStatus(bookEntity.getStatus());
                    fnMulEntity.setAuthor(bookEntity.getAuthor());
                    fnMulEntity.setHttp_image(bookEntity.getHttp_image());
                    fnMulEntity.setIntroduce(bookEntity.getIntroduce());
                    fnMulEntity.setIs_end(bookEntity.getIs_end());
                    fnMulEntity.setNovel_name(bookEntity.getNovel_name());
                    fnMulEntity.setScore(bookEntity.getScore());
                    fnMulEntity.setWord(bookEntity.getWord());
                    datas.add(fnMulEntity);
                } else {
                    fnMulEntity.setItemType(ForestallNewMultiEntity.ITEM_FIRST);
                    fnMulEntity.setSpanSize(ForestallNewMultiEntity.SPAN_SIZE_1);
                    fnMulEntity.setId(bookEntity.getId());
                    fnMulEntity.setNovel_id(bookEntity.getNovel_id());
                    fnMulEntity.setStatus(bookEntity.getStatus());
                    fnMulEntity.setAuthor(bookEntity.getAuthor());
                    fnMulEntity.setHttp_image(bookEntity.getHttp_image());
                    fnMulEntity.setIntroduce(bookEntity.getIntroduce());
                    fnMulEntity.setIs_end(bookEntity.getIs_end());
                    fnMulEntity.setNovel_name(bookEntity.getNovel_name());
                    fnMulEntity.setScore(bookEntity.getScore());
                    fnMulEntity.setWord(bookEntity.getWord());
                    datas.add(fnMulEntity);
                }
            }
            lubAdapter = new ForestBookMultiItemAdapter(datas);
            lubAdapter.setGridSpanSizeLookup(new GridSpanSizeLookup() {
                @Override
                public int getSpanSize(GridLayoutManager gridLayoutManager, int viewType, int position) {
                    return lubAdapter.getData().get(position).getSpanSize();
                }
            });
            rvLeadUpBooks.setAdapter(lubAdapter);
            lubAdapter.setOnItemClickListener((adapter, view, position) -> {
                ForestallNewMultiEntity multiEntity = lubAdapter.getData().get(position);
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, multiEntity.getNovel_id());
                startActivity(intent);
            });
            headViewNewBooks.setVisibility(View.VISIBLE);
        } else {
            headViewNewBooks.setVisibility(View.GONE);
        }
    }

    /**
     * 实时热搜数据
     */
    private void setHotSearchDatas(HomePageEntity hpDatas) {
        List<HotSearchEntity> searchNvList = hpDatas.getHot();
        if (searchNvList != null && searchNvList.size() > 0) {
            attTopSearchAdapter.setNewData(searchNvList);
            headViewTopSearch.setVisibility(View.VISIBLE);
        } else {
            headViewTopSearch.setVisibility(View.GONE);
        }
    }

    /**
     * 专题数据
     */
    private void setSpecialTopDatas(HomePageEntity hpDatas) {
        List<HpSpecialEntity> specialList = hpDatas.getSpecial();
        if (specialList != null && specialList.size() > 0) {
            vpSpt.setOffscreenPageLimit(specialList.size());
            stPagerAdapter = new SpecialTopicPagerAdapter(getContext(), specialList);
            stPagerAdapter.setmOnPagerItemClickListener(bookID -> {
                if (onMoreClick()) {
                    return;
                }
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, bookID);
                startActivity(intent);
            });
            vpSpt.setAdapter(stPagerAdapter);
            headViewSpecial.setVisibility(View.VISIBLE);
        } else {
            headViewSpecial.setVisibility(View.GONE);
        }
    }

    /**
     * 分类佳作数据
     */
    private void setGemDatas(HomePageEntity hpDatas) {
        List<GemGroupEntity> classifyNvList = hpDatas.getPoems();
        if (classifyNvList != null && classifyNvList.size() > 0) {
            List<Fragment> exwFragments = new ArrayList<>();
            for (int i = 0; i < classifyNvList.size(); i++) {
                exwFragments.add(ExWorksShowFargment.newInstance(classifyNvList.get(i)));
            }
            vpExWorks.setOffscreenPageLimit(exwFragments.size());
            vpExWorks.setAdapter(new MultiplePagerAdapter(getChildFragmentManager(), exwFragments));
            headViewExcellentWorks.setVisibility(View.VISIBLE);
        } else {
            headViewExcellentWorks.setVisibility(View.GONE);
        }
    }

    /**
     * 今日大热榜数据
     */
    private void setHotTodayDatas(HomePageEntity hpDatas) {
        List<RankBookEntity> rankNvList = hpDatas.getToday();
        if (rankNvList != null && rankNvList.size() > 0) {
            hotTodayAdapter.setNewData(rankNvList);
            headViewHot.setVisibility(View.VISIBLE);
        } else {
            headViewHot.setVisibility(View.GONE);
        }
    }


    /**
     * 各个模块数据（除了底部高分精选）
     *
     * @param isInit 进入页面初次加载
     */
    private void reqIndexDatas(boolean isInit) {
        OkGo.<LzyResponse<HomePageEntity>>post(Consts.INDEX_PAGE_API)
                .params(Consts.TYPE, Consts.TYPE_SELECTION)
                .cacheKey(Consts.INDEX_PAGE_API + Consts.TYPE_SELECTION)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheTime(24 * 60 * 60 * 1000)
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

                            //轮播图
                            bannerDatas = hpDatas.getBanner();
                            if (bannerDatas != null) {
                                mBanner.update(bannerDatas);
                            }

                            //今日大热榜
                            setHotTodayDatas(hpDatas);

                            //分类佳作
                            setGemDatas(hpDatas);

                            //专题
                            setSpecialTopDatas(hpDatas);

                            //实时热搜
                            setHotSearchDatas(hpDatas);

                            //新书抢先
                            setForestallBookDatas(hpDatas);
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
     *
     * @param prePage 上一页页码
     */
    private void reqHighMarksDatas(int prePage) {
        OkGo.<String>get(Consts.NOVEL_POPULAR_API)
                .params(Consts.PAGE, prePage + 1)
                .params(Consts.TYPE, Consts.TYPE_SELECTION)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<HighScoresEntity>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<HighScoresEntity>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<HighScoresEntity>> datas = entity.getData();
                            curPage = datas.getCurrent_page();
                            List<HighScoresEntity> bookList = datas.getData();
                            if (curPage == 1) {
                                mAdapter.setNewData(bookList);
                            } else {
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
