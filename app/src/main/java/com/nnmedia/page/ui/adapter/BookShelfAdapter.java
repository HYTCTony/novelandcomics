package com.nnmedia.page.ui.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.page.model.bean.BookShelfListBean;

public class BookShelfAdapter extends BaseQuickAdapter<BookShelfListBean, BaseViewHolder> {

    public BookShelfAdapter() {
        super(R.layout.item_coll_book);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookShelfListBean item) {
        ImageView mIvCover = helper.getView(R.id.coll_book_iv_cover);

        if (item.getIsLocal()) {
            //本地文件的图片
            Glide.with(getContext())
                    .load(R.drawable.ic_local_file)
                    .fitCenter()
                    .into(mIvCover);
        } else {
            //书的图片
            Glide.with(getContext())
                    .load(item.getHttp_novel_image())
                    .placeholder(R.drawable.ic_book_loading)
                    .error(R.drawable.ic_load_error)
                    .fitCenter()
                    .into(mIvCover);
        }

        helper.setText(R.id.coll_book_tv_name, item.getNovel_name())
                .setText(R.id.coll_book_tv_chapter, item.getLastChapter());
        if (!item.getIsLocal()) {
//            helper.setText(R.id.coll_book_tv_lately_update, StringUtils.dateConvert(time, Constant.FORMAT_FILE_DATE) + ":");
            helper.setVisible(R.id.coll_book_tv_lately_update, true);
        } else {
            helper.setText(R.id.coll_book_tv_lately_update, "阅读进度:");
        }

        if (item.getIsUpdate()) {
            helper.setVisible(R.id.coll_book_iv_red_rot, true);
        } else {
            helper.setVisible(R.id.coll_book_iv_red_rot, false);
        }
    }
}