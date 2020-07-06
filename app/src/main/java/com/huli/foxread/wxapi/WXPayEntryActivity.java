package com.huli.foxread.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.huli.foxread.FrApp;
import com.huli.foxread.ebsevent.WXPaySuccessEvent;
import com.huli.foxread.utils.Tos;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import androidx.appcompat.app.AppCompatActivity;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pay_success);

        api = WXAPIFactory.createWXAPI(this, FrApp.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        String result = "";

        EventBus.getDefault().post(new WXPaySuccessEvent(resp.errCode));
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:           //支付成功
//                Intent intent = new Intent(WXPayEntryActivity.this, Succ.class);
//                startActivity(intent);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "支付取消";
                Tos.showShort(this, result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "支付被拒绝";
                Tos.showShort(this, result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = "不支持";
                Tos.showShort(this, result);
                finish();
                break;
            default:
                result = "未知错误";
                Tos.showShort(this, result);
                finish();
                break;
        }
    }

}