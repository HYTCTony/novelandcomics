package com.nnmedia.read.ebsevent;

public class VipChargerEvent {
    private boolean becomingVip;

    public VipChargerEvent() {
    }

    public VipChargerEvent(boolean becomingVip) {
        this.becomingVip = becomingVip;
    }

    public boolean isBecomingVip() {
        return becomingVip;
    }

    public void setBecomingVip(boolean becomingVip) {
        this.becomingVip = becomingVip;
    }
}
