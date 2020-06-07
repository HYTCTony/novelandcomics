package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.engines.GlideImageLoader;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.entity.HomePageBGEntity;
import com.huli.foxread.entity.multi.HpBGModuleEntity;
import com.huli.foxread.ui.activities.BookRankingActivity;
import com.huli.foxread.ui.activities.ClassifyActivity;
import com.huli.foxread.ui.activities.EndBooksActivity;
import com.huli.foxread.ui.activities.NewBooksActivity;
import com.huli.foxread.ui.adapters.HpBoyGirlAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.ClickJumpUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 书城---男生（女生）
 */
public class BookStoreBoyFragment extends BaseFragment implements View.OnClickListener, OnBannerListener {

    private SmartRefreshLayout mRefreshLayout;
    private NestedScrollView mScrollView;
    private RecyclerView recyclerView;
    private HpBoyGirlAdapter mAdapter;
    private View viewFooter;

    private View headViewTop;
    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;

    private int mType;      //男生  女生

    public static BookStoreBoyFragment newInstance(int type, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.TYPE, type);
        bundle.putInt("index", index);
        BookStoreBoyFragment mFragment = new BookStoreBoyFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }

    /**
     * 回到顶部
     */
    public void back2Top() {
        if (mScrollView != null) {
            mScrollView.smoothScrollTo(0, 0);
        }
    }


    @Override
    public int bindLayout() {
        return R.layout.fragment_bookstore_boy_girl_refresh_recy;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        Bundle bundle = getArguments();
        mType = bundle.getInt(Consts.TYPE);
        int index = bundle.getInt("index");
        // 这个设置tag要与FragmentPagerAdapter中的获取方法getItemPosition方法要对应上
        view.setTag(index);

        viewFooter = $(view, R.id.fl_view_footer);
        mRefreshLayout = $(view, R.id.smartRefreshLayout_book_store);
        mScrollView = $(view, R.id.scrollView_boy_girl);
        recyclerView = $(view, R.id.recyclerView_book_store);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new HpBoyGirlAdapter();
        recyclerView.setAdapter(mAdapter);
//        mAdapter.setEmptyView(R.layout.layout_empty);
        initTopView(view);
//        mAdapter.addHeaderView(headViewTop);
        headViewTop.setVisibility(View.GONE);

    }

    @Override
    public void setListener() {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            reqIndexDatas(false);
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private boolean isInitData = true;     //官方懒加载方法

    @Override
    public void onResume() {
        super.onResume();
        if (isInitData) {
            mRefreshLayout.autoRefresh();
//            reqIndexDatas(true);
            isInitData = false;
        }
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
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.ctl_asBtn_classify:
                startActivity(new Intent(mActivity, ClassifyActivity.class));
                break;

            case R.id.ctl_asBtn_ranking:
                Intent rankIntent = new Intent(mActivity, BookRankingActivity.class);
                rankIntent.putExtra(Consts.TYPE, mType);
                startActivity(rankIntent);
                break;

            case R.id.ctl_asBtn_new_book:
                Intent intent = new Intent(mActivity, NewBooksActivity.class);
                intent.putExtra(Consts.TYPE, mType);
                startActivity(intent);
                break;

            case R.id.ctl_asBtn_book_finished:
                Intent ebIntent = new Intent(mActivity, EndBooksActivity.class);
                ebIntent.putExtra(Consts.TYPE, mType);
                startActivity(ebIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 顶部headerView
     */
    private void initTopView(View view) {
//        headViewTop = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_sb_top, recyclerView, false);
        headViewTop = $(view, R.id.ctl_head_contentView);
        initBannerView(view);
        initCenterBar(view);
    }

    /**
     * 轮播广告
     */
    private void initBannerView(View rootView) {
        mBanner = $(rootView, R.id.banner_boy_girl_top);
        //设置banner样式
//        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(6000);
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
        $(rootView, R.id.ctl_asBtn_classify).setOnClickListener(this);
        $(rootView, R.id.ctl_asBtn_ranking).setOnClickListener(this);
        $(rootView, R.id.ctl_asBtn_new_book).setOnClickListener(this);
        $(rootView, R.id.ctl_asBtn_book_finished).setOnClickListener(this);
    }

    /**
     * 各个模块数据
     */
    private void reqIndexDatas(boolean showDialog) {
        OkGo.<String>post(Consts.INDEX_PAGE_API)
                .params(Consts.TYPE, mType)
                .execute(new LtbCallback((AppCompatActivity) mActivity, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<HomePageBGEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<HomePageBGEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            HomePageBGEntity hpDatas = entity.getData();

                            List<HpBGModuleEntity> moduleList = hpDatas.getModule();

                            mAdapter.setList(moduleList);
                            //轮播图
                            bannerDatas = hpDatas.getBanner();
                            if (bannerDatas != null) {
                                mBanner.update(bannerDatas);
                            }

                            headViewTop.setVisibility(View.VISIBLE);
                            viewFooter.setVisibility(View.VISIBLE);
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
