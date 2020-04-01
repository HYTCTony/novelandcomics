package com.huli.foxread.utils;

import java.text.DecimalFormat;

public class MoneyUtil {
    public static String formatTosepara(double data) {
        if (data == 0) {
            return "0";
        }
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(data);
    }

    public static String formatToseparaYuan(double data) {
        if (data == 0) {
            return "0元";
        }
        DecimalFormat df = new DecimalFormat("#,###.00" + "元");
        return df.format(data);
    }

    public static String formatToseparaNoPoi(double data) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(data);
    }

    public static String formatYuan(double data) {
        DecimalFormat df = new DecimalFormat("########.##" + "元");
        return df.format(data);
    }
}
