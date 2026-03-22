package com.nnmedia.comics.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.stacklabelview.StackLabel;
import com.nnmedia.comics.component.AppGetter;
import com.nnmedia.comics.demo.TencentDemo;
import com.nnmedia.comics.manager.ComicManager;
import com.nnmedia.comics.manager.PreferenceManager;
import com.nnmedia.comics.model.Chapter;
import com.nnmedia.comics.model.Comic;
import com.nnmedia.novel.R;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.ui.widget.ExpandableTextView;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.GlideUtil;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import java.util.List;

/**
 * 漫画详情页 Activity
 */
public class ComicsDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_COMIC_SOURCE = "extra_comic_source";
    public static final String EXTRA_COMIC_CID = "extra_comic_cid";

    private static final int REQUEST_READ = 1;

    private Toolbar mToolbar;
    private TextView tvTopTitle;
    private ImageView btnShowMenu;

    private NestedScrollView scvComicsDetail;
    private ImageView ivComicsCover;
    private TextView tvHotFlag, tvComicsName, tvComicsAuthor, tvComicsTips;
    private TextView tvComicsScore, tvComicsReader;
    private MaterialRatingBar ratingBarScore;

    private ExpandableTextView expTextView;
    private StackLabel labelComicsTags;
    private TextView tvNewestSectionName, tvTotalSection;

    private TextView btnRelatedRecoRefresh;

    private TextView tvCopyright;

    private LinearLayout btnDownLoadComics;
    private TextView btnCollectComics;
    private Button btnBeginReading;

    private ComicManager mComicManager;

    private Comic comic;
    private int source;
    private String cid;

    private boolean isCollected = false;

    /**
     * 启动漫画详情页
     *
     * @param context 上下文
     * @param source  漫画源
     * @param cid     漫画ID
     */
    public static void start(Context context, int source, String cid) {
        Intent starter = new Intent(context, ComicsDetailActivity.class);
        starter.putExtra(EXTRA_COMIC_SOURCE, source);
        starter.putExtra(EXTRA_COMIC_CID, cid);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {
        mToolbar = findViewById(R.id.toolbar_normal);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvTopTitle = findViewById(R.id.tv_toolbar_center_title);
        tvTopTitle.setText("");
        btnShowMenu = findViewById(R.id.iv_asBtn_show_menu);

        scvComicsDetail = findViewById(R.id.scv_comics_detail);
        ivComicsCover = findViewById(R.id.iv_comics_cover_dt);
        tvHotFlag = findViewById(R.id.tv_hot_recommend_flag);
        tvComicsName = findViewById(R.id.tv_comics_name_dt);
        tvComicsAuthor = findViewById(R.id.tv_comics_author_dt);
        tvComicsTips = findViewById(R.id.tv_comics_tips_dt);
        tvComicsScore = findViewById(R.id.tv_comics_score_dt);
        tvComicsReader = findViewById(R.id.tv_comics_greet_dt);
        ratingBarScore = findViewById(R.id.ratingBar_comics_score_dt);

        expTextView = findViewById(R.id.etv_comics_synopsis);
        labelComicsTags = findViewById(R.id.stackLabelView_tag_comics_dt);

        tvNewestSectionName = findViewById(R.id.tv_comics_newest_section_name);
        tvTotalSection = findViewById(R.id.tv_comics_total_section);

        btnRelatedRecoRefresh = findViewById(R.id.tv_asBtn_related_recommendation_refresh);

        tvCopyright = findViewById(R.id.tv_tips_copyright_dt);

        btnDownLoadComics = findViewById(R.id.btn_download_comics);
        btnCollectComics = findViewById(R.id.btn_collect_comics);
        btnBeginReading = findViewById(R.id.btn_begin_reading_dt);

        // 初始化 ComicManager
        mComicManager = ComicManager.getInstance((AppGetter) getAppInstance());

        // 设置点击监听
        setClickListener();
    }

    @Override
    protected void initData() {
        source = getIntent().getIntExtra(EXTRA_COMIC_SOURCE, -1);
        cid = getIntent().getStringExtra(EXTRA_COMIC_CID);

        if (source == -1 || TextUtils.isEmpty(cid)) {
            // 如果没有传入 source 和 cid，则搜索"海贼王"并选择第一个结果
            searchAndLoadOnePiece();
        } else {
            loadComicsDetail();
        }
    }

    @Override
    protected void initUser() {
        super.initUser();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_comics_detail;
    }

    /**
     * 设置点击监听
     */
    private void setClickListener() {
        btnShowMenu.setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_newest_section_dt).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_comics_catalogue_dt).setOnClickListener(this);
        btnRelatedRecoRefresh.setOnClickListener(this);
        btnDownLoadComics.setOnClickListener(this);
        btnCollectComics.setOnClickListener(this);
        btnBeginReading.setOnClickListener(this);

        scvComicsDetail.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > DensityUtils.dp2px(this, 36)) {
                if (comic != null) {
                    tvTopTitle.setText(comic.getTitle());
                }
            } else {
                tvTopTitle.setText("");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_asBtn_show_menu:
                // 显示更多菜单（分享、举报等）
                showMoreMenu();
                break;
            case R.id.rtl_asBtn_newest_section_dt:
                // 打开最新章节
                openLatestChapter();
                break;
            case R.id.rtl_asBtn_comics_catalogue_dt:
                // 打开目录
                openCatalogue();
                break;
            case R.id.tv_asBtn_related_recommendation_refresh:
                // 刷新相关推荐
                refreshRelatedRecommendation();
                break;
            case R.id.btn_download_comics:
                // 下载漫画
                downloadComics();
                break;
            case R.id.btn_collect_comics:
                // 收藏漫画
                if (UserInfoCache.getIsTourist(this)) {
                    goRegister("亲爱的用户，为了有更好的阅读体验，请登录以后使用收藏功能！");
                } else {
                    toggleCollectComics();
                }
                break;
            case R.id.btn_begin_reading_dt:
                // 开始阅读
                beginReading();
                break;
            default:
                break;
        }
    }

    /**
     * 加载漫画详情
     */
    private void loadComicsDetail() {
        showProgressDialog();

        // 从本地数据库加载漫画信息
        comic = mComicManager.load(source, cid);

        if (comic == null) {
            hideProgressDialog();
            TipDialog.show(this, "漫画不存在", TipDialog.TYPE.ERROR)
                    .setOnDismissListener(this::finish);
            return;
        }

        hideProgressDialog();

        // 更新UI
        updateUI();
    }

    /**
     * 搜索"海贼王"并加载第一个结果
     */
    private void searchAndLoadOnePiece() {
        showProgressDialog();

        new Thread(() -> {
            try {
                // 使用腾讯动漫搜索"海贼王"
                TencentDemo tencentDemo = new TencentDemo(this);
                List<TencentDemo.ComicInfo> searchResults = tencentDemo.searchComic("海贼王", 1);

                if (searchResults == null || searchResults.isEmpty()) {
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        TipDialog.show(this, "搜索失败没有找到结果", TipDialog.TYPE.ERROR)
                                .setOnDismissListener(this::finish);
                    });
                    return;
                }

                // 选择第一个结果
                TencentDemo.ComicInfo selectedComic = searchResults.get(0);

                // 获取详情
                TencentDemo.ComicDetail detail = tencentDemo.getComicDetail(selectedComic.cid);

                if (detail == null) {
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        TipDialog.show(this, "获取漫画详情失败", TipDialog.TYPE.ERROR)
                                .setOnDismissListener(this::finish);
                    });
                    return;
                }

                // 将详情转换为 Comic 对象
                comic = convertToComic(selectedComic, detail);

                runOnUiThread(() -> {
                    hideProgressDialog();
                    updateUI();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    TipDialog.show(this, "搜索失败: " + e.getMessage(), TipDialog.TYPE.ERROR)
                            .setOnDismissListener(this::finish);
                });
            }
        }).start();
    }

    /**
     * 将腾讯动漫的搜索结果和详情转换为 Comic 对象
     * 
     * @param comicInfo 搜索结果
     * @param detail 详情
     * @return Comic 对象
     */
    private Comic convertToComic(TencentDemo.ComicInfo comicInfo, TencentDemo.ComicDetail detail) {
        int source = 51; // 腾讯动漫的 source 类型
        String cid = comicInfo.cid;
        String title = detail.title != null ? detail.title : comicInfo.title;
        String cover = detail.cover != null ? detail.cover : comicInfo.cover;
        String update = detail.update != null ? detail.update : comicInfo.update;
        String author = detail.author != null ? detail.author : comicInfo.author;
        String intro = detail.intro;
        boolean finish = detail.isFinish;

        Comic comic = new Comic(source, cid, title, cover, update, author);
        comic.setIntro(intro);
        comic.setFinish(finish);

        return comic;
    }

    /**
     * 更新UI界面
     */
    private void updateUI() {
        if (comic == null) {
            return;
        }

        // 加载封面
        GlideUtil.loadRoundRect(this, ivComicsCover, comic.getCover());

        // 设置标题
        tvComicsName.setText(comic.getTitle());

        // 设置作者
        if (!TextUtils.isEmpty(comic.getAuthor())) {
            tvComicsAuthor.setText(comic.getAuthor());
        } else {
            tvComicsAuthor.setVisibility(View.GONE);
        }

        // 设置状态和更新时间
        StringBuilder tipsBuilder = new StringBuilder();
        if (comic.getFinish() != null && comic.getFinish()) {
            tipsBuilder.append("已完结");
        } else {
            tipsBuilder.append("连载中");
        }

        if (!TextUtils.isEmpty(comic.getUpdate())) {
            tipsBuilder.append(" · 更新于").append(comic.getUpdate());
        }

        if (tipsBuilder.length() > 0) {
            tvComicsTips.setText(tipsBuilder.toString());
        } else {
            tvComicsTips.setVisibility(View.GONE);
        }

        // 设置简介
        if (!TextUtils.isEmpty(comic.getIntro())) {
            expTextView.setText(comic.getIntro());
        } else {
            expTextView.setVisibility(View.GONE);
        }

        // 设置最新章节
        if (!TextUtils.isEmpty(comic.getLast())) {
            tvNewestSectionName.setText(comic.getLast());
        }

        // 设置版权说明
        if (!TextUtils.isEmpty(comic.getTitle())) {
            String copyRightStr = "版权说明：本漫画版权归作者所有";
            SpannableString spannableString = new SpannableString(copyRightStr);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.col_red));
            StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);
            AbsoluteSizeSpan aSize = new AbsoluteSizeSpan(DensityUtils.sp2px(this, 15));
            spannableString.setSpan(colorSpan, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(styleSpan_B, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvCopyright.setText(spannableString);
        } else {
            tvCopyright.setVisibility(View.GONE);
        }

        // 检查是否已收藏
        checkCollectStatus();
    }

    /**
     * 检查收藏状态
     */
    private void checkCollectStatus() {
        if (comic != null && comic.getFavorite() != null) {
            isCollected = true;
            btnCollectComics.setText("已收藏");
            btnCollectComics.setTextColor(ContextCompat.getColor(this, R.color.txt_gray));
        } else {
            isCollected = false;
            btnCollectComics.setText("收藏");
            btnCollectComics.setTextColor(ContextCompat.getColor(this, R.color.txt_black_191919));
        }
    }

    /**
     * 显示更多菜单
     */
    private void showMoreMenu() {
        // TODO: 实现更多菜单（分享、举报等）
        Toast.makeText(this, "更多菜单功能待实现", Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开最新章节
     */
    private void openLatestChapter() {
        if (comic == null) {
            Toast.makeText(this, "获取章节失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: 打开最新章节
        Toast.makeText(this, "打开最新章节功能待实现", Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开目录
     */
    private void openCatalogue() {
        if (comic == null) {
            Toast.makeText(this, "获取章节失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: 打开章节目录对话框
        Toast.makeText(this, "打开目录功能待实现", Toast.LENGTH_SHORT).show();
    }

    /**
     * 刷新相关推荐
     */
    private void refreshRelatedRecommendation() {
        // TODO: 刷新相关推荐
        Toast.makeText(this, "刷新相关推荐功能待实现", Toast.LENGTH_SHORT).show();
    }

    /**
     * 下载漫画
     */
    private void downloadComics() {
        if (comic == null) {
            return;
        }

        if (UserInfoCache.getIsTourist(this)) {
            goRegister("亲爱的用户，为了有更好的阅读体验，请登录以后使用下载功能！");
            return;
        }

        // TODO: 实现下载功能
        Toast.makeText(this, "下载功能待实现", Toast.LENGTH_SHORT).show();
    }

    /**
     * 切换收藏状态
     */
    private void toggleCollectComics() {
        if (comic == null) {
            return;
        }

        if (isCollected) {
            // 取消收藏
            comic.setFavorite(null);
            mComicManager.updateOrDelete(comic);
            isCollected = false;
            btnCollectComics.setText("收藏");
            btnCollectComics.setTextColor(ContextCompat.getColor(this, R.color.txt_black_191919));
            TipDialog.show(this, "已取消收藏", TipDialog.TYPE.SUCCESS);
        } else {
            // 添加收藏
            comic.setFavorite(System.currentTimeMillis());
            mComicManager.updateOrInsert(comic);
            isCollected = true;
            btnCollectComics.setText("已收藏");
            btnCollectComics.setTextColor(ContextCompat.getColor(this, R.color.txt_gray));
            TipDialog.show(this, "收藏成功", TipDialog.TYPE.SUCCESS);
        }
    }

    /**
     * 开始阅读
     */
    private void beginReading() {
        if (comic == null) {
            TipDialog.show(this, "获取章节失败", TipDialog.TYPE.ERROR);
            return;
        }

        showProgressDialog();

        // 在后台线程获取章节列表
        new Thread(() -> {
            try {
                // 获取漫画的 cid
                String cid = comic.getCid();

                // 获取章节列表
                TencentDemo tencentDemo = new TencentDemo(this);
                List<TencentDemo.ChapterInfo> chapterInfos = tencentDemo.getChapterList(cid);

                if (chapterInfos == null || chapterInfos.isEmpty()) {
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        TipDialog.show(this, "获取章节列表失败", TipDialog.TYPE.ERROR);
                    });
                    return;
                }

                // 转换为 Chapter 对象列表
                List<Chapter> chapters = convertToChapters(chapterInfos, comic);

                // 启动阅读页面
                runOnUiThread(() -> {
                    hideProgressDialog();
                    startReaderActivity(chapters);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    TipDialog.show(this, "获取章节失败: " + e.getMessage(), TipDialog.TYPE.ERROR);
                });
            }
        }).start();
    }

    /**
     * 将腾讯动漫的章节列表转换为 Chapter 对象列表
     * 
     * @param chapterInfos 腾讯动漫章节列表
     * @param comic 漫画对象
     * @return Chapter 对象列表
     */
    private List<Chapter> convertToChapters(List<TencentDemo.ChapterInfo> chapterInfos, Comic comic) {
        List<Chapter> chapters = new java.util.ArrayList<>();

        long sourceComic = System.currentTimeMillis(); // 使用时间戳作为 sourceComic
        int index = 0;

        for (TencentDemo.ChapterInfo chapterInfo : chapterInfos) {
            // 创建 Chapter 对象
            long id = sourceComic + index++;
            Chapter chapter = new Chapter(id, String.valueOf(sourceComic), chapterInfo.title, chapterInfo.path);
            chapters.add(chapter);
        }

        return chapters;
    }

    /**
     * 启动阅读页面
     * 
     * @param chapters 章节列表
     */
    private void startReaderActivity(List<Chapter> chapters) {
        if (chapters == null || chapters.isEmpty()) {
            TipDialog.show(this, "章节列表为空", TipDialog.TYPE.ERROR);
            return;
        }

        // 获取阅读模式
        int mode = mPreference.getInt(PreferenceManager.PREF_READER_MODE, PreferenceManager.READER_MODE_PAGE);

        // 获取第一个章节的 ID
        long chapterId = chapters.get(0).getId();

        // 创建 Intent
        Intent intent = ReaderActivity.createIntent(this, chapterId, chapters, mode);
        startActivityForResult(intent, REQUEST_READ);
    }

    /**
     * 跳转登录
     */
    private void goRegister(String content) {
        MessageDialog.show(this, "提示", content, "去登录", "返回")
                .setCancelable(true)
                .setOnCancelButtonClickListener((baseDialog, v) -> {
                    baseDialog.doDismiss();
                    return false;
                })
                .setOnOkButtonClickListener((baseDialog, v) -> {
                    // TODO: 跳转到登录页面
                    // LoginActivity2.start(this);
                    Toast.makeText(this, "跳转登录功能待实现", Toast.LENGTH_SHORT).show();
                    baseDialog.doDismiss();
                    return false;
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_READ && resultCode == RESULT_OK && data != null) {
            // 从阅读页面返回，更新收藏状态
            checkCollectStatus();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
