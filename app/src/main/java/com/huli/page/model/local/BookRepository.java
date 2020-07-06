package com.huli.page.model.local;

import android.util.Log;

import com.huli.page.model.bean.BookChapter;
import com.huli.page.model.bean.BookRecordBean;
import com.huli.page.model.bean.BookShelfListBean;
import com.huli.page.model.dao.BookChapterDao;
import com.huli.page.model.dao.BookRecordBeanDao;
import com.huli.page.model.dao.DaoSession;
import com.huli.page.model.dao.DownloadTaskBeanDao;
import com.huli.page.utils.Constant;
import com.huli.page.utils.FileUtils;
import com.huli.page.utils.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;


/**
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节),BookRecord(记录))
 */
public class BookRepository {
    private static final String TAG = "CollBookManager";
    private static volatile BookRepository sInstance;
    private DaoSession mSession;
//    private BookShelfListBeanDao mBookShlefDao;

    private BookRepository() {
        mSession = DaoDbHelper.getInstance().getSession();
//        mBookShlefDao = mSession.getBookShelfListBeanDao();
    }

    public static BookRepository getInstance() {
        if (sInstance == null) {
            synchronized (BookRepository.class) {
                if (sInstance == null) {
                    sInstance = new BookRepository();
                }
            }
        }
        return sInstance;
    }

    //存储已收藏书籍
    public void saveBooksListWithAsync(BookShelfListBean bean) {
        //启动异步存储
        mSession.startAsyncSession()
                .runInTx(
                        () -> {
                            if (bean.getBookChapters() != null) {
                                // 存储BookChapterBean
                                mSession.getBookChapterDao()
                                        .insertOrReplaceInTx(bean.getBookChapters());
                            }
                            //存储CollBook (确保先后顺序，否则出错)
//                            mBookShlefDao.insertOrReplace(bean);
                        }
                );
    }

    /**
     * 异步存储。
     * 同时保存BookChapter
     *
     * @param beans
     */
    public void saveBooksListWithAsync(List<BookShelfListBean> beans) {
        mSession.startAsyncSession()
                .runInTx(
                        () -> {
                            for (BookShelfListBean bean : beans) {
                                if (bean.getBookChapters() != null) {
                                    //存储BookChapterBean(需要修改，如果存在id相同的则无视)
                                    mSession.getBookChapterDao()
                                            .insertOrReplaceInTx(bean.getBookChapters());
                                }
                            }
                            //存储CollBook (确保先后顺序，否则出错)
//                            mBookShlefDao.insertOrReplaceInTx(beans);
                        }
                );
    }

    public void saveBook(BookShelfListBean bean) {
//        mBookShlefDao.insertOrReplace(bean);
    }

    public void saveBooks(List<BookShelfListBean> beans) {
//        mBookShlefDao.insertOrReplaceInTx(beans);
    }

    /**
     * 异步存储BookChapter
     *
     * @param beans
     */
    public void saveBookChaptersToAsync(List<BookChapter> beans) {
        mSession.startAsyncSession()
                .runInTx(
                        () -> {
                            //存储BookChapterBean
                            mSession.getBookChapterDao().insertOrReplaceInTx(beans);
                            Log.d(TAG, "saveBookChaptersWithAsync: " + "进行存储");
                        }
                );
    }

    /**
     * 存储章节
     *
     * @param folderName
     * @param fileName
     * @param content
     */
    public void saveChapterInfo(String folderName, String fileName, String content) {
        File file = FileUtils.getBookFile(folderName, fileName);
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

    public void saveBookRecord(BookRecordBean bean) {
        mSession.getBookRecordBeanDao()
                .insertOrReplace(bean);
    }

    /*****************************get************************************************/
//    public BookShelfListBean getBookShelf(String bookId) {
//        BookShelfListBean bean = mBookShlefDao.queryBuilder()
//                .where(BookShelfListBeanDao.Properties.Novel_id.eq(bookId))
//                .unique();
//        return bean;
//    }

//    public List<BookShelfListBean> getBookShelfList() {
//        return mBookShlefDao
//                .queryBuilder()
//                .orderDesc(BookShelfListBeanDao.Properties.LastRead)
//                .list();
//    }

    //获取书籍列表
    public Single<List<BookChapter>> getBookChaptersFormRx(String bookId) {
        return Single.create(new SingleOnSubscribe<List<BookChapter>>() {
            @Override
            public void subscribe(SingleEmitter<List<BookChapter>> e) throws Exception {
                List<BookChapter> beans = mSession
                        .getBookChapterDao()
                        .queryBuilder()
                        .where(BookChapterDao.Properties.BookId.eq(bookId))
                        .list();
                e.onSuccess(beans);
            }
        });
    }

    //获取阅读记录
    public BookRecordBean getBookRecord(String bookId) {
        return mSession.getBookRecordBeanDao()
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(bookId))
                .unique();
    }

    //TODO:需要进行获取编码并转换的问题
//    public ChapterInfoBean getChapterInfoBean(String folderName, String fileName) {
//        File file = new File(Constant.BOOK_CACHE_PATH + folderName
//                + File.separator + fileName + FileUtils.SUFFIX_NB);
//        if (!file.exists()) return null;
//        Reader reader = null;
//        String str = null;
//        StringBuilder sb = new StringBuilder();
//        try {
//            reader = new FileReader(file);
//            BufferedReader br = new BufferedReader(reader);
//            while ((str = br.readLine()) != null) {
//                sb.append(str);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(reader);
//        }
//
//        ChapterInfoBean bean = new ChapterInfoBean();
//        bean.setTitle(fileName);
//        bean.setBody(sb.toString());
//        return bean;
//    }

    /************************************************************/

    /************************************************************/
    public Single<Void> deleteCollBookInRx(BookShelfListBean bean) {
        return Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> e) throws Exception {
                //查看文本中是否存在删除的数据
                deleteBook(bean.getNovel_id());
                //删除任务
                deleteDownloadTask(bean.getNovel_id());
                //删除目录
                deleteBookChapter(bean.getNovel_id());
                //删除CollBook
//                mBookShlefDao.delete(bean);
                e.onSuccess(new Void());
            }
        });
    }

    //这个需要用rx，进行删除
    public void deleteBookChapter(String bookId) {
        mSession.getBookChapterDao()
                .queryBuilder()
                .where(BookChapterDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

//    public void deleteCollBook(BookShelfListBean collBook) {
//        mBookShlefDao.delete(collBook);
//    }

    //删除书籍
    public void deleteBook(String bookId) {
        FileUtils.deleteFile(Constant.BOOK_CACHE_PATH + bookId);
    }

    public void deleteBookRecord(String id) {
        mSession.getBookRecordBeanDao()
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(id))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    //删除任务
    public void deleteDownloadTask(String bookId) {
        mSession.getDownloadTaskBeanDao()
                .queryBuilder()
                .where(DownloadTaskBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    public DaoSession getSession() {
        return mSession;
    }
}