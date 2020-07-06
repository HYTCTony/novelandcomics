package com.huli.page.model.local;


import android.text.TextUtils;

import com.huli.page.utils.ScreenUtils;
import com.huli.page.utils.SharedPreUtils;
import com.huli.page.widget.page.PageMode;
import com.huli.page.widget.page.PageStyle;
import com.huli.page.widget.page.TxtSpecing;

/**
 * 阅读器的配置管理
 */
public class ReadSettingManager {
    public static final int READ_BG_DEFAULT = 0;
    public static final int READ_BG_1 = 1;
    public static final int READ_BG_2 = 2;
    public static final int READ_BG_3 = 3;
    public static final int READ_BG_4 = 4;
    public static final int NIGHT_MODE = 5;

    public static final String SHARED_READ_BG = "shared_read_bg";
    public static final String SHARED_READ_BRIGHTNESS = "shared_read_brightness";
    public static final String SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto";
    public static final String SHARED_READ_TEXT_SIZE = "shared_read_text_size";
    public static final String SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default";
    public static final String SHARED_READ_PAGE_MODE = "shared_read_mode";
    public static final String SHARED_READ_SPECING_MODE = "shared_specing_mode";
    public static final String SHARED_READ_NIGHT_MODE = "shared_night_mode";
    public static final String SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page";
    public static final String SHARED_READ_FULL_SCREEN = "shared_read_full_screen";
    public static final String SHARED_READ_CONVERT_TYPE = "shared_read_convert_type";
    public static final String SHARED_READ_ADVERT_TIME = "shared_read_advert_time";
    public static final String SHARED_READ_FONT_NAME = "shared_read_font_name";

    private static volatile ReadSettingManager sInstance;

    private SharedPreUtils sharedPreUtils;

    public static ReadSettingManager getInstance() {
        if (sInstance == null) {
            synchronized (ReadSettingManager.class) {
                if (sInstance == null) {
                    sInstance = new ReadSettingManager();
                }
            }
        }
        return sInstance;
    }

    private ReadSettingManager() {
        sharedPreUtils = SharedPreUtils.getInstance();
    }

    public void setPageStyle(PageStyle pageStyle) {
        sharedPreUtils.putInt(SHARED_READ_BG, pageStyle.ordinal());
    }

    public PageStyle getPageStyle() {
        int style = sharedPreUtils.getInt(SHARED_READ_BG, PageStyle.BG_0.ordinal());
        return PageStyle.values()[style];
    }

    public void setTxtSpecing(TxtSpecing txtSpecing) {
        sharedPreUtils.putInt(SHARED_READ_SPECING_MODE, txtSpecing.ordinal());
    }

    public TxtSpecing getTxtSpecing() {
        int style = sharedPreUtils.getInt(SHARED_READ_SPECING_MODE, TxtSpecing.SP_1.ordinal());
        return TxtSpecing.values()[style];
    }

    public void setBrightness(int progress) {
        sharedPreUtils.putInt(SHARED_READ_BRIGHTNESS, progress);
    }

    public int getBrightness() {
        return sharedPreUtils.getInt(SHARED_READ_BRIGHTNESS, 40);
    }

    public void setAutoBrightness(boolean isAuto) {
        sharedPreUtils.putBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto);
    }

    public boolean isBrightnessAuto() {
        return sharedPreUtils.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, true);
    }

    public void setDefaultTextSize(boolean isDefault) {
        sharedPreUtils.putBoolean(SHARED_READ_IS_TEXT_DEFAULT, isDefault);
    }

    public boolean isDefaultTextSize() {
        return sharedPreUtils.getBoolean(SHARED_READ_IS_TEXT_DEFAULT, false);
    }

    public void setTextSize(int textSize) {
        sharedPreUtils.putInt(SHARED_READ_TEXT_SIZE, textSize);
    }

    public int getTextSize() {
        return sharedPreUtils.getInt(SHARED_READ_TEXT_SIZE, ScreenUtils.spToPx(18));
    }

    public void setPageMode(PageMode mode) {
        sharedPreUtils.putInt(SHARED_READ_PAGE_MODE, mode.ordinal());
    }

    public PageMode getPageMode() {
        int mode = sharedPreUtils.getInt(SHARED_READ_PAGE_MODE, PageMode.SLIDE.ordinal());
        if (PageMode.values()[mode] == PageMode.SCROLL) {
            return PageMode.NONE;
        }
        return PageMode.values()[mode];
    }

    public void setNightMode(boolean isNight) {
        sharedPreUtils.putBoolean(SHARED_READ_NIGHT_MODE, isNight);
    }

    public boolean isNightMode() {
        return sharedPreUtils.getBoolean(SHARED_READ_NIGHT_MODE, false);
    }

    public void setVolumeTurnPage(boolean isTurn) {
        sharedPreUtils.putBoolean(SHARED_READ_VOLUME_TURN_PAGE, isTurn);
    }

    public boolean isVolumeTurnPage() {
        return sharedPreUtils.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false);
    }

    public void setFullScreen(boolean isFullScreen) {
        sharedPreUtils.putBoolean(SHARED_READ_FULL_SCREEN, isFullScreen);
    }

    public boolean isFullScreen() {
        return sharedPreUtils.getBoolean(SHARED_READ_FULL_SCREEN, true);
    }

    public void setConvertType(int convertType) {
        sharedPreUtils.putInt(SHARED_READ_CONVERT_TYPE, convertType);
    }

    public int getConvertType() {
        return sharedPreUtils.getInt(SHARED_READ_CONVERT_TYPE, 0);
    }

    public void setAdvertTime(long advert_time) {
        sharedPreUtils.putLong(SHARED_READ_ADVERT_TIME, advert_time);
    }

    public long getAdvertTime() {
        return sharedPreUtils.getLong(SHARED_READ_ADVERT_TIME, 0);
    }

    public void setFont(String fontName) {
        sharedPreUtils.putString(SHARED_READ_FONT_NAME, fontName);
    }

    public String getFont() {
        return TextUtils.isEmpty(sharedPreUtils.getString(SHARED_READ_FONT_NAME)) ? "DEFAULT" : sharedPreUtils.getString(SHARED_READ_FONT_NAME);
    }
}