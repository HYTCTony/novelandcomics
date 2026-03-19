package com.nnmedia.read.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.utils.FileUtils;
import com.nnmedia.read.cache.AccountCache;
import com.nnmedia.read.cache.TokenCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.LoginRpsEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.AESCBCUtil;
import com.nnmedia.read.utils.QRCodeParseUtils;
import com.nnmedia.read.utils.SPFUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.nnmedia.read.utils.UniqueIdManager;
import com.nnmedia.read.utils.VerifyDevice;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class ScanCodeActivity extends BaseActivity implements DecoratedBarcodeView.TorchListener, EasyPermissions.PermissionCallbacks {
    Toolbar toolbar;
    DecoratedBarcodeView scanner;
    private BeepManager mBeepManager;
    private boolean isPost = false;

    public static void start(Context context) {
        Intent starter = new Intent(context, ScanCodeActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_scan_code;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setTransparentForImageView(this, toolbar);
    }

    @Override
    public void initView(View view) {

        toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, "扫描二维码登录牛牛小说");

        scanner = $(R.id.scanner);

        scanner.setTorchListener(this);
        scanner.decodeContinuous(mCallback);
        scanner.setStatusText("请将二维码对准相框");
        mBeepManager = new BeepManager(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_choise_image, menu);
        return true;
    }

    private final int REQUEST_CODE_SACN_GALLERY = 1001;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_image:
                requestPhoneStatePermission();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.pause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SACN_GALLERY:
                    if (data != null) {
                        Uri uri = data.getData();
                        parsePhoto(FileUtils.getUriPath(this, uri));
                    } else {
                        Toast.makeText(this, getString(R.string.txt_error_pic), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 启动线程解析二维码图片
     *
     * @param path
     */
    private void parsePhoto(String path) {
        //启动线程完成图片扫码
        new QrCodeAsyncTask(this, path).execute(path);
    }

    /**
     * AsyncTask 静态内部类，防止内存泄漏
     */
    static class QrCodeAsyncTask extends AsyncTask<String, Integer, String> {
        private WeakReference<Activity> mWeakReference;
        private String path;

        public QrCodeAsyncTask(Activity activity, String path) {
            mWeakReference = new WeakReference<>(activity);
            this.path = path;
        }

        @Override
        protected String doInBackground(String... strings) {
            // 解析二维码/条码
            return QRCodeParseUtils.syncDecodeQRCode(path);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //识别出图片二维码/条码，内容为s
            ScanCodeActivity activity = (ScanCodeActivity) mWeakReference.get();
            if (activity != null) {
                activity.handleQrCode(s);
            }
        }
    }

    /**
     * 处理图片二维码解析的数据
     *
     * @param resultText
     */
    public void handleQrCode(String resultText) {
        if (null == resultText) {
            Toast.makeText(this, getString(R.string.txt_error_pic), Toast.LENGTH_SHORT).show();
        } else {
            handleResult(resultText);
        }
    }

    private void handleResult(String resultText) {
        Log.e("resultText", "resultText==" + resultText);
        isPost = true;
        if (resultText.startsWith(Consts.TIP)) {
            String result = resultText.replace(Consts.TIP, "");
            Log.e("result", "result==" + result);
            String account = AESCBCUtil.decrypt(result, AESCBCUtil.key, AESCBCUtil.iv);
            if (account == null || account.isEmpty()) {
                TipDialog.show(ScanCodeActivity.this, getString(R.string.txt_error_qr), TipDialog.TYPE.ERROR);
                return;
            }
            String[] ac = account.split("&");
            if (ac[0].isEmpty() || ac[1].isEmpty()) {
                TipDialog.show(ScanCodeActivity.this, getString(R.string.txt_error_qr), TipDialog.TYPE.ERROR);
                return;
            }
            loginPhone(ac[0], ac[1]);
        } else {
            jumpOutSide(resultText);
        }
    }


    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    private BarcodeCallback mCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String resultText = result.getText();
            if (TextUtils.isEmpty(resultText)) {
                Snackbar.make(toolbar, "空二维码！", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!isPost)
                getStatu(resultText);
            mBeepManager.playBeepSoundAndVibrate();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    private void getStatu(String resultText) {
        Log.e("resultText", "resultText==" + resultText);
        isPost = true;
        if (resultText.startsWith(Consts.TIP)) {
            String result = resultText.replace(Consts.TIP, "");
            Log.e("result", "result==" + result);
            String account = AESCBCUtil.decrypt(result, AESCBCUtil.key, AESCBCUtil.iv);
            if (account == null || account.isEmpty()) {
                TipDialog.show(ScanCodeActivity.this, getString(R.string.txt_error_qr), TipDialog.TYPE.ERROR);
                return;
            }
            String[] ac = account.split("&");
            if (ac[0].isEmpty() || ac[1].isEmpty()) {
                TipDialog.show(ScanCodeActivity.this, getString(R.string.txt_error_qr), TipDialog.TYPE.ERROR);
                return;
            }
            loginPhone(ac[0], ac[1]);
        } else {
            jumpOutSide(resultText);
        }
    }

    /**
     * 账号密码登录
     *
     * @param tel      手机号
     * @param password 密码
     */
    private void loginPhone(String tel, String password) {
        WaitDialog.show(ScanCodeActivity.this, "请稍后！");
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USER_PASSWORD_LOGIN_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.PASSWORD, password)
                .add(Consts.UNIQUE_ID, uniqueID)
                .add(Consts.SIMULATOR, VerifyDevice.verify() ? 1 : 0)
                .asResponse(LoginRpsEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(loginRpsEntity -> {
                    SPFUtils.put(ScanCodeActivity.this, Common.KEY_FIRST_LOGIN, true);
                    TokenCache.saveToken(ScanCodeActivity.this, loginRpsEntity.getToken());
                    String account = AESCBCUtil.encrypt(tel + "&" + password, AESCBCUtil.key, AESCBCUtil.iv);
                    AccountCache.saveAccout(ScanCodeActivity.this, account);
                    reqUserInfo();
                }, (OnError) error -> {
                    TipDialog.show(ScanCodeActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR)
                            .setOnDismissListener(() -> {
                                finish();
                            });
                });
    }

    /**
     * 获取用户信息
     */
    private void reqUserInfo() {
        WaitDialog.dismiss();
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USERS_INFO_API)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(FUser.class)
                .to(RxLife.toMain(this))
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(ScanCodeActivity.this, fUser);
                    EventBus.getDefault().postSticky(fUser);
                    TipDialog.show(ScanCodeActivity.this, R.string.txt_login_success, TipDialog.TYPE.SUCCESS)
                            .setOnDismissListener(() -> {
                                setResult(RESULT_OK);
                                finish();
                            });

                }, (OnError) error -> {
                    MessageDialog.show(ScanCodeActivity.this, "", "登录失败是否重试", "重试", "退出")
                            .setCancelable(true)
                            .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                                @Override
                                public boolean onClick(BaseDialog baseDialog, View v) {
                                    baseDialog.doDismiss();
                                    finish();
                                    return false;
                                }
                            })
                            .setOnOkButtonClickListener((baseDialog, v) -> {
                                reqUserInfo();
                                return false;
                            });
                });
    }

    private void jumpOutSide(String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(ScanCodeActivity.this.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(ScanCodeActivity.this.getPackageManager());
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
            finish();
        } else {
            TipDialog.show(ScanCodeActivity.this, "无效链接，请重新尝试！", TipDialog.TYPE.ERROR);
        }
    }

    private static final int RC_PHONE_STATE_PERM = 101;

    @AfterPermissionGranted(RC_PHONE_STATE_PERM)
    private void requestPhoneStatePermission() {
        List<String> list = new ArrayList<>();
        if (!EasyPermissions.hasPermissions(ScanCodeActivity.this, Manifest.permission.READ_PHONE_STATE)) {
            list.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!EasyPermissions.hasPermissions(ScanCodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!EasyPermissions.hasPermissions(ScanCodeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        int size = list.size();
        if (size > 0) {
            String[] perms = list.toArray(new String[size]);
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, RC_PHONE_STATE_PERM, perms).setRationale
                    ("保存二维码需要相册权限，点击确定授予权限。").setNegativeButtonText("取消").setPositiveButtonText
                    ("确定").build());
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SACN_GALLERY);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}