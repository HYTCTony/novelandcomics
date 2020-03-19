package com.huli.foxread.ui.dialogs;

import android.content.DialogInterface;

import com.huli.foxread.R;
import com.huli.foxread.ui.dialogs.base.BaseDialog;
import com.huli.foxread.ui.dialogs.base.ViewHolder;


/**
 * BaseDialog show = LoadingDialog.newInstance()
 * .setDimAmout(0f)
 * .setOutCancel(false)
 * .show(getSupportFragmentManager());
 */
public class LoadingDialog extends BaseDialog {

    public static LoadingDialog newInstance() {
        return new LoadingDialog();
    }


    @Override
    public int setUpLayoutId() {
        return R.layout.layout_loadingview;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
