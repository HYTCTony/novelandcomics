package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.ui.adapters.BookCoverNameAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.pageradapter.SpecialTopicPagerAdapter;
import com.huli.foxread.ui.widget.ExpandableTextView;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.huli.foxread.utils.UnitConverUtil;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.model.bean.ChapterBean;
import com.huli.page.ui.activity.ReadBookActivity;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.stacklabelview.StackLabel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;

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
    private static final String EXTRA_BOOK_ID = "extra_book_id";
    public static final String RESULT_IS_COLLECTED = "result_is_collected";

    private static final int REQUEST_READ = 1;

    private ImageView ivBookCover;
    private TextView tvHotFlag, tvBookName, tvBookAuthor, tvBookTips;
    private TextView tvBookScore, tvBookReader;
    private MaterialRatingBar ratingBarScore;

    private ExpandableTextView expTextView;
    private StackLabel labelBookTags;
    private TextView tvNewestSectionName, tvTotalSection;

    private TextView btnRelatedRecoRefresh;       //相关推荐---换一换
    private RecyclerView recyclerView;
    private BookCoverNameAdapter mAdapter;

    private TextView tvCopyright;

    private TextView btnAddBookcase;
    private Button btnBeginReading;

    BookShelfListBean data;
    private String nId;

    private boolean isCollected = false;

    public static void start(Context context, int bookId) {
        Intent starter = new Intent(context, BookDetailsActivity.class);
        starter.putExtra(Common.KEY_BOOK_ID, bookId);
        context.startActivity(starter);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.col_black_333), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, false);
    }

    @Override
    public void initParms(Bundle parms) {
        if (parms != null) {
            nId = parms.getString(Common.KEY_BOOK_ID);
        }
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
        tvHotFlag = $(R.id.tv_hot_recommend_flag);
        tvBookName = $(R.id.tv_book_name_dt);
        tvBookAuthor = $(R.id.tv_book_author_dt);
        tvBookTips = $(R.id.tv_book_tips_dt);
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
        mAdapter = new BookCoverNameAdapter();
        recyclerView.setAdapter(mAdapter);

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

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BookEntity entity = mAdapter.getData().get(position);
                Intent intent = new Intent(BookDetailsActivity.this, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        if (TextUtils.isEmpty(nId)) {
            TipDialog.show(BookDetailsActivity.this, R.string.txt_books_do_not_exist, TipDialog.TYPE.ERROR)
                    .setOnDismissListener(this::finish);
            return;
        }


        /*GlideUtil.loadRoundRect(this, ivBookCover, "url");
        tvBookName.setText("天启时代");
        tvBookAuthor.setText("张三");
        tvBookTips.setText("科幻热血·连载·320万字");
        tvBookScore.setText(String.valueOf(2.3f));
        tvBookReader.setText(String.valueOf(12.3f));
        ratingBarScore.setRating(2.3f);

        expTextView.setText(getString(R.string.test_content));
        labelBookTags.setLabels(new String[]{"热血", "玄幻", "口碑佳作", "轻松爽文", "美女", "种马"});

        tvNewestSectionName.setText("第383章-共赴生死");
        tvTotalSection.setText("共383章");*/

        /*mAdapter = new BookCoverNameAdapter();
        recyclerView.setAdapter(mAdapter);
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            datas.add("ssssssss" + i);
        }
        mAdapter.setNewData(datas);*/

        /*SpannableString spannableString = new SpannableString(getString(R.string.tips_copyright));
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.col_red));
        StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);
        AbsoluteSizeSpan aSize = new AbsoluteSizeSpan(DensityUtils.sp2px(this, 15));
        spannableString.setSpan(colorSpan, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(styleSpan_B, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(aSize, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvCopyright.setText(spannableString);*/

        reqNovelDetails(nId);
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.rtl_asBtn_newest_section_dt:

                break;
            case R.id.rtl_asBtn_book_catalogue_dt:

                break;
            case R.id.tv_asBtn_related_recommendation_refresh:
                reqRelatedRecoBooks(nId);
                break;
            case R.id.btn_add_a_bookcase_dt:
                reqAddBookrack(nId);
                break;
            case R.id.btn_begin_reading_dt:
                startActivityForResult(new Intent(this, ReadBookActivity.class)
                        .putExtra(ReadBookActivity.EXTRA_IS_COLLECTED, isCollected)
                        .putExtra(ReadBookActivity.EXTRA_COLL_BOOK, data), REQUEST_READ);
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

    /**
     * 小说详情
     *
     * @param novelId 小说ID
     */
    private void reqNovelDetails(String novelId) {
        OkGo.<String>get(Consts.NOVEL_DETAILS_API)
                .params(Consts.NOVEL_ID, novelId)
                .cacheTime(2 * 60 * 1000)
                .cacheKey(Consts.NOVEL_DETAILS_API + "_" + novelId)
                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<BookShelfListBean> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<BookShelfListBean>>() {
                                });
                        if (entity.error_code == 0) {
                            data = entity.getData();
                            GlideUtil.loadRoundRect(BookDetailsActivity.this, ivBookCover, data.getHttp_image());
                            tvHotFlag.setVisibility(data.getIs_hot() == 1 ? View.VISIBLE : View.GONE);
                            tvBookName.setText(data.getNovel_name());
                            tvBookAuthor.setText(data.getAuthor());
//                            tvBookTips.setText("科幻热血·连载·320万字");
                            String bookTagStr = data.getClassify_name()
                                    + "·" + ((data.getIs_end() == 1) ? "完结" : "连载")
                                    + "·" + UnitConverUtil.formatNumUnit(BookDetailsActivity.this, data.getWord(), R.string.unit_word_w);
                            tvBookTips.setText(bookTagStr);
                            tvBookReader.setText(UnitConverUtil.formatNum(BookDetailsActivity.this, data.getReading_size()));
                            float score = data.getScore();
                            tvBookScore.setText(String.valueOf(score));
                            ratingBarScore.setRating(score / 2);
                            expTextView.setText(data.getIntroduce());

                            List<String> tags = data.getTag();
//                            labelBookTags.setLabels(new String[]{"热血", "玄幻", "口碑佳作", "轻松爽文", "美女", "种马"});
                            if (tags != null && tags.size() > 0) {
                                labelBookTags.setLabels(tags);
                            }
                            ChapterBean newChapter = data.getNewChapter();
                            if (newChapter != null) {
                                tvNewestSectionName.setText(String.format(getString(R.string.txt_chapter_x), newChapter.getChapter(), newChapter.getName()));
                                tvNewestSectionName.setText(newChapter.getName());
                            }
                            tvTotalSection.setText(String.format(getString(R.string.txt_total_chapter_x), data.getChapter_sum()));

                            //  版权说明
                            String copyRightStr = data.getCopyright_name();
                            SpannableString spannableString = new SpannableString(getString(R.string.tips_copyright_colon) + copyRightStr);
                            ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(BookDetailsActivity.this, R.color.col_red));
                            StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);
                            AbsoluteSizeSpan aSize = new AbsoluteSizeSpan(DensityUtils.sp2px(BookDetailsActivity.this, 15));
                            spannableString.setSpan(colorSpan, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(styleSpan_B, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(aSize, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            tvCopyright.setText(spannableString);

                            //请求相关推荐
                            reqRelatedRecoBooks(nId);
                        } else {
                            TipDialog.show(BookDetailsActivity.this, R.string.txt_books_do_not_exist, TipDialog.TYPE.ERROR)
                                    .setOnDismissListener(() -> finish());
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        TipDialog.show(BookDetailsActivity.this, R.string.txt_network_maybe_exceptions, TipDialog.TYPE.ERROR)
                                .setOnDismissListener(() -> finish());
                    }
                });
    }


    /**
     * 相关推荐
     *
     * @param novelId 小说ID
     */
    private void reqRelatedRecoBooks(String novelId) {
        OkGo.<String>get(Consts.NOVEL_NOMINATE_API)
                .params(Consts.NOVEL_ID, novelId)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<BookEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BookEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<BookEntity> booksList = entity.getData();
                            if (booksList != null && booksList.size() > 0) {
                                mAdapter.setNewData(booksList);
                            }
                        }
                    }
                });
    }

    /**
     * 小说详情---加入书架
     *
     * @param novelId 小说ID
     */
    private void reqAddBookrack(String novelId) {
        OkGo.<String>get(Consts.BOOKRACK_ADD_API)
                .params(Consts.NOVEL_ID, novelId)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            TipDialog.show(BookDetailsActivity.this, entity.msg, TipDialog.TYPE.SUCCESS);
                            //TODO do something
                        } else {
                            TipDialog.show(BookDetailsActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果进入阅读页面收藏了，页面结束的时候，就需要返回改变收藏按钮
        if (requestCode == REQUEST_READ) {
            if (data == null) {
                return;
            }

            isCollected = data.getBooleanExtra(RESULT_IS_COLLECTED, false);
            if (isCollected) {
                tvBookReader.setText("继续阅读");
            }
        }
    }
}