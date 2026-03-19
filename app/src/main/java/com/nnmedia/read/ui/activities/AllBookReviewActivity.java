package com.nnmedia.read.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.model.bean.BookShelfListBean;
import com.nnmedia.read.GlideApp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.BookReview;
import com.nnmedia.read.listeners.AppBarStateChangeListener;
import com.nnmedia.read.listeners.OnRecyCbCheckListener;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.adapters.BookReviewAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.FastBlur;
import com.nnmedia.read.utils.GlideUtil;
import com.nnmedia.read.utils.StatusBarUtils;
import com.rxjava.rxlife.RxLife;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class AllBookReviewActivity extends BaseActivity implements View.OnClickListener, OnRecyCbCheckListener, OnLoadMoreListener, OnItemClickListener {
    public static final int REQCODE_CHECK_ALL_REVIEW = 0x1911;

    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView ivBgTop;
    private ImageView ivBookCover;
    private TextView tvBookName;
    private MaterialRatingBar ratingBarBook;
    private TextView tvReviewNum, tvBookScore;
    private View ctlBtnWriteReview;

    private RecyclerView recyclerView;
    private BookReviewAdapter mAdapter;

    private BookShelfListBean bookBean;

    private int curPage = 0;

    public static void start(Context context, BookShelfListBean book) {
        Intent starter = new Intent(context, AllBookReviewActivity.class);
        starter.putExtra(Common.KEY_BOOK_DTO, book);
        context.startActivity(starter);
    }

    public static void start4Result(Activity context, int reqCode, BookShelfListBean book) {
        Intent starter = new Intent(context, AllBookReviewActivity.class);
        starter.putExtra(Common.KEY_BOOK_DTO, book);
        context.startActivityForResult(starter, reqCode);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setTransparentForImageView(this, $(R.id.toolbar_all_book_review));
    }

    @Override
    public void initParms(Bundle parms) {
        Serializable serializable = parms.getSerializable(Common.KEY_BOOK_DTO);
        if (serializable instanceof BookShelfListBean) {
            bookBean = (BookShelfListBean) serializable;
        }
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_all_bookreview;
    }

    @Override
    public void initView(View view) {
        toolbar = $(R.id.toolbar_all_book_review);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mAppBarLayout = $(R.id.appBarLayout_all_review);
        collapsingToolbar = $(R.id.collapsing_toolbar);

        ivBgTop = $(R.id.iv_review_top_bg);
        ivBookCover = $(R.id.iv_book_cover);
        tvBookName = $(R.id.tv_book_name);
        ratingBarBook = $(R.id.materialRatingBar_book_score_all_review);
        tvReviewNum = $(R.id.tv_review_people_num);
        tvBookScore = $(R.id.tv_book_score);
        ctlBtnWriteReview = $(R.id.ctl_bottom_bar_write_review);

        recyclerView = $(R.id.recyclerView_review_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookReviewAdapter();
//        mAdapter.setAnimationEnable(true);
//        mAdapter.setAnimationFirstOnly(true);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        ctlBtnWriteReview.setOnClickListener(this);
        mAdapter.setmRecyCbCheckListener(this);
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    if (bookBean != null) {
                        toolbar.setTitle(bookBean.getNovel_name());
                    }
                } else {
                    toolbar.setTitle("");
                }
            }
        });

        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (bookBean == null) {
            TipDialog.show(this, "数据神秘消失了！", TipDialog.TYPE.ERROR).setOnDismissListener(this::finish);
            return;
        }
        GlideUtil.loadRoundRect(this, ivBookCover, bookBean.getHttp_image());
        GlideApp.with(this)
                .asBitmap()
                .load(bookBean.getHttp_image())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap fastBlur = FastBlur.fastBlur(resource, 18);
                        ivBgTop.setImageBitmap(fastBlur);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        tvBookName.setText(bookBean.getNovel_name());
        float score = bookBean.getScore();
        ratingBarBook.setRating(score / 2);
        tvBookScore.setText((score + getString(R.string.unit_score)));

        reqReviewList(bookBean.getNovel_id(), 0);
    }

    @Override
    public void onClick(View v) {
        if (onMoreClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ctl_bottom_bar_write_review:
                if (isVip()) {
                    WriteBookReviewActivity.start4Result(this, WriteBookReviewActivity.REQCODE_WRITE_REVIEW, bookBean.getNovel_id());
                } else {
                    buyVip();
                }
                break;
        }
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (isVip()) {
            BookReview bookReview = mAdapter.getData().get(position);
            ReviewDetailActivity.start(this, bookReview, bookBean.getNovel_name(), bookBean.getHttp_image(), bookBean.getScore());
        } else {
            buyVip();
        }
    }

    @Override
    public void onCbCheckChanged(CompoundButton view, boolean b, int pos) {
        List<BookReview> datas = mAdapter.getData();
        BookReview bookReview = datas.get(pos);
        giveALikeOrCancel(bookReview.getId(), bookReview.getNovel_id(), b);
    }

    @Override
    public void onLoadMore() {
        reqReviewList(bookBean.getNovel_id(), curPage);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == WriteBookReviewActivity.REQCODE_WRITE_REVIEW) {
                reqReviewList(bookBean.getNovel_id(), 0);
                setResult(RESULT_OK);
            }
        }
    }

    /**
     * 评论列表
     *
     * @param novelId 小说ID
     */
    private void reqReviewList(String novelId, int page) {
        RxHttp.get(Consts.APPRAISE_LIST_API)
                .add(Consts.N_ID, novelId)
                .add(Consts.PAGE, page + 1)
                .asResponsePageList(BookReview.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(entity -> {
                    int totalNum = entity.getTotal();
                    tvReviewNum.setText(String.format(getString(R.string.txt_x_people_give_a_mark), totalNum));
                    curPage = entity.getCurrent_page();

                    List<BookReview> datas = entity.getData();
                    if (curPage == 1) {
                        mAdapter.setList(datas);
                    } else {
                        mAdapter.addData(datas);
                    }
                    if (entity.getLast_page() <= curPage) {
                        //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
//                                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }

    /**
     * 点赞或取消点赞
     *
     * @param reviewId  评论ID
     * @param novelId   小说ID
     * @param isChecked #
     */
    private void giveALikeOrCancel(String reviewId, String novelId, boolean isChecked) {
        RxHttp.get(Consts.APPRAISE_LIKE_API)
                .add(Consts.REVIEW_ID, reviewId)
                .add(Consts.NOVEL_ID, novelId)
                .add(Consts.PREFER, isChecked ? 1 : 2)
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
                    setResult(RESULT_OK);
                });
    }

    private void buyVip() {
        MessageDialog.show(AllBookReviewActivity.this, "成为VIP", "VIP会员才能使用书评功能，是否前往获取？", "去激活", "下次再说")
                .setCancelable(true)
                .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        baseDialog.doDismiss();
                        return false;
                    }
                })
                .setOnOkButtonClickListener((baseDialog, v) -> {
//                    if (UserInfoCache.isGetFree(this))
//                        FreeGetVipActivity.start(this);
//                    else
                        GoldExchangeVipActivity.start(this, 0);
                    baseDialog.doDismiss();
                    return false;
                });
    }

    private boolean isVip() {
        if (UserInfoCache.getSuperVip(AllBookReviewActivity.this) == 1) {
            return true;
        }
        if (UserInfoCache.getIsVip(AllBookReviewActivity.this)) {
            return true;
        }
        return false;
    }
}