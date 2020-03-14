package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.GridSpanSizeLookup;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.engines.GlideImageLoader;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.BookMultiEntity;
import com.huli.foxread.entity.HomePageBGEntity;
import com.huli.foxread.entity.HpBGPraiseNvET;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.activities.BookRankingActivity;
import com.huli.foxread.ui.activities.ClassifyActivity;
import com.huli.foxread.ui.activities.EndBooksActivity;
import com.huli.foxread.ui.activities.NewBooksActivity;
import com.huli.foxread.ui.adapters.BooksGridAdapter;
import com.huli.foxread.ui.adapters.BooksListAdapter;
import com.huli.foxread.ui.adapters.BooksMultiItemAdapter;
import com.huli.foxread.ui.base.LazyLoadFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.pageradapter.SpecialTopicPagerAdapter;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class BookStoreBoyFragment extends LazyLoadFragment implements View.OnClickListener, OnBannerListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private BooksListAdapter mAdapter;

    private Banner mBanner;

    private View headViewHighScore;

    private View headViewSpecial;
    private ViewPager vpSpt;

    private int mType;      //男生  女生  图书

    public static BookStoreBoyFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.TYPE, type);
        BookStoreBoyFragment mFragment = new BookStoreBoyFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }


    @Override
    public int bindLayout() {
        return R.layout.fragment_bookstore_general_refresh_recy;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        mType = getArguments().getInt(Consts.TYPE);

        mRefreshLayout = $(view, R.id.smartRefreshLayout_book_store);

        recyclerView = $(view, R.id.recyclerView_book_store);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new BooksListAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View headViewTop = inflater.inflate(R.layout.layout_rv_head_sb_top, recyclerView, false);
        mAdapter.addHeaderView(headViewTop, 0);
        initBannerView(headViewTop);
        initCenterBar(headViewTop);


    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            BookEntity entity = mAdapter.getData().get(position);
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
            startActivity(intent);
        });
        // 设置加载更多监听事件
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
//                loadMore();
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reqIndexDatas(false);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
//        reqIndexDatas(true);
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_classify:
                startActivity(new Intent(mActivity, ClassifyActivity.class));
                break;

            case R.id.tv_asBtn_ranking:
                startActivity(new Intent(mActivity, BookRankingActivity.class));
                break;

            case R.id.tv_asBtn_new_book:
                Intent intent = new Intent(mActivity, NewBooksActivity.class);
                intent.putExtra(Consts.TYPE, mType);
                startActivity(intent);
                break;

            case R.id.tv_asBtn_book_finished:
                Intent ebIntent = new Intent(mActivity, EndBooksActivity.class);
                ebIntent.putExtra(Consts.TYPE, mType);
                startActivity(ebIntent);
                break;

            case R.id.tv_asBtn_praise_good_refresh:             //换一换
                Tos.showShort(mActivity, "换一换===" + view.getTag());
                break;

            default:
                break;
        }
    }

    private void initSpecialTopicView(LayoutInflater inflater) {
        if (headViewSpecial == null) {
            headViewSpecial = inflater.inflate(R.layout.layout_rv_head_sb_special_topic, recyclerView, false);
            mAdapter.setHeaderView(headViewSpecial, 8);
            $(headViewSpecial, R.id.tv_asBtn_special_topic_more).setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    Tos.showShort(mActivity, "点个锤子，这个模块没了！");
                }
            });
            vpSpt = $(headViewSpecial, R.id.viewPager_special_topic);
            vpSpt.setPageMargin(DensityUtils.dp2px(mActivity, 16));
        }

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(i);
        }
        vpSpt.setOffscreenPageLimit(list.size());
        vpSpt.setAdapter(new SpecialTopicPagerAdapter(getContext(), list));
    }

    private void initPraiseNvView(LayoutInflater inflater, List<HpBGPraiseNvET> praiseNvList) {
        for (int i = 0; i < praiseNvList.size(); i++) {
            HpBGPraiseNvET bgPraiseNvET = praiseNvList.get(i);

            View headViewPraiseGood = inflater.inflate(R.layout.layout_rv_head_praise_good_books, recyclerView, false);
            mAdapter.setHeaderView(headViewPraiseGood, i + 1);
            $(headViewPraiseGood, R.id.tv_asBtn_praise_good_refresh).setOnClickListener(this);
            $(headViewPraiseGood, R.id.tv_asBtn_praise_good_refresh).setTag(bgPraiseNvET.getId());
            TextView tvTitle = $(headViewPraiseGood, R.id.tv_title_bar_praise_good);
            tvTitle.setText(bgPraiseNvET.getName());
            RecyclerView rvLeadUpBooks = $(headViewPraiseGood, R.id.recyclerView_praise_good_books);

            int layout = bgPraiseNvET.getLayout();      //布局样式

            List<BookEntity> novelList = bgPraiseNvET.getNovel();
            List<BookMultiEntity> datas = new ArrayList<>();
            BookMultiEntity entity;
            for (int p = 0; p < novelList.size(); p++) {
                entity = new BookMultiEntity();
                BookEntity bookEntity = novelList.get(p);
                if (layout == 1) {      //多布局
                    if (p == 0) {
                        entity.setItemType(BookMultiEntity.DETAILED);
                        entity.setSpanSize(BookMultiEntity.SPAN_SIZE_4);
                        entity.setAuthor(bookEntity.getAuthor());
                        entity.setName(bookEntity.getName());
                        entity.setScore(bookEntity.getScore());
                        entity.setIntroduce(bookEntity.getIntroduce());
                        entity.setHttp_image(bookEntity.getHttp_image());
                        datas.add(entity);
                    } else {
                        entity = new BookMultiEntity();
                        entity.setItemType(BookMultiEntity.SUCCINCT);
                        entity.setSpanSize(BookMultiEntity.SPAN_SIZE_1);
                        entity.setAuthor(bookEntity.getAuthor());
                        entity.setName(bookEntity.getName());
                        entity.setScore(bookEntity.getScore());
                        entity.setIntroduce(bookEntity.getIntroduce());
                        entity.setHttp_image(bookEntity.getHttp_image());
                        datas.add(entity);
                    }
                } else {
                    entity = new BookMultiEntity();
                    entity.setItemType(BookMultiEntity.SUCCINCT);
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
            rvLeadUpBooks.setLayoutManager(new GridLayoutManager(mActivity, 4));
            lubAdapter.setGridSpanSizeLookup(new GridSpanSizeLookup() {
                @Override
                public int getSpanSize(GridLayoutManager gridLayoutManager, int viewType, int position) {
                    return datas.get(position).getSpanSize();
                }
            });
            rvLeadUpBooks.setAdapter(lubAdapter);
            lubAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    BookMultiEntity data = lubAdapter.getData().get(position);
                    Tos.showShort(mActivity, "书的名字===" + data.getName());
                    Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                    intent.putExtra(Common.KEY_BOOK_ID, data.getId());
                    startActivity(intent);
            }
            });
        }
    }

    private void initExclusiveView(View rootView) {
        $(rootView, R.id.tv_asBtn_exclusive_recommend_refresh).setOnClickListener(this);

        RecyclerView rvTopSearch = $(rootView, R.id.recyclerView_exclusive_recommend);
        rvTopSearch.setNestedScrollingEnabled(false);
        rvTopSearch.setLayoutManager(new GridLayoutManager(mActivity, 4));
        rvTopSearch.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(mActivity, 16), true));
        BooksGridAdapter bgAdapter = new BooksGridAdapter();
        rvTopSearch.setAdapter(bgAdapter);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add("sssssssssss");
        }
        bgAdapter.setNewData(list);
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


    private void reqIndexDatas(boolean showDialog) {
        OkGo.<LzyResponse<HomePageBGEntity>>get(Consts.INDEX_PAGE_API)
                .params(Consts.TYPE, mType)
                .execute(new LtbJsonCallback<LzyResponse<HomePageBGEntity>>((AppCompatActivity) mActivity, showDialog,
                        new TypeReference<LzyResponse<HomePageBGEntity>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<HomePageBGEntity>> response) {
                        LzyResponse<HomePageBGEntity> entity = response.body();
                        if (entity.error_code == 0) {
                            HomePageBGEntity hpDatas = entity.getData();
                            LayoutInflater inflater = LayoutInflater.from(mActivity);

                            List<HpBGPraiseNvET> praiseNvList = hpDatas.getPraise_novel();
                            if (praiseNvList != null && praiseNvList.size() > 0) {
                                initPraiseNvView(inflater, praiseNvList);
                            }

                            initSpecialTopicView(inflater);

                            //男生|女生都喜欢（高分精选）
                            if (headViewHighScore == null) {
                                headViewHighScore = inflater.inflate(R.layout.layout_rv_head_normal_title, recyclerView, false);
                                TextView tvTitle = $(headViewHighScore, R.id.tv_title_normal);
                                if (mType == Consts.TYPE_BOY) {
                                    tvTitle.setText(R.string.txt_all_boys_love);
                                } else if (mType == Consts.TYPE_GIRL) {
                                    tvTitle.setText(R.string.txt_all_girls_love);
                                } else {
                                    tvTitle.setText(R.string.txt_high_score_well_chosen);
                                }
                                mAdapter.setHeaderView(headViewHighScore, 9);
                            }
                            List<BookEntity> hotNvdata = hpDatas.getHot_novel().getData();
                            mAdapter.setNewData(hotNvdata);

                        }
                    }


                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }

                });
    }

}
