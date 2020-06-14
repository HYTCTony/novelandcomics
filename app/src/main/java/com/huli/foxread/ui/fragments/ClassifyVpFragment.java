package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.CategoryEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.adapters.ClassifyBookListAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;
import com.kongzue.stacklabelview.StackLabel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewPager中---分类
 */
public class ClassifyVpFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, OnRefreshListener, OnLoadMoreListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private ClassifyBookListAdapter mAdapter;

    /*书籍类型、字数、完结与否*/
    private StackLabel stackLabel1, stackLabel2, stackLabel3;
    private List<String> stack1Datas = new ArrayList<>();
    private List<CategoryEntity> datas = new ArrayList<>();

    private View headView;
    private ImageView ivFirstSign, ivSecondSign, ivThirdSign;
    private ImageView ivTop3Center, ivTop3Left, ivTop3Right;
    private FrameLayout flTop1, flTop2, flTop3;
    private TextView tvTop3BookNameCenter, tvTop3BookNameLeft, tvTop3BookNameRight;
    private View flContentCenter, flContentLeft, flContentRight;
    private TextView tvHitsCenter, tvHitsLeft, tvHitsRight;


    private int mType = 1;
    /*分类id */
    private int paramSubCatID = 0;

    private int paramWordsNum = 0;
    private int paramIsEnd = 0;
    private int paramCurPage = 0;

    private boolean isInit = true;

    private int index;

    public static ClassifyVpFragment newInstance(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        ClassifyVpFragment frag = new ClassifyVpFragment();
        frag.setArguments(bundle);
        return frag;
    }


    @Override
    public int bindLayout() {
        return R.layout.fragment_classify_in_vp;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        Bundle bundle = getArguments();
        index = bundle.getInt("index");
        // 这个设置tag要与FragmentPagerAdapter中的获取方法getItemPosition方法要对应上
        view.setTag(index);

        mType = index + 1;

        stackLabel1 = $(view, R.id.stackLabelView_filter_1);
        stackLabel2 = $(view, R.id.stackLabelView_filter_2);
        stackLabel3 = $(view, R.id.stackLabelView_filter_3);

        stack1Datas.add(getString(R.string.txt_all));
        List<String> stack2Datas = Arrays.asList(getResources().getStringArray(R.array.cat_word_num));
        List<String> stack3Datas = Arrays.asList(getResources().getStringArray(R.array.cat_is_end));
        stackLabel1.setLabels(stack1Datas);
        stackLabel2.setLabels(stack2Datas);
        stackLabel3.setLabels(stack3Datas);
        stackLabel1.setSelectMode(true, stack1Datas);
        stackLabel2.setSelectMode(true, stack2Datas.subList(0, 1));
        stackLabel3.setSelectMode(true, stack3Datas.subList(0, 1));


        mRefreshLayout = $(view, R.id.refreshLayout_classify_list);
        mRefreshLayout.setDragRate(1);
        recyclerView = $(view, R.id.recyclerView_classify_in_vp);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new ClassifyBookListAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
        mAdapter.setHeaderWithEmptyEnable(true);

        headView = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_classify_fragment_top3, recyclerView, false);
        mAdapter.setHeaderView(headView);
        ivFirstSign = $(headView, R.id.iv_top3_gold_crown_sign);
        ivSecondSign = $(headView, R.id.iv_top3_silver_crown_sign);
        ivThirdSign = $(headView, R.id.iv_top3_copper_crown_sign);
        ivTop3Center = $(headView, R.id.iv_top3_bookCover_first);
        ivTop3Left = $(headView, R.id.iv_top3_bookCover_second);
        ivTop3Right = $(headView, R.id.iv_top3_bookCover_third);
        flTop1 = $(headView, R.id.fl_top3_first_sign);
        flTop2 = $(headView, R.id.fl_top3_second_sign);
        flTop3 = $(headView, R.id.fl_top3_third_sign);
        tvTop3BookNameCenter = $(headView, R.id.tv_top3_bookName_first);
        tvTop3BookNameLeft = $(headView, R.id.tv_top3_bookName_second);
        tvTop3BookNameRight = $(headView, R.id.tv_top3_bookName_third);
        flContentCenter = $(headView, R.id.fl_content_hits_center);
        flContentLeft = $(headView, R.id.fl_content_hits_left);
        flContentRight = $(headView, R.id.fl_content_hits_right);
        tvHitsCenter = $(headView, R.id.tv_hits_center);
        tvHitsLeft = $(headView, R.id.tv_hits_left);
        tvHitsRight = $(headView, R.id.tv_hits_right);
        headView.setVisibility(View.GONE);
    }

    @Override
    public void setListener() {
        stackLabel1.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "1***选中===" + s + "----" + index);
            if (index > 0) {
                if (datas != null && datas.size() >= index) {
                    paramSubCatID = datas.get(index - 1).getId();
                } else {
                    paramSubCatID = 0;
                }
            } else {
                paramSubCatID = 0;
            }
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            paramCurPage = 0;
            reqCategoryDatas(true);
        });

        stackLabel2.setOnLabelClickListener((index, v, s) -> {
//            Log.e(TAG, "2***选中===" + s + "----" + index);
            paramWordsNum = index;
            paramCurPage = 0;
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            reqCategoryDatas(true);
        });
        stackLabel3.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "3***选中===" + s + "----" + index);
            paramIsEnd = index;
            paramCurPage = 0;
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            reqCategoryDatas(true);
        });

        mRefreshLayout.setOnRefreshListener(this);
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);
        ivTop3Center.setOnClickListener(this);
        ivTop3Left.setOnClickListener(this);
        ivTop3Right.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        reqSubCategory();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInit) {
            isInit = false;
            reqCategoryDatas(true);
            //TODO初始化数据
        }
    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (stackLabel1.getLabels().size() <= 1) {
            reqSubCategory();
        }
        paramCurPage = 0;
        reqCategoryDatas(false);
    }


    @Override
    public void onLoadMore() {
        reqCategoryDatas(false);
    }

    @Override
    public void onClick(View v) {
        if (onMoreClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_top3_bookCover_first:
            case R.id.iv_top3_bookCover_second:
            case R.id.iv_top3_bookCover_third:
                String bookId = (String) v.getTag();
                Intent intent = new Intent(mActivity, BookDetailsActivity.class);
                intent.putExtra(Common.KEY_BOOK_ID, bookId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BookEntity entity = mAdapter.getData().get(position);
        Intent intent = new Intent(mActivity, BookDetailsActivity.class);
        intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
        startActivity(intent);
    }

    /**
     * 查询分类下的书籍数据
     *
     * @param showDialog
     */
    private void reqCategoryDatas(boolean showDialog) {
        OkGo.<String>post(Consts.NOVEL_CHOICE_SUPERIOR_API)
                .params(Consts.CAT_BOY_GIRL, mType)
                .params(Consts.CAT_SECOND_CLASSIFY_ID, paramSubCatID)
                .params(Consts.CAT_IS_END, paramIsEnd)
                .params(Consts.CAT_WORD_NUM, paramWordsNum)
                .params(Consts.PAGE, paramCurPage + 1)
                .execute(new LtbCallback((AppCompatActivity) mActivity, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<BookEntity>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<BookEntity>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<BookEntity>> data = entity.getData();
                            paramCurPage = data.getCurrent_page();
                            int lastPage = data.getLast_page();
                            List<BookEntity> bookList = data.getData();
                            if (paramCurPage == 1) {
                                int size = bookList.size();
                                if (size >= 3) {
                                    mAdapter.setList(bookList.subList(3, size));

                                    headView.setVisibility(View.VISIBLE);
                                    ivFirstSign.setVisibility(View.VISIBLE);
                                    ivSecondSign.setVisibility(View.VISIBLE);
                                    ivThirdSign.setVisibility(View.VISIBLE);
                                    ivTop3Center.setVisibility(View.VISIBLE);
                                    ivTop3Left.setVisibility(View.VISIBLE);
                                    ivTop3Right.setVisibility(View.VISIBLE);
                                    flTop1.setVisibility(View.VISIBLE);
                                    flTop2.setVisibility(View.VISIBLE);
                                    flTop3.setVisibility(View.VISIBLE);
                                    tvTop3BookNameCenter.setVisibility(View.VISIBLE);
                                    tvTop3BookNameLeft.setVisibility(View.VISIBLE);
                                    tvTop3BookNameRight.setVisibility(View.VISIBLE);
                                    flContentCenter.setVisibility(View.VISIBLE);
                                    flContentLeft.setVisibility(View.VISIBLE);
                                    flContentRight.setVisibility(View.VISIBLE);

                                    showBookTop1(bookList);
                                    showBookTop2(bookList);
                                    showBookTop3(bookList);
                                } else {
                                    mAdapter.setList(null);
                                    if (size == 1) {
                                        headView.setVisibility(View.VISIBLE);
                                        ivFirstSign.setVisibility(View.VISIBLE);
                                        ivSecondSign.setVisibility(View.INVISIBLE);
                                        ivThirdSign.setVisibility(View.INVISIBLE);
                                        ivTop3Center.setVisibility(View.VISIBLE);
                                        ivTop3Left.setVisibility(View.INVISIBLE);
                                        ivTop3Right.setVisibility(View.INVISIBLE);
                                        flTop1.setVisibility(View.VISIBLE);
                                        flTop2.setVisibility(View.INVISIBLE);
                                        flTop3.setVisibility(View.INVISIBLE);
                                        tvTop3BookNameCenter.setVisibility(View.VISIBLE);
                                        tvTop3BookNameLeft.setVisibility(View.INVISIBLE);
                                        tvTop3BookNameRight.setVisibility(View.INVISIBLE);
                                        flContentCenter.setVisibility(View.VISIBLE);
                                        flContentLeft.setVisibility(View.INVISIBLE);
                                        flContentRight.setVisibility(View.INVISIBLE);

                                        showBookTop1(bookList);
                                    } else if (size == 2) {
                                        headView.setVisibility(View.VISIBLE);
                                        ivFirstSign.setVisibility(View.VISIBLE);
                                        ivSecondSign.setVisibility(View.VISIBLE);
                                        ivThirdSign.setVisibility(View.INVISIBLE);
                                        ivTop3Center.setVisibility(View.VISIBLE);
                                        ivTop3Left.setVisibility(View.VISIBLE);
                                        ivTop3Right.setVisibility(View.INVISIBLE);
                                        flTop1.setVisibility(View.VISIBLE);
                                        flTop2.setVisibility(View.VISIBLE);
                                        flTop3.setVisibility(View.INVISIBLE);
                                        tvTop3BookNameCenter.setVisibility(View.VISIBLE);
                                        tvTop3BookNameLeft.setVisibility(View.VISIBLE);
                                        tvTop3BookNameRight.setVisibility(View.INVISIBLE);
                                        flContentCenter.setVisibility(View.VISIBLE);
                                        flContentLeft.setVisibility(View.VISIBLE);
                                        flContentRight.setVisibility(View.INVISIBLE);

                                        showBookTop1(bookList);
                                        showBookTop2(bookList);
                                    } else {
                                        headView.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                mAdapter.addData(bookList);
                            }

                            if (lastPage <= paramCurPage) {
                                //没有下一页
                                mAdapter.getLoadMoreModule().loadMoreEnd();
                            } else {
                                mAdapter.getLoadMoreModule().loadMoreComplete();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mAdapter.getLoadMoreModule().loadMoreFail();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }
                });
    }

    private void showBookTop1(List<BookEntity> bookList) {
        BookEntity book = bookList.get(0);
        ivTop3Center.setTag(book.getId());
        GlideUtil.loadRoundRect(mActivity, ivTop3Center, book.getHttp_image());
        tvTop3BookNameCenter.setText(book.getName());
        tvHitsCenter.setText(FigureProcessor.formatGreet(mActivity, book.getGreet()));
    }

    private void showBookTop2(List<BookEntity> bookList) {
        BookEntity book = bookList.get(1);
        ivTop3Left.setTag(book.getId());
        GlideUtil.loadRoundRect(mActivity, ivTop3Left, book.getHttp_image());
        tvTop3BookNameLeft.setText(book.getName());
        tvHitsLeft.setText(FigureProcessor.formatGreet(mActivity, book.getGreet()));
    }

    private void showBookTop3(List<BookEntity> bookList) {
        BookEntity book = bookList.get(2);
        ivTop3Right.setTag(book.getId());
        GlideUtil.loadRoundRect(mActivity, ivTop3Right, book.getHttp_image());
        tvTop3BookNameRight.setText(book.getName());
        tvHitsRight.setText(FigureProcessor.formatGreet(mActivity, book.getGreet()));
    }


    /**
     * 男1 女2
     */
    private void reqSubCategory() {
        OkGo.<LzyResponse<List<CategoryEntity>>>post(Consts.NOVEL_CATEGORY_SUB_API)
                .params(Consts.CAT_PID, index + 1)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheKey(Consts.NOVEL_CATEGORY_SUB_API + "/gender_" + (index + 1))
                .execute(new LtbJsonCallback<LzyResponse<List<CategoryEntity>>>((AppCompatActivity) mActivity, false,
                        new TypeReference<LzyResponse<List<CategoryEntity>>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<List<CategoryEntity>>> response) {
                        LzyResponse<List<CategoryEntity>> entity = response.body();

                        // 分类栏
                        if (entity.error_code == 0) {
                            datas = entity.getData();
                            for (CategoryEntity ce : datas) {
                                stack1Datas.add(ce.getName());
                            }
                            stackLabel1.setLabels(stack1Datas);
                            stackLabel1.setSelectMode(true, stack1Datas.subList(0, 1));
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<LzyResponse<List<CategoryEntity>>> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }
                });
    }

}
