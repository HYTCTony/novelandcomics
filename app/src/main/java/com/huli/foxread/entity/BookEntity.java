package com.huli.foxread.entity;

import java.io.Serializable;
import java.util.List;

public class BookEntity implements Serializable {
    private String id;
    private String name;                //书名
    private float score;                //评分
    private String file;                //小说下载链接
    private float read_sum;             //阅读次数（万）
    private int is_end;                 //0未完结，1已完结
    private float greet;                //小说人气值（万）
    private String author;
    private float word;                 //小说字数（万）
    private int number;                 //被搜索的次数
    private int is_new;
    private int is_hot;
    private int hot;                    //热度（万）
    private float reading_size;         //在读人数（万）
    private String introduce;
    private String http_image;
    private List<String> tag;

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

    public float getRead_sum() {
        return read_sum;
    }

    public void setRead_sum(float read_sum) {
        this.read_sum = read_sum;
    }

    public int getIs_end() {
        return is_end;
    }

    public void setIs_end(int is_end) {
        this.is_end = is_end;
    }

    public float getGreet() {
        return greet;
    }

    public void setGreet(float greet) {
        this.greet = greet;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public float getWord() {
        return word;
    }

    public void setWord(float word) {
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

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public float getReading_size() {
        return reading_size;
    }

    public void setReading_size(float reading_size) {
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
