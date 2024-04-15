package com.zhongqin.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author Kevin
 * @date 2022/2/28 14:30
 */
public class PostCodeUtil {

    /**
     * 比较邮编是否在begin和end区间
     *
     * @param begin 开始邮编
     * @param end   结束邮编
     * @param str   区间邮编
     * @return
     */
    public static boolean comparison(String begin, String end, String str) {
        char[] beginArray = begin.toCharArray();
        char[] endArray = end.toCharArray();
        char[] strArray = str.toCharArray();
        String beginStr = StringUtils.EMPTY;
        String endStr = StringUtils.EMPTY;
        String strStr = StringUtils.EMPTY;
        for (int i = 0; i < end.length(); i++) {
            if (i < beginArray.length) {
                char b = beginArray[i];
                beginStr += Integer.valueOf(b);
            }
            if (i < endArray.length) {
                char e = endArray[i];
                endStr += Integer.valueOf(e);
            }
            if (i < strArray.length) {
                char s = strArray[i];
                strStr += Integer.valueOf(s);
            }
        }
        return new BigDecimal(strStr).compareTo(new BigDecimal(beginStr)) >= 0
                && new BigDecimal(strStr).compareTo(new BigDecimal(endStr)) <= 0;
    }
}
