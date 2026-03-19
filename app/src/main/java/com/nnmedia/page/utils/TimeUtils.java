package com.nnmedia.page.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    //时间戳转换成时间
    public static String yyyyMMddHHmmss(long seconds) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            return sdf.format(new Date(seconds));
        } catch (Exception e) {
            return "";
        }
    }

    //时间戳转换成时间
    public static String yyyyMMdd(long seconds) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return sdf.format(new Date(seconds));
        } catch (Exception e) {
            return "";
        }
    }

    //时间戳转换成时间
    public static String MMdd(String seconds) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.CHINA);
            return sdf.format(new Date(Long.valueOf(seconds)));
        } catch (Exception e) {
            return "";
        }
    }

    //时间戳转换成时间
    public static String HHmm(String seconds) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
            return sdf.format(new Date(Long.valueOf(seconds)));
        } catch (Exception e) {
            return "";
        }
    }

    //获取当前时间
    public static String getCurrentTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return sdf.format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return "";
        }
    }

    public static String getCurrentTimeForYear() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.CHINA);
            return sdf.format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return "";
        }
    }

    public static String getCurrentTimeForMonthToChinese() {
        String[] month = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
        try {
            Calendar cd = Calendar.getInstance();
            int m = cd.get(Calendar.MONTH);
            return month[m];
        } catch (Exception e) {
            return "本";
        }
    }

    public static String getCurrentTimeForMonth() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.CHINA);
            return sdf.format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return "";
        }
    }

    public static String getCurrentTimeForDay() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.CHINA);
            return sdf.format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return "";
        }
    }

    //获取三年后的日期
    public static String getTreeYearCurrentTime() {
        try {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Calendar canlandar = Calendar.getInstance();
            canlandar.setTime(date);
            canlandar.add(canlandar.DAY_OF_MONTH, 1095);
            return sdf.format(canlandar.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * long时间戳转字符串
     *
     * @param time
     * @return
     */
    public static String getStringFormLong(long time) {
        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(date);
    }

    /**
     * 将日期格式化成友好的字符串：几分钟前、几小时前、几天前、几月前、几年前、刚刚
     *
     * @param time
     * @return
     */
    public static String formatFriendly(long time) {
        Date date = new Date(time * 1000);
        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    private static long lastTime;                //最后一次点击的时间

    private static long delay = 800;

    public static boolean onMoreClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time < delay) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }
}
