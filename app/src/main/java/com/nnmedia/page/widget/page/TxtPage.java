package com.nnmedia.page.widget.page;

import java.util.ArrayList;
import java.util.List;

public class TxtPage {
    public int position;
    public String title;
    public int titleLines; //当前 lines 中为 title 的行数。
    public List<String> lines;
    public List<Integer> highlight = new ArrayList<>();
    public float offset;
    public boolean wrap;

    public boolean isCustomView;//标记当前页是否是自填充view
    public boolean hasDrawAd;//标记是否已经添加过广告
    public String pageType = "";//标记是封面view还是广告view

    public static final String VALUE_STRING_COVER_TYPE = "cover", VALUE_STRING_AD_TYPE = "ad";
}
