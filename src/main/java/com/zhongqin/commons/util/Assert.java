package com.zhongqin.commons.util;

import com.zhongqin.commons.exception.CustomException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 断言工具类
 *
 * @author Kevin
 * @date 2020/9/16 14:10
 */
public class Assert {

    /**
     * 断言这个Boolean是 true 还是 false
     * <p>为false 则抛出异常</p>
     *
     * @param bool
     * @param msg
     */
    public static void isBoolean(Boolean bool, String msg) {
        if (!bool) {
            throw new CustomException(msg);
        }
    }

    /**
     * 断言这个两个字符串是否相等 区分大小写
     * <p>不相等 则抛出异常</p>
     *
     * @param str
     * @param str2
     * @param msg
     */
    public static void isEquals(String str, String str2, String msg) {
        if (!str.equals(str2)) {
            throw new CustomException(msg);
        }
    }

    /**
     * 断言这个两个字符串是否相等 并忽略大小写
     * <p>不相等 则抛出异常</p>
     *
     * @param str
     * @param str2
     * @param msg
     */
    public static void isEqualsIgnoreCase(String str, String str2, String msg) {
        if (!str.equalsIgnoreCase(str2)) {
            throw new CustomException(msg);
        }
    }

    /**
     * 断言这个字符串不是空白
     * <p>为空白字符串 则抛出异常</p>
     *
     * @param str
     * @param msg
     */
    public static void isBlank(String str, String msg) {
        if (StringUtils.isBlank(str)) {
            throw new CustomException(msg);
        }
    }

    /**
     * 断言这个字符串是空白
     * <p>不为空白字符串 则抛出异常</p>
     *
     * @param str
     * @param msg
     */
    public static void isNotBlank(String str, String msg) {
        if (StringUtils.isNotBlank(str)) {
            throw new CustomException(msg);
        }
    }

    /**
     * 断言这个对象是否为空
     * <p>为空 则抛出异常</p>
     *
     * @param object
     * @param msg
     */
    public static void isNull(Object object, String msg) {
        if (Objects.isNull(object)) {
            throw new CustomException(msg);
        }
    }

    /**
     * 断言这个对象是否为空
     * <p>不为空 则抛出异常</p>
     *
     * @param object
     * @param msg
     */
    public static void nonNull(Object object, String msg) {
        if (Objects.nonNull(object)) {
            throw new CustomException(msg);
        }
    }

    /**
     * 判断字符串是否已经超过了指定的长度
     * 超过了则抛出异常信息
     *
     * @param str
     * @param length
     * @param msg
     */
    public static void isMaxLength(String str, int length, String msg) {
        if (StringUtils.isBlank(str)) {
            /// throw new CustomException(str + "not null!");
            return;
        }
        if (str.length() > length) {
            throw new CustomException(msg);
        }
    }

}
