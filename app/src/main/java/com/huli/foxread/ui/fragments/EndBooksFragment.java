package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.huli.foxread.entity.EndNewBookGroupEntity;
import com.huli.foxread.entity.sections.NEbookSection;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.adapters.SectionNeBookAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EndBooksFragment extends BaseFragment implements OnItemClickListener {

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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
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


    /**
     * 获取完本书
     *
     * @param url
     */
    private void reqFindEndBooks(String url) {
        OkGo.<String>get(url)
                .params(Consts.TYPE, Consts.TYPE_ENDBOOK)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<EndNewBookGroupEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<EndNewBookGroupEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<EndNewBookGroupEntity> data = entity.getData();

                            List<NEbookSection<BookEntity>> list = new ArrayList<>();

                            for (int i = 0; i < data.size(); i++) {
                                EndNewBookGroupEntity ebgEntity = data.get(i);
                                List<BookEntity> novels = ebgEntity.getNovelColumnAccess();
                                list.add(new NEbookSection<>(true, false, ebgEntity.getId(), ebgEntity.getName(), null));
                                for (int j = 0; j < novels.size(); j++) {
                                    list.add(new NEbookSection<>(false, false, ebgEntity.getId(), ebgEntity.getName(), novels.get(j)));
                                }
                            }
                            mAdapter.setNewData(list);
                        }
                    }
                });
    }
}
