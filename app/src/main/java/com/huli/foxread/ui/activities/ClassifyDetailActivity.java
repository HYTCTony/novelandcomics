package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.adapters.BooksListAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.GlideUtil;
import com.kongzue.stacklabelview.StackLabel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClassifyDetailActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BooksListAdapter mAdapter;

    private StackLabel stackLabel_1, stackLabel_2, stackLabel_3, stackLabel_4;
    private TextView btnMoreFilter;
    private View layoutMoreFilter;

    private View headViewTop3;
    private TextView tvClassifyTop3Title;
    private ImageView ivCoverFirst, ivCoverSecond, ivCoverThird;
    private TextView tvBookNameFirst, tvBookNameSecond, tvBookNameThird;


    private int catId;
    private String mTitle;

    private int paramIsEnd = 0;
    private int paramWordsNum = 0;
    private int paramStatus = 1;
    private int paramCurPage = 0;

    @Override
    public void initParms(Bundle parms) {
        catId = parms.getInt(Common.KEY_CAT_ID);
        mTitle = parms.getString(Common.KEY_CAT_TITLE);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_classify_detail;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, mTitle);

        recyclerView = $(R.id.recyclerView_classify_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BooksListAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(this);
        View headViewFilter = inflater.inflate(R.layout.layout_rv_head_classify_detail_filter, recyclerView, false);
        headViewTop3 = inflater.inflate(R.layout.layout_rv_head_classify_detail_top3, recyclerView, false);
        mAdapter.addHeaderView(headViewFilter, 0);

        stackLabel_1 = headViewFilter.findViewById(R.id.stackLabelView_filter_1);
        stackLabel_1.setVisibility(View.GONE);
        stackLabel_2 = headViewFilter.findViewById(R.id.stackLabelView_filter_2);
        stackLabel_3 = headViewFilter.findViewById(R.id.stackLabelView_filter_3);
        stackLabel_4 = headViewFilter.findViewById(R.id.stackLabelView_filter_4);
        btnMoreFilter = headViewFilter.findViewById(R.id.tv_asBtn_more_filter);
        layoutMoreFilter = headViewFilter.findViewById(R.id.ll_more_filter_layout);

        tvClassifyTop3Title = headViewTop3.findViewById(R.id.tv_classify_top3_title);
        ivCoverFirst = headViewTop3.findViewById(R.id.iv_top3_bookCover_first);
        ivCoverSecond = headViewTop3.findViewById(R.id.iv_top3_bookCover_second);
        ivCoverThird = headViewTop3.findViewById(R.id.iv_top3_bookCover_third);
        tvBookNameFirst = headViewTop3.findViewById(R.id.tv_top3_bookName_first);
        tvBookNameSecond = headViewTop3.findViewById(R.id.tv_top3_bookName_second);
        tvBookNameThird = headViewTop3.findViewById(R.id.tv_top3_bookName_third);

        List<String> stack2Datas = Arrays.asList(getResources().getStringArray(R.array.cat_is_end));
        List<String> stack3Datas = Arrays.asList(getResources().getStringArray(R.array.cat_word_num));
        List<String> stack4Datas = Arrays.asList(getResources().getStringArray(R.array.cat_state));
        stackLabel_2.setLabels(stack2Datas);
        stackLabel_3.setLabels(stack3Datas);
        stackLabel_4.setLabels(stack4Datas);
        stackLabel_2.setSelectMode(true, stack2Datas.subList(0, 1));
        stackLabel_3.setSelectMode(true, stack3Datas.subList(0, 1));
        stackLabel_4.setSelectMode(true, stack4Datas.subList(0, 1));
        tvClassifyTop3Title.setText(String.format(getString(R.string.txt_category_dt_sub_title), mTitle, stack4Datas.subList(0, 1)));

    }

    @Override
    public void setListener() {
        btnMoreFilter.setOnClickListener(view -> {
            btnMoreFilter.setVisibility(View.GONE);
            layoutMoreFilter.setVisibility(View.VISIBLE);
        });

        stackLabel_1.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "1***选中===" + s + "----" + index);
        });
        stackLabel_2.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "2***选中===" + s + "----" + index);
            paramIsEnd = index;
            paramCurPage = 0;
            reqCategoryDatas(false);
        });
        stackLabel_3.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "3***选中===" + s + "----" + index);
            paramWordsNum = index;
            paramCurPage = 0;
            reqCategoryDatas(false);
        });
        stackLabel_4.setOnLabelClickListener((index, v, s) -> {
//                Log.e(TAG, "4***选中===" + s + "----" + index);
            tvClassifyTop3Title.setText(String.format(getString(R.string.txt_category_dt_sub_title), mTitle, s));

            paramStatus = index + 1;
            paramCurPage = 0;
            reqCategoryDatas(false);
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        List<BookEntity> list = new ArrayList<>();
        BookEntity data;
        for (int i = 0; i < 10; i++) {
            data = new BookEntity();
            data.setId("" + i);
            list.add(data);
        }

//        stackLabel_1.setLabels(new String[]{"全部", "总裁豪门", "都市人生", "婚恋爱情", "职场情缘", "民国旧影", "娱乐明星"});
//        stackLabel_2.setLabels(getResources().getStringArray(R.array.cat_is_end));
//        stackLabel_3.setLabels(new String[]{"全部", "100万字以下", "100-200万字", "200-300万字", "300万字以上"});
//        stackLabel_3.setLabels(getResources().getStringArray(R.array.cat_word_num));
//        stackLabel_4.setLabels(getResources().getStringArray(R.array.cat_state));

//        List<String> selected = Collections.singletonList("全部");
//        List<String> selected2 = Collections.singletonList("最热");
//        stackLabel_1.setSelectMode(true, selected);

//        tvClassifyTop3Title.setText(String.format(getString(R.string.txt_category_dt_sub_title), mTitle, "最热"));
//        GlideUtil.loadRoundRect(this, ivCoverFirst, "url", 0);
//        GlideUtil.loadRoundRect(this, ivCoverSecond, "url", 0);
//        GlideUtil.loadRoundRect(this, ivCoverThird, "url", 0);
//        tvBookNameFirst.setText("豪门千金的超级战神");
//        tvBookNameSecond.setText("天启时代");
//        tvBookNameThird.setText("傲娇总裁侨萌妻");



        reqCategoryDatas(true);
    }


    private void reqCategoryDatas(boolean showDialog) {
        OkGo.<String>post(Consts.NOVEL_CHOICE_API)
                .params(Consts.CAT_ID, catId)
                .params(Consts.CAT_IS_END, paramIsEnd)
                .params(Consts.CAT_WORD_NUM, paramWordsNum)
                .params(Consts.CAT_STATUS, paramStatus)
                .params(Consts.PAGE, paramCurPage + 1)
                .execute(new LtbCallback(this, showDialog) {
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
                                    mAdapter.setHeaderView(headViewTop3, 1);
                                    BookEntity book1 = bookList.get(0);
                                    BookEntity book2 = bookList.get(1);
                                    BookEntity book3 = bookList.get(2);
                                    GlideUtil.loadRoundRect(ClassifyDetailActivity.this, ivCoverFirst, book1.getHttp_image(), 0);
                                    GlideUtil.loadRoundRect(ClassifyDetailActivity.this, ivCoverSecond, book2.getHttp_image(), 0);
                                    GlideUtil.loadRoundRect(ClassifyDetailActivity.this, ivCoverThird, book3.getHttp_image(), 0);
                                    tvBookNameFirst.setText(book1.getName());
                                    tvBookNameSecond.setText(book2.getName());
                                    tvBookNameThird.setText(book3.getName());
                                    mAdapter.setNewData(bookList.subList(3, size));
                                } else {
                                    mAdapter.removeHeaderView(headViewTop3);
                                    mAdapter.setNewData(bookList);
                                }
                            } else {
                                mAdapter.addData(bookList);
                            }

                            if (lastPage == 0) {
                                //TODO 没有下一页
                            }
                        }
                    }
                });
    }


}
