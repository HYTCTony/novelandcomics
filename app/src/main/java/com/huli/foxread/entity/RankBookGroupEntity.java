package com.huli.foxread.entity;

import java.util.List;

public class RankBookGroupEntity {
    private long time;                      //更新时间
    private List<RankBookEntity> list;      //排行榜

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<RankBookEntity> getList() {
        return list;
    }

    public void setList(List<RankBookEntity> list) {
        this.list = list;
    }
}
