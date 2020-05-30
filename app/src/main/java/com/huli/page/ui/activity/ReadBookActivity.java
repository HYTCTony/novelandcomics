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
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.appbar.AppBarLayout;
import com.huli.foxread.R;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.config.TTAdManagerHolder;
import com.huli.foxread.notchtools.NotchTools;
import com.huli.foxread.ui.activities.AdvFreeSuccessActivity;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.BookChapter;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.model.local.BookRepository;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.presenter.ReadBookPresenter;
import com.huli.page.presenter.contract.ReadBookContract;
import com.huli.page.ui.adapter.CatalogAdapter;
import com.huli.page.ui.base.BaseMvpViewActivity;
import com.huli.page.ui.dialog.BrightnessDialog;
import com.huli.page.ui.dialog.DislikeDialog;
import com.huli.page.ui.dialog.ReadSettingDialog;
import com.huli.page.utils.BrightnessUtils;
import com.huli.page.utils.Constant;
import com.huli.page.utils.MD5Utils;
import com.huli.page.utils.ScreenUtils;
import com.huli.page.utils.SpanUtils;
import com.huli.page.utils.StringUtils;
import com.huli.page.utils.SystemBarUtils;
import com.huli.page.widget.page.PageStyle;
import com.huli.page.widget.page.TxtChapter;
import com.huli.page.widget.read.PageView;
import com.huli.page.widget.read.ReadLoader;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private static final int POLLING_REQUE_BOTTOM_AD = 2 * 60 * 1000;
    private static final int POLLING_SET_IS_ABC = 60 * 1000;
    private static final int READ_ONE_PAGE_INTERVAL = 15;

    private View mAdView;
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private TTNativeExpressAd mTTAdPage;
    private TTNativeExpressAd mTTAdBottom;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    @BindView(R.id.banner_container)
    FrameLayout mBannerContainer;
    @BindView(R.id.iv_bg_bottom_view)
    ImageView ivBackgroud;
    @BindView(R.id.btn_bottom_ad)
    TextView btnBottomAd;
    @BindView(R.id.rl)
    RelativeLayout rl;
    RelativeLayout mExpressContainer;
    TextView btnNextPage;
    TextView tvAdView;
    /*观看视频验证*/
    private boolean mRewardVerify;
    private boolean isABC = false;
    private boolean isFirstRequest = true;

    private String id;
    private String title;
    private int sum;
    private int quota = READ_ONE_PAGE_INTERVAL;
    private long second = 0;
    private Timer timer;
    private TimerTask timerTask;

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
//                    doPolling(READ_TYPE_CONTINUE);
                    break;
                case MSG_BOTTOM_AD:
                    requestAdBottom();
                    break;
                case MSG_IS_ABC:
                    isABC = testingIsABC();
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
        PageStyle mPageStyle = ReadSettingManager.getInstance().getPageStyle();
        rl.setBackgroundResource(isNightMode ? R.color.hl_read_bg_night : mPageStyle.getBgColor());
        mBookId = data.getNovel_id();
        //获取页面加载器
        mPageLoader = mPvPage.getPageLoader(data);
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
        //获取目录
        loadCategory();
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
                if (isFirstRequest && sum != 0) {
                    requestAdBottom();
                    isFirstRequest = false;
                }
                quota = READ_ONE_PAGE_INTERVAL;
                sum++;
            }

            @Override
            public void onStyleChange(PageStyle pageStyle, boolean isNightMode) {
                rl.setBackgroundResource(isNightMode ? R.color.hl_read_bg_night : pageStyle.getBgColor());
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
        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //封面
        View coverPageView = LayoutInflater.from(this).inflate(R.layout.layout_cover_view, null, false);
        ImageView ivBookCover = coverPageView.findViewById(R.id.iv_book_cover);
        TextView tvBookName = coverPageView.findViewById(R.id.tv_book_name);
        TextView tvAuthorName = coverPageView.findViewById(R.id.tv_author_name);
        TextView tvCopyrightDescription = coverPageView.findViewById(R.id.tv_copyright_description);
        GlideUtil.loadRoundRect(mContext, ivBookCover, data.getHttp_novel_image());
        tvBookName.setText(data.getNovel_name());
        tvAuthorName.setText("作者：" + data.getAuthor());
        SpannableStringBuilder builderCopyrightDescription = new SpanUtils(mContext).appendLine("本书已授权一起看书进行电子制作发行").append
                ("本故事纯属虚构·版权所有·侵权必究").create();
        tvCopyrightDescription.setText(builderCopyrightDescription);
        //广告
        mAdView = LayoutInflater.from(this).inflate(R.layout.layout_ad_view, null, false);
        mExpressContainer = mAdView.findViewById(R.id.express_container);
        tvAdView = mAdView.findViewById(R.id.btn_watch_video);
        SpannableStringBuilder builderVideoMessage = new SpanUtils(mContext).append("看小视频免20分钟广告>").setUnderline().create();
        tvAdView.setText(builderVideoMessage);
        btnNextPage = mAdView.findViewById(R.id.btn_next_page);
        btnNextPage.setTextColor(isNightMode ? ContextCompat.getColor(mContext, R.color.txt_black) : ContextCompat.getColor(mContext,
                R.color.txt_gray_b2));
        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPvPage.autoNextPage();
            }
        });
        tvAdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVideoAd();
            }
        });
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
    }

    private void requestAdPage() {
        if (isABC) {
            return;
        }
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlotPage = new AdSlot.Builder()
                .setCodeId("945191706") //广告位id  945191678*视频   945191706*图片
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(ScreenUtils.getScreenSize(mContext)[0], 0) //期望模板广告view的size,单位dp
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadNativeExpressAd(adSlotPage, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
//                Log.e("ExpressView", "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAdPage = ads.get(0);
                bindAdListener1(mTTAdPage);
                startTime = System.currentTimeMillis();
                mTTAdPage.render();
            }
        });
    }

    private void requestAdBottom() {
        if (isABC) {
            return;
        }
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlotBottom = new AdSlot.Builder()
                .setCodeId("945191946") //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(ScreenUtils.getScreenSize(mContext)[0], 48) //期望模板广告view的size,单位dp
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadNativeExpressAd(adSlotBottom, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
//                Log.e("ExpressView", "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAdBottom = ads.get(0);
                bindAdListener2(mTTAdBottom);
                startTime = System.currentTimeMillis();
                mTTAdBottom.render();
            }
        });
        mHandler.sendEmptyMessageDelayed(MSG_BOTTOM_AD, POLLING_REQUE_BOTTOM_AD);
    }

    private void loadVideoAd() {
        if (isABC) {
            return;
        }
        WaitDialog.show(ReadBookActivity.this, R.string.loading).setCancelable(true);

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945192284")
                .setSupportDeepLink(true)
//                    .setRewardName("金币") //奖励的名称
//                    .setRewardAmount(3)  //奖励的数量
                .setUserID(TokenCache.getToken(mContext))//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
//                Log.e(TAG, "onError: " + code + ", " + String.valueOf(message));
                WaitDialog.dismiss();
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
//                Log.e(TAG, "onRewardVideoCached");
                WaitDialog.dismiss();

                if (mttRewardVideoAd != null) {
                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                    //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

                    //展示广告，并传入广告展示的场景
                    mttRewardVideoAd.showRewardVideoAd(mContext, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "foxread_test");
                    mttRewardVideoAd = null;
                } else {
//                    TToast.show(RewardVideoActivity.this, "请先加载广告");
                }
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                mttRewardVideoAd = ad;

                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                    }

                    @Override
                    public void onAdVideoBarClick() {
                    }

                    @Override
                    public void onAdClose() {
                        if (mRewardVerify) {
                            AdvFreeSuccessActivity.start(mContext);
                            mRewardVerify = false;
                            isABC = true;
                            mHandler.sendEmptyMessageDelayed(MSG_IS_ABC, POLLING_SET_IS_ABC);
                        }
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
//                        Log.e(TAG, "onVideoComplete");
                    }

                    @Override
                    public void onVideoError() {
//                        Log.e(TAG, "onVideoError");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        mRewardVerify = rewardVerify;
                        Log.e(TAG, "onRewardVerify===" + rewardVerify + "---" + rewardAmount + "---" + rewardName);
                    }

                    @Override
                    public void onSkippedVideo() {
//                        Log.e(TAG, "onSkippedVideo");
                    }
                });
            }
        });
    }

    private void bindAdListener1(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
//                Log.e("ExpressView", "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
//                Log.e("ExpressView", "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
//                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
//                Log.e("ExpressView", msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
//                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
//                Log.e("ExpressView", "width:" + width);
//                Log.e("ExpressView", "height:" + height);
                //返回view的宽高 单位 dp
//                Log.e("ExpressView", "渲染成功");
//                mAdView = view;
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);

            }
        });
        //dislike设置
//        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
//                Log.e("ExpressView", "点击开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
//                    Log.e("ExpressView", "下载中，点击暂停");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                Log.e("ExpressView", "下载暂停，点击继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                Log.e("ExpressView", "下载失败，点击重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
//                Log.e("ExpressView", "安装完成，点击图片打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                Log.e("ExpressView", "点击安装");
            }
        });
    }

    private void bindAdListener2(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
//                Log.e("ExpressView", "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
//                Log.e("ExpressView", "广告展示");
                btnBottomAd.setVisibility(VISIBLE);
                ivBackgroud.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
//                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
//                Log.e("ExpressView", msg + " code:" + code);
                btnBottomAd.setVisibility(View.INVISIBLE);
                ivBackgroud.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
//                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
//                Log.e("ExpressView", "渲染成功");
                ivBackgroud.setVisibility(View.INVISIBLE);
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(view);
            }
        });
        //dislike设置
//        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
//                Log.e("ExpressView", "点击开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
//                    Log.e("ExpressView", "下载中，点击暂停");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                Log.e("ExpressView", "下载暂停，点击继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                Log.e("ExpressView", "下载失败，点击重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
//                Log.e("ExpressView", "安装完成，点击图片打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                Log.e("ExpressView", "点击安装");
            }
        });
    }

    /**
     * 设置广告的不喜欢，注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
     *
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            //使用自定义样式
            List<FilterWord> words = ad.getFilterWords();
            if (words == null || words.isEmpty()) {
                return;
            }

            final DislikeDialog dislikeDialog = new DislikeDialog(this, words);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //屏蔽广告
//                    Log.e("ExpressView", "点击 " + filterWord.getName());
                    //用户选择不喜欢原因后，移除广告展示
//                    mExpressContainer.removeAllViews();
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback(ReadBookActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
//                Log.e("ExpressView", "点击 " + value);
                //用户选择不喜欢原因后，移除广告展示
//                mExpressContainer.removeAllViews();
            }

            @Override
            public void onCancel() {
//                Log.e("ExpressView", "点击取消 ");
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

    private void loadCategory() {
//        // 如果是已经收藏的，那么就从数据库中获取目录
//        if (isCollected) {
//            Disposable disposable = BookRepository.getInstance()
//                    .getBookChaptersFormRx(mBookId)
//                    .compose(RxUtils::toSimpleSingle)
//                    .subscribe((bookChapterBeen, throwable) -> {
//                                // 设置 CollBook
//                                mPageLoader.getCollBook().setBookChapters(bookChapterBeen);
//                                // 刷新章节列表
//                                mPageLoader.refreshChapterList();
//                                // 如果是网络小说并被标记更新的，则从网络下载目录
//                                if (data.getIsUpdate() && !data.getIsLocal()) {
//                                    presenter.loadCategory(ReadBookActivity.this, mBookId);
//                                }
//                            }
//                    );
//            CompositeDisposable mDisposable = new CompositeDisposable();
//            mDisposable.add(disposable);
//        } else {
//            // 从网络中获取目录
//        }
        presenter.loadCategory(ReadBookActivity.this, mBookId);
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
//        if (data.getIsUpdate() && isCollected) {
//            BookRepository.getInstance().saveBookChaptersToAsync(bookChapters);
//        }
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
            R.id.read_tv_setting})
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
                PageStyle mPageStyle = ReadSettingManager.getInstance().getPageStyle();
                if (mPageStyle.getBgColor() == R.color.hl_read_bg_1) {
                    llDrawerLayout.setBackgroundResource(isNightMode ? R.color.hl_read_bg_night : R.drawable.theme_leather_bg);
                } else {
                    llDrawerLayout.setBackgroundResource(isNightMode ? R.color.hl_read_bg_night : mPageStyle.getBgColor());
                }
                tvTitle.setTextColor(ContextCompat.getColor(mContext, isNightMode ? R.color.hl_read_font_night : mPageStyle.getFontColor()));
                tvBookStatu.setTextColor(ContextCompat.getColor(mContext, isNightMode ? R.color.hl_read_font_night : mPageStyle.getFontColor()));
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
                btnNextPage.setTextColor(isNightMode ? ContextCompat.getColor(mContext, R.color.txt_black) : ContextCompat.getColor(mContext,
                        R.color.txt_gray_b2));
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
        }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();

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

    @Override
    protected void onResume() {
        super.onResume();
        isABC = testingIsABC();
        if (isABC) {
            rl.setVisibility(GONE);
            mPageLoader.setABC(isABC);
        }
        //加载广告
        requestAdPage();
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!TextUtils.isEmpty(id) && timer == null)
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
        unregisterReceiver(mReceiver);
        mHandler.removeMessages(MSG_BOTTOM_AD);
        mHandler.removeMessages(MSG_POLLING);
        mHandler.removeMessages(WHAT_CATEGORY);
        mHandler.removeMessages(WHAT_CHAPTER);
        mHandler.removeMessages(MSG_IS_ABC);

        mPageLoader.closeBook();
        mPageLoader = null;
        if (mTTAdPage != null) {
            mTTAdPage.destroy();
        }
        if (mTTAdBottom != null) {
            mTTAdBottom.destroy();
        }
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
//        mHandler.sendEmptyMessageDelayed(MSG_POLLING, POLLING_INTERVAL);
    }

    private boolean testingIsABC() {
        long now = System.currentTimeMillis();
        long advertTime = ReadSettingManager.getInstance().getAdvertTime() * 1000;
        Log.d(TAG, "now==" + now + "    advertTime==" + advertTime);
        return advertTime > now;
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