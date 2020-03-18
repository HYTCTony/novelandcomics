package com.huli.foxread.callbacks;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Stack;

public class ActivityManager implements Application.ActivityLifecycleCallbacks {

    private ActivityState mActivityState;
    //记录Activity，用于判断APP处于前台或后台
    private int count = 0;

    private static Stack<Activity> activities = new Stack<>();
    //商品详情页最多个数,这里为了测试只写了2,大家根据自己的情况设值
   /* private static final int MAX_ACTIVITY_DETAIL_NUM = 2;
    private static Stack<ActivityDetail> store = new Stack<>();*/


    private static ActivityManager activityManager;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ActivityManager getInstance(ActivityState mActivityState) {
        if (activityManager == null)
            synchronized (ActivityManager.class) {
                if (activityManager == null) {
                    activityManager = new ActivityManager(mActivityState);
                }
            }
        return activityManager;
    }

    private ActivityManager(ActivityState mActivityState) {
        this.mActivityState = mActivityState;
    }


    /**
     * 获取Activity任务栈
     *
     * @return activity stack
     */
    public Stack<Activity> getActivityStack() {
        return activities;
    }

    /**
     * Activity 入栈
     *
     * @param activity Activity
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * Activity出栈
     *
     * @param activity Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activities.remove(activity);
        }
    }

    /**
     * 结束某Activity
     *
     * @param activity Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            removeActivity(activity);
            activity.finish();
        }
    }

    /**
     * 获取当前Activity
     *
     * @return current activity
     */
    public Activity getCurrentActivity() {
        return activities.lastElement();
    }

    /**
     * 结束当前Activity
     */
    public void finishActivity() {
        finishActivity(activities.lastElement());
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
       /* Activity sss = null;
        if (activity instanceof ActivityDetail) {
            if (store.size() >= MAX_ACTIVITY_DETAIL_NUM) {
                sss = store.peek();
                activities.remove(sss);
                sss.finish(); //移除栈底的详情页并finish,保证商品详情页个数最大不超过指定
            }
            store.add(activity);
        }*/
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (count == 0) { //后台切换到前台
            mActivityState.isFront();
        }
        count++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        count--;
        if (count == 0) { //前台切换到后台
            mActivityState.isBack();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        removeActivity(activity);
    }

    public void finishAll() {
        for (Activity activity : activities) {
            finishActivity(activity);
        }
        activities.clear();
    }

}