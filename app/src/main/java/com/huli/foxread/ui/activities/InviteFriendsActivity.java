package com.huli.foxread.ui.activities;

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
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache2;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.InviteFriendsPageBean;
import com.huli.foxread.entity.InviteRewardBean;
import com.huli.foxread.handlers.EncodingHandler;
import com.huli.foxread.ui.adapters.InviteRewardAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InviteFriendsActivity extends BaseActivity implements View.OnClickListener {

    private TextView btnExplain;
    private TextView tvInviteCode;
    private Button btnCopy;
    private Button btnImmediatelyInvite;

    private TextView tvInvitedNum, tvMyMoney;
    private TextView btnGo2Check, btnGo2Withdrawal;

    private RecyclerView recyclerView;

    private TextView btnInviteWx, btnInviteMoments, btnInviteFace2Face;

    //我的现金余额
    private double myMoney;
    //我的邀请码
    private String inviteCode;

    //获取剪贴板管理器：
    private ClipboardManager cm;

    private boolean isInit = true;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.transparent), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, false);
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
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_invite_friends);
        StatusBarUtils.offsetView(this, toolbar);

        btnExplain = $(R.id.tv_asBtn_explain);
        tvInviteCode = $(R.id.tv_my_invite_code);
        btnCopy = $(R.id.btn_copy);
        btnImmediatelyInvite = $(R.id.btn_immediately_invite);

        btnInviteWx = $(R.id.tv_invite_way_wechat);
        btnInviteMoments = $(R.id.tv_invite_way_moments);
        btnInviteFace2Face = $(R.id.tv_invite_way_face2face);

        tvInvitedNum = $(R.id.tv_has_invited_friends_num);
        tvMyMoney = $(R.id.tv_has_made_money);
        recyclerView = $(R.id.recyclerView_invite_reward);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, DensityUtils.dp2px(this, 16)));

        btnGo2Check = $(R.id.tv_has_invited_friends_num_go2_check);
        btnGo2Withdrawal = $(R.id.tv_has_made_money_go2_withdrawal);
        initBtnCheckItf();
        initBtnWithdrawal();
    }

    @Override
    public void setListener() {
        btnExplain.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        btnImmediatelyInvite.setOnClickListener(this);

        btnInviteWx.setOnClickListener(this);
        btnInviteMoments.setOnClickListener(this);
        btnInviteFace2Face.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录
        if (UserInfoCache2.getIsVisitor(mContext)) {
            LoginActivity.start4Result(this, LoginActivity.REQCODE_LOGIN);
            return;
        }

        inviteCode = UserInfoCache2.getDistribution(mContext);
        tvInviteCode.setText(inviteCode);

        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reqInviteFriendsInfo(isInit);
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
            case R.id.btn_copy:
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("my_invite_code", inviteCode);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Tos.showShort(this, R.string.tips_copy_success);
                break;
            case R.id.btn_immediately_invite:
                //TODO 立即邀请 --- 弹出分享集成面板
                CustomDialog.show(this, R.layout.layout_custom_dialog_invite_qrcode, (dialog, v) -> {
                    v.findViewById(R.id.iv_asBtn_close).setOnClickListener(view1 -> dialog.doDismiss());
                    ImageView ivQrCode = v.findViewById(R.id.iv_invite_qr_code);

                    displayQrCode(Consts.DOWNLOAD_URL, ivQrCode);
                });
            case R.id.tv_invite_way_wechat:
                //TODO 微信分享
                break;
            case R.id.tv_invite_way_moments:
                //TODO 朋友圈分享
                break;
            case R.id.tv_invite_way_face2face:
                //面对面分享
                CustomDialog.show(this, R.layout.layout_custom_dialog_invite_qrcode, (dialog, v) -> {
                    v.findViewById(R.id.iv_asBtn_close).setOnClickListener(view1 -> dialog.doDismiss());
                    ImageView ivQrCode = v.findViewById(R.id.iv_invite_qr_code);

                    displayQrCode(Consts.DOWNLOAD_URL, ivQrCode);
                });
                break;
            default:
                break;
        }
    }

    /**
     * 查看已邀请的好友
     */
    private void initBtnCheckItf() {
        String itfCheckStr = getString(R.string.txt_itf_go2_check);
        SpannableString spannableString = new SpannableString(itfCheckStr);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(InviteFriendsActivity.this, MyInviteFriendsActivity.class));
            }

            //去除连接下划线
            @Override
            public void updateDrawState(TextPaint ds) {
                /**set textColor**/
//                ds.setColor(ds.linkColor);
                ds.setColor(ContextCompat.getColor(InviteFriendsActivity.this, R.color.txt_red));
                /**Remove the underline**/
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, itfCheckStr.length() - 3, itfCheckStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        btnGo2Check.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        btnGo2Check.setText(spannableString);
    }

    /**
     * 现金提现
     */
    private void initBtnWithdrawal() {
        String withdrawalStr = getString(R.string.txt_made_money_go2_withdrawal);
        SpannableString spannableString = new SpannableString(withdrawalStr);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(InviteFriendsActivity.this, WithdrawalRMBActivity.class);
                intent.putExtra(Common.EXTRA_KEY_MONEY, myMoney);
                startActivity(intent);
            }

            //去除连接下划线
            @Override
            public void updateDrawState(TextPaint ds) {
                /**set textColor**/
                ds.setColor(ContextCompat.getColor(InviteFriendsActivity.this, R.color.txt_red));
                /**Remove the underline**/
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, withdrawalStr.length() - 3, withdrawalStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        btnGo2Withdrawal.setMovementMethod(LinkMovementMethod.getInstance());   //不设置 没有点击事件
        btnGo2Withdrawal.setText(spannableString);
    }


    /**
     * 邀请好友页面信息
     */
    private void reqInviteFriendsInfo(boolean showDialog) {
        isInit = false;
        OkGo.<String>get(Consts.WELFARE_INVITE_API)
                .execute(new LtbCallback(this, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<InviteFriendsPageBean> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<InviteFriendsPageBean>>() {
                                });
                        if (entity.error_code == 0) {
                            InviteFriendsPageBean data = entity.getData();
                            tvInvitedNum.setText(String.valueOf(data.getSum_man()));
                            myMoney = data.getMoney();
                            tvMyMoney.setText(new DecimalFormat("######0.00").format(myMoney));

                            List<InviteRewardBean> list = data.getList();
                            recyclerView.setAdapter(new InviteRewardAdapter(list));
                        } else {
                            TipDialog.show(InviteFriendsActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }


    /**
     * 显示二维码
     */
    private void displayQrCode(String invitUrl, ImageView imageView) {
        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.mipmap.app_huli_logo_round_small);
        Bitmap logoBorder = getRoundedCornerBorderBitmap(this, resource);
        Bitmap qrCodeBm = EncodingHandler.createQRImage(invitUrl, logoBorder, 512);
        imageView.setImageBitmap(qrCodeBm);
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
