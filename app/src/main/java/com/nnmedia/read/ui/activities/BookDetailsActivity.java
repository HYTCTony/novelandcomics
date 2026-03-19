package com.nnmedia.read.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.stacklabelview.StackLabel;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.model.bean.BookChapter;
import com.nnmedia.page.model.bean.BookShelfListBean;
import com.nnmedia.page.model.bean.ChapterBean;
import com.nnmedia.page.model.local.BookRepository;
import com.nnmedia.page.ui.activity.ReadBookActivity;
import com.nnmedia.page.utils.SpanUtils;
import com.nnmedia.read.FrApp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.entity.BookReview;
import com.nnmedia.read.listeners.OnRecyCbCheckListener;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.adapters.BookCoverNameAdapter;
import com.nnmedia.read.ui.adapters.BookReviewAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.decoration.GridSpacingItemDecoration;
import com.nnmedia.read.ui.dialogs.ChaptersDialogFragment;
import com.nnmedia.read.ui.dialogs.DownloadSelectDialog;
import com.nnmedia.read.ui.dlpopwindow.DLPopupWindow;
import com.nnmedia.read.ui.widget.ExpandableTextView;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.FigureProcessor;
import com.nnmedia.read.utils.GlideUtil;
import com.nnmedia.read.utils.PackageUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.rxjava.rxlife.RxLife;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import rxhttp.wrapper.exception.ParseException;

public class BookDetailsActivity extends BaseActivity implements View.OnClickListener, OnRecyCbCheckListener {

    //    private static final String EXTRA_BOOK_ID = "extra_book_id";
    public static final String RESULT_IS_COLLECTED = "result_is_collected";
    public static final String RESULT_IS_BOOK_CHAPTER = "result_is_book_chapter";

    private static final int REQUEST_READ = 1;

    private TextView tvTopTitle;
    private ImageView btnShowMenu;
    /**
     * 自定义弹窗实例化
     */
    private DLPopupWindow popupWindow;

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

    private LinearLayout btnDownLoadBook;
    private TextView btnAddBookcase;
    private Button btnBeginReading;

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
        Toolbar mToolbar = $(R.id.toolbar_normal);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        tvTopTitle = $(R.id.tv_toolbar_center_title);
        tvTopTitle.setText("");
        btnShowMenu = $(R.id.iv_asBtn_show_menu);

        scvBookDetail = $(R.id.scv_book_detail);
        ivBookCover = $(R.id.iv_book_cover_dt);
        tvHotFlag = $(R.id.tv_hot_recommend_flag);
        tvBookName = $(R.id.tv_book_name_dt);
        tvBookAuthor = $(R.id.tv_book_author_dt);
        tvBookTips = $(R.id.tv_book_tips_dt);
        tvBookScore = $(R.id.tv_book_score_dt);
        tvBookReader = $(R.id.tv_book_greet_dt);
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

        btnDownLoadBook = $(R.id.btn_download_book);
        btnAddBookcase = $(R.id.btn_add_a_bookcase_dt);
        btnBeginReading = $(R.id.btn_begin_reading_dt);

//        btnDownLoadBook.setVisibility(UserInfoCache.getIsVip(this) ? View.VISIBLE : View.GONE);
        btnMoreReview = $(R.id.tv_asBtn_check_more_review);
        btnMoreReview.setVisibility(View.GONE);
        btnReview = $(R.id.tv_asBtn_review);
        RecyclerView rcReview = $(R.id.recyclerView_review);
        rcReview.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new BookReviewAdapter();
        reviewAdapter.setAnimationEnable(false);
        rcReview.setAdapter(reviewAdapter);
        reviewAdapter.setEmptyView(R.layout.layout_empty_no_comments);
    }

    @Override
    public void setListener() {
        btnShowMenu.setOnClickListener(this);
        $(R.id.rtl_asBtn_newest_section_dt).setOnClickListener(this);
        $(R.id.rtl_asBtn_book_catalogue_dt).setOnClickListener(this);
        btnRelatedRecoRefresh.setOnClickListener(this);
        btnDownLoadBook.setOnClickListener(this);
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
                    tvTopTitle.setText(bookBean.getNovel_name());
                }
            } else {
                tvTopTitle.setText("");
            }

        });

        //评论
        reviewAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (isVip()) {
                BookReview bookReview = reviewAdapter.getData().get(position);
                ReviewDetailActivity.start(this, bookReview, bookBean.getNovel_name(), bookBean.getHttp_image(), bookBean.getScore());
            } else {
                buyVip("VIP会员才能使用书评功能，是否前往获取？");
            }
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
            case R.id.iv_asBtn_show_menu:
//                if (popupWindow == null) {
//                    List<DLPopItem> mList = new ArrayList<>();
//                    DLPopItem dlItem = new DLPopItem(R.drawable.ic_warning_lamp_black_18dp, "举报", 0x272F3A);
//                    mList.add(dlItem);
//                    dlItem = new DLPopItem(R.drawable.ic_share_18dp, "分享", 0x272F3A);
//                    mList.add(dlItem);
//                    popupWindow = new DLPopupWindow(this, mList, DLPopupWindow.STYLE_DEF);
//                    popupWindow.setOnItemClickListener((pos) -> {
//                        if (pos == 0) {
//                            startActivity(new Intent(BookDetailsActivity.this, GoToFeedBackActivity.class));
//                        } else if (pos == 1) {
//                            doShare();
//                        }
//                    });
//                }
//                popupWindow.showAsDropDown(view, 0, -DensityUtils.dp2px(this, 12));
                break;
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
                if (UserInfoCache.getIsTourist(BookDetailsActivity.this)){
                    goRegister("亲爱的用户，为了有更好的阅读体验，请登录以后使用书架功能！");
                }else{
                    if (isCollected) {
//                    TipDialog.show(BookDetailsActivity.this, R.string.txt_has_been_added_2_the_shelf, TipDialog.TYPE.ERROR);
                        return;
                    }
                    reqAdd2BookRack(nId);
                }
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
                if (isVip()) {
                    if (bookBean != null) {
                        WriteBookReviewActivity.start4Result(this, WriteBookReviewActivity.REQCODE_WRITE_REVIEW, bookBean.getNovel_id());
                    }
                } else {
                    buyVip("VIP会员才能使用书评功能，是否前往获取？");
                }
                break;
            case R.id.btn_download_book:                     //缓存书籍
                DownloadSelectDialog.newInstance()
                        .setLayoutId(R.layout.dialog_download_select)
                        .setConvertListener(viewConvertListener)
                        .setDimAmout(0.5f)
                        .setShowBottom(true)
                        .setAnimStyle(R.style.PaymentDialogAnim)
                        .show(getSupportFragmentManager());
                break;
            default:
                break;
        }
    }

    private void goRegister(String content) {
        MessageDialog.show(BookDetailsActivity.this, "提示", content, "去登录", "返回")
                .setCancelable(true)
                .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        baseDialog.doDismiss();
                        return false;
                    }
                })
                .setOnOkButtonClickListener((baseDialog, v) -> {
                    LoginActivity2.start(this);
                    baseDialog.doDismiss();
                    return false;
                });
    }

    private DownloadSelectDialog.PConvertListener viewConvertListener = (holder, dialog) -> {
        List<BookChapter> bookChapters = new ArrayList<>();
        holder.setOnClickListener(R.id.btn_1, view -> {
            if (isVip()) {
                if (chapters.size() > 20) {
                    for (int i = 0; i < 20; i++) {
                        bookChapters.add(chapters.get(i));
                    }
                    loadChapter(BookDetailsActivity.this, bookBean.getNovel_id(), bookChapters);
                } else {
                    loadChapter(BookDetailsActivity.this, bookBean.getNovel_id(), chapters);
                }
                TipDialog.show(BookDetailsActivity.this, "已经添加后台下载，请继续浏览书籍", TipDialog.TYPE.SUCCESS);
            } else {
                buyVip("VIP会员才能使用云缓存书功能，是否前往获取？");
            }
            dialog.dismiss();
        });
        holder.setOnClickListener(R.id.btn_2, view -> {
            if (isVip()) {
                if (chapters.size() > 100) {
                    for (int i = 0; i < 100; i++) {
                        bookChapters.add(chapters.get(i));
                    }
                    loadChapter(BookDetailsActivity.this, bookBean.getNovel_id(), bookChapters);
                } else {
                    loadChapter(BookDetailsActivity.this, bookBean.getNovel_id(), chapters);
                }
                TipDialog.show(BookDetailsActivity.this, "已经添加后台下载，请继续浏览书籍", TipDialog.TYPE.SUCCESS);
            } else {
                buyVip("VIP会员才能使用云缓存书功能，是否前往获取？");
            }
            dialog.dismiss();
        });
        holder.setOnClickListener(R.id.btn_3, view -> {
            if (isVip()) {
                loadChapter(BookDetailsActivity.this, bookBean.getNovel_id(), chapters);
                TipDialog.show(BookDetailsActivity.this, "已经添加后台下载，请继续浏览书籍", TipDialog.TYPE.SUCCESS);
            } else {
                buyVip("VIP会员才能使用云缓存书功能，是否前往获取？");
            }
            dialog.dismiss();
        });
    };

    private void openBook(int chapter) {
        FrApp.getInstance().mActivityManager.finishActivityclass(ReadBookActivity.class);
        startActivityForResult(new Intent(this, ReadBookActivity.class)
                .putExtra(ReadBookActivity.EXTRA_IS_COLLECTED, isCollected)
                .putExtra(ReadBookActivity.EXTRA_PAGE_POS, chapter)
                .putExtra(ReadBookActivity.EXTRA_COLL_BOOK, bookBean), REQUEST_READ);
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

    private Handler handler = new Handler(msg -> {
        if (msg.what == 88) {
            showLoadingDialog();
        }
        return false;
    });

    /**
     * 小说详情
     *
     * @param novelId 小说ID
     */
    private void reqNovelDetails(String novelId) {
        RxHttp.get(Consts.NOVEL_DETAILS_API)
                .add(Consts.N_ID, novelId)
                .asResponse(BookShelfListBean.class)
                .doOnSubscribe(disposable -> {
                    handler.sendEmptyMessageDelayed(88, 500);
//                    showLoadingDialog();
                })
                .doFinally(() -> {
                    handler.removeMessages(88);
                    dismissLoadingDialog();
                })
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(bean -> {
                    if (bean == null) {
                        TipDialog.show(BookDetailsActivity.this, R.string.txt_books_do_not_exist, TipDialog.TYPE.ERROR)
                                .setOnDismissListener(this::finish);
                        return;
                    }
                    bookBean = bean;
                    GlideUtil.loadRoundRect(BookDetailsActivity.this, ivBookCover, bookBean.getHttp_image());

                    tvHotFlag.setVisibility(bookBean.getIs_hot() == 1 ? View.VISIBLE : View.GONE);
                    tvBookName.setText(bookBean.getNovel_name());
                    tvBookAuthor.setText(bookBean.getAuthor());
                    SpannableStringBuilder bookTagStr = new SpanUtils(BookDetailsActivity.this)
                            .append(((bookBean.getIs_end() == 1) ? getString(R.string.txt_end) : getString(R.string.txt_serialize)))
                            .setForegroundColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.col_red))
                            .appendLine(" · " + FigureProcessor.formatWordNum(BookDetailsActivity.this, bookBean.getWord()))
                            .create();
//                    String bookTagStr = bookBean.getClassify_name()
//                            + " · " + ((bookBean.getIs_end() == 1) ? getString(R.string.txt_end) : getString(R.string.txt_serialize))
//                            + " · " + FigureProcessor.formatWordNum(BookDetailsActivity.this, bookBean.getWord());
                    tvBookTips.setText(bookTagStr);
                    tvBookReader.setText(FigureProcessor.formatNum(BookDetailsActivity.this, bookBean.getGreet()));
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
                    if (TextUtils.isEmpty(copyRightStr)) {
                        tvCopyright.setVisibility(View.GONE);
                    } else {
                        tvCopyright.setVisibility(View.VISIBLE);
                        SpannableString spannableString = new SpannableString(getString(R.string.tips_copyright_colon) + copyRightStr);
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(BookDetailsActivity.this, R.color.col_red));
                        StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);
                        AbsoluteSizeSpan aSize = new AbsoluteSizeSpan(DensityUtils.sp2px(BookDetailsActivity.this, 15));
                        spannableString.setSpan(colorSpan, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(styleSpan_B, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                        spannableString.setSpan(aSize, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        tvCopyright.setText(spannableString);
                    }

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
                }, (OnError) error -> {
                    if (error.getThrowable() instanceof ParseException) {
                        TipDialog.show(BookDetailsActivity.this, R.string.txt_books_do_not_exist, TipDialog.TYPE.ERROR)
                                .setOnDismissListener(this::finish);
                    } else {
                        TipDialog.show(BookDetailsActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR)
                                .setOnDismissListener(this::finish);
                    }
                });
    }

    /**
     * 相关推荐
     *
     * @param novelId 小说ID
     */
    private void reqRelatedRecoBooks(String novelId) {
        RxHttp.get(Consts.NOVEL_NOMINATE_API)
                .add(Consts.N_ID, novelId)
                .asResponseList(BookEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(list -> {
                    if (list != null && list.size() > 0) {
                        mAdapter.setList(list);
                    }
                });
    }

    /**
     * 评论列表
     *
     * @param novelId 小说ID
     */
    private void reqReviewList(String novelId) {
        RxHttp.get(Consts.APPRAISE_LIST_API)
                .add(Consts.N_ID, novelId)
                .add(Consts.PAGE, 1)
                .add(Consts.PAGE_SIZE, 3)
                .asResponsePageList(BookReview.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(entity -> {
                    int totalNum = entity.getTotal();
                    List<BookReview> reviews = entity.getData();
                    reviewAdapter.setList(reviews);
                    if (totalNum >= 3) {
                        btnMoreReview.setText(String.format(getString(R.string.txt_more_review_x), totalNum));
                        btnMoreReview.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * 点赞或取消点赞
     *
     * @param reviewId  评论ID
     * @param novelId   小说ID
     * @param isChecked #
     */
    private void giveALikeOrCancel(String reviewId, String novelId, boolean isChecked) {
        RxHttp.get(Consts.APPRAISE_LIKE_API)
                .add(Consts.N_ID, reviewId)
                .add(Consts.NOVEL_ID, novelId)
                .add(Consts.PREFER, isChecked ? 1 : 2)
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
                });
    }

    /**
     * 小说详情---加入书架
     *
     * @param novelId 小说ID
     */
    private void reqAdd2BookRack(String novelId) {
        RxHttp.postForm(Consts.BOOKRACK_ADD_API)
                .add(Consts.N_ID, novelId)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
                    TipDialog.show(BookDetailsActivity.this, R.string.txt_in_kookshelf, TipDialog.TYPE.SUCCESS);
                    isCollected = true;
                    btnAddBookcase.setText(R.string.txt_in_kookshelf);
                    btnAddBookcase.setTextColor(ContextCompat.getColor(BookDetailsActivity.this, R.color.txt_gray));
                }, (OnError) error -> TipDialog.show(BookDetailsActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR).setTipTime(800));
    }

    /**
     * 用途：小说章节列表(GET)
     *
     * @param novelId 小说ID
     */
    private void loadCategory(String novelId) {
        RxHttp.get(Consts.NOVEL_NOVELCHAPTERLIST_API)
                .add(Consts.N_ID, novelId)
                .asResponseList(BookChapter.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(list -> {
                    chapters.clear();
//                    for (BookChapter data : list) {
//                        if (!isVip() && data.getChapter() > list.size() / 3)
//                            data.setLock(true);
//                        else
//                            data.setLock(false);
//                    }
                    chapters.addAll(list);
                    //目录章节数
                    tvTotalSection.setText(String.format(getString(R.string.txt_total_chapter_x), chapters.size()));
                    //最新一章
                    BookChapter newChapter = chapters.get(chapters.size() - 1);
                    if (newChapter != null) {
                        tvNewestSectionName.setText(String.format(getString(R.string.txt_chapter_x), newChapter.getChapter(), newChapter.getName()));
                        tvNewestSectionName.setText(newChapter.getName());
                    }
                }, (OnError) error -> {
                    chapter = -1;
                    if (error.getThrowable() instanceof ParseException) {
                        MessageDialog.show(BookDetailsActivity.this, R.string.nb_common_tip, R.string.hint_content_no_book_message, R.string.txt_got_it)
                                .setCancelable(false)
                                .setOnOkButtonClickListener((baseDialog, v) -> {
                                    finish();
                                    baseDialog.doDismiss();
                                    return false;
                                });
                    }
                });
    }

    private boolean isVip() {
        if (UserInfoCache.getSuperVip(BookDetailsActivity.this) == 1) {
            return true;
        }
        if (UserInfoCache.getIsVip(BookDetailsActivity.this)) {
            return true;
        }
        return false;
    }

    private void buyVip(String content) {
        MessageDialog.show(BookDetailsActivity.this, "成为VIP", content, "去激活", "下次再说")
                .setCancelable(true)
                .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        baseDialog.doDismiss();
                        return false;
                    }
                })
                .setOnOkButtonClickListener((baseDialog, v) -> {
//                    if (UserInfoCache.isGetFree(this))
//                        FreeGetVipActivity.start(this);
//                    else
                        GoldExchangeVipActivity.start(this, 0);
                    baseDialog.doDismiss();
                    return false;
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
                chapters = (List<BookChapter>) data.getSerializableExtra(RESULT_IS_BOOK_CHAPTER);
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

    public void loadChapter(AppCompatActivity context, String bookId, List<BookChapter> chapters) {
        int size = chapters.size();
        // 将要下载章节，转换成网络请求。
        for (int i = 0; i < size; ++i) {
            BookChapter bookChapter = chapters.get(i);
            RxHttp.postForm(Consts.NOVEL_CONTENT_API)
                    .add(Consts.NOVEL_ID, bookChapter.getNovel_id())
                    .add(Consts.CHAPTER_ID, bookChapter.getId())
                    .add(Consts.CHAPTER, bookChapter.getChapter())
                    .add("os", "android")
                    .add("version", PackageUtils.getVersionCode(context))
                    .asResponse(ChapterBean.class)
                    .to(RxLife.toMain(context))
                    .subscribe(date -> {
                        //存储数据
                        BookRepository.getInstance().saveChapterInfo(bookId, bookChapter.getName(), date.getContent());
                    }, (OnError) error -> TipDialog.show(BookDetailsActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
        }
    }
}