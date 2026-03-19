package com.nnmedia.read.ui.adapters;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.page.model.bean.BookShelfListBean;
import com.nnmedia.read.GlideApp;
import com.nnmedia.read.entity.multi.BookShelfOrADsMultEntity;
import com.nnmedia.read.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * 书架
 */
public class BookRackAdapter extends BaseMultiItemQuickAdapter<BookShelfOrADsMultEntity, BaseViewHolder> {

    private boolean isManagerMode = false;
    private SparseBooleanArray selectLists = new SparseBooleanArray();

    /**
     * 设置manage模式
     */
    public void setManagerMode(boolean managerMode) {
        isManagerMode = managerMode;
        if (managerMode) {
            selectLists = new SparseBooleanArray();
        } else {
            selectLists.clear();
        }
        notifyItemRangeChanged(0, getItemCount(), "1");
    }

    /**
     * manage模式下点击item选中
     *
     * @param position 数据集合下标（不包含header和footer）
     * @return
     */
    public int clickItemOnManageMode(int position) {
        int pos = position + this.getHeaderLayoutCount();
        selectLists.put(position, !selectLists.get(position));
        notifyItemChanged(pos, "2");
        return getSelectedCount();
    }

    /**
     * 全选or取消全选
     */
    public int funCheckAll(boolean checkAll) {
        if (checkAll) {
            //去掉头尾和最后一个“+”item
            for (int i = getHeaderLayoutCount(); i <= getData().size() - 1; i++) {
                selectLists.put(i - getHeaderLayoutCount(), true);  //数据集合下标作为key（不包含header和footer）
            }
            notifyItemRangeChanged(getHeaderLayoutCount(), getData().size() - 1, "2");
            return getSelectedCount();
        } else {
            //去掉头尾和最后一个“+”item
           /* for (int i = getHeaderLayoutCount(); i < getData().size() - 1; i++) {
                selectLists.put(i, false);
            }*/
            selectLists.clear();
            notifyItemRangeChanged(getHeaderLayoutCount(), getData().size() - 1, "2");
            return 0;
        }
    }


    /**
     * 获取选中数量
     */
    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < selectLists.size(); i++) {
            if (selectLists.valueAt(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取选中的item的下标
     */
    public List<Integer> getSelectedIndexs() {
        List<Integer> indexs = new ArrayList<>();
        for (int i = 0; i < selectLists.size(); i++) {
            if (selectLists.valueAt(i)) {
                indexs.add(selectLists.keyAt(i));
            }
        }
        return indexs;
    }

    public List<BookShelfOrADsMultEntity> getSelectedEntityList() {
        List<Integer> indexs = getSelectedIndexs();
        List<BookShelfOrADsMultEntity> list = new ArrayList<>();
        List<BookShelfOrADsMultEntity> allData = getData();
        for (int i = 0; i < indexs.size(); i++) {
            if (i < allData.size()) {
                list.add(allData.get(indexs.get(i)));
            }
        }
        return list;
    }

    public void clearSelected() {
        if (selectLists != null) {
            selectLists.clear();
        }
    }

    public BookRackAdapter(List<BookShelfOrADsMultEntity> data) {
        super(data);
        addItemType(BookShelfOrADsMultEntity.ITEM_ADD_BOOK, R.layout.recy_grid_item_my_book_rack_addbook);
        addItemType(BookShelfOrADsMultEntity.TYPE_ADS_GDT, R.layout.listitem_ad_book_rack_gdt);
        addItemType(BookShelfOrADsMultEntity.TYPE_ADS_BAIDU, R.layout.listitem_ad_native_express);
//        addItemType(BookShelfOrADsMultEntity.TYPE_ADS_CSJ, R.layout.listitem_ad_native_express);
        addItemType(BookShelfOrADsMultEntity.DETAILED, R.layout.recy_grid_item_my_book_rack2);
    }

    public BookRackAdapter() {
        super();
        addItemType(BookShelfOrADsMultEntity.ITEM_ADD_BOOK, R.layout.recy_grid_item_my_book_rack_addbook);
        addItemType(BookShelfOrADsMultEntity.TYPE_ADS_GDT, R.layout.listitem_ad_book_rack_gdt);
        addItemType(BookShelfOrADsMultEntity.TYPE_ADS_BAIDU, R.layout.listitem_ad_native_express);
//        addItemType(BookShelfOrADsMultEntity.TYPE_ADS_CSJ, R.layout.listitem_ad_native_express);
        addItemType(BookShelfOrADsMultEntity.DETAILED, R.layout.recy_grid_item_my_book_rack2);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookShelfOrADsMultEntity item) {
        switch (helper.getItemViewType()) {
            case BookShelfOrADsMultEntity.ITEM_ADD_BOOK:
                if (isManagerMode) {
                    helper.setVisible(R.id.ctl_add_book, false);
                    helper.setEnabled(R.id.ctl_add_book, false);
                } else {
                    helper.setVisible(R.id.ctl_add_book, true);
                    helper.setEnabled(R.id.ctl_add_book, true);
                }
                break;
            case BookShelfOrADsMultEntity.TYPE_ADS_GDT:
                convertGDTAd(helper, item);
                break;
            case BookShelfOrADsMultEntity.TYPE_ADS_BAIDU:

                break;
           /* case BookShelfOrADsMultEntity.TYPE_ADS_CSJ:
                Object adsCSJ = item.getAds();
                if (adsCSJ instanceof TTFeedAd) {
                    AdLogoView ivLogoCSJ = helper.getView(R.id.ad_logo_view);
                    ivLogoCSJ.setAdLogoType(AdNameType.CSJ, adsCSJ);
                    TTFeedAd repCSJ = (TTFeedAd) adsCSJ;
                    List<TTImage> imageList = repCSJ.getImageList();
                    if (imageList != null && imageList.size() > 0) {
                        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_ad_img), imageList.get(0).getImageUrl());
                    }
                    helper.setText(R.id.tv_iv_ad_title, repCSJ.getTitle());
                    helper.setText(R.id.iv_ad_intro, repCSJ.getDescription());
                    helper.setText(R.id.iv_ad_source, repCSJ.getSource());
                }
                break;*/
            case BookShelfOrADsMultEntity.DETAILED:
                covertDefaultBook(helper, item);
                break;
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, BookShelfOrADsMultEntity item, @NotNull List<?> payloads) {
        super.convert(holder, item, payloads);
        if (payloads.get(0).equals("1")) {
            switch (holder.getItemViewType()) {
                case BookShelfOrADsMultEntity.DETAILED:
                    if (isManagerMode) {
                        holder.setVisible(R.id.cb_del_select_book_rack, true);
                        holder.setGone(R.id.tv_book_state, true);
                    } else {
                        AppCompatCheckBox checkBox = holder.getView(R.id.cb_del_select_book_rack);
                        checkBox.setChecked(false);

                        holder.setGone(R.id.cb_del_select_book_rack, true);
                        holder.setVisible(R.id.tv_book_state, true);
                    }
                    break;
                case BookShelfOrADsMultEntity.TYPE_ADS_GDT:
                    if (isManagerMode) {
                        holder.getView(R.id.ctl_touch_layout).setClickable(false);
                        holder.setVisible(R.id.cb_del_select_book_rack, true);
                        holder.setGone(R.id.iv_ad_source, true);
                    } else {
                        holder.getView(R.id.ctl_touch_layout).setClickable(true);
                        AppCompatCheckBox checkBox = holder.getView(R.id.cb_del_select_book_rack);
                        checkBox.setChecked(false);

                        holder.setGone(R.id.cb_del_select_book_rack, true);
                        holder.setVisible(R.id.iv_ad_source, true);
                    }
                    break;
                case BookShelfOrADsMultEntity.ITEM_ADD_BOOK:
                    if (isManagerMode) {
                        holder.setVisible(R.id.ctl_add_book, false);
                        holder.setEnabled(R.id.ctl_add_book, false);
                    } else {
                        holder.setVisible(R.id.ctl_add_book, true);
                        holder.setEnabled(R.id.ctl_add_book, true);
                    }
                    break;
            }
        } else if (payloads.get(0).equals("2")) {
            if (holder.getItemViewType() == BookShelfOrADsMultEntity.DETAILED || holder.getItemViewType() == BookShelfOrADsMultEntity.TYPE_ADS_GDT) {
                AppCompatCheckBox checkBox = holder.getView(R.id.cb_del_select_book_rack);
                checkBox.setChecked(selectLists.get(holder.getLayoutPosition() - getHeaderLayoutCount()));
            }
        }
    }

    private void covertDefaultBook(BaseViewHolder helper, BookShelfOrADsMultEntity item) {
        BookShelfListBean bookShelf = item.getBook();
        String bookName = bookShelf.getNovel_name();
        helper.setText(R.id.tv_book_name, bookName);

        if (TextUtils.isEmpty(bookShelf.getLastChapter())) {
            helper.setText(R.id.tv_reading, (getContext().getString(R.string.txt_markread_null)));
        } else {
            String chapter = bookShelf.getLastChapter();
            helper.setText(R.id.tv_reading, (String.format(getContext().getString(R.string.txt_markread_chapter_x), chapter)));
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
                helper.setText(R.id.tv_book_state, R.string.txt_unshelve);
                helper.setTextColorRes(R.id.tv_book_state, R.color.txt_white);
                helper.setBackgroundResource(R.id.tv_book_state, R.color.txt_gray_999);
            }
            //书的图片
            GlideUtil.loadRoundRect(getContext(), iv, bookShelf.getHttp_image());
        }

        AppCompatCheckBox checkBox = helper.getView(R.id.cb_del_select_book_rack);
        checkBox.setChecked(selectLists.get(helper.getLayoutPosition() - getHeaderLayoutCount()));
        if (isManagerMode) {
            helper.setVisible(R.id.cb_del_select_book_rack, true);
            helper.setGone(R.id.tv_book_state, true);
        } else {
            helper.setGone(R.id.cb_del_select_book_rack, true);
            if (bookShelf.getStatus() == 3) {
                helper.setVisible(R.id.tv_book_state, true);
                helper.setText(R.id.tv_book_state, R.string.txt_update);
                helper.setTextColorRes(R.id.tv_book_state, R.color.txt_white);
                helper.setBackgroundResource(R.id.tv_book_state, R.drawable.shape_bg_book_update);
            } else {
                if (bookShelf.getIs_end() == 1) {
                    helper.setVisible(R.id.tv_book_state, true);
                    helper.setText(R.id.tv_book_state, R.string.txt_end);
                    helper.setTextColorRes(R.id.tv_book_state, R.color.txt_dark_gold);
                    helper.setBackgroundResource(R.id.tv_book_state, R.drawable.shape_border_round2dp_dark_gold);
                } else {
//                    helper.setGone(R.id.tv_book_state, true);
                    helper.setVisible(R.id.tv_book_state, true);
                    helper.setText(R.id.tv_book_state, R.string.txt_serialize);
                    helper.setTextColorRes(R.id.tv_book_state, R.color.txt_dark_gold);
                    helper.setBackgroundResource(R.id.tv_book_state, R.drawable.shape_border_round2dp_dark_gold);
                }
            }
        }
    }

    private void convertGDTAd(BaseViewHolder helper, BookShelfOrADsMultEntity item) {

        RelativeLayout adContainer = helper.getView(R.id.gdt_ad_container);
        ImageView ivLogo = helper.getView(R.id.ad_logo_view);

        //不移除栈底的会导致重复绑定bindAdToView
        if (adContainer.getChildCount() > 1) {
            adContainer.removeViews(1, adContainer.getChildCount() - 1);
        }

        AppCompatCheckBox checkBox = helper.getView(R.id.cb_del_select_book_rack);
        checkBox.setChecked(selectLists.get(helper.getLayoutPosition() - getHeaderLayoutCount()));
        if (isManagerMode) {
            helper.getView(R.id.ctl_touch_layout).setClickable(false);
            helper.setVisible(R.id.cb_del_select_book_rack, true);
            helper.setGone(R.id.iv_ad_source, true);
        } else {
            helper.getView(R.id.ctl_touch_layout).setClickable(true);
            helper.setGone(R.id.cb_del_select_book_rack, true);
            helper.setVisible(R.id.iv_ad_source, true);
        }

    }

}
