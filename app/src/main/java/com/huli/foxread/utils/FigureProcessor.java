package com.huli.foxread.utils;

import android.content.Context;

import com.huli.foxread.R;

import java.text.DecimalFormat;

public class FigureProcessor {

    public static String formatNum(Context context, int number) {
        if (number >= 10000) {
            DecimalFormat df = new DecimalFormat("#####.#" + context.getResources().getString(R.string.unit_w));
            return df.format((float) number / 10000);
        }
        return String.valueOf(number);
    }

    public static String formatWordNum(Context context, int wordNum) {
        if (wordNum >= 10000) {
            DecimalFormat df = new DecimalFormat("#####.#" + context.getResources().getString(R.string.unit_word_w));
            return df.format((float) wordNum / 10000);
        }
        return wordNum + context.getResources().getString(R.string.unit_word);
    }

    public static String formatPeopleNum(Context context, int peopleNum) {
        if (peopleNum >= 10000) {
            DecimalFormat df = new DecimalFormat("#####.#" + context.getResources().getString(R.string.unit_ren_w));
            return df.format((float) peopleNum / 10000);
        }
        return peopleNum + context.getResources().getString(R.string.unit_ren);
    }

    public static String formatHeat(Context context, int heat) {
        if (heat >= 10000) {
            DecimalFormat df = new DecimalFormat("#####.#" + context.getResources().getString(R.string.txt_heat_wan));
            return df.format((float) heat / 10000);
        }
        return heat + context.getResources().getString(R.string.txt_heat);
    }

    public static String formatGreet(Context context, int greet) {
        if (greet >= 10000) {
            DecimalFormat df = new DecimalFormat("#####" + context.getResources().getString(R.string.txt_greet_wan));
            return df.format((float) greet / 10000);
        }
        return greet + context.getResources().getString(R.string.txt_greet);
    }
}
