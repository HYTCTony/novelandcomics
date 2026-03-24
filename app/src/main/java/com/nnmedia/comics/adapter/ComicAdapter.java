package com.nnmedia.comics.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nnmedia.comics.R;
import com.nnmedia.comics.model.ComicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 漫画列表适配器
 */
public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {

    private Context context;
    private List<ComicBean> comics = new ArrayList<>();
    private OnComicClickListener listener;

    public ComicAdapter(Context context) {
        this.context = context;
    }

    public void setComics(List<ComicBean> comics) {
        if (comics != null) {
            this.comics.clear();
            this.comics.addAll(comics);
            notifyDataSetChanged();
        }
    }

    public void addComics(List<ComicBean> comics) {
        if (comics != null) {
            this.comics.addAll(comics);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        this.comics.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mkz_layout_item_category_comic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComicBean comic = comics.get(position);

        // 设置标题
        if (holder.tvTitle != null) {
            holder.tvTitle.setText(comic.getTitle());
        }

        // 设置作者
        if (holder.tvAuthor != null) {
            holder.tvAuthor.setText(comic.getAuthor());
        }

        // 设置最新章节
        if (holder.tvLatestChapter != null) {
            holder.tvLatestChapter.setText(comic.getLatestChapter());
        }

        // 设置封面
        if (holder.ivCover != null && comic.getCover() != null) {
            Glide.with(context)
                    .load(comic.getCover())
                    .into(holder.ivCover);
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onComicClick(position, comic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comics.size();
    }

    public void setOnComicClickListener(OnComicClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvLatestChapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 根据实际布局文件查找控件ID
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvLatestChapter = itemView.findViewById(R.id.tv_latest_chapter);
        }
    }

    public interface OnComicClickListener {
        void onComicClick(int position, ComicBean comic);
    }
}
