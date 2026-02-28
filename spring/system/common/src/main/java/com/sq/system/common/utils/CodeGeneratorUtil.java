package com.sq.system.common.utils;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGeneratorUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成唯一小号 code：
     * 规则：时间戳(秒级) + 3位随机数 → 打乱顺序 → 拼接
     * 长度固定为 13 位
     */
    public static String generateCode() {
        long timestamp = Instant.now().toEpochMilli(); // 毫秒级时间戳
        int rand = RANDOM.nextInt(900) + 100; // 3 位随机数 [100,999]

        String base = timestamp + "" + rand; // 原始串

        // 打乱顺序
        List<Character> chars = new ArrayList<>();
        for (char c : base.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, RANDOM);

        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * 生成固定长度的随机 code，长度不足则补随机数
     */
    public static String generateCode(int length) {
        String code = generateCode();
        if (code.length() >= length) {
            return code.substring(0, length);
        }
        // 补位
        while (code.length() < length) {
            code += RANDOM.nextInt(10);
        }
        return code;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.println(generateCode());
        }
    }
}
