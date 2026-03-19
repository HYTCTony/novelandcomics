package com.nnmedia.read.wxapi;

//import com.umeng.socialize.weixin.view.WXCallbackActivity;

//public class WXEntryActivity extends WXCallbackActivity {

    /* IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, FrApp.WECHAT_APP_ID);
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                iwxapi.sendReq(req);*/
   /* private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pay_success);

        api = WXAPIFactory.createWXAPI(this, FrApp.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        hintKeyboard();
        //如果没回调onResp，八成是这句没有写
        api.handleIntent(getIntent(), this);
    }

    private void hintKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_BAN:
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:

                if (RETURN_MSG_TYPE_SHARE == baseResp.getType()) {
                }
                else {
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (baseResp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //这里 拿到了微信返回的code
                        String code = ((SendAuth.Resp) baseResp).code;
                        Log.e("sssssss", "code===" + code);
                        break;

                    case RETURN_MSG_TYPE_SHARE:

                        break;
                }
                break;
        }
    }

    //该方法执行umeng登陆的回调的处理
    @Override
    public void a(com.umeng.weixin.umengwx.b b) {
//        super.a(b);
    }

    @Override
    protected void a(Intent intent) {
        super.a(intent);
    }

    //在onResume中处理从微信授权通过以后不会自动跳转的问题，手动结束该页面
    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }*/
//}
