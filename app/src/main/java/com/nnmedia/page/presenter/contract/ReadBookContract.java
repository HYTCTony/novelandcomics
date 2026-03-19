package com.nnmedia.page.presenter.contract;


import com.nnmedia.page.model.bean.Advert;
import com.nnmedia.page.model.bean.BookChapter;
import com.nnmedia.page.ui.base.BaseContract;
import com.nnmedia.page.widget.page.TxtChapter;
import com.nnmedia.read.entity.CapitalEntity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public interface ReadBookContract {
    interface View extends BaseContract.IBaseView {
        void reqAdvertAd(Advert data);

        void reqAddBookrack(String data);

        void showCategory(List<BookChapter> bookChapterList);

        void unlockChapter(CapitalEntity data);

        void finishChapter();

        void errorChapter();

        void errorUnlock();

    }

    interface Presenter extends BaseContract.IBasePresenter<View> {
        void reqAdvertAd(AppCompatActivity context);

        void reqAddBookrack(AppCompatActivity context, String novelId);

        void loadCategory(AppCompatActivity context, String bookId);

        void loadChapter(AppCompatActivity context, String bookId, List<TxtChapter> bookChapterList);

        void recordDuration(AppCompatActivity context, int type, long duration, String id, String check, int num);

        void recordRead(AppCompatActivity context, String bookId, String chapterId, String chapterName, int chapter);

        void unlockChapter(AppCompatActivity context, String bookId, String chapterId);
    }
}
