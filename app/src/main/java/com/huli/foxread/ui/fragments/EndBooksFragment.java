package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.HpBGPraiseNvET;
import com.huli.foxread.entity.sections.NEbookSection;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.adapters.SectionNewBookAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EndBooksFragment extends BaseFragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private SectionNewBookAdapter mAdapter;

    private int mType;

    public static EndBooksFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.TYPE, type);
        EndBooksFragment frag = new EndBooksFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_end_books;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        recyclerView = $(view, R.id.recyclerView_end_book);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4));
        mAdapter = new SectionNewBookAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        mType = getArguments().getInt(Consts.TYPE);

//        List<NbSection<NewBookEntity>> choiSections = initDatas();
//        mAdapter.setNewData(choiSections);

        String url;
        if (mType == Consts.TYPE_BOY) {
            url = Consts.NOVEL_COLUMN_BOYEND_API;
        } else {
            url = Consts.NOVEL_COLUMN_GIRLEND_API;
        }
        reqFindEndBooks(url);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        NEbookSection<BookEntity> nEbookSection = mAdapter.getData().get(position);
        BookEntity book = nEbookSection.getObject();
        if (book != null) {
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, book.getId());
            startActivity(intent);
        }
    }


   /* private List<NbSection<NewBookEntity>> initDatas() {
        List<NbSection<NewBookEntity>> list = new ArrayList<>();

        list.add(new NbSection<>(true, false, "主编精选", null));
        for (int i = 0; i < 8; i++) {
            NewBookEntity newbook = new NewBookEntity();
            newbook.setCoverImgUrl("sssssssss");
            newbook.setTitle("无能狂怒" + "title" + i);
            list.add(new NbSection<>(false, "", newbook));
        }

        list.add(new NbSection<>(true, true, "上周更新", null));
        for (int i = 0; i < 8; i++) {
            NewBookEntity newbook = new NewBookEntity();
            newbook.setCoverImgUrl("sssssssss");
            newbook.setTitle("英文字幕英文字幕英文字幕英文字幕英文字幕" + "title" + i);
            list.add(new NbSection<>(false, "", newbook));
        }

        list.add(new NbSection<>(true, true, "现代萌宝", null));
        for (int i = 0; i < 8; i++) {
            NewBookEntity newbook = new NewBookEntity();
            newbook.setCoverImgUrl("sssssssss");
            newbook.setTitle("压脉带" + "title" + i);
            list.add(new NbSection<>(false, "", newbook));
        }

        list.add(new NbSection<>(true, true, "穿越架空", null));
        for (int i = 0; i < 8; i++) {
            NewBookEntity newbook = new NewBookEntity();
            newbook.setCoverImgUrl("sssssssss");
            newbook.setTitle("高能*******" + i);
            list.add(new NbSection<>(false, "", newbook));
        }
        return list;
    }*/

    /**
     * 获取完本书
     *
     * @param url
     */
    private void reqFindEndBooks(String url) {
        OkGo.<String>get(url)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<HpBGPraiseNvET>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<HpBGPraiseNvET>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<HpBGPraiseNvET> data = entity.getData();

                            List<NEbookSection<BookEntity>> list = new ArrayList<>();

                            for (int i = 0; i < data.size(); i++) {
                                HpBGPraiseNvET hpBGPraiseNvET = data.get(i);
                                List<BookEntity> novels = hpBGPraiseNvET.getNovel();
                                list.add(new NEbookSection<>(true, false, hpBGPraiseNvET.getId(), hpBGPraiseNvET.getTheme_id(), hpBGPraiseNvET.getName(), null));
                                for (int j = 0; j < novels.size(); j++) {
                                    list.add(new NEbookSection<>(false, novels.get(j)));
                                }
                            }
                            mAdapter.setNewData(list);
                        }
                    }
                });
    }
}
