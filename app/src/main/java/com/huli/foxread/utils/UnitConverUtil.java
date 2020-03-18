package com.huli.foxread.utils;

import android.content.Context;

import com.huli.foxread.R;

import java.text.DecimalFormat;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

public class UnitConverUtil {

    private static DecimalFormat df;

    public static String formatNum(Context context, float wordsNum) {
        if (df == null) {
            df = new DecimalFormat("#####.#");
        }
        return df.format(wordsNum);
    }

    public static String formatNumWan(Context context, float wordsNum) {
        if (df == null) {
            df = new DecimalFormat("#####.#");
        }
        return df.format(wordsNum) + context.getString(R.string.unit_w);
    }

    public static String formatNumUnit(Context context, float wordsNum, @StringRes int unit) {
        if (df == null) {
            df = new DecimalFormat("#####.#");
        }
        return df.format(wordsNum) + context.getString(unit);
    }

}
