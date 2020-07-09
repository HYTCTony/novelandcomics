package com.huli.foxread.listeners;

import android.view.View;

/**
 * 防止重复点击（点击过快）
 */

public abstract class OnClickEvent implements View.OnClickListener {
    private static long lastTime;                //最后一次点击的时间

    public abstract void singleClick(View v);

    private long delay = 800;

    public OnClickEvent() {
    }

    public OnClickEvent(long delay) {
        this.delay = delay;
    }

    @Override
    public void onClick(View v) {
        if (!onMoreClick()) {
            singleClick(v);
        }
        lastTime = System.currentTimeMillis();
    }

    /**
     * 无效的连续点击会重置 间隔时间
     *
     * @return 是否点击过快
     */
    private boolean onMoreClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time < delay) {
            flag = true;
        }
        return flag;
    }
}
