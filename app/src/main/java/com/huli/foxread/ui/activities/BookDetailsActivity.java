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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.BookReview;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.entity.eventbus.BookRackChangeEvent;
import com.huli.foxread.listeners.OnRecyCbCheckListener;
import com.huli.foxread.ui.adapters.BookCoverNameAdapter;
import com.huli.foxread.ui.adapters.BookReviewAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.dialogs.ChaptersDialogFragment;
import com.huli.foxread.ui.fragments.MainBookrackFragment;
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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class BookDetailsActivity extends BaseActivity implements View.OnClickListener, OnRecyCbCheckListener {

    private static final String EXTRA_BOOK_ID = "extra_book_id";
    public static final String RESULT_IS_COLLECTED = "result_is_collected";

    private static final int REQUEST_READ = 1;

    private Toolbar mToolbar;

    private NestedScrollView scvBookDetail;
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

    private RecyclerView rcReview;
    private BookReviewAdapter reviewAdapter;
    private TextView btnReview, btnMoreReview;


    BookShelfListBean bookBean;
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
        mToolbar = $(R.id.toolbar_book_detail);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        scvBookDetail = $(R.id.scv_book_detail);
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

        btnMoreReview = $(R.id.tv_asBtn_check_more_review);
        btnMoreReview.setVisibility(View.GONE);
        btnReview = $(R.id.tv_asBtn_review);
        rcReview = $(R.id.recyclerView_review);
        rcReview.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new BookReviewAdapter();
        reviewAdapter.setAnimationEnable(false);
        rcReview.setAdapter(reviewAdapter);
        reviewAdapter.setEmptyView(R.layout.layout_empty_no_comments);
    }

    @Override
    public void setListener() {
        $(R.id.rtl_asBtn_newest_section_dt).setOnClickListener(this);
        $(R.id.rtl_asBtn_book_catalogue_dt).setOnClickListener(this);
        btnRelatedRecoRefresh.setOnClickListener(this);
        btnAddBookcase.setOnClickListener(this);
        btnBeginReading.setOnClickListener(this);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            BookEntity entity = mAdapter.getData().get(position);
            Intent intent = new Intent(BookDetailsActivity.this, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
            startActivity(intent);
        });

        scvBookDetail.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > DensityUtils.dp2px(BookDetailsActivity.this, 36)) {
                if (bookBean != null) {
                    mToolbar.setTitle(bookBean.getNovel_name());
                }
            } else {
                mToolbar.setTitle("");
            }

        });

        //评论
        reviewAdapter.setOnItemClickListener((adapter, view, position) -> {
            BookReview bookReview = reviewAdapter.getData().get(position);
            ReviewDetailActivity.start(this, bookReview, bookBean.getNovel_name(), bookBean.getHttp_image(), bookBean.getScore());
        });
        reviewAdapter.setmRecyCbCheckListener(this);
        btnMoreReview.setOnClickListener(this);
        btnReview.setOnClickListener(this);

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
                    Toast.makeText(this, R.string.txt_acquisition_chapters_failed, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rtl_asBtn_book_catalogue_dt:
                ChaptersDialogFragment dialogFragment = ChaptersDialogFragment.newInstance(chapters);
                dialogFragment.show(getSupportFragmentManager(), "chapters");
                dialogFragment.setListener(num -> {
                    chapter = num;
                    if (chapter >= 0) {
                        openBook(chapter);
                    } else {
                        Toast.makeText(BookDetailsActivity.this, R.string.txt_acquisition_chapters_failed, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case R.id.tv_asBtn_related_recommendation_refresh:
                reqRelatedRecoBooks(nId);
                break;
            case R.id.btn_add_a_bookcase_dt:
                if (isCollected) {
                    TipDialog.show(BookDetailsActivity.this, R.string.txt_has_been_added_2_the_shelf, TipDialog.TYPE.ERROR);
                    return;
                }
                reqAddBookrack(nId);
                break;
            case R.id.btn_begin_reading_dt:
                if (chapters.isEmpty()) {
                    TipDialog.show(BookDetailsActivity.this, R.string.txt_acquisition_chapters_failed, TipDialog.TYPE.ERROR);
                    return;
                }
                openBook(-1);
                break;
            case R.id.tv_asBtn_check_more_review:           //更多评论
                if (bookBean != null) {
                    AllBookReviewActivity.start4Result(this, AllBookReviewActivity.REQCODE_CHECK_ALL_REVIEW, bookBean);
                }
                break;
            case R.id.tv_asBtn_review:                      //写评论
                if (bookBean != null) {
                    WriteBookReviewActivity.start4Result(this, WriteBookReviewActivity.REQCODE_WRITE_REVIEW, bookBean.getNovel_id());
                }
                break;
            default:
                break;
        }
    }

    private void openBook(int chapter) {
        startActivityForResult(new Intent(this, ReadBookActivity.class)
                .putExtra(ReadBookActivity.EXTRA_IS_COLLECTED, isCollected)
                .putExtra(ReadBookActivity.EXTRA_PAGE_POS, chapter)
                .putExtra(ReadBookActivity.EXTRA_COLL_BOOK, bookBean), REQUEST_READ);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        //反射 解决ICON不显示
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_report) {
            startActivity(new Intent(BookDetailsActivity.this, GoToFeedBackActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    /*点赞*/
    @Override
    public void onCbCheckChanged(CompoundButton view, boolean b, int pos) {
        if (onMoreClick()) {
            return;
        }
        BookReview bookReview = reviewAdapter.getData().get(pos);
        giveALikeOrCancel(bookReview.getId(), bookReview.getNovel_id(), b);
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
                            bookBean = entity.getData();
                            GlideUtil.loadRoundRect(BookDetailsActivity.this, ivBookCover, bookBean.getHttp_image());

                            tvHotFlag.setVisibility(bookBean.getIs_hot() == 1 ? View.VISIBLE : View.GONE);
                            tvBookName.setText(bookBean.getNovel_name());
                            tvBookAuthor.setText(bookBean.getAuthor());
                            String bookTagStr = bookBean.getClassify_name()
                                    + " · " + ((bookBean.getIs_end() == 1) ? getString(R.string.txt_end) : getString(R.string.txt_serialize))
                                    + " · " + FigureProcessor.formatWordNum(BookDetailsActivity.this, bookBean.getWord());
                            tvBookTips.setText(bookTagStr);
                            tvBookReader.setText(FigureProcessor.formatNum(BookDetailsActivity.this, bookBean.getReading_size()));
                            float score = bookBean.getScore();
                            tvBookScore.setText(String.valueOf(score));
                            ratingBarScore.setRating(score / 2);
                            expTextView.setText(bookBean.getIntroduce());

                            List<String> tags = bookBean.getTag();
//                            labelBookTags.setLabels(new String[]{"热血", "玄幻", "口碑佳作", "轻松爽文", "美女", "种马"});
                            if (tags != null && tags.size() > 0) {
                                labelBookTags.setLabels(tags);
                            }

                            //  版权说明
                            String copyRightStr = bookBean.getCopyright_name();
                            SpannableString spannableString = new SpannableString(getString(R.string.tips_copyright_colon) + copyRightStr);
                            ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(BookDetailsActivity.this, R.color.col_red));
                            StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);
                            AbsoluteSizeSpan aSize = new AbsoluteSizeSpan(DensityUtils.sp2px(BookDetailsActivity.this, 15));
                            spannableString.setSpan(colorSpan, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(styleSpan_B, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(aSize, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            tvCopyright.setText(spannableString);

                            if (bookBean.getIs_exist_bookshelf() == 1) {
                                isCollected = true;
                                btnAddBookcase.setText(R.string.txt_in_kookshelf);
                                btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_gray));
                            } else {
                                isCollected = false;
                                btnAddBookcase.setText(R.string.txt_add_2_kookshelf);
                                btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_black_191919));
                            }

                            //请求相关推荐
                            reqRelatedRecoBooks(nId);

                            //获取评论列表
                            reqReviewList(nId);
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
                                mAdapter.setList(booksList);
                            }
                        }
                    }
                });
    }

    /**
     * 评论列表
     *
     * @param novelId 小说ID
     */
    private void reqReviewList(String novelId) {
        OkGo.<String>get(Consts.APPRAISE_LIST_API)
                .params(Consts.NOVEL_ID, novelId)
                .params(Consts.PAGE, 1)
                .params(Consts.PAGE_SIZE, 3)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<BookReview>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<BookReview>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<BookReview>> datas = entity.getData();
                            int totalNum = datas.getTotal();
                            List<BookReview> reviews = datas.getData();
                            reviewAdapter.setList(reviews);
                            if (totalNum >= 3) {
                                btnMoreReview.setText(String.format(getString(R.string.txt_more_review_x), totalNum));
                                btnMoreReview.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    /**
     * 点赞或取消点赞
     *
     * @param reviewId  评论ID
     * @param novelId   小说ID
     * @param isChecked
     */
    private void giveALikeOrCancel(String reviewId, String novelId, boolean isChecked) {
        OkGo.<String>get(Consts.APPRAISE_LIKE_API)
                .params(Consts.REVIEW_ID, reviewId)
                .params(Consts.BOOK_ID, novelId)
                .params(Consts.PREFER, isChecked ? 1 : 2)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                       /* LzyResponse<PagingWarpper<List<BookReview>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<BookReview>>>>() {
                                });
                        if (entity.error_code == 0) {

                        }*/
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
                            btnAddBookcase.setText(R.string.txt_in_kookshelf);
                            btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_gray));

                            //通知刷新书架
                            EventBus.getDefault().post(new BookRackChangeEvent(false));
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
        switch (requestCode) {
            case REQUEST_READ:
                if (data == null) {
                    return;
                }
                isCollected = data.getBooleanExtra(RESULT_IS_COLLECTED, false);
                if (isCollected) {
                    btnAddBookcase.setText(R.string.txt_in_kookshelf);
                    btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_gray));
                }

                break;

            case WriteBookReviewActivity.REQCODE_WRITE_REVIEW:
            case AllBookReviewActivity.REQCODE_CHECK_ALL_REVIEW:
                if (bookBean == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    reqReviewList(bookBean.getNovel_id());
                }
                break;
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