package com.nnmedia.page.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.nnmedia.read.FrApp;


public class SharedPreUtils {
    private static final String SHARED_NAME = "IReader_pref";
    private static SharedPreUtils sInstance;
    private SharedPreferences sharedReadable;
    private SharedPreferences.Editor sharedWritable;

    private SharedPreUtils() {
        sharedReadable = FrApp.getInstance()
                .getSharedPreferences(SHARED_NAME, Context.MODE_MULTI_PROCESS);
        sharedWritable = sharedReadable.edit();
    }

    public static SharedPreUtils getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreUtils.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreUtils();
                }
            }
        }
        return sInstance;
    }

    public String getString(String key) {
        return sharedReadable.getString(key, "");
    }

    public String getString(String key, String value) {
        return sharedReadable.getString(key, value);
    }

    public void putString(String key, String value) {
        sharedWritable.putString(key, value);
        sharedWritable.commit();
    }

    public void putInt(String key, int value) {
        sharedWritable.putInt(key, value);
        sharedWritable.commit();
    }

    public void putLong(String key, long value) {
        sharedWritable.putLong(key, value);
        sharedWritable.commit();
    }

    public void putBoolean(String key, boolean value) {
        sharedWritable.putBoolean(key, value);
        sharedWritable.commit();
    }

    public int getInt(String key, int def) {
        return sharedReadable.getInt(key, def);
    }

    public void putFloat(String key, float def) {
        sharedWritable.putFloat(key, def);
        sharedWritable.commit();
    }

    public float getFloat(String key, float def) {
        return sharedReadable.getFloat(key, def);
    }

    public long getLong(String key, long def) {
        return sharedReadable.getLong(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedReadable.getBoolean(key, def);
    }
}
