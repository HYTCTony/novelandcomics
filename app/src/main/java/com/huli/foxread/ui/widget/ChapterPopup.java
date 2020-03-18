package com.huli.foxread.ui.widget;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.ChapterAdapter;
import com.huli.page.model.bean.BookChapter;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.VerticalRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

public class ChapterPopup extends BottomPopupView {
    VerticalRecyclerView recyclerView;
    AppCompatTextView tvCommentsNum;
    ChapterAdapter mAdapter;
    List<BookChapter> data = new ArrayList<>();
    private onClickListener listener;

    public ChapterPopup(@NonNull Context context, List<BookChapter> data) {
        super(context);
        this.data.addAll(data);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_bottom_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.rv);
        mAdapter = new ChapterAdapter(data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                listener.onButtonClick(position);
                onDismiss();
            }
        });
    }

    //完全可见执行
    @Override
    protected void onShow() {
        super.onShow();
    }

    //完全消失执行
    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext()) * .85f);
    }

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public interface onClickListener {
        void onButtonClick(int num);
    }
}