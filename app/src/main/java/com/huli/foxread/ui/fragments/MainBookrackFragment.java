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
import com.huli.foxread.rxhttp.ErrorInfo;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.activities.MainActivity;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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

        layout = $(view, R.id.smart);
        layout.setDragRate(1);
        recyclerView = $(view, R.id.recyclerView_my_bookrack);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        rackAdapter = new BookRackAdapter();
        recyclerView.setAdapter(rackAdapter);
        View headView = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_book_rack, recyclerView, false);
        rackAdapter.setHeaderView(headView);

        cardSpecialRecommend = $(headView, R.id.card_special_recommend);
        ivookCoverPush = $(headView, R.id.iv_book_cover_push);
        tvBookNamePush = $(headView, R.id.tv_book_name_push);
        tvBookIntroPush = $(headView, R.id.tv_book_introduction_push);
    }

    @Override
    public void setListener() {
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
        ((MainActivity) mActivity).getUserReadTime();
    }


    @SuppressLint("CheckResult")
    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        List<BookShelfOrADsMultEntity> datas = rackAdapter.getData();
        BookShelfOrADsMultEntity multEntity = datas.get(position);
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


    @SuppressLint("CheckResult")
    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        List<BookShelfOrADsMultEntity> datas = rackAdapter.getData();
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
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_book_rack, menu);
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
    private void loadListAd(boolean isRefresh) {
        /*float expressViewWidth = UIUtils.getScreenWidthDp(mActivity);
        float expressViewHeight = 0;     //高度设置为0,则高度会自适应
        //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(CsjAdsCode.BOOKRACK_CODE_ID)
                .setSupportDeepLink(true)
//                .setImageAcceptedSize(280,360 )//这个参数设置即可，不影响个性化模板广告的size
                .setExpressViewAcceptedSize(expressViewWidth, expressViewWidth / 4) //期望模板广告view的size,单位dp
                .setAdCount(count) //请求广告数量为1到3条
                .build();
        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.isEmpty()) {
//                    Toast.makeText(mActivity, "on FeedAdLoaded: ad is null!", Toast.LENGTH_SHORT).show();
                    return;
                }
               *//* if (isRefresh) {
                } else {
                    rackAdapter.addData(0, new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.TYPE_ADS_CSJ, null, ads.get(0)));
                    recyclerView.scrollToPosition(0);
                }*//*

                List<BookShelfOrADsMultEntity> oldDatas = rackAdapter.getData();
                if (oldDatas.size() > 0) {
                    BookShelfOrADsMultEntity adEntity = oldDatas.get(0);
                    if (adEntity.getItemType() == BookShelfOrADsMultEntity.TYPE_ADS_CSJ) {
                        rackAdapter.setData(0, new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.TYPE_ADS_CSJ, null, ads.get(0)));
                    } else {
                        rackAdapter.addData(0, new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.TYPE_ADS_CSJ, null, ads.get(0)));
                        recyclerView.scrollToPosition(0);
                    }
                } else {
                    rackAdapter.addData(0, new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.TYPE_ADS_CSJ, null, ads.get(0)));
                    recyclerView.scrollToPosition(0);
                }

            }
        });*/
        /*List<BookShelfOrADsMultEntity> datas = rackAdapter.getData();
        if (datas.size() > 0) {
            BookShelfOrADsMultEntity multEntity = datas.get(0);
            if(multEntity.getItemType()==BookShelfOrADsMultEntity.TYPE_ADS_GDT){
                NativeUnifiedADData addata = (NativeUnifiedADData) multEntity.getAds();
                if(addata!=null){
                    addata.destroy();
                }
            }
        }*/

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
                .add(Consts.NOVEL_ID, novelIds)
                .setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE)
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
                    loadListAd(false);
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
                });
    }

}