package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.SomeMonitorEditText;
import com.kongzue.dialog.v3.TipDialog;
import com.rxjava.rxlife.RxLife;

import androidx.appcompat.widget.Toolbar;

public class BankCardBindActivity extends BaseActivity implements View.OnClickListener {

    private EditText etBankDepositName, etPayeeName, etIdCardNumber, etBankAccount, etBankAddress;
    private Button btnSubmit;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_bank_card_binding;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_bind_bankcard);

        etBankDepositName = $(R.id.editText_bank_of_deposit_name);
        etPayeeName = $(R.id.editText_name_of_payee);
        etIdCardNumber = $(R.id.editText_id_card_number);
        etBankAccount = $(R.id.editText_bank_account);
        etBankAddress = $(R.id.editText_bank_address);

        btnSubmit = $(R.id.btn_submit_2_binding);

        new SomeMonitorEditText(btnSubmit, etBankDepositName, etPayeeName, etIdCardNumber, etBankAccount, etBankAddress);
    }

    @Override
    public void setListener() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit_2_binding) {
            String bankDepositName = etBankDepositName.getText().toString();
            String payeeName = etPayeeName.getText().toString();
            String idCardNumber = etIdCardNumber.getText().toString();
            String bankAccount = etBankAccount.getText().toString();
            String bankAddress = etBankAddress.getText().toString();
            reqBindingBankcard(bankDepositName, payeeName, idCardNumber, bankAccount, bankAddress);
        }
    }


    /**
     * 绑定银行卡
     */
    private void reqBindingBankcard(String bankDepositName, String payeeName, String idCardNumber, String bankAccount, String bankAddress) {
        RxHttp.postForm(Consts.BANK_CREATE_API)
                .add(Consts.BANK_NAME, bankDepositName)
                .add(Consts.CARDHOLDER_NAME, payeeName)
                .add(Consts.ID_CARD_NUMBER, idCardNumber)
                .add(Consts.BANK_ACCOUNT, bankAccount)
                .add(Consts.BANK_ADDRESS, bankAddress)
                .asResponse(String.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> TipDialog.show(BankCardBindActivity.this, R.string.txt_binding_success, TipDialog.TYPE.SUCCESS)
                        .setOnDismissListener(() -> {
                            Intent intent = new Intent();
                            intent.putExtra(Common.BANKCARD_NO, bankAccount);
                            setResult(RESULT_OK);
                            finish();
                        }), (OnError) error -> TipDialog.show(BankCardBindActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

}
