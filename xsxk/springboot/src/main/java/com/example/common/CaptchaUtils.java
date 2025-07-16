package com.example.common;

import java.util.Random;

public  class CaptchaUtils {
    // 验证码字符集（可自定义，数字+字母等）
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CAPTCHA_LENGTH = 6; // 验证码长度

    /**
     * 生成文本验证码
     */
    public static String generateTextCaptcha() {
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            captcha.append(CHAR_POOL.charAt(index));
        }
        return captcha.toString();
    }
}