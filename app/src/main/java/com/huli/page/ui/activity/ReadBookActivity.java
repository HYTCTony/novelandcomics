package com.huli.page.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.appbar.AppBarLayout;
import com.huli.foxread.R;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.BookChapter;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.model.local.BookRepository;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.presenter.ReadBookPresenter;
import com.huli.page.presenter.contract.ReadBookContract;
import com.huli.page.ui.adapter.CatalogAdapter;
import com.huli.page.ui.base.BaseMvpViewActivity;
import com.huli.page.ui.dialog.ReadSettingDialog;
import com.huli.page.utils.BrightnessUtils;
import com.huli.page.utils.Constant;
import com.huli.page.utils.RxUtils;
import com.huli.page.utils.ScreenUtils;
import com.huli.page.utils.StringUtils;
import com.huli.page.utils.SystemBarUtils;
import com.huli.page.widget.page.TxtChapter;
import com.huli.page.widget.read.PageWidget;
import com.huli.page.widget.read.ReadLoader;
import com.lzy.okgo.OkGo;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReadBookActivity extends BaseMvpViewActivity<ReadBookContract.Presenter> implements ReadBookContract.View {
    private static final String TAG = "ReadBookActivity";
    public static final int REQUEST_MORE_SETTING = 1;
    // 注册 Brightness 的 uri
    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");
    /***************content_view******************/
    @BindView(R.id.read_pv_page)
    PageWidget mPvPage;
    /*************top_menu_view*******************/
    @BindView(R.id.read_abl_top_menu)
    AppBarLayout appBarLayout;
    /***************bottom_menu_view***************************/
    @BindView(R.id.read_ll_bottom_menu)
    LinearLayout llBottomMenu;
    @BindView(R.id.read_sb_chapter_progress)
    SeekBar mProgress;
    @BindView(R.id.read_tv_night_mode)
    TextView tvNightMode;
    //页码
    @BindView(R.id.read_tv_page_tip)
    TextView tvPageTip;
    /***************left slide*******************************/
    @BindView(R.id.read_dl_slide)
    DrawerLayout mDlSlide;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindColor(R.color.light_translucent)
    int grey;
    /*****************view******************/
    private ReadSettingDialog mSettingDialog;
    private ReadLoader mPageLoader;
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;
    /****************数据及绑定**********************/
    List<TxtChapter> mChapters = new ArrayList<>();
    private CatalogAdapter catalogAdapter;
    BookShelfListBean data;
    /**********************控制屏幕常亮**************/
    private PowerManager.WakeLock mWakeLock;
    /*******************数据传递以及状态设置*************************/
    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";
    public static final String EXTRA_PAGE_POS = "extra_page_pos";
    private boolean isCollected = false; // isFromSDCard
    private boolean isNightMode = false;
    private boolean isFullScreen = false;
    private boolean isRegistered = false;
    private int chapter = 0; // 如果是0，则跳转到上一次阅读的页码
    private String mBookId;
    private static final int WHAT_CATEGORY = 1;
    private static final int WHAT_CHAPTER = 2;
    private static final int MSG_POLLING = 3;
    private static final int POLLING_INTERVAL = 5 * 60 * 1000;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_CATEGORY:
                    rv.smoothScrollToPosition(mPageLoader.getChapterPos());
                    break;
                case WHAT_CHAPTER:
                    mPageLoader.openChapter();
                    break;
                case MSG_POLLING:
                    doPolling();
                    break;
            }
        }
    };

    public static void start(Context context, BookShelfListBean bean, boolean isCollected, int page) {
        context.startActivity(new Intent(context, ReadBookActivity.class)
                .putExtra(EXTRA_IS_COLLECTED, isCollected)
                .putExtra(EXTRA_PAGE_POS, page)
                .putExtra(EXTRA_COLL_BOOK, bean));
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_read_book;
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void initView() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.black), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, false);
        data = (BookShelfListBean) getIntent().getSerializableExtra(EXTRA_COLL_BOOK);
        isCollected = getIntent().getBooleanExtra(EXTRA_IS_COLLECTED, false);
        chapter = getIntent().getIntExtra(EXTRA_PAGE_POS, 0);
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        isFullScreen = ReadSettingManager.getInstance().isFullScreen();
        mBookId = data.getNovel_id();
        //获取页面加载器
        mPageLoader = mPvPage.getPageLoader(data);
        //禁止滑动展示DrawerLayout
        mDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //侧边打开后，返回键能够起作用
        mDlSlide.setFocusableInTouchMode(false);
        mSettingDialog = new ReadSettingDialog(this, mPageLoader);

        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(grey)
                .sizeResId(R.dimen.dp_0_5)
                .build());
        catalogAdapter = new CatalogAdapter(mChapters);
        rv.setAdapter(catalogAdapter);
        //夜间模式按钮的状态
        toggleNightMode();
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);
        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setDefaultBrightness(this);
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().getBrightness());
        }
        //初始化屏幕常亮类
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "keep bright");
        //隐藏StatusBar
        mPvPage.post(
                () -> {
                    //隐藏
                    SystemBarUtils.hideStableStatusBar(this);
                    if (isFullScreen) {
                        SystemBarUtils.hideStableNavBar(this);
                    }
                }
        );
        //初始化TopMenu
        appBarLayout.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
        //初始化BottomMenu
        initBottomMenu();
        //获取目录
        loadCategory();
        if (chapter != 0) {
            mPageLoader.skipToChapter(chapter);
        }
        mPageLoader.setOnPageChangeListener(new ReadLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos) {
                for (TxtChapter data : mChapters) {
                    data.setSelect(false);
                }
                mChapters.get(pos).setSelect(true);
                catalogAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestChapters(List<TxtChapter> requestChapters) {
                presenter.loadChapter(ReadBookActivity.this, mBookId, requestChapters);
                mHandler.sendEmptyMessage(WHAT_CATEGORY);
                //隐藏提示
                tvPageTip.setVisibility(GONE);
            }

            @Override
            public void onCategoryFinish(List<TxtChapter> chapters) {
                for (TxtChapter chapter : chapters) {
                    chapter.setTitle(StringUtils.convertCC(chapter.getTitle(), mPvPage.getContext()));
                }
                mChapters = chapters;
                catalogAdapter.setNewData(mChapters);
            }

            @Override
            public void onPageCountChange(int count) {
                mProgress.setMax(Math.max(0, count - 1));
                mProgress.setProgress(0);
                // 如果处于错误状态，那么就冻结使用
                if (mPageLoader.getPageStatus() == ReadLoader.STATUS_LOADING
                        || mPageLoader.getPageStatus() == ReadLoader.STATUS_ERROR) {
                    mProgress.setEnabled(false);
                } else {
                    mProgress.setEnabled(true);
                }
            }

            @Override
            public void onPageChange(int pos) {
                mProgress.post(
                        () -> mProgress.setProgress(pos)
                );
            }
        });
        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (llBottomMenu.getVisibility() == VISIBLE) {
                    //显示标题
                    tvPageTip.setText((progress + 1) + "/" + (mProgress.getMax() + 1));
                    tvPageTip.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //进行切换
                int pagePos = mProgress.getProgress();
                if (pagePos != mPageLoader.getPagePos()) {
                    mPageLoader.skipToPage(pagePos);
                }
                //rvPageTip
                tvPageTip.setVisibility(GONE);
            }
        });
        mPvPage.setTouchListener(new PageWidget.TouchListener() {
            @Override
            public boolean onTouch() {
                return !hideReadMenu();
            }

            @Override
            public void center() {
                toggleMenu(true);
            }

            @Override
            public void prePage() {

            }

            @Override
            public void nextPage() {

            }

            @Override
            public void cancel() {

            }
        });
        catalogAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mDlSlide.closeDrawer(GravityCompat.START);
                mPageLoader.skipToChapter(position);
            }
        });
        mSettingDialog.setOnDismissListener(
                dialog -> hideSystemBar()
        );
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        setTitle(data.getNovel_name());
        super.initToolbar(toolbar);
    }

    private void loadCategory() {
        // 如果是已经收藏的，那么就从数据库中获取目录
        if (isCollected) {
            Disposable disposable = BookRepository.getInstance()
                    .getBookChaptersFormRx(mBookId)
                    .compose(RxUtils::toSimpleSingle)
                    .subscribe((bookChapterBeen, throwable) -> {
                                // 设置 CollBook
                                mPageLoader.getCollBook().setBookChapters(bookChapterBeen);
                                // 刷新章节列表
                                mPageLoader.refreshChapterList();
                                // 如果是网络小说并被标记更新的，则从网络下载目录
                                if (data.getIsUpdate() && !data.getIsLocal()) {
                                    presenter.loadCategory(ReadBookActivity.this, mBookId);
                                }
                                if (throwable != null)
                                    Log.e(TAG, throwable.getMessage());
                            }
                    );
            CompositeDisposable mDisposable = new CompositeDisposable();
            mDisposable.add(disposable);
        } else {
            // 从网络中获取目录
            presenter.loadCategory(ReadBookActivity.this, mBookId);
        }
    }

    @Override
    public void reqAddBookrack(String data) {
        exit();
        showToast("加入成功！");
    }

    @Override
    public void showCategory(List<BookChapter> bookChapters) {
        mPageLoader.getCollBook().setBookChapters(bookChapters);
        mPageLoader.refreshChapterList();

        // 如果是目录更新的情况，那么就需要存储更新数据
        if (data.getIsUpdate() && isCollected) {
            BookRepository.getInstance().saveBookChaptersToAsync(bookChapters);
        }
    }

    @Override
    public void finishChapter() {
        if (mPageLoader.getPageStatus() == ReadLoader.STATUS_LOADING) {
            mHandler.sendEmptyMessage(WHAT_CHAPTER);
        }
        // 当完成章节的时候，刷新列表
        catalogAdapter.notifyDataSetChanged();
    }

    @Override
    public void errorChapter() {
        if (mPageLoader.getPageStatus() == ReadLoader.STATUS_LOADING) {
            mPageLoader.chapterError();
        }
    }

    @OnClick({R.id.read_tv_pre_chapter, R.id.read_tv_next_chapter, R.id.read_tv_category, R.id.read_tv_night_mode, R.id.read_tv_setting})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.read_tv_pre_chapter:
                if (mPageLoader.skipPreChapter()) {
                    for (TxtChapter data : mChapters) {
                        data.setSelect(false);
                    }
                    mChapters.get(mPageLoader.getChapterPos()).setSelect(true);
                    catalogAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.read_tv_next_chapter:
                if (mPageLoader.skipNextChapter()) {
                    for (TxtChapter data : mChapters) {
                        data.setSelect(false);
                    }
                    mChapters.get(mPageLoader.getChapterPos()).setSelect(true);
                    catalogAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.read_tv_category:
                //移动到指定位置
                if (mChapters.size() > 0) {
                    for (TxtChapter data : mChapters) {
                        data.setSelect(false);
                    }
                    mChapters.get(mPageLoader.getChapterPos()).setSelect(true);
                    catalogAdapter.notifyDataSetChanged();
                }
                //切换菜单
                toggleMenu(true);
                //打开侧滑动栏
                mDlSlide.openDrawer(GravityCompat.START);
                break;
            case R.id.read_tv_night_mode:
                if (isNightMode) {
                    isNightMode = false;
                } else {
                    isNightMode = true;
                }
                mPageLoader.setNightMode(isNightMode);
                toggleNightMode();
                break;
            case R.id.read_tv_setting:
                toggleMenu(false);
                mSettingDialog.show();
                break;
        }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();

        if (appBarLayout.getVisibility() == View.VISIBLE) {
            //关闭
            appBarLayout.startAnimation(mTopOutAnim);
            llBottomMenu.startAnimation(mBottomOutAnim);
            appBarLayout.setVisibility(GONE);
            llBottomMenu.setVisibility(GONE);
            tvPageTip.setVisibility(GONE);

            if (hideStatusBar) {
                hideSystemBar();
            }
        } else {
            appBarLayout.setVisibility(View.VISIBLE);
            llBottomMenu.setVisibility(View.VISIBLE);
            appBarLayout.startAnimation(mTopInAnim);
            llBottomMenu.startAnimation(mBottomInAnim);

            showSystemBar();
        }
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private boolean hideReadMenu() {
        hideSystemBar();
        if (appBarLayout.getVisibility() == VISIBLE) {
            toggleMenu(true);
            return true;
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }

    private void showSystemBar() {
        //显示
        SystemBarUtils.showUnStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this);
        }
    }

    private void hideSystemBar() {
        //隐藏
        SystemBarUtils.hideStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this);
        }
    }

    private void initBottomMenu() {
        //判断是否全屏
        if (ReadSettingManager.getInstance().isFullScreen()) {
            //还需要设置mBottomMenu的底部高度
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) llBottomMenu.getLayoutParams();
            params.bottomMargin = ScreenUtils.getNavigationBarHeight();
            llBottomMenu.setLayoutParams(params);
        } else {
            //设置mBottomMenu的底部距离
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) llBottomMenu.getLayoutParams();
            params.bottomMargin = 0;
            llBottomMenu.setLayoutParams(params);
        }
    }

    //初始化菜单动画
    private void initMenuAnim() {
        if (mTopInAnim != null) return;

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        //退出的速度要快
        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }

    private void toggleNightMode() {
        if (isNightMode) {
            tvNightMode.setText(StringUtils.getString(R.string.nb_mode_morning));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_morning);
            tvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        } else {
            tvNightMode.setText(StringUtils.getString(R.string.nb_mode_night));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_night);
            tvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    final ContentResolver cr = getContentResolver();
                    cr.unregisterContentObserver(mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable throwable) {
            if (throwable != null)
                Log.e(TAG, "register mBrightObserver error! " + throwable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
        doPolling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
        if (isCollected) {
            mPageLoader.saveRecord();
        }
        doPolling();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    getContentResolver().unregisterContentObserver(mBrightObserver);
                    isRegistered = false;
                }
            }
        } catch (Throwable throwable) {
            if (throwable != null)
                Log.e(TAG, "unregister BrightnessObserver error! " + throwable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mHandler.removeMessages(MSG_POLLING);
        mHandler.removeMessages(WHAT_CATEGORY);
        mHandler.removeMessages(WHAT_CHAPTER);

        mPageLoader.closeBook();
        mPageLoader = null;

        OkGo.getInstance().cancelAll();
    }

    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mPageLoader.updateBattery(level);
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mPageLoader.updateTime();
            }
        }
    };
    // 亮度调节监听
    // 由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
    private ContentObserver mBrightObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);

            // 判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog.isBrightFollowSystem()) return;

            // 如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_MODE_URI.equals(uri)) {
                Log.d(TAG, "亮度模式改变");
            } else if (BRIGHTNESS_URI.equals(uri) && !BrightnessUtils.isAutoBrightness(mContext)) {
                Log.d(TAG, "亮度模式为手动模式 值改变");
                BrightnessUtils.setBrightness(mContext, BrightnessUtils.getScreenBrightness(mContext));
            } else if (BRIGHTNESS_ADJ_URI.equals(uri) && BrightnessUtils.isAutoBrightness(mContext)) {
                Log.d(TAG, "亮度模式为自动模式 值改变");
                BrightnessUtils.setDefaultBrightness(mContext);
            } else {
                Log.d(TAG, "亮度调整 其他");
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (appBarLayout.getVisibility() == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen()) {
                toggleMenu(true);
                return;
            }
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return;
        } else if (mDlSlide.isDrawerOpen(GravityCompat.START)) {
            mDlSlide.closeDrawer(GravityCompat.START);
            return;
        }

        if (!data.getIsLocal() && !isCollected
                && !data.getBookChapters().isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("加入书架")
                    .setMessage("喜欢本书就加入书架吧")
                    .setPositiveButton("确定", (dialog, which) -> {
                        //设置为已收藏
                        isCollected = true;
                        //设置阅读时间
                        data.setLastRead(StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
                        BookRepository.getInstance().saveBooksListWithAsync(data);
                        presenter.reqAddBookrack(ReadBookActivity.this, mBookId);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        exit();
                    }).create();
            alertDialog.show();
        } else {
            exit();
        }
    }

    private void doPolling() {
        presenter.recordRead(ReadBookActivity.this);//asyn network
        mHandler.sendEmptyMessageDelayed(MSG_POLLING, POLLING_INTERVAL);
    }

    // 退出
    private void exit() {
        // 返回给BookDetail。
        Intent result = new Intent();
        result.putExtra(BookDetailsActivity.RESULT_IS_COLLECTED, isCollected);
        setResult(Activity.RESULT_OK, result);
        // 退出
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isVolumeTurnPage = ReadSettingManager
                .getInstance().isVolumeTurnPage();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (isVolumeTurnPage) {
                    return mPageLoader.skipToPrePage();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (isVolumeTurnPage) {
                    return mPageLoader.skipToNextPage();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemBarUtils.hideStableStatusBar(this);
        if (requestCode == REQUEST_MORE_SETTING) {
            boolean fullScreen = ReadSettingManager.getInstance().isFullScreen();
            if (isFullScreen != fullScreen) {
                isFullScreen = fullScreen;
                // 刷新BottomMenu
                initBottomMenu();
            }

            // 设置显示状态
            if (isFullScreen) {
                SystemBarUtils.hideStableNavBar(this);
            } else {
                SystemBarUtils.showStableNavBar(this);
            }
        }
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onFailure(int code, String err) {
        showToast(err);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected ReadBookContract.Presenter initPresenter() {
        return new ReadBookPresenter();
    }

}