package com.huli.page.model.bean;


import com.huli.page.model.dao.BookChapterDao;
import com.huli.page.model.dao.BookShelfListBeanDao;
import com.huli.page.model.dao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

@Entity
public class BookShelfListBean implements Serializable {
    private static final long serialVersionUID = 56423411313L;

    @Id
    private String id;
    private String novel_id;//小说ID
    private String user_id;
    private String novel_name;//书名
    private String novel_image;
    private String http_novel_image;
    private String author;

    @Transient
    private List<String> tag;
    @Transient
    private String copyright_name;

    /**************************************************************/
    private float score;                    //评分
    private String file;                    //小说下载链接
    private int read_sum;                   //阅读次数（万）
    private int is_end;                     //0未完结，1已完结
    private int type;                       //类型:1=男生,2=女生,3=图书
    private int classify_id;                //分类ID
    private String classify_name;           //分类名
    private int greet;                      //人气值,单位:万
    private float word;                     //小说字说(万)
    private int is_new;
    private int is_hot;
    private float reading_size;             //在读人数（万）
    private String introduce;
    private String http_image;
    private int chapter_sum;
    private int is_exist_bookshelf;      //是否加入书架 0：否  1：是
    /******************************************************************/
    public long createtime;
    public long updatetime;
    public long deletetime;
    //最新阅读日期
    private String lastRead;
    private String lastChapter;
    //是否更新或未阅读
    private boolean isUpdate = true;
    //是否是本地文件
    private boolean isLocal = false;
    @ToMany(referencedJoinProperty = "bookId")
    private List<BookChapter> bookChapterList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 314701721)
    private transient BookShelfListBeanDao myDao;


    @Generated(hash = 394971066)
    public BookShelfListBean() {
    }


    @Generated(hash = 730251230)
    public BookShelfListBean(String id, String novel_id, String user_id, String novel_name,
                             String novel_image, String http_novel_image, String author, float score,
                             String file, int read_sum, int is_end, int type, int classify_id,
                             String classify_name, int greet, float word, int is_new, int is_hot,
                             float reading_size, String introduce, String http_image, int chapter_sum,
                             int is_exist_bookshelf, long createtime, long updatetime, long deletetime,
                             String lastRead, String lastChapter, boolean isUpdate, boolean isLocal) {
        this.id = id;
        this.novel_id = novel_id;
        this.user_id = user_id;
        this.novel_name = novel_name;
        this.novel_image = novel_image;
        this.http_novel_image = http_novel_image;
        this.author = author;
        this.score = score;
        this.file = file;
        this.read_sum = read_sum;
        this.is_end = is_end;
        this.type = type;
        this.classify_id = classify_id;
        this.classify_name = classify_name;
        this.greet = greet;
        this.word = word;
        this.is_new = is_new;
        this.is_hot = is_hot;
        this.reading_size = reading_size;
        this.introduce = introduce;
        this.http_image = http_image;
        this.chapter_sum = chapter_sum;
        this.is_exist_bookshelf = is_exist_bookshelf;
        this.createtime = createtime;
        this.updatetime = updatetime;
        this.deletetime = deletetime;
        this.lastRead = lastRead;
        this.lastChapter = lastChapter;
        this.isUpdate = isUpdate;
        this.isLocal = isLocal;
    }


    public void setBookChapters(List<BookChapter> beans) {
        bookChapterList = beans;
        for (BookChapter bean : bookChapterList) {
            bean.setBookId(getNovel_id());
        }
    }

    public List<BookChapter> getBookChapters() {
        if (daoSession == null) {
            return bookChapterList;
        } else {
            return getBookChapterList();
        }
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNovel_id() {
        return this.novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNovel_name() {
        return this.novel_name;
    }

    public void setNovel_name(String novel_name) {
        this.novel_name = novel_name;
    }

    public String getNovel_image() {
        return this.novel_image;
    }

    public void setNovel_image(String novel_image) {
        this.novel_image = novel_image;
    }

    public String getHttp_novel_image() {
        return this.http_novel_image;
    }

    public void setHttp_novel_image(String http_novel_image) {
        this.http_novel_image = http_novel_image;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getCopyright_name() {
        return copyright_name;
    }

    public void setCopyright_name(String copyright_name) {
        this.copyright_name = copyright_name;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getRead_sum() {
        return this.read_sum;
    }

    public void setRead_sum(int read_sum) {
        this.read_sum = read_sum;
    }

    public int getIs_end() {
        return this.is_end;
    }

    public void setIs_end(int is_end) {
        this.is_end = is_end;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getClassify_id() {
        return this.classify_id;
    }

    public void setClassify_id(int classify_id) {
        this.classify_id = classify_id;
    }

    public String getClassify_name() {
        return this.classify_name;
    }

    public void setClassify_name(String classify_name) {
        this.classify_name = classify_name;
    }

    public int getGreet() {
        return this.greet;
    }

    public void setGreet(int greet) {
        this.greet = greet;
    }

    public float getWord() {
        return this.word;
    }

    public void setWord(float word) {
        this.word = word;
    }

    public int getIs_new() {
        return this.is_new;
    }

    public void setIs_new(int is_new) {
        this.is_new = is_new;
    }

    public int getIs_hot() {
        return this.is_hot;
    }

    public void setIs_hot(int is_hot) {
        this.is_hot = is_hot;
    }

    public float getReading_size() {
        return this.reading_size;
    }

    public void setReading_size(float reading_size) {
        this.reading_size = reading_size;
    }

    public String getIntroduce() {
        return this.introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getHttp_image() {
        return this.http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }

    public int getChapter_sum() {
        return this.chapter_sum;
    }

    public void setChapter_sum(int chapter_sum) {
        this.chapter_sum = chapter_sum;
    }

    public long getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getUpdatetime() {
        return this.updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public long getDeletetime() {
        return this.deletetime;
    }

    public void setDeletetime(long deletetime) {
        this.deletetime = deletetime;
    }

    public String getLastRead() {
        return this.lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }

    public String getLastChapter() {
        return this.lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public boolean getIsUpdate() {
        return this.isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public boolean getIsLocal() {
        return this.isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    public int getIs_exist_bookshelf() {
        return this.is_exist_bookshelf;
    }

    public void setIs_exist_bookshelf(int is_exist_bookshelf) {
        this.is_exist_bookshelf = is_exist_bookshelf;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1897639194)
    public List<BookChapter> getBookChapterList() {
        if (bookChapterList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookChapterDao targetDao = daoSession.getBookChapterDao();
            List<BookChapter> bookChapterListNew = targetDao
                    ._queryBookShelfListBean_BookChapterList(id);
            synchronized (this) {
                if (bookChapterList == null) {
                    bookChapterList = bookChapterListNew;
                }
            }
        }
        return bookChapterList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1077762221)
    public synchronized void resetBookChapterList() {
        bookChapterList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1425417835)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBookShelfListBeanDao() : null;
    }


}