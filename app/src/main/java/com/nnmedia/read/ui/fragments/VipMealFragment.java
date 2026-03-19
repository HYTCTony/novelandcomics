package com.nnmedia.read.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.VipCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.ReExchangeSetEntity;
import com.nnmedia.read.ui.adapters.VipExchangeAdapter;
import com.nnmedia.read.ui.base.BaseFragment;
import com.nnmedia.read.ui.decoration.GridSpacingItemDecoration;
import com.nnmedia.read.utils.DensityUtils;
import com.rxjava.rxlife.RxLife;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VipMealFragment extends BaseFragment implements VipExchangeAdapter.OnVipComboSelectListenr {

    private int mType;

    private RecyclerView recyclerView;
    private VipExchangeAdapter mAdapter;

    public static VipMealFragment newInstance(int type, int index) {
        Bundle bundle = new Bundle();
        VipMealFragment fragment = new VipMealFragment();
        bundle.putInt(Consts.VIP_FROM_TYPE, type);
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_vip_meal;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        Bundle bundle = getArguments();
        int index = bundle.getInt("index");
        // 这个设置tag要与FragmentPagerAdapter中的获取方法getItemPosition方法要对应上
        view.setTag(index);

        recyclerView = $(view, R.id.recyclerView_vip_packages);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, DensityUtils.dp2px(mActivity, 16), true));
        mAdapter = new VipExchangeAdapter(this);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        mType = getArguments().getInt(Consts.VIP_FROM_TYPE, 0);
        reqRechargeCombo();
    }

    @Override
    public void onVipComboSelect(String comboId, String alipay, String weixin, int cdkey) {
        VipCache.saveCombo(getActivity(), comboId, alipay, weixin, cdkey, mType);
//        Logger.d("type：" + mType + "&comboId：" + comboId + "&alipay：" + alipay + "&weixin：" + weixin);
    }

    /**
     * 获取充值列表
     */
    private void reqRechargeCombo() {
//        if (mType == 3) {
//            RxHttp.get(Consts.ORDER_H_POINT_EXCHANGE_API)
//                    .asResponseList(ReExchangeSetEntity.class)
//                    .to(RxLife.toMain(this))
//                    .subscribe(datas -> mAdapter.setList(datas));
//        } else
            if (mType == 1) {
            RxHttp.get(Consts.ORDER_GOLD_EXCHANGE_API)
                    .asResponseList(ReExchangeSetEntity.class)
                    .to(RxLife.toMain(this))
                    .subscribe(datas -> mAdapter.setList(datas));
        } else if (mType == 2) {
            RxHttp.get(Consts.ORDER_POINT_EXCHANGE_API)
                    .asResponseList(ReExchangeSetEntity.class)
                    .to(RxLife.toMain(this))
                    .subscribe(datas -> mAdapter.setList(datas));
        } else {
            RxHttp.get(Consts.ORDER_ESCROW_API)
                    .asResponseList(ReExchangeSetEntity.class)
                    .to(RxLife.toMain(this))
                    .subscribe(datas -> mAdapter.setList(datas));
        }
    }
}