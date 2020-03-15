package com.huli.page.presenter;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.page.model.bean.BookChapter;
import com.huli.page.model.bean.ChapterBean;
import com.huli.page.model.local.BookRepository;
import com.huli.page.presenter.contract.ReadBookContract;
import com.huli.page.ui.base.BasePresenter;
import com.huli.page.widget.page.TxtChapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static com.huli.foxread.contact.Consts.NOT_CPL_URL;

public class ReadBookPresenter extends BasePresenter<ReadBookContract.View> implements ReadBookContract.Presenter {
    private static final String TAG = "ReadBookPresenter";

    @Override
    public void loadCategory(AppCompatActivity context, String bookId) {
        checkViewAttached();
        OkGo.<String>get(Consts.NOVEL_NOVELCHAPTERLIST_API)
                .params(Consts.NOVEL_ID, bookId)
                .execute(new LtbCallback(context, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (isViewAttached()) {
                            LzyResponse<List<BookChapter>> entity = JSONObject.parseObject(response.body(),
                                    new TypeReference<LzyResponse<List<BookChapter>>>() {
                                    });
                            if (entity.error_code == 0) {
                                //进行设定BookChapter所属的书的id。
                                for (BookChapter bookChapter : entity.getData()) {
//                            bookChapter.setId(MD5Utils.strToMd5By16(bookChapter.getLink()));
                                    bookChapter.setBookId(bookId);
                                }
                                view.showCategory(entity.getData());

                            } else {
                                view.onFailure(entity.error_code, entity.msg);
                            }
                        }
                    }
                });
    }

    @Override
    public void loadChapter(AppCompatActivity context, String bookId, List<TxtChapter> bookChapters) {
        checkViewAttached();
        int size = bookChapters.size();
        // 将要下载章节，转换成网络请求。
        for (int i = 0; i < size; ++i) {
            TxtChapter bookChapter = bookChapters.get(i);

            OkGo.<String>get(NOT_CPL_URL + bookChapter.getLink())
                    .execute(new LtbCallback(context, false) {
                        @Override
                        public void onSuccess(Response<String> response) {
                            if (isViewAttached()) {
                                LzyResponse<ChapterBean> entity = JSONObject.parseObject(response.body(),
                                        new TypeReference<LzyResponse<ChapterBean>>() {
                                        });
                                if (entity.error_code == 0) {
                                    //存储数据
                                    BookRepository.getInstance().saveChapterInfo(bookId, bookChapter.getTitle(), entity.getData().getContent());
                                    view.finishChapter();
                                } else {
                                    //只有第一个加载失败才会调用errorChapter
                                    if (bookChapters.get(0).getTitle().equals(bookChapter.getTitle())) {
                                        view.errorChapter();
                                    }
                                    Log.e(TAG, entity.msg);
                                    view.onFailure(entity.error_code, entity.msg);
                                }
                            }
                        }
                    });
        }

    }
}