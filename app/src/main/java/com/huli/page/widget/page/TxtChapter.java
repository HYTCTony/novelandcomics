package com.huli.page.widget.page;

public class TxtChapter {

    //章节所属的小说(网络)
    public String bookId;
    //章节的链接(网络)
    public String link;

    //章节名(共用)
    public String title;
    //是否选中
    public boolean isSelect;

    //章节内容在文章中的起始位置(本地)
    public long start;
    //章节内容在文章中的终止位置(本地)
    public long end;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String id) {
        this.bookId = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "TxtChapter{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
