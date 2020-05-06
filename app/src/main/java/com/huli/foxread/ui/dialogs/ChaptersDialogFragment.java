package com.huli.foxread.ui.dialogs;

import android.os.Bundle;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.ChapterAdapter;
import com.huli.foxread.ui.decoration.SimpleDividerDecoration;
import com.huli.foxread.ui.dialogs.base.BaseBottomSheetDialogFragment;
import com.huli.page.model.bean.BookChapter;

import java.io.Serializable;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * BookDetails---章节目录
 */
public class ChaptersDialogFragment extends BaseBottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private ChapterAdapter mAdapter;
    private onClickListener listener;

    public static ChaptersDialogFragment newInstance(List<BookChapter> data) {
        ChaptersDialogFragment fragment = new ChaptersDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("datas", (Serializable) data);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void OnStateChange(View view, int i) {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.custom_bottom_popup;
    }

    @Override
    protected boolean isTransparent() {
        return true;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        List<BookChapter> data = (List<BookChapter>) bundle.getSerializable("datas");

        recyclerView = mRootView.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SimpleDividerDecoration(getContext(), R.dimen.dp_1));
        mAdapter = new ChapterAdapter(data);
        mAdapter.setAnimationEnable(true);
        mAdapter.setAnimationFirstOnly(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (listener != null) {
                listener.onButtonClick(position);
            }
            dismiss();
        });
    }


    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public interface onClickListener {
        void onButtonClick(int num);
    }
}