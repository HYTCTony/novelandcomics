package com.huli.foxread.ui.adapters;

import android.util.Log;

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

/**
 * 评论回复列表
 */
public class BookReviewReplyAdapter extends BaseQuickAdapter<BookReview, BaseViewHolder> implements LoadMoreModule {

    private OnRecyCbCheckListener mRecyCbCheckListener;

    public void setmRecyCbCheckListener(OnRecyCbCheckListener mRecyCbCheckListener) {
        this.mRecyCbCheckListener = mRecyCbCheckListener;
    }

    public BookReviewReplyAdapter(List<BookReview> data) {
        super(R.layout.recy_list_item_book_review_reply, data);
    }

    public BookReviewReplyAdapter() {
        super(R.layout.recy_list_item_book_review_reply);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookReview bookReview) {
        GlideUtil.loadCircle(getContext(), holder.getView(R.id.iv_replier_headImg), bookReview.getHttp_avatar());
        holder.setText(R.id.iv_replier_id, String.format(getContext().getString(R.string.txt_book_friend_xid), bookReview.getUsername()));
        holder.setText(R.id.tv_reply_content, bookReview.getContent());
        holder.setText(R.id.tv_reply_time, DateTimeUtil.formatDateTime(bookReview.getCreatetime() * 1000, DateTimeUtil.DF_YYYY_MM_DD));

        AppCompatCheckBox cbLike = holder.getView(R.id.cb_reply_like_and_number);
        cbLike.setChecked(bookReview.getCondition() == 1);
        cbLike.setText(String.valueOf(bookReview.getPrefer()));

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
                int position = holder.getLayoutPosition() - getHeaderLayoutCount();
                Log.e("sssssssssss", "pos===" + position);
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
