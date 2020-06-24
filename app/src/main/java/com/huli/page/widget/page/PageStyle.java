package com.huli.page.widget.page;

import com.huli.foxread.R;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;


/**
 * 作用：页面的展示风格。
 */
public enum PageStyle {
    BG_0(R.color.hl_read_font_1,
            R.color.hl_read_bg_1,
            R.color.hl_prompt_font_1,
            R.color.hl_tips_font_1,
            R.color.hl_ad_view_bg_1,
            R.color.hl_select_font_1,
            R.mipmap.theme_book_cover_bg_01,
            R.mipmap.bg_theme_bottom_ad_01),
    BG_1(R.color.hl_read_font_2,
            R.color.hl_read_bg_2,
            R.color.hl_prompt_font_2,
            R.color.hl_tips_font_2,
            R.color.hl_ad_view_bg_2,
            R.color.hl_select_font_2,
            R.mipmap.theme_book_cover_bg_02,
            R.mipmap.bg_theme_bottom_ad_02),
    BG_2(R.color.hl_read_font_3,
            R.color.hl_read_bg_3,
            R.color.hl_prompt_font_3,
            R.color.hl_tips_font_3,
            R.color.hl_ad_view_bg_3,
            R.color.hl_select_font_3,
            R.mipmap.theme_book_cover_bg_03,
            R.mipmap.bg_theme_bottom_ad_03),
    BG_3(R.color.hl_read_font_4,
            R.color.hl_read_bg_4,
            R.color.hl_prompt_font_4,
            R.color.hl_tips_font_4,
            R.color.hl_ad_view_bg_4,
            R.color.hl_select_font_4,
            R.mipmap.theme_book_cover_bg_04,
            R.mipmap.bg_theme_bottom_ad_04),
    BG_4(R.color.hl_read_font_5,
            R.color.hl_read_bg_5,
            R.color.hl_prompt_font_5,
            R.color.hl_tips_font_5,
            R.color.hl_ad_view_bg_5,
            R.color.hl_select_font_5,
            R.mipmap.theme_book_cover_bg_05,
            R.mipmap.bg_theme_bottom_ad_05),
    BG_5(R.color.hl_read_font_6,
            R.color.hl_read_bg_6,
            R.color.hl_prompt_font_6,
            R.color.hl_tips_font_6,
            R.color.hl_ad_view_bg_6,
            R.color.hl_select_font_6,
            R.mipmap.theme_book_cover_bg_06,
            R.mipmap.bg_theme_bottom_ad_06),
    NIGHT(R.color.hl_read_font_night,
            R.color.hl_read_bg_night,
            R.color.hl_prompt_font_night,
            R.color.hl_tips_font_night,
            R.color.hl_ad_view_bg_night,
            R.color.hl_select_font_night,
            R.mipmap.theme_book_cover_bg_07,
            R.mipmap.bg_theme_bottom_ad_07);

    private int fontColor;
    private int bgColor;
    private int promptColor;
    private int tipsColor;
    private int adBgColor;
    private int selectFont;
    private int coverBgImg;
    private int adPlaceholderImg;

    PageStyle(@ColorRes int fontColor,
              @ColorRes int bgColor,
              @ColorRes int promptColor,
              @ColorRes int tipsColor,
              @ColorRes int adBgColor,
              @ColorRes int selectFont,
              @DrawableRes int coverBgImg,
              @DrawableRes int adPlaceholderImg) {
        this.fontColor = fontColor;
        this.bgColor = bgColor;
        this.promptColor = promptColor;
        this.tipsColor = tipsColor;
        this.adBgColor = adBgColor;
        this.selectFont = selectFont;
        this.coverBgImg = coverBgImg;
        this.adPlaceholderImg = adPlaceholderImg;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public int getPromptColor() {
        return promptColor;
    }

    public int getTipsColor() {
        return tipsColor;
    }

    public int getAdBgColor() {
        return adBgColor;
    }

    public int getSelectFont() {
        return selectFont;
    }

    public int getCoverBgImg() {
        return coverBgImg;
    }

    public int getAdPlaceholderImg() {
        return adPlaceholderImg;
    }
}