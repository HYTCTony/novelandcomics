package com.huli.page.presenter.contract;


import com.huli.page.model.bean.BookChapter;
import com.huli.page.ui.base.BaseContract;
import com.huli.page.widget.page.TxtChapter;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public interface ReadBookContract {
    interface View extends BaseContract.IBaseView {
        void reqAddBookrack(String data);

        void showCategory(List<BookChapter> bookChapterList);

        void finishChapter();

        void errorChapter();
    }

    interface Presenter extends BaseContract.IBasePresenter<View> {
        void reqAddBookrack(AppCompatActivity context, String novelId);

        void loadCategory(AppCompatActivity context, String bookId);

        void loadChapter(AppCompatActivity context, String bookId, List<TxtChapter> bookChapterList);
    }
}
