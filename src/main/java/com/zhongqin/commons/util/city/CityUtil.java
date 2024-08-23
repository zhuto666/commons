package com.zhongqin.commons.util.city;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongqin.commons.util.HttpsUtil;

/**
 * @author Kevin
 * IP数据云 获取IP城市
 * @version 1.0
 * @date 2024/7/1 13:52 星期一
 */
public class CityUtil {

    /**
     * 根据 IP 获取城市 调用的是 第三方IP数据云的接口 <a href="https://www.ipdatacloud.com/">...</a>
     *
     * @param ip ip
     * @return 城市
     */
    public static String getCityByIp(String ip) {
        String str = HttpsUtil.doGet("https://api.ipdatacloud.com/v2/query?ip=" + ip + "&key=32b6aa56374f11ef94c900163e167ffb");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(str);
            if ("200".equals(jsonNode.get("code").asText())) {
                return jsonNode.get("data").get("location").get("province").asText() + " " + jsonNode.get("data").get("location").get("city").asText();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

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
