package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

public class BookRackAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public BookRackAdapter(List<String> data) {
        super(R.layout.recy_grid_item_my_book_rack, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String s) {
        int position = helper.getLayoutPosition();
        if (position == getData().size() - 1) {
            helper.setImageBitmap(R.id.iv_book_cover, null);
            helper.setText(R.id.tv_book_name, "添加书籍");
            helper.setText(R.id.tv_book_state, "");
            helper.setText(R.id.tv_reading, "");

            helper.setVisible(R.id.iv_add_book, true);
        } else {
            GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), "url", 0);
            helper.setText(R.id.tv_book_name, "猎能者猎能者猎能者");
            helper.setText(R.id.tv_book_state, "完结");
            helper.setText(R.id.tv_reading, (getContext().getString(R.string.txt_markread) + "18.5%"));

            helper.setVisible(R.id.iv_add_book, false);
        }

    }

}
