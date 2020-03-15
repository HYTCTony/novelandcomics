package com.huli.page.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

public class CreatCodeManager {

    /**
     * 获得设备硬件标识
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    public static String getDeviceId(Context context) {
        //如果缓存过，则直接从缓存获取
        String sha1 = getDeviceIdForSharePreference(context);
        if (!TextUtils.isEmpty(sha1)) {
            return sha1;
        }

        StringBuilder sbDeviceId = new StringBuilder();
        //获得设备序列号（如：WTK7N16923005607）, android O以上需要权限，android Q以上无法获得
        String serial = serial();
        //获得AndroidId（无需权限）,部分手机厂商写死,部分手机无法获取
        String androidid = getAndroidId(context);
        //获得硬件uuid（根据硬件相关属性，生成uuid）（无需权限）
        String deviceid = getDeviceUUID().replace("-", "");

        //追加serial
        sbDeviceId.append(serial);
        //追加androidid
        sbDeviceId.append(androidid);
        //如果手机序列号和androidId均无法获得，追加随机uuid，保证标识符唯一性。
        if (sbDeviceId.length() <= 0) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            //追加随机uuid
            if (!TextUtils.isEmpty(uuid)) {
                sbDeviceId.append(uuid);
            }
        }
        //追加硬件id
        sbDeviceId.append(deviceid);

        //生成SHA1，统一DeviceId长度
        try {
            byte[] hash = getHashByString(sbDeviceId.toString());
            sha1 = bytesToHex(hash);
            if (TextUtils.isEmpty(sha1)) {
                //如果以上硬件标识数据均无法获得，
                //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
                sha1 = UUID.randomUUID().toString().replace("-", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //如果转换数据出错情况下
            //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
            sha1 = UUID.randomUUID().toString().replace("-", "");
        }
        //将设备标识符缓存入sharepreference
        setDeviceIdToSharePreference(context, sha1);
        return sha1;
    }

    /**
     * 获得设备序列号（如：WTK7N16923005607）, android 8以上无法获得
     *
     * @return 设备序列号
     */
    @SuppressLint("HardwareIds")
    private static String serial() {
        try {
            String serial = Build.SERIAL;
            if (!TextUtils.isEmpty(serial) && !"unknown".equalsIgnoreCase(serial))
                return Build.SERIAL;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    @SuppressLint("HardwareIds")
    private static String getAndroidId(Context context) {
        try {
            String androidid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(androidid) && !"9774d56d682e549c".equalsIgnoreCase(androidid))
                return androidid;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     *
     * @return 设备硬件uuid
     */
    private static String getDeviceUUID() {
        String dev = "68" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits;
        try {
            String serial = serial();
            if (TextUtils.isEmpty(serial))
                serial = "serial1688";
            return new UUID(dev.hashCode(), serial.hashCode()).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 取SHA1
     *
     * @param data 数据
     * @return 对应的hash值
     */
    private static byte[] getHashByString(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception e) {
            return "".getBytes();
        }
    }

    /**
     * 转16进制字符串
     *
     * @param data 数据
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String stmp;
        for (int n = 0; n < data.length; n++) {
            stmp = (Integer.toHexString(data[n] & 0xFF));
            if (stmp.length() == 1)
                sb.append("0");
            sb.append(stmp);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }

    /**
     * 保存设备码
     *
     * @param context   上下文
     * @param device_id 设备id
     */
    private static void setDeviceIdToSharePreference(Context context, String device_id) {
        SharedPreferences sp = context.getSharedPreferences("system", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("device_id", device_id);
        editor.apply();
    }

    /**
     * 获取设备码
     *
     * @param context 上下文
     * @return 设备id
     */
    private static String getDeviceIdForSharePreference(Context context) {
        SharedPreferences sp = context.getSharedPreferences("system", Context.MODE_PRIVATE);
        return sp.getString("device_id", "");
    }
}