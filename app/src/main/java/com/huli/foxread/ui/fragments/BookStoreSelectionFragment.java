package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.listener.GridSpanSizeLookup;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.EditorRecoEntity;
import com.huli.foxread.entity.ForestallNewEntity;
import com.huli.foxread.entity.GemEntity;
import com.huli.foxread.entity.HighScoresEntity;
import com.huli.foxread.entity.HomePageEntity;
import com.huli.foxread.entity.HotSearchEntity;
import com.huli.foxread.entity.HpSpecialEntity;
import com.huli.foxread.entity.RankBookEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.entity.multi.ForestallNewMultiEntity;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.adapters.AttTopSearchAdapter;
import com.huli.foxread.ui.adapters.BooksHighScoreAdapter;
import com.huli.foxread.ui.adapters.ForestBookMultiItemAdapter;
import com.huli.foxread.ui.adapters.HotTodayAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.pageradapter.MultiplePagerAdapter2;
import com.huli.foxread.ui.pageradapter.SpecialTopicPagerAdapter;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * 书城---精选
 */
public class BookStoreSelectionFragment extends BaseFragment implements View.OnClickListener {

    private SmartRefreshLayout mRefreshLayout;
    public RecyclerView recyclerView;
    private BooksHighScoreAdapter mAdapter;

    private View headViewTop;
    private ImageView ivRecoBookLeft, ivRecoBookCenter, ivRecoBookRight;
    private TextView tvRecoBookNameLeft, tvRecoBookNameCenter, tvRecoBookNameRight;

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

    //改变tabLayout背景色的临界高度
    private int criticalHeight = 0;
    //竖直方向一共滚动的距离
    private int totalScrollY = 0;

    public static BookStoreSelectionFragment newInstance(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        BookStoreSelectionFragment mFragment = new BookStoreSelectionFragment();
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
        int index = getArguments().getInt("index");
        // 这个设置tag要与FragmentPagerAdapter中的获取方法getItemPosition方法要对应上
        view.setTag(index);

        mRefreshLayout = $(view, R.id.smartRefreshLayout_book_store);

        recyclerView = $(view, R.id.recyclerView_book_store);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new BooksHighScoreAdapter();
        mAdapter.setAnimationEnable(true);
        mAdapter.setAnimationFirstOnly(false);
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


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrollY += dy;
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof MainBookstoreFragment) {
                    MainBookstoreFragment mainBookstoreFragment = (MainBookstoreFragment) parentFragment;
                    if (totalScrollY >= criticalHeight && !mainBookstoreFragment.isBleach) {
                        mainBookstoreFragment.childCtrlTabBleach();
                    } else if (totalScrollY < criticalHeight && mainBookstoreFragment.isBleach) {
                        mainBookstoreFragment.childCtrlTabRestore();
                    }
                }
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        reqIndexDatas(true);

        reqHighMarksDatas(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_editor_recommend_book_left:
            case R.id.iv_editor_recommend_book_center:
            case R.id.iv_editor_recommend_book_right:
                String novelId = (String) v.getTag();
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, novelId);
                startActivity(intent);
                break;

            case R.id.tv_asBtn_full_list:       //今日大热榜 -> 完整榜单
                Tos.showShort(mActivity, "今日大热榜 -> 完整榜单");
                break;

            case R.id.tv_asBtn_excellent_work_more:       //分类佳作 -> 更多
                Tos.showShort(mActivity, "分类佳作 -> 更多");
                break;

            case R.id.tv_asBtn_special_topic_more:       //专题 -> 更多
                Tos.showShort(mActivity, "专题 -> 更多");
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
        $(headViewSpecial, R.id.tv_asBtn_special_topic_more).setOnClickListener(this);
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
        rvHot.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtils.dp2px(mActivity, 16), true));

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
        headViewTop = inflater.inflate(R.layout.layout_rv_head_editors_recommend, recyclerView, false);
        mAdapter.addHeaderView(headViewTop);
        ivRecoBookLeft = headViewTop.findViewById(R.id.iv_editor_recommend_book_left);
        ivRecoBookCenter = headViewTop.findViewById(R.id.iv_editor_recommend_book_center);
        ivRecoBookRight = headViewTop.findViewById(R.id.iv_editor_recommend_book_right);
        tvRecoBookNameLeft = headViewTop.findViewById(R.id.tv_editor_recommend_book_name_left);
        tvRecoBookNameCenter = headViewTop.findViewById(R.id.tv_editor_recommend_book_name_center);
        tvRecoBookNameRight = headViewTop.findViewById(R.id.tv_editor_recommend_book_name_right);

        ivRecoBookLeft.setOnClickListener(this);
        ivRecoBookCenter.setOnClickListener(this);
        ivRecoBookRight.setOnClickListener(this);

        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        headViewTop.measure(width, height);
        criticalHeight = headViewTop.getMeasuredHeight();
        headViewTop.post(() -> criticalHeight = headViewTop.getHeight());
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
        List<GemEntity> classifyNvList = hpDatas.getPoems();
        if (classifyNvList != null && classifyNvList.size() > 0) {
            vpExWorks.setOffscreenPageLimit(classifyNvList.size());
            MultiplePagerAdapter2 exPagerAdapter2 = new MultiplePagerAdapter2(getContext(), classifyNvList);
            vpExWorks.setAdapter(exPagerAdapter2);
            exPagerAdapter2.setmOnPagerItemClickListener(bookID -> {
                if (onMoreClick()) {
                    return;
                }
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, bookID);
                startActivity(intent);
            });
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

    private void setTopDatas(HomePageEntity hpDatas) {
        List<EditorRecoEntity> recoThree = hpDatas.getTop();
        if (recoThree != null && recoThree.size() >= 3) {
            EditorRecoEntity bookLeft = recoThree.get(0);
            EditorRecoEntity bookCenter = recoThree.get(1);
            EditorRecoEntity bookRight = recoThree.get(2);
            GlideUtil.loadRoundRect(mActivity, ivRecoBookLeft, bookLeft.getHttpImage(), 0);
            GlideUtil.loadRoundRect(mActivity, ivRecoBookCenter, bookCenter.getHttpImage(), 0);
            GlideUtil.loadRoundRect(mActivity, ivRecoBookRight, bookRight.getHttpImage(), 0);
            ivRecoBookLeft.setTag(bookLeft.getNovelId());
            ivRecoBookCenter.setTag(bookCenter.getNovelId());
            ivRecoBookRight.setTag(bookRight.getNovelId());
            tvRecoBookNameLeft.setText(bookLeft.getNovelName());
            tvRecoBookNameCenter.setText(bookCenter.getNovelName());
            tvRecoBookNameRight.setText(bookRight.getNovelName());
            headViewTop.findViewById(R.id.labelView_left_book).setVisibility(View.VISIBLE);
            headViewTop.findViewById(R.id.labelView_center_book).setVisibility(View.VISIBLE);
            headViewTop.findViewById(R.id.labelView_right_book).setVisibility(View.VISIBLE);

            headViewTop.setVisibility(View.VISIBLE);
        } else {
            headViewTop.setVisibility(View.GONE);
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

                            //编辑力推
                            setTopDatas(hpDatas);

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

}
