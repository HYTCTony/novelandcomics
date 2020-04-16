package com.huli.foxread.ui.activities;

import android.app.Activity;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity2;
import com.huli.foxread.ui.adapters.BookCoverNameAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.widget.ChapterPopup;
import com.huli.foxread.ui.widget.ExpandableTextView;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.BookChapter;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.ui.activity.ReadBookActivity;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.stacklabelview.StackLabel;
import com.lxj.xpopup.XPopup;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
    private BookCoverNameAdapter mAdapter;

    private TextView tvCopyright;

    private TextView btnAddBookcase;
    private Button btnBeginReading;

    BookShelfListBean data;
    List<BookChapter> chapters = new ArrayList<>();
    private String nId;

    private boolean isCollected = false;
    private int chapter = -1;

   /* public static void start(Context context, int bookId) {
        Intent starter = new Intent(context, BookDetailsActivity.class);
        starter.putExtra(Common.KEY_BOOK_ID, bookId);
        context.startActivity(starter);
    }*/

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
        RecyclerView recyclerView = $(R.id.recyclerView_related_recommendation);
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
                BookEntity2 entity = mAdapter.getData().get(position);
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

        reqNovelDetails(nId);
        loadCategory(nId);
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.rtl_asBtn_newest_section_dt:
                chapter = chapters.size() - 1;
                if (chapter >= 0) {
                    openBook(chapter);
                } else {
                    Toast.makeText(this, "获取章节失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rtl_asBtn_book_catalogue_dt:
                ChapterPopup popup = new ChapterPopup(BookDetailsActivity.this, chapters);
                new XPopup.Builder(BookDetailsActivity.this)
                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .asCustom(popup)
                        .show();
                popup.setListener(new ChapterPopup.onClickListener() {
                    @Override
                    public void onButtonClick(int num) {
                        chapter = num;
                        if (chapter >= 0) {
                            openBook(chapter);
                        } else {
                            Toast.makeText(BookDetailsActivity.this, "获取章节失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.tv_asBtn_related_recommendation_refresh:
                reqRelatedRecoBooks(nId);
                break;
            case R.id.btn_add_a_bookcase_dt:
                if (isCollected) {
                    TipDialog.show(BookDetailsActivity.this, "已经加入书架！", TipDialog.TYPE.ERROR);
                    return;
                }
                reqAddBookrack(nId);
                break;
            case R.id.btn_begin_reading_dt:
                if (chapters.isEmpty()) {
                    TipDialog.show(BookDetailsActivity.this, "获取章节失败！", TipDialog.TYPE.ERROR);
                    return;
                }
                openBook(chapter);
                break;
            default:
                break;
        }
    }

    private void openBook(int chapter) {
        startActivityForResult(new Intent(this, ReadBookActivity.class)
                .putExtra(ReadBookActivity.EXTRA_IS_COLLECTED, isCollected)
                .putExtra(ReadBookActivity.EXTRA_PAGE_POS, chapter)
                .putExtra(ReadBookActivity.EXTRA_COLL_BOOK, data), REQUEST_READ);
    }

    private void initSpecialTopicView() {
        ViewPager vpSpt = $(R.id.viewPager_special_topic);

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(i);
        }
        vpSpt.setOffscreenPageLimit(list.size());
//        vpSpt.setAdapter(new SpecialTopicPagerAdapter(this, list));
        vpSpt.setPageMargin(DensityUtils.dp2px(this, 16));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_report) {
            startActivity(new Intent(BookDetailsActivity.this, GoToFeedBackActivity.class));
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
                            String bookTagStr = data.getClassify_name()
                                    + " · " + ((data.getIs_end() == 1) ? "完结" : "连载")
                                    + " · " + FigureProcessor.formatWordNum(BookDetailsActivity.this, data.getWord());
                            tvBookTips.setText(bookTagStr);
                            tvBookReader.setText(FigureProcessor.formatNum(BookDetailsActivity.this, data.getReading_size()));
                            float score = data.getScore();
                            tvBookScore.setText(String.valueOf(score));
                            ratingBarScore.setRating(score / 2);
                            expTextView.setText(data.getIntroduce());

                            List<String> tags = data.getTag();
//                            labelBookTags.setLabels(new String[]{"热血", "玄幻", "口碑佳作", "轻松爽文", "美女", "种马"});
                            if (tags != null && tags.size() > 0) {
                                labelBookTags.setLabels(tags);
                            }

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

                            if (data.getIs_exist_bookshelf() == 0) {
                                isCollected = false;
                                btnAddBookcase.setText("加入书架");
                                btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_black_191919));
                            } else {
                                isCollected = true;
                                btnAddBookcase.setText("已加入书架");
                                btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_gray));
                            }

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
                        LzyResponse<List<BookEntity2>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BookEntity2>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<BookEntity2> booksList = entity.getData();
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
        OkGo.<String>post(Consts.BOOKRACK_ADD_API)
                .params(Consts.NOVEL_ID, novelId)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            TipDialog.show(BookDetailsActivity.this, entity.msg, TipDialog.TYPE.SUCCESS);
                            isCollected = true;
                            btnAddBookcase.setText("已加入书架");
                            btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_gray));
                        } else {
                            TipDialog.show(BookDetailsActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

    /**
     * 用途：小说章节列表(GET)
     *
     * @param novelId 小说ID
     */
    private void loadCategory(String novelId) {
        OkGo.<String>get(Consts.NOVEL_NOVELCHAPTERLIST_API)
                .params(Consts.NOVEL_ID, novelId)
                .execute(new LtbCallback(BookDetailsActivity.this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<BookChapter>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BookChapter>>>() {
                                });
                        if (entity.error_code == 0) {
                            chapters.clear();
                            chapters.addAll(entity.getData());
                            //目录章节数
                            tvTotalSection.setText(String.format(getString(R.string.txt_total_chapter_x), chapters.size()));
                            //最新一章
                            BookChapter newChapter = chapters.get(chapters.size() - 1);
                            if (newChapter != null) {
                                tvNewestSectionName.setText(String.format(getString(R.string.txt_chapter_x), newChapter.getChapter(), newChapter.getName()));
                                tvNewestSectionName.setText(newChapter.getName());
                            }
                        } else {
                            chapter = -1;
                            MessageDialog.show(BookDetailsActivity.this, R.string.nb_common_tip, R.string.hint_content_no_book_message, R.string.txt_got_it)
                                    .setCancelable(false)
                                    .setOnOkButtonClickListener((baseDialog, v) -> {
                                        finish();
                                        baseDialog.doDismiss();
                                        return false;
                                    });
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
                btnAddBookcase.setText("已加入书架");
                btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_gray));
            }
        }
    }

    //可能存在BookDetailsActivity套娃，这个办法暂不可行
    @Override
    public void onBackPressed() {
        Stack<Activity> activityStack = FrApp.getInstance().mActivityManager.getActivityStack();
        boolean mainActExist = false;//栈中是否存在MainActivity
        int selfActCount = 0;
        for (Activity act : activityStack) {
            if (act instanceof MainActivity) {
                mainActExist = true;
            }
            if (act instanceof BookDetailsActivity) {
                selfActCount++;
            }
        }
        if (!mainActExist && selfActCount == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        super.onBackPressed();
    }
}