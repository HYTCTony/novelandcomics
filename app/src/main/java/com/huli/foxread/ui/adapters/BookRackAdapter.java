package com.huli.foxread.ui.adapters;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.page.model.bean.BookShelfListBean;

import java.util.List;

public class BookRackAdapter extends BaseQuickAdapter<BookShelfListBean, BaseViewHolder> {

    public BookRackAdapter(List<BookShelfListBean> data) {
        super(R.layout.recy_grid_item_my_book_rack, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookShelfListBean item) {
        int position = helper.getLayoutPosition();
        if (position == getData().size() - 1) {
            helper.setImageBitmap(R.id.iv_book_cover, null);
            helper.setText(R.id.tv_book_name, "添加书籍");
            helper.setText(R.id.tv_book_state, "");
            helper.setText(R.id.tv_reading, "");

            helper.setVisible(R.id.iv_add_book, true);
            helper.setVisible(R.id.tv_book_state, false);
            helper.setVisible(R.id.tv_reading, false);
        } else {
            helper.setText(R.id.tv_book_name, item.getNovel_name());
            helper.setText(R.id.tv_book_state, item.getIs_end() == 0 ? "连载" : "完结");
            if (TextUtils.isEmpty(item.getLastChapter())) {
                helper.setText(R.id.tv_reading, (getContext().getString(R.string.txt_markread_null)));
            } else {
                helper.setText(R.id.tv_reading, (getContext().getString(R.string.txt_markread) + item.getLastChapter()));
            }
            helper.setVisible(R.id.iv_add_book, false);
            helper.setVisible(R.id.tv_book_state, true);
            helper.setVisible(R.id.tv_reading, true);
            ImageView iv = helper.getView(R.id.iv_book_cover);
            if (item.getIsLocal()) {
                //本地文件的图片
                Glide.with(getContext())
                        .load(R.drawable.ic_local_file)
                        .fitCenter()
                        .into(iv);
            } else {
                //书的图片
                Glide.with(getContext())
                        .load(item.getHttp_novel_image())
                        .placeholder(R.drawable.ic_book_loading)
                        .error(R.drawable.ic_load_error)
                        .fitCenter()
                        .into(iv);
            }
        }

    }

}