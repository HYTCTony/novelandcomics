package com.huli.foxread.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 书
 */
public class BookEntity implements Serializable {
    private String id;                  //ID
    private String name;                //书名
    private int heat;                   //热度
    private int reading_size;           //现在 在读人数
    private int read_sum;               //阅读次数
    private int is_end;                 //0未完结，1已完结
    private int is_copyright;           //0无版权，1有版权
    private int status;                 //上架（1） 下架（0）状态
    private String author;              //作者
    private String introduce;           //简介
    private String http_image;          //封面
    private String tag_group;           //标签字符串
    private List<String> tag;           //标签
    private int word;                   //字数
    private float score;                //评分
    private String classify_name;       //归属分类
    private int number;                 //搜索次数
    private int greet;                  //人气
    private int rank;                   //排名

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

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public int getReading_size() {
        return reading_size;
    }

    public void setReading_size(int reading_size) {
        this.reading_size = reading_size;
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

    public int getIs_copyright() {
        return is_copyright;
    }

    public void setIs_copyright(int is_copyright) {
        this.is_copyright = is_copyright;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getTag_group() {
        return tag_group;
    }

    public void setTag_group(String tag_group) {
        this.tag_group = tag_group;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public int getWord() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getClassify_name() {
        return classify_name;
    }

    public void setClassify_name(String classify_name) {
        this.classify_name = classify_name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getGreet() {
        return greet;
    }

    public void setGreet(int greet) {
        this.greet = greet;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
