package com.huli.foxread.ui.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.ReadRecordEntity;
import com.huli.foxread.utils.GlideUtil;
import com.huli.page.utils.TimeUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

public class ReadingRecordsAdapter extends BaseQuickAdapter<ReadRecordEntity, BaseViewHolder> implements LoadMoreModule {

    private boolean isManagerMode = false;

    public void setManagerMode(boolean managerMode) {
        isManagerMode = managerMode;
        clearSelect();
        notifyDataSetChanged();
    }

    private SparseBooleanArray selectLists = new SparseBooleanArray();

    public SparseBooleanArray getSelectedItem() {
        return selectLists;
    }

    private void clearSelect() {
        selectLists.clear();
    }


    @Override
    public void setNewData(List<ReadRecordEntity> data) {
        super.setNewData(data);
        selectLists = new SparseBooleanArray();
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < selectLists.size(); i++) {
            if (selectLists.valueAt(i)) {
                count++;
            }
        }
        return count;
    }

    public String getSelectedIds() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < getData().size(); i++) {
            if (i< selectLists.size() && selectLists.get(i)) {
                buffer.append(getData().get(i).getId());
                buffer.append(",");
            }
        }
        if (buffer.length() > 0)
            buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    public String getSelectedBookId() {
        List<ReadRecordEntity> datas = getData();
        StringBuilder mBuilder = new StringBuilder();
        for (int i = 0; i < datas.size(); i++) {
            ReadRecordEntity entity = datas.get(i);
            if (selectLists.get(i)) {
                String novelId = entity.getProfileNovel().getId();
                if (!TextUtils.isEmpty(novelId)) {
                    mBuilder.append(novelId);
                    mBuilder.append(",");
                }
            }
        }
        if (mBuilder.length() > 0)
            mBuilder.deleteCharAt(mBuilder.length() - 1);
        return mBuilder.toString();
    }

    public int funCheck(int position) {
        Log.e("Ssssssssssss", "sssss====" + selectLists.get(position));
        selectLists.put(position, !selectLists.get(position));
        notifyItemChanged(position);
        return getSelectedCount();
    }

    public int funCheckAll() {
        for (int i = 0; i < getData().size(); i++) {
            selectLists.put(i, true);
        }
        notifyDataSetChanged();
        return getSelectedCount();
    }


    public ReadingRecordsAdapter(boolean isManagerMode) {
        super(R.layout.recy_list_item_reading_record);
        this.isManagerMode = isManagerMode;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, ReadRecordEntity item) {
        holder.setText(R.id.tv_book_name, item.getProfileNovel() == null ? "书，走丢了" : item.getProfileNovel().getName());
        holder.setText(R.id.tv_read_book_section, item.getChapter_name());
        holder.setText(R.id.tv_last_reading_time, "阅读时间：" + TimeUtils.formatFriendly(item.getCreatetime()));
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), item.getProfileNovel() == null ? "" :
                item.getProfileNovel().getHttp_image(), 0);

        AppCompatCheckBox checkBox = holder.getView(R.id.checkBoxSample_check_book);
        if (isManagerMode) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(selectLists.get(holder.getLayoutPosition()));
        } else {
            checkBox.setVisibility(View.GONE);
        }
    }

}
