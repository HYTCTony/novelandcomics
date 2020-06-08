package com.huli.foxread.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.google.android.material.appbar.AppBarLayout;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.config.TTAdManagerHolder;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.eventbus.BookRackChangeEvent;
import com.huli.foxread.entity.eventbus.ReadingTimeEvent;
import com.huli.foxread.entity.multi.BookShelfOrADsMultEntity;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.activities.MainActivity;
import com.huli.foxread.ui.activities.ReadingRecordActivity;
import com.huli.foxread.ui.activities.SearchBookActivity;
import com.huli.foxread.ui.activities.SignInActivity;
import com.huli.foxread.ui.adapters.BookRackAdapter2;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.UIUtils;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.model.local.BookRepository;
import com.huli.page.ui.activity.ReadBookActivity;
import com.huli.page.utils.RxUtils;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 书架
 */
public class MainBookrackFragment extends BaseFragment implements OnItemLongClickListener, OnItemClickListener, OnRefreshListener {

    private AppBarLayout appBarLayout;
    private Toolbar mToolbar;
    private SmartRefreshLayout layout;
    private RecyclerView recyclerView;
    private BookRackAdapter2 rackAdapter;

    private CardView cardSpecialRecommend;
    private ImageView ivookCoverPush;
    private TextView tvBookNamePush, tvBookIntroPush, tvTotalReadingTimeToday, tvAsBtnSignIngGold;

    private List<BookShelfOrADsMultEntity> datas = new ArrayList<>();

    //当Fragment可见的时候刷新书架
    private boolean shouldRefresh = false;

    private String specialBookId;

    private TTAdNative mTTAdNative;

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

        appBarLayout = $(view, R.id.appBarLayout_bookrack);

        cardSpecialRecommend = $(view, R.id.card_special_recommend);
        tvTotalReadingTimeToday = $(view, R.id.tv_total_reading_time_today);
        tvAsBtnSignIngGold = $(view, R.id.tv_asBtn_sign_in_4_gold);
        ivookCoverPush = $(view, R.id.iv_book_cover_push);
        tvBookNamePush = $(view, R.id.tv_book_name_push);
        tvBookIntroPush = $(view, R.id.tv_book_introduction_push);

        layout = $(view, R.id.smart);
        layout.setDragRate(1);
        recyclerView = $(view, R.id.recyclerView_my_bookrack);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        rackAdapter = new BookRackAdapter2(datas);
        recyclerView.setAdapter(rackAdapter);
    }

    @Override
    public void setListener() {
        rackAdapter.setOnItemClickListener(this);
        rackAdapter.setOnItemLongClickListener(this);
        layout.setOnRefreshListener(this);
        layout.setEnableLoadMore(false);

        tvAsBtnSignIngGold.setOnClickListener(v -> startActivity(new Intent(mActivity, SignInActivity.class)));
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

        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(mActivity);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        boolean haveAsked = (boolean) SPFUtils.get(mContext, "csj_have_asked_perm", false);
        if (!haveAsked) {
            TTAdManagerHolder.get().requestPermissionIfNecessary(mActivity);
            SPFUtils.put(mContext, "csj_have_asked_perm", true);
        }

        getSpecialBook();
        reqGetBooks();
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
        reqGetBooks();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReadingTimeEvent(ReadingTimeEvent event) {
        tvTotalReadingTimeToday.setText(event.getReadMin());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBookRackEvent(BookRackChangeEvent event) {
        if (event.isRefreshImmediately()) {
            refreshBookRack();
        } else {
            shouldRefresh = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);
            if (isVisible() && shouldRefresh) {
//                Log.e("ssssssss", "onHiddenChanged可见");
                layout.autoRefresh();
                shouldRefresh = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible() && shouldRefresh) {
//            Log.e("ssssssss", "onResume可见");
            layout.autoRefresh();
            shouldRefresh = false;
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getSpecialBook();
        refreshBookRack();
        ((MainActivity) mActivity).getUserReadTime();
    }


    @SuppressLint("CheckResult")
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        List<BookShelfOrADsMultEntity> datas = rackAdapter.getData();
        BookShelfOrADsMultEntity multEntity = datas.get(position);
        if (multEntity.getItemType() == BookShelfOrADsMultEntity.DETAILED) {
            BookShelfListBean bean = multEntity.getBook();
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

    boolean fff;

    @SuppressLint("CheckResult")
    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
//        if (!fff) {
//            appBarLayout.setExpanded(false, true);
//            recyclerView.setNestedScrollingEnabled(false);
//            Window window = mActivity.getWindow();//获取当前activity的window
//            ViewGroup decorView = (ViewGroup) window.getDecorView();//获取activity的跟布局
//        } else {
//            appBarLayout.setExpanded(true, true);
//            recyclerView.setNestedScrollingEnabled(true);
//        }
//        fff = !fff;

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

    /**
     * 控制appbar的滑动
     *
     * @param isScroll true 允许滑动 false 禁止滑动
     */
    private void banAppBarScroll(boolean isScroll) {
        View mAppBarChildAt = appBarLayout.getChildAt(0);
        AppBarLayout.LayoutParams mAppBarParams = (AppBarLayout.LayoutParams) mAppBarChildAt.getLayoutParams();
        if (isScroll) {
            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            mAppBarChildAt.setLayoutParams(mAppBarParams);
        } else {
            mAppBarParams.setScrollFlags(0);
        }
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
    private void loadListAd(int count, boolean isRefresh) {
        float expressViewWidth = UIUtils.getScreenWidthDp(mActivity);
        float expressViewHeight = 0;     //高度设置为0,则高度会自适应
        //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945165433")
                .setSupportDeepLink(true)
//                .setImageAcceptedSize(280,360 )//这个参数设置即可，不影响个性化模板广告的size
                .setExpressViewAcceptedSize(expressViewWidth, expressViewWidth / 4) //期望模板广告view的size,单位dp
                .setAdCount(count) //请求广告数量为1到3条
                .build();
        //step5:请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
       /* mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                TTFeedAd ttFeedAd;
            }
        });*/
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
                if (isRefresh) {
                    rackAdapter.setData(0, new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.ITEM_ADS, null, ads.get(0)));
                } else {
                    rackAdapter.addData(0, new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.ITEM_ADS, null, ads.get(0)));
                    recyclerView.scrollToPosition(0);
                }
            }
        });
    }

    /*private void bindAdListener(final List<TTNativeExpressAd> ads) {
        final int count = mData.size();
        for (TTNativeExpressAd ad : ads) {
            final TTNativeExpressAd adTmp = ad;
            int random = (int) (Math.random() * LIST_ITEM_COUNT) + count - LIST_ITEM_COUNT;
            mData.set(random, adTmp);
            myAdapter.notifyDataSetChanged();

            adTmp.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                @Override
                public void onAdClicked(View view, int type) {
                    TToast.show(NativeExpressListActivity.this, "广告被点击");
                }

                @Override
                public void onAdShow(View view, int type) {
                    TToast.show(NativeExpressListActivity.this, "广告展示");
                }

                @Override
                public void onRenderFail(View view, String msg, int code) {
                    TToast.show(NativeExpressListActivity.this, msg + " code:" + code);
                }

                @Override
                public void onRenderSuccess(View view, float width, float height) {
                    //返回view的宽高 单位 dp
                    TToast.show(NativeExpressListActivity.this, "渲染成功");
                    myAdapter.notifyDataSetChanged();
                }
            });
            ad.render();

        }

    }*/


    /**
     * 刷新书架
     */
    private void refreshBookRack() {
        OkGo.<String>get(Consts.BOOKRACK_GETLIST_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        datas = new ArrayList<>();
                        LzyResponse<List<BookShelfListBean>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BookShelfListBean>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<BookShelfListBean> list = entity.getData();
                            BookShelfOrADsMultEntity multEntity;
                            for (BookShelfListBean bean : list) {
                                multEntity = new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.DETAILED, bean, null);
                                datas.add(multEntity);
                            }
                            datas.add(new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.ITEM_ADD_BOOK, null, null));
                            List<BookShelfOrADsMultEntity> oldDatas = rackAdapter.getData();
                            if (oldDatas.size() > 0) {
                                BookShelfOrADsMultEntity adEntity = oldDatas.get(0);
                                datas.add(0, adEntity);
                            }
                            rackAdapter.setList(datas);
                            loadListAd(1, true);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        layout.finishRefresh();
                    }
                });
    }


    /**
     * 删除书架书籍
     */
    private void reqDelBooks(String novelIds) {
        OkGo.<String>post(Consts.BOOKRACK_DEL_API)
                .params(Consts.NOVEL_ID, novelIds)
                .execute(new LtbCallback((AppCompatActivity) mActivity) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {

                        } else {
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

    /**
     * 获取书架列表
     */
    private void reqGetBooks() {
        OkGo.<String>get(Consts.BOOKRACK_GETLIST_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        datas = new ArrayList<>();
                        LzyResponse<List<BookShelfListBean>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BookShelfListBean>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<BookShelfListBean> list = entity.getData();
                            BookShelfOrADsMultEntity multEntity;
                            for (BookShelfListBean bean : list) {
                                multEntity = new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.DETAILED, bean, null);
                                datas.add(multEntity);
                            }
                            datas.add(new BookShelfOrADsMultEntity(BookShelfOrADsMultEntity.ITEM_ADD_BOOK, null, null));
                            rackAdapter.setList(datas);
                        } else {
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.ERROR);
                        }
                        loadListAd(1, false);
                    }
                });
    }

    /**
     * 获取特别推荐的一本书
     */
    private void getSpecialBook() {
        OkGo.<String>get(Consts.SPECIAL_BOOK_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<BookEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<BookEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            BookEntity data = entity.getData();
                            specialBookId = data.getId();
                            GlideUtil.loadRoundRect(mActivity, ivookCoverPush, data.getHttp_image());
                            tvBookNamePush.setText(data.getName());
                            tvBookIntroPush.setText(data.getIntroduce());
                        } else {
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

}