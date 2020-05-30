package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookReview;
import com.huli.foxread.listeners.OnRecyCbCheckListener;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * 评论回复列表
 */
public class BookReviewReplyAdapter extends BaseQuickAdapter<BookReview, BaseViewHolder> implements LoadMoreModule {

    public static final int PAYLOAD_CHECKBOX = 1;

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
        holder.setText(R.id.iv_replier_id, String.format(getContext().getString(R.string.txt_book_friend_xid), bookReview.getUser_id()));
        holder.setText(R.id.tv_reply_content, bookReview.getContent());
        holder.setText(R.id.tv_reply_time, DateTimeUtil.formatDateTime(bookReview.getCreatetime() * 1000, DateTimeUtil.DF_YYYY_MM_DD));

        AppCompatCheckBox cbLike = holder.getView(R.id.cb_reply_like_and_number);
        cbLike.setChecked(bookReview.getCondition() == 1);
        cbLike.setText(String.valueOf(bookReview.getPrefer()));
        if (mRecyCbCheckListener != null) {
            cbLike.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    int position = holder.getLayoutPosition();
                    mRecyCbCheckListener.onCbCheckChanged(buttonView, isChecked, position);
                    if (isChecked) {
                        getData().get(position).setPrefer(bookReview.getPrefer() + 1);
                        getData().get(position).setCondition(1);
                    } else {
                        getData().get(position).setPrefer(bookReview.getPrefer() - 1);
                        getData().get(position).setCondition(0);
                    }
                    notifyItemChanged(position, BookReviewReplyAdapter.PAYLOAD_CHECKBOX);
                }
            });
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookReview item, @NonNull List<?> payloads) {
        super.convert(holder, item, payloads);
        for (Object obj : payloads) {
            if (obj instanceof Integer) {
                int payload = (int) obj;
                //局部更新点赞数
                if (payload == PAYLOAD_CHECKBOX) {
                    holder.setText(R.id.cb_review_like_and_number, String.valueOf(item.getPrefer()));
                }
            }
        }
    }
}
