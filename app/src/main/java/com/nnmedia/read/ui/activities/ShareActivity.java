package com.nnmedia.read.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.ui.base.BaseViewActivity;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.ShareBean;
import com.nnmedia.read.handlers.EncodingHandler;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.rxjava.rxlife.RxLife;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintTypedArray;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.OnClick;

public class ShareActivity extends BaseViewActivity {

    @BindView(R.id.iv_qr_code)
    AppCompatImageView ivQrCode;
    @BindView(R.id.tv_invitation)
    AppCompatTextView tvInvitation;
    @BindView(R.id.tv_code_number)
    AppCompatTextView tvCodeNumber;

    private SimpleDateFormat df;
    String url;
    String pic;
    String code;

    public static void start(Context context) {
        Intent starter = new Intent(context, ShareActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void initView() {
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity2.start4Result(this, LoginActivity2.REQCODE_LOGIN);
            return;
        }

        StatusBarUtils.setTransparentForImageView(this, toolbar);

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        reqShareMsg();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_share;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        setTitle("分享");
        super.initToolbar(toolbar);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this,
                null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        Drawable drawable = a.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
        drawable.setColorFilter(white, PorterDuff.Mode.MULTIPLY);
        toolbar.setTitleTextColor(white);
        toolbar.setSubtitleTextColor(white);
        toolbar.setNavigationIcon(drawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exchange:
                GoldExchangeVipActivity.start(ShareActivity.this, 3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.btn_copy_code, R.id.btn_save_image, R.id.tv_invitation})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_copy_code:
                if (url == null || url.isEmpty()) {
                    TipDialog.show(ShareActivity.this, "无法获取到地址，请刷新页面后重试。", TipDialog.TYPE.ERROR);
                    return;
                }
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("share", url);
                    cm.setPrimaryClip(mClipData);
                    TipDialog.show(ShareActivity.this, "拷贝成功", TipDialog.TYPE.SUCCESS);
                }
                break;
            case R.id.btn_save_image:
                Log.e("图片处理", "图片正在处理");
                if (pic == null || pic.isEmpty()) {
                    TipDialog.show(ShareActivity.this, "无法获取到地址，请刷新页面后重试。", TipDialog.TYPE.ERROR);
                    return;
                }
                Glide.with(mContext).asBitmap().load(pic).override(1080, 1920).diskCacheStrategy(DiskCacheStrategy.ALL).into(simpleTarget);
                break;
            case R.id.tv_invitation:
                startActivity(new Intent(ShareActivity.this, MyInviteFriendsActivity.class));
                break;
        }
    }

    SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                Bitmap qrBitmap = barcodeEncoder.encodeBitmap(url, BarcodeFormat.QR_CODE, 480, 480);
            Bitmap qrBitmap = createQrCode(url, 380);
            Bitmap alertBitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), resource.getConfig());
            Canvas canvas = new Canvas(alertBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            int x1 = (resource.getWidth() - qrBitmap.getWidth()) / 2;
            int y1 = (resource.getHeight() - qrBitmap.getHeight()) / 2;
            int x2 = resource.getWidth() / 2;
            int y2 = (resource.getHeight() - qrBitmap.getHeight()) / 2 + qrBitmap.getHeight() + 60;
            canvas.drawBitmap(resource, new Matrix(), paint);
            canvas.drawBitmap(qrBitmap, x1, y1, paint);
            paint.setFakeBoldText(true);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("邀请码：" + code, x2, y2, paint);
            shareImage(alertBitmap);
            Log.e("图片处理", "图片处理完成");
        }
    };

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

    private void shareImage(Bitmap bitmap) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            TipDialog.show(ShareActivity.this, "未插入sd卡", TipDialog.TYPE.SUCCESS);
            return;
        }
        File file = BitmapToFile(bitmap);
        if (file == null || !file.exists()) {
            TipDialog.show(ShareActivity.this, "图片文件不存在", TipDialog.TYPE.SUCCESS);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    getApplicationContext().getPackageName() + ".fileProvider", file);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "分享图片"));
    }

    private File BitmapToFile(Bitmap bitmap) {
        if (bitmap == null) return null;
//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                .getAbsolutePath();
        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
        String imageName = "Share-" + df.format(new Date().getTime()) + ".jpg";
        File imageFile = new File(path, imageName);

        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 下面的步骤必须有，不然在相册里找不到图片，若不需要让用户知道你保存了图片，可以不写下面的代码。
        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    imageFile.getAbsolutePath(), imageName, null);
            TipDialog.show(ShareActivity.this, "保存成功，请您到 相册/图库 中查看", TipDialog.TYPE.SUCCESS);
        } catch (FileNotFoundException e) {
            TipDialog.show(ShareActivity.this, "保存失败", TipDialog.TYPE.SUCCESS);
            e.printStackTrace();
        }
        // 最后通知图库更新
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(imageFile.getPath()))));
        return imageFile;
    }

    /**
     * 生成二维码
     */
    private Bitmap createQrCode(String inviteUrl, int weight) {
        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.mipmap.app_logo);
        Bitmap logoBorder = getRoundedCornerBorderBitmap(mContext, resource);
        return EncodingHandler.createQRImage(inviteUrl, logoBorder, weight);
    }

    private void getQrCode(ShareBean data) {
        url = data.getUrl();
        pic = data.getPic();
        code = data.getDistribution();
        tvInvitation.setText("已成功邀请" + data.getInvitation_sum() + "人");
        tvCodeNumber.setText("您的推广码：" + data.getDistribution());
        try {
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            Bitmap bitmap = barcodeEncoder.encodeBitmap(url, BarcodeFormat.QR_CODE, 600, 600);
            Bitmap bitmap = createQrCode(url, 600);
            Glide.with(mContext).load(bitmap).into(ivQrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享页面信息
     */
    private void reqShareMsg() {
        RxHttp.get(Consts.SHARE_INDEX_API)
                .asResponse(ShareBean.class)
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    getQrCode(entity);
                }, (OnError) error -> {
                    TipDialog.show(ShareActivity.this, "页面信息错误，正在退出...", TipDialog.TYPE.ERROR)
                            .setOnDismissListener(() -> {
                                finish();
                            });
                });
    }
}