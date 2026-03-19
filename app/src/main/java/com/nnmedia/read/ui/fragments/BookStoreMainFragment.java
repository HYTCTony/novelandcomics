package com.nnmedia.read.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.utils.TimeUtils;
import com.nnmedia.read.GlideApp;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.BannerADEntity;
import com.nnmedia.read.entity.HomePageBGEntity;
import com.nnmedia.read.entity.multi.HpBGModuleEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.activities.ClassifyActivity2;
import com.nnmedia.read.ui.activities.MoreBooksListActivity;
import com.nnmedia.read.ui.activities.MustReadActivity;
import com.nnmedia.read.ui.activities.NewBooksActivity;
import com.nnmedia.read.ui.adapters.HpBoyGirlAdapter;
import com.nnmedia.read.ui.base.BaseFragment;
import com.nnmedia.read.utils.ClickJumpUtil;
import com.nnmedia.read.utils.DensityUtils;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rxhttp.wrapper.cahce.CacheMode;

public class BookStoreMainFragment extends BaseFragment implements View.OnClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private NestedScrollView mScrollView;
    private RecyclerView recyclerView;
    private HpBoyGirlAdapter mAdapter;
    private View viewFooter;

    private View headViewTop;
    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;

    private TextView tvFreeBook;

    private int mType;      //男生  女生
    private int curPage = 0;//页码

    public static BookStoreMainFragment newInstance(int type, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.TYPE, type);
        bundle.putInt("index", index);
        BookStoreMainFragment mFragment = new BookStoreMainFragment();
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

        tvFreeBook = $(view, R.id.tv_end_book_hint);
        viewFooter = $(view, R.id.fl_view_footer);
        mRefreshLayout = $(view, R.id.smartRefreshLayout_book_store);
        mScrollView = $(view, R.id.scrollView_boy_girl);
        recyclerView = $(view, R.id.recyclerView_book_store);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new HpBoyGirlAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
        initTopView(view);
//        mAdapter.addHeaderView(headViewTop);
        headViewTop.setVisibility(View.GONE);
//        String freeHiteTitle = TimeUtils.getCurrentTimeForMonthToChinese() + getString(R.string.txt_tips_freebook);
//        tvFreeBook.setText(freeHiteTitle);
    }

    @Override
    public void setListener() {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            curPage = 0;
            reqIndexDatas(curPage, CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
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
            mBanner.stop();
        }
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.ctl_asBtn_classify:
                ClassifyActivity2.start(mActivity);
                break;
            case R.id.ctl_asBtn_ranking:
                MustReadActivity.start(mActivity);
                break;
            case R.id.ctl_asBtn_new_book:
                Intent intent = new Intent(mActivity, NewBooksActivity.class);
                intent.putExtra(Consts.TYPE, mType);
                startActivity(intent);
                break;
            case R.id.ctl_asBtn_book_finished:
                Toast.makeText(mActivity, "尽情期待...", Toast.LENGTH_SHORT).show();
//                MoreBooksListActivity.start(mActivity, getString(R.string.txt_free_book), "" + 0, 0);
                break;
            default:
                break;
        }
    }

    /**
     * 顶部headerView
     */
    private void initTopView(View view) {
        headViewTop = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_sb_top, recyclerView, false);
        headViewTop = $(view, R.id.ctl_head_contentView);
        initBannerView(view);
        initCenterBar(view);
    }

    /**
     * 轮播广告
     */
    private void initBannerView(View rootView) {
        mBanner = $(rootView, R.id.banner_boy_girl_top);
        mBanner.setAdapter(new BannerImageAdapter<BannerADEntity>(bannerDatas) {

            @Override
            public void onBindView(BannerImageHolder holder, BannerADEntity data, int position, int size) {
                if (data instanceof BannerADEntity) {
                    BannerADEntity adEntity = (BannerADEntity) data;
                    //Glide 加载图片简单用法
                    GlideApp.with(holder.itemView)
                            .load(adEntity.getImageText())
                            .transform(new CenterCrop(), new RoundedCorners(DensityUtils.dp2px(mActivity, 8)))
//                    .placeholder(R.mipmap.banner_place_holder)
                            .error(R.mipmap.banner_place_holder)
                            .into(holder.imageView);
                }
            }
        }).setOnBannerListener((data, position) -> {
            if (bannerDatas != null && bannerDatas.size() > position) {
                BannerADEntity entity = bannerDatas.get(position);
                ClickJumpUtil.handleJump(mActivity, entity.getLink(), entity.getJump());
            }
        })
                .addBannerLifecycleObserver(this)
                .setIndicator(new CircleIndicator(mActivity));
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
    private void reqIndexDatas(int prePage, CacheMode cacheMode) {
        RxHttp.postForm(Consts.INDEX_PAGE_API)
                .add(Consts.TYPE, mType)
                .setCacheMode(cacheMode)
                .asResponse(HomePageBGEntity.class)
                .doFinally(() -> mRefreshLayout.finishRefresh())
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(entity -> {
                    //轮播图
                    bannerDatas = entity.getBanner();
                    if (!bannerDatas.isEmpty()) {
                        mBanner.setVisibility(View.VISIBLE);
                        mBanner.setDatas(bannerDatas);
                    } else {
                        mBanner.setVisibility(View.GONE);
                    }
                    List<HpBGModuleEntity> moduleList = entity.getModule();

                    mAdapter.addData(moduleList);

                    headViewTop.setVisibility(View.VISIBLE);
                    viewFooter.setVisibility(View.VISIBLE);
                }, (OnError) error -> {
                    TipDialog.show((AppCompatActivity) mActivity, error.getErrorMsg(), TipDialog.TYPE.ERROR);
                });

    }
}