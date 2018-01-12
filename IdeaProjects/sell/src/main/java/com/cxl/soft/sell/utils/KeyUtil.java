package com.cxl.soft.sell.utils;

import java.util.Random;

/**
 * 生成主键id
 */
public class KeyUtil {
    public static synchronized String gen() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(number);
    }
}
