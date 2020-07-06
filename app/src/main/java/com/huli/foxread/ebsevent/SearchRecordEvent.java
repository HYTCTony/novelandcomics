package com.huli.foxread.ebsevent;

public class SearchRecordEvent {
    private String keyword;

    public SearchRecordEvent() {
    }

    public SearchRecordEvent(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
