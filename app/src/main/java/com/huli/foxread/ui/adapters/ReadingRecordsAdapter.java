package com.huli.foxread.ui.adapters;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

public class ReadingRecordsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

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
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_book_name, "末世四万年");
        holder.setText(R.id.tv_read_book_section, "第163章 事与愿违");
        holder.setText(R.id.tv_read_book_progress, "36.5%");
        holder.setText(R.id.tv_last_reading_time, "刚刚");

        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), "url", 0);

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
