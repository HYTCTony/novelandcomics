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
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static com.huli.foxread.contact.Consts.NOT_CPL_URL;

public class ReadBookPresenter extends BasePresenter<ReadBookContract.View> implements ReadBookContract.Presenter {
    private static final String TAG = "ReadBookPresenter";

    @Override
    public void reqAddBookrack(AppCompatActivity context, String novelId) {
        OkGo.<String>post(Consts.BOOKRACK_ADD_API)
                .params(Consts.NOVEL_ID, novelId)
                .execute(new LtbCallback(context) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            view.reqAddBookrack("成功!");
                        } else {
                            view.onFailure(entity.error_code, entity.msg);
                        }
                    }
                });
    }

    @Override
    public void loadCategory(AppCompatActivity context, String bookId) {
        checkViewAttached();
        OkGo.<String>get(Consts.NOVEL_NOVELCHAPTERLIST_API)
                .params(Consts.NOVEL_ID, bookId)
                .cacheTime(8 * 60 * 60 * 1000)
                .cacheKey(Consts.NOVEL_NOVELCHAPTERLIST_API + bookId)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .execute(new LtbCallback(context, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (isViewAttached()) {
                            LzyResponse<List<BookChapter>> entity = JSONObject.parseObject(response.body(),
                                    new TypeReference<LzyResponse<List<BookChapter>>>() {
                                    });
                            if (entity.error_code == 0) {
                                view.showCategory(entity.getData());
                            } else {
                                view.onFailure(entity.error_code, entity.msg);
                            }
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
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

    @Override
    public void recordDuration(AppCompatActivity context) {
        checkViewAttached();
        OkGo.<String>get(Consts.RECORD_DURATION_API)
                .execute(new LtbCallback(context, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (isViewAttached()) {
                            LzyResponse<List<String>> entity = JSONObject.parseObject(response.body(),
                                    new TypeReference<LzyResponse<List<String>>>() {
                                    });
                            if (entity.error_code == 0) {
                                Log.d(TAG, "记录时间");
                            }
                        }
                    }
                });
    }

    @Override
    public void recordRead(AppCompatActivity context, String bookId, String chapterId, String chapterName, int chapter) {
        checkViewAttached();
        OkGo.<String>post(Consts.RECORD_CREATE_API)
                .params(Consts.BOOK_ID, bookId)
                .params(Consts.CHAPTER_ID, chapterId)
                .params(Consts.CHAPTER_NAME, chapterName)
                .params(Consts.CHAPTER, chapter)
                .execute(new LtbCallback(context, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (isViewAttached()) {
                            LzyResponse<List<String>> entity = JSONObject.parseObject(response.body(),
                                    new TypeReference<LzyResponse<List<String>>>() {
                                    });
                            if (entity.error_code == 0) {
                                Log.d(TAG, "提交阅读");
                            }
                        }
                    }
                });
    }
}