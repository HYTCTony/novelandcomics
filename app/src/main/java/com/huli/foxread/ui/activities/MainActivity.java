package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.eventbus.ReadingTimeEvent;
import com.huli.foxread.entity.tab.TabEntity;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.BookStoreBoyFragment;
import com.huli.foxread.ui.fragments.MainBookrackFragment;
import com.huli.foxread.ui.fragments.MainBookstoreFragment;
import com.huli.foxread.ui.fragments.MainMineFragment;
import com.huli.foxread.ui.fragments.MainWelfareFragment;
import com.huli.foxread.ui.fragments.SelectionBookFragment;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends BaseActivity implements OnTabSelectListener {
    private static final long INTERVAL = 2000;  //按两次返回键退出间隔的时间
    private long mExitFirstTime;  //用于暂存第一次按返回键的时间

    private CommonTabLayout mTabLayout;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private FragmentManager fragmentManager;

    private MainBookstoreFragment bookstoreFragment;
    private MainBookrackFragment bookrackFragment;
    private MainWelfareFragment welfareFragment;
    private MainMineFragment mineFragment;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.transparent), 0);
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
        return R.layout.activity_main;
    }

    @Override
    public void initView(View view) {
        mTabLayout = $(R.id.cTabLayout_main);
        String[] bottomBarTitles = getResources().getStringArray(R.array.bottom_bar_main);
        mTabEntities.add(new TabEntity(bottomBarTitles[0], R.drawable.tab_bookstore_selected, R.drawable.tab_bookstore_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[1], R.drawable.tab_bookrack_selected, R.drawable.tab_bookrack_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[2], R.drawable.tab_welfare_selected, R.drawable.tab_welfare_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[3], R.drawable.tab_mine_selected, R.drawable.tab_mine_unselected));
        mTabLayout.setTabData(mTabEntities);
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void setListener() {
        mTabLayout.setOnTabSelectListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        switch2Bookstore();

        reqUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reqMyCapitalDetail();
        getUserReadTime();
    }

    @Override
    public void onTabSelect(int position) {
        changeFragment(position);
    }

    @Override
    public void onTabReselect(int position) {
        if (position == 0) {
            try {
                MainBookstoreFragment bookStoreFrag = (MainBookstoreFragment) getSupportFragmentManager().getFragments().get(position);
                int currentTab = bookStoreFrag.slidingTabLayout.getCurrentTab();
                Fragment fragment = bookStoreFrag.getChildFragmentManager().getFragments().get(currentTab);
                if (fragment instanceof SelectionBookFragment) {
                    SelectionBookFragment selectionBookFrag = (SelectionBookFragment) fragment;
                    if (selectionBookFrag.recyclerView.canScrollVertically(-1)) {           //判断RecyclerView是否在顶部
                        selectionBookFrag.recyclerView.smoothScrollToPosition(0);
                    }
                } else if (fragment instanceof BookStoreBoyFragment) {
                    BookStoreBoyFragment bsbFrag = (BookStoreBoyFragment) fragment;
                    if (bsbFrag.recyclerView.canScrollVertically(-1)) {                     //判断RecyclerView是否在顶部
                        bsbFrag.recyclerView.smoothScrollToPosition(0);
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * 切换Fragment
     */
    private void changeFragment(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (position) {
            case 0:
                if (bookstoreFragment == null) {
                    bookstoreFragment = new MainBookstoreFragment();
                    transaction.add(R.id.fl_frag_content_main, bookstoreFragment);
                } else {
                    transaction.show(bookstoreFragment);
                }
                break;

            case 1:
                if (bookrackFragment == null) {
                    bookrackFragment = new MainBookrackFragment();
                    transaction.add(R.id.fl_frag_content_main, bookrackFragment);
                } else {
                    transaction.show(bookrackFragment);
                }
                break;

            case 2:
                if (welfareFragment == null) {
                    welfareFragment = new MainWelfareFragment();
                    transaction.add(R.id.fl_frag_content_main, welfareFragment);
                } else {
                    transaction.show(welfareFragment);
                }
                break;

            case 3:
                if (mineFragment == null) {
                    mineFragment = new MainMineFragment();
                    transaction.add(R.id.fl_frag_content_main, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();   //记得提交事务
    }

    /**
     * 将所有Fragment设置为隐藏
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (bookstoreFragment != null) {
            transaction.hide(bookstoreFragment);
        }
        if (bookrackFragment != null) {
            transaction.hide(bookrackFragment);
        }
        if (welfareFragment != null) {
            transaction.hide(welfareFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    /**
     * 跳到书城
     */
    public void switch2Bookstore() {
        mTabLayout.setCurrentTab(0);
        changeFragment(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitFirstTime) > INTERVAL) {
                Tos.showShort(this, R.string.text_point_out_once_again);
                mExitFirstTime = System.currentTimeMillis();
            } else {
                FrApp.getInstance().exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        OkGo.<String>get(Consts.USER_CAPITAL_API)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<CapitalEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<CapitalEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            CapitalEntity data = entity.getData();
                            EventBus.getDefault().postSticky(data);
                        }
                    }
                });
    }

    /**
     * 获取用户信息
     */
    public void reqUserInfo() {
        OkGo.<LzyResponse<FUser>>get(Consts.USERS_INFO_API)
                .execute(new LtbJsonCallback<LzyResponse<FUser>>(this, false,
                        new TypeReference<LzyResponse<FUser>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<FUser>> response) {
                        if (response.body().error_code == 0) {
                            FUser data = response.body().getData();
                            UserInfoCache.saveCacheAll(MainActivity.this, data);

                            EventBus.getDefault().postSticky(data);
                        }
                    }
                });
    }


    /**
     * 获取用户阅读时间
     */
    private void getUserReadTime() {
        OkGo.<String>get(Consts.USER_READ_TIME_API)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            EventBus.getDefault().postSticky(new ReadingTimeEvent(entity.getData()));
                        } else {
                            TipDialog.show(MainActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }
}
