package com.huli.foxread.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.google.android.material.appbar.AppBarLayout;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookReview;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.listeners.AppBarStateChangeListener;
import com.huli.foxread.listeners.OnRecyCbCheckListener;
import com.huli.foxread.ui.adapters.BookReviewAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.FastBlur;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.BookShelfListBean;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class AllBookReviewActivity extends BaseActivity implements View.OnClickListener, OnRecyCbCheckListener, OnLoadMoreListener {
    public static final int REQCODE_CHECK_ALL_REVIEW = 0x1911;

    private AppBarLayout mAppBarLayout;
    private Toolbar toolbar;
    private ImageView ivBgTop;
    private ImageView ivBookCover;
    private TextView tvBookName;
    private MaterialRatingBar ratingBarBook;
    private TextView tvReviewNum, tvBookScore;

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
        StatusBarUtils.offsetView(this, $(R.id.toolbar_all_book_review));
        StatusBarUtils.setTransparent(this);
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
        ivBgTop = $(R.id.iv_review_top_bg);
        ivBookCover = $(R.id.iv_book_cover);
        tvBookName = $(R.id.tv_book_name);
        ratingBarBook = $(R.id.materialRatingBar_book_score_all_review);
        tvReviewNum = $(R.id.tv_review_people_num);
        tvBookScore = $(R.id.tv_book_score);

        recyclerView = $(R.id.recyclerView_review_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookReviewAdapter();
        mAdapter.setAnimationEnable(true);
        mAdapter.setAnimationFirstOnly(true);
        recyclerView.setAdapter(mAdapter);
//        mAdapter.setEmptyView(R.layout.layout_empty_no_comments);
    }

    @Override
    public void setListener() {
        $(R.id.ctl_bottom_bar_write_review).setOnClickListener(this);
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
    }

    @Override
    public void doBusiness(Context mContext) {
        if (bookBean == null) {
            TipDialog.show(this, "数据神秘消失了！", TipDialog.TYPE.ERROR).setOnDismissListener(this::finish);
            return;
        }
        GlideUtil.loadRoundRect(this, ivBookCover, bookBean.getHttp_image(), 0);
        GlideApp.with(this)
                .asBitmap()
                .load(bookBean.getHttp_image())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        ivBookCover.setImageBitmap(resource);
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

        reqReviewList(bookBean.getNovel_id(), 0, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctl_bottom_bar_write_review:
                WriteBookReviewActivity.start4Result(this, WriteBookReviewActivity.REQCODE_WRITE_REVIEW, bookBean.getNovel_id());
                break;
        }
    }

    @Override
    public void onCbCheckChanged(CompoundButton view, boolean b, int pos) {
        if (onMoreClick()) {
            return;
        }
        BookReview bookReview = mAdapter.getData().get(pos);
        giveALikeOrCancel(bookReview.getId(), bookReview.getNovel_id(), b);
    }

    @Override
    public void onLoadMore() {
        reqReviewList(bookBean.getNovel_id(), curPage, false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == WriteBookReviewActivity.REQCODE_WRITE_REVIEW) {
                reqReviewList(bookBean.getNovel_id(), 0, true);
                setResult(RESULT_OK);
            }
        }
    }

    /**
     * 评论列表
     *
     * @param novelId 小说ID
     */
    private void reqReviewList(String novelId, int page, boolean showDialog) {
        OkGo.<String>get(Consts.APPRAISE_LIST_API)
                .params(Consts.NOVEL_ID, novelId)
                .params(Consts.PAGE, page + 1)
                .execute(new LtbCallback(this, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<BookReview>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<BookReview>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<BookReview>> datas = entity.getData();
                            int totalNum = datas.getTotal();
                            tvReviewNum.setText(String.format(getString(R.string.txt_x_people_give_a_mark), totalNum));
                            curPage = datas.getCurrent_page();

                            List<BookReview> reviews = datas.getData();

                            if (curPage == 1) {
                                mAdapter.setNewInstance(reviews);
//                                recyclerView.scrollToPosition(0);
                            } else {
                                mAdapter.addData(reviews);
                            }
                            if (datas.getLast_page() <= curPage) {
                                //没有下一页
                                mAdapter.getLoadMoreModule().loadMoreEnd();
                                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
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
     * 点赞或取消点赞
     *
     * @param reviewId  评论ID
     * @param novelId   小说ID
     * @param isChecked
     */
    private void giveALikeOrCancel(String reviewId, String novelId, boolean isChecked) {
        OkGo.<String>get(Consts.APPRAISE_LIKE_API)
                .params(Consts.REVIEW_ID, reviewId)
                .params(Consts.BOOK_ID, novelId)
                .params(Consts.PREFER, isChecked ? 1 : 2)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                              setResult(RESULT_OK);
                        }
                    }
                });
    }

}
