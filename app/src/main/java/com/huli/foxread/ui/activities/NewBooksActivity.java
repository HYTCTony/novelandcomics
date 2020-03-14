package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.HpBGPraiseNvET;
import com.huli.foxread.entity.NEbookSection;
import com.huli.foxread.entity.NbSection;
import com.huli.foxread.entity.NewBookEntity;
import com.huli.foxread.ui.adapters.SectionNewBookAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewBooksActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SectionNewBookAdapter mAdapter;

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

        recyclerView = $(R.id.recyclerView_new_book);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new SectionNewBookAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
//        List<NbSection<NewBookEntity>> choiSections = initDatas();
//        mAdapter.setNewData(choiSections);

        String url;
        switch (mType) {
            case Consts.TYPE_BOY:
                url = Consts.NOVEL_COLUMN_BOYNEW_API;
                break;
            case Consts.TYPE_GIRL:
                url = Consts.NOVEL_COLUMN_GIRLNEW_API;
                break;
            case Consts.TYPE_SELECTION:
                url = Consts.NOVEL_COLUMN_SELECTIONNEW_API;
                break;
            case Consts.TYPE_LIBRARY:
            default:
                url = Consts.NOVEL_COLUMN_LIBNEW_API;
                break;
        }
        reqFindNewBooks(url);
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
     * 获取新书
     *
     * @param url
     */
    private void reqFindNewBooks(String url) {
        OkGo.<String>get(url)
                .execute(new LtbCallback(this, false) {
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
                                list.add(new NEbookSection<>(true, true, hpBGPraiseNvET.getId(), hpBGPraiseNvET.getTheme_id(), hpBGPraiseNvET.getName(), null));
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
