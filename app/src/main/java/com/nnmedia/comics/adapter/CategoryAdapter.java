package com.nnmedia.comics.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nnmedia.comics.R;
import com.nnmedia.comics.model.CategoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类适配器
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<CategoryBean> categories = new ArrayList<>();
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    public void setCategories(List<CategoryBean> categories) {
        if (categories != null) {
            this.categories.clear();
            this.categories.addAll(categories);
            notifyDataSetChanged();
        }
    }

    public void addCategories(List<CategoryBean> categories) {
        if (categories != null) {
            this.categories.addAll(categories);
            notifyDataSetChanged();
        }
    }

    public void setSelectedPosition(int position) {
        if (selectedPosition != position) {
            int oldPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(position);
        }
    }

    public CategoryBean getSelectedCategory() {
        if (selectedPosition >= 0 && selectedPosition < categories.size()) {
            return categories.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mkz_layout_item_category_theme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryBean category = categories.get(position);
        holder.tvCategory.setText(category.getName());

        // 设置选中状态
        if (position == selectedPosition) {
            holder.tvCategory.setSelected(true);
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.mkz_red));
        } else {
            holder.tvCategory.setSelected(false);
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.mkz_gray8));
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(position, category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 根据实际布局文件查找控件ID
            tvCategory = itemView.findViewById(R.id.tv_category);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(int position, CategoryBean category);
    }
}
