package com.sq.system.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String KEY = "1234567890123456";  // 16字节密钥

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getUrlEncoder().encodeToString(encrypted); // URL 安全
    }

    public static String decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(Base64.getUrlDecoder().decode(encrypted));
        return new String(decrypted);
    }

    public static void main(String[] args) throws Exception {
        String userId = "12345";
        String encrypted = encrypt(userId);
        System.out.println("加密后：" + encrypted);
        System.out.println("解密后：" + decrypt(encrypted));
    }
}
