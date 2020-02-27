package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.BookRackAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainBookrackFragment extends BaseFragment implements OnItemLongClickListener {

    private AppBarLayout appBarLayout;
    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private BookRackAdapter mAdapter;

    private ImageView ivookCoverPush;
    private TextView tvBookNamePush, tvBookIntroPush;

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
        mToolbar.setTitle("Hi小说萌新11");
//        mToolbar.inflateMenu(R.menu.menu_book_rack);
        ((AppCompatActivity) mActivity).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);

        appBarLayout = $(view, R.id.appBarLayout_bookrack);

        ivookCoverPush = $(view, R.id.iv_book_cover_push);
        tvBookNamePush = $(view, R.id.tv_book_name_push);
        tvBookIntroPush = $(view, R.id.tv_book_introduction_push);

        recyclerView = $(view, R.id.recyclerView_my_bookrack);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, DensityUtils.dp2px(mActivity, 16), true));

        List<String> list = new ArrayList<>();
        list.add("pppppppp");
        for (int i = 0; i < 4; i++) {
            list.add(list.size(), "sssssssssss");
        }
        mAdapter = new BookRackAdapter(list);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void setListener() {
        mToolbar.setOnMenuItemClickListener(item -> {
            Tos.showShort(mActivity, "点击===" + item.getTitle());
            return true;
        });
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
        }
    }

    boolean fff;
    Snackbar snackbar;

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        if (!fff) {
            appBarLayout.setExpanded(false, true);
            recyclerView.setNestedScrollingEnabled(false);
            Window window = mActivity.getWindow();//获取当前activity的window
            ViewGroup decorView = (ViewGroup) window.getDecorView();//获取activity的跟布局
            snackbar = Snackbar.make(decorView, "这是一个snackbar", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        } else {
            appBarLayout.setExpanded(true, true);
            recyclerView.setNestedScrollingEnabled(true);
            snackbar.dismiss();
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

}
