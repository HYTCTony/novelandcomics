package com.huli.foxread.utils;

import android.text.TextUtils;
import android.util.Log;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;

public class RSAUtil {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

    public static final int KEY_SIZE = 1024;

    private static String public_key = null;
    private static String private_key = null;

    public static void init(String publicKey, String privateKey) {
        public_key = publicKey;
        private_key = privateKey;
    }

    public static PublicKey restorePublicKey(byte[] keyBytes) {
        X509EncodedKeySpec KeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicekey = factory.generatePublic(KeySpec);
            return publicekey;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String encode(String plainText) {

        try {
//            if (public_key == null || public_key == "") {
            if (TextUtils.isEmpty(public_key)) {
                Log.d("hytc", "请先调用RsaUtil.init(String publicKey)初始化");
                return null;
            }
            PublicKey publicekey = restorePublicKey(Base64.decode(public_key));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicekey);
            byte[] data = plainText.getBytes();
            byte[] test = cipher.doFinal(data);
            return Base64.encode(test);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String decode(byte[] encodedText) {

        try {
            PrivateKey key = restorePrivateKey(Base64.decode(private_key));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(encodedText));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 生成随机数
     * @param length 生成字符串的长度
     * @return 随机数
     */
    public static String  getRandomString(int length){
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
