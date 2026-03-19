package com.nnmedia.read.entity;

import java.io.Serializable;
import java.util.List;

public class BookDetailEntity implements Serializable {
    private String id;
    private String name;                    //书名
    private float score;                    //评分
    private String file;                    //小说下载链接
    private int read_sum;                   //阅读次数
    private int is_end;                     //0未完结，1已完结
    private int type;                       //类型:1=男生,2=女生,3=图书
    private int classify_id;                //分类ID
    private String classify_name;           //分类名
    private int greet;                      //人气值
    private String author;
    private int word;                     //小说字说
    private int is_new;
    private int is_hot;
    private int reading_size;             //在读人数
    private String introduce;
    private String http_image;
    private List<String> tag;
    private int chapter_sum;
    private ChapterBean new_chapter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getRead_sum() {
        return read_sum;
    }

    public void setRead_sum(int read_sum) {
        this.read_sum = read_sum;
    }

    public int getIs_end() {
        return is_end;
    }

    public void setIs_end(int is_end) {
        this.is_end = is_end;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getClassify_id() {
        return classify_id;
    }

    public void setClassify_id(int classify_id) {
        this.classify_id = classify_id;
    }

    public String getClassify_name() {
        return classify_name;
    }

    public void setClassify_name(String classify_name) {
        this.classify_name = classify_name;
    }

    public int getGreet() {
        return greet;
    }

    public void setGreet(int greet) {
        this.greet = greet;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getWord() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }

    public int getIs_new() {
        return is_new;
    }

    public void setIs_new(int is_new) {
        this.is_new = is_new;
    }

    public int getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(int is_hot) {
        this.is_hot = is_hot;
    }

    public int getReading_size() {
        return reading_size;
    }

    public void setReading_size(int reading_size) {
        this.reading_size = reading_size;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public int getChapter_sum() {
        return chapter_sum;
    }

    public void setChapter_sum(int chapter_sum) {
        this.chapter_sum = chapter_sum;
    }

    public ChapterBean getNew_chapter() {
        return new_chapter;
    }

    public void setNew_chapter(ChapterBean new_chapter) {
        this.new_chapter = new_chapter;
    }
}
