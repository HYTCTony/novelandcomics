package com.huli.foxread.ui.dialogs;


import android.os.Bundle;

import com.huli.foxread.entity.PaymentInfoEntity;
import com.huli.foxread.ui.dialogs.base.BaseDialog;
import com.huli.foxread.ui.dialogs.base.ViewHolder;

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
public class PaymentSelectDialog extends BaseDialog {

    private static final String ARGUMENTS_KEY = "key_pay_info";


    public static PaymentSelectDialog newInstance(PaymentInfoEntity data) {
        PaymentSelectDialog fragmentB = new PaymentSelectDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENTS_KEY, data);
        fragmentB.setArguments(bundle);
        return fragmentB;
    }

    /**
     * 设置Dialog布局
     *
     * @param layoutId
     * @return
     */
    public PaymentSelectDialog setLayoutId(@LayoutRes int layoutId) {
        this.mLayoutResId = layoutId;
        return this;
    }

    @Override
    public int setUpLayoutId() {
        return mLayoutResId;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        Bundle bundle = getArguments();
        PaymentInfoEntity data;
        if (bundle != null) {
            data = (PaymentInfoEntity) bundle.getSerializable(ARGUMENTS_KEY);
            if (convertListener != null && data != null) {
                convertListener.convertView(holder, data, dialog);
            }
        }
    }

    private PConvertListener convertListener;

    public PaymentSelectDialog setConvertListener(PConvertListener convertListener) {
        this.convertListener = convertListener;
        return this;
    }

    public interface PConvertListener {
        void convertView(ViewHolder holder, PaymentInfoEntity data, BaseDialog dialog);
    }
}
