package com.zhongqin.commons.util;


import com.zhongqin.commons.exception.CustomException;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.util.UUID;

/**
 * Token生成工具类
 *
 * @author Kevin
 * @date 2020/9/7 16:51
 */
public class TokenGenerator {

    private static final char[] HEX_CODE = "0123456789abcdef".toCharArray();

    public static String generateValue() {
        return generateValue(UUID.randomUUID().toString());
    }

    private static String toHexString(byte[] data) {
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

    /**
     * MD5后转大写
     *
     * @param msg
     * @return
     */
    public static String sign(String msg) {
        return DigestUtils.md5Hex(msg).toUpperCase();
    }

}
