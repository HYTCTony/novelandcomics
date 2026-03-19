package com.nnmedia.read.utils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 用于加密
 */
public class EncryptUtils {
    public static final String ALGORITHM = "MD5";
    public static final String SHA1 = "SHA1";
    public static final String MD5 = "MD5";

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * encode string
     *
     * @param algorithm 加密方式
     * @param str 要加密的字符串
     * @return String
     */
    public static String encode(String algorithm, String str) {
        if (str == null) {
            return null;
        }
        //if (StringUtils.isNotEmpty(algorithm)) {
        if ("".equals(algorithm) || algorithm == null) {
            algorithm = EncryptUtils.ALGORITHM;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Takes the raw bytes from the digest and formats them correct.
     *
     * @param bytes the raw bytes from the digest.
     * @return the formatted bytes.
     */
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    /**
     * 四位随机数
     *
     * @return
     */
    public static String getNonce() {
        Random random = new Random();
        int max = 9999;
        int min = 1000;
        int z = random.nextInt(max - min + 1) + min;
        return z + "";
    }

    /**
     * 加密AppSecret
     *
     * @param appSecret
     * @param nonce
     * @return
     */
    public static String genSignature(String appSecret, String nonce) {
        Long timestamp = Calendar.getInstance().getTimeInMillis() / 1000000;
        String[] args = new String[]{appSecret, nonce, timestamp.toString()};
        List<String> list = Arrays.asList(args);
        Collections.sort(list);
        StringBuffer sb = new StringBuffer();
        for (String str : list) {
            sb.append(str);
        }
        String info = EncryptUtils.encode(EncryptUtils.SHA1, sb.toString());
        return info;
    }
}