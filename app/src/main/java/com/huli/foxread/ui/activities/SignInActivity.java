package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.SignDetailEntity;
import com.huli.foxread.entity.WelfareTaskEntity;
import com.huli.foxread.ui.adapters.WeekSignInStateAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.TaskProgressBar;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private TextView btnExplain;
    private TextView tvMyGoldCoin, tvTodayGoldCoin;

    private TextView tvTitleSignIn;
    private TextView tvSignInState;
    private TextView btnSignIn;
    private RecyclerView recyclerView;
    private WeekSignInStateAdapter mAdapter;
    private TextView tvMoreGoldCoin;
    private TaskProgressBar taskProgressBar;
    private TextView tvProgress;

   /* private TextView tvExtraCount1, tvExtraCount2, tvExtraCount3, tvExtraCount4, tvExtraCount5;
    private TextView tvExtraNeedDay1, tvExtraNeedDay2, tvExtraNeedDay3, tvExtraNeedDay4, tvExtraNeedDay5;*/

    private TextView tvGrpPeopleCount;
    private int signFlag = 0;                   //1是已签到
    private int getGb;                          //签到能拿的金币
    private int continuousSignInCount = 0;      //连续签到天数

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setStatusBarTextDark(this, false);
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
        return R.layout.activity_sign_in;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_my_signin);
        StatusBarUtils.setTranslucentForImageView(this, 0, toolbar);

        btnExplain = $(R.id.tv_asBtn_explain);
        tvMyGoldCoin = $(R.id.tv_my_gold_coin_count);
        tvTodayGoldCoin = $(R.id.tv_today_gold_coin_count);

        tvTitleSignIn = $(R.id.tv_title_sign_in);
        tvSignInState = $(R.id.tv_sign_in_state_this_week);
        btnSignIn = $(R.id.tv_asBtn_sign_in);
        recyclerView = $(R.id.recyclerView_sign_week);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(7, DensityUtils.dp2px(this, 10), false));
        mAdapter = new WeekSignInStateAdapter();
        recyclerView.setAdapter(mAdapter);

        tvMoreGoldCoin = $(R.id.tv_more_gold_coin_way);
        taskProgressBar = $(R.id.progressBar_sign_task);
        tvProgress = $(R.id.tv_progress_si);

        /*tvExtraCount1 = $(R.id.tv_extra_goldCoin_count_1);
        tvExtraCount2 = $(R.id.tv_extra_goldCoin_count_2);
        tvExtraCount3 = $(R.id.tv_extra_goldCoin_count_3);
        tvExtraCount4 = $(R.id.tv_extra_goldCoin_count_4);
        tvExtraCount5 = $(R.id.tv_extra_goldCoin_count_5);

        tvExtraNeedDay1 = $(R.id.tv_extra_goldCoin_need_day_1);
        tvExtraNeedDay2 = $(R.id.tv_extra_goldCoin_need_day_2);
        tvExtraNeedDay3 = $(R.id.tv_extra_goldCoin_need_day_3);
        tvExtraNeedDay4 = $(R.id.tv_extra_goldCoin_need_day_4);
        tvExtraNeedDay5 = $(R.id.tv_extra_goldCoin_need_day_5);
*/
        tvGrpPeopleCount = $(R.id.tv_get_redPacket_people_count);
    }

    @Override
    public void setListener() {
        btnExplain.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        tvMyGoldCoin.setText(String.valueOf(UserInfoCache.getScore(mContext)));
        tvTodayGoldCoin.setText(String.valueOf(UserInfoCache.getTodayScore(mContext)));

        /*taskProgressBar.setCurProgress(5, 200);
        taskProgressBar.setMaxProgress(10);
        tvProgress.setText("5/10");*/

        /*tvExtraCount1.setText("+300");
        tvExtraCount2.setText("+500");
        tvExtraCount3.setText("+1000");
        tvExtraCount4.setText("+2000");
        tvExtraCount5.setText("+4000");

        tvExtraNeedDay1.setText("7天");
        tvExtraNeedDay2.setText("14天");
        tvExtraNeedDay3.setText("30天");
        tvExtraNeedDay4.setText("45天");
        tvExtraNeedDay5.setText("60天");*/

        tvGrpPeopleCount.setText("0人已领");

        reqSignIn();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_explain:
                Intent intent = new Intent(this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.SIGN_IN_EXPLAIN_URL);
                startActivity(intent);
                break;
            case R.id.tv_asBtn_sign_in:
                if (signFlag != 1) {
                    reqSignIn();
                }
                break;

            default:
                break;
        }
    }


    /**
     * 获取签到详情
     */
    private void getSignInInfo() {
        OkGo.<String>get(Consts.WELFARE_SIGNIN_API)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<SignDetailEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<SignDetailEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            SignDetailEntity data = entity.getData();
                            signFlag = data.getFrequency();          //是否已签到标记
                            getGb = data.getReward();       //签到的奖励
                            continuousSignInCount = data.getSign_successions();

                            WelfareTaskEntity welfare = data.getWelfare();
                            String number = welfare.getNumber();
                            tvGrpPeopleCount.setText((number + getString(R.string.txt_people_already_receive)));
                            mAdapter.setNewData(data.getList());

                            if (signFlag == 1) {
                                mAdapter.setSignInChange(true);
                            }

                            btnSignIn.setEnabled(false);
                            btnSignIn.setTextColor(ContextCompat.getColor(SignInActivity.this, R.color.txt_red));
                            btnSignIn.setBackgroundResource(R.drawable.shape_btn_bg_semicircle_border_red);
                            btnSignIn.setText(String.format(getString(R.string.txt_continuous_sign_in_day_x), (continuousSignInCount + 1)));

                            SpannableString spanbs = new SpannableString(String.format(getString(R.string.txt_congratulations_get_gold_coin_x), getGb));
                            spanbs.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SignInActivity.this, R.color.txt_red)),
                                    spanbs.length() - 2 - String.valueOf(getGb).length() - 1,
                                    spanbs.length() - 2,
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            tvTitleSignIn.setText(spanbs);

                            setResult(RESULT_OK);
                        } else {
                            TipDialog.show(SignInActivity.this, entity.msg, TipDialog.TYPE.ERROR)
                                    .setOnDismissListener(() -> finish());
                        }
                    }
                });
    }

    /**
     * 签到
     */
    private void reqSignIn() {
        OkGo.<String>get(Consts.WELFARE_COMPLETESINGIN_API)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            signFlag = 1;
                            btnSignIn.setEnabled(false);
                           /* btnSignIn.setTextColor(ContextCompat.getColor(SignInActivity.this, R.color.txt_red));
                            btnSignIn.setBackgroundResource(R.drawable.shape_btn_bg_semicircle_border_red);
                            btnSignIn.setText(String.format(getString(R.string.txt_continuous_sign_in_day_x), (continuousSignInCount + 1)));

                            SpannableString spanbs = new SpannableString(String.format(getString(R.string.txt_congratulations_get_gold_coin_x), getGb));
                            spanbs.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SignInActivity.this, R.color.txt_red)),
                                    spanbs.length() - 2 - String.valueOf(getGb).length() - 1,
                                    spanbs.length() - 2,
                                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            tvTitleSignIn.setText(spanbs);*/


                            //弹窗提示签到成功
                            CustomDialog.show(SignInActivity.this, R.layout.layout_custom_dialog_sign_in_success, (dialog, v) -> {
                                TextView tvGetGold = v.findViewById(R.id.iv_get_gold_coin_count);
                                tvGetGold.setText(String.format(getString(R.string.txt_get_goldcoin_x), getGb));
                                v.findViewById(R.id.iv_asBtn_close).setOnClickListener(view1 -> dialog.doDismiss());
                                v.findViewById(R.id.btn_i_see).setOnClickListener(view12 -> dialog.doDismiss());
                            });
                        }

                        getSignInInfo();
                    }
                });
    }
}
