package com.huli.foxread.ebsevent;

public class ReadingTimeEvent {
    private String readMin;

    public ReadingTimeEvent() {
    }

    public ReadingTimeEvent(String readMin) {
        this.readMin = readMin;
    }

    public String getReadMin() {
        return readMin;
    }

    public void setReadMin(String readMin) {
        this.readMin = readMin;
    }
}
