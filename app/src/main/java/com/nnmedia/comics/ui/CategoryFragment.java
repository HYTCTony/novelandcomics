package com.nnmedia.comics.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nnmedia.comics.R;
import com.nnmedia.comics.adapter.CategoryAdapter;
import com.nnmedia.comics.adapter.ComicAdapter;
import com.nnmedia.comics.api.CategoryApi;
import com.nnmedia.comics.model.CategoryBean;
import com.nnmedia.comics.model.ComicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 漫画分类页面
 */
public class CategoryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CategoryAdapter categoryAdapter;
    private ComicAdapter comicAdapter;
    private CategoryApi categoryApi;

    private List<CategoryBean> categories = new ArrayList<>();
    private List<ComicBean> comics = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mkz_fragment_category_new, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        setupListeners();
        loadCategories();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
    }

    private void initData() {
        // 初始化API
        categoryApi = new CategoryApi();

        // 初始化适配器
        categoryAdapter = new CategoryAdapter(getContext());
        comicAdapter = new ComicAdapter(getContext());

        // 设置分类点击监听
        categoryAdapter.setOnCategoryClickListener(this::onCategoryClick);

        // 设置漫画点击监听
        comicAdapter.setOnComicClickListener(this::onComicClick);
    }

    private void setupListeners() {
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    CategoryBean category = categories.get(position);
                    loadComics(category.getId());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    }

    /**
     * 加载分类数据（预留网络请求）
     */
    private void loadCategories() {
        // TODO: 实现网络请求
        // 示例：categoryApi.getCategories(new CategoryApi.CategoryCallback() {
        //     @Override
        //     public void onSuccess(List<CategoryBean> data) {
        //         categories.clear();
        //         categories.addAll(data);
        //         categoryAdapter.setCategories(categories);
        //         setupTabs();
        //     }
        //
        //     @Override
        //     public void onError(String error) {
        //         // 处理错误
        //     }
        // });

        // 临时使用模拟数据
        loadMockCategories();
    }

    /**
     * 加载漫画数据（预留网络请求）
     */
    private void loadComics(String categoryId) {
        // TODO: 实现网络请求
        // 示例：categoryApi.getComics(categoryId, new CategoryApi.ComicCallback() {
        //     @Override
        //     public void onSuccess(List<ComicBean> data) {
        //         comics.clear();
        //         comics.addAll(data);
        //         comicAdapter.setComics(comics);
        //     }
        //
        //     @Override
        //     public void onError(String error) {
        //         // 处理错误
        //     }
        // });

        // 临时使用模拟数据
        loadMockComics();
    }

    private void setupTabs() {
        if (tabLayout != null) {
            tabLayout.removeAllTabs();
            for (CategoryBean category : categories) {
                tabLayout.addTab(tabLayout.newTab().setText(category.getName()));
            }
        }
    }

    /**
     * 分类点击事件
     */
    private void onCategoryClick(int position, CategoryBean category) {
        categoryAdapter.setSelectedPosition(position);
        loadComics(category.getId());
    }

    /**
     * 漫画点击事件
     */
    private void onComicClick(int position, ComicBean comic) {
        // TODO: 跳转到漫画详情页
        // Intent intent = new Intent(getActivity(), ComicDetailActivity.class);
        // intent.putExtra("comic_id", comic.getId());
        // startActivity(intent);
    }

    /**
     * 加载模拟分类数据
     */
    private void loadMockCategories() {
        categories.clear();

        CategoryBean category1 = new CategoryBean("1", "全部");
        CategoryBean category2 = new CategoryBean("2", "热血");
        CategoryBean category3 = new CategoryBean("3", "冒险");
        CategoryBean category4 = new CategoryBean("4", "悬疑");
        CategoryBean category5 = new CategoryBean("5", "恋爱");

        categories.add(category1);
        categories.add(category2);
        categories.add(category3);
        categories.add(category4);
        categories.add(category5);

        categoryAdapter.setCategories(categories);
        setupTabs();
    }

    /**
     * 加载模拟漫画数据
     */
    private void loadMockComics() {
        // 这里加载模拟数据，实际开发时替换为网络请求
        // loadMockComics()方法已经在上面的loadComics中调用了
    }
}
