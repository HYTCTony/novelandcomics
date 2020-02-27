package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.WelfareZoneMineAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.HorizontalItemDecoration;
import com.huli.foxread.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainMineFragment extends BaseFragment implements View.OnClickListener {

    private View layoutLogged, layoutNotLogin;
    private RecyclerView rvWelfareZone;

    @Override
    public int bindLayout() {
        return R.layout.fragment_main_mine;
    }

    @Override
    public void setStatusBar(View view) {
        StatusBarUtils.offsetView(mActivity, $(view, R.id.ctl_top_bar_mine));
        StatusBarUtils.setAndroidNativeLightStatusBar(mActivity, true);
    }

    @Override
    public void initView(View view) {

        layoutNotLogin = $(view, R.id.ll_not_login_show_mine);
        layoutLogged = $(view, R.id.ctl_logged_show_mine);


        rvWelfareZone = $(view, R.id.recyclerView_welfare_zone);
        LinearLayoutManager llManager = new LinearLayoutManager(mActivity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        llManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWelfareZone.setLayoutManager(llManager);
        rvWelfareZone.addItemDecoration(new HorizontalItemDecoration(16, mActivity, true));
        WelfareZoneMineAdapter wzAdapter = new WelfareZoneMineAdapter();
        rvWelfareZone.setAdapter(wzAdapter);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("ssssss" + i);
        }
        wzAdapter.setNewData(list);

        $(view, R.id.rtl_asBtn_my_privilege).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_msg_notify).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_reading_record).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_cash_withdrawal).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_mode_adolescent).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_help_and_feedback).setOnClickListener(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtl_asBtn_my_privilege:

                break;
            case R.id.rtl_asBtn_msg_notify:

                break;
            case R.id.rtl_asBtn_reading_record:

                break;
            case R.id.rtl_asBtn_cash_withdrawal:

                break;
            case R.id.rtl_asBtn_mode_adolescent:

                break;
            case R.id.rtl_asBtn_help_and_feedback:

                break;

            default:
                break;
        }
    }
}
