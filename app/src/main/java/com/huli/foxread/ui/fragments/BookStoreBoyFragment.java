package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.GridSpanSizeLookup;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.engines.GlideImageLoader;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.entity.ExclusiveBookEntity;
import com.huli.foxread.entity.HighScoresEntity;
import com.huli.foxread.entity.HomePageBGEntity;
import com.huli.foxread.entity.HpSpecialEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.entity.multi.BookMultiEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.activities.BookRankingActivity;
import com.huli.foxread.ui.activities.ClassifyActivity;
import com.huli.foxread.ui.activities.EndBooksActivity;
import com.huli.foxread.ui.activities.NewBooksActivity;
import com.huli.foxread.ui.adapters.BooksGridAdapter;
import com.huli.foxread.ui.adapters.BooksHighScoreAdapter;
import com.huli.foxread.ui.adapters.BooksMultiItemAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.pageradapter.SpecialTopicPagerAdapter;
import com.huli.foxread.utils.ClickJumpUtil;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * 书城---男生（女生）
 */
public class BookStoreBoyFragment extends BaseFragment implements View.OnClickListener, OnBannerListener {

    private SmartRefreshLayout mRefreshLayout;
    public RecyclerView recyclerView;
    private BooksHighScoreAdapter mAdapter;

    private View headViewTop;
    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;

    private View headViewExclusive;
    private BooksGridAdapter exclAdapter;

    private View headViewPraiseNv;
    private RecyclerView rvPraiseNv;
    private BooksMultiItemAdapter praiseNvAdapter;

    private View headViewHighScore;

    private View headViewSpecial;
    private ViewPager vpSpt;

    private int mType;      //男生  女生

    private int curPage = 0;

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
        mAdapter = new BooksHighScoreAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);

        LayoutInflater inflater = LayoutInflater.from(mActivity);

        initTopView(inflater);

        initExclusiveView(inflater);

        headViewPraiseNv = inflater.inflate(R.layout.layout_rv_head_praise_good_books, recyclerView, false);
        mAdapter.addHeaderView(headViewPraiseNv);
        $(headViewPraiseNv, R.id.tv_asBtn_praise_good_refresh).setOnClickListener(this);
        $(headViewPraiseNv, R.id.tv_asBtn_praise_good_refresh).setVisibility(View.GONE);
        rvPraiseNv = $(headViewPraiseNv, R.id.recyclerView_praise_good_books);
        rvPraiseNv.setLayoutManager(new GridLayoutManager(mActivity, 4));

        initSpecialTopicView(inflater);

        //男生|女生都喜欢（高分精选）
        headViewHighScore = inflater.inflate(R.layout.layout_rv_head_normal_title, recyclerView, false);
        TextView tvTitle = $(headViewHighScore, R.id.tv_title_normal);
        if (mType == Consts.TYPE_BOY) {
            tvTitle.setText(R.string.txt_all_boys_love);
        } else if (mType == Consts.TYPE_GIRL) {
            tvTitle.setText(R.string.txt_all_girls_love);
        } else {
            tvTitle.setText(R.string.txt_high_score_well_chosen);
        }
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
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqGuessYouLikeDatas(curPage));

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            reqIndexDatas(false);

            reqGuessYouLikeDatas(0);
            //可以上拉加载
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private boolean isInitData;

    @Override
    public void onResume() {
        super.onResume();
        if (!isInitData) {
            isInitData = true;
            reqIndexDatas(true);

            reqGuessYouLikeDatas(0);
            Log.e("ssssssssssssss", "sssssssssssssss353132132199999999");
        }
    }

    //    @Override
//    protected void onFragmentFirstVisible() {
//        super.onFragmentFirstVisible();
//        reqIndexDatas(true);
//
//        reqGuessYouLikeDatas(0);
//
//        Log.e("ssssssssssssss", "sssssssssssssss3531321321");
//    }

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_classify:
                startActivity(new Intent(mActivity, ClassifyActivity.class));
                break;

            case R.id.tv_asBtn_ranking:
                Intent rankIntent = new Intent(mActivity, BookRankingActivity.class);
                rankIntent.putExtra(Consts.TYPE, mType);
                startActivity(rankIntent);
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

            case R.id.tv_asBtn_praise_good_refresh:             //换一换（好评佳作）
                Tos.showShort(mActivity, "换一换");
                break;

            default:
                break;
        }
    }

    /**
     * 专题
     */
    private void initSpecialTopicView(LayoutInflater inflater) {
        if (headViewSpecial == null) {
            headViewSpecial = inflater.inflate(R.layout.layout_rv_head_sb_special_topic, recyclerView, false);
            mAdapter.addHeaderView(headViewSpecial);
            $(headViewSpecial, R.id.tv_asBtn_special_topic_more).setVisibility(View.GONE);
            $(headViewSpecial, R.id.tv_asBtn_special_topic_more).setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    Tos.showShort(mActivity, "点个锤子，这个模块没了！");
                }
            });
            vpSpt = $(headViewSpecial, R.id.viewPager_special_topic);
            vpSpt.setPageMargin(DensityUtils.dp2px(mActivity, 16));
        }
    }

    /*private void initPraiseNvView(LayoutInflater inflater, List<HpBGPraiseNvET> praiseNvList) {
        for (int i = 0; i < praiseNvList.size(); i++) {
            HpBGPraiseNvET bgPraiseNvET = praiseNvList.get(i);

            View headViewPraiseGood = inflater.inflate(R.layout.layout_rv_head_praise_good_books, recyclerView, false);
            mAdapter.setHeaderView(headViewPraiseGood, i + 1);
            $(headViewPraiseGood, R.id.tv_asBtn_praise_good_refresh).setOnClickListener(this);
            $(headViewPraiseGood, R.id.tv_asBtn_praise_good_refresh).setTag(bgPraiseNvET.getId());
            $(headViewPraiseGood, R.id.tv_asBtn_praise_good_refresh).setVisibility(View.GONE);
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
                        entity.setId(bookEntity.getId());
                        entity.setAuthor(bookEntity.getAuthor());
                        entity.setName(bookEntity.getName());
                        entity.setScore(bookEntity.getScore());
                        entity.setIntroduce(bookEntity.getIntroduce());
                        entity.setHttp_image(bookEntity.getHttp_image());
                        entity.setWord(bookEntity.getWord());
                        datas.add(entity);
                    } else {
                        entity = new BookMultiEntity();
                        entity.setItemType(BookMultiEntity.ITEM_FIRST);
                        entity.setSpanSize(BookMultiEntity.SPAN_SIZE_1);
                        entity.setId(bookEntity.getId());
                        entity.setAuthor(bookEntity.getAuthor());
                        entity.setName(bookEntity.getName());
                        entity.setScore(bookEntity.getScore());
                        entity.setIntroduce(bookEntity.getIntroduce());
                        entity.setHttp_image(bookEntity.getHttp_image());
                        entity.setWord(bookEntity.getWord());
                        datas.add(entity);
                    }
                } else {
                    entity = new BookMultiEntity();
                    entity.setItemType(BookMultiEntity.ITEM_FIRST);
                    entity.setSpanSize(BookMultiEntity.SPAN_SIZE_1);
                    entity.setId(bookEntity.getId());
                    entity.setAuthor(bookEntity.getAuthor());
                    entity.setName(bookEntity.getName());
                    entity.setScore(bookEntity.getScore());
                    entity.setIntroduce(bookEntity.getIntroduce());
                    entity.setHttp_image(bookEntity.getHttp_image());
                    entity.setWord(bookEntity.getWord());
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
            lubAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (onMoreClick()) {
                    return;
                }
                BookMultiEntity data = lubAdapter.getData().get(position);
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, data.getId());
                startActivity(intent);
            });
        }
    }*/

    /**
     * 全网独家，重磅推荐
     *
     * @param inflater
     */
    private void initExclusiveView(LayoutInflater inflater) {
        headViewExclusive = inflater.inflate(R.layout.layout_rv_head_exclusive_recommend_books, recyclerView, false);
        mAdapter.addHeaderView(headViewExclusive);
        $(headViewExclusive, R.id.tv_asBtn_exclusive_recommend_refresh).setOnClickListener(this);
        $(headViewExclusive, R.id.tv_asBtn_exclusive_recommend_refresh).setVisibility(View.GONE);

        RecyclerView rvExclusive = $(headViewExclusive, R.id.recyclerView_exclusive_recommend);
        rvExclusive.setNestedScrollingEnabled(false);
        rvExclusive.setLayoutManager(new GridLayoutManager(mActivity, 4));
        rvExclusive.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(mActivity, 16), true));
        exclAdapter = new BooksGridAdapter();
        rvExclusive.setAdapter(exclAdapter);
        exclAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ExclusiveBookEntity bookEntity = exclAdapter.getData().get(position);
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, bookEntity.getNovel_id());
                startActivity(intent);
            }
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

        //设置图片集合
//        mBanner.setImages(bannerADs);
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
     * 全网独家，重磅推荐
     */
    private void setExclusiveBookData(HomePageBGEntity hpDatas) {
        List<ExclusiveBookEntity> praises = hpDatas.getPraise();
        if (praises != null && praises.size() > 0) {
            exclAdapter.setNewData(praises);
            headViewExclusive.setVisibility(View.VISIBLE);
        } else {
            headViewExclusive.setVisibility(View.GONE);
        }
    }


    /**
     * 好评佳作
     */
    private void setPraiseNvData(HomePageBGEntity hpDatas) {
        List<BookMultiEntity> favourableList = hpDatas.getFavourable();
        if (favourableList != null && favourableList.size() > 0) {
            favourableList.get(0).setItemType(BookMultiEntity.ITEM_FIRST);
            favourableList.get(0).setSpanSize(BookMultiEntity.SPAN_SIZE_4);

            praiseNvAdapter = new BooksMultiItemAdapter(favourableList);
            praiseNvAdapter.setGridSpanSizeLookup(new GridSpanSizeLookup() {
                @Override
                public int getSpanSize(GridLayoutManager gridLayoutManager, int viewType, int position) {
                    return praiseNvAdapter.getData().get(position).getSpanSize();
                }
            });
            rvPraiseNv.setAdapter(praiseNvAdapter);
            praiseNvAdapter.setOnItemClickListener((adapter, view, position) -> {
                BookMultiEntity multiEntity = praiseNvAdapter.getData().get(position);
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, multiEntity.getNovel_id());
                startActivity(intent);
            });
            headViewPraiseNv.setVisibility(View.VISIBLE);
        } else {
            headViewPraiseNv.setVisibility(View.GONE);
        }
    }

    /**
     * 专题
     */
    private void setSpecialTopData(HomePageBGEntity hpDatas) {
        List<HpSpecialEntity> specialList = hpDatas.getSpecial();
        if (specialList != null && specialList.size() > 0) {
            vpSpt.setOffscreenPageLimit(specialList.size());
            SpecialTopicPagerAdapter stPagerAdapter = new SpecialTopicPagerAdapter(getContext(), specialList);
            stPagerAdapter.setmOnPagerItemClickListener(bookID -> {
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
     * 各个模块数据（除了底部猜你喜欢）
     */
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
                            if (hpDatas == null) {
                                return;
                            }

                            //轮播图
                            bannerDatas = hpDatas.getBanner();
                            if (bannerDatas != null) {
                                mBanner.update(bannerDatas);
                            }

                            //全网独家，重磅推荐
                            setExclusiveBookData(hpDatas);

                            //好评佳作
                            setPraiseNvData(hpDatas);

                            //专题
                            setSpecialTopData(hpDatas);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }
                });
    }


    /**
     * 猜你喜欢
     */
    private void reqGuessYouLikeDatas(int prePage) {
        OkGo.<String>post(Consts.PREFER_READ_API)
                .params(Consts.PAGE, prePage + 1)
                .params(Consts.TYPE, mType)
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
                .params(Consts.POSITION, mType)
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
