package com.zhongqin.commons.util;

import java.util.Calendar;
import java.util.Random;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/12/17 15:15 星期二
 */
public class GenerateUtil {

    /**
     * 生成八位数的key第一位是首字母第六位是下划线其他的是数字
     *
     * @return 企业密钥
     */
    public static String generateKey() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        // 生成首位字母（这里简单生成大写字母，范围是'A' - 'Z'）
        char firstChar = (char) (random.nextInt(26) + 'A');
        sb.append(firstChar);
        // 获取当前时间戳
        long timestamp = Calendar.getInstance().getTimeInMillis();
        for (int i = 1; i <= 4; i++) {
            sb.append(String.valueOf(System.currentTimeMillis()).charAt(random.nextInt(13)));
        }
        // 添加固定的下划线
        sb.append('_');
        // 再结合时间戳生成末尾两位数字（同样做简单运算）
        for (int i = 2; i <= 3; i++) {
            sb.append(String.valueOf(System.currentTimeMillis()).charAt(random.nextInt(13)));
        }
        return sb.toString();
    }

}
