package com.zhongqin.commons.util;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/8/23 16:41 星期五
 */
public class CityUtil {

    /**
     * 获取地域城市
     *
     * @param city 地域
     * @return 城市
     */
    public static String getCity(String city) {
        String replace = city.replace(" ", ",");
        String[] split = replace.split(",");
        if (split[0].contains("市")) {
            return split[0].replaceAll("市", "");
        }
        for (String str : split) {
            if (str.contains("市")) {
                replace = str;
                break;
            }
        }
        return replace.replaceAll("市", "");
    }

}
