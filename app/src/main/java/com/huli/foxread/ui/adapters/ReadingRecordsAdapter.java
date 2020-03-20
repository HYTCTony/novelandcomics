package com.huli.foxread.ui.adapters;

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


    public int getSelectedCount() {
        int cc = 0;
        for (int i = 0; i < selectLists.size(); i++) {
            if (selectLists.valueAt(i)) {
                cc++;
            }
        }
        Log.e("sssss", "getSelectedCount = " + cc);
        return cc;
    }

    public String getSelectedIds() {
        StringBuffer buffer = new StringBuffer();
        int cc = 0;
        for (int i = 0; i < selectLists.size(); i++) {
            if (selectLists.valueAt(i)) {
                cc++;
                buffer.append(getData().get(i).getId());
                buffer.append(",");
            }
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    public String getSelectedBookId() {
        StringBuffer buffer = new StringBuffer();
        int cc = 0;
        for (int i = 0; i < selectLists.size(); i++) {
            if (selectLists.valueAt(i)) {
                cc++;
                buffer.append(getData().get(i).getNovel_id());
                buffer.append(",");
            }
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    public int funCheck(int position) {
        //TODO 通过position获取 id 作为 key
//        selectLists.put(key, !selectLists.get(key));

        selectLists.put(position, !selectLists.get(position));
        notifyItemChanged(position);
        return getSelectedCount();
    }

    public int funCheckAll() {
        for (int i = 0; i < getData().size(); i++) {
            //TODO 通过position获取 id 作为 key
//            selectLists.put(key, !selectLists.get(key));
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
        holder.setText(R.id.tv_book_name, item.getProfileNovel().getNovel_name());
        holder.setText(R.id.tv_read_book_section, item.getChapter_name());
        holder.setText(R.id.tv_last_reading_time, "阅读时间：" + TimeUtils.formatFriendly(item.getCreatetime()));
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), item.getProfileNovel().getHttp_image(), 0);

        AppCompatCheckBox checkBox = holder.getView(R.id.checkBoxSample_check_book);
        if (isManagerMode) {
            checkBox.setVisibility(View.VISIBLE);
            //TODO 通过position获取 id 作为 key
//            checkBox.setChecked(selectLists.get(key));
            checkBox.setChecked(selectLists.get(holder.getLayoutPosition()));
        } else {
            checkBox.setVisibility(View.GONE);
        }
    }

}
