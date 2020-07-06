package com.huli.page.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.Font;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.ui.adapter.FontAdapter;
import com.huli.page.ui.base.BaseViewActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TintTypedArray;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindColor;
import butterknife.BindView;

public class FontSelectionActivity extends BaseViewActivity {
    private static final String TAG = "FontSelectionActivity";

    @BindView(R.id.rv)
    RecyclerView rv;
    FontAdapter mAdapter;
    @BindColor(R.color.col_gray_e5e5e5)
    int grey;
    private ReadSettingManager mSettingManager;
    private String path;

    String[] fontName = {"系统字体", "思源宋体", "方正黑体", "手书体"};
    String[] fontPath = {"DEFAULT", "sourcehanserif_cn_regular_1.otf", "fangzhengheitijianti_1.ttf", "shoushuti_2.ttf"};

    public static void start(Context context) {
        Intent starter = new Intent(context, FontSelectionActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_select_font;
    }

    @Override
    protected void initView() {
        StatusBarUtils.setTransparentForImageView(mContext, toolbar);
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.white), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
        mSettingManager = ReadSettingManager.getInstance();
        path = mSettingManager.getFont();
        List<Font> datas = new ArrayList<>();
        for (int i = 0; i < fontName.length; i++) {
            Font font = new Font(fontName[i], fontPath[i]);
            if (path.equals(fontPath[i])) {
                font.setSelect(true);
            }
            datas.add(font);
        }

        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(grey)
                .sizeResId(R.dimen.dp_0_5)
                .build());

        mAdapter = new FontAdapter(datas);
        rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                List<Font> datas = (List<Font>) adapter.getData();
                for (Font font : datas) {
                    font.setSelect(false);
                }
                Font font = (Font) adapter.getItem(position);
                font.setSelect(true);
                adapter.notifyDataSetChanged();
                mSettingManager.setFont(font.getFontPath());
                EventBus.getDefault().post(font);
                Log.d(TAG, "click");
            }
        });
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        setTitle("字体");
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this,
                null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        Drawable drawable = a.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
        drawable.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
        toolbar.setNavigationIcon(drawable);
        toolbar.setTitleTextColor(black);
        toolbar.setSubtitleTextColor(black);
        super.initToolbar(toolbar);
    }
}