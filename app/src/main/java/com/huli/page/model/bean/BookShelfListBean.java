package com.huli.page.model.bean;


import com.huli.foxread.FrApp;
import com.huli.page.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

public class BookShelfListBean implements Serializable {
    private static final long serialVersionUID = 56423411313L;

    private String id;
    private String novel_id;//小说ID
    private String user_id;
    private String novel_name;//书名
    private String novel_image;
    private String http_novel_image;//封面图
    private String author;

    private List<String> tag;
    private String copyright_name;

    /**************************************************************/
    private float score;                    //评分
    private String file;                    //小说下载链接
    private int read_sum;                   //阅读次数
    private int is_end;                     //2未完结，1已完结
    private int type;                       //类型:1=男生,2=女生,3=图书
    private int classify_id;                //分类ID
    private String classify_name;           //分类名
    private int greet;                      //人气值
    private int word;                       //小说字说
    private int is_new;
    private int is_hot;
    private int is_copyright;               //2无版权，1有版权
    private int reading_size;               //在读人数
    private String introduce;               //简介
    private String http_image;              //封面图
    private int chapter_sum;
    private int is_exist_bookshelf;         //是否加入书架 2：否  1：是
    private int status;                     //1隐藏，2未隐藏，3有更新
    /******************************************************************/
    private long createtime;
    private long updatetime;
    private long deletetime;
    //最新阅读日期
    private String lastRead;
    private String chapter_name;
    //是否更新或未阅读lastChapter
    private boolean isUpdate = true;
    //是否是本地文件
    private boolean isLocal = false;
    private List<BookChapter> bookChapterList;

    public void setBookChapters(List<BookChapter> beans) {
        bookChapterList = beans;
        for (BookChapter bean : bookChapterList) {
            bean.setBookId(getNovel_id());
        }
    }

    public List<BookChapter> getBookChapters() {
        return bookChapterList;
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
        return StringUtils.convertCC(novel_name, FrApp.getInstance());
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
        return StringUtils.convertCC(author, FrApp.getInstance());
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
        return StringUtils.convertCC(copyright_name, FrApp.getInstance());
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
        return StringUtils.convertCC(classify_name, FrApp.getInstance());
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

    public int getWord() {
        return this.word;
    }

    public void setWord(int word) {
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

    public int getIs_copyright() {
        return this.is_copyright;
    }

    public void setIs_copyright(int is_copyright) {
        this.is_copyright = is_copyright;
    }

    public int getReading_size() {
        return this.reading_size;
    }

    public void setReading_size(int reading_size) {
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
        return StringUtils.convertCC(lastRead, FrApp.getInstance());
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }

    public String getLastChapter() {
        return StringUtils.convertCC(chapter_name, FrApp.getInstance());
    }

    public void setLastChapter(String lastChapter) {
        this.chapter_name = lastChapter;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChapter_name() {
        return this.chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

}