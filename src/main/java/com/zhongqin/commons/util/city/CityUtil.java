package com.zhongqin.commons.util.city;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongqin.commons.util.HttpsUtil;
import com.zhongqin.commons.util.JsonTools;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * 车牌转省
     *
     * @param city 车牌
     * @return str
     */
    public static String getProvincialLevel(String city) {
        if (city.length() > 1) {
            city = String.valueOf(city.charAt(0));
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("皖", "安徽省");
        map.put("澳", "澳门省");
        map.put("港", "香港省");
        map.put("台", "台湾省");
        map.put("京", "北京省");
        map.put("沪", "上海省");
        map.put("津", "天津省");
        map.put("渝", "重庆省");
        map.put("冀", "河北省");
        map.put("豫", "河南省");
        map.put("云", "云南省");
        map.put("辽", "辽宁省");
        map.put("黑", "黑龙江省");
        map.put("湘", "湖南省");
        map.put("鲁", "山东省");
        map.put("新", "新疆维吾尔自治区");
        map.put("苏", "江苏省");
        map.put("浙", "浙江省");
        map.put("赣", "江西省");
        map.put("鄂", "湖北省");
        map.put("桂", "广西壮族自治区");
        map.put("甘", "甘肃省");
        map.put("晋", "山西省");
        map.put("蒙", "内蒙古自治区");
        map.put("陕", "陕西省");
        map.put("吉", "吉林省");
        map.put("闽", "福建省");
        map.put("贵", "贵州省");
        map.put("粤", "广东省");
        map.put("川", "四川省");
        map.put("青", "青海省");
        map.put("藏", "西藏自治区");
        map.put("琼", "海南省");
        map.put("宁", "宁夏回族自治区");
        return map.get(city);
    }

//    public static void main(String[] args) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("皖", "安徽省");
//        map.put("澳", "澳门");
//        map.put("港", "香港");
//        map.put("台", "台湾");
//        map.put("京", "北京省");
//        map.put("沪", "上海省");
//        map.put("津", "天津省");
//        map.put("渝", "重庆省");
//        map.put("冀", "河北省");
//        map.put("豫", "河南省");
//        map.put("云", "云南省");
//        map.put("辽", "辽宁省");
//        map.put("黑", "黑龙江省");
//        map.put("湘", "湖南省");
//        map.put("鲁", "山东省");
//        map.put("新", "新疆维吾尔自治区");
//        map.put("苏", "江苏省");
//        map.put("浙", "浙江省");
//        map.put("赣", "江西省");
//        map.put("鄂", "湖北省");
//        map.put("桂", "广西壮族自治区");
//        map.put("甘", "甘肃省");
//        map.put("晋", "山西省");
//        map.put("蒙", "内蒙古自治区");
//        map.put("陕", "陕西省");
//        map.put("吉", "吉林省");
//        map.put("闽", "福建省");
//        map.put("贵", "贵州省");
//        map.put("粤", "广东省");
//        map.put("川", "四川省");
//        map.put("青", "青海省");
//        map.put("藏", "西藏自治区");
//        map.put("琼", "海南省");
//        map.put("宁", "宁夏回族自治区");
//        List<Objects> list = new ArrayList<>();
//        // keySet获取map集合key的集合  然后在遍历key即可
//        for (String key : map.keySet()) {
//            String value = map.get(key);
//            Objects objects = new Objects();
//            objects.setText(value);
//            objects.setValue(value);
//            list.add(objects);
//        }
//        System.out.println(list.size());
//        System.out.println(JsonTools.objectToJson(list));
//    }

}
