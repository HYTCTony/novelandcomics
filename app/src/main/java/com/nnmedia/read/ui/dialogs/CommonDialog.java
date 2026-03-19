package com.nnmedia.read.ui.dialogs;


import com.nnmedia.read.ui.dialogs.base.BaseDialog;
import com.nnmedia.read.ui.dialogs.base.ViewHolder;

import androidx.annotation.LayoutRes;

/**
 * 创建人：Bill
 */
// CommonDialog.newInstance()
//         .setLayoutId(R.layout.dialog_main)
//         .setConvertListener(viewConvertListener)
//         .setDimAmout(0.5f)
//         .setShowBottom(true)
//         .setAnimStyle(R.style.DialogAnimation)
//         .setOnDismissListener(new DialogInterface.OnDismissListener() {
//@Override
//public void onDismiss(DialogInterface dialog) {
//        //显示AppBarLayout
//        showAppBarLayout();
//        }
//        })
//        .show(getSupportFragmentManager());
public class CommonDialog extends BaseDialog {

    private ViewConvertListener convertListener;

    public static CommonDialog newInstance() {
        return new CommonDialog();
    }

    /**
     * 设置Dialog布局
     *
     * @param layoutId
     * @return
     */
    public CommonDialog setLayoutId(@LayoutRes int layoutId) {
        this.mLayoutResId = layoutId;
        return this;
    }

    @Override
    public int setUpLayoutId() {
        return mLayoutResId;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        if (convertListener != null) {
            convertListener.convertView(holder, dialog);
        }
    }

    public CommonDialog setConvertListener(ViewConvertListener convertListener) {
        this.convertListener = convertListener;
        return this;
    }

    public interface ViewConvertListener {
        void convertView(ViewHolder holder, BaseDialog dialog);
    }
}
