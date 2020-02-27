package com.huli.foxread.ui.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.BookCoverNameAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.pageradapter.SpecialTopicPagerAdapter;
import com.huli.foxread.ui.widget.ExpandableTextView;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.stacklabelview.StackLabel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class BookDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBookCover;
    private TextView tvBookName, tvBookAuthor, tvBookTag;
    private TextView tvBookScore, tvBookReader;
    private MaterialRatingBar ratingBarScore;

    private ExpandableTextView expTextView;
    private StackLabel labelBookTags;
    private TextView tvNewestSectionName, tvTotalSection;

    private TextView btnRelatedRecoRefresh;
    private RecyclerView recyclerView;
    private BookCoverNameAdapter mAdapter;

    private TextView tvCopyright;

    private TextView btnAddBookcase;
    private Button btnBeginReading;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.col_black_333), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, false);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_book_details;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, "");

        ivBookCover = $(R.id.iv_book_cover_dt);
        tvBookName = $(R.id.tv_book_name_dt);
        tvBookAuthor = $(R.id.tv_book_author_dt);
        tvBookTag = $(R.id.tv_book_tag_dt);
        tvBookScore = $(R.id.tv_book_score_dt);
        tvBookReader = $(R.id.tv_book_reader_dt);
        ratingBarScore = $(R.id.ratingBar_book_score_dt);

        expTextView = $(R.id.etv_book_synopsis);
        labelBookTags = $(R.id.stackLabelView_tag_book_dt);

        tvNewestSectionName = $(R.id.tv_book_newest_section_name);
        tvTotalSection = $(R.id.tv_book_total_section);

        initSpecialTopicView();

        btnRelatedRecoRefresh = $(R.id.tv_asBtn_related_recommendation_refresh);
        recyclerView = $(R.id.recyclerView_related_recommendation);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(this, 16), true));

        tvCopyright = $(R.id.tv_tips_copyright_dt);

        btnAddBookcase = $(R.id.btn_add_a_bookcase_dt);
        btnBeginReading = $(R.id.btn_begin_reading_dt);
    }

    @Override
    public void setListener() {
        $(R.id.rtl_asBtn_newest_section_dt).setOnClickListener(this);
        $(R.id.rtl_asBtn_book_catalogue_dt).setOnClickListener(this);
        btnRelatedRecoRefresh.setOnClickListener(this);
        btnAddBookcase.setOnClickListener(this);
        btnBeginReading.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        GlideUtil.loadRoundRect(this, ivBookCover, "url");
        tvBookName.setText("天启时代");
        tvBookAuthor.setText("张三");
        tvBookTag.setText("科幻热血·连载·320万字");
        tvBookScore.setText(String.valueOf(2.3f));
        tvBookReader.setText(String.valueOf(12.3f));
        ratingBarScore.setRating(2.3f);

        expTextView.setText(getString(R.string.test_content));
        labelBookTags.setLabels(new String[]{"热血", "玄幻", "口碑佳作", "轻松爽文", "美女", "种马"});

        tvNewestSectionName.setText("第383章-共赴生死");
        tvTotalSection.setText("共383章");

        mAdapter = new BookCoverNameAdapter();
        recyclerView.setAdapter(mAdapter);
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            datas.add("ssssssss" + i);
        }
        mAdapter.setNewData(datas);

        SpannableString spannableString = new SpannableString(getString(R.string.tips_copyright));
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.col_red));
        StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);
        AbsoluteSizeSpan aSize = new AbsoluteSizeSpan(DensityUtils.sp2px(this, 15));
        spannableString.setSpan(colorSpan, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(styleSpan_B, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(aSize, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvCopyright.setText(spannableString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtl_asBtn_newest_section_dt:

                break;
            case R.id.rtl_asBtn_book_catalogue_dt:

                break;
            case R.id.tv_asBtn_related_recommendation_refresh:

                break;
            case R.id.btn_add_a_bookcase_dt:

                break;
            case R.id.btn_begin_reading_dt:

                break;
            default:
                break;
        }
    }

    private void initSpecialTopicView() {
        ViewPager vpSpt = $(R.id.viewPager_special_topic);

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(i);
        }
        vpSpt.setOffscreenPageLimit(list.size());
        vpSpt.setAdapter(new SpecialTopicPagerAdapter(this, list));
        vpSpt.setPageMargin(DensityUtils.dp2px(this, 16));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_download) {
            Tos.showShort(this, "下载");
        } else if (item.getItemId() == R.id.action_share) {
            Tos.showShort(this, "分享");
        }
        return super.onOptionsItemSelected(item);
    }

}
