package com.zhongqin.commons.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/9/5 15:25 星期四
 */
@Slf4j
public class WxServiceNotificationUtil {

    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxc06f403bac193398&secret=71be56f0fcf94ccfc3bab05dd987538f";
    private static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=";

    public static void sendNotifications(String templateId, String openid, String page, Map<String, Object> paramsData) {
        String str = HttpsUtil.doGet(TOKEN_URL);
        log.info("access_token结果,{}", str);
        JSONObject jsonObject = JSON.parseObject(str);
        String accessToken = String.valueOf(jsonObject.get("access_token"));
        Map<String, Object> params = new HashMap<>();
        params.put("template_id", templateId);
        params.put("touser", openid);
        params.put("data", paramsData);
        // 跳转小程序类型：developer 为开发版；trial 为体验版；formal 为正式版；默认为正式版
        params.put("miniprogram_state", "formal");
        params.put("page", page);
        log.info("params参数,{}", JsonTools.objectToJson(params));
        String result = HttpsUtil.doPost(SEND_URL + accessToken, JsonTools.objectToJson(params), null);
        log.info("发送结果：{}", result);
    }

}

