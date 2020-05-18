package com.huli.foxread.ui.adapters;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.multi.BookShelfOrADsMultEntity;
import com.huli.page.model.bean.BookShelfListBean;

import java.util.List;

public class BookRackAdapter2 extends BaseMultiItemQuickAdapter<BookShelfOrADsMultEntity, BaseViewHolder> {

    public BookRackAdapter2(List<BookShelfOrADsMultEntity> data) {
        super(data);
        addItemType(BookShelfOrADsMultEntity.ITEM_ADD_BOOK, R.layout.recy_grid_item_my_book_rack_addbook);
        addItemType(BookShelfOrADsMultEntity.ITEM_ADS, R.layout.listitem_ad_native_express);
        addItemType(BookShelfOrADsMultEntity.DETAILED, R.layout.recy_grid_item_my_book_rack2);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookShelfOrADsMultEntity item) {
        int position = helper.getLayoutPosition();
        switch (helper.getItemViewType()) {
            case BookShelfOrADsMultEntity.ITEM_ADD_BOOK:

                break;
            case BookShelfOrADsMultEntity.ITEM_ADS:
                TTNativeExpressAd ad = item.getAds();
                View video = ad.getExpressAdView();
                if (video != null) {
                    if (video.getParent() == null) {
                        FrameLayout videoView = helper.getView(R.id.iv_listitem_express);
                        videoView.removeAllViews();
                        videoView.addView(video);
                        ad.render();
                    }
                }
                break;
            case BookShelfOrADsMultEntity.DETAILED:
                BookShelfListBean bookShelf = item.getBook();
                helper.setText(R.id.tv_book_name, bookShelf.getNovel_name());
                helper.setText(R.id.tv_book_state, bookShelf.getIs_end() == 1 ? "完结" : "连载");
                if (TextUtils.isEmpty(bookShelf.getLastChapter())) {
                    helper.setText(R.id.tv_reading, (getContext().getString(R.string.txt_markread_null)));
                } else {
                    helper.setText(R.id.tv_reading, (String.format(getContext().getString(R.string.txt_markread_chapter_x), bookShelf.getLastChapter())));
                }
                ImageView iv = helper.getView(R.id.iv_book_cover);
                if (bookShelf.getIsLocal()) {
                    //本地文件的图片
                    GlideApp.with(getContext())
                            .load(R.drawable.ic_local_file)
                            .fitCenter()
                            .into(iv);
                } else {
                    if (bookShelf.getIs_copyright() == 1) {
                        iv.setColorFilter(null);
                    } else {
                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                        iv.setColorFilter(filter);
                        helper.setText(R.id.tv_book_state, "已下架");
                    }

                    //书的图片
                    GlideApp.with(getContext())
                            .load(bookShelf.getHttp_novel_image())
                            .placeholder(R.mipmap.img_holder_rect)
                            .error(R.mipmap.img_holder_rect)
                            .fitCenter()
                            .into(iv);

                }
                break;
        }
    }

}