package com.huli.foxread.ui.adapters;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baidu.mobad.feeds.NativeResponse;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.multi.BookShelfOrADsMultEntity;
import com.huli.foxread.utils.GlideUtil;
import com.huli.page.model.bean.BookShelfListBean;
import com.hytc.ads.AdLogoView;
import com.hytc.ads.other.AdNameType;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * 书架
 */
public class BookRackAdapter extends BaseMultiItemQuickAdapter<BookShelfOrADsMultEntity, BaseViewHolder> {

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

                break;
            case BookShelfOrADsMultEntity.TYPE_ADS_GDT:
                convertGDTAd(helper, item);
                break;
            case BookShelfOrADsMultEntity.TYPE_ADS_BAIDU:
                Object adsBAIDU = item.getAds();
                if (adsBAIDU instanceof NativeResponse) {
                    AdLogoView ivLogoBD = helper.getView(R.id.ad_logo_view);
                    ivLogoBD.setAdLogoType(AdNameType.BAIDU, adsBAIDU);
                    NativeResponse repBAIDU = (NativeResponse) adsBAIDU;
                    GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_ad_img), repBAIDU.getImageUrl());
                    helper.setText(R.id.tv_iv_ad_title, repBAIDU.getTitle());
                    helper.setText(R.id.iv_ad_intro, repBAIDU.getDesc());
                    helper.setText(R.id.iv_ad_source, repBAIDU.getBrandName());
                }
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
                BookShelfListBean bookShelf = item.getBook();
                helper.setText(R.id.tv_book_name, bookShelf.getNovel_name());
                boolean isEnd = bookShelf.getIs_end() == 1;
                helper.setText(R.id.tv_book_state, isEnd ? R.string.txt_end : R.string.txt_serialize);
                helper.setTextColorRes(R.id.tv_book_state, isEnd ? R.color.txt_dark_gold : R.color.txt_red);
                helper.setBackgroundResource(R.id.tv_book_state, isEnd ? R.drawable.shape_border_round2dp_dark_gold : R.drawable.shape_border_round2dp_red);
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
                        helper.setText(R.id.tv_book_state, R.string.txt_unshelve);
                        helper.setTextColorRes(R.id.tv_book_state, R.color.txt_white);
                        helper.setBackgroundResource(R.id.tv_book_state, R.color.txt_gray_999);
                    }
                    //书的图片
                    GlideUtil.loadRoundRect(getContext(), iv, bookShelf.getHttp_image());
                }
                break;
        }
    }

    private void convertGDTAd(BaseViewHolder helper, BookShelfOrADsMultEntity item) {
        NativeUnifiedADData adsGDT = (NativeUnifiedADData) item.getAds();

        NativeAdContainer adContainer = helper.getView(R.id.gdt_ad_container);
        AdLogoView ivLogo = helper.getView(R.id.ad_logo_view);

        //不移除栈底的会导致重复绑定bindAdToView
        if (adContainer.getChildCount() > 1) {
            adContainer.removeViews(1, adContainer.getChildCount() - 1);
        }

        ivLogo.setAdLogoType(AdNameType.GDT, adsGDT);
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_ad_img), adsGDT.getImgUrl());
        helper.setText(R.id.tv_iv_ad_title, adsGDT.getTitle());
        helper.setText(R.id.iv_ad_intro, adsGDT.getDesc());
        helper.setText(R.id.iv_ad_source, "腾讯广告");

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(helper.getView(R.id.ctl_touch_layout));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adContainer.getLayoutParams());
        layoutParams.gravity = Gravity.BOTTOM | Gravity.START;
//        layoutParams.rightMargin = DensityUtils.dp2px(getContext(), UIUtils.getScreenWidthDp(getContext())) - ivAdImg.getWidth() - DensityUtils.dp2px(getContext(), 16);
//        layoutParams.leftMargin = DensityUtils.dp2px(getContext(), 16) + ivAdImg.getWidth() / 2;
//        layoutParams.bottomMargin = DensityUtils.dp2px(getContext(), 8);
        adsGDT.bindAdToView(getContext(), adContainer, layoutParams, clickableViews);
            /*adsGDT.setNativeAdEventListener(new NativeADEventListener() {
                @Override
                public void onADExposed() {
//                    Log.e("ssssssssss", "广告曝光");
                }

                @Override
                public void onADClicked() {
//                    Log.e("ssssssssss", "点击广告");
                }

                @Override
                public void onADError(AdError adError) {

                }

                @Override
                public void onADStatusChanged() {

                }
            });*/
    }

}
