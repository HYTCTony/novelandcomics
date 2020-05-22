package com.huli.foxread.ui.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.contact.Common;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.multi.HpBGModuleEntity;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.pageradapter.SpecialTopicPagerAdapter2;
import com.huli.foxread.ui.widget.WrapViewPager;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 书城---男生女生
 */
public class HpBoyGirlAdapter extends BaseMultiItemQuickAdapter<HpBGModuleEntity, BaseViewHolder> {

    public HpBoyGirlAdapter(List<HpBGModuleEntity> data) {
        super(data);
        // 绑定 layout 对应的 type
        addItemType(HpBGModuleEntity.TYPE_HOT_BILLBOARD, R.layout.recy_group_item_type_hot_billboard);                      //（一行两个）布局
        addItemType(HpBGModuleEntity.TYPE_SPECIAL, R.layout.recy_group_item_type_special);                                  //专题布局
        addItemType(HpBGModuleEntity.TYPE_FIRST_MONOPOLIZE, R.layout.recy_group_item_type_first_monopolize);                //首本书独占一行---四网格

        addItemType(HpBGModuleEntity.TYPE_CATE_EXC_WORKS, R.layout.recy_group_item_type_rc_view);                           //分类佳作布局
        addItemType(HpBGModuleEntity.TYPE_GRID_4, R.layout.recy_group_item_type_rc_view);                                   //好评佳作
        addItemType(HpBGModuleEntity.TYPE_LIST, R.layout.recy_group_item_type_list);                                        //列表
        addItemType(HpBGModuleEntity.TYPE_HOT_SEARCH, R.layout.recy_group_item_type_rc_view);                               //实时热搜类型的
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, HpBGModuleEntity item) {
        switch (item.getItemType()) {
            case HpBGModuleEntity.TYPE_HOT_BILLBOARD:
                holder.setText(R.id.iv_title_hotlist_today, item.getName());
                if (!TextUtils.isEmpty(item.getIntroduce())) {
                    holder.setVisible(R.id.tv_sub_title, true);
                    holder.setText(R.id.tv_sub_title, item.getIntroduce());
                } else {
                    holder.setGone(R.id.tv_sub_title, true);
                }

                RecyclerView rvHotBoard = holder.getView(R.id.recyclerView_hotlist);
                rvHotBoard.setLayoutManager(new GridLayoutManager(getContext(), 4));
                if (rvHotBoard.getAdapter() == null) {
                    rvHotBoard.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(getContext(), 16), true));
                    HotTodayAdapter innerAdapter = new HotTodayAdapter(item.getNovel());
                    innerAdapter.setAnimationEnable(true);
                    rvHotBoard.setAdapter(innerAdapter);
                    innerAdapter.setOnItemClickListener(itemClickListener);
                } else {
                    HotTodayAdapter innerAdapter = (HotTodayAdapter) rvHotBoard.getAdapter();
                    innerAdapter.setNewInstance(item.getNovel());
                }
                break;
            case HpBGModuleEntity.TYPE_SPECIAL:
                holder.setText(R.id.iv_title_special_work, item.getName());
                if (!TextUtils.isEmpty(item.getIntroduce())) {
                    holder.setVisible(R.id.tv_subhead_special_work, true);
                    holder.setText(R.id.tv_subhead_special_work, item.getIntroduce());
                } else {
                    holder.setGone(R.id.tv_subhead_special_work, true);
                }
                WrapViewPager vpSpt = holder.getView(R.id.viewPager_special_works);
                vpSpt.setPageMargin(DensityUtils.dp2px(getContext(), 8));
                List<BookEntity> specialList = item.getNovel();
                vpSpt.setOffscreenPageLimit(specialList.size());
                SpecialTopicPagerAdapter2 stPagerAdapter = new SpecialTopicPagerAdapter2(getContext(), specialList);
                vpSpt.setAdapter(stPagerAdapter);
                stPagerAdapter.setmOnPagerItemClickListener(bookID -> {
                    Intent intent = new Intent(getContext(), BookDetailsActivity.class);
                    intent.putExtra(Common.KEY_BOOK_ID, bookID);
                    getContext().startActivity(intent);
                });
                break;
            case HpBGModuleEntity.TYPE_CATE_EXC_WORKS:
                initTitleViewNor(holder, item);

                RecyclerView rvCateExc = holder.getView(R.id.recyclerView_book_hp);
                rvCateExc.setLayoutManager(new GridLayoutManager(getContext(), 4));
                if (rvCateExc.getAdapter() == null) {
                    rvCateExc.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(getContext(), 16), true));
                    BookCoverNameTypeAdapter innerAdapter = new BookCoverNameTypeAdapter(item.getNovel());
                    innerAdapter.setAnimationEnable(true);
                    rvCateExc.setAdapter(innerAdapter);
                    innerAdapter.setOnItemClickListener(itemClickListener);
                } else {
                    BookCoverNameTypeAdapter innerAdapter = (BookCoverNameTypeAdapter) rvCateExc.getAdapter();
                    innerAdapter.setNewInstance(item.getNovel());
                }
                break;

            case HpBGModuleEntity.TYPE_LIST:
                initTitleViewNor(holder, item);

                RecyclerView rvList = holder.getView(R.id.recyclerView_book_hp);
                rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                if (rvList.getAdapter() == null) {
                    BooksListAdapter innerAdapter = new BooksListAdapter(item.getNovel());
                    innerAdapter.setAnimationEnable(true);
                    rvList.setAdapter(innerAdapter);
                    innerAdapter.setOnItemClickListener(itemClickListener);
                } else {
                    BooksListAdapter innerAdapter = (BooksListAdapter) rvList.getAdapter();
                    innerAdapter.setNewInstance(item.getNovel());
                }
                break;
            case HpBGModuleEntity.TYPE_FIRST_MONOPOLIZE:
                initTitleViewNor(holder, item);

                List<BookEntity> novelList = item.getNovel();
                if (novelList.size() > 1) {
                    BookEntity book = novelList.get(0);
                    GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), book.getHttp_image(), 0);
                    holder.setText(R.id.tv_book_title, book.getName());
                    holder.setText(R.id.tv_book_score, book.getScore() + getContext().getString(R.string.unit_score));
                    holder.setText(R.id.tv_book_description, book.getIntroduce());
                    holder.getView(R.id.layout_first_item_mplz).setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), BookDetailsActivity.class);
                        intent.putExtra(Common.KEY_BOOK_ID, book.getId());
                        getContext().startActivity(intent);
                    });

                    RecyclerView rvMplz = holder.getView(R.id.recyclerView_book_grid_mplz);
                    rvMplz.setLayoutManager(new GridLayoutManager(getContext(), 4));
                    if (rvMplz.getAdapter() == null) {
                        rvMplz.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(getContext(), 16), false));
                        BookCoverNameAuthorAdapter innerAdapter = new BookCoverNameAuthorAdapter(novelList.subList(1, novelList.size()));
                        innerAdapter.setAnimationEnable(true);
                        rvMplz.setAdapter(innerAdapter);
                        innerAdapter.setOnItemClickListener(itemClickListener);
                    } else {
                        BookCoverNameAuthorAdapter innerAdapter = (BookCoverNameAuthorAdapter) rvMplz.getAdapter();
                        innerAdapter.setNewInstance(novelList.subList(1, novelList.size()));
                    }
                }
                break;

            case HpBGModuleEntity.TYPE_HOT_SEARCH:
                initTitleViewNor(holder, item);

                RecyclerView rvSearch = holder.getView(R.id.recyclerView_book_hp);
                rvSearch.setLayoutManager(new GridLayoutManager(getContext(), 4));
                if (rvSearch.getAdapter() == null) {
                    rvSearch.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(getContext(), 16), true));
                    AttTopSearchAdapter innerAdapter = new AttTopSearchAdapter(item.getNovel());
                    innerAdapter.setAnimationEnable(true);
                    rvSearch.setAdapter(innerAdapter);
                    innerAdapter.setOnItemClickListener(itemClickListener);
                } else {
                    AttTopSearchAdapter innerAdapter = (AttTopSearchAdapter) rvSearch.getAdapter();
                    innerAdapter.setNewInstance(item.getNovel());
                }
                break;

            case HpBGModuleEntity.TYPE_GRID_4:
                initTitleViewNor(holder, item);

                RecyclerView rvGrid = holder.getView(R.id.recyclerView_book_hp);
                rvGrid.setLayoutManager(new GridLayoutManager(getContext(), 4));
                if (rvGrid.getAdapter() == null) {
                    rvGrid.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(getContext(), 16), true));
                    BookCoverNameScoreAdapter innerAdapter = new BookCoverNameScoreAdapter(item.getNovel());
                    innerAdapter.setAnimationEnable(true);
                    rvGrid.setAdapter(innerAdapter);
                    innerAdapter.setOnItemClickListener(itemClickListener);
                } else {
                    BookCoverNameScoreAdapter innerAdapter = (BookCoverNameScoreAdapter) rvGrid.getAdapter();
                    innerAdapter.setNewInstance(item.getNovel());
                }
                break;

            default:
                break;
        }
    }

    private OnItemClickListener itemClickListener = (adapter, view, position) -> {
        List list = adapter.getData();
        if (list.size() > position) {
            Object data = list.get(position);
            if (data instanceof BookEntity) {
                BookEntity book = (BookEntity) data;
                Intent intent = new Intent(getContext(), BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, book.getId());
                getContext().startActivity(intent);
            }
        }
    };

    /**
     * 设置标题栏
     *
     * @param holder
     * @param item
     */
    private void initTitleViewNor(@NonNull BaseViewHolder holder, HpBGModuleEntity item) {
        holder.setText(R.id.iv_title_book_hp, item.getName());
        if (!TextUtils.isEmpty(item.getIntroduce())) {
            holder.setVisible(R.id.tv_sub_title, true);
            holder.setText(R.id.tv_sub_title, item.getIntroduce());
        } else {
            holder.setGone(R.id.tv_sub_title, true);
        }
        LinearLayout linearLayout = holder.getView(R.id.ll_star_tag_content_view);
        linearLayout.removeAllViews();
        String labelStr = item.getLabel();
        if (!TextUtils.isEmpty(labelStr)) {
            String[] split = labelStr.split("，");
            for (String str : split) {
                TextView textView = new TextView(getContext());
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_brown_12dp);
                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                }
                textView.setCompoundDrawables(drawable, null, null, null);
                textView.setCompoundDrawablePadding(DensityUtils.dp2px(getContext(), 4));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_dark_gold));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(DensityUtils.dp2px(getContext(), 8), 0, 0, 0);
                textView.setText(str);
                linearLayout.addView(textView);
            }
        }
    }
}
