package com.nnmedia.read.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

public class UniqueIdManager {

    private static final String TAG = "UniqueIdManager";
    private static String UNIQUE_KEY = "unique_id";
    private static String uniqueID;

    /**
     * 获得设备硬件标识
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    public static String getUniqueID(Context context) {
        //两步读取：内存中，存储的SP表中
        if (!TextUtils.isEmpty(uniqueID)) {
            return uniqueID;
        }
        uniqueID = getDeviceIdForSharePreference(context);
        if (!TextUtils.isEmpty(uniqueID)) {
            return uniqueID;
        }

        StringBuilder sbCurId = new StringBuilder();
        //获得设备序列号（如：WTK7N16923005607）, android 8以上需要权限，android Q无法获得
        String serial = getSerial();
        //获得AndroidId（无需权限）,部分手机厂商写死,部分手机无法获取
        String androidId = getAndroidID(context);
        //获得硬件uuid（根据硬件相关属性，生成uuid）（无需权限）
        String deviceId = getDeviceUUID().replace("-", "");

        //追加serial
        sbCurId.append(serial);
        //追加androidId
        sbCurId.append(androidId);
        if (sbCurId.length() <= 0) {
            //随机UUID
            String uuid = UUID.randomUUID().toString().replace("-", "");
            sbCurId.append(uuid);
        }
        //追加硬件id
        sbCurId.append(deviceId);

        //生成SHA1，统一DeviceId长度
        try {
            byte[] hash = getHashByString(sbCurId.toString());
            uniqueID = bytesToHex(hash);
        } catch (Exception ex) {
            ex.printStackTrace();
            //如果以上硬件标识数据均无法获得，
            //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
            uniqueID = UUID.randomUUID().toString().replace("-", "");
        }

        setDeviceIdToSharePreference(context, uniqueID);
        return uniqueID;
    }

    /**
     * 获得设备序列号（如：WTK7N16923005607）, android 8以上无法获得
     *
     * @return 设备序列号
     */
    private static String getSerial() {
        String serial;
        try {
            //追加serial
            serial = Build.SERIAL;
            if (!TextUtils.isEmpty(serial) && !"UNKNOWN".equalsIgnoreCase(serial)) {
                return serial;
            }
            return serial;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * The Android ID
     * 通常被认为不可信，因为它有时为null。开发文档中说明了：这个ID会改变如果进行了出厂设置。并且，如果某个
     * Andorid手机被Root过的话，这个ID也可以被任意改变。无需任何许可。
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    private static String getAndroidID(Context context) {
        String androidID;
        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(androidID) && !TextUtils.equals("9774d56d682e549c", androidID)) {
                return androidID;
            }
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
        String serial;
        String m_szDevIDShort = "87" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            serial = Build.SERIAL;
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();       //例如：ffffffff-e0ee-9570-ffff-ffffef05ac4a
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial87";
        }
        //使用硬件信息拼凑出来的15位号码     //“87”加上后面的13位一共15位，我们可以得到875715565309247这样一串号码，不需要任何的权限，非常方便。
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
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
     * @param context 保存设备码
     */
    private static void setDeviceIdToSharePreference(Context context, String device_id) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(UNIQUE_KEY, device_id);
        editor.apply();
    }

    /**
     * 获取设备码
     *
     * @param context
     * @return
     */
    private static String getDeviceIdForSharePreference(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(UNIQUE_KEY, "");
    }
}