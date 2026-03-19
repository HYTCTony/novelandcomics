package com.nnmedia.read.ui.dialogs;

import com.nnmedia.read.ui.dialogs.base.BaseDialog;
import com.nnmedia.read.ui.dialogs.base.ViewHolder;

import androidx.annotation.LayoutRes;

public class DownloadSelectDialog extends BaseDialog {

    private PConvertListener convertListener;

    public static DownloadSelectDialog newInstance() {
        DownloadSelectDialog fragmentB = new DownloadSelectDialog();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(ARGUMENTS_KEY, data);
//        fragmentB.setArguments(bundle);
        return fragmentB;
    }

    public DownloadSelectDialog setLayoutId(@LayoutRes int layoutId) {
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

    public DownloadSelectDialog setConvertListener(PConvertListener convertListener) {
        this.convertListener = convertListener;
        return this;
    }

    public interface PConvertListener {
        void convertView(ViewHolder holder, BaseDialog dialog);
    }
}