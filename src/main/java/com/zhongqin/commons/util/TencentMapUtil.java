package com.zhongqin.commons.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/9/19 10:42 星期四
 */
@Slf4j
public class TencentMapUtil {

    /**
     * 根据地址查询经纬度
     *
     * @param address 精准地址
     * @return 地址
     */
    public static String getLngAndLat(String address) {
        String key = "L4DBZ-ELNK5-ANIIO-IARGT-FZAPO-4CB7R";
        JSONObject positionObj = new JSONObject();
        try {
            log.info("地址处理前：{}", address);
            String regExp = "[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？-～]";
            address = address.replaceAll(regExp, "").trim();
            log.info("地址处理后：{}", address);
            // 拼接请求高德的url
            String url = "https://apis.map.qq.com/ws/geocoder/v1/?address=" + address + "&key=" + key;
            // 请求高德接口
            String result = HttpsUtil.doGet(url);
            JSONObject parseObject = JSONObject.parseObject(result);
            log.info("腾讯地图接口原始数据：{}", parseObject);
            JSONObject jsonObject = parseObject.getJSONObject("result");
            log.info("腾讯地图接口[result]原始数据：{}", parseObject);
            if (jsonObject.size() > 0) {
                JSONObject location = jsonObject.getJSONObject("location");
                positionObj.put("longitude", location.get("lng"));
                positionObj.put("latitude", location.get("lat"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonTools.objectToJson(positionObj);
    }

    /**
     * 获取路线规划
     *
     * @param type        驾车（driving） 步行（walking）骑行（bicycling）电动车（ebicycling）公交（transit）新能源汽车（edriving）
     * @param origin      起点位置坐标 格式：纬度在前，经度在后，半角逗号分隔。注：系统将吸附就近道路作为起点，最大20公里内若无道路，会算路失败
     * @param destination 终点位置坐标，格式：纬度在前，经度在后，半角逗号分隔。
     * @return str
     */
    public static String getRouting(String type, String origin, String destination) {
        String key = "L4DBZ-ELNK5-ANIIO-IARGT-FZAPO-4CB7R";
        String url = "https://apis.map.qq.com/ws/direction/v1/" + type + "/?from=" + origin + "&to=" + destination + "&output=json&callback=cb&key=" + key;
        return HttpsUtil.doGet(url);
    }

}
