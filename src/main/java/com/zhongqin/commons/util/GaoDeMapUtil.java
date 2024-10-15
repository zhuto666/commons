package com.zhongqin.commons.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/8/30 11:10 星期五
 */
@Slf4j
public class GaoDeMapUtil {

    /**
     * 根据地址查询经纬度
     *
     * @param address 精准地址
     * @return 地址
     */
    public static String getLngAndLat(String address) {
        String key = "5f6de38fdaabc8fd5a80990b18939bcd";
        JSONObject positionObj = new JSONObject();
        try {
            // 拼接请求高德的url
            String url = "http://restapi.amap.com/v3/geocode/geo?address=" + address.trim() + "&output=JSON&key=" + key;
            // 请求高德接口
            String result = sendHttpGet(url);
            JSONObject parseObject = JSONObject.parseObject(result);
            log.info("高德接口返回原始数据：{}", parseObject);
            JSONArray geocodesArray = parseObject.getJSONArray("geocodes");
            if (geocodesArray.size() > 0) {
                String position = geocodesArray.getJSONObject(0).getString("location");
                String[] lngAndLat = position.split(",");
                String longitude = lngAndLat[0];
                String latitude = lngAndLat[1];
                positionObj.put("longitude", longitude);
                positionObj.put("latitude", latitude);
            }
            geocodesArray.getJSONObject(0).getString("location");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonTools.objectToJson(positionObj);
    }

    /**
     * 发送Get请求
     *
     * @param url 地址
     * @return str
     */
    public static String sendHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)
                .setSocketTimeout(10000)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        String result = "";
        try {
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet);
            HttpEntity entity = closeableHttpResponse.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
