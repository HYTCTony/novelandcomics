package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.adapters.SectionNeBookAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewBooksActivity extends BaseActivity implements OnItemClickListener {

    private ImageView btnSearch;
    private RecyclerView recyclerView;
    private SectionNeBookAdapter mAdapter;

    private int mType;

    @Override
    public void initParms(Bundle parms) {
        mType = parms.getInt(Consts.TYPE);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_new_books;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_new_book);

        btnSearch = $(R.id.iv_asBtn_search);
        recyclerView = $(R.id.recyclerView_new_book);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SectionNeBookAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Intent intent = new Intent(NewBooksActivity.this, SearchBookActivity.class);
                startActivity(intent);
            }
        });
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        String url;
        switch (mType) {
            case Consts.TYPE_BOY:
                url = Consts.NOVEL_COLUMN_BOY_API;
                break;
            case Consts.TYPE_GIRL:
                url = Consts.NOVEL_COLUMN_GIRL_API;
                break;
            case Consts.TYPE_SELECTION:
                url = Consts.NOVEL_COLUMN_SELECTED_API;
                break;
            default:
                url = Consts.NOVEL_COLUMN_BOY_API;
                break;
        }
        reqFindNewBooks(url);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (onMoreClick()) {
            return;
        }
        NEbookSection<BookEntity> nEbookSection = mAdapter.getData().get(position);
        BookEntity book = nEbookSection.getObject();
        if (book != null) {
            Intent intent = new Intent(this, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, book.getId());
            startActivity(intent);
        }
    }

    /**
     * 获取新书
     *
     * @param url
     */
    private void reqFindNewBooks(String url) {
        OkGo.<String>get(url)
                .params(Consts.TYPE, Consts.TYPE_NEWBOOK)
                .execute(new LtbCallback(this, false) {
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
