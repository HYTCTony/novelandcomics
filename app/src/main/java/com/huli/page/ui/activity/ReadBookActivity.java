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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.MemoryFile;
import android.os.Message;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobad.feeds.NativeResponse;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.appbar.AppBarLayout;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.config.AdConfig;
import com.huli.foxread.config.TogetherAdConst;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.notchtools.NotchTools;
import com.huli.foxread.rxhttp.ErrorInfo;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.rxhttp.Tip;
import com.huli.foxread.ui.activities.AdvFreeSuccessActivity;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.Advert;
import com.huli.page.model.bean.BookChapter;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.model.bean.Font;
import com.huli.page.model.bean.Timing;
import com.huli.page.model.bean.Voicer;
import com.huli.page.model.event.AdMessage;
import com.huli.page.model.local.BookRepository;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.presenter.ReadBookPresenter;
import com.huli.page.presenter.contract.ReadBookContract;
import com.huli.page.ui.adapter.CatalogAdapter;
import com.huli.page.ui.adapter.CloudVoicersAdapter;
import com.huli.page.ui.adapter.TimingAdapter;
import com.huli.page.ui.base.BaseMvpViewActivity;
import com.huli.page.ui.dialog.BrightnessDialog;
import com.huli.page.ui.dialog.ReadSettingDialog;
import com.huli.page.utils.BrightnessUtils;
import com.huli.page.utils.Constant;
import com.huli.page.utils.FileUtils;
import com.huli.page.utils.MD5Utils;
import com.huli.page.utils.ScreenUtils;
import com.huli.page.utils.SpanUtils;
import com.huli.page.utils.StringUtils;
import com.huli.page.utils.SystemBarUtils;
import com.huli.page.widget.page.PageStyle;
import com.huli.page.widget.page.TxtChapter;
import com.huli.page.widget.page.TxtPage;
import com.huli.page.widget.read.PageView;
import com.huli.page.widget.read.ReadLoader;
import com.hytc.ads.AdLogoView;
import com.hytc.ads.helper.banner.TogetherAdFakeBanner;
import com.hytc.ads.helper.mid.TogetherAdMidMix;
import com.hytc.ads.helper.stimulatevideo.TogetherAdStimulate;
import com.hytc.ads.other.AdNameType;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.msc.util.FileUtil;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.lzy.okgo.OkGo;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReadBookActivity extends BaseMvpViewActivity<ReadBookContract.Presenter> implements ReadBookContract.View {
    private static final String TAG = "ReadBookActivity";
    public static final int REQUEST_MORE_SETTING = 1;
    public static final int READ_TYPE_START = 1;
    public static final int READ_TYPE_CONTINUE = 2;
    public static final int READ_TYPE_STOP = 3;
    // 注册 Brightness 的 uri
    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");
    /***************content_view******************/
    @BindView(R.id.read_pv_page)
    PageView mPvPage;
    /*************top_menu_view*******************/
    @BindView(R.id.read_abl_top_menu)
    AppBarLayout appBarLayout;
    /***************bottom_menu_view***************************/
    @BindView(R.id.listen_ll_bottom_menu)
    LinearLayout llSpeakMenu;
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
    @BindView(R.id.ll_drawer_layout)
    LinearLayout llDrawerLayout;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.line_view)
    View lineView;
    @BindView(R.id.tv_book_statu)
    TextView tvBookStatu;
    @BindColor(R.color.light_translucent)
    int grey;
    /*****************view******************/
    private ReadSettingDialog mSettingDialog;
    private BrightnessDialog mBrightnessDialog;
    private ReadLoader mPageLoader;
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;
    /****************数据及绑定**********************/
    List<TxtChapter> mChapters = new ArrayList<>();
    List<BookChapter> bookChapters = new ArrayList<>();
    private CatalogAdapter catalogAdapter;
    BookShelfListBean data;
    /*******************数据传递以及状态设置*************************/
    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";
    public static final String EXTRA_PAGE_POS = "extra_page_pos";
    private boolean isCollected = false; // isFromSDCard
    private boolean isNightMode = false;
    private boolean isFullScreen = false;
    private boolean isRegistered = false;
    private int chapter = -1; // 如果是-1，则使用本地阅读记录
    private String mBookId;
    private static final int TIMER = 0;
    private static final int WHAT_CATEGORY = 1;
    private static final int WHAT_CHAPTER = 2;
    private static final int MSG_POLLING = 3;
    private static final int MSG_BOTTOM_AD = 4;
    private static final int MSG_IS_ABC = 5;
    private static final int POLLING_INTERVAL = 10 * 1000;
    private static final int POLLING_REQUE_BOTTOM_AD = 2 * 60 * 1000;
    private static final int POLLING_SET_IS_ABC = 30 * 1000;
    private static final int READ_ONE_PAGE_INTERVAL = 15;
    private String[] adPageConst = {TogetherAdConst.AD_CENTER_YELLOW_PAPER, TogetherAdConst.AD_CENTER_PINK, TogetherAdConst.AD_CENTER_ASHEN,
            TogetherAdConst.AD_CENTER_GREEN, TogetherAdConst.AD_CENTER_POOL_BLUE, TogetherAdConst.AD_CENTER_DARK_BLUE,
            TogetherAdConst.AD_CENTER_NIGHT};
    private String constPageId = adPageConst[0];
    private View mAdView;
    private View coverPageView;
    NativeAdContainer container;
    LinearLayout mLayout;
    ImageView mImage;
    TextView mTitle;
    TextView mIntro;
    TextView mSource;
    AdLogoView ivLogo;
    RelativeLayout rlAd;
    RelativeLayout mExpressContainer;
    TextView btnNextPage;
    TextView tvAdView;
    @BindView(R.id.gdt_ad_container)
    NativeAdContainer adContainer;
    @BindView(R.id.ctl_touch_layout)
    RelativeLayout ctlTouchLayout;
    @BindView(R.id.iv_ad_img)
    ImageView ivAdImg;
    @BindView(R.id.tv_iv_ad_title)
    TextView tvAdTitle;
    @BindView(R.id.tv_ad_intro)
    TextView tvAdIntro;
    @BindView(R.id.tv_ad_source)
    TextView tvAdSource;
    @BindView(R.id.banner_container)
    FrameLayout mBannerContainer;
    @BindView(R.id.iv_bg_bottom_view)
    ImageView ivBackgroud;
    @BindView(R.id.btn_bottom_ad)
    TextView btnBottomAd;
    @BindView(R.id.rl)
    RelativeLayout rl;
    //封面
    ImageView bgFrameImage;
    TextView tvBookName;
    TextView tvAuthorName;
    TextView tvCopyrightDescription;

    /*观看视频验证*/
    private boolean mRewardVerify;
    private boolean isABC = false;
    private boolean isFirstRequest = true;
    private int site = 0;
    private long lapse = 0;
    private long interval = 0;
    private boolean needRefreshPage = false;

    private String id;
    private String title;
    private int sum;//pages
    private int quota = READ_ONE_PAGE_INTERVAL;
    private long second = 0;//read time
    private int curPos = 0;//当前页码
    private int perPos = 0;//上一页页码
    private Timer timer;
    private TimerTask timerTask;

    /*********听书相关***************/
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String mVoicer = "xiaoqi";
    // 默认语速
    private String speed = "25";
    //云端发言人列表
    String[] mCloudVoicersEntries;
    String[] mCloudVoicersValue;
    String[] mTimerEntries = {"无", "15分钟", "30分钟", "60分钟", "90分钟"};
    int[] mTimeValue = {0, 15, 30, 60, 90};
    List<Voicer> voicers = new ArrayList<>();
    List<Timing> timings = new ArrayList<>();
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private boolean isListenBook = false;
    CloudVoicersAdapter mVoicersAdapter;
    @BindView(R.id.rv_voicer)
    RecyclerView rvV;
    TimingAdapter mTimingAdapter;
    @BindView(R.id.rv_timer)
    RecyclerView rvT;

    private Vector<byte[]> vector = new Vector<>();
    MemoryFile memFile;
    public volatile long mTotalSize = 0;
    private int endLine;
    private boolean isSkipToNextPage = false;
    StringBuffer texts;

    private void startMethod() {
        //防止多次点击开启计时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask = null;
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER:
                    if (quota > 0) {
                        quota--;
                        second++;
                    }
                    break;
                case WHAT_CATEGORY:
                    rv.smoothScrollToPosition(mPageLoader.getChapterPos());
                    break;
                case WHAT_CHAPTER:
                    mPageLoader.openChapter();
                    break;
                case MSG_POLLING:
                    if (timer == null)
                        doPolling(READ_TYPE_START);
                    break;
                case MSG_BOTTOM_AD:
                    requestAdBottom();
                    break;
                case MSG_IS_ABC:
                    isABC = testingIsABC(-1);
                    if (isABC) {
                        needRefreshPage = true;
                        rl.setVisibility(GONE);
                    } else {
                        if (needRefreshPage)
                            mPageLoader.refreshPage();
                        needRefreshPage = false;
                        rl.setVisibility(VISIBLE);
                        if (mAdView != null)
                            if (UserInfoCache.getIsTourist(mContext) || UserInfoCache.getIsVip(mContext) || site <= 0) {
                                tvAdView.setVisibility(GONE);
                            } else {
                                tvAdView.setVisibility(VISIBLE);
                            }
                    }
                    mPageLoader.setABC(isABC || isListenBook);
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

    @Override
    protected void initView() {
        /*初始化数据*/
        data = (BookShelfListBean) getIntent().getSerializableExtra(EXTRA_COLL_BOOK);
        isCollected = getIntent().getBooleanExtra(EXTRA_IS_COLLECTED, false);
        chapter = getIntent().getIntExtra(EXTRA_PAGE_POS, -1);
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        isFullScreen = ReadSettingManager.getInstance().isFullScreen();
        EventBus.getDefault().register(this);
        PageStyle mPageStyle = ReadSettingManager.getInstance().getPageStyle();
        mBookId = data.getNovel_id();
        //讯飞语言合成
        SpeechUtility.createUtility(ReadBookActivity.this, "appid=5edc91d4");
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(ReadBookActivity.this, mTtsInitListener);
        // 云端发音人名称列表
        mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);
        //准备发言人数据
        for (int i = 0; i < mCloudVoicersEntries.length; i++) {
            Voicer voicer = new Voicer(mCloudVoicersEntries[i], mCloudVoicersValue[i]);
            if (TextUtils.equals(voicer.value, mVoicer)) {
                voicer.isSelect = true;
            }
            voicers.add(voicer);
        }
        LinearLayoutManager llVoice = new LinearLayoutManager(this);
        llVoice.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvV.setLayoutManager(llVoice);
        mVoicersAdapter = new CloudVoicersAdapter();
        rvV.setAdapter(mVoicersAdapter);
        mVoicersAdapter.setNewInstance(voicers);
        //准备定时关闭数据
        for (int i = 0; i < mTimerEntries.length; i++) {
            Timing timing = new Timing(mTimerEntries[i], mTimeValue[i]);
            if (i == 0) {
                timing.isSelect = true;
            }
            timings.add(timing);
        }
        LinearLayoutManager llTime = new LinearLayoutManager(this);
        llTime.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvT.setLayoutManager(llTime);
        mTimingAdapter = new TimingAdapter();
        rvT.setAdapter(mTimingAdapter);
        mTimingAdapter.setNewInstance(timings);
        //获取目录
        presenter.loadCategory(ReadBookActivity.this, mBookId);
        //请求广告权限
        presenter.reqAdvertAd(ReadBookActivity.this);
        //初始化封面
        initCover();
        //初始化风格
        changeStyle(mPageStyle);
        //加载广告
//        requestAdPage();
        //获取页面加载器
        mPageLoader = mPvPage.getPageLoader(data, ScreenUtils.hasNotchScreen(mContext));
        /*初始化状态栏*/
        SystemBarUtils.blackNavBar(mContext);
        if (!isFullScreen) {
            appBarLayout.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
            llDrawerLayout.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
            SystemBarUtils.cancelFullScreen(mContext);
        }
        StatusBarUtils.setColor(mContext, ContextCompat.getColor(mContext, R.color.black), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(mContext, false);
        hideSystemBar();
        //禁止滑动展示DrawerLayout
        mDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //侧边打开后，返回键能够起作用
        mDlSlide.setFocusableInTouchMode(false);
        mSettingDialog = new ReadSettingDialog(mContext, mPageLoader);
        mBrightnessDialog = new BrightnessDialog(mContext);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
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
            BrightnessUtils.setDefaultBrightness(mContext);
        } else {
            BrightnessUtils.setBrightness(mContext, ReadSettingManager.getInstance().getBrightness());
        }
        if (chapter != -1) {
            mPageLoader.skipToChapter(chapter);
        }
        mPageLoader.setOnPageChangeListener(new ReadLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos) {
                mProgress.post(
                        () -> mProgress.setProgress(pos)
                );
                for (TxtChapter data : mChapters) {
                    data.setSelect(false);
                }
                mChapters.get(pos).setSelect(true);
                catalogAdapter.notifyDataSetChanged();
                id = mChapters.get(pos).getId();
                title = mChapters.get(pos).getTitle();
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
                mProgress.setMax(Math.max(0, chapters.size() - 1));
                // 如果处于错误状态，那么就冻结使用
//                if (mPageLoader.getPageStatus() == ReadLoader.STATUS_LOADING || mPageLoader.getPageStatus() == ReadLoader.STATUS_ERROR) {
//                    mProgress.setEnabled(false);
//                } else {
//                    mProgress.setEnabled(true);
//                }
                for (TxtChapter chapter : chapters) {
                    chapter.setTitle(StringUtils.convertCC(chapter.getTitle(), mPvPage.getContext()));
                    if (chapter.isSelect()) {
                        mProgress.setProgress(chapter.getChapter());
                    }
                }
                mChapters = chapters;
                if (mChapters.size() > 0) {
                    id = mChapters.get(0).getId();
                    title = mChapters.get(0).getTitle();
                    if (timer == null)
                        doPolling(READ_TYPE_START);
                }
                catalogAdapter.setNewInstance(mChapters);
            }

            @Override
            public void onPageCountChange(int count) {

            }

            @Override
            public void onPageChange(int pos) {
                if (!isABC) {
                    if (isFirstRequest && sum > 0) {
                        requestAdBottom();
                        isFirstRequest = false;
                    }
                }
                quota = READ_ONE_PAGE_INTERVAL;
                perPos = curPos;
                curPos = pos;
                if (curPos != perPos)
                    sum++;
            }

            @Override
            public void onStyleChange(PageStyle pageStyle, boolean isNightMode) {
                ReadBookActivity.this.isNightMode = isNightMode;
                changeStyle(pageStyle);
            }
        });
        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (llBottomMenu.getVisibility() == VISIBLE) {
                    //显示标题
                    tvPageTip.setText(mChapters.get(progress).getTitle());
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
                if (pagePos != mPageLoader.getChapterPos()) {
                    mPageLoader.skipToChapter(pagePos);
                }
                //rvPageTip
                tvPageTip.setVisibility(GONE);
            }
        });
        mPvPage.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return !hideReadMenu();
            }

            @Override
            public void center() {
                isNightMode = ReadSettingManager.getInstance().isNightMode();
                //夜间模式按钮的状态
                toggleNightMode();
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
        mBrightnessDialog.setOnDismissListener(
                dialog -> hideSystemBar()
        );
        mPvPage.setReaderAdListener(new PageView.ReaderAdListener() {
            @Override
            public View getAdView() {
                return mAdView;
            }

            @Override
            public void onRequestAd() {
                requestAdPage();
            }

            @Override
            public View getCoverPageView() {
                return coverPageView;
            }
        });
        mVoicersAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                for (Voicer voicer : voicers) {
                    voicer.isSelect = false;
                }
                voicers.get(position).isSelect = true;
                mVoicer = voicers.get(position).value;
                adapter.notifyDataSetChanged();
                setParam();
                if (texts.length() > 0) {
                    int code = mTts.startSpeaking(String.valueOf(texts), mTtsListener);
                    if (code != ErrorCode.SUCCESS) {
                        showToast("语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                    }
                }
            }
        });
        mTimingAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                for (Timing timing : timings) {
                    timing.isSelect = false;
                }
                timings.get(position).isSelect = true;
                int time = timings.get(position).value;
                adapter.notifyDataSetChanged();
            }
        });

        BubbleSeekBar mBubbleSeekBar = findViewById(R.id.seek_bar);
        mBubbleSeekBar.getConfigBuilder()
                .min(0.5f)
                .max(2.0f)
                .progress(1)
                .floatType()
                .sectionCount(6)
                .sectionTextInterval(2)
                .showSectionText()
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .autoAdjustSectionMark()
                .build();
        mBubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                float speedNum = progressFloat * 50;
                if (speedNum > 100) {
                    speedNum = 100;
                }
                speed = String.valueOf(speedNum);
                Log.d(TAG, "speed=" + speed);
                setParam();
                if (texts != null) {
                    int code = mTts.startSpeaking(String.valueOf(texts), mTtsListener);
                    if (code != ErrorCode.SUCCESS) {
                        showToast("语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                    }
                }
            }
        });
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        setTitle(data.getNovel_name());
        tvTitle.setText(data.getNovel_name());
        toolbar.setNavigationOnClickListener(
                (v) -> onBackPressed()
        );
        super.initToolbar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_read_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_download:
//                showToast("正在研发中...");
//                break;
            case R.id.action_listen:
                // 移动数据分析，收集开始合成事件
                /*FlowerCollector.onEvent(TtsDemo.this, "tts_play");*/

//                texts = ((EditText) findViewById(R.id.tts_text)).getText().toString();
                toggleMenu(true);
                isListenBook = true;
                mPageLoader.setABC(isListenBook);
                mPvPage.isCanTurnPage(!isListenBook);
                if (mPageLoader.isCustomView())
                    mPageLoader.skipToNextPage();
                ivBackgroud.setVisibility(VISIBLE);
                mBannerContainer.setVisibility(GONE);
                adContainer.setVisibility(GONE);
                // 设置参数
                setParam();
                String texts = "一起看书免费小说，开始为您朗读";
                /**
                 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
                 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
                 */
                //  String path = Environment.getExternalStorageDirectory() + "/tts.pcm";
                //	int code = mTts.synthesizeToUri(texts, path, mTtsListener);
                Log.d(TAG, texts);
                int code = mTts.startSpeaking(texts, mTtsListener);
                if (code != ErrorCode.SUCCESS) {
                    showToast("语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //支持实时音频返回，仅在synthesizeToUri条件下支持
            mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
            //	mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");

            //设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, mVoicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, speed);
            Log.d(TAG, "修改参数成功！" +   mTts.getParameter(SpeechConstant.SPEED));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, "50");
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, "50");
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }

        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.pcm");
    }


    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showToast("初始化失败,错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
//            showToast("开始播放");
        }

        @Override
        public void onSpeakPaused() {
//            showToast("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
//            showToast("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
//            Log.e("MscSpeechLog_", "percent =" + percent);
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//            Log.e("MscSpeechLog_", "percent =" + percent);
//            Log.e(TAG, "beginPos = " + beginPos + "  endPos = " + endPos + "  endLine = " + endLine);
            if (isSkipToNextPage && endPos >= endLine) {
//                Log.e(TAG, "翻页");
                mPageLoader.skipToNextPage();
                isSkipToNextPage = false;
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                //	showTip("播放完成");
//                DebugLog.LogD("播放完成," + vector.size());
                List<TxtPage> mCurPageList = mPageLoader.getCurPageList();
                texts = new StringBuffer();
                int pos = mPageLoader.getCurPagePos();
                TxtPage curPage = mCurPageList.get(pos);
                int index = curPage.highlight.isEmpty() ? 0 : curPage.highlight.get(curPage.highlight.size() - 1) + 1;
//                Log.d(TAG, "页码：" + pos);
//                Log.d(TAG, "播放位置：" + index);
                curPage.highlight.clear();
                for (int i = index; i < curPage.lines.size(); i++) {
                    String text = curPage.lines.get(i);
                    curPage.highlight.add(i);
                    texts.append(text);
                    if (i >= curPage.lines.size() - 1) {
                        endLine = texts.length() - 1;
                        isSkipToNextPage = true;
                        if (!text.endsWith("\n")) {
                            if (pos + 1 < mCurPageList.size()) {
                                TxtPage nextPage = mCurPageList.get(pos + 1);
                                for (int j = 0; j < nextPage.lines.size(); j++) {
                                    text = nextPage.lines.get(j);
//                                    Log.d(TAG, "第二页连接的段落内容：" + text);
                                    nextPage.highlight.add(j);
                                    texts.append(text);
                                    if (text.endsWith("\n")) {
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        if (text.endsWith("\n"))
                            break;
                    }
                }
                mPageLoader.setHighlight(mCurPageList);
//                Log.d(TAG, String.valueOf(texts));
                int code = mTts.startSpeaking(String.valueOf(texts), mTtsListener);
                if (code != ErrorCode.SUCCESS) {
                    showToast("语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }
                try {
                    for (int i = 0; i < vector.size(); i++) {
                        writeToFile(vector.get(i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileUtil.saveFile(memFile, mTotalSize, Environment.getExternalStorageDirectory() + "/1.pcm");
            } else if (error != null) {
                showToast(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            //	 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                Log.d(TAG, "session id =" + sid);
            }

            //当设置SpeechConstant.TTS_DATA_NOTIFY为1时，抛出buf数据
            if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
                byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
                Log.e("MscSpeechLog_", "bufis =" + buf.length);
                vector.add(buf);
            }
        }
    };

    private void writeToFile(byte[] data) throws IOException {
        if (data == null || data.length == 0)
            return;
        try {
            if (memFile == null) {
                Log.e("MscSpeechLog_", "ffffffffff");
                String mFilepath = Environment.getExternalStorageDirectory() + "/1.pcm";
                memFile = new MemoryFile(mFilepath, 1920000);
                memFile.allowPurging(false);
            }
            memFile.writeBytes(data, 0, (int) mTotalSize, data.length);
            mTotalSize += data.length;
        } finally {
        }
    }

    private void initCover() {
        //封面
        coverPageView = LayoutInflater.from(this).inflate(R.layout.layout_cover_view, null, false);
        ImageView ivBookCover = coverPageView.findViewById(R.id.iv_book_cover);
        bgFrameImage = coverPageView.findViewById(R.id.bg_frame_image);
        tvBookName = coverPageView.findViewById(R.id.tv_book_name);
        tvAuthorName = coverPageView.findViewById(R.id.tv_author_name);
        tvCopyrightDescription = coverPageView.findViewById(R.id.tv_copyright_description);
        GlideUtil.loadRoundRect(mContext, ivBookCover, data.getHttp_image());
        tvBookName.setText(data.getNovel_name());
        tvAuthorName.setText("作者：" + data.getAuthor());
        SpannableStringBuilder builderCopyrightDescription = new SpanUtils(mContext)
//                .appendLine("本书已授权一起看书进行电子制作发行")
                .append("本故事纯属虚构·版权所有·侵权必究").create();
        tvCopyrightDescription.setText(builderCopyrightDescription);
    }

    private void changeStyle(PageStyle mPageStyle) {
        //改变广告位id
        switch (mPageStyle) {
            case BG_0:
                constPageId = adPageConst[0];
                break;
            case BG_1:
                constPageId = adPageConst[1];
                break;
            case BG_2:
                constPageId = adPageConst[2];
                break;
            case BG_3:
                constPageId = adPageConst[3];
                break;
            case BG_4:
                constPageId = adPageConst[4];
                break;
            case BG_5:
                constPageId = adPageConst[5];
                break;
            case NIGHT:
                constPageId = adPageConst[6];
                break;
        }
        //改变封面风格
        bgFrameImage.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getCoverBgImg() : mPageStyle.getCoverBgImg());
        tvBookName.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getFontColor()) : ContextCompat.getColor(mContext,
                mPageStyle.getFontColor()));
        tvAuthorName.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getFontColor()) : ContextCompat.getColor(mContext,
                mPageStyle.getFontColor()));
        tvCopyrightDescription.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) :
                ContextCompat.getColor(mContext, mPageStyle.getPromptColor()));
        //改变底部风格
        rl.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getAdBgColor() : mPageStyle.getAdBgColor());
        ivBackgroud.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getAdPlaceholderImg() : mPageStyle.getAdPlaceholderImg());
        //改变目录风格
        if (mPageStyle.getBgColor() == R.color.hl_read_bg_1) {
            llDrawerLayout.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getBgColor() : R.drawable.theme_leather_bg);
        } else {
            llDrawerLayout.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getBgColor() : mPageStyle.getBgColor());
        }
        tvTitle.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getFontColor()) : ContextCompat.getColor(mContext,
                mPageStyle.getFontColor()));
        lineView.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getTipsColor() : mPageStyle.getTipsColor());
        tvBookStatu.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) : ContextCompat.getColor(mContext,
                mPageStyle.getPromptColor()));
        //改变广告页风格
        if (mAdView != null) {
            btnNextPage.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getTipsColor()) : ContextCompat.getColor(mContext,
                    mPageStyle.getTipsColor()));
            tvAdView.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) : ContextCompat.getColor(mContext,
                    mPageStyle.getPromptColor()));
            container.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getAdBgColor() : mPageStyle.getAdBgColor());
            mTitle.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) : ContextCompat.getColor(mContext,
                    mPageStyle.getFontColor()));
            mIntro.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) : ContextCompat.getColor(mContext,
                    mPageStyle.getTipsColor()));
            mSource.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) : ContextCompat.getColor(mContext,
                    mPageStyle.getTipsColor()));
        }
    }

    @Override
    public void reqAdvertAd(Advert data) {
        site = data.getSite();
        lapse = data.getLapse() * 1000;
        interval = data.getInterval();
        ReadSettingManager.getInstance().setAdvertTime(data.getAdvert_time());
        isABC = testingIsABC(data.getLapse());
        if (isABC) {
            needRefreshPage = true;
            rl.setVisibility(GONE);
        } else {
            if (needRefreshPage)
                mPageLoader.refreshPage();
            needRefreshPage = false;
            rl.setVisibility(VISIBLE);
            if (mAdView != null)
                if (UserInfoCache.getIsTourist(mContext) || UserInfoCache.getIsVip(mContext) || site <= 0 || interval > 0) {
                    tvAdView.setVisibility(GONE);
                } else {
                    tvAdView.setVisibility(VISIBLE);
                }
        }
        mPageLoader.setABC(isABC);
    }

    private boolean testingIsABC(long lapse) {
        if (UserInfoCache.getSuperVip(mContext) == 1) {
            return true;
        }
        if (UserInfoCache.getIsVip(mContext)) {
            return true;
        }
        if (UserInfoCache.getIsNewMan(mContext)) {
            return true;
        }
        long now = System.currentTimeMillis();
        long advertTime = ReadSettingManager.getInstance().getAdvertTime() * 1000;
        mHandler.sendEmptyMessageDelayed(MSG_IS_ABC, POLLING_SET_IS_ABC);
        return lapse != 0 && advertTime > now;
    }

    @Override
    public void reqAddBookrack(String data) {
        exit();
    }

    @Override
    public void showCategory(List<BookChapter> bookChapters) {
        this.bookChapters = bookChapters;
        mPageLoader.getCollBook().setBookChapters(bookChapters);
        mPageLoader.refreshChapterList();
        StringBuffer buffer = new StringBuffer();
        buffer.append(data.getIs_end() == 1 ? "已完结" : "未完结");
        buffer.append("，共");
        buffer.append(bookChapters.size());
        buffer.append("章");
        tvBookStatu.setText(buffer);
        // 如果是目录更新的情况，那么就需要存储更新数据
//        if (data.getIsUpdate() && isCollected)
//            BookRepository.getInstance().saveBookChaptersToAsync(bookChapters);
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

    @OnClick({R.id.read_tv_pre_chapter, R.id.read_tv_next_chapter, R.id.read_tv_category, R.id.read_tv_night_mode, R.id.read_tv_brightness,
            R.id.read_tv_setting, R.id.listen_setting_speak_paused})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.read_tv_pre_chapter:
                if (mPageLoader.skipPreChapter()) {
                    mProgress.post(
                            () -> mProgress.setProgress(mPageLoader.getChapterPos())
                    );
                    for (TxtChapter data : mChapters) {
                        data.setSelect(false);
                    }
                    mChapters.get(mPageLoader.getChapterPos()).setSelect(true);
                    catalogAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.read_tv_next_chapter:
                if (mPageLoader.skipNextChapter()) {
                    mProgress.post(
                            () -> mProgress.setProgress(mPageLoader.getChapterPos())
                    );
                    for (TxtChapter data : mChapters) {
                        data.setSelect(false);
                    }
                    mChapters.get(mPageLoader.getChapterPos()).setSelect(true);
                    catalogAdapter.notifyDataSetChanged();
                } else {
                    showToast("已经是最后一章了！");
                }
                break;
            case R.id.read_tv_category:
                mProgress.post(
                        () -> mProgress.setProgress(mPageLoader.getChapterPos())
                );
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
            case R.id.read_tv_brightness:
                toggleMenu(false);
                mBrightnessDialog.show();
                break;
            case R.id.read_tv_setting:
                toggleMenu(false);
                mSettingDialog.show();
                break;
            case R.id.listen_setting_speak_paused:
                /*
                 * 停止听书
                 * */
                isListenBook = false;
                mPageLoader.setABC(isListenBook);
                mPvPage.isCanTurnPage(!isListenBook);
                ivBackgroud.setVisibility(GONE);
                mTts.pauseSpeaking();
                llSpeakMenu.setVisibility(GONE);
                llSpeakMenu.startAnimation(mBottomOutAnim);
                mPageLoader.refreshPage();
                showToast("退出听书模式");
                break;
        }
    }

    private void requestAdPage() {
        if (isABC || isListenBook)
            return;
        TogetherAdMidMix.showAdMid(this, AdConfig.turnPageAdConfig(this), constPageId, new TogetherAdMidMix.AdListenerMid() {
            @Override
            public void onStartRequest(@NotNull String channel) {
//                Log.e("PageView", "onRenderSuccess.AdBottom()===" + channel);
                if (mAdView == null) {
//                    Log.e("PageView", "new mAdView()");
                    mAdView = LayoutInflater.from(mContext).inflate(R.layout.layout_ad_view, null, false);
                    tvAdView = mAdView.findViewById(R.id.btn_watch_video);
                    SpannableStringBuilder builderVideoMessage = new SpanUtils(mContext).append("看小视频免20分钟广告>").setUnderline().create();
                    tvAdView.setText(builderVideoMessage);
                    if (UserInfoCache.getIsTourist(mContext) || UserInfoCache.getIsVip(mContext) || site <= 0) {
                        tvAdView.setVisibility(GONE);
                    } else {
                        tvAdView.setVisibility(VISIBLE);
                    }
                    tvAdView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadVideoAd();
                        }
                    });
                    btnNextPage = mAdView.findViewById(R.id.btn_next_page);
                    btnNextPage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPageLoader.skipToNextPage();
                        }
                    });
                    mExpressContainer = mAdView.findViewById(R.id.express_container);//非自渲染
                    container = mAdView.findViewById(R.id.gdt_ad_container);//自渲染
                    mLayout = mAdView.findViewById(R.id.ctl_touch_layout);
                    mImage = mAdView.findViewById(R.id.iv_ad_img);
                    mTitle = mAdView.findViewById(R.id.tv_iv_ad_title);
                    mIntro = mAdView.findViewById(R.id.tv_ad_intro);
                    mSource = mAdView.findViewById(R.id.tv_ad_source);
                    ivLogo = mAdView.findViewById(R.id.ad_logo_view);

                    PageStyle mPageStyle = ReadSettingManager.getInstance().getPageStyle();
                    btnNextPage.setTextColor(ContextCompat.getColor(mContext, mPageStyle.getTipsColor()));
                    btnNextPage.setTextColor(ContextCompat.getColor(mContext, mPageStyle.getPromptColor()));
                    container.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getAdBgColor() : mPageStyle.getAdBgColor());
                    container.setBackgroundResource(isNightMode ? PageStyle.NIGHT.getAdBgColor() : mPageStyle.getAdBgColor());
                    mTitle.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) : ContextCompat.getColor(mContext,
                            mPageStyle.getFontColor()));
                    mIntro.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) : ContextCompat.getColor(mContext,
                            mPageStyle.getPromptColor()));
                    mSource.setTextColor(isNightMode ? ContextCompat.getColor(mContext, PageStyle.NIGHT.getPromptColor()) : ContextCompat.getColor(mContext,
                            mPageStyle.getTipsColor()));
                }
                switch (channel) {
                    case "gdt":
                        mExpressContainer.setVisibility(GONE);
                        container.setVisibility(VISIBLE);
                        mPvPage.max = 10;
                        break;
                    case "csj":
                        mExpressContainer.setVisibility(VISIBLE);
                        container.setVisibility(GONE);
                        mPvPage.max = 2;
                        break;
                    case "baidu":
                        mExpressContainer.setVisibility(GONE);
                        container.setVisibility(VISIBLE);
                        break;
                }
            }

            @Override
            public void onAdClick(@NotNull String channel) {

            }

            @Override
            public void onAdFailed(@Nullable String failedMsg) {
//                Log.e("PageView", "onAdFailed.AdBottom()===" + failedMsg);
                mAdView = null;
                mPvPage.unDraw();
                if (mPageLoader != null)
                    mPageLoader.setABCFail(true);
            }

            @Override
            public void onAdPrepared(@NotNull String channel) {

            }

            @Override
            public void onAdLoaded(@NotNull String channel, @NotNull List<?> adList) {
                Object any = adList.get(0);
                if (any instanceof NativeUnifiedADData) {
                    NativeUnifiedADData adsGDT = (NativeUnifiedADData) any;
                    ivLogo.setAdLogoType(AdNameType.GDT, adsGDT);
                    GlideUtil.loadRoundRect(mContext, mImage, adsGDT.getImgUrl(), 0);
                    mTitle.setText(adsGDT.getTitle());
                    mIntro.setText(adsGDT.getDesc());
                    mSource.setText("腾讯广告");
                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(mLayout);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(container.getLayoutParams());
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.START;
                    adsGDT.bindAdToView(mContext, container, layoutParams, clickableViews);
                    if (mPageLoader != null)
                        mPageLoader.setABCFail(false);
                } else if (any instanceof NativeResponse) {
                    NativeResponse data = (NativeResponse) any;
                }
            }

            @Override
            public void onRenderSuccess(@NotNull String channel, @NotNull View view, float width, float height) {
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
                if (mPageLoader != null)
                    mPageLoader.setABCFail(false);
            }

            @Override
            public void onADShow(@NotNull String channel) {
//                Log.d("PageView", "onAdShow：" + channel);
                mPvPage.drawIndex = 0;
                mPvPage.adShow = true;
                mPvPage.postInvalidate();
            }

            @Override
            public void onDisLike(@NotNull String channel, int position, @NotNull String value) {
                mExpressContainer.removeAllViews();
                mPvPage.postInvalidate();
                mPageLoader.skipToNextPage();
            }
        });
    }

    private void requestAdBottom() {
        if (isABC || isListenBook) {
            isFirstRequest = true;
            return;
        }
        TogetherAdFakeBanner.getMixAd(this, AdConfig.bannerAdConfig(this), TogetherAdConst.AD_BTM_BANNER, 1, 48F, new TogetherAdFakeBanner.AdListenerList() {
            @Override
            public void onAdFailed(@Nullable String failedMsg) {
                Log.e(TAG, "onAdFailed.AdBottom()===" + failedMsg);
                adContainer.setVisibility(View.INVISIBLE);
                mBannerContainer.setVisibility(View.INVISIBLE);
                btnBottomAd.setVisibility(View.INVISIBLE);
                ivBackgroud.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdLoaded(@NotNull String channel, @NotNull List<?> adList) {
                Object any = adList.get(0);
                if (any instanceof NativeUnifiedADData) {
                    NativeUnifiedADData adsGDT = (NativeUnifiedADData) any;
                    if (adContainer.getChildCount() > 1) {
                        adContainer.removeViews(1, adContainer.getChildCount() - 1);
                    }
                    GlideUtil.loadRoundRect(mContext, ivAdImg, adsGDT.getImgUrl());
                    tvAdTitle.setText(adsGDT.getTitle());
                    tvAdIntro.setText(adsGDT.getDesc());
                    tvAdSource.setText("腾讯广告");
                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(ctlTouchLayout);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adContainer.getLayoutParams());
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.START;
                    adsGDT.bindAdToView(mContext, adContainer, null, clickableViews);
                } else if (any instanceof NativeResponse) {
                    NativeResponse data = (NativeResponse) any;
                    adContainer.setVisibility(View.INVISIBLE);
                    mBannerContainer.setVisibility(View.INVISIBLE);
                    btnBottomAd.setVisibility(View.INVISIBLE);
                    ivBackgroud.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStartRequest(@NotNull String channel) {
//                Log.e(TAG, "onRenderSuccess.AdBottom()===" + channel);
                ivBackgroud.setVisibility(View.INVISIBLE);
                switch (channel) {
                    case "gdt":
                        mBannerContainer.setVisibility(View.INVISIBLE);
                        adContainer.setVisibility(VISIBLE);
                        btnBottomAd.setVisibility(View.INVISIBLE);
                        break;
                    case "csj":
                        mBannerContainer.setVisibility(VISIBLE);
                        adContainer.setVisibility(View.INVISIBLE);
                        btnBottomAd.setVisibility(View.VISIBLE);
                        break;
                    case "baidu":
                        adContainer.setVisibility(View.INVISIBLE);
                        mBannerContainer.setVisibility(View.INVISIBLE);
                        btnBottomAd.setVisibility(View.INVISIBLE);
                        ivBackgroud.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onCsjRenderSuccess(@NotNull View view, float width, float height) {
                ivBackgroud.setVisibility(View.INVISIBLE);
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(view);
            }
        });
        mHandler.sendEmptyMessageDelayed(MSG_BOTTOM_AD, POLLING_REQUE_BOTTOM_AD);
    }

    private boolean verify = false;

    private void loadVideoAd() {
        if (isABC)
            return;

        String token = TokenCache.getToken(mContext);
        String adConst = TogetherAdConst.AD_WELFARE_STIMULATE_2;

        TogetherAdStimulate.showAdFull(mContext, token, AdConfig.welfareBonusesConfig(mContext), adConst, new TogetherAdStimulate.AdListenerSplashFull() {

            @Override
            public void onStartRequest(@NotNull String channel) {
                WaitDialog.show((AppCompatActivity) mContext, R.string.loading).setCancelable(false);
            }

            @Override
            public void onADClick(@NotNull String channel) {

            }

            @Override
            public void onAdFailed(@Nullable String failedMsg) {
                Tip.show(failedMsg);
            }

            @Override
            public void onAdRewardVerify(boolean rewardVerify) {
                verify = rewardVerify;
                if (verify) {
                    reportStimulateMission(Consts.WELFARE_CHANGEADVERT_API);
                }
            }

            @Override
            public void onAdDismissed() {
                if (verify) {
                    AdvFreeSuccessActivity.start(mContext);
                } else {
                    Tip.show("激励视频奖励验证未通过");
                }
                verify = false;
            }

            @Override
            public void onAdPrepared(@NotNull String channel) {
                WaitDialog.dismiss();
            }
        });
    }

    /**
     * 上报激励视频任务验证状况
     */
    private void reportStimulateMission(String url) {
        RxHttp.postForm(url)
                .addHeader(Consts.TOKEN, TokenCache.getToken(mContext))
                .asResponse(String.class)
                .subscribe(s -> {
                }, (OnError) ErrorInfo::show);
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();

        if (isListenBook) {
            if (appBarLayout.getVisibility() == View.VISIBLE && llBottomMenu.getVisibility() == VISIBLE) {
                //关闭
                appBarLayout.startAnimation(mTopOutAnim);
                llBottomMenu.startAnimation(mBottomOutAnim);
                appBarLayout.setVisibility(GONE);
                llBottomMenu.setVisibility(GONE);
                tvPageTip.setVisibility(GONE);

                if (hideStatusBar) {
                    hideSystemBar();
                }
            }
            if (llSpeakMenu.getVisibility() == View.VISIBLE) {
                llSpeakMenu.startAnimation(mBottomOutAnim);
                llSpeakMenu.setVisibility(GONE);
                mTts.resumeSpeaking();
            } else {
                llSpeakMenu.setVisibility(VISIBLE);
                llSpeakMenu.startAnimation(mBottomInAnim);
                mTts.pauseSpeaking();
            }
            return;
        }

        if (appBarLayout.getVisibility() == View.VISIBLE && llBottomMenu.getVisibility() == VISIBLE) {
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
        } else if (mBrightnessDialog.isShowing()) {
            mBrightnessDialog.dismiss();
            return true;
        }
        return false;
    }

    //初始化菜单动画
    private void initMenuAnim() {
        if (mTopInAnim != null) return;

        mTopInAnim = AnimationUtils.loadAnimation(mContext, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(mContext, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.slide_bottom_out);
        /*设置弹窗动画执行速度*/
        mTopInAnim.setDuration(250);
        mBottomInAnim.setDuration(250);
        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }

    private void toggleNightMode() {
        if (isNightMode) {
            tvNightMode.setText(StringUtils.getString(R.string.nb_mode_morning));
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_read_menu_morning);
            tvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        } else {
            tvNightMode.setText(StringUtils.getString(R.string.nb_mode_night));
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_read_menu_night);
            tvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    ContentResolver cr = getContentResolver();
                    cr.unregisterContentObserver(mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFontChangeEvent(Font event) {
        String fontPath = event.getFontPath();
        if (fontPath.equals(Constant.FONT_TYPE) || FileUtils.isFontDownload(fontPath))
            mPageLoader.setFont(event.getFontPath());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onABCRefreshEvent(AdMessage event) {
        site--;
        isABC = testingIsABC(-1);
        if (isABC) {
            needRefreshPage = true;
            rl.setVisibility(GONE);
        } else {
            if (needRefreshPage)
                mPageLoader.refreshPage();
            needRefreshPage = false;
            rl.setVisibility(VISIBLE);
        }
        mPageLoader.setABC(isABC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (timer == null)
            doPolling(READ_TYPE_START);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (isCollected) {
            mPageLoader.saveRecord();
        }
        doPolling(READ_TYPE_STOP);
        int pos = mPageLoader.getChapterPos();
        if (!bookChapters.isEmpty() && pos >= 0)
            presenter.recordRead(ReadBookActivity.this, mBookId, bookChapters.get(pos).getId(), bookChapters.get(pos).getName(), pos + 1);
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
            throwable.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mReceiver);
        mHandler.removeMessages(MSG_BOTTOM_AD);
        mHandler.removeMessages(MSG_POLLING);
        mHandler.removeMessages(WHAT_CATEGORY);
        mHandler.removeMessages(WHAT_CHAPTER);
        mHandler.removeMessages(MSG_IS_ABC);

        mPageLoader.closeBook();
        mPageLoader = null;
        OkGo.getInstance().cancelAll();
    }

    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mPageLoader.updateBattery(level);
            }
            // 监听分钟的变化
            else if (TextUtils.equals(intent.getAction(), Intent.ACTION_TIME_TICK)) {
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
//                Log.d(TAG, "亮度模式改变");
            } else if (BRIGHTNESS_URI.equals(uri) && !BrightnessUtils.isAutoBrightness(mContext)) {
//                Log.d(TAG, "亮度模式为手动模式 值改变");
                BrightnessUtils.setBrightness(mContext, BrightnessUtils.getScreenBrightness(mContext));
            } else if (BRIGHTNESS_ADJ_URI.equals(uri) && BrightnessUtils.isAutoBrightness(mContext)) {
//                Log.d(TAG, "亮度模式为自动模式 值改变");
                BrightnessUtils.setDefaultBrightness(mContext);
            } else {
//                Log.d(TAG, "亮度调整 其他");
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (appBarLayout.getVisibility() == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen()) {
                toggleMenu(true);
            }
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return;
        } else if (mBrightnessDialog.isShowing()) {
            mBrightnessDialog.dismiss();
            return;
        } else if (mDlSlide.isDrawerOpen(GravityCompat.START)) {
            mDlSlide.closeDrawer(GravityCompat.START);
            return;
        }

        if (!data.getIsLocal() && !isCollected && data.getBookChapters() != null && !data.getBookChapters().isEmpty()) {
            MessageDialog.show(ReadBookActivity.this, "加入书架", "喜欢本书就加入书架吧", "确定", "取消")
                    .setCancelable(true)
                    .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                        @Override
                        public boolean onClick(BaseDialog baseDialog, View v) {
                            exit();
                            baseDialog.doDismiss();
                            return false;
                        }
                    })
                    .setOnOkButtonClickListener((baseDialog, v) -> {
                        //设置为已收藏
                        isCollected = true;
                        //设置阅读时间
                        data.setLastRead(StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
                        BookRepository.getInstance().saveBooksListWithAsync(data);
                        presenter.reqAddBookrack(ReadBookActivity.this, mBookId);
                        baseDialog.doDismiss();
                        return false;
                    });
        } else {
            exit();
        }
    }

    private void doPolling(int type) {
        if (TextUtils.isEmpty(id)) {
            mHandler.sendEmptyMessageDelayed(MSG_POLLING, POLLING_INTERVAL);
            return;
        }
        String check = MD5Utils.strToMd5By32(id + title);
        if (type == READ_TYPE_START)
            //开始计时
            startMethod();
        else {
            if (timer != null) {
                timerTask.cancel();
                timer = null;
                timerTask = null;
            }
        }
        presenter.recordDuration(ReadBookActivity.this, type, second, id, check, sum);
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
        boolean isVolumeTurnPage = ReadSettingManager.getInstance().isVolumeTurnPage();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!isListenBook && isVolumeTurnPage) {
                    return mPageLoader.skipToPrePage();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (!isListenBook && isVolumeTurnPage) {
                    return mPageLoader.skipToNextPage();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSystemBar() {
        if (isFullScreen) {
            SystemBarUtils.cancelFullScreen(mContext);
            appBarLayout.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
            llDrawerLayout.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
            StatusBarUtils.setColor(mContext, ContextCompat.getColor(mContext, R.color.black), 0);
            StatusBarUtils.setAndroidNativeLightStatusBar(mContext, false);
        }
    }

    private void hideSystemBar() {
        if (isFullScreen) {
            appBarLayout.setPadding(0, 0, 0, 0);
            llDrawerLayout.setPadding(0, 0, 0, 0);
            NotchTools.getFullScreenTools().fullScreenUseStatus(mContext);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MORE_SETTING) {
            boolean fullScreen = ReadSettingManager.getInstance().isFullScreen();
            if (isFullScreen != fullScreen) {
                isFullScreen = fullScreen;
            }
            // 设置显示状态
            if (isFullScreen) {
                hideSystemBar();
            } else {
                appBarLayout.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
                llDrawerLayout.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
                SystemBarUtils.cancelFullScreen(mContext);
                StatusBarUtils.setColor(mContext, ContextCompat.getColor(mContext, R.color.black), 0);
                StatusBarUtils.setAndroidNativeLightStatusBar(mContext, false);
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
        if (code == -1) {
//            Log.d(TAG, "onFailure");
            isABC = testingIsABC(-1);
            if (isABC) {
                needRefreshPage = true;
                rl.setVisibility(GONE);
            } else {
                if (needRefreshPage)
                    mPageLoader.refreshPage();
                needRefreshPage = false;
                rl.setVisibility(VISIBLE);
            }
            mPageLoader.setABC(isABC);
        }
        showToast(err);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
//        TipDialog.show(ReadBookActivity.this, msg, TipDialog.TYPE.ERROR);
    }

    @Override
    protected ReadBookContract.Presenter initPresenter() {
        return new ReadBookPresenter();
    }

}