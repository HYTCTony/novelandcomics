package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.EditorRecoEntity;
import com.huli.foxread.entity.HomePageEntity;
import com.huli.foxread.entity.RvTitleEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.entity.multi.HpBGModuleEntity;
import com.huli.foxread.entity.sections.HpSection;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.adapters.BsSelectionAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.kongzue.dialog.v3.TipDialog;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rxhttp.wrapper.cahce.CacheMode;

/**
 * 书城---精选
 */
public class BookStoreSelectionFragment extends BaseFragment implements View.OnClickListener {

    /*父Fragment*/
    private MainBookstoreFragment parentFragment;

    private SmartRefreshLayout mRefreshLayout;
    private AppBarLayout mAppBarLayout;
    private RecyclerView recyclerView;
    private BsSelectionAdapter mAdapter;

    private View contentLayout;
    private ImageView ivRecoBookLeft, ivRecoBookCenter, ivRecoBookRight;
    private TextView tvRecoBookNameLeft, tvRecoBookNameCenter, tvRecoBookNameRight;
    private ImageView ivCornerLeft, ivCornerCenter, ivCornerRight;

    private int curPage = 0;//高分精选页码

    public static BookStoreSelectionFragment newInstance(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        BookStoreSelectionFragment mFragment = new BookStoreSelectionFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_bookstore_selection;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        int index = getArguments().getInt("index");
        // 这个设置tag要与FragmentPagerAdapter中的获取方法getItemPosition方法要对应上
        view.setTag(index);

        mAppBarLayout = $(view, R.id.appBarLayout);
        mAppBarLayout.setVisibility(View.GONE);
        initTopView(view);
        mRefreshLayout = $(view, R.id.smartRefreshLayout_book_store);

        recyclerView = $(view, R.id.recyclerView_book_store);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4));
        mAdapter = new BsSelectionAdapter();
        mAdapter.setAnimationEnable(true);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
//        mAdapter.setHeaderWithEmptyEnable(true);
        mAdapter.setGridSpanSizeLookup((gridLayoutManager, viewType, position) -> mAdapter.getData().get(position).getSpanSize());

        parentFragment = (MainBookstoreFragment) getParentFragment();
    }


    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onMoreClick()) {
                return;
            }
            HpSection hpSection = mAdapter.getData().get(position);
            Object obj = hpSection.getObject();
            if (obj instanceof BookEntity) {
                BookEntity book = (BookEntity) obj;
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, book.getId());
                startActivity(intent);
            }
        });

        //刷新
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            reqIndexDatas(false);
            //可以上拉加载
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            //重置高分精选页码
            curPage = 0;

            refreshLayout.finishLoadMore(15);
        });

        // 设置加载更多监听事件
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqHighMarksDatas(curPage));
    }

    public void back2Top() {
        if (recyclerView.canScrollVertically(-1)) {           //判断RecyclerView是否在顶部
            recyclerView.smoothScrollToPosition(0);
            mAppBarLayout.setExpanded(true, true);
        }
    }

    @Override
    public void doBusiness(Context mContext) {
    }

    private boolean isInit = true;

    @Override
    public void onResume() {
        super.onResume();

        if (isInit) {
            mRefreshLayout.autoRefresh();
//            reqIndexDatas(false);
            isInit = false;
        }

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (parentFragment != null) {
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    parentFragment.childCtrlTab2White();
                } else {
                    parentFragment.childCtrlTab2Yellow();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_editor_recommend_book_left:
            case R.id.iv_editor_recommend_book_center:
            case R.id.iv_editor_recommend_book_right:
                String novelId = (String) v.getTag();
                if (!TextUtils.isEmpty(novelId)) {
                    Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                    intent.putExtra(Common.KEY_BOOK_ID, novelId);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 顶部headerView
     */
    private void initTopView(View rootView) {
        contentLayout = $(rootView, R.id.ctl_editor_recommend_content_layout);
        ivRecoBookLeft = $(rootView, R.id.iv_editor_recommend_book_left);
        ivRecoBookCenter = $(rootView, R.id.iv_editor_recommend_book_center);
        ivRecoBookRight = $(rootView, R.id.iv_editor_recommend_book_right);
        tvRecoBookNameLeft = $(rootView, R.id.tv_editor_recommend_book_name_left);
        tvRecoBookNameCenter = $(rootView, R.id.tv_editor_recommend_book_name_center);
        tvRecoBookNameRight = $(rootView, R.id.tv_editor_recommend_book_name_right);
        ivCornerLeft = $(rootView, R.id.iv_corner_mark_left_book);
        ivCornerCenter = $(rootView, R.id.iv_corner_mark_center_book);
        ivCornerRight = $(rootView, R.id.iv_corner_mark_right_book);

        ivRecoBookLeft.setOnClickListener(this);
        ivRecoBookCenter.setOnClickListener(this);
        ivRecoBookRight.setOnClickListener(this);
    }

    /**
     * 设置小编力荐的数据
     *
     * @param recoThree
     */
    private void setTopDatas(List<EditorRecoEntity> recoThree) {
        if (recoThree != null) {
            if (recoThree.size() >= 3) {
                EditorRecoEntity bookCenter = recoThree.get(0);
                EditorRecoEntity bookLeft = recoThree.get(1);
                EditorRecoEntity bookRight = recoThree.get(2);

                GlideUtil.loadRoundRect(mActivity, ivRecoBookCenter, bookCenter.getHttpImage(), DensityUtils.dp2px(mActivity, 6));
                GlideUtil.loadRoundRect(mActivity, ivRecoBookLeft, bookLeft.getHttpImage(), DensityUtils.dp2px(mActivity, 6));
                GlideUtil.loadRoundRect(mActivity, ivRecoBookRight, bookRight.getHttpImage(), DensityUtils.dp2px(mActivity, 6));

                ivRecoBookCenter.setTag(bookCenter.getNovelId());
                ivRecoBookLeft.setTag(bookLeft.getNovelId());
                ivRecoBookRight.setTag(bookRight.getNovelId());

                tvRecoBookNameCenter.setText(bookCenter.getNovelName());
                tvRecoBookNameLeft.setText(bookLeft.getNovelName());
                tvRecoBookNameRight.setText(bookRight.getNovelName());

                ivCornerCenter.setVisibility(View.VISIBLE);
                ivCornerLeft.setVisibility(View.VISIBLE);
                ivCornerRight.setVisibility(View.VISIBLE);
            } else if (recoThree.size() == 2) {
                EditorRecoEntity bookCenter = recoThree.get(0);
                EditorRecoEntity bookLeft = recoThree.get(1);

                GlideUtil.loadRoundRect(mActivity, ivRecoBookCenter, bookCenter.getHttpImage(), DensityUtils.dp2px(mActivity, 6));
                GlideUtil.loadRoundRect(mActivity, ivRecoBookLeft, bookLeft.getHttpImage(), DensityUtils.dp2px(mActivity, 6));

                ivRecoBookCenter.setTag(bookCenter.getNovelId());
                ivRecoBookLeft.setTag(bookLeft.getNovelId());

                tvRecoBookNameCenter.setText(bookCenter.getNovelName());
                tvRecoBookNameLeft.setText(bookLeft.getNovelName());

                ivCornerCenter.setVisibility(View.VISIBLE);
                ivCornerLeft.setVisibility(View.VISIBLE);
                ivCornerRight.setVisibility(View.GONE);
            } else if (recoThree.size() == 1) {
                EditorRecoEntity bookCenter = recoThree.get(0);

                GlideUtil.loadRoundRect(mActivity, ivRecoBookCenter, bookCenter.getHttpImage(), DensityUtils.dp2px(mActivity, 6));

                ivRecoBookCenter.setTag(bookCenter.getNovelId());

                tvRecoBookNameCenter.setText(bookCenter.getNovelName());

                ivCornerCenter.setVisibility(View.VISIBLE);
                ivCornerLeft.setVisibility(View.GONE);
                ivCornerRight.setVisibility(View.GONE);
            }
            contentLayout.setVisibility(View.VISIBLE);
        } else {
            contentLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 构造数据
     *
     * @param hpDatas
     */
    private List<HpSection> constructDatas4Rv(HomePageEntity hpDatas) {
        List<HpBGModuleEntity> modules = hpDatas.getModule();
        List<HpSection> datas = new ArrayList<>();
        for (int i = 0; i < modules.size(); i++) {
            HpBGModuleEntity moduleEntity = modules.get(i);
            datas.add(new HpSection(true, new RvTitleEntity(moduleEntity.getId(), moduleEntity.getName(), moduleEntity.getIntroduce(), moduleEntity.getLabel())));
            List<BookEntity> novels = moduleEntity.getNovel();
            int layoutType = moduleEntity.getLayout();
            switch (layoutType) {
                case HpBGModuleEntity.TYPE_HOT_BILLBOARD:
                    for (int k = 0; k < novels.size(); k++) {
                        BookEntity book = novels.get(k);
                        book.setRank(k + 1);
                        datas.add(new HpSection(false, HpSection.SE_TYPE_HOT_BILLBOARD, novels.get(k)));
                    }
                    break;
                case HpBGModuleEntity.TYPE_CATE_EXC_WORKS:
                    for (int k = 0; k < novels.size(); k++) {
                        if (k == 0) {
                            datas.add(new HpSection(false, HpSection.SE_TYPE_FIRST_ITEM, novels.get(k)));
                        } else {
                            datas.add(new HpSection(false, HpSection.SE_TYPE_CATE_EXC_WORKS, novels.get(k)));
                        }
                    }
                    break;
                case HpBGModuleEntity.TYPE_LIST:
                    for (int k = 0; k < novels.size(); k++) {
                        datas.add(new HpSection(false, HpSection.SE_TYPE_LIST, novels.get(k)));
                    }
                    break;
                case HpBGModuleEntity.TYPE_FIRST_MONOPOLIZE:
                    for (int k = 0; k < novels.size(); k++) {
                        if (k == 0) {
                            datas.add(new HpSection(false, HpSection.SE_TYPE_FIRST_ITEM, novels.get(k)));
                        } else {
                            datas.add(new HpSection(false, HpSection.SE_TYPE_GRID_NOR, novels.get(k)));
                        }
                    }
                    break;
                case HpBGModuleEntity.TYPE_HOT_SEARCH:
                    for (int k = 0; k < novels.size(); k++) {
                        datas.add(new HpSection(false, HpSection.SE_TYPE_HOT_SEARCH, novels.get(k)));
                    }
                    break;
                case HpBGModuleEntity.TYPE_GRID_4:
                case HpBGModuleEntity.TYPE_SPECIAL:
                default:
                    for (int k = 0; k < novels.size(); k++) {
                        datas.add(new HpSection(false, HpSection.SE_TYPE_GRID_NOR, novels.get(k)));
                    }
                    break;
            }
        }
        return datas;
    }


    /**
     * 各个模块数据（除了底部高分精选）
     *
     * @param showDialog 进入页面初次加载
     */
    private void reqIndexDatas(boolean showDialog) {
        /*OkGo.<String>post(Consts.INDEX_PAGE_API)
                .params(Consts.TYPE, Consts.TYPE_SELECTION)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .execute(new LtbCallback((AppCompatActivity) mActivity, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<HomePageEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<HomePageEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            HomePageEntity hpDatas = entity.getData();
                            if (hpDatas == null) {
                                return;
                            }
                            mAppBarLayout.setVisibility(View.VISIBLE);
                            setTopDatas(hpDatas.getTop());
                            List<HpSection> list = constructDatas4Rv(hpDatas);
                            mAdapter.setList(list);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }
                });*/

        RxHttp.postForm(Consts.INDEX_PAGE_API)
                .add(Consts.TOKEN, TokenCache.getToken(mActivity))
                .add(Consts.TYPE, Consts.TYPE_SELECTION)
                .setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)
                .asResponse(HomePageEntity.class)
                .doFinally(() -> mRefreshLayout.finishRefresh())
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(entity -> {
                    if (entity == null) {
                        return;
                    }
                    mAppBarLayout.setVisibility(View.VISIBLE);
                    setTopDatas(entity.getTop());
                    List<HpSection> list = constructDatas4Rv(entity);
                    mAdapter.setList(list);
                }, (OnError) error -> TipDialog.show((AppCompatActivity) mActivity, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }


    /**
     * 高分精选
     *
     * @param prePage 上一页页码
     */
    private void reqHighMarksDatas(int prePage) {
       /* OkGo.<String>get(Consts.NOVEL_POPULAR_API)
                .params(Consts.PAGE, prePage + 1)
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
                            List<HpSection> newdatas = new ArrayList<>();
                            if (curPage == 1) {
                                newdatas.add(new HpSection(true, new RvTitleEntity("", getString(R.string.txt_high_score_well_chosen), "", "经典，正能量")));
                            }
                            for (BookEntity book : bookList) {
                                newdatas.add(new HpSection(false, HpSection.SE_TYPE_LIST, book));
                            }
                            mAdapter.addData(newdatas);

                            if (datas.getLast_page() <= curPage) {
                                //没有下一页
                                mAdapter.getLoadMoreModule().loadMoreEnd();
//                                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
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
                });*/

        RxHttp.postForm(Consts.NOVEL_POPULAR_API)
                .add(Consts.TOKEN, TokenCache.getToken(mActivity))
                .add(Consts.PAGE, prePage + 1)
                .add(Consts.TYPE, Consts.TYPE_SELECTION)
                .asResponsePageList(BookEntity.class)
                .doFinally(() -> mRefreshLayout.finishRefresh())
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(entity -> {
                    curPage = entity.getCurrent_page();
                    List<BookEntity> bookList = entity.getData();
                    List<HpSection> newdatas = new ArrayList<>();
                    if (curPage == 1) {
                        newdatas.add(new HpSection(true, new RvTitleEntity("", getString(R.string.txt_high_score_well_chosen), "", "经典，正能量")));
                    }
                    for (BookEntity book : bookList) {
                        newdatas.add(new HpSection(false, HpSection.SE_TYPE_LIST, book));
                    }
                    mAdapter.addData(newdatas);

                    if (entity.getLast_page() <= curPage) {
                        //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
//                      recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> {
                    mAdapter.getLoadMoreModule().loadMoreFail();
                });
    }

}
