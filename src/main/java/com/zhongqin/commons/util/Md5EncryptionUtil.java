package com.zhongqin.commons.util;

import com.zhongqin.commons.exception.CustomException;

import java.security.MessageDigest;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/8/23 16:42 星期五
 */
public class Md5EncryptionUtil {

    /**
     * MD5 加密
     *
     * @param param str
     * @return str
     */
    public static String generateValue(String param) {
        try {
            MessageDigest algorithm;
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(param.getBytes());
            byte[] messageDigest = algorithm.digest();
            return toHexString(messageDigest);
        } catch (Exception ex) {
            throw new CustomException(ExceptionUtil.getStackTrace(ex));
        }

    }

    public static String toHexString(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(HEX_CODE[(b >> 4 & 0xF)]);
            r.append(HEX_CODE[(b & 0xF)]);
        }
        return r.toString();
    }

    public static final char[] HEX_CODE = "0123456789abcdef".toCharArray();

}
