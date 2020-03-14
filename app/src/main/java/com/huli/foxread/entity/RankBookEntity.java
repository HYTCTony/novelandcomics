package com.huli.foxread.entity;

public class RankBookEntity {
    private String id;
    private String name;        //书名
    private float score;        //评分
    private String file;
    private int read_sum;
    private int is_end;         //0未完结，1已完结
    private int status;
    private int type;
    private int classify_id;
    private long start_copyright;
    private long end_copyright;
    private int is_copyright;
    private int column_id;
    private int greet;
    private String author;
    private int word;
    private int word_calssify;
    private int is_new;
    private int is_hot;
    private float reading_size;
    private String introduce;
    private long createtime;
    private long updatetime;
    private String http_image;
    private int hot;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public long getStart_copyright() {
        return start_copyright;
    }

    public void setStart_copyright(long start_copyright) {
        this.start_copyright = start_copyright;
    }

    public long getEnd_copyright() {
        return end_copyright;
    }

    public void setEnd_copyright(long end_copyright) {
        this.end_copyright = end_copyright;
    }

    public int getIs_copyright() {
        return is_copyright;
    }

    public void setIs_copyright(int is_copyright) {
        this.is_copyright = is_copyright;
    }

    public int getColumn_id() {
        return column_id;
    }

    public void setColumn_id(int column_id) {
        this.column_id = column_id;
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

    public int getWord_calssify() {
        return word_calssify;
    }

    public void setWord_calssify(int word_calssify) {
        this.word_calssify = word_calssify;
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

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }
}
