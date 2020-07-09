package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookReview;
import com.huli.foxread.listeners.OnRecyCbCheckListener;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.adapters.BookReviewReplyAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.dialogs.CommonDialog;
import com.huli.foxread.ui.dialogs.base.BaseDialog;
import com.huli.foxread.ui.dialogs.base.ViewHolder;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.GlideUtil;
import com.huli.page.utils.KeyBoardUtils;
import com.rxjava.rxlife.RxLife;

import java.io.Serializable;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * 评论详情
 */
public class ReviewDetailActivity extends BaseActivity implements View.OnClickListener, OnLoadMoreListener, OnRecyCbCheckListener {

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
        mRecyclerView = $(R.id.recyclerView_review_reply);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookReviewReplyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        View headView = LayoutInflater.from(this).inflate(R.layout.layout_rv_head_review_detail, mRecyclerView, false);
        mAdapter.setHeaderView(headView);
        mAdapter.setEmptyView(R.layout.layout_empty_no_reply);
        mAdapter.setHeaderWithEmptyEnable(true);

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
    }

    @Override
    public void setListener() {
        ctlBookContent.setOnClickListener(this);
        btnReply.setOnClickListener(this);

        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
        mAdapter.setmRecyCbCheckListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (review == null) {
            return;
        }

        GlideUtil.loadCircle(this, ivReviewerHeadImg, review.getHttp_avatar());
        ivReviewerId.setText(String.format(getString(R.string.txt_book_friend_xid), review.getUsername()));
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

        reqReplyDatas(review.getId(), 0);
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
            case R.id.ctl_asBtn_reply:
                CommonDialog.newInstance()
                        .setLayoutId(R.layout.layout_dialog_reply_edit)
                        .setConvertListener(new CommonDialog.ViewConvertListener() {
                            @Override
                            public void convertView(ViewHolder holder, BaseDialog dialog) {
//                                KeyBoardUtils.openKeyboard(dialog.getContext(), holder.getView(R.id.et_say_something));
                                EditText etInput = holder.getView(R.id.et_say_something);
                                etInput.setFocusable(true);
                            }
                        })
                        .setDimAmout(0.5f)
                        .setShowBottom(true)
                        .setAnimStyle(R.style.DialogAnimation)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
//                                KeyBoardUtils.closeKeyboard(ReviewDetailActivity.this, dialog);
                                hideKeyboard(ReviewDetailActivity.this);
                            }
                        })
                        .show(getSupportFragmentManager());

                break;
            default:
                break;
        }
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onLoadMore() {
        reqReplyDatas(review.getNovel_id(), curPage);
    }

    @Override
    public void onCbCheckChanged(CompoundButton view, boolean b, int pos) {
        BookReview bookReview = mAdapter.getData().get(pos);
        giveALikeOrCancel(bookReview.getId(), bookReview.getNovel_id(), b);
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
                .add(Consts.BOOK_ID, novelId)
                .add(Consts.PREFER, isChecked ? 1 : 2)
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
//                    setResult(RESULT_OK);
                });
    }

    /**
     * 获取书评详情
     *
     * @param reviewId 评论ID
     */
    private void reqReplyDatas(String reviewId, int page) {
        RxHttp.get(Consts.APPRAISE_REPLY_API)
                .add(Consts.REVIEW_ID, reviewId)
                .add(Consts.PAGE, page + 1)
                .asResponsePageList(BookReview.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(replyEntity -> {
                    int totalNum = replyEntity.getTotal();
                    tvAllReply.setText(String.format(getString(R.string.txt_all_reply_x), totalNum));
                    curPage = replyEntity.getCurrent_page();

                    List<BookReview> datas = replyEntity.getData();
                    if (curPage == 1) {
                        mAdapter.setList(datas);
                    } else {
                        mAdapter.addData(datas);
                    }
                    if (replyEntity.getLast_page() <= curPage) {
                        //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
//                                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }


}
