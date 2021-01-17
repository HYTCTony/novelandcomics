package com.huli.foxread.ui.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.huli.foxread.R;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.contact.Url;
import com.huli.foxread.handlers.EncodingHandler;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.JsWebView;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 邀请好友---Web
 */
public class InviteFriendsActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private ConstraintLayout ctlShare;
    private TextView btnExplain;
    private JsWebView mWebview;

    private TextView btnInviteWx, btnInviteMoments, btnInviteFace2Face;

    //我的邀请码
    private String inviteCode;

    /*要分享的二维码图片*/
    private Bitmap bmpShare;

    //获取剪贴板管理器：
    private ClipboardManager cm;

    private boolean isInit = true;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.white), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
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
        return R.layout.activity_invite_friends;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_invite_friends);

        ctlShare = $(R.id.ctl_share);
        btnExplain = $(R.id.tv_asBtn_explain);
        mWebview = $(R.id.webView_invite_friend);

        btnInviteWx = $(R.id.tv_invite_way_wechat);
        btnInviteMoments = $(R.id.tv_invite_way_moments);
        btnInviteFace2Face = $(R.id.tv_invite_way_face2face);
    }

    @Override
    public void setListener() {
        btnInviteWx.setOnClickListener(this);
        btnInviteMoments.setOnClickListener(this);
        btnInviteFace2Face.setOnClickListener(this);

        btnExplain.setOnClickListener(this);
        mWebview.setOnWebViewListener(new JsWebView.onWebViewListener() {
            @Override
            public void onProgressChange(WebView view, int newProgress) {
                if (newProgress == 100) {
                    ctlShare.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTitleReceived(WebView view, String title) {

            }
        });

        MessageDialog.build(this).setStyle(DialogSettings.STYLE.STYLE_MATERIAL);
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity.start4Result(this, LoginActivity.REQCODE_LOGIN);
            return;
        }
        inviteCode = UserInfoCache.getDistribution(mContext);
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        regHandler();


        String mToken = TokenCache.getToken(this);
        String url = Url.baseurl + "/api/share?token=" + mToken;
        mWebview.loadUrl(url);
//        mWebview.loadUrl("file:///android_asset/demo.html");

    }

    /**
     * js call java
     */
    private void regHandler() {
        mWebview.registerHandler("doShareByBoard", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                doShare();
//                Log.e(TAG, "handler = submitFromWeb, data from web = " + data);
//                function.onCallBack("submitFromWeb exe, response data from Java");
            }
        });

        mWebview.registerHandler("copyInviteCode", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                copyInviteCode();
//                Log.e(TAG, "handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data from Java");
            }
        });

        mWebview.registerHandler("go2Withdrawal", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
//                Log.e(TAG, "去提现 = " + data);

                Intent intent = new Intent(InviteFriendsActivity.this, WithdrawalActivity.class);
                startActivity(intent);

                function.onCallBack("submitFromWeb exe, response data from Java");
            }
        });

        mWebview.registerHandler("viewInvitedFriends", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                startActivity(new Intent(InviteFriendsActivity.this, MyInviteFriendsActivity.class));
//                Log.e(TAG, "查看看好友 = " + data);
                function.onCallBack("submitFromWeb exe, response data from Java");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_asBtn_explain:
                Intent intent = new Intent(this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.INVITE_FRIENDS_EXPLAIN_URL);
                startActivity(intent);
                break;
            case R.id.tv_invite_way_wechat:
                //微信分享
                doShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.tv_invite_way_moments:
                //朋友圈分享
                doShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.tv_invite_way_face2face:
                //面对面分享
                CustomDialog.show(this, R.layout.layout_custom_dialog_invite_qrcode, (dialog, v) -> {
                    v.findViewById(R.id.iv_asBtn_close).setOnClickListener(view1 -> dialog.doDismiss());
                    ImageView ivQrCode = v.findViewById(R.id.iv_invite_qr_code);
                    bmpShare = createQrCode(Consts.DOWNLOAD_URL + inviteCode);
                    ivQrCode.setImageBitmap(bmpShare);
                    ivQrCode.setOnLongClickListener(v1 -> {
                        externalStorageTask();
                        dialog.doDismiss();
                        return false;
                    });
                });
                break;
            default:
                break;
        }
    }

    private void copyInviteCode() {
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("my_invite_code", inviteCode);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Tos.showShort(this, R.string.tips_copy_success);
    }

    /**
     * 图片分享调起分享面板
     */
    private void doShareImage() {
        if (bmpShare == null) {
            return;
        }
        UMImage umImage = new UMImage(this, bmpShare);
        new ShareAction(this).withMedia(umImage)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setCallback(umShareListener)
                .open();
    }

    /**
     * 调起分享面板
     */
    private void doShare() {
        UMWeb umWeb = new UMWeb(Consts.DOWNLOAD_URL + inviteCode, getString(R.string.txt_share_title), getString(R.string.txt_share_content), null);
        new ShareAction(this).withMedia(umWeb)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setCallback(umShareListener)
                .open();
    }

    /**
     * 指定平台分享
     */
    private void doShare(SHARE_MEDIA platform) {
        UMWeb umWeb = new UMWeb(Consts.DOWNLOAD_URL + inviteCode, getString(R.string.txt_share_title), getString(R.string.txt_share_content), null);
        new ShareAction(this)
                .setPlatform(platform)//传入平台
                .withMedia(umWeb)
                .setCallback(umShareListener)//回调监听器
                .share();
    }


    /**
     * 分享回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(InviteFriendsActivity.this, "分享成功！", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(InviteFriendsActivity.this, "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(InviteFriendsActivity.this, "分享取消了...", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    /*获取读写内存权限*/
    private static final int RC_WRITE_EXTERNAL_STORAGE_PERM = 0x722;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//    private static final String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean hasExternalStoragepermissions() {
        return EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 调起分享面板
     */
    @AfterPermissionGranted(RC_WRITE_EXTERNAL_STORAGE_PERM)
    private void externalStorageTask() {
        if (hasExternalStoragepermissions()) {
            doShareImage();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.rationale_write_external_storage),
                    RC_WRITE_EXTERNAL_STORAGE_PERM,
                    WRITE_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /**
     * 生成二维码
     */
    private Bitmap createQrCode(String inviteUrl) {
        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.mipmap.app_huli_logo);
        Bitmap logoBorder = getRoundedCornerBorderBitmap(this, resource);
        return EncodingHandler.createQRImage(inviteUrl, logoBorder, 512);
    }

    /**
     * 圆角白边图片
     *
     * @param bitmap
     * @return
     */
    private static Bitmap getRoundedCornerBorderBitmap(Context context, Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int mBorderColor = Color.WHITE;
        float mBorderWidth = (float) DensityUtils.dp2px(context, 4);
        float mCornerRadius = (float) DensityUtils.dp2px(context, 2);

        Paint mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

//        Matrix mMatrix = new Matrix();
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        BitmapShader mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        float scale = 1.0f;

        // shader的变换矩阵，我们这里主要用于放大或者缩小
//        mMatrix.preScale(scale, scale);
//        mBitmapShader.setLocalMatrix(mMatrix);
//        // 设置变换矩阵
//        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);


        Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        RectF mRoundRect = new RectF(mBorderWidth / 2, mBorderWidth / 2, w - mBorderWidth / 2, h - mBorderWidth / 2);

        Path mRoundPath = new Path();
        mRoundPath.reset();
        mRoundPath.addRoundRect(mRoundRect,
                new float[]{mCornerRadius, mCornerRadius,
                        mCornerRadius, mCornerRadius,
                        mCornerRadius, mCornerRadius,
                        mCornerRadius, mCornerRadius},
                Path.Direction.CW);
        //绘制描边(其实是圆角矩形)
        canvas.drawPath(mRoundPath, mBorderPaint);

        canvas.drawPath(mRoundPath, mBitmapPaint);

        return output;
    }
}
