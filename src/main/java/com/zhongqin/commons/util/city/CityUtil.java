package com.zhongqin.commons.util.city;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongqin.commons.util.HttpsUtil;

import java.util.HashMap;

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


    public static String getProvincialLevel(String city) {
        if (city.length() > 1) {
            city = String.valueOf(city.charAt(0));
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("皖", "安徽");
        map.put("澳", "澳门");
        map.put("港", "香港");
        map.put("台", "台湾");
        map.put("京", "北京");
        map.put("沪", "上海");
        map.put("津", "天津");
        map.put("渝", "重庆");
        map.put("冀", "河北省");
        map.put("豫", "河南");
        map.put("云", "云南");
        map.put("辽", "辽宁");
        map.put("黑", "黑龙江");
        map.put("湘", "湖南");
        map.put("鲁", "山东");
        map.put("新", "新疆维吾尔自治区");
        map.put("苏", "江苏");
        map.put("浙", "浙江");
        map.put("赣", "江西");
        map.put("鄂", "湖北");
        map.put("桂", "广西壮族自治区");
        map.put("甘", "甘肃省");
        map.put("晋", "山西");
        map.put("蒙", "内蒙古自治区");
        map.put("陕", "陕西");
        map.put("吉", "吉林");
        map.put("闽", "福建");
        map.put("贵", "贵州");
        map.put("粤", "广东");
        map.put("川", "四川");
        map.put("青", "青海");
        map.put("藏", "西藏自治区");
        map.put("琼", "海南");
        map.put("宁", "宁夏回族自治区");
        return map.get(city);
    }

}
