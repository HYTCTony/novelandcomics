package com.huli.page.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.Font;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.ui.adapter.FontAdapter;
import com.huli.page.ui.base.BaseViewActivity;
import com.huli.page.utils.Constant;
import com.huli.page.utils.FileUtils;
import com.rxjava.rxlife.RxLife;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TintTypedArray;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindColor;
import butterknife.BindView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class FontSelectionActivity extends BaseViewActivity {
    private static final String TAG = "FontSelectionActivity";

    @BindView(R.id.rv)
    RecyclerView rv;
    FontAdapter mAdapter;
    @BindColor(R.color.col_gray_e5e5e5)
    int grey;
    private ReadSettingManager mSettingManager;
    private String path;

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
//      StatusBarUtils.setTransparentForImageView(mContext, toolbar);
        StatusBarUtils.setColor(this, ContextCompat.getColor(mContext, R.color.black), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(mContext, false);
        mSettingManager = ReadSettingManager.getInstance();
        path = mSettingManager.getFont();

        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(grey)
                .sizeResId(R.dimen.dp_0_5)
                .build());

        mAdapter = new FontAdapter();
        rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                List<Font> datas = (List<Font>) adapter.getData();
                Font font = (Font) adapter.getItem(position);
                if (font.isDownload()) {
                    for (Font data : datas) {
                        data.setSelect(false);
                    }
                    font.setSelect(true);
                    adapter.notifyDataSetChanged();
                    mSettingManager.setFont(font.getFontPath());
                    EventBus.getDefault().post(font);
                } else {
                    showNotice("请先下载字体，再进行选择。");
                }
            }
        });

        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                Font font = (Font) adapter.getItem(position);
                font.setState(1);
                getFont(font);
            }
        });
        getFontList();
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        setTitle("字体");
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(mContext,
                null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        Drawable drawable = a.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
        drawable.setColorFilter(white, PorterDuff.Mode.MULTIPLY);
        toolbar.setNavigationIcon(drawable);
        toolbar.setTitleTextColor(white);
        toolbar.setSubtitleTextColor(white);
        super.initToolbar(toolbar);
    }

    private void getFontList() {
        RxHttp.postForm(Consts.GET_FONT_LIST)
                .asResponseList(Font.class)
                .to(RxLife.toMain(this))
                .subscribe(datas -> {
                    for (Font font : datas) {
                        font.setState(0);
                        if (TextUtils.isEmpty(font.getFile_name())) {
                            font.setFile_name(Constant.FONT_TYPE);
                            font.setFontPath(Constant.FONT_TYPE);
                        } else
                            font.setFontPath(Constant.FONT_DOWNLOAD_PATH + font.getFile_name());
                        if (path.equals(font.getFontPath())) {
                            font.setSelect(true);
                        }
                        if (font.getFile_name().equals(Constant.FONT_TYPE) || FileUtils.isFontDownload(font.getFontPath())) {
                            font.setDownload(true);
                        } else {
                            font.setDownload(false);
                        }
                    }
                    mAdapter.setNewInstance(datas);
                }, (OnError) error -> Toast.makeText(mContext, error.getErrorMsg(), Toast.LENGTH_SHORT).show());
    }

    public static long lastChangedTime;

    //500毫秒刷新一次列表
    private void notifyDataSetChanged(boolean force) {
        long time = System.currentTimeMillis();
        if (time - lastChangedTime > 500 || force) {
            mAdapter.notifyDataSetChanged();
            lastChangedTime = time;
        }
    }

    private void getFont(Font font) {
        //文件存储路径
        String destPath = Constant.FONT_DOWNLOAD_PATH + font.getFile_name();
        RxHttp.get(font.getHttp_file()).setAssemblyEnabled(false)
                .asDownload(destPath, progress -> {
                    //下载进度回调,0-100，仅在进度有更新时才会回调，最多回调101次，最后一次回调文件存储路径
                    font.setProgress(progress.getProgress());//当前进度 0-100
                    font.setCurrentSize(progress.getCurrentSize());//当前已下载的字节大小
                    font.setTotalSize(progress.getTotalSize()); //要下载的总字节大小
                    notifyDataSetChanged(false);
                }, AndroidSchedulers.mainThread()) //指定主线程回调
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {//s为String类型，这里为文件存储路径
                    //下载完成，处理相关逻辑
                    font.setDownload(true);
                    font.setState(4);
                    notifyDataSetChanged(true);
                }, throwable -> {
                    font.setState(5);
                    //下载失败，处理相关逻辑
                    mAdapter.notifyDataSetChanged();
                });
        font.setState(2);
    }

}