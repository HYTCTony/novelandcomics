package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.contact.Common;
import com.huli.foxread.entity.BookReview;
import com.huli.foxread.ui.adapters.BookReviewReplyAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.GlideUtil;

import java.io.Serializable;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * 评论详情
 */
public class ReviewDetailActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private BookReviewReplyAdapter mAdapter;

    private View btnReply;

    private ImageView ivReviewerHeadImg;
    private TextView ivReviewerId, tvReviewContent, tvReviewTime;
    private View ctlBookContent;
    private ImageView ivBookCover;
    private TextView tvBookName, tvBookScore, tvAllReply;
    private MaterialRatingBar ratingBarBook;

    private int curPage = 0;

    private BookReview review;
    private String bookName, bookImg;
    private float bookScore;

    public static void start(Context context, BookReview review, String bookName, String bookCover, float bookScore) {
        Intent starter = new Intent(context, ReviewDetailActivity.class);
        starter.putExtra(Common.KEY_BOOK_NAME, bookName);
        starter.putExtra(Common.KEY_BOOK_IMG, bookCover);
        starter.putExtra(Common.KEY_BOOK_SCORE, bookScore);
        starter.putExtra(Common.KEY_BOOK_REVIEW_DTO, review);
        context.startActivity(starter);
    }

    public static void start(Context context, BookReview review) {
        Intent starter = new Intent(context, ReviewDetailActivity.class);
        starter.putExtra(Common.KEY_BOOK_REVIEW_DTO, review);
        context.startActivity(starter);
    }

    @Override
    public void initParms(Bundle parms) {
        Serializable serializable = parms.getSerializable(Common.KEY_BOOK_REVIEW_DTO);
        if (serializable instanceof BookReview) {
            review = (BookReview) serializable;
        }
        bookName = parms.getString(Common.KEY_BOOK_NAME);
        bookImg = parms.getString(Common.KEY_BOOK_IMG);
        bookScore = parms.getFloat(Common.KEY_BOOK_SCORE, 0.0f);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_review_detail;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_review_detail);

        btnReply = $(R.id.ctl_asBtn_reply);
        btnReply.setVisibility(View.GONE);
        mRecyclerView = $(R.id.recyclerView_review_reply);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookReviewReplyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        View headView = LayoutInflater.from(this).inflate(R.layout.layout_rv_head_review_detail, mRecyclerView, false);
        mAdapter.setHeaderView(headView);
//        mAdapter.setEmptyView(R.layout.layout_empty_no_reply);
//        mAdapter.setHeaderWithEmptyEnable(true);

        ivReviewerHeadImg = headView.findViewById(R.id.iv_reviewer_headImg);
        ivReviewerId = headView.findViewById(R.id.iv_reviewer_id);
        tvReviewContent = headView.findViewById(R.id.tv_review_content);
        tvReviewContent.setMaxLines(999);
        tvReviewTime = headView.findViewById(R.id.tv_review_time);
        headView.findViewById(R.id.ratingBar_score_by_reviewer).setVisibility(View.GONE);
        headView.findViewById(R.id.tv_review_content_more).setVisibility(View.GONE);
        headView.findViewById(R.id.cb_review_like_and_number).setVisibility(View.GONE);

        ctlBookContent = headView.findViewById(R.id.ctl_asBtn_book_content);
        ivBookCover = headView.findViewById(R.id.iv_book_cover);
        tvBookName = headView.findViewById(R.id.tv_book_name);
        ratingBarBook = headView.findViewById(R.id.materialRatingBar_book_score);
        tvBookScore = headView.findViewById(R.id.tv_book_score);
        tvAllReply = headView.findViewById(R.id.tv_all_reply);
        tvAllReply.setVisibility(View.GONE);
    }

    @Override
    public void setListener() {
        ctlBookContent.setOnClickListener(this);
//        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
//        mAdapter.setmRecyCbCheckListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (review == null) {
            return;
        }

        GlideUtil.loadCircle(this, ivReviewerHeadImg, review.getHttp_avatar());
        ivReviewerId.setText(String.format(getString(R.string.txt_book_friend_xid), review.getUser_id()));
        tvReviewContent.setText(review.getContent());
        tvReviewTime.setText(DateTimeUtil.formatDateTime(review.getCreatetime() * 1000, DateTimeUtil.DF_YYYY_MM_DD));

        tvAllReply.setText(String.format(getString(R.string.txt_all_reply_x), 0));
        GlideUtil.loadRoundRect(this, ivBookCover, bookImg);
        tvBookName.setText(bookName);
        if (bookScore <= 0) {
            bookScore = 0;
            ratingBarBook.setRating(bookScore);
        } else {
            ratingBarBook.setRating(bookScore / 2);
        }
        tvBookScore.setText(String.valueOf(bookScore));


//        reqReviewList(review.getNovel_id(), 0, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctl_asBtn_book_content:
                Intent intent = new Intent(this, BookDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        //清除掉BookDetailsActivity之上的Activity，启动模式是默认则BookDetailsActivity自身也清除掉
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);     //不想重新再创建BookDetailsActivity
                intent.putExtra(Common.KEY_BOOK_ID, review.getNovel_id());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

  /*  @Override
    public void onLoadMore() {
        reqReviewList(review.getNovel_id(), curPage, false);
    }

    @Override
    public void onCbCheckChanged(CompoundButton view, boolean b, int pos) {
        if (onMoreClick()) {
            return;
        }
        BookReview bookReview = mAdapter.getData().get(pos);
        giveALikeOrCancel(bookReview.getId(), bookReview.getNovel_id(), b);
    }

    *//**
     * 评论列表
     *
     * @param novelId 小说ID
     *//*
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
                            tvAllReply.setText(String.format(getString(R.string.txt_all_reply_x), totalNum));
                            curPage = datas.getCurrent_page();

                            List<BookReview> reviews = datas.getData();

                            if (curPage == 1) {
                                mAdapter.setNewInstance(reviews);
                            } else {
                                mAdapter.addData(reviews);
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

    *//**
     * 点赞或取消点赞
     *
     * @param reviewId  评论ID
     * @param novelId   小说ID
     * @param isChecked
     *//*
    private void giveALikeOrCancel(String reviewId, String novelId, boolean isChecked) {
        OkGo.<String>get(Consts.APPRAISE_LIKE_API)
                .params(Consts.REVIEW_ID, reviewId)
                .params(Consts.BOOK_ID, novelId)
                .params(Consts.PREFER, isChecked ? 1 : 2)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                       *//* LzyResponse<PagingWarpper<List<BookReview>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<BookReview>>>>() {
                                });
                        if (entity.error_code == 0) {

                        }*//*
                    }
                });
    }*/

}
