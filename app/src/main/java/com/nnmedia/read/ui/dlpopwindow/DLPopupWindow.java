package com.nnmedia.read.ui.dlpopwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.nnmedia.novel.R;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 创建时间：2020/7/7  11:15
 * 备注：
 */
public class DLPopupWindow extends PopupWindow {

    /**
     * 定义一个接口
     */
    public interface OnItemClickListener {
        void OnClick(int pos);
    }

    /**
     * 实例化
     */
    private OnItemClickListener onItemClickListener = null;

    /**
     * 设置点击回调
     *
     * @param on
     */
    public void setOnItemClickListener(OnItemClickListener on) {
        this.onItemClickListener = on;
    }

    /**
     * 微信样式
     */
    public static final int STYLE_WEIXIN = 1;
    /**
     * 默认样式
     */
    public static final int STYLE_DEF = 2;

    /**
     * 上下文
     */
    private Context mContext;
    private LayoutInflater mInflater;
    private View mContentView;
    /**
     * 适配器
     */
    private DLPopAdapter mAdapter;
    /**
     * 数据列表
     */
    private List<DLPopItem> mList;

    public DLPopupWindow(Context context, List<DLPopItem> list, int style) {
        this.mContext = context;
        this.mList = list;

        // 打气筒
        mInflater = LayoutInflater.from(mContext);
        // 打气
        mContentView = mInflater.inflate(R.layout.pop_window, null, false);
        // 设置View
        setContentView(mContentView);
        // 设置宽与高
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置可以获取集点
        setFocusable(true);
        // 设置背景只有设置了这个才可以点击外边和BACK消失
        setBackgroundDrawable(new ColorDrawable());
        // 设置点击外边可以消失
        setOutsideTouchable(true);

        RecyclerView recyclerView = mContentView.findViewById(R.id.recyclerView_popwindow);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new DLPopAdapter(mList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            // 回调
            onItemClickListener.OnClick(position);
            dismiss();
        });

        LinearLayout llBG = mContentView.findViewById(R.id.ll_bg);
        // 根据类型修改背景
        if (style == STYLE_WEIXIN) {
            llBG.setBackgroundResource(R.drawable.menu_open_weixin);
        } else {
            llBG.setBackgroundResource(R.drawable.menu_open);
        }
    }
}
