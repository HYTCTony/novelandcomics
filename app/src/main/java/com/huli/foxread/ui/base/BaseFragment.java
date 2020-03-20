package com.huli.foxread.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    private View mContextView = null;
    protected Activity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContextView = inflater.inflate(bindLayout(), container, false);

        setStatusBar(mContextView);

        initView(mContextView);
        setListener();
        doBusiness(mActivity);

        return mContextView;
    }

    /**
     * [绑定布局]
     *
     * @return
     */
    public abstract int bindLayout();

    /**
     * 在Fragment中设置状态栏样式
     *
     * @param view
     * @return
     */
    public abstract void setStatusBar(final View view);

    /**
     * [初始化控件]
     *
     * @param view
     */
    public abstract void initView(final View view);

    /**
     * [设置监听]
     */
    public abstract void setListener();

    /**
     * [业务操作]
     *
     * @param mContext
     */
    public abstract void doBusiness(Context mContext);

    @SuppressWarnings("unchecked")
    public <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }


    private static long lastClickTime;                //最后一次点击的时间
    /**
     * 无效的连续点击会重置 间隔时间
     *
     * @return 是否点击过快
     */
    protected boolean onMoreClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastClickTime;
        if (time < 800) {
            flag = true;
        }
        lastClickTime = System.currentTimeMillis();
        return flag;
    }
}
