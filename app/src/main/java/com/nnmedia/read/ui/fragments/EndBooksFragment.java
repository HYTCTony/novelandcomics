package com.nnmedia.read.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.entity.EndNewBookGroupEntity;
import com.nnmedia.read.entity.sections.NEbookSection;
import com.nnmedia.read.ui.activities.BookDetailsActivity;
import com.nnmedia.read.ui.activities.MoreBooksListActivity;
import com.nnmedia.read.ui.adapters.SectionNeBookAdapter;
import com.nnmedia.read.ui.base.BaseFragment;
import com.rxjava.rxlife.RxLife;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EndBooksFragment extends BaseFragment implements OnItemClickListener, OnItemChildClickListener {

    private RecyclerView recyclerView;
    private SectionNeBookAdapter mAdapter;

    private int mType;

    public static EndBooksFragment newInstance(int type, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.TYPE, type);
        bundle.putInt("index", index);
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
        Bundle bundle = getArguments();
        int index = bundle.getInt("index");
        // 这个设置tag要与FragmentPagerAdapter中的获取方法getItemPosition方法要对应上
        view.setTag(index);

        recyclerView = $(view, R.id.recyclerView_end_book);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new SectionNeBookAdapter();
        mAdapter.setAnimationEnable(true);
        mAdapter.setAnimationFirstOnly(false);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        mType = getArguments().getInt(Consts.TYPE);

        String url;
        if (mType == Consts.TYPE_BOY) {
            url = Consts.NOVEL_COLUMN_BOY_API;
        } else {
            url = Consts.NOVEL_COLUMN_GIRL_API;
        }
        reqFindEndBooks(url);
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        if (onMoreClick()) {
            return;
        }
        NEbookSection<BookEntity> nEbookSection = mAdapter.getData().get(position);
        BookEntity book = nEbookSection.getObject();
        if (book != null) {
            Intent intent = new Intent(mActivity, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, book.getId());
            startActivity(intent);
        }
    }


    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        if (view.getId() == R.id.tv_asBtn_get_more) {
            NEbookSection<BookEntity> nbSection = mAdapter.getData().get(position);
            MoreBooksListActivity.start(mActivity, nbSection.getName(), nbSection.getId(), 0);
        }
    }


    /**
     * 获取完本书
     */
    private void reqFindEndBooks(String url) {
        RxHttp.get(url)
                .add(Consts.TYPE, Consts.TYPE_ENDBOOK)
                .asResponseList(EndNewBookGroupEntity.class)
                .doOnSubscribe(disposable -> {
                    showLoadingDialog();
                })
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(entityList -> {
                    List<NEbookSection<BookEntity>> datas = new ArrayList<>();
                    for (int i = 0; i < entityList.size(); i++) {
                        EndNewBookGroupEntity ebgEntity = entityList.get(i);
                        List<BookEntity> novels = ebgEntity.getNovelColumnAccess();
                        datas.add(new NEbookSection<>(true, true, ebgEntity.getId(), ebgEntity.getName(), null));
                        for (int j = 0; j < novels.size(); j++) {
                            datas.add(new NEbookSection<>(false, false, ebgEntity.getId(), ebgEntity.getName(), novels.get(j)));
                        }
                    }
                    mAdapter.setList(datas);
                });
    }
}
