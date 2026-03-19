package com.nnmedia.page.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.azhon.appupdate.dialog.NumberProgressBar;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.page.model.bean.Font;
import com.nnmedia.page.utils.Constant;

public class FontAdapter extends BaseQuickAdapter<Font, BaseViewHolder> {

    public FontAdapter() {
        super(R.layout.item_font);
    }

    @Override
    protected void convert(BaseViewHolder helper, Font item) {
        ImageView name = helper.getView(R.id.iv_font_name);
        Glide.with(getContext()).load(item.getHttp_image()).into(name);

        TextView size = helper.getView(R.id.tv_file_size);
        size.setText(item.getFile_size());

        NumberProgressBar progressBar = helper.getView(R.id.progress_bar);
        progressBar.setProgress(item.getProgress());

        if (item.getFile_name().equals(Constant.FONT_TYPE) || item.isDownload()) {
            helper.setGone(R.id.iv_select, false);
            helper.setGone(R.id.btn_download, true);
            helper.setGone(R.id.progress_bar, true);
        } else {
            int state = item.getState();
            switch (state) {
                case 0://未开始
                    helper.setGone(R.id.iv_select, true);
                    helper.setGone(R.id.btn_download, false);
                    helper.setGone(R.id.progress_bar, true);
                    break;
                case 1://等待中..
                    helper.setGone(R.id.iv_select, true);
                    helper.setGone(R.id.btn_download, true);
                    helper.setGone(R.id.progress_bar, false);
                    break;
                case 2://下载中..
                    helper.setGone(R.id.iv_select, true);
                    helper.setGone(R.id.btn_download, true);
                    helper.setGone(R.id.progress_bar, false);
                    break;
                case 3://已暂停
                    helper.setGone(R.id.iv_select, true);
                    helper.setGone(R.id.btn_download, false);
                    helper.setGone(R.id.progress_bar, true);
                    break;
                case 4://已完成
                    helper.setGone(R.id.iv_select, false);
                    helper.setGone(R.id.btn_download, true);
                    helper.setGone(R.id.progress_bar, true);
                    break;
                case 5://下载失败
                    helper.setGone(R.id.iv_select, true);
                    helper.setGone(R.id.btn_download, false);
                    helper.setGone(R.id.progress_bar, true);
                    break;
                case 6://已取消
                    helper.setGone(R.id.iv_select, true);
                    helper.setGone(R.id.btn_download, false);
                    helper.setGone(R.id.progress_bar, true);
                    break;
            }
        }
        if (item.isSelect()) {
            helper.setBackgroundResource(R.id.iv_select, R.mipmap.icon_select_red);
        } else {
            helper.setBackgroundResource(R.id.iv_select, R.mipmap.icon_select_white);
        }
        addChildClickViewIds(R.id.btn_download);
    }

}