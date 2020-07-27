package com.huli.foxread.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobad.feeds.NativeResponse;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.config.AdConfig;
import com.huli.foxread.config.TogetherAdConst;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.multi.BookShelfOrADsMultEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.rxhttp.ErrorInfo;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.activities.MainActivity;
import com.huli.foxread.ui.activities.MyPrivilegeActivity;
import com.huli.foxread.ui.activities.ReadingRecordActivity;
import com.huli.foxread.ui.activities.SearchBookActivity;
import com.huli.foxread.ui.activities.SignInActivity;
import com.huli.foxread.ui.adapters.BookRackAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.model.local.BookRepository;
import com.huli.page.ui.activity.ReadBookActivity;
import com.huli.page.utils.RxUtils;
import com.hytc.ads.helper.flow.TogetherAdFlow;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.MessageDialog;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rxhttp.wrapper.cahce.CacheMode;

/**
 * 书架
 */
public class MainBookrackFragment extends BaseFragment implements OnItemLongClickListener, OnItemClickListener, OnRefreshListener {

    private Toolbar mToolbar;
    private SmartRefreshLayout layout;
    private RecyclerView recyclerView;
    private BookRackAdapter rackAdapter;

    private CardView cardSpecialRecommend;
    private ImageView ivookCoverPush;
    private TextView tvBookNamePush, tvBookIntroPush;

    /*书架数据*/
    private List<BookShelfOrADsMultEntity> datas = new ArrayList<>();
    private boolean isInit = true;

    private String specialBookId;

    private long lastReqTime;       //上次获取书架数据的时间

    private TextView btnComplete;
    private Menu aMenu;                         //获取optionmenu
    private boolean isManagerMode = false;      //管理模式也标示是否要显示optionmenu
    private boolean isSelectAll = false;         //管理模式下是否全选标志

    public boolean isManagerMode() {
        return isManagerMode;
    }

    public void setManagerMode(boolean managerMode) {
        this.isManagerMode = managerMode;
        if (isManagerMode) {
            checkOptionMenu();
            ((MainActivity) mActivity).showBrBottomBar();
            rackAdapter.setManagerMode(isManagerMode);
            if (layout != null) {
                layout.setEnableRefresh(false);
            }
        } else {
            checkOptionMenu();
            ((MainActivity) mActivity).dismissBrBottomBar();
            rackAdapter.setManagerMode(isManagerMode);
            if (layout != null) {
                layout.setEnableRefresh(true);
            }
        }
    }

    public boolean isSelectAll() {
        return isSelectAll;
    }

    public void setSelectAll(boolean selectAll) {
        isSelectAll = selectAll;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_main_book_rack;
    }

    @Override
    public void setStatusBar(View view) {
        StatusBarUtils.offsetView(mActivity, $(view, R.id.toolbar_book_rack));
        StatusBarUtils.setAndroidNativeLightStatusBar(mActivity, true);
    }

    @Override
    public void initView(View view) {
        mToolbar = $(view, R.id.toolbar_book_rack);
        FUser fUser = UserInfoCache.getUserInfo(mActivity);
        if (fUser.isIs_tourist()) {
            mToolbar.setTitle(R.string.txt_say_hi);
        } else {
            mToolbar.setTitle(fUser.getUsername());
        }
        ((AppCompatActivity) mActivity).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);

        btnComplete = $(view, R.id.tv_asBtn_complete_bookrack_manage);

        layout = $(view, R.id.smart);
        layout.setDragRate(1);
        recyclerView = $(view, R.id.recyclerView_my_bookrack);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        rackAdapter = new BookRackAdapter();
        recyclerView.setAdapter(rackAdapter);
        View headView = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_book_rack, recyclerView, false);
        rackAdapter.setHeaderView(headView);
        rackAdapter.setEmptyView(R.layout.layout_empty);

        cardSpecialRecommend = $(headView, R.id.card_special_recommend);
        cardSpecialRecommend.setVisibility(View.GONE);
        ivookCoverPush = $(headView, R.id.iv_book_cover_push);
        tvBookNamePush = $(headView, R.id.tv_book_name_push);
        tvBookIntroPush = $(headView, R.id.tv_book_introduction_push);
    }

    @Override
    public void setListener() {
        btnComplete.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                if (isManagerMode) {
                    setManagerMode(false);
                }
            }
        });
        rackAdapter.setOnItemClickListener(this);
        rackAdapter.setOnItemLongClickListener(this);
        layout.setOnRefreshListener(this);
        layout.setEnableLoadMore(false);

        cardSpecialRecommend.setOnClickListener(v -> {
            if (TextUtils.isEmpty(specialBookId)) {
                Toast.makeText(mActivity, "获取书籍失败！", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, specialBookId);
            startActivity(intent);
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        EventBus.getDefault().register(this);

        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        boolean haveAsked = (boolean) SPFUtils.get(mContext, "csj_have_asked_perm", false);
        if (!haveAsked) {
            SPFUtils.put(mContext, "csj_have_asked_perm", true);
        }

        getSpecialBook();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoChangeEvent(FUser event) {
        if (event.isIs_tourist()) {
            mToolbar.setTitle(R.string.txt_say_hi);
        } else {
            mToolbar.setTitle(event.getUsername());
        }

        isInit = true;
        reqGetBooks();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);
            if (isVisible()) {
//                Log.e("ssssssss", "onHiddenChanged可见");
                reqGetBooks();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
//            Log.e("ssssssss", "onResume可见");
            reqGetBooks();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getSpecialBook();
        reqGetBooks();
    }

    /**
     * 全选or非全选
     */
    public int funCheckAll(boolean checkAll) {
        setSelectAll(checkAll);
        return rackAdapter.funCheckAll(checkAll);
    }


    /**
     * 删除书架书籍（广告）
     */
    public void delBookShelfData() {
        List<BookShelfOrADsMultEntity> list = rackAdapter.getSelectedEntityList();

        //构造删除书籍的参数
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            BookShelfOrADsMultEntity entity = list.get(i);
            if (entity.getItemType() == BookShelfOrADsMultEntity.DETAILED) {
                BookShelfListBean bean = entity.getBook();
                ids.append(bean.getId()).append(",");
            }
        }

        if (list.size() > 0 && ids.length() == 0) {
            BottomMenu.show((AppCompatActivity) mActivity, new String[]{"删除广告", "全场去广告>>"}, (text, index) -> {
                if (index == 0) {
                    for (int q = 0; q < list.size(); q++) {
                        rackAdapter.remove(list.get(q));
                    }
                    setDelConfirmUI();
                } else if (index == 1) {
                    btnComplete.performClick();
                    //充会员
                    startActivity(new Intent(mActivity, MyPrivilegeActivity.class));
                }
            }).setMenuTextInfo(new TextInfo().setFontSize(14).setFontColor(ContextCompat.getColor(mActivity, R.color.col_orange_ea6b3c)))
                    .setCancelButtonTextInfo(new TextInfo().setFontSize(14));
            return;
        }

        MessageDialog.show((AppCompatActivity) mActivity, "删除书籍", "是否删除这些书籍？", "确定", "取消")
                .setOnOkButtonClickListener((baseDialog, v) -> {
                    //确认删除后的ui
                    setDelConfirmUI();

                    if (ids.length() > 1) {
                        //网络请求
                        reqDelBooks(ids.substring(0, ids.length() - 1));
                    }

                    //刷新列表数据
                    /*rackAdapter.getData().removeAll(list);
                    rackAdapter.notifyDataSetChanged();*/

                    //删除本地数据
                    for (int p = 0; p < list.size(); p++) {
                        BookShelfOrADsMultEntity entity = list.get(p);
                        if (entity.getItemType() == BookShelfOrADsMultEntity.DETAILED) {
                            BookShelfListBean bean = entity.getBook();
                            BookRepository.getInstance().deleteCollBookInRx(bean)
                                    .compose(RxUtils::toSimpleSingle)
                                    .subscribe(
                                            (Void) -> rackAdapter.remove(entity)
                                    );
                        } else {
                            rackAdapter.remove(entity);
                        }
                    }
                    return false;
                });

        rackAdapter.clearSelected();
        //是否删完了
        if (rackAdapter.getData().size() <= 1) {
            btnComplete.performClick();
        }
    }

    /**
     * 确认删除后的ui
     */
    private void setDelConfirmUI() {
        ((MainActivity) mActivity).setBrDelNum(0);
        setSelectAll(false);
        ((MainActivity) mActivity).setSelectBtnText(isSelectAll());
    }

    @SuppressLint("CheckResult")
    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        List<BookShelfOrADsMultEntity> datas = rackAdapter.getData();
        BookShelfOrADsMultEntity multEntity = datas.get(position);
        if (isManagerMode) {
            if (multEntity.getItemType() == BookShelfOrADsMultEntity.ITEM_ADD_BOOK) {
                return;
            }
            int count = rackAdapter.clickItemOnManageMode(position);
            MainActivity mainActivity = ((MainActivity) mActivity);
            mainActivity.setBrDelNum(count);
            if (count >= datas.size() - 1 && !isSelectAll) {        //设置为全选状态
                setSelectAll(true);
                mainActivity.setSelectBtnText(true);
            } else if (count < datas.size() - 1 && isSelectAll) {   //设置为非全选状态
                setSelectAll(false);
                mainActivity.setSelectBtnText(false);
            }
        } else {
            if (multEntity.getItemType() == BookShelfOrADsMultEntity.DETAILED) {
                BookShelfListBean bean = multEntity.getBook();
                bean.setIs_exist_bookshelf(1);
                if (bean.getIsLocal()) {
                    Toast.makeText(mActivity, "抱歉，暂时不支持本地书籍", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bean.getIs_copyright() == 1) {
                    ReadBookActivity.start(mActivity, bean, true, -1);
                } else {
                    MessageDialog.show((AppCompatActivity) mActivity, "温馨提示", "这本书版权过期，是否删除这本书？", "确定")
                            .setOnOkButtonClickListener((baseDialog, v) -> {
                                reqDelBooks(bean.getId());
                                BookRepository.getInstance().deleteCollBookInRx(bean)
                                        .compose(RxUtils::toSimpleSingle)
                                        .subscribe(
                                                (Void) -> adapter.remove(position)
                                        );
                                return false;
                            });
                }
            } else if (multEntity.getItemType() == BookShelfOrADsMultEntity.ITEM_ADD_BOOK) {
                ((MainActivity) mActivity).switch2Bookstore();
            }
        }
    }


    @SuppressLint("CheckResult")
    @Override
    public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
            /*List<BookShelfOrADsMultEntity> datas = rackAdapter.getData();
            BookShelfOrADsMultEntity multEntity = datas.get(position);
            if (multEntity.getItemType() == BookShelfOrADsMultEntity.DETAILED) {
            BookShelfListBean bean = multEntity.getBook();
            MessageDialog.show((AppCompatActivity) mActivity, "温馨提示", "是否删除[" + bean.getNovel_name() + "]这本书？", "确定", "取消")
                    .setOnOkButtonClickListener((baseDialog, v) -> {
                        reqDelBooks(bean.getId());
                        BookRepository.getInstance().deleteCollBookInRx(bean)
                                .compose(RxUtils::toSimpleSingle)
                                .subscribe(
                                        (Void) -> adapter.remove(position)
                                );
                        return false;
                    });

            }*/
        if (!isManagerMode) {
            List<BookShelfOrADsMultEntity> datas = rackAdapter.getData();
            BookShelfOrADsMultEntity multEntity = datas.get(position);
            if (multEntity.getItemType() == BookShelfOrADsMultEntity.DETAILED) {
                setManagerMode(true);
            }
        }
        return false;
    }

    /**
     * 设置menu的隐藏显示
     */
    public void checkOptionMenu() {
        if (null != aMenu) {
            if (!isManagerMode) {
                for (int i = 0; i < aMenu.size(); i++) {
                    aMenu.getItem(i).setVisible(true);
                    aMenu.getItem(i).setEnabled(true);
                }
                btnComplete.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < aMenu.size(); i++) {
                    aMenu.getItem(i).setVisible(false);
                    aMenu.getItem(i).setEnabled(false);
                }
                btnComplete.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        aMenu = menu;
        aMenu.clear();
        inflater.inflate(R.menu.menu_book_rack, aMenu);
        checkOptionMenu();
        super.onCreateOptionsMenu(aMenu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_in:
                startActivity(new Intent(mActivity, SignInActivity.class));
                break;
            case R.id.action_history:
                startActivity(new Intent(mActivity, ReadingRecordActivity.class));
                break;
            case R.id.action_search:
                Intent intent = new Intent(mActivity, SearchBookActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载feed广告
     */
    private void loadListAd() {
        TogetherAdFlow.getAdList(mActivity, AdConfig.listAdConfig(mActivity), TogetherAdConst.AD_FLOW_BOOKRACK, 4, new TogetherAdFlow.AdListenerList() {
            @Override
            public void onAdFailed(@Nullable String failedMsg) {

            }

            @Override
            public void onAdLoaded(@NotNull String channel, @NotNull List<?> adList) {
                if (adList.size() == 0) {
//                    Toast.makeText(mActivity, "on FeedAdLoaded: ad is null!", Toast.LENGTH_SHORT).show();
                    return;
                }
                BookShelfOrADsMultEntity adData = null;
                Random random = new Random();
                Object any = adList.get(random.nextInt(adList.size()));
                if (any instanceof NativeUnifiedADData) {
                    adData = new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.TYPE_ADS_GDT, null, any);
                } else if (any instanceof NativeResponse) {
                    adData = new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.TYPE_ADS_BAIDU, null, any);
                } else if (any instanceof TTFeedAd) {
//                    adData = new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.TYPE_ADS_CSJ, null, any);
                }

                if (adData != null) {
                    if (isInit) {
                        rackAdapter.addData(0, adData);
                        recyclerView.scrollToPosition(0);
                    } else {
                        MainBookrackFragment.this.datas.add(0, adData);
                        rackAdapter.setList(MainBookrackFragment.this.datas);
                    }
                }
                isInit = false;
            }

            @Override
            public void onStartRequest(@NotNull String channel) {

            }
        });
    }

    /**
     * 删除书架书籍
     */
    private void reqDelBooks(String novelIds) {
        RxHttp.postForm(Consts.BOOKRACK_DEL_API)
                .add(Consts.BOOKRACK_ID, novelIds)
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
                }, (OnError) ErrorInfo::show);
    }

    /**
     * 获取书架列表
     */
    private void reqGetBooks() {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - lastReqTime) < 5 * 1000) {
            layout.finishRefresh(1000);
            return;
        }
        lastReqTime = nowTime;

        RxHttp.postForm(Consts.BOOKRACK_GETLIST_API)
                .setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)
                .asResponseList(BookShelfListBean.class)
                .doFinally(() -> layout.finishRefresh())
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(list -> {
                    datas.clear();
                    BookShelfOrADsMultEntity multEntity;
                    for (BookShelfListBean bean : list) {
                        multEntity = new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.DETAILED, bean, null);
                        datas.add(multEntity);
                    }
                    datas.add(new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.ITEM_ADD_BOOK, null, null));

                    if (isInit) {
                        rackAdapter.setList(datas);
                    }

                    //vip或者荣誉vip不显示广告
                    if (!UserInfoCache.getIsVip(mActivity) && UserInfoCache.getSuperVip(mActivity) == 2) {
                        loadListAd();
                    }
                });
    }

    /**
     * 获取特别推荐的一本书
     */
    private void getSpecialBook() {
        RxHttp.postForm(Consts.SPECIAL_BOOK_API)
                .setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)
                .asResponse(BookEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(data -> {
                    specialBookId = data.getId();
                    GlideUtil.loadRoundRect(mActivity, ivookCoverPush, data.getHttp_image());
                    tvBookNamePush.setText(data.getName());
                    tvBookIntroPush.setText(data.getIntroduce());
                    cardSpecialRecommend.setVisibility(View.VISIBLE);
                });
    }

}