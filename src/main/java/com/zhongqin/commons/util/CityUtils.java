package com.zhongqin.commons.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import lombok.SneakyThrows;
import org.springframework.data.repository.init.ResourceReader;

import java.io.*;
import java.net.InetAddress;
import java.util.Objects;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/6/24 12:44 星期一
 */
public class CityUtils {

    /**
     * 根据IP 获取城市
     * GeoLite2-City.mmdb 城市库不全
     *
     * @param ip   ip
     * @param path 文件地址
     * @return city
     */
    @SneakyThrows
    public static String getCity(String ip, String path) {
        // 创建 DatabaseReader 对象。指定 GeoLite2 数据库的位置
        File database;
        if (StringUtils.isBlank(path)) {
            database = new File(Objects.requireNonNull(ResourceReader.class.getClassLoader().getResource("GeoLite2-City.mmdb")).getFile());
        } else {
            database = new File(path);
        }
        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
        InetAddress ipAddress = InetAddress.getByName(ip);
        // 创建 CityResponse 对象来接收查询结果
        CityResponse response = dbReader.city(ipAddress);
        // 输出城市信息
        String cityName = response.getCity().getNames().get("zh-CN");
        // 关闭 DatabaseReader
        dbReader.close();
        return cityName;
    }

    /**
     * 根据 IP 获取城市 这里调用的是 第三方 IP数据云的接口 <a href="https://www.ipdatacloud.com/">...</a>
     *
     * @param ip ip
     * @return 城市
     */
    public static String getCity(String ip) {
        String str = HttpsUtil.doGet("https://api.ipdatacloud.com/v2/query?ip=" + ip + "&key=4f919146329f11ef8a9e00163e167ffb");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(str);
            if ("200".equals(jsonNode.get("code").asText())) {
                return jsonNode.get("data").get("location").get("city").asText();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}
