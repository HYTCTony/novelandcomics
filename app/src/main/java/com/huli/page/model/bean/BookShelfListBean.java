package com.huli.page.model.bean;


import com.huli.page.model.dao.BookChapterDao;
import com.huli.page.model.dao.BookShelfListBeanDao;
import com.huli.page.model.dao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.List;

@Entity
public class BookShelfListBean implements Serializable {
    private static final long serialVersionUID = 56423411313L;

    @Id
    public String id;
    public String novel_id;
    public String user_id;
    public String novel_name;
    public String novel_image;
    public String http_novel_image;
    public String author;
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

    @Generated(hash = 2143102112)
    public BookShelfListBean(String id, String novel_id, String user_id,
                             String novel_name, String novel_image, String http_novel_image,
                             String author, long createtime, long updatetime, long deletetime,
                             String lastRead, String lastChapter, boolean isUpdate,
                             boolean isLocal) {
        this.id = id;
        this.novel_id = novel_id;
        this.user_id = user_id;
        this.novel_name = novel_name;
        this.novel_image = novel_image;
        this.http_novel_image = http_novel_image;
        this.author = author;
        this.createtime = createtime;
        this.updatetime = updatetime;
        this.deletetime = deletetime;
        this.lastRead = lastRead;
        this.lastChapter = lastChapter;
        this.isUpdate = isUpdate;
        this.isLocal = isLocal;
    }

    @Generated(hash = 394971066)
    public BookShelfListBean() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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