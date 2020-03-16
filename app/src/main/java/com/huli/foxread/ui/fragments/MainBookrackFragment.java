package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.google.android.material.appbar.AppBarLayout;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.adapters.BookRackAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.ui.activity.ReadBookActivity;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainBookrackFragment extends BaseFragment implements OnItemLongClickListener, OnItemClickListener {

    private AppBarLayout appBarLayout;
    private Toolbar mToolbar;
    private SmartRefreshLayout layout;
    private RecyclerView recyclerView;
    private BookRackAdapter mAdapter;

    private ImageView ivookCoverPush;
    private TextView tvBookNamePush, tvBookIntroPush;

    private List<BookShelfListBean> data = new ArrayList<>();

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
        boolean isVisitor = UserInfoCache.getIsVisitor(mActivity);
        if (isVisitor) {
            mToolbar.setTitle(R.string.txt_say_hi);
        } else {
            mToolbar.setTitle(UserInfoCache.getUserName(mActivity));
        }
        ((AppCompatActivity) mActivity).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);

        appBarLayout = $(view, R.id.appBarLayout_bookrack);

        ivookCoverPush = $(view, R.id.iv_book_cover_push);
        tvBookNamePush = $(view, R.id.tv_book_name_push);
        tvBookIntroPush = $(view, R.id.tv_book_introduction_push);

        layout = $(view, R.id.smart);
        recyclerView = $(view, R.id.recyclerView_my_bookrack);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, DensityUtils.dp2px(mActivity, 16), true));

        mAdapter = new BookRackAdapter(data);
        recyclerView.setAdapter(mAdapter);
        layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reqGetBooks();
                refreshLayout.finishRefresh();
            }
        });
        layout.setEnableLoadMore(false);
        data.add(new BookShelfListBean());
        mAdapter.setNewData(data);
        reqGetBooks();
    }


    @Override
    public void setListener() {
        mToolbar.setOnMenuItemClickListener(item -> {
            Tos.showShort(mActivity, "点击===" + item.getTitle());
            return true;
        });
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        GlideUtil.loadRoundRect(mContext, ivookCoverPush, "url", 0);
        tvBookNamePush.setText("九阳帝尊");
        tvBookIntroPush.setText("深山里走出的少年深山里走出的少年深山的少年深山里走出的少年深山里走出的少年");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);

            boolean isVisitor = UserInfoCache.getIsVisitor(mActivity);
            if (isVisitor) {
                mToolbar.setTitle(R.string.txt_say_hi);
            } else {
                mToolbar.setTitle(UserInfoCache.getUserName(mActivity));
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position != data.size() - 1) {
            BookShelfListBean bean = (BookShelfListBean) adapter.getItem(position);
            if (bean.getIsLocal()) {
                Toast.makeText(mActivity, "抱歉，暂时不支持本地书籍", Toast.LENGTH_SHORT).show();
                return;
            }
            ReadBookActivity.start(mActivity, bean, true);
        } else {
            Toast.makeText(mActivity, "添加书籍", Toast.LENGTH_SHORT).show();
        }
    }

    boolean fff;

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        if (!fff) {
            appBarLayout.setExpanded(false, true);
            recyclerView.setNestedScrollingEnabled(false);
            Window window = mActivity.getWindow();//获取当前activity的window
            ViewGroup decorView = (ViewGroup) window.getDecorView();//获取activity的跟布局
        } else {
            appBarLayout.setExpanded(true, true);
            recyclerView.setNestedScrollingEnabled(true);
        }
        fff = !fff;
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
                }
            }
        }
    }

    /**
     * @param novelIds ["56","32","99","5","796"]
     * @param novelIds List也行
     */
    private void reqDelBooks(String[] novelIds) {
        String ids = JSON.toJSONString(novelIds);
        OkGo.<String>post(Consts.BOOKRACK_DEL_API)
                .params(Consts.NOVEL_IDS, ids)
                .execute(new LtbCallback((AppCompatActivity) mActivity) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            //TODO 刷新数据
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
                .execute(new LtbCallback((AppCompatActivity) mActivity) {
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
                });
    }

}