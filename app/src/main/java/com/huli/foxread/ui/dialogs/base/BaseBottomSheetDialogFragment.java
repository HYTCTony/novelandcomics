package com.huli.foxread.ui.dialogs.base;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.huli.foxread.R;
import com.huli.foxread.utils.DensityUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;


public abstract class BaseBottomSheetDialogFragment extends DialogFragment {

    protected View mRootView;
    protected BottomSheetBehavior<FrameLayout> behavior;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(this.getContext());
    }


    @Override
    public void onStart() {
        super.onStart();

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        FrameLayout bottomSheet = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            //设置DialogFragment外透明
            Window window = dialog.getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.4f;
            window.setAttributes(windowParams);

            if (isTransparent()) {
                setDialogBg(bottomSheet);
            }

            CoordinatorLayout.LayoutParams layoutParams =
                    (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = getExpandedHeight() - DensityUtils.dp2px(getContext(), 24);
//            layoutParams.width = getResources().getDisplayMetrics().widthPixels - 24;
            layoutParams.width = getResources().getDisplayMetrics().widthPixels;
            bottomSheet.setLayoutParams(layoutParams);
            behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(getPeekHeight());
            // 初始为展开状态
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    OnStateChange(bottomSheet, newState);
                    if(newState==BottomSheetBehavior.STATE_HIDDEN){
                        dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
        }
    }

    /**
     * 弹窗高度，默认为屏幕高度的2分之1
     * 子类可重写该方法返回peekHeight
     *
     * @return height
     */
    protected int getPeekHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels;
        return peekHeight - peekHeight / 2;
    }

    /**
     * 弹窗高度，最高高度
     *
     * @return height
     */
    protected int getExpandedHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels;
        return peekHeight;
//        return peekHeight - peekHeight / 2;
    }

    protected abstract void OnStateChange(View view, int i);

    /**
     * 返回布局 resId
     *
     * @return layoutId
     */
    protected abstract int getLayoutRes();

    protected abstract boolean isTransparent();

    protected void setDialogBg(FrameLayout bottomSheet) {
        //设置DialogFragment背景色
        bottomSheet.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 初始化数据
     *
     * @param savedInstanceState bundle
     */
    protected abstract void initData(Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutRes(), container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(savedInstanceState);
    }

}