package com.nnmedia.read.ui.adapters;

import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.page.utils.TimeUtils;
import com.nnmedia.read.entity.ReadRecordEntity;
import com.nnmedia.read.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

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
    public void setNewInstance(List<ReadRecordEntity> list) {
        super.setNewInstance(list);
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
            if (selectLists.get(i)) {
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

    /**
     * 单本加入书架
     *
     * @param pos
     */
    public void add2Bookshelf(int pos) {
        getData().get(pos).setExist_bookshelf(1);
        notifyItemChanged(pos + getHeaderLayoutCount(), "1");
    }


    public ReadingRecordsAdapter(boolean isManagerMode) {
        super(R.layout.recy_list_item_reading_record);
        this.isManagerMode = isManagerMode;
        addChildClickViewIds(R.id.tv_asBtn_add_to_Bookshelf_or_go2read);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, ReadRecordEntity item) {
        holder.setText(R.id.tv_book_name, item.getProfileNovel() == null ? "书，走丢了" : item.getProfileNovel().getName());
        holder.setText(R.id.tv_read_book_section, item.getChapter_name());
        holder.setText(R.id.tv_last_reading_time, "阅读时间：" + TimeUtils.formatFriendly(item.getCreatetime()));
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), item.getProfileNovel() == null ? "" : item.getProfileNovel().getHttp_image());

        if (item.getExist_bookshelf() == 1) {       //已在书架
            holder.setText(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, "去阅读");
            holder.setBackgroundResource(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, R.drawable.shape_border_round6dp_gray);
            holder.setTextColorRes(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, R.color.txt_col_365565);
        } else {
            holder.setText(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, "加书架");
            holder.setBackgroundResource(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, R.drawable.shape_border_round6dp_red);
            holder.setTextColorRes(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, R.color.txt_red);
        }

        AppCompatCheckBox checkBox = holder.getView(R.id.checkBoxSample_check_book);
        if (isManagerMode) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(selectLists.get(holder.getLayoutPosition()));
            holder.setGone(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, true);
        } else {
            checkBox.setVisibility(View.GONE);
            holder.setVisible(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, true);
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ReadRecordEntity item, @NotNull List<?> payloads) {
        super.convert(holder, item, payloads);
        if (payloads.get(0).equals("1")) {
            holder.setText(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, "去阅读");
            holder.setBackgroundResource(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, R.drawable.shape_border_round6dp_gray);
            holder.setTextColorRes(R.id.tv_asBtn_add_to_Bookshelf_or_go2read, R.color.txt_col_365565);
        }
    }
}
