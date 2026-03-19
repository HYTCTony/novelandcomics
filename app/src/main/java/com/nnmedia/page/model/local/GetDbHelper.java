package com.nnmedia.page.model.local;


import com.nnmedia.page.model.bean.DownloadTaskBean;

import java.util.List;

public interface GetDbHelper {
//    Single<List<BookCommentBean>> getBookComments(String block, String sort, int start, int limited, String distillate);
//    Single<List<BookHelpsBean>> getBookHelps(String sort, int start, int limited, String distillate);
//    Single<List<BookReviewBean>> getBookReviews(String sort, String bookType, int start, int limited, String distillate);
//    BookSortPackage getBookSortPackage();
//    BillboardPackage getBillboardPackage();
//
//    AuthorBean getAuthor(String id);
//    ReviewBookBean getReviewBook(String id);
//    BookHelpfulBean getBookHelpful(String id);

    /******************************/
    List<DownloadTaskBean> getDownloadTaskList();
}
