package com.nnmedia.read.ui.dialogs;

import android.app.Dialog;
import android.content.Context;

public class VersionDialog extends Dialog {
    private int res;

    public VersionDialog(Context context, int theme, int res) {
        super(context, theme);
        //  自动生成的构造函数存根
        setContentView(res);
        this.res = res;
        setCanceledOnTouchOutside(false);
    }
}
