package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.CapitalEntity;
import com.nnmedia.read.entity.WithdrawalOptionEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.rxhttp.Tip;
import com.nnmedia.read.ui.adapters.WithdrawalGoldAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.decoration.GridSpacingItemDecoration;
import com.nnmedia.read.utils.DateTimeUtil;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.rxjava.rxlife.RxLife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 金币提现
 */
public class WithdrawalActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {
    private static final int REQCODE_BIND_BANKCARD = 0x1999;

    private TextView tvGoldBalance, tvExchangeYuan;
    private TextView btnRecord;
    private TextView btnGo2Invite;

    private RecyclerView recyclerView;
    private WithdrawalGoldAdapter mAdapter;

    private TextView tvWithdrawalTips;

    /*提现套餐ID*/
    private String planID;
    /*提现申请提示框title*/
    private String msgTitle;

    /*我的金币余额*/
    private int goldCoinBalance = 0;

    private boolean hasQuery;       //已经查询用户资产信息
    private String myBankCardNo;

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
        return R.layout.activity_gold_coin_withdrawal;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_gold_coin_withdrawal);
        StatusBarUtils.offsetView(this, toolbar);

        tvGoldBalance = $(R.id.tv_my_gold_coin_balance);
        tvExchangeYuan = $(R.id.tv_my_gold_coin_balance_exchange_yuan);
        btnRecord = $(R.id.tv_asBtn_withdrawal_record);
        btnGo2Invite = $(R.id.btn_invite_friends_2_make_money);

        recyclerView = $(R.id.recyclerView_withdrawal);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new WithdrawalGoldAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.layout_rv_head_withdrawal_title, recyclerView, false);
        View footView = inflater.inflate(R.layout.layout_rv_footer_withdrawal, recyclerView, false);
        mAdapter.addHeaderView(headView);
        mAdapter.addFooterView(footView);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtils.dp2px(this, 16), true, 1));
        tvWithdrawalTips = footView.findViewById(R.id.tv_withdrawal_tips);

    }

    @Override
    public void setListener() {
        btnRecord.setOnClickListener(this);
        btnGo2Invite.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity2.start4Result(this, LoginActivity2.REQCODE_LOGIN);
            return;
        }


//        reqMyCapitalDetail();
//
//        reqWithdrawalCombo();
    }


    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_asBtn_withdrawal_record:
//                startActivity(new Intent(WithdrawalActivity.this, WithdrawalRecordActivity.class));
                break;

            case R.id.btn_invite_friends_2_make_money:
//                startActivity(new Intent(this, InviteFriendsActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//        WithdrawalOptionEntity data = (WithdrawalOptionEntity) adapter.getData().get(position);
//        if (data.getNeed_score() < goldCoinBalance) {
//            MessageDialog.show(this, "余额不足", "余额不足，先去做任务赚金币吧！", "做任务", "取消")
//                    .setButtonPositiveTextInfo(new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.col_red_fc4545)))
//                    .setOnOkButtonClickListener((baseDialog, v) -> {
//                        startActivity(new Intent(WithdrawalActivity.this, MainActivity.class)
//                                .putExtra(Common.WITHDRAWAL_DO_TASKS, true));
//                        return false;
//                    });
//        } else {
//            planID = data.getId();
//            msgTitle = data.getTitle();
//            if (!TextUtils.isEmpty(myBankCardNo)) {
//                showWithdrawalDialog(planID, msgTitle);
//            } else {
//                if (hasQuery) {
//                    MessageDialog.show(this, "无提现账号", "请完善账户信息！" + myBankCardNo, "前往", "稍后再填")
//                            .setButtonPositiveTextInfo(new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.col_red_fc4545)))
//                            .setOnOkButtonClickListener((baseDialog, v) -> {
//                                startActivityForResult(new Intent(WithdrawalActivity.this, BankCardBindActivity.class), REQCODE_BIND_BANKCARD);
//                                return false;
//                            });
//                } else {
//                    Tip.show("正在查询您的提现账号，请稍候...");
//                }
//            }
//        }
    }

    /**
     * 提现确认提示
     *
     * @param planID   体现套餐ID
     * @param msgTitle 提示标题
     */
    private void showWithdrawalDialog(String planID, String msgTitle) {
        MessageDialog.show(this, msgTitle, "申请提现到银行卡账户：" + myBankCardNo, "立即申请", "取消")
                .setButtonPositiveTextInfo(new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.col_red_fc4545)))
                .setOnOkButtonClickListener((baseDialog, v) -> {
                    reqGoldWithdrawal(planID);
                    return false;
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQCODE_BIND_BANKCARD) {
                if (data != null) {
                    myBankCardNo = data.getStringExtra(Common.BANKCARD_NO);
                    if (!TextUtils.isEmpty(planID) && !TextUtils.isEmpty(myBankCardNo)) {
//                        showWithdrawalDialog(planID, msgTitle);
                    }
                }
            }
        }
    }

    /**
     * 金币提现套餐
     */
    private void reqWithdrawalCombo() {
        RxHttp.postForm(Consts.WITHDRAWAL_MENU_API)
                .asResponseList(WithdrawalOptionEntity.class)
//                .doOnSubscribe(disposable -> showLoadingDialog())
//                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(list -> mAdapter.setList(list));
    }

    /**
     * 金币提现
     *
     * @param planID 提现套餐ID
     */
    private void reqGoldWithdrawal(String planID) {
        RxHttp.postForm(Consts.WITHDRAWAL_SCORE_API)
                .add(Consts.WITHDRAWAL_PLAN_ID, planID)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    MessageDialog.show(WithdrawalActivity.this, getString(R.string.txt_withdrawal_success_title),
                            DateTimeUtil.getCurrentDate(), getString(R.string.txt_got_it))
                            .setCustomView(R.layout.dialog_withdrawal_success, (dialog, v) -> {
                            });
                    reqMyCapitalDetail();
                }, (OnError) error -> {
                    if (error.getErrorCode() == 10008) {
                        Tip.show(error.getErrorMsg());
                        startActivityForResult(new Intent(WithdrawalActivity.this, BankCardBindActivity.class), REQCODE_BIND_BANKCARD);
                    } else {
                        TipDialog.show(WithdrawalActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR);
                    }
                });
    }

    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        RxHttp.get(Consts.USER_CAPITAL_API)
                .asResponse(CapitalEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    hasQuery = true;
                    myBankCardNo = entity.getAccount();
                    goldCoinBalance = entity.getScore();
                    tvGoldBalance.setText(String.valueOf(goldCoinBalance));

                    int exchangeMoney;       //金币余额转换RMB
                    try {
                        exchangeMoney = goldCoinBalance / entity.getProportion();
                    } catch (Exception e) {
                        exchangeMoney = 0;
                    }
//                    DecimalFormat df = new DecimalFormat("约" + "#######.##" + getString(R.string.unit_yuan));
                    tvExchangeYuan.setText((exchangeMoney + getString(R.string.unit_yuan)));
                });
    }
}
