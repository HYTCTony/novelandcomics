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
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.google.android.material.appbar.AppBarLayout;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.eventbus.ReadingTimeEvent;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.activities.MainActivity;
import com.huli.foxread.ui.activities.ReadingRecordActivity;
import com.huli.foxread.ui.activities.SearchBookActivity;
import com.huli.foxread.ui.activities.SignInActivity;
import com.huli.foxread.ui.adapters.BookRackAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.model.local.BookRepository;
import com.huli.page.ui.activity.ReadBookActivity;
import com.huli.page.utils.RxUtils;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainBookrackFragment extends BaseFragment implements OnItemLongClickListener, OnItemClickListener {

    private AppBarLayout appBarLayout;
    private Toolbar mToolbar;
    private SmartRefreshLayout layout;
    private RecyclerView recyclerView;
    private BookRackAdapter mAdapter;

    private CardView cardSpecialRecommend;
    private ImageView ivookCoverPush;
    private TextView tvBookNamePush, tvBookIntroPush, tvTotalReadingTimeToday, tvAsBtnSignIngGold;

    private List<BookShelfListBean> data = new ArrayList<>();

    private String specialBookId;

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
        recyclerView = $(view, R.id.recyclerView_my_bookrack);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, DensityUtils.dp2px(mActivity, 16), true));
        mAdapter = new BookRackAdapter(data);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getSpecialBook();
                reqGetBooks();
                ((MainActivity) mActivity).getUserReadTime();
                refreshLayout.finishRefresh();
            }
        });
        layout.setEnableLoadMore(false);
        tvAsBtnSignIngGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, SignInActivity.class));
            }
        });
        cardSpecialRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(specialBookId)) {
                    Toast.makeText(mActivity, "获取书籍失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, specialBookId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        EventBus.getDefault().register(this);
        data.add(new BookShelfListBean());
        mAdapter.setNewData(data);
        getSpecialBook();
    }

    @Override
    public void onResume() {
        super.onResume();
        reqGetBooks();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserInfoChangeEvent(FUser event) {
        if (event.isIs_tourist()) {
            mToolbar.setTitle(R.string.txt_say_hi);
        } else {
            mToolbar.setTitle(event.getUsername());
        }
//        reqGetBooks();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReadingTimeEvent(ReadingTimeEvent event) {
        tvTotalReadingTimeToday.setText(event.getReadMin());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        List<BookShelfListBean> datas = mAdapter.getData();
        if (position != datas.size() - 1) {
            BookShelfListBean bean = (BookShelfListBean) adapter.getItem(position);
            if (bean.getIsLocal()) {
                Toast.makeText(mActivity, "抱歉，暂时不支持本地书籍", Toast.LENGTH_SHORT).show();
                return;
            }
            ReadBookActivity.start(mActivity, bean, true, -1);
        } else {
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
        if (position != data.size() - 1) {
            BookShelfListBean bean = (BookShelfListBean) adapter.getItem(position);
            MessageDialog.show((AppCompatActivity) mActivity, "温馨提示", "是否删除这本书？", "确定", "取消")
                    .setOnOkButtonClickListener((baseDialog, v) -> {
                        reqDelBooks(bean.getId());
                        BookRepository.getInstance().deleteCollBookInRx(bean)
                                .compose(RxUtils::toSimpleSingle)
                                .subscribe(
                                        (Void) -> {
                                            data.remove(position);
                                            adapter.notifyDataSetChanged();
                                        }
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
                intent.putExtra(Consts.TYPE, Consts.TYPE_SELECTION);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                .cacheTime(8 * 60 * 60 * 1000)
                .cacheKey(Consts.BOOKRACK_GETLIST_API + "_bookShelf")
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        data.clear();
                        LzyResponse<List<BookShelfListBean>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BookShelfListBean>>>() {
                                });
                        if (entity.error_code == 0) {
                            data.addAll(entity.getData());
                        } else {
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.ERROR);
                        }
                        data.add(new BookShelfListBean());
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }

                });
    }

    /**
     * 获取特别推荐的一本书
     */
    private void getSpecialBook() {
        OkGo.<String>get(Consts.SPECIAL_BOOK_API)
                .cacheTime(60 * 60 * 1000)
                .cacheKey(Consts.SPECIAL_BOOK_API + "_bookShelf")
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<BookShelfListBean> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<BookShelfListBean>>() {
                                });
                        if (entity.error_code == 0) {
                            BookShelfListBean data = entity.getData();
                            specialBookId = data.getId();
                            Glide.with(getActivity())
                                    .load(data.getHttp_image())
                                    .placeholder(R.drawable.ic_book_loading)
                                    .error(R.drawable.ic_load_error)
                                    .fitCenter()
                                    .into(ivookCoverPush);
                            tvBookNamePush.setText(data.getNovel_name());
                            tvBookIntroPush.setText(data.getIntroduce());
                        } else {
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

}