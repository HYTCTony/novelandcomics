package com.huli.foxread.ui.adapters;

import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.entity.BookReview;
import com.huli.foxread.listeners.OnRecyCbCheckListener;
import com.huli.foxread.ui.activities.LoginActivity;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * 评论列表
 */
public class BookReviewAdapter extends BaseQuickAdapter<BookReview, BaseViewHolder> implements LoadMoreModule {

    private OnRecyCbCheckListener mRecyCbCheckListener;

    public void setmRecyCbCheckListener(OnRecyCbCheckListener mRecyCbCheckListener) {
        this.mRecyCbCheckListener = mRecyCbCheckListener;
    }

    public BookReviewAdapter(List<BookReview> data) {
        super(R.layout.recy_list_item_book_review, data);
    }

    public BookReviewAdapter() {
        super(R.layout.recy_list_item_book_review);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookReview bookReview) {
        GlideUtil.loadCircle(getContext(), holder.getView(R.id.iv_reviewer_headImg), bookReview.getHttp_avatar());
        String userName = bookReview.getUsername();
        if (!TextUtils.isEmpty(userName)) {
            holder.setText(R.id.iv_reviewer_id, String.format(getContext().getString(R.string.txt_book_friend_xid), userName));
        } else {
            holder.setText(R.id.iv_reviewer_id, String.format(getContext().getString(R.string.txt_book_friend_xid), bookReview.getUser_id()));
        }
        TextView tvContent = holder.getView(R.id.tv_review_content);
        holder.setText(R.id.tv_review_time, DateTimeUtil.formatDateTime(bookReview.getCreatetime() * 1000, DateTimeUtil.DF_YYYY_MM_DD));

        MaterialRatingBar ratingBar = holder.getView(R.id.ratingBar_score_by_reviewer);
        ratingBar.setRating(bookReview.getScore() / 2);

        AppCompatCheckBox cbLike = holder.getView(R.id.cb_review_like_and_number);
        cbLike.setChecked(bookReview.getCondition() == 1);
        cbLike.setText(String.valueOf(bookReview.getPrefer()));
        tvContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int lines = tvContent.getLineCount();
                if (lines >= 5) {
                    if (tvContent.getEllipsize() != null) {
                        holder.setVisible(R.id.tv_review_content_more, true);
                    } else {
                        holder.setGone(R.id.tv_review_content_more, true);
                    }
                } else {
                    holder.setGone(R.id.tv_review_content_more, true);
                }
                //这个回调会调用多次，获取完行数记得注销监听
                tvContent.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
        tvContent.setText(bookReview.getContent());

        cbLike.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }
            if (mRecyCbCheckListener != null) {
                if (UserInfoCache.getIsTourist(getContext())) {
                    LoginActivity.start(getContext());
                    cbLike.setChecked(!isChecked);
                    return;
                }
                int position = holder.getLayoutPosition();
                mRecyCbCheckListener.onCbCheckChanged(buttonView, isChecked, position);
                if (isChecked) {
                    bookReview.setPrefer(bookReview.getPrefer() + 1);
                    bookReview.setCondition(1);
                    getData().set(position, bookReview);
                } else {
                    if (bookReview.getPrefer() > 0) {
                        bookReview.setPrefer(bookReview.getPrefer() - 1);
                        bookReview.setCondition(0);
                        getData().set(position, bookReview);
                    }
                }
            }
            cbLike.setText(String.valueOf(bookReview.getPrefer()));
            //防止频繁点击
            cbLike.setEnabled(false);
            cbLike.postDelayed(() -> cbLike.setEnabled(true), 1200);
        });
    }
}
