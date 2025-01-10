package com.zhongqin.commons.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/10/8 14:00 星期二
 */
@Slf4j
public class GenerateValidateCodeUtil {

    /**
     * 生成图像验证码
     *
     * @param width       图像宽度
     * @param height      图像高度
     * @param codeCount   字符个数
     * @param circleCount 干扰圆圈数量
     * @param minutes     过期时间
     * @return Map<String, String> 包含验证码的Map，包含key和imageBase64两个键值对
     */
    public static Map<String, String> generateValidateCode(int width, int height, int codeCount, int circleCount, long minutes) {
        // 通过工具类生成图片验证码
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(width, height, codeCount, circleCount);
        // 验证码的值
        String codeValue = circleCaptcha.getCode();
        log.info("验证码为：{}", codeValue);
        // 将图片进行base64编码，并返回
        String imageBase64 = circleCaptcha.getImageBase64();
        // 将验证码的值存入redis并设置5分钟过期
        String token = JwtUtil.createToken(codeValue, LocalDateTimeUtil.convertLocalDteTimeToDate(LocalDateTime.now().plusMinutes(minutes)));
        return Map.of("key", token, "imageBase64", "data:image/png;base64," + imageBase64);
    }

}
