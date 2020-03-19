package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.SomeMonitorEditText;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

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
     * 获取充值列表
     */
    private void reqBindingBankcard(String bankDepositName, String payeeName, String idCardNumber, String bankAccount, String bankAddress) {
        OkGo.<String>post(Consts.BANK_CREATE_API)
                .params(Consts.BANK_NAME, bankDepositName)
                .params(Consts.CARDHOLDER_NAME, payeeName)
                .params(Consts.ID_CARD_NUMBER, idCardNumber)
                .params(Consts.BANK_ACCOUNT, bankAccount)
                .params(Consts.BANK_ADDRESS, bankAddress)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            TipDialog.show(BankCardBindActivity.this, entity.msg, TipDialog.TYPE.SUCCESS)
                                    .setOnDismissListener(() -> {
                                        setResult(RESULT_OK);
                                        finish();
                                    });
                        } else {
                            TipDialog.show(BankCardBindActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

}
