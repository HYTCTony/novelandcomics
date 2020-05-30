package com.huli.page.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.ui.activity.MoreSettingActivity;
import com.huli.page.ui.activity.ReadBookActivity;
import com.huli.page.ui.adapter.PageStyleAdapter;
import com.huli.page.utils.BrightnessUtils;
import com.huli.page.utils.ScreenUtils;
import com.huli.page.widget.page.PageMode;
import com.huli.page.widget.page.PageStyle;
import com.huli.page.widget.page.TxtSpecing;
import com.huli.page.widget.read.ReadLoader;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReadSettingDialog extends Dialog {
    private static final String TAG = "ReadSettingDialog";
    private static final int DEFAULT_TEXT_SIZE = 20;

    @BindView(R.id.read_setting_iv_brightness_minus)
    ImageView mIvBrightnessMinus;
    @BindView(R.id.read_setting_sb_brightness)
    SeekBar mSbBrightness;
    @BindView(R.id.read_setting_iv_brightness_plus)
    ImageView mIvBrightnessPlus;
    @BindView(R.id.read_setting_cb_brightness_auto)
    CheckBox mCbBrightnessAuto;
    @BindView(R.id.read_setting_tv_font_minus)
    TextView mTvFontMinus;
    @BindView(R.id.read_setting_tv_font)
    TextView mTvFont;
    @BindView(R.id.read_setting_tv_font_plus)
    TextView mTvFontPlus;
    @BindView(R.id.read_setting_cb_font_default)
    CheckBox mCbFontDefault;

    @BindView(R.id.read_setting_page_spacing)
    RadioGroup mRgSpacingMode;
    @BindView(R.id.read_setting_spacing_small)
    RadioButton mRbSpacingSmall;
    @BindView(R.id.read_setting_spacing_medium)
    RadioButton mRbSpacingMedium;
    @BindView(R.id.read_setting_spacing_big)
    RadioButton mRbSpacingBig;

    @BindView(R.id.read_setting_rg_page_mode)
    RadioGroup mRgPageMode;
    @BindView(R.id.read_setting_rb_simulation)
    RadioButton mRbSimulation;
    @BindView(R.id.read_setting_rb_cover)
    RadioButton mRbCover;
    @BindView(R.id.read_setting_rb_slide)
    RadioButton mRbSlide;
    @BindView(R.id.read_setting_rb_scroll)
    RadioButton mRbScroll;
    @BindView(R.id.read_setting_rb_none)
    RadioButton mRbNone;

    @BindView(R.id.read_setting_rv_bg)
    RecyclerView mRvBg;
    @BindView(R.id.read_setting_tv_more)
    TextView mTvMore;
    /************************************/
    private PageStyleAdapter mPageStyleAdapter;
    private ReadSettingManager mSettingManager;
    private ReadLoader mReadLoader;
    private Activity mActivity;

    private PageMode mPageMode;
    private PageStyle mPageStyle;
    private TxtSpecing txtSpecing;

    private int mBrightness;
    private int mTextSize;

    private boolean isBrightnessAuto;
    private boolean isTextDefault;

    public ReadSettingDialog(@NonNull Activity activity, ReadLoader mPageLoader) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        this.mReadLoader = mPageLoader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read_setting);
        ButterKnife.bind(this);
        setUpWindow();
        initData();
        initWidget();
        initClick();
    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();

        isBrightnessAuto = mSettingManager.isBrightnessAuto();
        mBrightness = mSettingManager.getBrightness();
        mTextSize = mSettingManager.getTextSize();
        isTextDefault = mSettingManager.isDefaultTextSize();
        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();
        txtSpecing = mSettingManager.getTxtSpecing();
    }

    private void initWidget() {
        mSbBrightness.setProgress(mBrightness);
        mTvFont.setText(mTextSize + "");
        mCbBrightnessAuto.setChecked(isBrightnessAuto);
        mCbFontDefault.setChecked(isTextDefault);
        initPageMode();
        initSpecingMode();
        setUpAdapter();
    }

    private void setUpAdapter() {
        Drawable[] drawables = {
                getDrawable(R.drawable.oval_bg_hl_read_1)
                , getDrawable(R.drawable.oval_bg_hl_read_2)
                , getDrawable(R.drawable.oval_bg_hl_read_3)
                , getDrawable(R.drawable.oval_bg_hl_read_4)
                , getDrawable(R.drawable.oval_bg_hl_read_5)
                , getDrawable(R.drawable.oval_bg_hl_read_6)};

        mPageStyleAdapter = new PageStyleAdapter();
        mRvBg.setLayoutManager(new GridLayoutManager(getContext(), 6));
        mRvBg.setAdapter(mPageStyleAdapter);
        mRvBg.addItemDecoration(new GridSpacingItemDecoration(6, DensityUtils.dp2px(getContext(), 8), false));
        mPageStyleAdapter.refreshItems(Arrays.asList(drawables));

        mPageStyleAdapter.setPageStyleChecked(mPageStyle);

    }

    private void initPageMode() {
        switch (mPageMode) {
            case SIMULATION:
                mRbSimulation.setChecked(true);
                break;
            case COVER:
                mRbCover.setChecked(true);
                break;
            case SLIDE:
                mRbSlide.setChecked(true);
                break;
            case NONE:
                mRbNone.setChecked(true);
                break;
            case SCROLL:
                mRbScroll.setChecked(true);
                break;
        }
    }

    private void initSpecingMode() {
        switch (txtSpecing) {
            case SP_0:
                mRbSpacingSmall.setChecked(true);
                break;
            case SP_1:
                mRbSpacingMedium.setChecked(true);
                break;
            case SP_2:
                mRbSpacingBig.setChecked(true);
                break;
        }
    }

    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }

    private void initClick() {
        //亮度调节
        mIvBrightnessMinus.setOnClickListener(
                (v) -> {
                    if (mCbBrightnessAuto.isChecked()) {
                        mCbBrightnessAuto.setChecked(false);
                    }
                    int progress = mSbBrightness.getProgress() - 1;
                    if (progress < 0) return;
                    mSbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                }
        );
        mIvBrightnessPlus.setOnClickListener(
                (v) -> {
                    if (mCbBrightnessAuto.isChecked()) {
                        mCbBrightnessAuto.setChecked(false);
                    }
                    int progress = mSbBrightness.getProgress() + 1;
                    if (progress > mSbBrightness.getMax()) return;
                    mSbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                    //设置进度
                    ReadSettingManager.getInstance().setBrightness(progress);
                }
        );

        mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mCbBrightnessAuto.isChecked()) {
                    mCbBrightnessAuto.setChecked(false);
                }
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(mActivity, progress);
                //存储亮度的进度条
                ReadSettingManager.getInstance().setBrightness(progress);
            }
        });

        mCbBrightnessAuto.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        //获取屏幕的亮度
                        BrightnessUtils.setBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity));
                    } else {
                        //获取进度条的亮度
                        BrightnessUtils.setBrightness(mActivity, mSbBrightness.getProgress());
                    }
                    ReadSettingManager.getInstance().setAutoBrightness(isChecked);
                }
        );

        //字体大小调节
        mTvFontMinus.setOnClickListener(
                (v) -> {
                    if (mCbFontDefault.isChecked()) {
                        mCbFontDefault.setChecked(false);
                    }
                    int fontSize = Integer.valueOf(mTvFont.getText().toString()) - 2;
                    if (fontSize < 24) {
                        fontSize = 24;
                    }
                    mTvFont.setText(fontSize + "");
                    mReadLoader.setTextSize(fontSize);
                }
        );

        mTvFontPlus.setOnClickListener(
                (v) -> {
                    if (mCbFontDefault.isChecked()) {
                        mCbFontDefault.setChecked(false);
                    }
                    int fontSize = Integer.valueOf(mTvFont.getText().toString()) + 2;
                    if (fontSize > 120) {
                        fontSize = 120;
                    }
                    mTvFont.setText(fontSize + "");
                    mReadLoader.setTextSize(fontSize);
                }
        );

        mCbFontDefault.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        int fontSize = ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE);
                        mTvFont.setText(fontSize + "");
                        mReadLoader.setTextSize(fontSize);
                    }
                }
        );

        //Spacing Mode 切换
        mRgSpacingMode.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    TxtSpecing txtSpecing;
                    switch (checkedId) {
                        case R.id.read_setting_spacing_small:
                            txtSpecing = TxtSpecing.SP_0;
                            break;
                        case R.id.read_setting_spacing_medium:
                            txtSpecing = TxtSpecing.SP_1;
                            break;
                        case R.id.read_setting_spacing_big:
                            txtSpecing = TxtSpecing.SP_2;
                            break;
                        default:
                            txtSpecing = TxtSpecing.SP_1;
                            break;
                    }
                    mReadLoader.setTxtSpecing(txtSpecing);
                }
        );

        //Page Mode 切换
        mRgPageMode.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    PageMode pageMode;
                    switch (checkedId) {
                        case R.id.read_setting_rb_simulation:
                            pageMode = PageMode.SIMULATION;
                            break;
                        case R.id.read_setting_rb_cover:
                            pageMode = PageMode.COVER;
                            break;
                        case R.id.read_setting_rb_slide:
                            pageMode = PageMode.SLIDE;
                            break;
                        case R.id.read_setting_rb_scroll:
                            pageMode = PageMode.SCROLL;
                            break;
                        case R.id.read_setting_rb_none:
                            pageMode = PageMode.NONE;
                            break;
                        default:
                            pageMode = PageMode.SLIDE;
                            break;
                    }
                    mReadLoader.setPageMode(pageMode);
                }
        );

        //背景的点击事件
        mPageStyleAdapter.setOnItemClickListener(
                (view, pos) -> {
                    mReadLoader.setPageStyle(PageStyle.values()[pos]);
                    mReadLoader.setNightMode(false);
                }
        );

        //更多设置
        mTvMore.setOnClickListener(
                (v) -> {
                    Intent intent = new Intent(getContext(), MoreSettingActivity.class);
                    mActivity.startActivityForResult(intent, ReadBookActivity.REQUEST_MORE_SETTING);
                    //关闭当前设置
                    dismiss();
                }
        );
    }

    public boolean isBrightFollowSystem() {
        if (mCbBrightnessAuto == null) {
            return false;
        }
        return mCbBrightnessAuto.isChecked();
    }
}